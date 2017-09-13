package com.gerp83.slimrequest;

/**
 * callBack for request progress
 */
public interface SlimProgressCallback {

    void onProgress(int chuckBytes, int totalBytes);

}