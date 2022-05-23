package flyingkite.library.androidx.widget;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class ViewDisplayer {

    public static final int VISIBLE   = 0; // = View.VISIBLE
    public static final int SHOWING   = 1; //
    public static final int HIDING    = 3; //
    public static final int INVISIBLE = 4; // = View.INVISIBLE
    protected int state = VISIBLE;
    protected static final Handler mainHandler = new Handler(Looper.getMainLooper());
    protected final TimeInterpolator slower = new DecelerateInterpolator();
    protected final TimeInterpolator faster = new AccelerateInterpolator();
    protected ShowListener onShow;
    protected HideListener onHide;

    protected final View actor;
    private long idleTime = 2_000;

    public static final int ACTION_SHOW = 0;
    public static final int ACTION_HIDE = 1;
    public static final int ACTION_SHOW_THEN_HIDE_WHEN_IDLE = 2;

    public ViewDisplayer(View v) {
        actor = v;
        onShow = new ShowListener(v);
        onHide = new HideListener(v);
    }

    public void setIdleTime(long ms) {
        idleTime = ms;
    }

    public long getIdleTime() {
        return idleTime;
    }

    protected final Runnable animateHide = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    public final class ShowListener extends AnimatorListenerAdapter {
        private final View viewToShow;
        public ShowListener(View v) {
            viewToShow = v;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            state = SHOWING;
            if (viewToShow != null) {
                viewToShow.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            state = VISIBLE;
        }
    }

    public final class HideListener extends AnimatorListenerAdapter {
        private final View viewToHide;
        public HideListener(View v) {
            viewToHide = v;
        }
        @Override
        public void onAnimationStart(Animator animation) {
            state = HIDING;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            state = INVISIBLE;
            if (viewToHide != null) {
                viewToHide.setVisibility(View.GONE);
            }
        }
    }

    public ViewDisplayer show() {
        mainHandler.removeCallbacks(animateHide);
        onShow(actor);
        return this;
    }

    public void hide() {
        mainHandler.removeCallbacks(animateHide);
        onHide(actor);
    }

    public void onShow(View v) {
        if (v != null) {
            v.clearAnimation();
            v.animate().alpha(1).setListener(onShow).start();
            //actor.setVisibility(View.VISIBLE);
            //actor.startAnimation(AnimationUtils.loadAnimation(actor.getContext(), R.anim.fade_in));
        }
    }

    public void onHide(View v) {
        if (v != null) {
            v.clearAnimation();
            v.animate().alpha(0).setListener(onHide).start();
            //actor.setVisibility(View.GONE);
            //actor.startAnimation(AnimationUtils.loadAnimation(actor.getContext(), R.anim.fade_out));
        }
    }

    public void requestHideWhenIdle() {
        mainHandler.removeCallbacks(animateHide);
        mainHandler.postDelayed(animateHide, idleTime);
    }

    public void performAction(int action) {
        if (action == ACTION_HIDE) {
            hide();
        } else if (action == ACTION_SHOW) {
            show();
        } else if (action == ACTION_SHOW_THEN_HIDE_WHEN_IDLE) {
            show().requestHideWhenIdle();
        }
    }
}

