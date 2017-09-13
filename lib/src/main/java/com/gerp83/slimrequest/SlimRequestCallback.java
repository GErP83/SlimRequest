package com.gerp83.slimrequest;


import com.gerp83.slimrequest.model.SlimResult;

/**
 * callBack for request
 */
public interface SlimRequestCallback {

    void onSuccess(SlimResult result);
    void onFail(SlimResult result);

}