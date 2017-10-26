package com.gerp83.slimrequest.stack;

import android.content.Context;

import com.gerp83.slimrequest.SlimRequest;
import com.gerp83.slimrequest.SlimRequestCallback;
import com.gerp83.slimrequest.model.SlimResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * request stack, loads all request in the stack async
 */
public class SlimStack {

    private ArrayList<SlimRequest> requests;
    private ArrayList<SlimResult> results;
    private SlimStackCallback slimStackCallback;

    private boolean stopped = false;
    private int asyncNumMax = 0;
    private int asyncNum = 0;

    public SlimStack() {
        requests = new ArrayList<>();
        results = new ArrayList<>();
    }

    /**
     * create a stack
     */
    public static SlimStack create() {
        return new SlimStack();
    }

    /**
     * push a SlimRequest to the stack
     *
     * @param request SlimRequest to push
     */
    public SlimStack push(SlimRequest request) {
        requests.add(request);
        return this;
    }

    /**
     * start run all requests in the stack async
     *
     * @param context               mandatory param
     * @param slimStackCallback     SlimChainCallback param
     */
    public void run(Context context, SlimStackCallback slimStackCallback) {
        if (context == null || slimStackCallback == null || requests.size() == 0) {
            return;
        }

        this.slimStackCallback = slimStackCallback;
        stopped = false;
        asyncNumMax = 0;
        asyncNum = 0;

        for(int i = 0; i < requests.size(); i++) {
            if (requests.get(i) != null) {
                asyncNumMax ++;
                requests.get(i).setStackPosition(i).run(context, requestCallback);

            } else {
                results.add(new SlimResult().setStackPosition(i).setSkipped());

            }
        }

    }

    private SlimRequestCallback requestCallback = new SlimRequestCallback() {
        @Override
        public void onSuccess(SlimResult result) {
            handleCallback(result);
        }

        @Override
        public void onFail(SlimResult result) {
            handleCallback(result);
        }

    };

    private void handleCallback(SlimResult result){
        if (stopped) {
            return;
        }
        results.add(result);

        asyncNum ++;
        if(asyncNum == asyncNumMax) {
            if (!stopped && results.size() > 0) {
                Collections.sort(results, new Comparator<SlimResult>() {
                    @Override
                    public int compare(SlimResult r1, SlimResult r2) {
                        return r1.getStackPosition() > r2.getStackPosition() ? 1 : -1;
                    }
                });
                slimStackCallback.onResult(results);
            }
        }

    }

    /**
     * cancel all requests in stack
     */
    public void cancelAll() {
        if (requests != null) {
            for (SlimRequest request : requests) {
                if (request != null) {
                    request.cancel();
                }
            }
        }
    }

    /**
     * clear all data and cancel all requests in stack
     */
    public void clear() {
        stopped = true;
        cancelAll();
        requests.clear();
        results.clear();
    }

    /**
     * push SlimChainCallback
     *
     * @param slimStackCallback     SlimChainCallback
     */
    public void setSlimStackCallback(SlimStackCallback slimStackCallback) {
        this.slimStackCallback = slimStackCallback;
    }

}