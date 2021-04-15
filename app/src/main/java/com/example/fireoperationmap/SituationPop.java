package com.example.fireoperationmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SituationPop extends Activity {
    private TextView Building_total, Building_sale, Building_food, Building_multiple, Building_house, Building_the_other;
    private TextView Arcade_total, Arcade_sale, Arcade_food, Arcade_multiple, Arcade_building, Arcade_the_other;
    private Button finish_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.building_status);

        Building_total = (TextView) findViewById(R.id.Building_total);
        Building_sale = (TextView) findViewById(R.id.Building_sale);
        Building_food = (TextView) findViewById(R.id.Building_food);
        Building_multiple = (TextView) findViewById(R.id.Building_multiple);
        Building_house = (TextView) findViewById(R.id.Building_house);
        Building_the_other = (TextView)findViewById(R.id.Building_the_other);

        Arcade_total = (TextView) findViewById(R.id.Arcade_total);
        Arcade_sale = (TextView) findViewById(R.id.Arcade_sale);
        Arcade_food = (TextView) findViewById(R.id.Arcade_food);
        Arcade_multiple = (TextView) findViewById(R.id.Arcade_multiple);
        Arcade_building = (TextView) findViewById(R.id.Arcade_building);
        Arcade_the_other = (TextView)findViewById(R.id.Arcade_the_other);

        finish_btn = (Button) findViewById(R.id.finish_button);

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
