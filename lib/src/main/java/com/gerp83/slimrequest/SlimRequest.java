package com.gerp83.slimrequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;

import com.gerp83.slimrequest.model.SlimErrorType;
import com.gerp83.slimrequest.model.SlimFile;
import com.gerp83.slimrequest.model.SlimResult;

import org.json.JSONObject;

import java.io.File;

import javax.net.ssl.SSLContext;

/**
 * http request main class to perform http calls
 */
public class SlimRequest {

    private SlimBuilder requestBuilder;
    private SlimWorker worker;

    private static final String URL_NULL_ERROR = "URL must be non null!";

    private SlimRequest(String url, String method) {

        requestBuilder = new SlimBuilder();
        if(method.equals("GET")) {
            requestBuilder.get(url);

        } else if(method.equals("POST")) {
            requestBuilder.post(url);

        } else if(method.equals("PUT")) {
            requestBuilder.put(url);

        } else if(method.equals("PATCH")) {
            requestBuilder.patch(url);

        } else if(method.equals("DELETE")) {
            requestBuilder.delete(url);

        }

    }

    /**
     * get request to url
     *
     * @param url url to call
     */
    public static SlimRequest get(String url) {
        if (url == null) {
            throw new NullPointerException(URL_NULL_ERROR);
        }
        return new SlimRequest(url, "GET");
    }

    /**
     * post request to url
     *
     * @param url url to call
     */
    public static SlimRequest post(String url) {
        if (url == null) {
            throw new NullPointerException(URL_NULL_ERROR);
        }
        return new SlimRequest(url, "POST");
    }

    /**
     * put request to url
     *
     * @param url url to call
     */
    public static SlimRequest put(String url) {
        if (url == null) {
            throw new NullPointerException(URL_NULL_ERROR);
        }
        return new SlimRequest(url, "PUT");
    }

    /**
     * patch request to url
     *
     * @param url url to call
     */
    public static SlimRequest patch(String url) {
        if (url == null) {
            throw new NullPointerException(URL_NULL_ERROR);
        }
        return new SlimRequest(url, "PATCH");
    }

    /**
     * delete request to url
     *
     * @param url url to call
     */
    public static SlimRequest delete(String url) {
        if (url == null) {
            throw new NullPointerException(URL_NULL_ERROR);
        }
        return new SlimRequest(url, "DELETE");
    }

    /**
     * push contentType
     *
     * @param contentType content type. Default SlimConstants.CONTENT_TYPE_FORM;
     */
    public SlimRequest setContentType(String contentType) {
        if (contentType != null) {
            requestBuilder.setContentType(contentType);
        }
        return this;
    }

    /**
     * push http param
     *
     * @param key http param key
     * @param value http param value
     */
    public SlimRequest addParam(String key, String value) {
        if (key != null && value != null) {
            requestBuilder.addParam(key, value);
        }
        return this;
    }

    /**
     * push http header
     *
     * @param key http header key
     * @param value http header value
     */
    public SlimRequest addHeader(String key, String value) {
        if (key != null && value != null) {
            requestBuilder.addHeader(key, value);
        }
        return this;
    }

    /**
     * push a JSONObject to POST
     *
     * @param postJson JSONObject to post
     */
    public SlimRequest setPostJson(JSONObject postJson) {
        if (postJson != null) {
            requestBuilder.setPostJson(postJson);
        }
        return this;
    }

    /**
     * set basic authentication
     *
     * @param username username param
     * @param password password param
     */
    public SlimRequest setBasicAuthentication(String username, String password) {
        if (username != null && password != null) {
            requestBuilder.setBasicAuthentication(username, password);
        }
        return this;
    }

    /**
     * all https will be trusted
     */
    public SlimRequest trustAllHttps() {
        requestBuilder.trustAllHttps();
        return this;
    }

    /**
     * push a SSLContext, it loads a SSL certificate from assets and it to the https call
     *
     * @param sslContext SSLContext param
     */
    public SlimRequest setSSLContext(SSLContext sslContext) {
        if (sslContext != null) {
            requestBuilder.setSSLContext(sslContext);
        }
        return this;
    }

