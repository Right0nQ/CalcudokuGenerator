package com.brainyology.calcudokugenerator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by testtube24 on 12/31/17.
 */

class Kenken {
    private int size;
    private int screenWidth;
    private int highlightX = 0;
    private int highlightY = 0;

    private int[] enteredNums;
    private int[] enteredColors;
    private ArrayList<Integer> prevMoves;

    private long startTime = System.currentTimeMillis();
    public int completedTime = 0;

    public boolean complete = false;
    public boolean notifyCompletion = true;
    public int notifX;

    public Kenken (int size) {
        this.size = size;
        enteredNums = new int[size * size];
        enteredColors = new int[size * size];
        prevMoves = new ArrayList<Integer>();
    }

    private int[] grid (long seed) {

        Random generator = new Random(seed);

        int[] gridNums = new int[size * size];
        for (int y = 0; y < size; y++) {
            for (int val = 1; val <= size; val++) {
                int[] places = new int[size];
                int[] altPlaces = new int[size];
                int a = 0;
                int b = 0;
                for (int xx = 0; xx < size; xx++) {
                    boolean yes = true;
                    for (int yy = 0; yy < y; yy++) {
                        if (gridNums[yy * size + xx] == val) {
                            yes = false;
                        }
                    }
                    if (yes && gridNums[y * size + xx] == 0) {
                        places[a] = xx;
                        a++;
                    } else if (yes) {
                        altPlaces[b] = xx;
                        b++;
                    }
                }
                int placing;
                if (a > 0) {
                    placing = Math.round((float) (generator.nextDouble() * a - 0.5));
                    gridNums[y * size + places[placing]] = val;
                } else {
                    placing = Math.round((float) (generator.nextDouble() * b - 0.5));
                    int nextVal = gridNums[y * size + altPlaces[placing]];
                    gridNums[y * size + altPlaces[placing]] = val;
                    boolean continueLoop = true;
                    while (continueLoop) {
                        places = new int[size];
                        altPlaces = new int[size];
                        a = 0;
                        b = 0;
                        for (int xx = 0; xx < size; xx++) {
                            boolean yes = true;
                            for (int yy = 0; yy < y; yy++) {
                                if (gridNums[yy * size + xx] == nextVal) {
                                    yes = false;
                                }
                            }
                            if (yes && gridNums[y * size + xx] == 0) {
                                places[a] = xx;
                                a++;
                            } else if (yes) {
                                altPlaces[b] = xx;
                                b++;
                            }
                        }
                        if (a > 0) {
                            placing = Math.round((float) (generator.nextDouble() * a - 0.5));
                            gridNums[y * size + places[placing]] = nextVal;
                            continueLoop = false;
                        } else {
                            placing = Math.round((float) (generator.nextDouble() * b - 0.5));
                            int holdVal = gridNums[y * size + altPlaces[placing]];
                            gridNums[y * size + altPlaces[placing]] = nextVal;
                            nextVal = holdVal;
                            continueLoop = true;
                        }
                    }
                }
            }
        }
        return gridNums;
    }

