package com.rzm.mglide.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LoadDataManager implements ILoadData, Runnable {

    private final static String TAG = LoadDataManager.class.getSimpleName();

    private String path;
    private ResponseListener responseListener;
    private Context context;

    /**
     * @param path
     * @param responseListener
     * @param context
     * @return
     */
    @Override
    public Value loadResource(String path, ResponseListener responseListener, Context context) {
        this.path = path;
        this.responseListener = responseListener;
        this.context = context;

        Uri uri = Uri.parse(path);
        if ("HTTP".equalsIgnoreCase(uri.getScheme()) || "HTTPS".equalsIgnoreCase(uri.getScheme())) {
            new ThreadPoolExecutor(0,
                    Integer.MAX_VALUE,
                    60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>()).execute(this);
        } else {
            // 本地资源
            // ....
        }
        return null;
    }

    @Override
    public void run() {
        InputStream inputStream = null;  // 成果
        HttpURLConnection httpURLConnection = null; // HttpURLConnection内部已经是Okhttp，因为太高效了

        try {
            URL url = new URL(path);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(5000);

            final int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200
                inputStream = httpURLConnection.getInputStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                // TODO Bitmap 做缩放，做比例，做压缩
                // 成功 切换主线程  Ui线程 == Looper.getMainLooper()
                new Handler(Looper.getMainLooper()).post(() -> {
                    Value value = new Value();
                    value.setBitmap(bitmap);
                    responseListener.responseSuccess(value);
                });
            } else {
                new Handler(Looper.getMainLooper()).post(() -> responseListener.responseException(new IllegalStateException("请求失败，请求码：" + responseCode)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "run: 关闭 inputStream.close(); e:" + e.getMessage());
                }
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }
}
