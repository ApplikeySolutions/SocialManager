package com.applikeysolutions.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.applikeysolutions.library.networks.FacebookAuthActivity;
import com.applikeysolutions.library.networks.GoogleAuthActivity;
import com.applikeysolutions.library.networks.InstagramAuthActivity;
import com.applikeysolutions.library.networks.TwitterAuthActivity;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static com.applikeysolutions.library.Network.FACEBOOK;
import static com.applikeysolutions.library.Network.GOOGLE;
import static com.applikeysolutions.library.Network.INSTAGRAM;
import static com.applikeysolutions.library.Network.TWITTER;
import static com.twitter.sdk.android.core.TwitterConfig.Builder;

public class Authentication {

    @SuppressLint("StaticFieldLeak")
    private static Authentication instance;
    private Context appContext;
    private PublishSubject<NetworklUser> emitter;
    private List<String> facebookScopes = new ArrayList<>();
    private List<String> googleScopes = new ArrayList<>();
    private List<String> twitterScopes = new ArrayList<>();
    private List<String> instagramScopes = new ArrayList<>();
    private Network network;

    private Authentication() {
    }

    public static Authentication getInstance() {
        if (instance == null) {
            synchronized (Authentication.class) {
                if (instance == null) {
                    instance = new Authentication();
                }
            }
        }
        return instance;
    }

    public static void init(Context context) {
        Context appContext = context.getApplicationContext();
        getInstance().appContext = appContext;
        getInstance().initFacebook(appContext);
        getInstance().initTwitter(appContext);
    }

    public Observable<NetworklUser> login() {
        emitter = PublishSubject.create();
        networkChooser();
        return emitter;
    }

    public void onLoginSuccess(NetworklUser user) {
        if (emitter != null) {
            emitter.onNext(user);
            emitter.onComplete();
        }
    }

    public void onLoginError(Throwable throwable) {
        if (emitter != null) {
            Throwable copy = new Throwable(throwable);
            emitter.onError(copy);
        }
    }

    public void onLoginCancel() {
        if (emitter != null) {
            emitter.onComplete();
        }
    }

    public void networkChooser() {
        if (network == FACEBOOK) {
            appContext.startActivity(FacebookAuthActivity.getIntent(appContext));
        } else if (network == GOOGLE) {
            appContext.startActivity(GoogleAuthActivity.getIntent(appContext));
        } else if (network == INSTAGRAM) {
            appContext.startActivity(InstagramAuthActivity.getIntent(appContext));
        } else if (network == TWITTER) {
            appContext.startActivity(TwitterAuthActivity.getIntent(appContext));
        } else {
            throw new RuntimeException("No such network");
        }
    }

    public Authentication connectFacebook(@Nullable List<String> scopes) {
        facebookScopes = scopes;
        network = FACEBOOK;

        return this;
    }

    public void disconnectFacebook() {
        facebookScopes = null;
        LoginManager.getInstance().logOut();
    }

    public Authentication connectGoogle(@Nullable List<String> scopes) {
        googleScopes = scopes;
        network = GOOGLE;
        return this;
    }

    public void disconnectGoogle() {
        googleScopes = null;
        clearCookies();
    }

    public Authentication connectTwitter() {
        twitterScopes = Collections.<String>emptyList();
        network = TWITTER;
        return this;
    }

    public void disconnectTwitter() {
        twitterScopes = null;
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
        clearCookies();
    }

    public Authentication connectInstagram(@Nullable List<String> scopes) {
        instagramScopes = scopes;
        network = INSTAGRAM;
        return this;
    }

    public void disconnectInstagram() {
        instagramScopes = null;
        clearCookies();
    }

    public List<String> getFacebookScopes() {
        return facebookScopes;
    }

    public List<String> getGoogleScopes() {
        return googleScopes;
    }

    public List<String> getTwitterScopes() {
        return twitterScopes;
    }

    public List<String> getInstagramScopes() {
        return instagramScopes;
    }

    private void initFacebook(Context appContext) {
        String fbAppId = Utils.getMetaDataValue(appContext, appContext.getString(R.string.vv_com_applikeysolutions_socialmanager_facebookAppId));
        if (!TextUtils.isEmpty(fbAppId)) {
            FacebookSdk.setApplicationId(fbAppId);
        }
    }

    private void initTwitter(Context appContext) {
        String consumerKey = Utils.getMetaDataValue(appContext, appContext.getString(R.string.vv_com_applikeysolutions_library_twitterConsumerKey));
        String consumerSecret = Utils.getMetaDataValue(appContext, appContext.getString(R.string.vv_com_applikeysolutions_library_twitterConsumerSecret));

        if (consumerKey != null && consumerSecret != null) {
            TwitterConfig twitterConfig = new Builder(appContext)
                    .twitterAuthConfig(new TwitterAuthConfig(consumerKey, consumerSecret))
                    .build();
            Twitter.initialize(twitterConfig);
        }
    }

    private void clearCookies() {
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().removeAllCookies(null);
        } else {
            CookieSyncManager.createInstance(appContext);
            CookieManager.getInstance().removeAllCookie();
        }
    }
}
