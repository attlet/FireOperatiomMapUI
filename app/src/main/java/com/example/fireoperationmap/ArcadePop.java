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

public class ArcadePop extends Activity {
    private TextView enter_num, address, detail_info;
    private ImageView imageView, imageView2;
    private Button finish_btn;
    private int[] arcadepin_name = {R.drawable.arcadepin1, R.drawable.arcadepin2, R.drawable.arcadepin3, R.drawable.arcadepin4,
            R.drawable.arcadepin5, R.drawable.arcadepin6, R.drawable.arcadepin7, R.drawable.arcadepin8}; //사진 다 바꿔야 함

    private int[] arcade_switch = {R.drawable.arcade_entrance1, R.drawable.arcade_entrance2, R.drawable.arcade_entrance3, R.drawable.arcade_entrance4,
            R.drawable.arcade_entrance5, R.drawable.arcade_entrance6, R.drawable.arcade_entrance7, R.drawable.arcade_entrance8};

    private int[] arcade_switch2 = {R.drawable.arcade_door1, R.drawable.arcade_door2, R.drawable.arcade_door3, R.drawable.arcade_door4, R.drawable.arcade_door5,
            R.drawable.arcade_door6, R.drawable.arcade_door7, R.drawable.arcade_door8};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.arcade);

        enter_num = (TextView) findViewById(R.id.enter_num);
        address = (TextView) findViewById(R.id.address);
        detail_info = (TextView) findViewById(R.id.detail_info);
        imageView = (ImageView) findViewById(R.id.arcadeimg);
        imageView2 = (ImageView) findViewById(R.id.arcadeimg2);
        finish_btn = (Button)findViewById(R.id.finish_button);

        Intent intent = getIntent();
        enter_num.setText("○옥상번호: " + intent.getStringExtra("Enter_num"));
        address.setText("○건물주소: 청주시  " + intent.getStringExtra("Address"));
        detail_info.setText("○세부사항: " + intent.getStringExtra("Detail_info"));

        imageView.setImageResource(arcade_switch[Integer.parseInt(intent.getStringExtra("Enter_num")) - 1]);
        imageView2.setImageResource(arcade_switch2[Integer.parseInt(intent.getStringExtra("Enter_num")) - 1]);

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
