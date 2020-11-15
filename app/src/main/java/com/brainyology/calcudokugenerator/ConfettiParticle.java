package com.brainyology.calcudokugenerator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class ConfettiParticle {
    private float x;
    private float y;
    private float vx;
    private float vy;
    private float angle;
    private float vangle;
    private int color;
    private int radius;
    private final static float resist = 0.98f;
    //private final static float maxSpeed = 0.98f;


    public ConfettiParticle(float x, float y, float vx, float vy, float vangle, int radius, int color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.angle = 0;
        this.vangle = vangle;
        this.color = color;
        this.radius = radius;
    }

    public boolean update(int screenHeight) {
        vx *= resist;
        vy *= resist;

        x += vx;
        y += vy;
        angle += vangle;

        if (y < screenHeight)
            return true;
        return false;
    }

    public void addForce(float fx, float fy) {
        vx += fx;
        vy += fy;
    }

    public void draw(Canvas canvas, Paint paint) {
        Path path = new Path();
        path.moveTo(x + (float) Math.cos(angle) * radius, y + (float) Math.sin(angle) * radius);
        path.lineTo(x + (float) Math.cos(angle + 2 * Math.PI / 3) * radius, y + (float) Math.sin(angle + 2 * Math.PI / 3) * radius);
        path.lineTo(x + (float) Math.cos(angle + 4 * Math.PI / 3) * radius, y + (float) Math.sin(angle + 4 * Math.PI / 3) * radius);
        path.lineTo(x + (float) Math.cos(angle) * radius, y + (float) Math.sin(angle) * radius);

        paint.setColor(color);

        canvas.drawPath(path, paint);
    }
}
