package com.edgetag.providers.blotoutcloud.geasture;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;

import io.reactivex.Emitter;
import io.reactivex.Observable;

public class Gesture {

    private final String TAG = "Gesture";

    private static final int DEFAULT_PRESS_TIMEOUT = 100;
    private static final int DEFAULT_TAP_TIMEOUT = 300;
    private static final int DEFAULT_MOVE_EPSILON = 4;
    private static final long DEFAULT_LONG_PRESS_TIMEOUT = 500;

    private int pressTimeout = DEFAULT_PRESS_TIMEOUT;
    private int tapTimeout = DEFAULT_TAP_TIMEOUT;
    private int moveEpsilon = DEFAULT_MOVE_EPSILON;
    private long longPressTimeout = DEFAULT_LONG_PRESS_TIMEOUT;

    private Runnable tapHandler;
    private Runnable pressHandler;
    private Runnable longPressHandler;

    private long prevTouchTime;
    private float prevTouchY;
    private float prevTouchX;

    private boolean dragging;
    private boolean moving;
    private int clicks = 0;

    private final Handler handler;
    private GestureListener listener;
    private Emitter<GestureEvent> emitter;

    public Gesture() {
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void addListener(GestureListener listener) {
        if (checkNotNull(listener)) {
            this.listener = listener;
        }else{
            Log.d(TAG,"GestureListener is not initialized");
        }
    }

    public Observable<GestureEvent> observe() {
        this.listener = createReactiveListener();
        return Observable.create(emitter -> Gesture.this.emitter = emitter);
    }

    public void dispatchTouchEvent(final MotionEvent motionEvent) {
        if(checkNotNull(motionEvent)) {
            float dx = motionEvent.getX() - prevTouchX;
            float dy = motionEvent.getY() - prevTouchY;
            long dt = System.currentTimeMillis() - prevTouchTime;
            handler.removeCallbacks(longPressHandler);
            longPressHandler = null;

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onActionDown(motionEvent, dt);
                    break;
                case MotionEvent.ACTION_UP:
                    onActionUp(motionEvent, dt);
                    break;
                case MotionEvent.ACTION_MOVE:
                    onActionMove(motionEvent, dx, dy);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    onActionCancel(motionEvent);
                    break;
                default:
                    break;
            }

            prevTouchX = motionEvent.getX();
            prevTouchY = motionEvent.getY();
        }
    }

    private void onActionDown(final MotionEvent motionEvent, long dt) {
        dragging = false;
        moving = false;

        if (tapHandler != null) {
            clicks++;
            handler.removeCallbacks(tapHandler);
            tapHandler = null;
        }

        handler.removeCallbacks(pressHandler);

        pressHandler = new Runnable() {
            @Override
            public void run() {
                onPress(motionEvent);
            }
        };

        handler.postDelayed(pressHandler, pressTimeout);

        longPressHandler = () -> onLongPress(motionEvent);

        handler.postDelayed(longPressHandler, longPressTimeout);
        prevTouchTime += dt;
    }

    private void onActionUp(final MotionEvent motionEvent, long dt) {
        if (dt > tapTimeout || moving || dragging) {
            onRelease(motionEvent);
            return;
        }

        tapHandler = () -> {
            handler.removeCallbacks(longPressHandler);
            longPressHandler = null;
            onTap(motionEvent, clicks);
        };

        handler.postDelayed(tapHandler, tapTimeout - dt);
        prevTouchTime += dt;
    }

    private void onActionMove(MotionEvent motionEvent, float dx, float dy) {
        if (Math.abs(dx) > moveEpsilon || Math.abs(dy) > moveEpsilon || dragging || moving) {
            if (pressHandler == null && !moving) {
                dragging = true;
            }

            handler.removeCallbacks(tapHandler);
            tapHandler = null;
            handler.removeCallbacks(pressHandler);
            pressHandler = null;
            handler.removeCallbacks(longPressHandler);
            longPressHandler = null;
            moving = true;

            if (dragging) {
                onDrag(motionEvent);
            } else {
                onMove(motionEvent);
            }
        }
    }

    private void onActionCancel(MotionEvent motionEvent) {
        handler.removeCallbacks(pressHandler);
        pressHandler = null;
        handler.removeCallbacks(tapHandler);
        tapHandler = null;
        handler.removeCallbacks(longPressHandler);
        longPressHandler = null;
        onRelease(motionEvent);
    }

    private void onRelease(MotionEvent motionEvent) {
        clicks = 0;
        listener.onRelease(motionEvent);
    }

    private void onDrag(MotionEvent motionEvent) {
        clicks = 0;
        listener.onDrag(motionEvent);
    }

    private void onMove(MotionEvent motionEvent) {
        clicks = 0;
        listener.onMove(motionEvent);
    }

    private void onTap(MotionEvent motionEvent, int clicks) {
        tapHandler = null;
        if (clicks == 0) {
            listener.onTap(motionEvent);
        } else {
            listener.onMultiTap(motionEvent, clicks + 1);
        }
        this.clicks = 0;
    }

    private void onLongPress(MotionEvent motionEvent) {
        clicks = 0;
        longPressHandler = null;
        listener.onLongPress(motionEvent);
    }

    private void onPress(MotionEvent motionEvent) {
        pressHandler = null;
        listener.onPress(motionEvent);
    }

    private GestureListener createReactiveListener() {
        return new GestureListener() {
            @Override
            public void onPress(MotionEvent motionEvent) {
                onNextSafely(GestureEvent.ON_PRESS);
            }

            @Override
            public void onTap(MotionEvent motionEvent) {
                onNextSafely(GestureEvent.ON_TAP);
            }

            @Override
            public void onDrag(MotionEvent motionEvent) {
                onNextSafely(GestureEvent.ON_DRAG);
            }

            @Override
            public void onMove(MotionEvent motionEvent) {
                onNextSafely(GestureEvent.ON_MOVE);
            }

            @Override
            public void onRelease(MotionEvent motionEvent) {
                onNextSafely(GestureEvent.ON_RELEASE);
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                onNextSafely(GestureEvent.ON_LONG_PRESS);
            }

            @Override
            public void onMultiTap(MotionEvent motionEvent, int clicks) {
                onNextSafely(GestureEvent.ON_MULTI_TAP.withClicks(clicks));
            }
        };
    }

    private void onNextSafely(final GestureEvent gestureEvent) {
        if (emitter != null) {
            emitter.onNext(gestureEvent);
        }
    }

    public int getPressTimeout() {
        return pressTimeout;
    }

    public void setPressTimeout(int pressTimeout) {
        this.pressTimeout = pressTimeout;
    }

    public int getTapTimeout() {
        return tapTimeout;
    }

    public void setTapTimeout(int tapTimeout) {
        this.tapTimeout = tapTimeout;
    }

    public int getMoveEpsilon() {
        return moveEpsilon;
    }

    public void setMoveEpsilon(int moveEpsilon) {
        this.moveEpsilon = moveEpsilon;
    }

    public long getLongPressTimeout() {
        return longPressTimeout;
    }

    public void setLongPressTimeout(long longPressTimeout) {
        this.longPressTimeout = longPressTimeout;
    }

    private boolean checkNotNull(Object object) {
        return object != null;
    }
}
