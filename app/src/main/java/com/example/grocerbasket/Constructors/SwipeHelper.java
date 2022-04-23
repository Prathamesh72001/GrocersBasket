package com.example.grocerbasket.Constructors;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerbasket.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public abstract class SwipeHelper extends ItemTouchHelper.SimpleCallback {

    int buttonWidth;
    RecyclerView recyclerView;
    List<MyButton> buttonList;
    GestureDetector gestureDetector;
    int swpPos = -1;
    float swpT = 0.5f;
    Map<Integer, List<MyButton>> buttonBuffer;
    Queue<Integer> removerQueue;

    GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for (MyButton button : buttonList) {
                if (button.onClick(e.getX(), e.getY()))
                    break;
            }
            return true;
        }
    };

    public View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (swpPos < 0) return false;

            Point point = new Point((int) event.getRawX(), (int) event.getRawY());
            try {
                RecyclerView.ViewHolder swipeVH = recyclerView.findViewHolderForAdapterPosition(swpPos);

                View swpItem = swipeVH.itemView;

                Rect rect = new Rect();
                swpItem.getGlobalVisibleRect(rect);


                if (event.getAction() == MotionEvent.ACTION_DOWN ||
                        event.getAction() == MotionEvent.ACTION_UP ||
                        event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (rect.top < point.y && rect.bottom > point.y)
                        gestureDetector.onTouchEvent(event);
                    else {
                        removerQueue.add(swpPos);
                        swpPos = -1;
                        recoverSwipeItem();
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return false;
        }
    };


    public SwipeHelper(Context context, RecyclerView recyclerView, int buttonWidth) {
        super(0, ItemTouchHelper.LEFT);
        this.recyclerView = recyclerView;
        this.buttonList = new ArrayList<>();
        this.gestureDetector = new GestureDetector(context, gestureListener);
        this.recyclerView.setOnTouchListener(onTouchListener);
        this.buttonBuffer = new HashMap<>();
        this.buttonWidth = buttonWidth;
        removerQueue = new LinkedList<Integer>() {
            @Override
            public boolean add(Integer o) {
                if (contains(o))
                    return false;
                else
                    return super.add(o);
            }
        };
        attachSwipe();

    }

    private void attachSwipe() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private synchronized void recoverSwipeItem() {
        while (!removerQueue.isEmpty()) {
            int po = removerQueue.poll();
            if (po > -1)
                recyclerView.getAdapter().notifyItemChanged(po);
        }
    }


    public class MyButton {
        String text;
        int image, textSize, color, pos;
        RectF clickRegion;
        MyButtonClickListener listener;
        Context context;
        Resources resources;

        public MyButton(Context context, String text, int textSize, int image, int color, MyButtonClickListener listener) {
            this.text = text;
            this.image = image;
            this.textSize = textSize;
            this.color = color;
            this.listener = listener;
            this.context = context;
            resources = context.getResources();
        }

        public boolean onClick(float x, float y) {
            if (clickRegion != null && clickRegion.contains(x, y)) {
                listener.onClick(pos);
                return true;
            }
            return false;
        }

        public void onDraw(Canvas c, RectF rectF, int pos) {
            Paint p = new Paint();
            p.setColor(color);
            c.drawRect(rectF, p);

            p.setColor(Color.WHITE);
            p.setTextSize(textSize);

            //Typeface
            Typeface typeface = ResourcesCompat.getFont(context, R.font.bungee);
            p.setTypeface(typeface);

            Rect r = new Rect();
            float cH = rectF.height();
            float cW = rectF.width();
            p.setTextAlign(Paint.Align.LEFT);
            p.getTextBounds(text, 0, text.length(), r);
            float x = 0, y = 0;
            if (image == 0) {
                x = cW / 2f - r.width() / 2f - r.left;
                y = cH / 2f + r.height() / 2f - r.bottom;
                c.drawText(text, rectF.left + x, rectF.top + y, p);
            } else {
                Drawable d = ContextCompat.getDrawable(context, image);
                Bitmap bitmap = drawableToBitmap(d);
                c.drawBitmap(bitmap, rectF.centerX(), rectF.centerY(), p);
            }
            clickRegion = rectF;
            this.pos = pos;
        }
    }

    private Bitmap drawableToBitmap(Drawable d) {
        if (d instanceof BitmapDrawable)
            return ((BitmapDrawable) d).getBitmap();
        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(),
                d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        return bitmap;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getAdapterPosition();
        if (swpPos != pos)
            removerQueue.add(swpPos);
        swpPos = pos;
        if (buttonBuffer.containsKey(swpPos))
            buttonList = buttonBuffer.get(swpPos);
        else
            buttonList.clear();
        buttonBuffer.clear();
        swpT = 0.5f * buttonList.size() * buttonWidth;
        recoverSwipeItem();
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return swpT;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.1f * defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 5.0f * defaultValue;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int pos = viewHolder.getAdapterPosition();
        float tranX = dX;
        View itemView = viewHolder.itemView;
        if (pos < 0) {
            swpPos = pos;
            return;
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                List<MyButton> buffer = new ArrayList<>();
                if (!buttonBuffer.containsKey(pos)) {
                    instantiateMyButton(viewHolder, buffer);
                    buttonBuffer.put(pos, buffer);
                } else {
                    buffer = buttonBuffer.get(pos);
                }
                tranX = dX * buffer.size() * buttonWidth / itemView.getWidth();
                drawButton(c, itemView, buffer, pos, tranX);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, tranX, dY, actionState, isCurrentlyActive);
    }

    private void drawButton(Canvas c, View itemView, List<MyButton> buffer, int pos, float tranX) {
        float right = itemView.getRight();
        float bW = -1 * tranX / buffer.size();
        for (MyButton button : buffer) {
            float left = right - bW;
            button.onDraw(c, new RectF(left, itemView.getTop(), right, itemView.getBottom()), pos);
            right = left;
        }
    }

    public abstract void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer);
}
