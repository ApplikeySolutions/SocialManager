package com.applikeysolutions.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

import java.util.Collections;
import java.util.List;

public class SimpleAuth {

  private static final String KEY_IS_GOOGLE_DISCONNECT_REQUESTED = SimpleAuth.class.getName() + "KEY_IS_GOOGLE_DISCONNECT_REQUESTED";
  private static final String KEY_IS_GOOGLE_REVOKE_REQUESTED = SimpleAuth.class.getName() + "KEY_IS_GOOGLE_REVOKE_REQUESTED";

  @SuppressLint("StaticFieldLeak")
  private static SimpleAuth instance;
  private Context appContext;
  private AuthData facebookAuthData;
  private AuthData googleAuthData;
  private AuthData twitterAuthData;
  private AuthData instagramAuthData;

  private SimpleAuth() {
  }

  public static SimpleAuth getInstance() {
    if (instance == null) {
      synchronized (SimpleAuth.class) {
        if (instance == null) {
          instance = new SimpleAuth();
        }
      }
    }
    return instance;
  }

  static void init(Context context) {
    Context appContext = context.getApplicationContext();
    getInstance().appContext = appContext;
    getInstance().initFacebook(appContext);
    //getInstance().initTwitter(appContext);
  }

  private void initFacebook(Context appContext) {
    String fbAppId = AppUtils.getMetaDataValue(appContext, appContext.getString(R.string.vv_com_applikeysolutions_socialmanager_facebookAppId));
    if (!StringUtils.isEmpty(fbAppId)) {
        Log.e("test", "initFacebook: " + fbAppId );
      FacebookSdk.setApplicationId(fbAppId);
      FacebookSdk.sdkInitialize(appContext);
   //   FacebookSdk.setWebDialogTheme();
    }
  }

 /* private void initTwitter(Context appContext) {
    String consumerKey = AppUtils.getMetaDataValue(appContext, appContext.getString(R.string.sa_com_jaychang_sa_twitterConsumerKey));
    String consumerSecret = AppUtils.getMetaDataValue(appContext, appContext.getString(R.string.sa_com_jaychang_sa_twitterConsumerSecret));

    if (consumerKey != null && consumerSecret != null) {
      TwitterConfig twitterConfig = new TwitterConfig.Builder(appContext)
        .twitterAuthConfig(new TwitterAuthConfig(consumerKey, consumerSecret))
        .build();
      Twitter.initialize(twitterConfig);
    }
  }
*/
  public void connectFacebook(@Nullable List<String> scopes, @NonNull AuthCallback listener) {
    facebookAuthData = new AuthData(scopes, listener);
    FacebookAuthActivity.start(appContext);
  }

  public void connectFacebook(@NonNull AuthCallback listener) {
    connectFacebook(Collections.<String>emptyList(), listener);
  }

  public void disconnectFacebook() {
    facebookAuthData = null;
    LoginManager.getInstance().logOut();
  }

  public void revokeFacebook(final RevokeCallback callback) {
    new GraphRequest(AccessToken.getCurrentAccessToken(),
      "/me/permissions/", null, HttpMethod.DELETE,
            new GraphRequest.Callback() {
                @Override public void onCompleted(GraphResponse graphResponse) {
                    SimpleAuth.this.disconnectFacebook();
                    if (callback != null) {
                        callback.onRevoked();
                    }
                }
            }
    ).executeAsync();
  }

  public void revokeFacebook() {
    revokeFacebook(null);
  }

  public void connectGoogle(@Nullable List<String> scopes, @NonNull AuthCallback listener) {
    googleAuthData = new AuthData(scopes, listener);
    GoogleAuthActivity.start(appContext);
  }

  public void connectGoogle(@NonNull AuthCallback listener) {
    connectGoogle(Collections.<String>emptyList(), listener);
  }

  public void disconnectGoogle() {
    googleAuthData = null;
    setGoogleDisconnectRequested(true);
  }

  public void revokeGoogle() {
    googleAuthData = null;
    setGoogleRevokeRequested(true);
  }

  /*public void connectTwitter(@NonNull AuthCallback listener) {
    twitterAuthData = new AuthData(Collections.emptyList(), listener);
    TwitterAuthActivity.start(appContext);
  }*/

  /*public void disconnectTwitter() {
    twitterAuthData = null;
    TwitterCore.getInstance().getSessionManager().clearActiveSession();
    clearCookies();
  }*/

 /* public void connectInstagram(@Nullable List<String> scopes, @NonNull AuthCallback listener) {
    instagramAuthData = new AuthData(scopes, listener);
    InstagramAuthActivity.start(appContext);
  }*/

  /*public void connectInstagram(@NonNull AuthCallback listener) {
    connectInstagram(Collections.emptyList(), listener);
  }*/

  public void disconnectInstagram() {
    instagramAuthData = null;
    clearCookies();
  }

  private void clearCookies() {
    if (Build.VERSION.SDK_INT >= 21) {
      CookieManager.getInstance().removeAllCookies(null);
    } else {
      CookieSyncManager.createInstance(appContext);
      CookieManager.getInstance().removeAllCookie();
    }
  }

  AuthData getFacebookAuthData() {
    return facebookAuthData;
  }

  AuthData getGoogleAuthData() {
    return googleAuthData;
  }

  AuthData getTwitterAuthData() {
    return twitterAuthData;
  }

  AuthData getInstagramAuthData() {
    return instagramAuthData;
  }

  boolean isGoogleDisconnectRequested() {
    return PreferenceUtils.getBoolean(appContext, KEY_IS_GOOGLE_DISCONNECT_REQUESTED);
  }

  void setGoogleDisconnectRequested(boolean isRequested) {
    PreferenceUtils.saveBoolean(appContext, KEY_IS_GOOGLE_DISCONNECT_REQUESTED, isRequested);
  }

  boolean isGoogleRevokeRequested() {
    return PreferenceUtils.getBoolean(appContext, KEY_IS_GOOGLE_REVOKE_REQUESTED);
  }

  void setGoogleRevokeRequested(boolean isRequested) {
    PreferenceUtils.saveBoolean(appContext, KEY_IS_GOOGLE_REVOKE_REQUESTED, isRequested);
  }
}