    private ReturnTwoArrays findRepeats (int[] gridNums, long seed) {

        Random generator = new Random(seed);

        int a;
        int b;
        int[] repeatsX = new int[size * size + 4 - ((size * size) % 4)];//ab in row vertically over ba in another row
        int xLength = 0;
        int[] repeatsY = new int[size * size + 4 - ((size * size) % 4)];//ab in column next to ba in another column
        int yLength = 0;
        for (int yy = 0; yy < size - 1; yy++) {
            for (int x = 0; x < size - 1; x++) {
                a = gridNums[yy * size + x];
                b = gridNums[yy * size + x + 1];
                for (int y = yy + 1; y < size; y++) {
                    if (a == gridNums[y * size + x + 1] && b == gridNums[y * size + x]) {
                        if (generator.nextDouble() < 0.5) {
                            repeatsX[xLength] = x;
                            repeatsX[xLength + 1] = yy;
                            repeatsX[xLength + 2] = x + 1;
                            repeatsX[xLength + 3] = yy;
                            xLength += 4;
                        } else {
                            repeatsX[xLength] = x;
                            repeatsX[xLength + 1] = y;
                            repeatsX[xLength + 2] = x + 1;
                            repeatsX[xLength + 3] = y;
                            xLength += 4;
                        }
                        //if (xLength != 4) {
                        //System.out.println(repeatsX[xLength - 4] + ", " + repeatsX[xLength - 3] + ", " + repeatsX[xLength - 2] + ", " + repeatsX[xLength - 1]);
                        //}
                    }
                }
            }
        }
        for (int xx = 0; xx < size - 1; xx++) {
            for (int y = 0; y < size - 1; y++) {
                a = gridNums[y * size + xx];
                b = gridNums[y * size + xx + size];
                for (int x = xx + 1; x < size; x++) {
                    if (a == gridNums[y * size + x + size] && b == gridNums[y * size + x]) {
                        if (generator.nextDouble() < 0.5) {
                            repeatsY[yLength] = xx;
                            repeatsY[yLength + 1] = y;
                            repeatsY[yLength + 2] = xx;
                            repeatsY[yLength + 3] = y + 1;
                            yLength += 4;
                        } else {
                            repeatsY[yLength] = x;
                            repeatsY[yLength + 1] = y;
                            repeatsY[yLength + 2] = x;
                            repeatsY[yLength + 3] = y + 1;
                            yLength += 4;
                        }
                        if (xLength >= 4) {
                            //System.out.println(repeatsY[yLength - 4] + ", " + repeatsY[yLength - 3] + ", " + repeatsY[yLength - 2] + ", " + repeatsY[yLength - 1]);
                        }
                    }
                }
            }
        }
        return new ReturnTwoArrays(repeatsX, repeatsY, xLength, yLength);
    }

