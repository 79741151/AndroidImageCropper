package com.theartofdev.edmodo.cropper.test;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @创建者 魏震
 * @创建时间 2020/9/17 16:16
 * @描述 ...........
 */
public class BitmapUtil {

    /**
     * 通过资源id转化成Bitmap
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitmapById(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 缩放图片
     *
     * @param bm        要缩放图片
     * @param newWidth  宽度
     * @param newHeight 高度
     * @return处理后的图片
     */
    public static Bitmap scaleImage(Bitmap bm, int newWidth, int newHeight) {
        if (bm == null) {
            return null;
        }
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        if (bm != null & !bm.isRecycled()) {
            bm.recycle();//销毁原图片
            bm = null;
        }
        return newbm;
    }

    /**
     * 按照一定的宽高比例裁剪图片
     *
     * @param bitmap     要裁剪的图片
     * @param num1       长边的比例
     * @param num2       短边的比例
     * @param isRecycled 是否回收原图片
     * @return 裁剪后的图片
     */
    public static Bitmap imageCrop(Bitmap bitmap, int num1, int num2, boolean isRecycled) {
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int retX, retY;
        int nw, nh;
        if (w > h) {
            if (h > w * num2 / num1) {
                nw = w;
                nh = w * num2 / num1;
                retX = 0;
                retY = (h - nh) / 2;
            } else {
                nw = h * num1 / num2;
                nh = h;
                retX = (w - nw) / 2;
                retY = 0;
            }
        } else {
            if (w > h * num2 / num1) {
                nh = h;
                nw = h * num2 / num1;
                retY = 0;
                retX = (w - nw) / 2;
            } else {
                nh = w * num1 / num2;
                nw = w;
                retY = (h - nh) / 2;
                retX = 0;
            }
        }
        Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null, false);
        if (isRecycled && bitmap != null && !bitmap.equals(bmp) && !bitmap.isRecycled()) {
            bitmap.recycle();//回收原图片
            bitmap = null;
        }
        return bmp;
    }

    /**
     * 图片转圆角
     *
     * @param bitmap 要转的bitmap
     * @param pixels 转圆角的弧度
     * @return 转圆角的bitmap
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return output;
    }

    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return output;
    }

    /**
     * 旋转图片
     *
     * @param angle  旋转角度
     * @param bitmap 要处理的Bitmap
     * @return 处理后的Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (resizedBitmap != bitmap && bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return resizedBitmap;
    }

    /**
     * 按宽/高缩放图片到指定大小并进行裁剪得到中间部分图片（进行缩放）
     *
     * @param bitmap 源bitmap
     * @param w      缩放后指定的宽度
     * @param h      缩放后指定的高度
     * @return 缩放后的中间部分图片 Bitmap
     */
    public static Bitmap zoomBitmap1(Bitmap bitmap, float w, float h) {
        float width = bitmap.getWidth();//获得图片宽高
        float height = bitmap.getHeight();
        Log.i("cropper", "zoomBitmap---" + "width:" + width + "---" + "height:" + height);
        float scaleWidht, scaleHeight, x, y;//图片缩放倍数以及x，y轴平移位置
        Bitmap newbmp = null; //新的图片
        Matrix matrix = new Matrix();//变换矩阵
        if ((width / height) <= w / h) {//当宽高比大于所需要尺寸的宽高比时以宽的倍数为缩放倍数
            scaleWidht = w / width;
            scaleHeight = scaleWidht;
            y = ((height * scaleHeight - h) / 2) / scaleHeight;// 获取bitmap源文件中y做表需要偏移的像数大小
            x = 0;
        } else {
            scaleWidht = h / height;
            scaleHeight = scaleWidht;
            x = 140;//((width * scaleWidht - w) / 2) / scaleWidht;// 获取bitmap源文件中x做表需要偏移的像数大小
            y = 0;
        }
        matrix.postScale(scaleWidht / 1f, scaleHeight / 1f);
        try {
            if (width - x > 0 && height - y > 0 && bitmap != null)//获得新的图片 （原图，x轴起始位置，y轴起始位置，x轴结束位置，Y轴结束位置，缩放矩阵，是否过滤原图）为防止报错取绝对值
                newbmp = scaleImage(Bitmap.createBitmap(bitmap, (int) Math.abs(x), (int) Math.abs(y), (int) Math.abs(width - x * 2) - 140,
                        (int) Math.abs(height - y * 2), matrix, false), (int) w, (int) h);// createBitmap()方法中定义的参数x+width要小于或等于bitmap.getWidth()，y+height要小于或等于bitmap.getHeight()
        } catch (Exception e) {//如果报错则返回原图，不至于为空白
            e.printStackTrace();
            return bitmap;
        }
        return newbmp;

    }

    /**
     * * 按宽/高缩放图片到指定大小并进行裁剪得到中间部分图片   (不进行缩放)
     * * @param bitmap 源bitmap
     * * @param w 缩放后指定的宽度
     * * @param h 缩放后指定的高度
     * * @return  缩放后的中间部分图片 Bitmap
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, float w, float h) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.i("cropper", "zoomBitmap---" + "width:" + width + "---" + "height:" + height);
        float scaleWidht, scaleHeight, x, y;
        Bitmap newbmp;
        Matrix matrix = new Matrix();
        if (width > height) {
            scaleWidht = ((float) h / height);
            scaleHeight = ((float) h / height);
            x = ((width - w * height / h) / 2) - 100;// 获取bitmap源文件中x做表需要偏移的像数大小
            y = 0;
        } else if (width < height) {
            scaleWidht = ((float) w / width);
            scaleHeight = ((float) w / width);
            x = 0;
            y = (height - h * width / w) / 2;// 获取bitmap源文件中y做表需要偏移的像数大小
        } else {
            scaleWidht = ((float) w / width);
            scaleHeight = ((float) w / width);
            x = 0;
            y = 0;
        }
        matrix.postScale(scaleWidht, scaleHeight);
        try {
            ////获得新的图片 （原图，x轴起始位置，y轴起始位置，x轴结束位置，Y轴结束位置，缩放矩阵，是否过滤原图）为防止报错取绝对值
            newbmp = scaleImage(Bitmap.createBitmap(bitmap, (int) x, (int) y, (int) (width - x * 2) - 100, (int) (height - y * 2), matrix, true), (int) w, (int) h);// createBitmap()方法中定义的参数x+width要小于或等于bitmap.getWidth()，y+height要小于或等于bitmap.getHeight()
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return newbmp;
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    /**
     *Uri 转 Bitmap
     *
     * 图片太大会报OOM 报错
     * @param context
     * @param uri
     * @return
     */
    private Bitmap getBitmapFromUri(Context context ,Uri uri) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            Log.e("[Android]", e.getMessage());
            Log.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读取一个缩放后的图片，限定图片大小，避免OOM
     *
     * 默认是用“Bitmap.Config.RGB_565”，可以占用比较少的内存。
     * 如果需要显示高清图片，建议使用“Bitmap.Config.RGB_8888”。
     * @param uri       图片uri，支持“file://”、“content://”
    * @param maxWidth  最大允许宽度
     * @param maxHeight 最大允许高度
     * @return  返回一个缩放后的Bitmap，失败则返回null
     */
    public static Bitmap decodeUri(Context context, Uri uri, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //只读取图片尺寸
        resolveUri(context, uri, options);

        //计算实际缩放比例
        int scale = 1;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if ((options.outWidth / scale > maxWidth &&
                    options.outWidth / scale > maxWidth * 1.4) ||
                    (options.outHeight / scale > maxHeight &&
                            options.outHeight / scale > maxHeight * 1.4)) {
                scale++;
            } else {
                break;
            }
        }

        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;//读取图片内容
        options.inPreferredConfig = Bitmap.Config.RGB_565; //根据情况进行修改
        Bitmap bitmap = null;
        try {
            bitmap = resolveUriForBitmap(context, uri, options);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        //return compressImage(bitmap);
        return bitmap;
    }


    private static void resolveUri(Context context, Uri uri, BitmapFactory.Options options) {
        if (uri == null) {
            return;
        }

        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme) ||
                ContentResolver.SCHEME_FILE.equals(scheme)) {
            InputStream stream = null;
            try {
                stream = context.getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(stream, null, options);
            } catch (Exception e) {
                Log.w("resolveUri", "Unable to open content: " + uri, e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.w("resolveUri", "Unable to close content: " + uri, e);
                    }
                }
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            Log.w("resolveUri", "Unable to close content: " + uri);
        } else {
            Log.w("resolveUri", "Unable to close content: " + uri);
        }
    }

    private static Bitmap resolveUriForBitmap(Context context, Uri uri, BitmapFactory.Options options) {
        if (uri == null) {
            return null;
        }

        Bitmap bitmap = null;
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme) ||
                ContentResolver.SCHEME_FILE.equals(scheme)) {
            InputStream stream = null;
            try {
                stream = context.getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(stream, null, options);
            } catch (Exception e) {
                Log.w("resolveUriForBitmap", "Unable to open content: " + uri, e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.w("resolveUriForBitmap", "Unable to close content: " + uri, e);
                    }
                }
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            Log.w("resolveUriForBitmap", "Unable to close content: " + uri);
        } else {
            Log.w("resolveUriForBitmap", "Unable to close content: " + uri);
        }
        return bitmap;
    }

}
