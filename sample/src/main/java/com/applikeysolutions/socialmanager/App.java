package com.applikeysolutions.socialmanager;


import android.app.Application;

import com.applikeysolutions.library.Authentication;

public class App extends Application {
    @Override public void onCreate() {
        super.onCreate();
        Authentication.init(getBaseContext());
    }
}
