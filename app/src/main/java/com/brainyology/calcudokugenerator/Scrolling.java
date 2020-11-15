package com.brainyology.calcudokugenerator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by testtube24 on 2/24/18.
 */

public class Scrolling {

    private Context context;

    private ArrayList<ArrayList<ScrollingObject>> list;
    private ArrayList<String> titles;
    public int curPage;
   // private int totalHeight;
    public int screenHeight;
    private int scrollY;

    //public boolean firstFrame = true;//set to false first frame open every time
    //public int framesDown = 0;
    //public static final int totalFramesDown = 5;


    public int x;
    public int y;
    public int width;
    public ArrayList<Integer> listLengths;
    public ArrayList<Integer> listHeights;

    private int prevAction;
    private boolean clicking;

    private File scoreFile;
    private FileInputStream read;
    private FileOutputStream write;

    public Scrolling(int x, int y, int width, int screenHeight, Context context) {
        this.context = context;

        list = new ArrayList<ArrayList<ScrollingObject>>();
        titles = new ArrayList<String>();
        listLengths = new ArrayList<Integer>();
        listHeights = new ArrayList<Integer>();
        scrollY = 0;
        curPage = 0;
        //framesDown = 0;

        this.x = x;
        this.y = y;
        this.width = width;
        this.screenHeight = screenHeight;

        clicking = false;
    }

    public void readFile() {

        File directory = context.getFilesDir();

        scoreFile = new File(directory, "scoreFile");

        //FileOutputStream writeRead;
        //FileInputStream read;
        //clearScores();

        try {
            scoreFile.createNewFile();
            read = context.openFileInput("scoreFile");
            //write = context.openFileOutput("scoreFile", Context.MODE_PRIVATE);
        } catch (IOException exc) {
            exc.printStackTrace();
            return;
        }

        //ArrayList<Byte> data = new ArrayList<Byte>();

        /*  try {
            while (true) {
                data.add((byte) read.read());
                if (data.get(data.size() - 1) == -1)
                    break;
            }
        } catch (IOException exc) {
            exc.printStackTrace();
            System.out.println("BUGGGGGGGG3");
        }

        System.out.println(data.size() + " AAA");

        for (int i = 0; i < data.size() - 19; i += 20) {
            //System.out.println(data.get(i) + " : " + data.get(i+1) + " : " + data.get(i+2) + " : " + data.get(i+3));
            //System.out.println((8 << 2) & (5));
            int time = (data.get(i) << 24) | (data.get(i + 1) << 16) | (data.get(i+2) << 8) | data.get(i+3);
            long seed = (data.get(i+4) << 56) | (data.get(i+5) << 48) | (data.get(i+6) << 40) | (data.get(i+7) << 32) | (data.get(i+8) << 24) | (data.get(i+9) << 16) | (data.get(i+10) << 8) | data.get(i+11);
            int height = (data.get(i+12) << 24) | (data.get(i+13) << 16) | (data.get(i+14) << 8) | data.get(i+15);
            int page = (data.get(i+12) << 24) | (data.get(i+13) << 16) | (data.get(i+14) << 8) | data.get(i+15);

            add(page, new ScrollingObject(time, seed, height), true);

        }*/
        ArrayList<byte[]> allInfo = new ArrayList<byte[]>();

        while (true) {
            byte[] info = new byte[20];
            try {
                int i = read.read(info);
                if (i != 20) {
                    System.out.println(i);
                    break;
                }
            } catch(IOException exc) {
                exc.printStackTrace();
                break;
            }

            allInfo.add(info);

            ByteBuffer b = ByteBuffer.allocate(20);
            b.put(info);

            int time = b.getInt(0);
            long seed = b.getLong(4);
            int height = b.getInt(12);
            int page = b.getInt(16);

            add(page, new ScrollingObject(time, seed, height), true);
        }

        try {
            read.close();
        } catch (IOException exc) {
            exc.printStackTrace();
            return;
        }

        try {
            write = context.openFileOutput("scoreFile", Context.MODE_PRIVATE);
            for (int i = 0; i < allInfo.size(); i++) {
                write.write(allInfo.get(i));
            }
        } catch (IOException exc) {
            exc.printStackTrace();
            return;
        }

    }

    public void clearScores() {
        scoreFile.delete();
    }