    public KenkenBlocks makeBlocks (int[] gridNums, int[] repeatsX, int[] repeatsY, long seed) {

        Random generator = new Random(seed);

        //boolean continueLoop = true;
        int x = 0;
        int y = 0;
        int oneBlocks = 0;
        int maxOneBlocks = size - 4;
        int[] copyGridNums = new int[size * size];
        for (int i = 0; i < copyGridNums.length; i++) {
            copyGridNums[i] = gridNums[i];
        }
        KenkenBlocks head = new KenkenBlocks(new int[0], new int[0], 0);
        while (true) {
            try {
                while (gridNums[y * size + x] == 0) {
                    x++;
                    if (x == size) {
                        y++;
                        x = 0;
                    }
                }
            } catch (Exception exc) {
                //continueLoop = false;
                break;
            }
            gridNums[y * size + x] = 0;
            int[] coords = new int[6];
            int cLength = 2;
            int[] freeCoords = new int[12];
            int fclength = 0;
            coords[0] = x;
            coords[1] = y;
            int xRepeatsX = -2;
            int yRepeatsY = -2;
            for (int i = 0; i < repeatsX.length; i += 2) {
                if (repeatsX[i] == x && repeatsX[i + 1] == y) {
                    if ((i / 2) % 2 == 0) {
                        xRepeatsX = repeatsX[i + 2];
                    } else {
                        xRepeatsX = repeatsX[i - 2];
                    }
                }
            }
            for (int i = 0; i < repeatsY.length; i += 2) {
                if (repeatsY[i] == x && repeatsY[i + 1] == y) {
                    if ((i / 2) % 2 == 0) {
                        yRepeatsY = repeatsY[i + 3];
                    } else {
                        yRepeatsY = repeatsY[i - 1];
                    }
                }
            }

            if ((x != 0 && x - 1 != xRepeatsX) && gridNums[y * size + x - 1] != 0) {
                freeCoords[fclength] = x - 1;
                freeCoords[fclength + 1] = y;
                fclength += 2;
            }
            if ((x != size - 1 && x + 1 != xRepeatsX) && gridNums[y * size + x + 1] != 0) {
                freeCoords[fclength] = x + 1;
                freeCoords[fclength + 1] = y;
                fclength += 2;
            }
            if ((y != 0 && y - 1 != yRepeatsY) && gridNums[y * size + x - size] != 0) {
                freeCoords[fclength] = x;
                freeCoords[fclength + 1] = y - 1;
                fclength += 2;
            }
            if ((y != size - 1 && y + 1 != yRepeatsY) && gridNums[y * size + x + size] != 0) {
                freeCoords[fclength] = x ;
                freeCoords[fclength + 1] = y + 1;
                fclength += 2;
            }
            double a = generator.nextDouble();
            if ((a < 0.99 || (a > 0.99 && oneBlocks >= maxOneBlocks)) && fclength != 0) {
                int newCoord = Math.round((float) ((generator.nextDouble() * fclength / 2) - 0.5));
                newCoord *= 2;
                gridNums[freeCoords[newCoord + 1] * size + freeCoords[newCoord]] = 0;
                coords[2] = freeCoords[newCoord];
                coords[3] = freeCoords[newCoord + 1];
                x = coords[2];
                y = coords[3];
                cLength = 4;

                int[] holder = new int[fclength + 6];
                int c = 0;
                for (int i = 0; i < fclength; i++) {
                    if (i != newCoord && i != newCoord + 1) {
                        holder[c] = freeCoords[i];
                        c++;
                    }
                }
                fclength -= 2;
                freeCoords = holder;

                xRepeatsX = -2;
                yRepeatsY = -2;
                for (int i = 0; i < repeatsX.length; i += 2) {
                    if (repeatsX[i] == x && repeatsX[i + 1] == y) {
                        if ((i / 2) % 2 == 0) {
                            xRepeatsX = repeatsX[i + 2];
                        } else {
                            xRepeatsX = repeatsX[i - 2];
                        }
                    }
                }
                for (int i = 0; i < repeatsY.length; i += 2) {
                    if (repeatsY[i] == x && repeatsY[i + 1] == y) {
                        if ((i / 2) % 2 == 0) {
                            yRepeatsY = repeatsY[i + 3];
                        } else {
                            yRepeatsY = repeatsY[i - 1];
                        }
                    }
                }

                if ((x != 0 && x - 1 != xRepeatsX) && gridNums[y * size + x - 1] != 0) {
                    freeCoords[fclength] = x - 1;
                    freeCoords[fclength + 1] = y;
                    fclength += 2;
                }
                if ((x != size - 1 && x + 1 != xRepeatsX) && gridNums[y * size + x + 1] != 0) {
                    freeCoords[fclength] = x + 1;
                    freeCoords[fclength + 1] = y;
                    fclength += 2;
                }
                if ((y != 0 && y - 1 != yRepeatsY) && gridNums[y * size + x - size] != 0) {
                    freeCoords[fclength] = x;
                    freeCoords[fclength + 1] = y - 1;
                    fclength += 2;
                }
                if ((y != size - 1 && y - 1 != yRepeatsY) && gridNums[y * size + x + size] != 0) {
                    freeCoords[fclength] = x ;
                    freeCoords[fclength + 1] = y + 1;
                    fclength += 2;
                }
                if (generator.nextDouble() < 0.4 && fclength != 0) {
                    newCoord = Math.round((float) ((generator.nextDouble() * fclength / 2) - 0.5));
                    newCoord *= 2;
                    gridNums[freeCoords[newCoord + 1] * size + freeCoords[newCoord]] = 0;
                    coords[4] = freeCoords[newCoord];
                    coords[5] = freeCoords[newCoord + 1];
                    cLength = 6;
                }
            } else {
                oneBlocks++;
            }
            int[] values = new int[3];
            for (int i = 0; i < cLength / 2; i++) {
                values[i] = copyGridNums[coords[i * 2 + 1] * size + coords[i * 2]];
            }
            if (head.cLength == 0) {
                head = new KenkenBlocks(coords, values, cLength);
            } else {
                KenkenBlocks body = head;
                while (body.tail != null) {
                    body = body.tail;
                }
                body.tail = new KenkenBlocks(coords, values, cLength);
            }
            x = 0;
            y = 0;

        }
        return head;
    }

    public KenkenBlocks create (long fromSeed) {
        long seed = fromSeed > 0? fromSeed: (long) (Math.random() * Integer.MAX_VALUE);
        int[] gridN = new int[size * size];
        gridN = grid(seed);
        ReturnTwoArrays testRepeats = findRepeats(gridN, seed);

        KenkenBlocks head = makeBlocks(gridN, testRepeats.getArrayOne(), testRepeats.getArrayTwo(), seed);

        Random generator = new Random(seed);
        seed = generator.nextLong();

        head.assignHint(seed);
        if (head.tail != null) {
            KenkenBlocks body = head.tail;
            while (true) {
                seed = generator.nextLong();
                body.assignHint(seed);
                //body.draw();
                if (body.tail != null) {
                    body = body.tail;
                } else {
                    break;
                }
            }
        }
        return head;
    }

