package com.brainyology.calcudokugenerator;

/**
 * Created by testtube24 on 12/31/17.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread thread;

    private String page = "MENU";
    private String nextPage = "";
    private int nextPageX;
    private boolean buttonsActive = true;

    private Kenken mainPuzzle;
    private KenkenBlocks head = new KenkenBlocks(new int[0], new int[0], 0);
    private int size = 7;
    private int width;
    private int height;

    private static final int[] colors = {Color.BLACK, Color.RED, Color.BLUE, Color.GREEN};
    private int colorNumber;

    private boolean recorded = false;
    private long seed = 0;

    private Scrolling highscores = null;

    private Bitmap home;
    private Bitmap undo;

    private ConfettiCannon leftCannon;
    private ConfettiCannon rightCannon;

    private MediaPlayer success;

    private Paint paint;

    private final static int[] sizeButtonC = {
            Color.rgb(91, 137, 230),
            Color.rgb(47, 96, 207),
            Color.rgb(130, 186, 106),
            Color.rgb(89, 155, 62),
            Color.rgb(250, 210, 83),
            Color.rgb(236, 183, 39),
            Color.rgb(240, 163, 88),
            Color.rgb(222, 126, 43),
            Color.rgb(214, 79, 84),
            Color.rgb(191, 33, 34)};

    Typeface myFont;

    private Button newGame;//menu buttons

    public GamePanel(Context context) {
        super(context);

        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        setFocusable(true);

        myFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/impact.ttf");
        highscores = new Scrolling(50, 200, 500, 500, context);//values later changed
        highscores.addPage("5x5");
        highscores.addPage("6x6");
        highscores.addPage("7x7");
        highscores.addPage("8x8");
        highscores.addPage("9x9");

        highscores.readFile();

        colorNumber = 0;

        paint = new Paint();
        paint.setTypeface(myFont);

        home = BitmapFactory.decodeResource(context.getResources(), R.drawable.home);
        undo = BitmapFactory.decodeResource(context.getResources(), R.drawable.undo);
        success = MediaPlayer.create(context, R.raw.success);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new MainThread(getHolder(), this);

        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Button.touchListen(event);
        if (page.equals("GAME")) {
            mainPuzzle.touchListen(event);
        } else if (page.equals("SCORES")) {
            long seed = highscores.touchListen(event, myFont);
            if (seed > 0) {
                size = highscores.curPage + 5;
                mainPuzzle = new Kenken(size);
                head = mainPuzzle.create(seed);
                recorded = true;
                page = "GAME";
            }

        }

        /*if (!page.equals("SCORES") && highscores.framesDown != 0)
            highscores.framesDown = 0;*/

        return true;
        //return super.onTouchEvent(event);
    }

    public void update() {
    }

    //@Override
    public void draw(Canvas canvas, long frameCount) {
        super.draw(canvas);

        canvas.drawColor(Color.WHITE);

        width = canvas.getWidth();
        height = canvas.getHeight();

        //highscores = new Scrolling(width / 12, width / 5, width * 5 / 6, width * 5 / 6);

        int boxW = (width - 100) / (size + 2);

        paint.setTextSize(width / 8);
        if (home.getHeight() != width / 8) {
            home = Bitmap.createScaledBitmap(home, (int) (width / 8), (int) (width / 8), false);
            undo = Bitmap.createScaledBitmap(undo, boxW  * 2 / 3, boxW * 2 / 3, false);
        }

        if (leftCannon == null || rightCannon == null) {
            leftCannon = new ConfettiCannon(0, height * 3 /4, (float) -Math.PI * 3 / 7, 0.7f, width / 20, height / 2000f);
            rightCannon = new ConfettiCannon(width, height * 3 / 4, (float) -Math.PI * 4 / 7, 0.7f, width / 20, height / 2000f);
        }

        int shadow = width / 80;

        float[] menuColor = {(frameCount / 4f) % 360, 0.5f, 1};

        if (page.equals("MENU")) {

            paint.setColor(Color.HSVToColor(menuColor));
            canvas.drawRect(0, 0, width, height, paint);
            paint.setColor(Color.argb(50, 0, 0, 0));
            paint.setTextSize(width / 3);
            float offset = paint.measureText("C");
            canvas.drawText("C", width / 10 + shadow, width * 11 / 24 + shadow, paint);
            paint.setTextSize(width / 8);
            canvas.drawText("alcudoku", width / 10 + offset + shadow, width / 3 + shadow, paint);
            canvas.drawText("Generator", width / 5 + offset + shadow, width * 11 / 24 + shadow, paint);

            paint.setColor(Color.BLACK);
            paint.setTextSize(width / 3);
            canvas.drawText("C", width / 10, width * 11 / 24, paint);
            paint.setTextSize(width / 8);
            canvas.drawText("alcudoku", width / 10 + offset, width / 3, paint);
            canvas.drawText("Generator", width / 5 + offset, width * 11 / 24, paint);

            if (Button.circleButton(width / 12, width * 3 / 5, width / 3, Color.rgb(200, 200, 200), Color.rgb(150, 150, 150), "NEW\nPUZZLE", width / 12, 1, canvas, paint) == 1 && buttonsActive) {
                nextPage = "SIZE";
                nextPageX = -width;
                buttonsActive = false;
            }

            if (Button.circleButton(width * 7 / 12, width * 3 / 5, width / 3, Color.rgb(200, 200, 200), Color.rgb(150, 150, 150), "HIGH\nSCORES", width / 12, 1, canvas, paint) == 1 && buttonsActive) {
                nextPage = "SCORES";
                nextPageX = -width;
                highscores.resetPages();
                buttonsActive = false;
            }

            if (Button.circleButton(width / 3, width, width / 3, Color.rgb(200, 200, 200), Color.rgb(150, 150, 150), "HOW TO", width / 12, 1, canvas, paint) == 1 && buttonsActive) {
                nextPage = "HELP";
                nextPageX = -width;
                buttonsActive = false;
            }

        }

        if (page.equals("SIZE") || nextPage.equals("SIZE")) {
            int shiftX = 0;
            if (nextPage.equals("SIZE")) {
                shiftX = nextPageX;
            }
            paint.setColor(Color.WHITE);
            canvas.drawRect(shiftX, 0, shiftX + width, height, paint);
            //int bWidth = (int) (width) / 5;
            paint.setTextSize(width / 8);
            paint.setColor(Color.argb(50, 0, 0, 0));
            canvas.drawText("Calcudoku", width / 2 - paint.measureText("Calcudoku") / 2 + shadow + shiftX, width / 8 + shadow, paint);
            paint.setColor(Color.BLACK);
            canvas.drawText("Calcudoku", width / 2 - paint.measureText("Calcudoku") / 2 + shiftX, width / 8, paint);

            if (Button.picButton(shiftX, 0, (int) ((width - paint.measureText("Calcudoku")) / 2.1), width / 8, home, 1, canvas, paint) == 1 && buttonsActive) {
                nextPage = "MENU";
                nextPageX = -width;
                buttonsActive = false;
            }

            paint.setTextSize(width / 10);
            paint.setColor(Color.BLACK);
            canvas.drawText("Choose Size", width / 2 - paint.measureText("Choose Size") / 2 + shiftX, height * 4 / 5, paint);

            for (int i = 0; i <= 4; i++) {
                if (Button.circleButton(width / 10 + (width * 3 / 20) * i + shiftX, height * 2 / 5 + (i % 2 == 1? width / 5: 0), width / 5, sizeButtonC[i * 2], sizeButtonC[i*2 + 1], (i + 5) + "", width / 8, 1, canvas, paint) == 1 && buttonsActive) {
                    size = i + 5;
                    mainPuzzle = new Kenken(size);
                    seed = (long) (Math.random() * Integer.MAX_VALUE);
                    head = mainPuzzle.create(seed);
                    colorNumber = 0;
                    recorded = false;
                    nextPage = "GAME";
                    nextPageX = -width;
                    buttonsActive = false;//1 (914) 874-4497 - Hannah Zipkin
                }
            }

        }
        if (page.equals("GAME") || nextPage.equals("GAME")) {
            int shiftX = 0;
            if (nextPage.equals("GAME")) {
                shiftX = nextPageX;
                paint.setColor(Color.WHITE);
                canvas.drawRect(shiftX, 0, shiftX + width, height, paint);
            }
            paint.setTextSize(width / 8);
            paint.setColor(Color.rgb(210, 210, 210));
            canvas.drawText("Calcudoku", width / 2 - paint.measureText("Calcudoku") / 2 + shadow + shiftX, width / 8 + shadow, paint);
            paint.setColor(Color.BLACK);
            canvas.drawText("Calcudoku", width / 2 - paint.measureText("Calcudoku") / 2 + shiftX, width / 8, paint);

            if (Button.picButton(shiftX, 0, (int) ((width - paint.measureText("Calcudoku")) / 2.1), width / 8, home, 1, canvas, paint) == 1 && buttonsActive) {
                nextPage = "MENU";
                nextPageX = -width;
                buttonsActive = false;
            }

            KenkenBlocks body = head;
            if (body != null) {
                mainPuzzle.draw(canvas, paint, body, canvas.getWidth(), shiftX);
            }

            int buttonW = (width - 100) / 10;

            for (int i = 0; i <= size + 1; i++) {//0 = undo, 1 = delete
                if (i == 0) {
                    if (Button.picButton(50 + i * boxW + shiftX, height - boxW - width / 20, boxW, boxW, undo, Color.rgb(200, 200, 200), Color.rgb(150, 150, 150), i, canvas, paint) >= 0) {
                        mainPuzzle.enterNum(i - 1, colors[colorNumber]);
                    }
                } else {
                    if (Button.rectButton(50 + i * boxW + shiftX, height - boxW - width / 20, boxW, boxW, Color.rgb(200, 200, 200), Color.rgb(150, 150, 150), i == 1 ? "X" : (i - 1) + "", boxW / 2, i, canvas, paint) >= 0) {
                        mainPuzzle.enterNum(i - 1, colors[colorNumber]);
                    }
                }
            }

            paint.setColor(colors[colorNumber]);
            canvas.drawRect((width / 2) - buttonW * 36 / 10 + colorNumber * buttonW * 2 + shiftX,height - boxW - width / 10 - buttonW * 11 / 10,  (width / 2) - buttonW * 24 / 10 + colorNumber * buttonW * 2 + shiftX, height - boxW - width / 10 + buttonW / 10, paint);
            paint.setColor(Color.WHITE);
            canvas.drawRect((width / 2) - buttonW * 7 / 2 + colorNumber * buttonW * 2 + shiftX,height - boxW - width / 10 - buttonW,  (width / 2) - buttonW * 5 / 2 + colorNumber * buttonW * 2 + shiftX, height - boxW - width / 10, paint);

            for (int i = 0; i < colors.length; i++) {
                int b = (i == colorNumber? buttonW / 10: 0);
                if (Button.rectButton((width / 2) - buttonW * 7 / 2 + i * buttonW * 2 + b + shiftX, height - boxW - width / 10 - buttonW + b, buttonW - 2 * b, buttonW - 2 * b, colors[i], colors[i], "", 0, i, canvas, paint) >= 0) {
                    colorNumber = i;
                }
            }

            if (Button.rectButton(width / 12 + shiftX, width * 6 / 5 - 100, width / 3, width / 10, Color.rgb(130, 186, 106), Color.rgb(89, 155, 62), "NEW PUZZLE", width / 20, 1, canvas, paint) == 1) {
                mainPuzzle = new Kenken(size);
                seed = (long) (Math.random() * Integer.MAX_VALUE);
                head = mainPuzzle.create(seed);
                recorded = false;
            }
            if (Button.rectButton(width * 7 / 12 + shiftX, width * 6 / 5 - 100, width / 3, width / 10, Color.rgb(214, 79, 84), Color.rgb(191, 33, 34), "CLEAR ALL", width / 20, 1, canvas, paint) == 1) {
                mainPuzzle.clear();
            }

            if (mainPuzzle.complete && !recorded) {
                highscores.add(size - 5, new ScrollingObject(mainPuzzle.completedTime, seed, height / 15), false);
                leftCannon.fire(50, height / 50, height / 25, sizeButtonC);
                rightCannon.fire(50, height / 50, height / 25, sizeButtonC);
                success.start();
                recorded = true;
            }

            leftCannon.update(height);
            rightCannon.update(height);
            leftCannon.draw(canvas, paint);
            rightCannon.draw(canvas, paint);
        }
        if (page.equals("SCORES") || nextPage.equals("SCORES")) {
            int shiftX = 0;
            if (nextPage.equals("SCORES")) {
                shiftX = nextPageX;
            }
            paint.setColor(Color.WHITE);
            canvas.drawRect(shiftX, 0, shiftX + width, height, paint);

            if (highscores.width != width - boxW); {
                highscores.width = width - boxW;
                highscores.screenHeight = height - width / 4 - boxW;
                highscores.x = boxW / 2;
                highscores.y = width / 8 + boxW / 2;

            }
            highscores.draw(canvas, paint, shiftX);

            paint.setTextSize(width / 8);
            paint.setColor(Color.argb(50, 0, 0, 0));
            canvas.drawText("Calcudoku", width / 2 - paint.measureText("Calcudoku") / 2 + shadow + shiftX, width / 8 + shadow, paint);
            paint.setColor(Color.BLACK);
            canvas.drawText("Calcudoku", width / 2 - paint.measureText("Calcudoku") / 2 + shiftX, width / 8, paint);

            if (Button.picButton(shiftX, 0, (int) ((width - paint.measureText("Calcudoku")) / 2.1), width / 8, home, 1, canvas, paint) == 1 && buttonsActive) {
                nextPage = "MENU";
                nextPageX = -width;
                buttonsActive = false;
            }
        }
        if (page.equals("HELP") || nextPage.equals("HELP")) {
            int shiftX = 0;
            if (nextPage.equals("HELP")) {
                shiftX = nextPageX;
                paint.setColor(Color.WHITE);
                canvas.drawRect(shiftX, 0, shiftX + width, height, paint);
            }
            paint.setTextSize(width / 8);
            paint.setColor(Color.rgb(210, 210, 210));
            canvas.drawText("Calcudoku", width / 2 - paint.measureText("Calcudoku") / 2 + shadow + shiftX, width / 8 + shadow, paint);
            paint.setColor(Color.BLACK);
            canvas.drawText("Calcudoku", width / 2 - paint.measureText("Calcudoku") / 2 + shiftX, width / 8, paint);

            if (Button.picButton(shiftX, 0, (int) ((width - paint.measureText("Calcudoku")) / 2.1), width / 8, home, 1, canvas, paint) == 1 && buttonsActive) {
                nextPage = "MENU";
                nextPageX = -width;
                buttonsActive = false;
            }
        }

        if (nextPage.equals("MENU")) {
            int shiftX = 0;
            if (nextPage.equals("MENU")) {
                shiftX = nextPageX;
                paint.setColor(Color.HSVToColor(menuColor));
                canvas.drawRect(shiftX, 0, shiftX + width, height, paint);
            }

            paint.setColor(Color.argb(50, 0, 0, 0));
            paint.setTextSize(width / 3);
            float offset = paint.measureText("C");
            canvas.drawText("C", width / 10 + shadow + shiftX, width * 11 / 24 + shadow, paint);
            paint.setTextSize(width / 8);
            canvas.drawText("alcudoku", width / 10 + offset + shadow + shiftX, width / 3 + shadow, paint);
            canvas.drawText("Generator", width / 5 + offset + shadow + shiftX, width * 11 / 24 + shadow, paint);

            paint.setColor(Color.BLACK);
            paint.setTextSize(width / 3);
            canvas.drawText("C", width / 10 + shiftX, width * 11 / 24, paint);
            paint.setTextSize(width / 8);
            canvas.drawText("alcudoku", width / 10 + offset + shiftX, width / 3, paint);
            canvas.drawText("Generator", width / 5 + offset + shiftX, width * 11 / 24, paint);

            if (Button.circleButton(width / 12 + shiftX, width * 3 / 5, width / 3, Color.rgb(200, 200, 200), Color.rgb(150, 150, 150), "NEW\nPUZZLE", width / 12, 1, canvas, paint) == 1 && buttonsActive) {
                nextPage = "SIZE";
                nextPageX = -width;
                buttonsActive = false;
            }

            if (Button.circleButton(width * 7 / 12 + shiftX, width * 3 / 5, width / 3, Color.rgb(200, 200, 200), Color.rgb(150, 150, 150), "HIGH\nSCORES", width / 12, 1, canvas, paint) == 1 && buttonsActive) {
                nextPage = "SCORES";
                nextPageX = -width;
                highscores.resetPages();
                buttonsActive = false;
            }

            if (Button.circleButton(width / 3 + shiftX, width, width / 3, Color.rgb(200, 200, 200), Color.rgb(150, 150, 150), "HOW TO", width / 12, 1, canvas, paint) == 1 && buttonsActive) {
                nextPage = "HELP";
                nextPageX = -width;
                buttonsActive = false;
            }

        }

        if (nextPageX < -2) {
            nextPageX /= 2;
            if (nextPageX >= -3 && nextPageX < 0) {
                page = nextPage;
                nextPage = "";
                nextPageX = 0;
                buttonsActive = true;
            }
        }

        Button.update();

    }

    public static void quad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, Canvas can, Paint pain) {
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.lineTo(x4, y4);
        path.lineTo(x1, y1);
        path.close();

        can.drawPath(path, pain);

    }
}
