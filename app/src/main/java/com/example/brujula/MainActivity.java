package com.example.brujula;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Log.Brujula";

    private Brujula miBrujula;
    private ImageView arrowView;
    private TextView sotwLabel;  // SOTW is for "side of the world"

    private float currentAzimuth;
    private SOTWFormatter sotwFormatter;
    private boolean brujulaActiva=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sotwFormatter = new SOTWFormatter(this);

        arrowView = findViewById(R.id.main_image_hands);
        sotwLabel = findViewById(R.id.sotw_label);
        setupCompass();


        sotwLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
               if (brujulaActiva) {
                   miBrujula.stop();
                   brujulaActiva = false;
               }else{
                   miBrujula.start();
                   brujulaActiva=true;
               }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "start miBrujula");
        miBrujula.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        miBrujula.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        miBrujula.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stop miBrujula");
        miBrujula.stop();
    }

    private void setupCompass() {
        miBrujula = new Brujula(this);
        Brujula.CompassListener cl = getCompassListener();
        miBrujula.setListener(cl);
    }

    private void adjustArrow(float azimuth) {
        Log.d(TAG, "will set rotation from " + currentAzimuth + " to " + azimuth);

        Animation an = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = azimuth;

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        arrowView.startAnimation(an);
    }

    private void adjustSotwLabel(float azimuth) {
        sotwLabel.setText(sotwFormatter.format(azimuth));
    }

    private Brujula.CompassListener getCompassListener() {
        return new Brujula.CompassListener() {
            @Override
            public void onNewAzimuth(final float azimuth) {
                // UI updates only in UI thread
                // https://stackoverflow.com/q/11140285/444966
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adjustArrow(azimuth);
                        adjustSotwLabel(azimuth);
                    }
                });
            }
        };
    }
}
