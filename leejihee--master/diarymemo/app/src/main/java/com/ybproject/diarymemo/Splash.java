package com.ybproject.diarymemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by 즤 on 2016-06-05.
 */
public class Splash extends Activity {

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashh);

        final ImageView iv = (ImageView) findViewById(R.id.imageView);
        Intent i = new Intent(getBaseContext(), Grid_mainActivity.class);
        startActivity(i);
    }
}
