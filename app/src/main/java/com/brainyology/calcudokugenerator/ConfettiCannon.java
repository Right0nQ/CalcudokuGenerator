package com.brainyology.calcudokugenerator;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

public class ConfettiCannon {
    private float x;
    private float y;
    private float angle;
    private float fan;
    private ArrayList<ConfettiParticle> particles;
    private float grav = 1f;
    private int radius;

    public ConfettiCannon(float x, float y, float angle, float fan, int radius, float grav) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.fan = fan;
        this.radius = radius;
        this.grav = grav;

        particles = new ArrayList<ConfettiParticle>();
    }

    public void fire(int amount, float minSpeed, float maxSpeed, int[] colors) {
        float speed;
        float adjAngle;
        for (int i = 0; i < amount; i++) {
            speed = (float) (minSpeed + (maxSpeed - minSpeed) * Math.random());
            adjAngle = angle + (float) (Math.random() - 0.5) * fan;
            particles.add(new ConfettiParticle(x, y, (float) (speed * Math.cos(adjAngle)), (float) (speed * Math.sin(adjAngle)), (float) (Math.random() - 0.5) / 5, radius, colors[(int) (Math.random() * colors.length)]));
        }
    }

    public void update(int screenHeight) {
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).addForce(0, grav);
            if (!particles.get(i).update(screenHeight)) {
                particles.remove(i);
                i--;
            }
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).draw(canvas, paint);
        }
    }
}
