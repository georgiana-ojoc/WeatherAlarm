package android.weather_alarm.utility;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.animation.DecelerateInterpolator;
import android.weather_alarm.R;
import android.widget.Button;

public final class AnimationUtility {
    public static void animateButtonTextColor(Button button) {
        ObjectAnimator animator = ObjectAnimator.ofInt(button, "textColor",
                button.getResources().getColor(R.color.shadow), Color.WHITE);
        animator.setDuration(1000);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setInterpolator(new DecelerateInterpolator(2));
        animator.start();
    }
}
