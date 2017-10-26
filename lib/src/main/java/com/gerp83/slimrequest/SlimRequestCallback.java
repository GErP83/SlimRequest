package com.gerp83.slimrequest;


import com.gerp83.slimrequest.model.SlimResult;

/**
 * callBack for a request
 */
public interface SlimRequestCallback {

    void onSuccess(SlimResult result);
    void onFail(SlimResult result);

}