package com.gerp83.slimrequest.chain;

import android.content.Context;

import com.gerp83.slimrequest.SlimRequest;
import com.gerp83.slimrequest.SlimRequestCallback;
import com.gerp83.slimrequest.model.SlimResult;

import java.util.ArrayList;

/**
 * request chain, loads one request and transfers its results to the next request
 */
public class SlimChain {

    private ArrayList<SlimResult> results;
    private ArrayList<Object> chainList;

    private Context context;
    private SlimChainCallback slimChainCallback;
    private SlimRequest currentRequest;

    private boolean stopped = false;

    public SlimChain() {
        results = new ArrayList<>();
        chainList = new ArrayList<>();
    }

    /**
     * create a stack
     */
    public static SlimChain create() {
        return new SlimChain();
    }

    /**
     * push a SlimRequest to the stack
     *
     * @param request SlimRequest to push
     */
    public SlimChain push(SlimRequest request) {
        chainList.add(request);
        return this;
    }


    /**
     * push a SlimTransfer to the stack
     *
     * @param transfer SlimRequest to push
     */
    public SlimChain push(SlimTransfer transfer) {
        chainList.add(transfer);
        return this;
    }

    /**
     * start run chain
     *
     * @param context               mandatory param
     * @param slimChainCallback     SlimChainCallback param
     */
    public void run(Context context, SlimChainCallback slimChainCallback) {
        if (context == null || slimChainCallback == null || chainList.size() == 0) {
            return;
        }

        this.context = context;
        this.slimChainCallback = slimChainCallback;
        stopped = false;

        passNext();
    }

    private void passNext() {
        if (stopped) {
            return;
        }

        if (chainList.size() > 0) {
            Object object = chainList.remove(0);
            if(object != null) {                    //don't add null in the chain

                if(object instanceof SlimRequest) {
                    currentRequest = (SlimRequest) object;
                    currentRequest.run(context, requestCallback);

                } else if(object instanceof SlimTransfer) {
                    SlimTransfer currentTransfer = (SlimTransfer) object;

                    //last item not a SlimTransfer and next item also not a SlimTransfer
                    if (chainList.size() > 0 || chainList.get(0) instanceof SlimRequest){
                        currentTransfer.gather(results.get(results.size() -1));
                        currentTransfer.pass((SlimRequest) chainList.get(0));
                        passNext();

                    } else {
                        slimChainCallback.onResult(results);

                    }
                }

            } else {
                slimChainCallback.onResult(results);

            }

        } else {
            if (results.size() > 0) {
                slimChainCallback.onResult(results);
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

    private void handleCallback(SlimResult result) {
        results.add(result);
        passNext();
    }

    /**
     * cancel all requests in stack
     */
    public void cancelAll() {
        if (currentRequest != null) {
            currentRequest.cancel();
        }

        if (chainList != null) {
            for (Object object : chainList) {
                if (object != null && object instanceof SlimRequest) {
                    ((SlimRequest)object).cancel();
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
        chainList.clear();
        results.clear();
    }

    /**
     * push SlimChainCallback
     *
     * @param slimChainCallback     SlimChainCallback
     */
    public void setSlimChainCallback(SlimChainCallback slimChainCallback) {
        this.slimChainCallback = slimChainCallback;
    }

}