package com.stanford.lolapp.network;

/**
 * Created by Mark Stanford on 4/25/14.
 */

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.ImageLoader;

public class VolleyTask {

    private static RequestQueue mRequestQueue = null;
    private static ImageLoader mImageLoader = null;

    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 12;

    public static RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            DefaultHttpClient http = new DefaultHttpClient();
            ClientConnectionManager mgr = http.getConnectionManager();
            HttpParams params = http.getParams();
            http = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
            mRequestQueue = new RequestQueue(
                    new DiskBasedCache(context.getCacheDir()),
                    new BasicNetwork(new HttpClientStack(http)),
                    DEFAULT_NETWORK_THREAD_POOL_SIZE);
        }
        return mRequestQueue;
    }

    public static ImageLoader getImageLoader() {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
                public void putBitmap(String url, Bitmap bitmap) {
                    mCache.put(url, bitmap);
                }
                public Bitmap getBitmap(String url) {
                    return mCache.get(url);
                }
            });
        }
        return mImageLoader;
    }
}
