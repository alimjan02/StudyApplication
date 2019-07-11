package com.sxt.chat.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.LinearInterpolator;

import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;

public class CircularRevealActivity extends HeaderActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular_reveal);
    }

    public static Animator createRevealAnimator(View view, int centerX, int centerY, float startRadius, float endRadius) {
        AnimatorSet set = new AnimatorSet();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
            set.play(revealAnimator);
        }
        return set;
    }

    public void startAnimator(View view) {
        View target = findViewById(R.id.img);
        Animator animator = createRevealAnimator(target, (int) target.getPivotX(), (int) target.getPivotY(), 0, target.getWidth());
        animator.setDuration(800);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }
}