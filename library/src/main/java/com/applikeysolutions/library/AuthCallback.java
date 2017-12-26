package com.applikeysolutions.library;

public interface AuthCallback {

  void onSuccess(SocialUser socialUser);

  void onError(Throwable error);

  void onCancel();

}
