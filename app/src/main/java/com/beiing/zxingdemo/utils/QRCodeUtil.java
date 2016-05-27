package com.beiing.zxingdemo.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenliu on 2016/5/12.<br/>
 * 描述：二维码生成工具类
 * </br>
 */
public class QRCodeUtil {


    /**
     * 生成普通的二维码
     * @param content
     * @param widthPix
     * @param heightPix
     * @return
     */
    public static Bitmap createQRCode(String content, int widthPix, int heightPix){
        return createQRCodeWithLogo(content, widthPix, heightPix, null);
    }


    /**
     * 生成带logo的二维码
     * @param content   内容
     * @param widthPix  图片宽度
     * @param heightPix 图片高度
     * @param logoBm    二维码中心的Logo图标（可以为null）
     * @return
     */
    public static Bitmap createQRCodeWithLogo(String content, int widthPix, int heightPix, Bitmap logoBm){
        Bitmap qrcode = null;
        try{
            if (!TextUtils.isEmpty(content)) {
                //配置参数
                Map<EncodeHintType, Object> hints = new HashMap<>();
                hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
                //容错级别
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
                //设置空白边距的宽度
//            hints.put(EncodeHintType.MARGIN, 2); //default is 4

                // 图像数据转换，使用了矩阵转换
                BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
                int[] pixels = new int[widthPix * heightPix];
                // 下面这里按照二维码的算法，逐个生成二维码的图片，
                // 两个for循环是图片横列扫描的结果
                for (int y = 0; y < heightPix; y++) {
                    for (int x = 0; x < widthPix; x++) {
                        if (bitMatrix.get(x, y)) {
                            pixels[y * widthPix + x] = 0xff000000;
                        } else {
                            pixels[y * widthPix + x] = 0xffffffff;
                        }
                    }
                }

                // 生成二维码图片的格式，使用ARGB_8888
                qrcode = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
                qrcode.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);

                if (logoBm != null) {
                    qrcode = addLogo(qrcode, logoBm);
                }
            }
        } catch(Exception ep){
            ep.printStackTrace();
            qrcode = null;
        }
        return qrcode;
    }

    /**
     * 在二维码中间添加Logo图案
     * @param qrcode 原二维码
     * @param logo 要添加的logo
     * @return
     */
    public static Bitmap addLogo(Bitmap qrcode, Bitmap logo) {
        if (qrcode == null) {
            return null;
        }

        if (logo == null) {
            return qrcode;
        }

        //获取图片的宽高
        int srcWidth = qrcode.getWidth();
        int srcHeight = qrcode.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return qrcode;
        }

        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(qrcode, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

}