package org.itzheng.opencv3;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.IOException;

public class MainActivity extends BaseActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }

    ImageView ivBefore;
    ImageView ivAfter;
    Button btnProcess;
    /**
     * 原始图片
     */
    String src = "";
    /**
     * 转换后的图片
     */
    String desc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        if (OpenCVLoader.initDebug()) {//OpenCV java 是否可用
            tv.setText("OpenCVLoader init");
        }
        ivBefore = findViewById(R.id.ivBefore);
        ivBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermissions(NEEDED_PERMISSIONS)) {
                    ActivityCompat.requestPermissions(MainActivity.this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
                } else {
                    pickImage();
                }

            }
        });
        ivAfter = findViewById(R.id.ivAfter);
        btnProcess = findViewById(R.id.btnProcess);
        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desc = getApplication().getExternalCacheDir().getAbsolutePath() + "/" + "opencv" + "/gray.jpg";
                Log.w(TAG, "desc: " + desc);
                File file = new File(desc);
                File dir = file.getParentFile();
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                if (file.exists()) {
                    file.delete();
                }
                openCVToGray(src, desc);
                showImage(desc, ivAfter);
            }
        });
    }

    private void pickImage() {
        //选择图片
        Intent intent = new Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    private static final String TAG = "MainActivity";
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    /**
     * 所需的所有权限信息
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 选取图片的返回值
        if (requestCode == 1) {
            //
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                src = picturePath;
                showImage(src, ivBefore);
            }
        }
    }

    /**
     * 显示图片
     *
     * @param src
     * @param imageView
     */
    private void showImage(String src, ImageView imageView) {
        if (imageView == null) {
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(src);
        imageView.setImageBitmap(bitmap);
    }

    private native void openCVToGray(String src, String desc);


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public void afterRequestPermission(int requestCode, boolean isAllGranted) {
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            if (isAllGranted) {
                pickImage();
            } else {
                showToast("jjie");
            }
        }
    }
}
