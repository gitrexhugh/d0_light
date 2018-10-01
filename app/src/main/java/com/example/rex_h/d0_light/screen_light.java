package com.example.rex_h.d0_light;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class screen_light extends AppCompatActivity{
    private int function;// 1: back_light; 2: back_light_camera; 3: screen_light_camera; 4: screen_light
    private int light_state; //0:light_off; 1: light_on
    //以image view宣告圖形按鈕
    private ImageView ibtn_screen_light;
    private ImageView ibtn_screen_light_camera;
    private ImageView ibtn_back_light;
    private ImageView ibtn_back_light_camera;
    private ImageView ibtn_power;
    private TextView show_status;
    private String str_status;
    SeekBar sk_R;
    SeekBar sk_G;
    SeekBar sk_B;
    TextView show_text;
    int cR=255;
    int cG=255;
    int cB=255;
    int sk_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load_screen_light();
    }

    private void load_screen_light(){
        setContentView(R.layout.screen_light);
        image_menu();//執行選單宣告
        //以下宣告ROG seekBar
        sk_R=(SeekBar)findViewById(R.id.seekR);
        sk_G=(SeekBar)findViewById(R.id.seekG);
        sk_B=(SeekBar)findViewById(R.id.seekB);
        show_text=(TextView)findViewById(R.id.show_color);
        //screen_bg.setBackgroundColor(Color.argb(255,cR,cG,cB));
        show_text.setBackgroundColor(Color.argb(255,cR,cG,cB));
        show_text.setText("status:"+light_state+";"+String.format("%02x", cR)+String.format("%02x", cG)+String.format("%02x", cB));
        sk_R.setOnSeekBarChangeListener(seekbartracking);
        sk_G.setOnSeekBarChangeListener(seekbartracking);
        sk_B.setOnSeekBarChangeListener(seekbartracking);

    }


    //Screen Light RGB seekbar
    private SeekBar.OnSeekBarChangeListener seekbartracking=new SeekBar.OnSeekBarChangeListener(){

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            sk_id=seekBar.getId();
            switch (sk_id){
                case R.id.seekR:
                    cR=sk_R.getProgress();
                    //cR=progress;
                    break;

                case R.id.seekG:
                    cG=sk_G.getProgress();
                    break;

                case R.id.seekB:
                    cB=sk_B.getProgress();
                    break;

            }
            show_text.setBackgroundColor(Color.argb(255,cR,cG,cB));
            //show_text.setText("status:"+light_state+";"+String.format("%02x", cR)+String.format("%02x", cG)+String.format("%02x", cB));

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    //宣告功能選單
    private void image_menu(){
        ibtn_back_light=(ImageView) findViewById(R.id.btn_back_light);
        ibtn_back_light_camera=(ImageView) findViewById(R.id.btn_back_light_camera);
        ibtn_screen_light=(ImageView)findViewById(R.id.btn_screen_light);
        ibtn_screen_light_camera=(ImageView)findViewById(R.id.btn_screen_light_camera);
        //以下宣告按鈕，Listener內容另外寫
        ibtn_back_light.setOnClickListener(ibtn_back_light_click);// Back light
        ibtn_back_light_camera.setOnClickListener(ibtn_back_light_camera_click);// Back light with Camera
        ibtn_screen_light.setOnClickListener(ibtn_screen_light_Click);// Screen Light
        ibtn_screen_light_camera.setOnClickListener(ibtn_screen_light_camera_Click);// Back light with Camera
    }

    //以下為主功能的各項選單對應程式
    private View.OnClickListener ibtn_screen_light_Click= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           /* lightOff();
            Intent intent=new Intent();
            intent.setClass(main_activity.this,screen_light.class);
            startActivity(intent);*/
        }
    };

    private View.OnClickListener ibtn_screen_light_camera_Click= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           /* lightOff();
            Intent intent=new Intent();
            intent.setClass(main_activity.this,screen_light_camera.class);
            startActivity(intent);*/

        }
    };

    private View.OnClickListener ibtn_back_light_click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent();
            intent.setClass(screen_light.this,main_activity.class);
            startActivity(intent);

        }
    };
    private View.OnClickListener ibtn_back_light_camera_click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent();
            intent.setClass(screen_light.this,back_light_camera.class);
            startActivity(intent);

        }
    };


}
