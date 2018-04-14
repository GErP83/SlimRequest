package com.gerp83.slimrequest;

import android.os.AsyncTask;

import com.gerp83.slimrequest.model.SlimResult;

/**
 * perform http request in the background with AsyncTask
 */
class SlimWorker extends AsyncTask<SlimBuilder, Integer, Void> {

    private SlimProgressCallback slimProgressCallback;
    private SlimRequestCallback requestCallback;
    private SlimResult requestResult;

    SlimWorker(SlimRequestCallback requestCallback) {
        this.requestCallback = requestCallback;
    }

    @Override
    protected Void doInBackground(SlimBuilder... params) {
        if(slimProgressCallback != null) {
            params[0].setSlimBuilderProgressListener(new SlimBuilder.SlimBuilderProgressListener() {
                @Override
                public void onUpdate(int chunkBytes, int totalBytes) {
                    publishProgress(chunkBytes, totalBytes);
                }
            });
        }
        if (params[0].getUploadFile() == null) {
            requestResult = params[0].request();
        } else {
            requestResult = params[0].requestWithFile();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        if(slimProgressCallback != null) {
            slimProgressCallback.onProgress(progress[0], progress[1]);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (!requestResult.hasError()) {
            requestCallback.onSuccess(requestResult);

        } else {
            requestCallback.onFail(requestResult);

        }
    }

    public void setSlimProgressCallback(SlimProgressCallback slimProgressCallback) {
        this.slimProgressCallback = slimProgressCallback;
    }

    public void cancel() {
        this.cancel(true);
    }


}