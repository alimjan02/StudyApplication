package com.sxt.chat.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.sxt.chat.R;

import static android.view.View.GONE;

/**
 * @author Miguel Catalan Bañuls
 */
public class AnimationUtil {
    public static final String TAG = "AnimationUtil";
    public static int ANIMATION_DURATION_SHORT = 200;
    public static int ANIMATION_DURATION_MEDIUM = 400;
    public static int ANIMATION_DURATION_LONG = 800;

    public static final String SCALE_X = "scaleX";
    public static final String SCALE_Y = "scaleY";
    public static final String TRANSLATION_X = "translationX";
    public static final String TRANSLATION_Y = "translationY";
    public static final String ROTATION_X = "rotationX";
    public static final String ROTATION_Y = "rotationY";
    public static final String ALPHA = "alpha";

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

    public static void fadeInView(View view, float fromAlpha, float toAlpha, int duration, final AnimationListener listener) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(fromAlpha);
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
        ViewCompat.animate(view).alpha(toAlpha).setDuration(duration).setListener(vpListener);
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
                    view.setVisibility(GONE);
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
        final ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, SCALE_X, 1, 0.3f);
        final ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, SCALE_Y, 1, 0.3f);
        final ObjectAnimator alpha = ObjectAnimator.ofFloat(view, ALPHA, 0.8f, 1);

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
                    final float dimension = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics());
                    int statusBarHeight = 0;
                    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
                    if (resourceId > 0) {
                        statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
                    }
                    final float actionBarHeight = context.getResources().getDimension(R.dimen.app_bar_height);
                    float endX = view.getWidth() * (1 - view.getScaleX()) / 2 - dimension;
                    final float distanceY = view.getHeight() * (1 - view.getScaleY()) / 2;
                    final float endY = view.getTop() - distanceY + actionBarHeight + statusBarHeight + dimension;
                    ObjectAnimator translationX = ObjectAnimator.ofFloat(view, TRANSLATION_X, view.getLeft(), endX);
                    ObjectAnimator translationY = ObjectAnimator.ofFloat(view, TRANSLATION_Y, view.getTop(), endY);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(translationX, translationY);
                    animatorSet.setDuration(duration).setInterpolator(new DecelerateInterpolator());
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            float start = endY;
                            ObjectAnimator translateAnimation = ObjectAnimator.ofFloat(view, TRANSLATION_Y, start, start + dimension / 2, start - dimension / 2);
                            translateAnimation.setRepeatCount(ValueAnimator.INFINITE);
                            translateAnimation.setRepeatMode(ValueAnimator.REVERSE);//反向重复执行,可以避免抖动
                            translateAnimation.setDuration(1500);
                            translateAnimation.start();

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
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, SCALE_X, view.getScaleX(), 0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, SCALE_Y, view.getScaleY(), 0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, ALPHA, 1, 0);
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

    public static void rotation(final View view, final boolean isShown) {
        if (isShown && view.getVisibility() == View.VISIBLE) {
            return;
        }
        view.setVisibility(View.VISIBLE);
        RotateAnimation rotateAnimation = new RotateAnimation(isShown ? 0 : 90, isShown ? 90 : 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(isShown ? 0.5f : 1, isShown ? 1 : 0);
        AnimationSet animatorSet = new AnimationSet(true);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.addAnimation(rotateAnimation);
        animatorSet.addAnimation(alphaAnimation);
        animatorSet.setDuration(ANIMATION_DURATION_SHORT);
        animatorSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animatorSet);
    }

    /**
     * Y轴执行位移动画
     */
    public static void translationYAnimator(View target, int startTranslationY, int endTranslationY, long duration) {
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(target, "translationY", startTranslationY, endTranslationY).setDuration(duration);
        translationAnimator.setInterpolator(new LinearInterpolator());
        translationAnimator.start();
    }

    /**
     * Y轴执行位移,透明渐变动画
     */
    private void translationYAlphaAnimator(View targetView, int startTranslationY, int endTranslationY, int startAlpha, int endAlpha, long duration) {
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(targetView, "translationY", startTranslationY, endTranslationY).setDuration(200);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(targetView, "alpha", startAlpha, endAlpha);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.playTogether(translationAnimator, alphaAnimator);
        animatorSet.start();
    }
}