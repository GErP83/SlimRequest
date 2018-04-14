package com.gerp83.easyrequesttest;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.gerp83.slimrequest.SlimRequest;

public class TestApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        SlimRequest.setBaseUrl(this, "https://api.themoviedb.org/3/");
    }
}