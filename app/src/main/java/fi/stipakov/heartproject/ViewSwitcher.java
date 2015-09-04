package fi.stipakov.heartproject;

import android.animation.Animator;
import android.app.Activity;
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

    private float _dy;
    private float _initialY;

    private boolean _sliderOnTop = true;

    public ViewSwitcher(final Activity activity, View slider, IViewSwicherListener listener) {
        _listener = listener;

        _slider = slider;

        slider.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final View v = view;

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                        v.setY(event.getRawY() - _dy);
                        break;

                    case MotionEvent.ACTION_UP:
                        int delta = (int)Math.abs(_initialY - v.getY());
                        int activityHeight = activity.getWindow().getDecorView().getHeight();
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
                                if (swi) {
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
                        _initialY = v.getY();
                        _dy = event.getRawY()-v.getY();
                        break;
                }
                return true;
            }
        });
    }
}
