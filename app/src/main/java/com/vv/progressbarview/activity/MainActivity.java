package com.vv.progressbarview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vv.progressbarview.R;
import com.vv.progressbarview.interfaces.ProgressListener;
import com.vv.progressbarview.widget.ProgressBarView;

public class MainActivity extends AppCompatActivity {

    ProgressBarView mProgressBar;
    TextView textView;

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mProgressBar = (ProgressBarView) findViewById(R.id.horizontal_progress_view);

        textView = (TextView) findViewById(R.id.progress_tv);
        button = (Button) findViewById(R.id.startAnimationBtn);


        mProgressBar.setProgressWithAnimation(80).setProgressListener(new ProgressListener() {
            @Override
            public void currentProgressListener(float currentProgress) {
            }
        });
        mProgressBar.startProgressAnimation();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setProgressWithAnimation(100);

            }
        });
    }

}