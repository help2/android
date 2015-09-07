package fi.stipakov.heartproject;

import android.animation.Animator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by stipa on 2.9.15.
 */
public class ViewSwitcher {
    interface IViewSwicherListener {
        void onViewSwitched();
    }

    IViewSwicherListener _listener;
    View _slider;
    Activity _activity;
    SharedPreferences _prefs;

    public static final String PREFS_NAME = "prefs";
    public static final String PREF_INITIAL_ANIMATION = "initial_animation_play";

    private float _dy;
    private float _initialY;

    private boolean _sliderOnTop = true;

    enum INITIAL_ANIMATION_STATE {NOT_PLAYED, PHASE_1, PHASE_2, DONE};

    INITIAL_ANIMATION_STATE _animState;

    private void onInitialAnimationDone() {
        _animState = INITIAL_ANIMATION_STATE.DONE;

        SharedPreferences.Editor editor = _prefs.edit();
        editor.putInt(PREF_INITIAL_ANIMATION, _animState.ordinal());
        editor.commit();
    }

    private void playAnimation(final View slider) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (_activity == null) {
                    onInitialAnimationDone();
                    return;
                }

                int activityHeight = _activity.getWindow().getDecorView().getHeight();
                float targetY = _sliderOnTop ? activityHeight - _slider.getHeight() * 2 :
                        0;
                _sliderOnTop = !_sliderOnTop;

                slider.animate().y(targetY).setDuration(1000).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (_activity == null) {
                            onInitialAnimationDone();
                            return;
                        }

                        _listener.onViewSwitched();

                        if (_animState == INITIAL_ANIMATION_STATE.PHASE_1) {
                            _animState = INITIAL_ANIMATION_STATE.PHASE_2;
                            playAnimation(slider);
                        } else if (_animState == INITIAL_ANIMATION_STATE.PHASE_2) {
                            onInitialAnimationDone();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) { }
                });
            }
        }, _animState == INITIAL_ANIMATION_STATE.PHASE_1 ? 2000 : 1500);
    }

    public void playInitialAnimation(final View slider) {
        if (_animState != INITIAL_ANIMATION_STATE.NOT_PLAYED || _activity == null) {
            return;
        }


        _animState = INITIAL_ANIMATION_STATE.PHASE_1;
        playAnimation(slider);
    }

    public ViewSwitcher(Activity activity, View slider, IViewSwicherListener listener) {
        _listener = listener;
        _activity = activity;
        _slider = slider;
        _prefs = _activity.getSharedPreferences(PREFS_NAME, 0);

        _animState = INITIAL_ANIMATION_STATE.values()[_prefs.getInt(PREF_INITIAL_ANIMATION, 0)];

        slider.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final View v = view;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        v.setY(event.getRawY() - _dy);
                        break;

                    case MotionEvent.ACTION_UP:
                        int delta = (int) Math.abs(_initialY - v.getY());
                        int activityHeight = _activity.getWindow().getDecorView().getHeight();
                        final boolean swi = (delta > activityHeight / 4);
                        float targetY = _initialY;
                        if (swi) {
                            targetY = _sliderOnTop ? activityHeight - _slider.getHeight() * 2 :
                                    0;
                            _sliderOnTop = !_sliderOnTop;
                        }

                        v.animate().y(targetY).setDuration(100).
                                setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        if (swi && _activity != null) {
                                            _listener.onViewSwitched();
                                        }
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {
                                    }
                                }).setInterpolator(new AccelerateInterpolator()).start();
                        break;

                    case MotionEvent.ACTION_DOWN:
                        if (_animState != INITIAL_ANIMATION_STATE.DONE) {
                            return false;
                        }

                        _initialY = v.getY();
                        _dy = event.getRawY() - v.getY();
                        break;
                }
                return true;
            }
        });
    }

    public void onActivityDestroyed() {
        _activity = null;
    }
}
