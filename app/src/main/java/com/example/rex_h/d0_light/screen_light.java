package com.example.rex_h.d0_light;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
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
        //隱藏狀態列
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        load_screen_light();
    }

    private void load_screen_light(){
        setContentView(R.layout.screen_light);
        image_menu();//執行選單宣告
        setBrightness(this,255);
        //以下宣告ROG seekBar
        sk_R=(SeekBar)findViewById(R.id.seekR);
        sk_G=(SeekBar)findViewById(R.id.seekG);
        sk_B=(SeekBar)findViewById(R.id.seekB);
        show_text=(TextView)findViewById(R.id.show_color);
        show_text.setBackgroundColor(Color.argb(255,cR,cG,cB));
        //show_text.setText("status:"+light_state+";"+String.format("%02x", cR)+String.format("%02x", cG)+String.format("%02x", cB));
        sk_R.setOnSeekBarChangeListener(seekbartracking);
        sk_G.setOnSeekBarChangeListener(seekbartracking);
        sk_B.setOnSeekBarChangeListener(seekbartracking);

    }
    /**
     * 判斷是否開啟了自動亮度調節
     */
   /* public static boolean isAutoBrightness(Context context) {
        ContentResolver resolver = context.getContentResolver();
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(resolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }*/
    /**
     * 獲取螢幕的亮度
     */
    /*public static int getScreenBrightness(Context context) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = context.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }*/
    /**
     * 設定當前Activity顯示時的亮度
     * 螢幕亮度最大數值一般為255，各款手機有所不同
     * screenBrightness 的取值範圍在[0,1]之間
     */
    public static void setBrightness(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        activity.getWindow().setAttributes(lp);
    }
    /**
     * 開啟關閉自動亮度調節
     */
    public static boolean autoBrightness(Context activity, boolean flag) {
        int value = 0;
        if (flag) {
            value = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC; //開啟
        } else {
            value = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;//關閉
        }
        return Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                value);
    }
    /**
     * 儲存亮度設定狀態，退出app也能保持設定狀態
     */
    public static void saveBrightness(Context context, int brightness) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = android.provider.Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        android.provider.Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        resolver.notifyChange(uri, null);
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
            Intent intent=new Intent();
            intent.setClass(screen_light.this,screen_light_camera.class);
            startActivity(intent);
            finish();

        }
    };

    private View.OnClickListener ibtn_back_light_click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent();
            intent.setClass(screen_light.this,main_activity.class);
            startActivity(intent);
            finish();

        }
    };
    private View.OnClickListener ibtn_back_light_camera_click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent();
            intent.setClass(screen_light.this,back_light_camera.class);
            startActivity(intent);
            finish();

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
