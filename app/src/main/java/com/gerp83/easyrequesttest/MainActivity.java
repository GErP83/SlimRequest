package com.gerp83.easyrequesttest;

import android.app.Activity;
import android.os.Bundle;

import com.gerp83.slimrequest.SlimRequest;
import com.gerp83.slimrequest.SlimRequestCallback;
import com.gerp83.slimrequest.model.SlimResult;

public class MainActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SlimRequest
                .get("search/movie")
                .addParam("api_key", "some_api_key")
                .addParam("query", "aaa")
                .run(this, new SlimRequestCallback() {
                    @Override
                    public void onSuccess(SlimResult result) {
                        System.out.println(result.toString());
                    }

                    @Override
                    public void onFail(SlimResult result) {
                        System.out.println(result.toString());
                    }
                });

    }

}