    public void draw (Canvas canvas, Paint mainPaint, KenkenBlocks body, int w, int shiftX) {
        Paint paint = new Paint();
        //g.setFont(new Font("monospace", Font.PLAIN, 50));
        screenWidth = w;
        int boxW = (screenWidth - 100) / size;
        int downShift = screenWidth / 7;
        paint.setTextSize(screenWidth / 10);
        paint.setColor(Color.YELLOW);
        canvas.drawRect(50 + highlightX * boxW + shiftX, downShift + highlightY * boxW, 50 + highlightX * boxW + boxW + shiftX, downShift + highlightY * boxW + boxW, paint);
        //paint.setColor(Color.BLACK);
        //canvas.drawText("Calcudoku", w / 2 - paint.measureText("Calcudoku") / 2, screenWidth / 10, paint);
        //g.setFont(new Font("monospace", Font.PLAIN, 10));
        //paint.font
        paint.setTextSize(boxW / 3);
        KenkenBlocks copyBody = body;
        int strk = Math.max((int) boxW / 40, 1);
        paint.setStrokeWidth(strk);

        paint.setColor(Color.DKGRAY);
        for (int i = 0; i <= size; i++) {
            canvas.drawLine(50 + i * boxW + shiftX, downShift, 50 + i* boxW + shiftX, downShift + boxW * size, paint);
        }
        for (int i = 0; i <= size; i++) {
            canvas.drawLine(50 + shiftX, downShift + i * boxW, 50 + size * boxW + shiftX, downShift + i* boxW, paint);
        }

        //strk = strk * 2;
        paint.setStrokeWidth(strk * 3);
        paint.setColor(Color.BLACK);
        while (true) {
            if (body != null && body.hint != null) {
                int topRightX = 0;
                int topRightY = size;
                for (int i = 0; i < body.cLength; i += 2) {
                    int x = body.coords[i];
                    int y = body.coords[i + 1];
                    if (y < topRightY || (y == topRightY && x > topRightX)) {
                        topRightX = x;
                        topRightY = y;
                    }
                    boolean left = true;
                    boolean right = true;
                    boolean up = true;
                    boolean down = true;
                    for (int j = 0; j < body.cLength; j += 2) {
                        if (j != i) {
                            if (body.coords[j] == x - 1 && body.coords[j + 1] == y) {
                                left = false;
                            } else if (body.coords[j] == x + 1 && body.coords[j + 1] == y) {
                                right = false;
                            } else if (body.coords[j] == x && body.coords[j + 1] == y - 1) {
                                up = false;
                            } else if (body.coords[j] == x && body.coords[j + 1] == y + 1) {
                                down = false;
                            }
                        }
                    }
                    if (left) {
                        canvas.drawLine(50 + x * boxW + shiftX, downShift + y * boxW, 50 + x * boxW + shiftX, downShift + y * boxW + boxW, paint);
                    }
                    if (right) {
                        canvas.drawLine(50 + x * boxW + boxW + shiftX, downShift + y * boxW, 50 + x * boxW + boxW + shiftX, downShift + y * boxW + boxW, paint);
                    }
                    if (up) {
                        canvas.drawLine(50 + x * boxW + shiftX, downShift + y * boxW, 50 + x * boxW + boxW + shiftX, downShift + y * boxW, paint);
                    }
                    if (down) {
                        canvas.drawLine(50 + x * boxW + shiftX, downShift + y * boxW + boxW, 50 + x * boxW + boxW + shiftX, downShift + y * boxW + boxW, paint);
                    }
                }
                canvas.drawText(body.hint, 45 + topRightX * boxW - paint.measureText(body.hint) + boxW + shiftX, downShift + topRightY * boxW + 3 * (boxW / 8), paint);
            } else {
                break;
            }
            if (body.tail != null) {
                body = body.tail;
            } else {
                break;
            }
        }
        paint.setTextSize(screenWidth / 8);
        float timeAlign = paint.measureText("Calcudoku") / 2;

        paint.setTextSize((float) downShift / 2);
        if (!complete) {
            int sec = Math.round((float) (System.currentTimeMillis() - startTime) / 1000);
            String txt = Integer.toString(Math.round((float) (sec / 60 - 0.5))) + ":" + (sec % 60 < 10? "0": "") + Integer.toString(sec % 60);
            canvas.drawText(txt, screenWidth * 3/ 4 + timeAlign / 2 - paint.measureText(txt) / 2 + shiftX, screenWidth / 8 /*size * boxW + (screenWidth) / 4*/, paint);
        } else if (complete && !notifyCompletion) {
            String txt = Integer.toString(Math.round((float) (completedTime / 60 - 0.5))) + ":" + (completedTime % 60 < 10? "0": "") + Integer.toString(completedTime % 60);
            canvas.drawText(txt, screenWidth * 3 / 4 + timeAlign / 2 - paint.measureText(txt) / 2 + shiftX, screenWidth / 8, paint);
        }

        //g.setFont(new Font("monospace", Font.PLAIN, 20));
        paint.setTextSize((boxW * 4) / 5);
        boolean filledIn = true;
        for (int i = 0; i < enteredNums.length; i++) {
            if (enteredNums[i] != 0) {
                paint.setColor(enteredColors[i]);
                canvas.drawText(Integer.toString(enteredNums[i]), 50 + (i % size) * boxW + boxW / 2 - paint.measureText(Integer.toString(enteredNums[i])) / 2 + shiftX, downShift + Math.round((float) (i / size - 0.5)) * boxW + (boxW - paint.ascent()) / 2, paint);
            } else {
                filledIn = false;
            }
        }

        if (filledIn && !complete) {
            boolean correct = true;
            while (true) {
                if (copyBody != null) {
                    if (copyBody.cLength != 2) {
                        String operation = Character.toString(copyBody.hint.charAt(0));

                        int val = 0;
                        int[] cl = copyBody.coords;
                        //System.out.println(operation + " , " + (operation == "+"));
                        if (operation.equals("+")) {
                            //System.out.println(1);
                            val = enteredNums[cl[0] + cl[1] * size] + enteredNums[cl[2] + cl[3] * size] + (copyBody.cLength > 4? enteredNums[cl[4] + cl[5] * size] : 0);
                        } else if (operation.equals("-")) {
                            val = Math.max(enteredNums[cl[0] + cl[1] * size], enteredNums[cl[2] + cl[3] * size]) - Math.min(enteredNums[cl[0] + cl[1] * size], enteredNums[cl[2] + cl[3] * size]);
                            //System.out.println(2);
                        } else if (operation.equals("*")) {
                            val = enteredNums[cl[0] + cl[1] * size] * enteredNums[cl[2] + cl[3] * size] * (copyBody.cLength > 4? enteredNums[cl[4] + cl[5] * size] : 1);
                            //System.out.println(3);
                        } else if (operation.equals("/")) {
                            val = Math.max(enteredNums[cl[0] + cl[1] * size], enteredNums[cl[2] + cl[3] * size]) / Math.min(enteredNums[cl[0] + cl[1] * size], enteredNums[cl[2] + cl[3] * size]);
                            //System.out.println(4);
                        }

                        if (val != Integer.parseInt(copyBody.hint.substring(1))) {
                            //System.out.println(val + " , " + Integer.parseInt(copyBody.hint.substring(1)));
                            correct = false;
                        }

                    } else {
                        if (enteredNums[copyBody.coords[0] + copyBody.coords[1] * size] != Integer.parseInt(copyBody.hint)) {
                            correct = false;
                        }
                    }
                } else {
                    break;
                }
                if (copyBody.tail != null) {
                    copyBody = copyBody.tail;
                } else {
                    break;
                }
            }
            if (correct) {
                for (int yy = 0; yy < size - 1; yy++) {
                    for (int x = 0; x < size; x++) {
                        for (int y = yy + 1; y < size; y++) {
                            if (enteredNums[y * size + x] == enteredNums[yy * size + x]) {
                                correct = false;
                            }
                        }
                    }
                }
                for (int xx = 0; xx < size - 1; xx++) {
                    for (int y = 0; y < size; y++) {
                        for (int x = xx + 1; x < size; x++) {
                            if (enteredNums[y * size + x] == enteredNums[y * size + xx]) {
                                correct = false;
                            }
                        }
                    }
                }
            }
            if (correct) {
                //System.out.println(1);
                complete = true;
                completedTime = Math.round((float) (System.currentTimeMillis() - startTime) / 1000);
                notifX = -screenWidth;
            }

        }

        if (complete && notifyCompletion) {
            //System.out.println(2);
            //g.setFont(new Font("monospace", Font.PLAIN, 20));
            mainPaint.setColor(Color.BLACK);
            canvas.drawRect(50 + downShift / 2 - strk + notifX, (size * boxW) / 2 - downShift / 2 - strk, 50 + size * boxW - (downShift / 2) + strk + notifX, downShift * 5 / 2 + (size * boxW) / 2 + strk, mainPaint);
            mainPaint.setColor(Color.rgb(200, 200, 255));
            canvas.drawRect(50 + downShift / 2 + notifX, (size * boxW) / 2 - downShift / 2, 50 + size * boxW - (downShift / 2) + notifX, downShift * 5 / 2 + (size * boxW) / 2, mainPaint);
            mainPaint.setColor(Color.BLACK);
            mainPaint.setTextSize(downShift * 9 / 8);
            //canvas.drawRect(60, 10 + (size * boxW) / 2, size * boxW - 20, 80, paint);
            canvas.drawText("Nice Job!", (size * boxW) / 2 + 50 - mainPaint.measureText("Nice Job!") / 2 + notifX, downShift + (size * boxW) / 2, mainPaint);
            //g.setFont(new Font("monospace", Font.PLAIN, 30));
            mainPaint.setTextSize(downShift);
            String time = Integer.toString(Math.round((float) (completedTime / 60 - 0.5))) + ":" + (completedTime % 60 < 10? "0": "") + Integer.toString(completedTime % 60);
            canvas.drawText(time, (size * boxW) / 2 + 50 - mainPaint.measureText(time) / 2 + notifX, downShift + (size * boxW) / 2 - (mainPaint.ascent() * 5) / 4, mainPaint);
            notifX /= 2;
        }

        if (complete && notifyCompletion && Button.rectButton(50 + size * boxW - downShift * 11 / 10 + notifX, (size * boxW) / 2 - downShift * 2 / 5, downShift / 2, downShift / 2, Color.rgb(200, 200, 255), Color.rgb(200, 200, 255), "X", downShift / 2, 1, canvas, mainPaint) == 1)
            notifyCompletion = false;

    }

