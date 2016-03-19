package org.telegram.android;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;
import org.telegram.ui.LaunchActivity;

/**
 * Created by rahil on 3/18/2016.
 */
public class ChatHeadService extends Service {
    WindowManager.LayoutParams params;
    boolean isMoving = false;
    DisplayMetrics metrics;
    GestureDetectorCompat gestureDetectorCompat;
    boolean first = true;
    private WindowManager windowManager;
    private ImageView chatHead;
    private boolean didFling;
    private float ratioY = 0;
    private float oldWidth = 0;
    private float oldX = 0;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private boolean confChange = false;
    private int[] clickLocation = new int[2];
String chat_id;
    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        chatHead = new ImageView(this);
        chatHead.setImageResource(R.drawable.chathead);
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        //this code is for dragging the chat head
        /*chatHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("check", "hua kya");
                stopSelf();
            }
        });*/
        gestureDetectorCompat = new GestureDetectorCompat(ApplicationLoader.applicationContext, new GestureDetector.OnGestureListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onDown(MotionEvent e) {
                Log.d("check", "onDown");
                initialX = params.x;
                initialY = params.y;
                initialTouchX = e.getRawX();
                initialTouchY = e.getRawY();
                didFling = false;
                return false;
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onShowPress(MotionEvent e) {
                Log.d("check2", "onShowPress");
                chatHead.setAlpha(0.8f);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (first == true) {
                    Intent i = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    first = false;
                    return true;
                } else {
                    first = true;
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                    return true;
                }
            }
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                params.x = (initialX + (int) ((e2.getRawX() - initialTouchX)));
                params.y = (initialY + (int) ((e2.getRawY() - initialTouchY)));
                windowManager.updateViewLayout(chatHead, params);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                stopSelf();
                Log.d("check", "long pressed");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d("move hua kya", "onFling");
                didFling = true;
                int newX = params.x;
                if (newX > (metrics.widthPixels / 2))
                    params.x = metrics.widthPixels;
                else
                    params.x = 0;
                windowManager.updateViewLayout(chatHead, params);
                return false;
            }
        });
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        chatHead.setOnTouchListener(new View.OnTouchListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetectorCompat.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    chatHead.setAlpha(1.0f);
                    if (!didFling) {
                        Log.d("chut", "ACTION_UP");
                        int newX = params.x;
                        if (newX > (metrics.widthPixels / 2))
                            params.x = metrics.widthPixels;
                        else
                            params.x = 0;
                        windowManager.updateViewLayout(chatHead, params);
                    }
                }
                return true;
            }
        });

      /*  chatHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private float Xdiff;
            private float Ydiff;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        isMoving = false;
                        return true;
                    case MotionEvent.ACTION_UP:
                        Xdiff=Math.abs(event.getRawX() - initialTouchX);
                        Ydiff=Math.abs(event.getRawX()-initialTouchY);
                        if (!isMoving || (Xdiff<10 && Ydiff<10)){
                            chatHead.performClick();
                            chatHead.performLongClick();
                        }
                            return true;
                    case MotionEvent.ACTION_MOVE:
                        isMoving=true;
                        params.x = initialX
                                + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY
                                + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(chatHead, params);
                        return true;
                }
                return false;
            }
        });*/

        windowManager.addView(chatHead, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null)
            windowManager.removeView(chatHead);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
  /*  @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            int[] location = new int[2];
            chatHead.getLocationOnScreen(location);
            oldWidth = metrics.widthPixels;
            confChange = true;
            if (chatHead.getVisibility() == View.VISIBLE) {
                oldX = clickLocation[0];
                ratioY = (float) (clickLocation[1]) / (float) metrics.heightPixels;
            } else {
                oldX = location[0];
                ratioY = (float) (location[1]) / (float) metrics.heightPixels;
            }
            floaty.floatyOrientationListener.beforeOrientationChange(floaty);
            floaty.stopService();
            floaty.startService();
            floaty.floatyOrientationListener.afterOrientationChange(floaty);
        }
    }*/

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            return true;
        }
    }
}
