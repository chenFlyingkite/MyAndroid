package flyingkite.library.java.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

import flyingkite.library.java.log.L;
import flyingkite.library.java.log.Loggable;
import flyingkite.library.java.util.ThreadUtil;

public class TaskMonitor implements Loggable {
    /**
     * Our clients interested in those tasks source. </br>
     * Each client is eager to be notified {@link OnTaskState#onTaskDone(int, String)} or {@link OnTaskState#onAllTaskDone()}
     * After all task done, we will remove client.
     */
    private final List<OnTaskState> clients = Collections.synchronizedList(new ArrayList<>());

    private TaskOwner taskOwner;

    public TaskMonitor(TaskOwner source) {
        if (source == null) {
            throw new NullPointerException("source is null");
        }
        taskOwner = source;
    }

    public interface TaskOwner {
        /**
         * How many tasks to be done, this value should be constant since we will notify {@link #isTaskDone(int)}
         */
        int taskCount();

        /**
         * Asking source whether the task[index] is done.
         */
        boolean isTaskDone(int index);

        String getTaskTag(int index);
    }

    public interface OnTaskState {
        /**
         * Notify the task[index] is done
         */
        default void onTaskDone(int index, String tag) {}

        /**
         * Notify all the tasks are completed
         */
        default void onAllTaskDone() {}
    }

    public void registerClient(OnTaskState listener) {
        if (listener == null) {
            throw new NullPointerException("listener is null");
        }
        log("Register");
        synchronized (clients) {
            clients.add(listener);
            notifyClientsState();
        }
    }

    public synchronized void notifyClientsState() {
        synchronized (clients) {
            List<Integer> toRemove = new ArrayList<>();
            int n = clients.size();
            log("+ notify state to %s clients = %s", clients.size(), clients);
            for (int i = 0; i < n; i++) {
                int done = 0;
                OnTaskState ci = clients.get(i);
                // For each task[j], notify client[i] if task[j] is done
                for (int j = 0; j < taskOwner.taskCount(); j++) {
                    if (taskOwner.isTaskDone(j)) {
                        ci.onTaskDone(j, taskOwner.getTaskTag(j));
                        done++;
                    }
                }
                // If all tasks are done, we plans to remove client[i]
                if (done == taskOwner.taskCount()) {
                    ci.onAllTaskDone();
                    toRemove.add(i);
                }
            }

            // Remove the clients, from largest index to smallest
            log("Remove clients = %s", toRemove);
            n = toRemove.size();
            for (int i = n - 1; i >= 0; i--) {
                int clientIndex = toRemove.get(i);
                clients.remove(clientIndex);
            }
            log("- end with %s clients = %s", clients.size(), clients);
        }
    }

    @Override
    public void log(String message) {
        //Log.i(LTag(), message);
    }

    public static TaskMonitor join(List<Runnable> preRun, Runnable ended) {
        return join(preRun, ended, L.getImpl());
    }

    /**
     * When all the tasks of preRun finished, execute the ended one.
     * Just like WinJS.Promise.join does. <br/>
     * WinJS.Promise.join creates a single promise that is fulfilled when all the others are fulfilled or fail with errors (a logical AND)
     * Using the {@link TaskMonitor} as implementation
     * @param preRun tasks to be fullfill
     * @param ended tasks to run after all the preRun end
     */
    public static TaskMonitor join(List<Runnable> preRun, Runnable ended, Loggable g) {
        // Flags records all task if is done
        final boolean[] done = new boolean[preRun.size()];
        // Create TaskOwner
        TaskMonitor.TaskOwner owner = new TaskMonitor.TaskOwner() {
            @Override
            public int taskCount() {
                return preRun.size();
            }

            @Override
            public boolean isTaskDone(int index) {
                synchronized (done) {
                    return done[index];
                }
            }

            @Override
            public String getTaskTag(int index) {
                return preRun.get(index).getClass().getSimpleName();
            }
        };
        TaskMonitor.OnTaskState state =  new TaskMonitor.OnTaskState() {
            @Override
            public void onTaskDone(int index, String tag) {
                if (g != null) {
                    g.log("Task OK #%s %s", index, tag);
                }
            }

            @Override
            public void onAllTaskDone() {
                if (g != null) {
                    g.log("Task All OK");
                }
                ended.run();
            }
        };
        TaskMonitor monitor = new TaskMonitor(owner);
        // Run all those preRun in pool
        ExecutorService pool = ThreadUtil.cachedThreadPool;
        for (int i = 0; i < preRun.size(); i++) {
            final int pos = i;
            pool.submit(() -> {
                preRun.get(pos).run();
                synchronized (done) {
                    done[pos] = true;
                }
                monitor.notifyClientsState();
            });
        }
        monitor.registerClient(state);
        return monitor;
    }
}
