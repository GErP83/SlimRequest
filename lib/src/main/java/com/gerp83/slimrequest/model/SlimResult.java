package com.gerp83.slimrequest.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * http request result class
 */
public class SlimResult {

    private int responseCode = -1;
    private Object data = null;
    private int errorType = -1;
    private String error = null;
    private Map<String, List<String>> headers = null;
    private boolean skipped = false;
    private int stackPosition = -1;

    private long runTime = -1;
    private int bytesUploaded = -1;
    private int bytesDownloaded = -1;


    public SlimResult() {
    }

    /**
     * returns responseCode
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * set responseCode
     *
     * @param responseCode responseCode
     */
    public SlimResult setResponseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    /**
     * returns data as String
     */
    public String getStringData() {
        if (data == null) {
            return null;
        }
        try {
            return (String) data;
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * returns data as JSONObject
     */
    public JSONObject getJsonObjectData() {
        if (data == null) {
            return null;
        }
        try {
            return (JSONObject) data;
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * returns data as JSONArray
     */
    public JSONArray getJsonArrayData() {
        if (data == null) {
            return null;
        }
        try {
            return (JSONArray) data;
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * returns data as bytes
     */
    public byte[] getBytesData() {
        if (data == null) {
            return null;
        }
        try {
            return (byte[]) data;
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * set data
     *
     * @param data data
     */
    public SlimResult setData(Object data) {
        this.data = data;
        return this;
    }

    /**
     * returns http headers
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * set http headers
     *
     * @param headers headers
     */
    public SlimResult setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * returns error type
     */
    public int getErrorType() {
        return errorType;
    }

    /**
     * set error type
     *
     * @param errorType error type
     */
    public SlimResult setErrorType(int errorType) {
        this.errorType = errorType;
        return this;
    }

    /**
     * returns error
     */
    public String getError() {
        return error;
    }

    /**
     * set error
     *
     * @param error error
     */
    public SlimResult setError(String error) {
        this.error = error;
        return this;
    }

    /**
     * has any error
     */
    public boolean hasError(){
        return getError() != null || getErrorType() != -1;
    }

    /**
     * is request skipped able
     */
    public boolean isSkipped() {
        return skipped;
    }

    /**
     * set request skipped able
     */
    public SlimResult setSkipped() {
        this.skipped = true;
        return this;
    }

    /**
     * get stack position
     */
    public int getStackPosition() {
        return stackPosition;
    }

    /**
     * set stack position
     */
    public SlimResult setStackPosition(int stackPosition) {
        this.stackPosition = stackPosition;
        return this;
    }

    /**
     * get run time in milliseconds
     */
    public long getRunTime() {
        return runTime;
    }

    /**
     * get bytes uploaded
     */
    public int getBytesUploaded() {
        return bytesUploaded;
    }

    /**
     * set bytes uploaded
     */
    public SlimResult setBytesUploaded(int bytesUploaded) {
        this.bytesUploaded = bytesUploaded;
        return this;
    }

    /**
     * get bytes download
     */
    public int getBytesDownloaded() {
        return bytesDownloaded;
    }

    /**
     * set bytes download
     */
    public SlimResult setBytesDownloaded(int bytesDownloaded) {
        this.bytesDownloaded = bytesDownloaded;
        return this;
    }

    /**
     * set run time in milliseconds
     */
    public SlimResult setRunTime(long runTime) {
        this.runTime = runTime;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("ResponseCode: ").append(responseCode).append("\n");
        builder.append("Runtime: ").append(runTime).append("\n");
        builder.append("Bytes Uploaded: ").append(bytesUploaded).append("\n");
        builder.append("Bytes Downloaded: ").append(bytesDownloaded).append("\n");

        if(errorType != -1) {
            builder.append("ErrorType: ").append(errorType).append("\n");

        }
        if (error != null) {
            builder.append("Error: ").append(error).append("\n");

        }
        if (data == null) {
            builder.append("Data: null").append("\n");

        } else if (data instanceof String) {
            builder.append("Data String: ").append(data).append("\n");

        } else if (data instanceof JSONObject) {
            builder.append("Data JSONObject: ").append(data.toString()).append("\n");

        } else if (data instanceof JSONArray) {
            builder.append("Data JSONArray: ").append(data.toString()).append("\n");

        } else if (data instanceof byte[]) {
            builder.append("Data bytes: ").append(((byte[]) data).length).append("\n");

        }

        if (headers == null) {
            builder.append("Headers: null").append("\n");

        } else {
            builder.append("Headers: ").append("\n");
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                builder.append("Key: ").append(entry.getKey()).append(", Value: ").append(entry.getValue()).append("\n");
            }

        }
        builder.append("Skipped: ").append(skipped).append("\n");

        return builder.toString();
    }

}