    /**
     * push SlimReturnType. Default is SlimReturnType.STRING.
     *
     * @param returnType the http call return type
     */
    public SlimRequest setReturnType(int returnType) {
        if (returnType >= 0) {
            requestBuilder.setReturnType(returnType);
        }
        return this;
    }

    /**
     * set connection timeout for the request
     *
     * @param connectionTimeOut time in int
     */
    public SlimRequest setConnectionTimeOut(int connectionTimeOut) {
        if (connectionTimeOut > 0) {
            requestBuilder.setConnectionTimeOut(connectionTimeOut);
        }
        return this;
    }

    /**
     * set read timeout for the request
     *
     * @param readTimeOut time in int
     */
    public SlimRequest setReadTimeOut(int readTimeOut) {
        if (readTimeOut > 0) {
            requestBuilder.setReadTimeOut(readTimeOut);
        }
        return this;
    }

    /**
     * add SlimFile for file upload
     *
     * @param uploadFile the file to upload
     */
    public SlimRequest setUploadFile(SlimFile uploadFile) {
        if (uploadFile != null) {
            requestBuilder.setUploadFile(uploadFile);
        }
        return this;
    }

    /**
     * add File for download
     *
     * @param downloadFile the destination Fle to download
     */
    public SlimRequest downloadToFile(File downloadFile) {
        if (downloadFile != null) {
            requestBuilder.setDownloadFile(downloadFile);
        }
        return this;
    }

    /**
     * set chunk size for upload/download
     *
     * @param chunkSize chunk size
     */
    public SlimRequest setChunkSize(int chunkSize) {
        if (chunkSize < 1024) {
            requestBuilder.setChunkSize(chunkSize);
        }
        return this;
    }

    /**
     * set stack position
     */
    public SlimRequest setStackPosition(int stackPosition) {
        if (stackPosition >= 0) {
            requestBuilder.setStackPosition(stackPosition);
        }
        return this;
    }

    /**
     * save a Session
     *
     * @param context mandatory param
     * @param sessionKey key for session
     * @param sessionValue value for session
     */
    public static void saveSession(Context context, String sessionKey, String sessionValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sessionKey", sessionKey);
        editor.putString("sessionValue", sessionValue);
        editor.apply();
    }

    /**
     * push Session to SlimRequest header
     *
     * @param context mandatory param
     */
    public SlimRequest addSession(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getString("sessionKey", null) != null) {
            requestBuilder.addHeader(sharedPreferences.getString("sessionKey", null), sharedPreferences.getString("sessionValue", null));
        }
        return this;
    }

    /**
     * clear Session
     *
     * @param context mandatory param
     */
    public static void clearSession(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("sessionKey");
        editor.remove("sessionValue");
        editor.apply();
    }

    /**
     * start SlimRequest
     *
     * @param context mandatory param
     * @param requestCallback SlimRequestCallback param
     */
    public SlimRequest run(Context context, SlimRequestCallback requestCallback) {
        return run(context, null, requestCallback);
    }

    /**
     * start SlimRequest
     *
     * @param context mandatory param
     * @param progressCallback SlimProgressCallback param
     * @param requestCallback SlimRequestCallback param
     */
    public SlimRequest run(Context context, SlimProgressCallback progressCallback, SlimRequestCallback requestCallback) {
        if (context == null || hasNetworkConnection(context)) {
            worker = new SlimWorker(requestCallback);
            if(progressCallback != null) {
                worker.setSlimProgressCallback(progressCallback);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                worker.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, requestBuilder);
            } else {
                worker.execute(requestBuilder);
            }

        } else {
            requestCallback.onFail(new SlimResult().setErrorType(SlimErrorType.NETWORK));
        }
        return this;
    }

    /**
     * cancel SlimRequest
     */
    public void cancel() {
        if (worker != null) {
            worker.cancel();
        }
    }

    private boolean hasNetworkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}