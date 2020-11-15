package com.brainyology.calcudokugenerator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by testtube24 on 1/1/18.
 */

public class Button {
    private static float fingerX;
    private static float fingerY;
    private static boolean fingerDown = false;
    private static boolean firstRelease = false;
    private static boolean fingerReleased = false;

    public static void touchListen(MotionEvent e) {

        if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE) {
            fingerDown = true;
            //firstPress = true;
            fingerX = e.getX();
            fingerY = e.getY();
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            fingerReleased = true;
            fingerDown = false;
        }

    }

    public static void update() {
        if (fingerReleased)
            fingerReleased = false;
    }

    public static int circleButton(int x, int y, int width, int color, int color2, String text, int textSize, int returned, Canvas canvas, Paint paint) {
        boolean tapped = false;

        if (Math.sqrt((x + width / 2 - fingerX) * (x + width / 2 - fingerX) + (y + width / 2 - fingerY) * (y + width / 2 - fingerY)) < width / 2 && (fingerDown || fingerReleased)) {
            tapped = true;
        }
        if (tapped) {
            paint.setColor(color2);
        } else {
            paint.setColor(color);
        }
        //canvas.drawRoundRect(x, y, x + width, y + width, (width < height? width: height) / 4f, (width < height? width: height) / 4f, paint);

        canvas.drawArc(x, y, x + width, y + width, 135, 180, true, paint);

        if (tapped) {
            paint.setColor(color);
        } else {
            paint.setColor(color2);
        }

        canvas.drawArc(x, y, x + width, y + width, 315, 180, true, paint);
        paint.setColor(Color.BLACK);

        ArrayList<String> txts = new ArrayList<String>();
        while(text.length() > 0) {
            txts.add(text.substring(0, text.indexOf("\n") != -1? text.indexOf("\n"): text.length()));
            text = text.indexOf("\n") != -1? text.substring(text.indexOf("\n") + 1): "";
        }

        paint.setTextSize(textSize);


        float txtH = -paint.ascent();

        //System.out.println(txtH);

        for (int i = 0; i < txts.size(); i++) {
            canvas.drawText(txts.get(i), x + width / 2 - paint.measureText(txts.get(i)) / 2, y + width / 2 - txtH * (txts.size() / 2f - i - 1), paint);
        }

        if (tapped && fingerReleased) {
            return returned;
        }

        return -1;
    }

    public static int rectButton(int x, int y, int width, int height, int color, int color2, String text, int textSize, int returned, Canvas canvas, Paint paint) {
        boolean tapped = false;

        if (fingerX > x && fingerX < x + width && fingerY > y && fingerY < y + height && (fingerDown || fingerReleased)) {
            tapped = true;
        }

        //Paint paint = new Paint();
        paint.setTextSize(textSize);
        if (tapped) {
            paint.setColor(color2);
        } else {
            paint.setColor(color);
        }
        canvas.drawRect(x, y, x + width, y + height, paint);

        if (tapped) {
            paint.setColor(color);
        } else {
            paint.setColor(color2);
        }

        Path path = new Path();
        path.moveTo(x, y + height);
        path.lineTo(x + width, y);
        path.lineTo(x + width, y + height);
        path.lineTo(x, y + height);
        //canvas.drawVertices(Canvas.VertexMode.TRIANGLES, 3, verts, 0,  null, 0, colors, 0, null, 0, 0, paint);
        canvas.drawPath(path, paint);



        paint.setColor(Color.BLACK);
        canvas.drawText(text, x + width / 2 - paint.measureText(text) / 2, y + height / 2 - paint.ascent() / 2, paint);

        if (tapped && fingerReleased) {
            return returned;
        }
        return -1;
    }

    public static int picButton(int x, int y, int width, int height, Bitmap pic, int returned, Canvas canvas, Paint paint) {
        boolean tapped = false;

        if (fingerX > x && fingerX < x + width && fingerY > y && fingerY < y + height && (fingerDown || fingerReleased)) {
            tapped = true;
        }

        canvas.drawBitmap(pic, x + width / 2 - pic.getWidth() / 2, y + height / 2 - pic.getHeight() / 2, paint);

        if (tapped && fingerReleased) {
            return returned;
        }

        return -1;
    }

    public static int picButton(int x, int y, int width, int height, Bitmap pic, int color, int color2, int returned, Canvas canvas, Paint paint) {
        boolean tapped = false;

        if (fingerX > x && fingerX < x + width && fingerY > y && fingerY < y + height && (fingerDown || fingerReleased)) {
            tapped = true;
        }

        if (tapped) {
            paint.setColor(color2);
        } else {
            paint.setColor(color);
        }
        canvas.drawRect(x, y, x + width, y + height, paint);

        if (tapped) {
            paint.setColor(color);
        } else {
            paint.setColor(color2);
        }

        Path path = new Path();
        path.moveTo(x, y + height);
        path.lineTo(x + width, y);
        path.lineTo(x + width, y + height);
        path.lineTo(x, y + height);
        //canvas.drawVertices(Canvas.VertexMode.TRIANGLES, 3, verts, 0,  null, 0, colors, 0, null, 0, 0, paint);
        canvas.drawPath(path, paint);

        canvas.drawBitmap(pic, x + width / 2 - pic.getWidth() / 2, y + height / 2 - pic.getHeight() / 2, paint);

        if (tapped && fingerReleased) {
            return returned;
        }

        return -1;
    }

}
