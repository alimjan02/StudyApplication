package com.sxt.chat.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.sxt.chat.R;

/**
 * @author Miguel Catalan Bañuls
 */
public class AnimationUtil {
    public static final String TAG = "AnimationUtil";
    public static int ANIMATION_DURATION_SHORT = 150;
    public static int ANIMATION_DURATION_MEDIUM = 400;
    public static int ANIMATION_DURATION_LONG = 800;

    public interface AnimationListener {
        /**
         * @return true to override parent. Else execute Parent method
         */
        boolean onAnimationStart(View view);

        boolean onAnimationEnd(View view);

        boolean onAnimationCancel(View view);
    }

    public static void fadeInView(View view, int duration, final AnimationListener listener) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0f);
        ViewPropertyAnimatorListener vpListener = null;

        if (listener != null) {
            vpListener = new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(View view) {
                    if (!listener.onAnimationStart(view)) {
                        view.setDrawingCacheEnabled(true);
                    }
                }

                @Override
                public void onAnimationEnd(View view) {
                    if (!listener.onAnimationEnd(view)) {
                        view.setDrawingCacheEnabled(false);
                    }
                }

                @Override
                public void onAnimationCancel(View view) {
                }
            };
        }
        ViewCompat.animate(view).alpha(1f).setDuration(duration).setListener(vpListener);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void open(final View view, final AnimationListener listener) {
        int cx = view.getWidth() - (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 24, view.getResources().getDisplayMetrics());
        int cy = view.getHeight() / 2;
        int finalRadius = Math.max(view.getWidth(), view.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        view.setVisibility(View.VISIBLE);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                listener.onAnimationStart(view);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                listener.onAnimationEnd(view);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                listener.onAnimationCancel(view);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void close(final View view1, View view2, final AnimationListener listener) {
        int cx = view2.getWidth() - (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 24, view1.getResources().getDisplayMetrics());
        int cy = view1.getHeight() / 2;
        int finalRadius = Math.max(view1.getWidth(), view1.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(view2, cx, cy, finalRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                listener.onAnimationStart(view1);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                listener.onAnimationEnd(view1);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                listener.onAnimationCancel(view1);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    public static void fadeOutView(View view, int duration, final AnimationListener listener) {
        ViewCompat.animate(view).alpha(0f).setDuration(duration).setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
                if (listener == null || !listener.onAnimationStart(view)) {
                    view.setDrawingCacheEnabled(true);
                }
            }

            @Override
            public void onAnimationEnd(View view) {
                if (listener == null || !listener.onAnimationEnd(view)) {
                    view.setVisibility(View.GONE);
                    view.setDrawingCacheEnabled(false);
                }
            }

            @Override
            public void onAnimationCancel(View view) {
            }
        });
    }

    public static void fadeInScaleView(final Context context, final View view, final int duration, final Animator.AnimatorListener listener) {

        view.setVisibility(View.VISIBLE);
        final ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1, 0.4f);
        final ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1, 0.4f);
        final ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0.4f, 1);

        AnimatorSet set = new AnimatorSet();
        set.play(scaleX).with(scaleY).with(alpha);
        set.setDuration(duration).setInterpolator(new LinearInterpolator());
        if (listener != null) {
            set.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                    if (listener != null) listener.onAnimationStart(animation);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (listener != null) listener.onAnimationEnd(animation);
                    float dimension = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics());
                    int statusBarHeight = 0;
                    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
                    if (resourceId > 0) {
                        statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
                    }
                    float actionBarHeight = context.getResources().getDimension(R.dimen.app_bar_height);
                    ObjectAnimator translationX = ObjectAnimator.ofFloat(view, "translationX", view.getLeft(), view.getWidth() * (1 - view.getScaleX()) / 2 - dimension);
                    float distanceY = view.getHeight() * (1 - view.getScaleY()) / 2;
                    ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", view.getTop(), view.getTop() - distanceY + actionBarHeight + statusBarHeight + dimension);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(translationX, translationY);
                    animatorSet.setDuration(duration).setInterpolator(new BounceInterpolator());
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    });
                    animatorSet.start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (listener != null) listener.onAnimationCancel(animation);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    if (listener != null) listener.onAnimationRepeat(animation);
                }
            });
        }
        AudioUtil.getInstance(context).playCaptureSound();
        set.start();
    }

    public static void fadeOutScaleView(View view, int duration, final Animator.AnimatorListener listener) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), 0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", view.getScaleY(), 0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY, alpha);
        set.setDuration(duration).setInterpolator(new LinearInterpolator());
        if (listener != null) {
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (listener != null) listener.onAnimationStart(animation);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (listener != null) listener.onAnimationEnd(animation);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (listener != null) listener.onAnimationCancel(animation);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    if (listener != null) listener.onAnimationRepeat(animation);
                }
            });
        }
        set.start();
    }

}