package com.example.rex_h.d0_light;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraMetadata;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import android.provider.Settings;
import java.text.SimpleDateFormat;

public class back_light_camera extends AppCompatActivity {
    private int light_state; //0:light_off; 1: light_on
    //以image view宣告圖形按鈕
    private ImageView ibtn_screen_light;
    private ImageView ibtn_screen_light_camera;
    private ImageView ibtn_back_light;
    private ImageView ibtn_back_light_camera;
    private ImageView ibtn_power;
    private TextView show_status;
    private String str_status;
    //以下為照相機功能宣告
    private TextureView mTextureView;
    private Button btn_shot;
    private SurfaceHolder mSurfaceHolder;
    private CameraManager mCameraManager;
    private Handler mHandler;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private String mCameraId;
    protected CameraDevice mCameraDevice;
    protected CameraCaptureSession mcameraCaptureSession;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private ImageReader mImageReader;
    private Size imageDimension;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSuppoerted;
    private static final String TAG = "Camera2API";
    private CameraManager.TorchCallback Torchcall;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load_back_light_camera();
    }

    private void load_back_light_camera(){
        setContentView(R.layout.back_light_camera);
        image_menu();//執行選單宣告
        mCameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);

        mTextureView = (TextureView) findViewById(R.id.preview);
        assert mTextureView != null;
        mTextureView.setSurfaceTextureListener(textureListener);
        btn_shot = (Button) findViewById(R.id.shoot);
        assert btn_shot != null;

        btn_shot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
           //lightOn();
           openCamera();

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.e(TAG, "onOpened");
            mCameraDevice = camera;
            createCameraPreview();

        }


        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            mCameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    };

    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Toast.makeText(back_light_camera.this, "Saved" + file, Toast.LENGTH_SHORT).show();
            createCameraPreview();
        }
    };

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void takePicture() {
        SimpleDateFormat timeStamp=new SimpleDateFormat("yyyyMMdd-hhmmss");
        Date cur=new Date(System.currentTimeMillis());
        String file_time=timeStamp.format(cur);
        file_time=file_time+".jpg";
        if (null == mCameraDevice) {
            Log.e(TAG, "Camera Device is Null");
            return;
        }

        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraDevice.getId());
            int width=900;
            int height=1600;

            //Size[] jpegSizes = null;
            if (characteristics != null) {
                //jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                Size[] jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getHighResolutionOutputSizes(ImageFormat.JPEG);// 取得高解析度輸出尺寸
                if (jpegSizes != null && 0 < jpegSizes.length) {
                    width = jpegSizes[0].getWidth();
                    height = jpegSizes[0].getHeight();
                }else{
                    width=900;
                    height=1600;
                }

            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(mTextureView.getSurfaceTexture()));
           final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            captureBuilder.set(CaptureRequest.FLASH_MODE,CameraMetadata.FLASH_MODE_TORCH);


//            captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //captureRequestBuilder.addTarget(reader.getSurface());
//            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);


            //照片Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            //設定存放路徑
            File path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            final File file = new File(path,  file_time);
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }

                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(back_light_camera.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
                    createCameraPreview();
                }
            };
            mCameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                        //session.capture(captureRequestBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);

            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureBuilder.addTarget(surface);
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            captureBuilder.set(CaptureRequest.FLASH_MODE,CameraMetadata.FLASH_MODE_TORCH);

/*            captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.set(CaptureRequest.FLASH_MODE,CameraMetadata.FLASH_MODE_TORCH);
            captureRequestBuilder.addTarget(surface);
*/
            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == mCameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    mcameraCaptureSession = cameraCaptureSession;
                    updatePreview(captureBuilder);
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(back_light_camera.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {

        Log.e(TAG, "is camera open");
        try {
            final String mCameraId=mCameraManager.getCameraIdList()[0];
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(back_light_camera.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }

            mCameraManager.openCamera(mCameraId, stateCallback, new Handler());

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    protected void updatePreview(CaptureRequest.Builder captureBuilder) {
        if (null == mCameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        //captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        //captureRequestBuilder.set(CaptureRequest.FLASH_MODE,CameraMetadata.FLASH_MODE_TORCH);
        try {
            mcameraCaptureSession.setRepeatingRequest(captureBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (null != mImageReader) {
            mImageReader.close();
            mImageReader = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(back_light_camera.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        startBackgroundThread();
        if (mTextureView.isAvailable()) {
            //openCamera();
        } else {
            mTextureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void lightOff(){
        str_status="light_Off";

        //以下為Camera Manager相關，不適用模擬器
        //CameraManager mCameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try{
            String cameraID=mCameraManager.getCameraIdList()[0];
            mCameraManager.setTorchMode(cameraID,false);
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
            intent.setClass(back_light_camera.this,screen_light.class);
            startActivity(intent);
            finish();
        }
    };

    private View.OnClickListener ibtn_screen_light_camera_Click= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            lightOff();
            Intent intent=new Intent();
            intent.setClass(back_light_camera.this,screen_light_camera.class);
            startActivity(intent);
            finish();

        }
    };

    private View.OnClickListener ibtn_back_light_click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent();
            intent.setClass(back_light_camera.this,main_activity.class);
            startActivity(intent);
            finish();

        }
    };
    private View.OnClickListener ibtn_back_light_camera_click=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*Intent intent=new Intent();
            intent.setClass(main_activity.this,back_light_camera.class);
            startActivity(intent);*/

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