    public void add(int page, ScrollingObject s, boolean readingFile) {
        //System.out.println(page + " : " + s.getSeed() + " : " + s.getTime() + " : " + s.getHeight());

        if (page >= listHeights.size())
            return;

        listHeights.set(page, listHeights.get(page) + s.getHeight());
        //System.out.println(totalHeight);

        boolean added = false;
        if (list.get(page).size() == 0 || list.get(page).get(0).getTime() > s.getTime()) {
            list.get(page).add(0, s);
            listLengths.set(page, listLengths.get(page) + 1);
            added = true;
        }

        if (!added) {
            int i = 0;

            //System.out.println(s.getTime() + " /; " + list.get(page).get(i).getTime());

            while (i < list.get(page).size() && list.get(page).get(i).getTime() <= s.getTime())
                i++;

            list.get(page).add(i, s);

            listLengths.set(page, listLengths.get(page) + 1);
        }

        if (!readingFile) {
            try {
                //write = context.openFileOutput("scoreFile", Context.MODE_PRIVATE);
                byte[] info = ByteBuffer.allocate(4).putInt(s.getTime()).array();
                write.write(info);
                info = ByteBuffer.allocate(8).putLong(s.getSeed()).array();
                write.write(info);
                info = ByteBuffer.allocate(4).putInt(s.getHeight()).array();
                write.write(info);
                info = ByteBuffer.allocate(4).putInt(page).array();
                write.write(info);
                //read = context.openFileInput("scoreFile");
                //System.out.println(read.read() + " : " + info[3]);
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }
    }

    public void addPage(String title) {
        list.add(new ArrayList<ScrollingObject>());
        listLengths.add(0);
        listHeights.add(0);
        titles.add(title);
    }

    public void remove(int n) {
        list.remove(n);
    }

    public void scrollDown(int n) {
        scrollY = Math.min(Math.max(listHeights.get(curPage) - screenHeight + width / 10, 0), scrollY + n);
        //System.out.println(scrollY + " ; " + totalHeight + " : " + screenHeight);
    }

    public void scrollUp(int n) {
        scrollY = Math.max(0, scrollY - n);
    }

    public void resetPages() {
        scrollY = 0;
        curPage = 0;
    }

    public long touchListen(MotionEvent e, Typeface font) {
        if (e.getAction() == MotionEvent.ACTION_MOVE && e.getHistorySize() != 0) {
            if (Math.abs(e.getHistoricalY(0) - e.getY()) > 2 && Math.abs(e.getHistoricalX(0) - e.getX()) > 2) {
                clicking = false;
            }
            //System.out.println(e.getHistorySize() + " ; " + (e.getHistoricalY(0) - e.getY()) + " : " + scrollY + " ; " + totalHeight + " : " + screenHeight);
            float deltaY = (e.getHistoricalY(0) - e.getY());//e.getHistoricalY(0) - e.getY();
            if (deltaY > 0)
                scrollDown((int) deltaY);
            else
                scrollUp((int) -deltaY);
        } else if (e.getAction() == MotionEvent.ACTION_UP && (prevAction != MotionEvent.ACTION_MOVE || clicking)) {
            Paint paint = new Paint();
            paint.setTextSize(width / 12);
            paint.setTypeface(font);

            int shift = 0;
            for (int i = 0; i < list.size(); i++) {
                float xDist = paint.measureText(titles.get(i)) * 3 / 2;

                if (e.getX() > x + shift && e.getX() < x + shift + xDist && e.getY() > y + screenHeight - width / 10 && e.getY() < y + screenHeight) {
                    curPage = i;
                    scrollY = 0;
                    return -1;
                }

                shift += xDist;
            }

            int height = 0;
            for (int i = 0; i < list.get(curPage).size(); i++) {
                height += list.get(curPage).get(i).getHeight() + width / 100;
                if (height - scrollY > 0) {
                    if (e.getX() > x && e.getX() < x + width && e.getY() > y + (height - scrollY - list.get(curPage).get(i).getHeight()) && e.getY() < y + (height - scrollY))
                        return list.get(curPage).get(i).getSeed();
                }
            }
        } else if (e.getAction() == MotionEvent.ACTION_DOWN) {
            clicking = true;
        }
        prevAction = e.getAction();
        /*System.out.println(e.getAction());

        if (mainScreen.equals("SCORES") && framesDown < totalFramesDown)
            framesDown++;*/
        return -1;
    }
    public void draw(Canvas canvas, Paint mainPaint, int shiftX) {
        Paint paint = new Paint();
        paint.setColor(Color.rgb(220, 220, 220));
        canvas.drawRect(x + shiftX, y, x + width + shiftX, y + screenHeight - width / 10, paint);
        paint.setColor(Color.rgb(180, 180, 180));

        mainPaint.setColor(Color.BLACK);
        int height = 0;
        for (int i = 0; i < list.get(curPage).size(); i++) {
            mainPaint.setTextSize(list.get(curPage).get(i).getHeight() * 2 / 3);
            height += list.get(curPage).get(i).getHeight();

            if ((height - scrollY) < 0)
                continue;

            if ((height - scrollY - list.get(curPage).get(i).getHeight()) > screenHeight - width / 10)
                break;

            String time = Integer.toString(Math.round((float) (list.get(curPage).get(i).getTime() / 60 - 0.5))) + ":" + (list.get(curPage).get(i).getTime() % 60 < 10? "0": "") + Integer.toString(list.get(curPage).get(i).getTime() % 60);
            if (height - scrollY > 0) {
                canvas.drawRect(x + width / 100 + shiftX, y + (height - scrollY - list.get(curPage).get(i).getHeight()) + width / 100, x + width - width / 100 + shiftX, y + (height - scrollY), paint);
                canvas.drawText((i + 1) + ".", x + width / 50 + shiftX, y + (height - scrollY - list.get(curPage).get(i).getHeight()) + list.get(curPage).get(i).getHeight() * 5 / 6, mainPaint);
                //canvas.drawText(list.get(curPage).get(i).getName(), x + width / 10, y + (height - scrollY - list.get(curPage).get(i).getHeight()) + list.get(curPage).get(i).getHeight() * 13 / 30, mainPaint);
                canvas.drawText(time, x + width / 5 + shiftX, y + (height - scrollY - list.get(curPage).get(i).getHeight())+ list.get(curPage).get(i).getHeight() * 5 / 6, mainPaint);
                canvas.drawText("#" + list.get(curPage).get(i).getSeed(), x + width / 5 + mainPaint.measureText(time) + width / 10 + shiftX, y + (height - scrollY - list.get(curPage).get(i).getHeight())+ list.get(curPage).get(i).getHeight() * 5 / 6, mainPaint);
            }
        }

        paint.setColor(Color.rgb(220, 220, 220));
        canvas.drawRect(x + shiftX, y, x + width + shiftX, y + width / 100, paint);
        canvas.drawRect(x + shiftX, y + screenHeight - width * 11 / 100, x + width + shiftX, y + screenHeight - width / 10, paint);
        paint.setColor(Color.rgb(255, 255, 255));
        canvas.drawRect(x + shiftX, y - screenHeight / 6, x + width + shiftX, y, paint);
        canvas.drawRect(x + shiftX, y + screenHeight - width / 10, x + width + shiftX, y + screenHeight - width / 10 + screenHeight / 6, paint);

        if (list.get(curPage).size() == 0) {
            mainPaint.setTextSize(mainPaint.getTextSize() * (width * 4 / 5) / mainPaint.measureText("None"));
            mainPaint.setColor(Color.rgb(180, 180, 180));

            canvas.drawText("None", x + width / 10 + shiftX, y + screenHeight / 2, mainPaint);
        }

        mainPaint.setTextSize(width / 12);
        mainPaint.setColor(Color.BLACK);

        int shift = 0;

        for (int i = 0; i < list.size(); i++) {
            if (i == curPage)
                paint.setColor(Color.rgb(220, 220, 220));
            else
                paint.setColor(Color.rgb(180, 180, 180));

            float xDist = mainPaint.measureText(titles.get(i)) * 3 / 2;

            GamePanel.quad(x + shift + shiftX, y + screenHeight - width / 10, x + shift + xDist + shiftX, y + screenHeight - width / 10, x + shift + xDist - width / 50 + shiftX, y + screenHeight, x + shift + width / 50 + shiftX, y + screenHeight, canvas, paint);
            canvas.drawText(titles.get(i), x + shift + xDist / 6 + shiftX, y + screenHeight - width / 120, mainPaint);

            shift += xDist;
        }
    }
}
