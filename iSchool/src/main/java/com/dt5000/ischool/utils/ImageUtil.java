package com.dt5000.ischool.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.dt5000.ischool.activity.PlayVideoActivity3;

import retrofit2.http.Url;

/**
 * 图片处理辅助类
 *
 * @author 周锋
 * @date 2016年4月21日 上午10:50:48
 * @ClassInfo com.dt5000.ischool.utils.ImageUtil
 * @Description
 */
public class ImageUtil {

    /**
     * 从图片文件读取Bitmap并缩放到指定尺寸，使用ThumbnailUtils来缩放
     *
     * @param filePath 图片文件路径
     * @param width    指定宽度
     * @param height   指定高度
     * @return
     */
    public static Bitmap decodeBitmapToFixSize(String filePath, int width,
                                               int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        int realHeight = options.outHeight;
        int realWidth = options.outWidth;
        int scaleAccordWidth = realWidth / width;
        int scaleAccordHeight = realHeight / height;
        int scale = 1;
        if (scaleAccordWidth < scaleAccordHeight) {
            scale = scaleAccordWidth;
        } else {
            scale = scaleAccordHeight;
        }
        if (scale <= 0) {
            scale = 1;
        }

        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(filePath, options);

        return ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    }

    /**
     * 根据当前时间命名图片文件
     *
     * @return
     */
    public static String getPhotoFileNameWithCurrentTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss", Locale.CHINA);
        return dateFormat.format(date) + ".jpg";
    }

    /**
     * 将Bitmap缩放到指定尺寸，比使用inSampleSize（采样率）来缩放占内存，但是尺寸可以精确
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    /**
     * 将Bitmap图像保存到系统相册
     *
     * @param bmp
     * @return
     */
    public static File saveBitmapToDCIM(Bitmap bmp) {
        File dcimDir = new File(Environment.getExternalStorageDirectory(),
                "/DCIM/Camera/");
        if (!dcimDir.exists()) {
            dcimDir.mkdir();
        }

        File savefile = new File(dcimDir, getPhotoFileNameWithCurrentTime());

        try {
            FileOutputStream fos = new FileOutputStream(savefile);
            bmp.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return savefile;
    }

    /**
     * 将Bitmap图像保存到系统相册
     *
     * @param bmp
     * @return
     */
    public static File saveBitmapToDCIM(Context context, Bitmap bmp, String filePath) {
        File savefile = new File(context.getCacheDir(),
                filePath.substring(0, filePath.lastIndexOf(".")) + ".jpg");

        try {
            FileOutputStream fos = new FileOutputStream(savefile);
            bmp.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return savefile;
    }

    public static Bitmap getBitmapFromDCIM(Context context, String filePath) {
        String fullPath = context.getCacheDir() + "/" + filePath.substring(0, filePath.lastIndexOf(".")) + ".jpg";
        return decodeBitmapWithInSampleSize(fullPath, 300);
        //return BitmapFactory.decodeFile(fullPath);
    }

    /**
     * 从本地资源图片中读取Bitmap
     *
     * @param resId
     * @param context
     * @return
     */
    public Bitmap decodeBitmapFromResource(int resId, Context context) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 从图片文件读取Bitmap并缩放到限定尺寸，使用ThumbnailUtils来缩放，尺寸精确效率一般
     *
     * @param filePath 图片文件路径
     * @param maxSize  最大长度或宽度
     * @return
     */
    public static Bitmap decodeBitmapWithThumbnailUtils(String filePath,
                                                        int maxSize) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        // 设置只解码图片的边距，不会占用内存
        opt.inJustDecodeBounds = true;

        // 解码图片文件
        BitmapFactory.decodeFile(filePath, opt);

        // 获得图片的实际高和宽
        int realWidth = opt.outWidth;
        int realHeight = opt.outHeight;
        MLog.i("实际高度：" + realHeight);
        MLog.i("实际宽度：" + realWidth);

        // 判断是否需要缩放
        int fixWidth = realWidth;
        int fixHeight = realHeight;
        if (realHeight > maxSize || realWidth > maxSize) {
            if (realHeight >= realWidth) {
                fixHeight = maxSize;
                fixWidth = (int) (fixHeight * (realWidth * 1.0 / realHeight));
            } else {
                fixWidth = maxSize;
                fixHeight = (int) (fixWidth * (realHeight * 1.0 / realWidth));
            }
        }
        MLog.i("期望高度：" + fixHeight);
        MLog.i("期望宽度：" + fixWidth);

        // 读取资源图片
        opt.inJustDecodeBounds = false;
        Bitmap sourceBitmap = BitmapFactory.decodeFile(filePath);

        return ThumbnailUtils.extractThumbnail(sourceBitmap, fixWidth,
                fixHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    }

    /**
     * 从图片文件读取Bitmap并缩放到限定尺寸，使用inSampleSize（采样率）来缩放，效率高但是尺寸无法精确
     *
     * @param filePath 图片文件路径
     * @param maxSize  最大长度或宽度
     * @return
     */
    public static Bitmap decodeBitmapWithInSampleSize(String filePath,
                                                      int maxSize) {
        FileInputStream fs = null;
        BufferedInputStream bs = null;
        try {
            fs = new FileInputStream(filePath);
            bs = new BufferedInputStream(fs);

            BitmapFactory.Options opt = new BitmapFactory.Options();
            // 设置只解码图片的边距，不会占用内存
            opt.inJustDecodeBounds = true;

            // 解码图片文件
            BitmapFactory.decodeFile(filePath, opt);

            // 获得图片的实际高和宽
            int realWidth = opt.outWidth;
            int realHeight = opt.outHeight;

            // 判断是否需要缩放
            if (realHeight > maxSize || realWidth > maxSize) {
                // 计算缩放比，1表示原比例，2表示原来的四分之一，以此类推
                int scaleAccordWidth = realWidth / maxSize;
                int scaleAccordHeight = realHeight / maxSize;
                opt.inSampleSize = scaleAccordHeight > scaleAccordWidth ? scaleAccordHeight
                        : scaleAccordWidth;
            }

            // 设置加载图片的颜色数为16bit，默认是RGB_8888，表示24bit颜色和透明通道，但一般用不上
            opt.inPreferredConfig = Bitmap.Config.RGB_565;

            // 标志复原
            opt.inJustDecodeBounds = false;

            return BitmapFactory.decodeStream(bs, null, opt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bs.close();
                fs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将Bitmap转成字节数组，代码来源：微信分享SDK
     *
     * @param bmp
     * @param needRecycle
     * @return
     */
    public static byte[] bmpToByteArray(Bitmap bmp, boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.JPEG, 70, output);

        byte[] result = output.toByteArray();

        if (needRecycle) {
            bmp.recycle();
        }

        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     *
     * @param videoPath 视频的路径
     * @param width     指定输出视频缩略图的宽度
     * @param height    指定输出视频缩略图的高度度
     * @param kind      参照MediaStore.Images(Video).Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *                  其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind); //調用ThumbnailUtils類的靜態方法createVideoThumbnail獲取視頻的截圖；
        if (bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);//調用ThumbnailUtils類的靜態方法extractThumbnail將原圖片（即上方截取的圖片）轉化為指定大小；
        }
        return bitmap;
    }

    /**
     * 获取视频文件截图
     *
     * @return Bitmap 返回获取的Bitmap
     */
    public static Bitmap getVideoThumb(Context context, String fullPath, String mUrl) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        try {
            URL url = new URL(fullPath);
            url.openStream();
            media.setDataSource(fullPath, new HashMap<String, String>());
            Bitmap bm = zoomBitmap(media.getFrameAtTime(), 300, 300);
            ImageUtil.saveBitmapToDCIM(context, bm, mUrl);
            return bm;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap createVideoThumbnail(String filePath, int kind) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            if (filePath.startsWith("http://")
                    || filePath.startsWith("https://")
                    || filePath.startsWith("widevine://")) {
                retriever.setDataSource(filePath, new Hashtable<String, String>());
            } else {
                retriever.setDataSource(filePath);
            }
            bitmap = retriever.getFrameAtTime(-1);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
                ex.printStackTrace();
            }
        }

        if (bitmap == null) return null;

        if (kind == MediaStore.Images.Thumbnails.MINI_KIND) {
            // Scale down the bitmap if it's too large.
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int max = Math.max(width, height);
            if (max > 512) {
                float scale = 512f / max;
                int w = Math.round(scale * width);
                int h = Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            }
        } else if (kind == MediaStore.Images.Thumbnails.MICRO_KIND) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap,
                    96,
                    96,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    public static void saveImg2Local(Context context, String path) {
        if (!CheckUtil.stringIsBlank(path)) {
            // 将上传图片保存为homework_attach_时间.jpg
            File capture_file_upload = new File(
                    FileUtil.getCameraDir(),
                    "homework_attach_" + ImageUtil.getPhotoFileNameWithCurrentTime());
            try {
                capture_file_upload.createNewFile();
                FileOutputStream fos = new FileOutputStream(capture_file_upload);
                Bitmap decodeBitmap = ImageUtil.decodeBitmapWithThumbnailUtils(path, 2000);
                // 压缩图片质量至80%
                decodeBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));
        }
    }

    public static boolean isImage(String url) {
        url = url.toLowerCase();
        if (url.contains(".jpg") ||
                url.contains(".png") ||
                url.contains(".webP") ||
                url.contains("jpeg")) return true;
        return false;
    }

}
