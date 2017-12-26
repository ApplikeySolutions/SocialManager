package com.applikeysolutions.library;

import android.app.ProgressDialog;
import android.content.Context;

class DialogUtils {

  static ProgressDialog createLoadingDialog(Context context) {
    ProgressDialog loadingDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT);
    loadingDialog.setCancelable(false);
    loadingDialog.setMessage("Loading....");
    return loadingDialog;
  }

}
