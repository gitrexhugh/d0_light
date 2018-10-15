package com.example.rex_h.d0_light;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;

public class main_activity extends AppCompatActivity {
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load_back_light();
    }


    private void load_back_light(){
        setContentView(R.layout.back_light);
        image_menu();//執行選單宣告
        //以下宣告燈光按鈕
        ibtn_power=(ImageView)findViewById(R.id.btn_power);
        ibtn_power.setOnClickListener(ibtn_power_click);// Power
        lightOn();
    }

    private void lightOn(){
        show_status=(TextView)findViewById(R.id.show_status);
        str_status="light_On";
        show_status.setText(str_status+"|status:"+light_state);
        ibtn_power.setImageResource(R.mipmap.light_xxxhdpi);
        //呼叫動畫控制程式- 暫不執行
        // anim_control(light_state);

        //以下為Camera Manager相關，不適用模擬器
        CameraManager mCamera = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try{
            String cameraID=mCamera.getCameraIdList()[0];
            mCamera.setTorchMode(cameraID,true);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }
    private void lightOff(){
        str_status="light_Off";

        ibtn_power.setImageResource(R.mipmap.light_xxxhdpi_0);
        // anim_control(light_state); //呼叫動畫控制程式- 暫不執行

        //以下為Camera Manager相關，不適用模擬器
        CameraManager mCamera = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try{
            String cameraID=mCamera.getCameraIdList()[0];
            mCamera.setTorchMode(cameraID,false);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

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
            lightOff();
            Intent intent=new Intent();
            intent.setClass(main_activity.this,screen_light.class);
            startActivity(intent);
            finish();
        }
    };

    private View.OnClickListener ibtn_screen_light_camera_Click= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            lightOff();
            Intent intent=new Intent();
            intent.setClass(main_activity.this,screen_light_camera.class);
            startActivity(intent);
            finish();

        }
    };

    private View.OnClickListener ibtn_back_light_click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*Intent intent=new Intent();
            intent.setClass(main_activity.this,main_activity.class);
            startActivity(intent);*/

        }
    };
    private View.OnClickListener ibtn_back_light_camera_click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent();
            intent.setClass(main_activity.this,back_light_camera.class);
            startActivity(intent);
            finish();

        }
    };

    private View.OnClickListener ibtn_power_click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (light_state==1){
                light_state=4;
                lightOff();
            }else {
                light_state=1;
                lightOn();
            }
        }
    };
    // 關閉程式方法
    public boolean onKeyDown (int keyCode, KeyEvent event){
        if (keyCode== KeyEvent.KEYCODE_BACK){
            AlertDialog isExit=new AlertDialog.Builder(this)
                    .setTitle(R.string.is_exit_title)
                    .setMessage(R.string.is_exit_msg)
                    .setPositiveButton(R.string.is_exit_yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setNegativeButton(R.string.is_exit_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setCancelable(false)
                    .show();

        }
        return false;
    }

}

