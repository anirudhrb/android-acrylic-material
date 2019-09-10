package com.anirudhrb.acrylicmaterialdemo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import bolts.Continuation;
import bolts.Task;
import com.anirudhrb.acrylicmaterial.AcrylicMaterial;

import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Task.callInBackground(new Callable<Drawable>() {
            @Override
            public Drawable call() {
                return AcrylicMaterial.with(MainActivity.this)
                        .background(R.drawable.background_image)
                        .useDefaults()
                        .generate();
            }
        }).onSuccess(new Continuation<Drawable, Void>() {
            @Override
            public Void then(Task<Drawable> task) {
                Drawable drawable = task.getResult();
                if (drawable == null) {
                    return null;
                }

                final ImageView background = findViewById(R.id.background);
                background.setImageDrawable(drawable);

                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }
}
