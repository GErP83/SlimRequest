package com.gerp83.slimrequest.util;


import com.gerp83.slimrequest.model.SlimFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Multipart Entitiy for http post file uploads
 */
public class MultipartEntity {

    private static final String LINE_FEED = "\r\n";
    private String boundary;
    private String charset = "UTF-8";

    private String contentTypeName;
    private String contentTypeValue;
    private HashMap<String, String> params;
    private SlimFile requestFile;

    private PrintWriter writer;

    public MultipartEntity() {
        boundary = "===" + System.currentTimeMillis() + "===";
        contentTypeName = "Content-Type";
        contentTypeValue = "multipart/form-data; boundary=" + boundary;
    }

    private void addParam(String key, String value) {
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=").append(charset).append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }


    private void addFile(OutputStream outputStream) throws IOException {
        String fileName = requestFile.file.getName();
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(requestFile.paramName).append("\"; filename=\"").append(fileName).append("\"").append(LINE_FEED);
        writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(requestFile.file);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED);
        writer.flush();

    }

    /**
     * write file upload to OutputStream
     */
    public void writeTo(OutputStream outputStream) throws IOException {
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
        if (params != null) {
            for (Map.Entry<String, String> e : params.entrySet()) {
                addParam(e.getKey(), e.getValue());
            }
        }
        if (requestFile != null) {
            addFile(outputStream);
        }

        writer.append(LINE_FEED).flush();
        writer.append("--").append(boundary).append("--").append(LINE_FEED);
        writer.close();
    }

    /**
     * returns contentType name
     */
    public String getContentTypeName() {
        return contentTypeName;
    }

    /**
     * returns contentType value
     */
    public String getContentTypeValue() {
        return contentTypeValue;
    }

    /**
     * push http params
     *
     * @param params http params map
     */
    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    /**
     * set SlimFile
     *
     * @param requestFile SlimFile
     */
    public void setRequestFile(SlimFile requestFile) {
        this.requestFile = requestFile;
    }

}