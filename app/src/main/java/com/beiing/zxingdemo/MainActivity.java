package com.beiing.zxingdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.beiing.zxingdemo.utils.IsChineseOrNot;
import com.beiing.zxingdemo.utils.QRCodeUtil;
import com.beiing.zxingdemo.utils.StringUtil;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.UnsupportedEncodingException;

import zxing.activity.CaptureActivity;
import zxing.decoding.Intents;
import zxing.encoding.EncodingHandler;

public class MainActivity extends AppCompatActivity {
    Button createQrBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //内容
        final EditText contentET = (EditText) findViewById(R.id.et_create_qr_content);
        //显示二维码图片
        final ImageView imageView = (ImageView) findViewById(R.id.iv_create_qr);
        //是否添加Logo
        final CheckBox addLogoCB = (CheckBox) findViewById(R.id.cb_create_qr_addLogo);

        createQrBtn = (Button) findViewById(R.id.btn_create_qr);
        createQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
                new Thread(new Runnable() {
                    @Override
                    public void run() {

//                        final Bitmap bm = QRCodeUtil.createQRCode(contentET.getText().toString().trim(), 400, 400);
//                        runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    imageView.setImageBitmap(bm);
//                                }
//                            });

                        try {

                            String content = contentET.getText().toString().trim();
//                            String utfStr = new String(content.getBytes("utf-8"));

//                            Log.e("===", "origin utfStr:" + utfStr);
                            final Bitmap bm =  EncodingHandler.createQRCode(content, 400);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(bm);
                                }
                            });
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }


                    }
                }).start();
            }
        });
    }



    /**
     * 扫一扫
     * @param view
     */
    public void onScan(View view) {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, 100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==RESULT_OK){
            if(requestCode == 100){
                String result = data.getStringExtra(Intents.Scan.RESULT);
                Log.e("====", "result:" + result);

                try {
                    Log.e("===", "unicode result:" + new String(result.getBytes("utf-8")));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}