    public void enterNum(int num, int color) {
        if (num != -1 && (enteredNums[highlightY * size + highlightX] != num || enteredColors[highlightY * size + highlightX] != color)) {
            prevMoves.add(highlightY * size + highlightX);
            prevMoves.add(enteredNums[highlightY * size + highlightX]);
            prevMoves.add(enteredColors[highlightY * size + highlightX]);
            enteredNums[highlightY * size + highlightX] = num;
            enteredColors[highlightY * size + highlightX] = color;
        } else if (num == -1 && prevMoves.size() >= 3) {
            enteredNums[prevMoves.get(prevMoves.size() - 3)] = prevMoves.get(prevMoves.size() - 2);
            enteredColors[prevMoves.get(prevMoves.size() - 3)] = prevMoves.get(prevMoves.size() - 1);
            prevMoves.remove(prevMoves.size() - 3);
            prevMoves.remove(prevMoves.size() - 2);
            prevMoves.remove(prevMoves.size() - 1);
        }
    }

    public void clear() {
        enteredNums = new int[size * size];
        enteredColors = new int[size * size];
    };

    public void touchListen(MotionEvent e) {
        int boxW = (screenWidth - 100) / size;
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if ((!complete || (complete && !notifyCompletion)) && e.getX() > 50 && e.getX() < 50 + size * boxW && e.getY() > screenWidth / 7 && e.getY() < screenWidth / 7 + size * boxW) {
                highlightX = Math.round((float) ((e.getX() - 50) / boxW - 0.5));
                highlightY = Math.round((float) ((e.getY() - screenWidth / 7) / boxW - 0.5));
            }
        }
    }

}