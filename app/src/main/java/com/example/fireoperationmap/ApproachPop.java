package com.example.fireoperationmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ApproachPop extends Activity {
    private TextView name, address;
    private ImageView imageView;
    private Button finish_btn;

    private int[] approach_switch = {R.drawable.approach_image_a, R.drawable.approach_image_b, R.drawable.approach_image_c, R.drawable.approach_image_d, R.drawable.approach_image_e,
            R.drawable.approach_image_f, R.drawable.approach_image_g, R.drawable.approach_image_h, R.drawable.approach_image_i, R.drawable.approach_image_j, R.drawable.approach_image_k};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.approach);

        name = (TextView) findViewById(R.id.name);
        address = (TextView) findViewById(R.id.address);
        imageView = (ImageView) findViewById(R.id.approachImg);
        finish_btn = (Button) findViewById(R.id.finish_button);

        Intent intent = getIntent();
        name.setText("○진입로_" + intent.getStringExtra("Name"));
        address.setText("○건물주소: 청주시  " + intent.getStringExtra("Address"));

        imageView.setImageResource(approach_switch[Integer.parseInt(intent.getStringExtra("Num"))]);

    }
    public void mOnClose(View v){
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
}