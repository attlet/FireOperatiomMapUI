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

public class Arcadepop extends Activity {
    private TextView enter_num, address, detail_info;
    private ImageView imageView;
    private Button finish_btn;
    private int[] arcadepin_name = {R.drawable.arcadepin1, R.drawable.arcadepin2, R.drawable.arcadepin3, R.drawable.arcadepin4,
            R.drawable.arcadepin5, R.drawable.arcadepin6, R.drawable.arcadepin7, R.drawable.arcadepin8}; //사진 다 바꿔야 함

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.arcade);

        enter_num = (TextView) findViewById(R.id.enter_num);
        address = (TextView) findViewById(R.id.address);
        detail_info = (TextView) findViewById(R.id.detail_info);
        imageView = (ImageView) findViewById(R.id.arcadeimg);
        finish_btn = (Button)findViewById(R.id.finish_button);

        Intent intent = getIntent();
        enter_num.setText("옥상입구 번호: " + intent.getStringExtra("Enter_num"));
        address.setText("주소: " + intent.getStringExtra("Address"));
        detail_info.setText("세부사항: " + intent.getStringExtra("Detail_info"));

        imageView.setImageResource(arcadepin_name[Integer.parseInt(intent.getStringExtra("Enter_num"))] - 1);

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
