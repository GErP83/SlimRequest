package com.gerp83.slimrequest;

import android.annotation.SuppressLint;
import android.net.Uri;

import com.gerp83.slimrequest.model.SlimErrorType;
import com.gerp83.slimrequest.model.SlimFile;
import com.gerp83.slimrequest.model.SlimResult;
import com.gerp83.slimrequest.model.SlimReturnType;
import com.gerp83.slimrequest.util.MultipartEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/**
 * Http request builder
 */
class SlimBuilder {


    private String contentType = SlimConstants.CONTENT_TYPE_FORM;
    private int returnType = SlimReturnType.STRING;
    private int connectionTimeOut = SlimConstants.HTTP_CONNECTION_TIMEOUT;
    private int readTimeOut = SlimConstants.HTTP_READ_TIMEOUT;
    private int chunkSize = SlimConstants.DEFAULT_CHUNK_SIZE;

    private String url;
    private String baseUrl;
    private String requestMethod;
    private HashMap<String, String> params;
    private HashMap<String, String> headers;
    private JSONObject postJson;
    private SSLContext sslContext;

    private SlimFile uploadFile;
    private File downloadFile;

    private int stackPosition = -1;
    private long startTime = -1;
    private int totalBytes = -1;
    private int bytesUploaded = -1;
    private int bytesDownloaded = -1;

    private SlimBuilderProgressListener slimBuilderProgressListener;

    public interface SlimBuilderProgressListener {
        void onUpdate(int chunkBytes, int totalBytes);
    }

    void get(String url) {
        requestMethod = "GET";
        this.url = url;
    }

    void post(String url) {
        requestMethod = "POST";
        this.url = url;
    }

    void put(String url) {
        requestMethod = "PUT";
        this.url = url;
    }

    void patch(String url) {
        requestMethod = "PATCH";
        this.url = url;
    }

    void delete(String url) {
        requestMethod = "DELETE";
        this.url = url;
    }

    void setContentType(String contentType) {
        this.contentType = contentType;
    }

