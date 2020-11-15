package com.brainyology.calcudokugenerator;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Typeface myFont = Typeface.createFromAsset(getAssets(), "Last Resort");

        //context.getFilesDir();

        //getApplicationContext().getF

        setContentView(new GamePanel(this));
    }
}
