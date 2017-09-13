package com.gerp83.slimrequest.stack;

import com.gerp83.slimrequest.model.SlimResult;

import java.util.ArrayList;

/**
 * callBack for SlimStack
 */
public interface SlimStackCallback {

    void onResult(ArrayList<SlimResult> results);

}