    void addParam(String key, String value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, value);
    }

    void addHeader(String key, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(key, value);
    }

    void setPostJson(JSONObject postJson) {
        this.postJson = postJson;
    }

    void setReturnType(int returnType) {
        this.returnType = returnType;
    }

    void setConnectionTimeOut(int connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    void setStackPosition(int stackPosition) {
        this.stackPosition = stackPosition;
    }

    void setBasicAuthentication(final String username, final String password) {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(username, password.toCharArray());
            }
        });
    }

    void setSSLContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    void trustAllHttps() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return session.isValid();
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                @SuppressLint("TrustAllX509TrustManager")
                public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                @SuppressLint("TrustAllX509TrustManager")
                public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    SlimFile getUploadFile() {
        return uploadFile;
    }

    void setUploadFile(SlimFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    void setDownloadFile(File downloadFile) {
        returnType = SlimReturnType.FILE;
        this.downloadFile = downloadFile;
    }

    void setSlimBuilderProgressListener(SlimBuilderProgressListener slimBuilderProgressListener) {
        this.slimBuilderProgressListener = slimBuilderProgressListener;
    }

    void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    SlimResult request() {
        try {
            startTime = System.currentTimeMillis();
            if(baseUrl != null) {
                url = baseUrl + url;
            }
            Uri.Builder builder = Uri.parse(url).buildUpon();
            if (params != null) {
                for (Entry<String, String> e : params.entrySet()) {
                    builder.appendQueryParameter(e.getKey(), e.getValue());
                }
            }
            URL u = new URL(builder.toString());
            String postData = null;
            if (!requestMethod.contentEquals("GET")) {
                postData = u.getQuery();
            }

            HttpURLConnection urlConnection;
            if (sslContext == null) {
                urlConnection = (HttpURLConnection) u.openConnection();

            } else {
                urlConnection = (HttpsURLConnection) u.openConnection();
                ((HttpsURLConnection) urlConnection).setSSLSocketFactory(sslContext.getSocketFactory());

            }

            if (requestMethod.contentEquals("PATCH")) {
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            } else {
                urlConnection.setRequestMethod(requestMethod);

            }

            urlConnection.setConnectTimeout(connectionTimeOut);
            urlConnection.setReadTimeout(readTimeOut);
            urlConnection.setRequestProperty("Content-Type", contentType);

            if (headers != null) {
                for (String key : headers.keySet()) {
                    urlConnection.setRequestProperty(key, headers.get(key));
                }
            }

            if (!requestMethod.contentEquals("GET") && !requestMethod.contentEquals("DELETE")) {
                urlConnection.setDoOutput(true);
            }

            if (postData != null || postJson != null) {
                char[] bytes;
                if (postData != null) {
                    bytes = postData.toCharArray();

                } else {
                    bytes = postJson.toString().toCharArray();

                }
                urlConnection.setRequestProperty("Content-Length", String.valueOf(bytes.length));

                OutputStreamWriter post;
                post = new OutputStreamWriter(urlConnection.getOutputStream());
                post.write(bytes);
                post.flush();
            }

            return readOutData(urlConnection);

        } catch (Throwable e) {
            return new SlimResult()
                    .setRunTime(System.currentTimeMillis() - startTime)
                    .setBytesDownloaded(bytesDownloaded)
                    .setBytesUploaded(bytesUploaded)
                    .setStackPosition(stackPosition)
                    .setErrorType(SlimErrorType.REQUEST)
                    .setError(e.toString());

        }

    }

    SlimResult requestWithFile() {
        try {
            startTime = System.currentTimeMillis();
            MultipartEntity multipartEntity = new MultipartEntity();
            multipartEntity.setParams(params);
            multipartEntity.setRequestFile(uploadFile);

            if(baseUrl != null) {
                url = baseUrl + url;
            }
            URL u = new URL(url);

            HttpURLConnection urlConnection;
            if (sslContext == null) {
                urlConnection = (HttpURLConnection) u.openConnection();

            } else {
                urlConnection = (HttpsURLConnection) u.openConnection();
                ((HttpsURLConnection) urlConnection).setSSLSocketFactory(sslContext.getSocketFactory());

            }
            urlConnection.setRequestMethod(requestMethod);
            urlConnection.setConnectTimeout(connectionTimeOut);
            urlConnection.setReadTimeout(readTimeOut);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty(multipartEntity.getContentTypeName(), multipartEntity.getContentTypeValue());

            if (headers != null) {
                for (String key : headers.keySet()) {
                    urlConnection.setRequestProperty(key, headers.get(key));
                }
            }

            ByteArrayOutputStream cacheOutputStream = new ByteArrayOutputStream();
            multipartEntity.writeTo(cacheOutputStream);
            cacheOutputStream.close();
            byte[] payload = cacheOutputStream.toByteArray();

            urlConnection.setRequestProperty("Content-length", String.valueOf(payload.length));

            totalBytes = payload.length;
            bytesUploaded = 0;

            System.out.println("total: " + totalBytes);
            while (bytesUploaded < totalBytes) {
                int nextChunkSize = totalBytes - bytesUploaded;
                if (nextChunkSize > chunkSize) {
                    nextChunkSize = chunkSize;
                }
                System.out.println(nextChunkSize + ", " + chunkSize);
                urlConnection.getOutputStream().write(payload, bytesUploaded, nextChunkSize);
                bytesUploaded += nextChunkSize;
                if (slimBuilderProgressListener != null) {
                    slimBuilderProgressListener.onUpdate(bytesUploaded, totalBytes);
                }

            }

            return readOutData(urlConnection);

        } catch (Throwable e) {
            return new SlimResult()
                    .setRunTime(System.currentTimeMillis() - startTime)
                    .setBytesDownloaded(bytesDownloaded)
                    .setBytesUploaded(bytesUploaded)
                    .setStackPosition(stackPosition)
                    .setErrorType(SlimErrorType.REQUEST_UPLOAD)
                    .setError(e.toString());
        }
    }

    private SlimResult readOutData(HttpURLConnection urlConnection) {

        try {

            totalBytes = urlConnection.getContentLength();
            int responseCode = urlConnection.getResponseCode();
            InputStream inputStream;

            if (!String.valueOf(responseCode).startsWith("2")) {
                inputStream = new BufferedInputStream(urlConnection.getErrorStream());
            } else {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            }

            //hack: sometimes downloading pictures is buggy
            try {
                inputStream.reset();
            } catch (Throwable ignored) {
            }

            SlimResult slimResult = new SlimResult().setResponseCode(responseCode).setHeaders(urlConnection.getHeaderFields());
            slimResult.setStackPosition(stackPosition);

            switch (returnType) {

                case SlimReturnType.STRING:
                    slimResult.setData(readInputStream(inputStream).toString());
                    break;

                case SlimReturnType.JSON_OBJECT:
                case SlimReturnType.JSON_ARRAY:
                    String jsonString = readInputStream(inputStream).toString();
                    try {
                        if (returnType == SlimReturnType.JSON_OBJECT) {
                            slimResult.setData(new JSONObject(jsonString));
                        } else {
                            slimResult.setData(new JSONArray(jsonString));
                        }

                    } catch (JSONException e) {
                        return slimResult
                                .setRunTime(System.currentTimeMillis() - startTime)
                                .setErrorType(SlimErrorType.PARSE)
                                .setError(e.toString())
                                .setData(jsonString);
                    }
                    break;

                case SlimReturnType.BYTES:
                    slimResult.setData(readInputStream(inputStream).toByteArray());
                    break;

                case SlimReturnType.FILE:
                    saveToFile(inputStream, downloadFile);
                    break;

            }

            inputStream.close();
            urlConnection.disconnect();
            return slimResult
                    .setRunTime(System.currentTimeMillis() - startTime)
                    .setBytesDownloaded(bytesDownloaded)
                    .setBytesUploaded(bytesUploaded);

        } catch (Throwable e) {
            return new SlimResult()
                    .setRunTime(System.currentTimeMillis() - startTime)
                    .setBytesDownloaded(bytesDownloaded)
                    .setBytesUploaded(bytesUploaded)
                    .setStackPosition(stackPosition)
                    .setErrorType(SlimErrorType.REQUEST)
                    .setError(e.toString());
        }
    }

    private ByteArrayOutputStream readInputStream(InputStream inputStream) throws IOException{
        ByteArrayOutputStream bo = new ByteArrayOutputStream();

        bytesDownloaded = 0;
        byte[] data = new byte[chunkSize];
        int count;

        while ((count = inputStream.read(data)) != -1) {
            bytesDownloaded += count;
            if(totalBytes != - 1 && slimBuilderProgressListener != null) {
                slimBuilderProgressListener.onUpdate(bytesDownloaded, totalBytes);
            }
            bo.write(data, 0, count);
        }
        bo.close();
        return bo;
    }

    private void saveToFile(InputStream inputStream, File file) throws IOException {

        FileOutputStream fos = new FileOutputStream(file);
        bytesDownloaded = 0;
        byte[] data = new byte[chunkSize];
        int count;

        while ((count = inputStream.read(data)) != -1) {
            bytesDownloaded += count;
            if(totalBytes != - 1 && slimBuilderProgressListener != null) {
                slimBuilderProgressListener.onUpdate(bytesDownloaded, totalBytes);
            }
            fos.write(data, 0, count);
        }
        fos.close();
    }

}