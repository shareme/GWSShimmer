/*
 * Copyright 2014 Romain Piel
 *
 * Modifications Copyright (c) 2015 Fred Grott(GrottWorkShop)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.shareme.gwsshimmer.library;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;


/**
 * Shimmer, original author Romain Piel
 *
 * Created by fgrott on 5/1/2015.
 * @author Fred Grott
 */
public class Shimmer {
    public static final int ANIMATION_DIRECTION_LTR = 0;
    public static final int ANIMATION_DIRECTION_RTL = 1;

    private static final int DEFAULT_REPEAT_COUNT = ValueAnimator.INFINITE;
    private static final long DEFAULT_DURATION = 1000;
    private static final long DEFAULT_START_DELAY = 0;
    private static final int DEFAULT_DIRECTION = ANIMATION_DIRECTION_LTR;

    private int repeatCount;
    private long duration;
    private long startDelay;
    private int direction;
    private Animator.AnimatorListener animatorListener;

    private ObjectAnimator animator;

    public Shimmer() {
        repeatCount = DEFAULT_REPEAT_COUNT;
        duration = DEFAULT_DURATION;
        startDelay = DEFAULT_START_DELAY;
        direction = DEFAULT_DIRECTION;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    /**
     * setRepeatCount
     *
     * @param repeatCount int repeat var
     * @return context
     */
    public Shimmer setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    /**
     * setDuration
     * @param duration duration var
     * @return context
     */
    public Shimmer setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public long getStartDelay() {
        return startDelay;
    }

    /**
     * setStartDelay
     * @param startDelay startDelay var
     * @return context
     */
    public Shimmer setStartDelay(long startDelay) {
        this.startDelay = startDelay;
        return this;
    }

    public int getDirection() {
        return direction;
    }

    public Shimmer setDirection(int direction) {

        if (direction != ANIMATION_DIRECTION_LTR && direction != ANIMATION_DIRECTION_RTL) {
            throw new IllegalArgumentException("The animation direction must be either ANIMATION_DIRECTION_LTR or ANIMATION_DIRECTION_RTL");
        }

        this.direction = direction;
        return this;
    }

    public Animator.AnimatorListener getAnimatorListener() {
        return animatorListener;
    }

    public Shimmer setAnimatorListener(Animator.AnimatorListener animatorListener) {
        this.animatorListener = animatorListener;
        return this;
    }

    public <V extends View & IShimmerViewBase> void start(final V shimmerView) {

        if (isAnimating()) {
            return;
        }

        final Runnable animate = new Runnable() {
            @Override
            public void run() {

                shimmerView.setShimmering(true);

                float fromX = 0;
                float toX = shimmerView.getWidth();
                if (direction == ANIMATION_DIRECTION_RTL) {
                    fromX = shimmerView.getWidth();
                    toX = 0;
                }

                animator = ObjectAnimator.ofFloat(shimmerView, "gradientX", fromX, toX);
                animator.setRepeatCount(repeatCount);
                animator.setDuration(duration);
                animator.setStartDelay(startDelay);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        shimmerView.setShimmering(false);

                        if (VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN) {
                            shimmerView.postInvalidate();
                        } else {
                            shimmerView.postInvalidateOnAnimation();
                        }

                        animator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                if (animatorListener != null) {
                    animator.addListener(animatorListener);
                }

                animator.start();
            }
        };

        if (!shimmerView.isSetUp()) {
            shimmerView.setAnimationSetupCallback(new ShimmerViewHelper.AnimationSetupCallback() {
                @Override
                public void onSetupAnimation(final View target) {
                    animate.run();
                }
            });
        } else {
            animate.run();
        }
    }

    public void cancel() {
        if (animator != null) {
            animator.cancel();
        }
    }

    public boolean isAnimating() {
        return animator != null && animator.isRunning();
    }

}
