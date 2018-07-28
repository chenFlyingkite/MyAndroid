package flyingkite.tool;

import flyingkite.log.L;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class TaskMonitorUtil {

    /**
     * When all the tasks of preRun finished, execute the ended one.
     * Just like WinJS.Promise.join does. <br/>
     * WinJS.Promise.join creates a single promise that is fulfilled when all the others are fulfilled or fail with errors (a logical AND)
     * Using the {@link TaskMonitor} as implementation
     * @param preRun tasks to be fullfill
     * @param ended tasks to run after all the preRun end
     */
    public static void join(List<Runnable> preRun, Runnable ended) {
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
                L.log("Task OK #%s %s", index, tag);
            }

            @Override
            public void onAllTaskDone() {
                L.log("Task All OK");
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
    }
}
