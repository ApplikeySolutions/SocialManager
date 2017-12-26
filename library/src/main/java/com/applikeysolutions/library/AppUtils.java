package com.applikeysolutions.library;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AppUtils {
    public AppUtils() {
    }

    public static String getAppName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0?applicationInfo.nonLocalizedLabel.toString():context.getString(stringId);
    }

    @Nullable
    public static Drawable getAppIcon(Context context) {
        return getAppIcon(context, context.getPackageName());
    }

    @Nullable
    public static Drawable getAppIcon(Context context, String packageName) {
        if(StringUtils.isBlank(packageName)) {
            return null;
        } else {
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(packageName, 0);
                return pi == null?null:pi.applicationInfo.loadIcon(pm);
            } catch (PackageManager.NameNotFoundException var4) {
                var4.printStackTrace();
                return null;
            }
        }
    }

    public static String getVersionName(Context context) {
        try {
            Context appContext = context.getApplicationContext();
            PackageInfo pInfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException var3) {
            var3.printStackTrace();
            return "";
        }
    }

    public static int getVersionCode(Context context) {
        try {
            Context appContext = context.getApplicationContext();
            PackageInfo pInfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException var3) {
            var3.printStackTrace();
            return -1;
        }
    }

    public static Intent getLauncherIntent(Context context) {
        return context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
    }

    public static void changeLanguage(Context context, Locale locale, boolean restart) {
        Locale.setDefault(locale);
        Context appContext = context.getApplicationContext();
        Resources resources = appContext.getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        config.locale = locale;
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        if(restart) {
            Intent refresh = getLauncherIntent(context);
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(refresh);
        }

    }

    public static int dp2px(Context context, int dp) {
        float density = context.getApplicationContext().getApplicationContext().getResources().getDisplayMetrics().density;
        return (int)((float)dp * density + 0.5F);
    }

    public static int px2dp(Context context, int px) {
        float density = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int)((float)px / density + 0.5F);
    }

    public static int sp2px(Context context, float sp) {
        float fontScale = context.getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
        return (int)(sp * fontScale + 0.5F);
    }

    public static int px2sp(Context context, float px) {
        float fontScale = context.getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
        return (int)(px / fontScale + 0.5F);
    }

    public static double getScreenInch(Context context) {
        DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float widthDpi = metrics.xdpi;
        float heightDpi = metrics.ydpi;
        float widthInches = (float)widthPixels / widthDpi;
        float heightInches = (float)heightPixels / heightDpi;
        return Math.sqrt((double)(widthInches * widthInches + heightInches * heightInches));
    }

    public static int getScreenWidthPixels(Context context) {
        return context.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeightPixels(Context context) {
        return context.getApplicationContext().getResources().getDisplayMetrics().heightPixels;
    }

    @TargetApi(17)
    public static int getFullScreenHeightPixels(Context context) {
        WindowManager winMgr = (WindowManager)context.getApplicationContext().getSystemService("window");
        Display display = winMgr.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        int height;
        if(Build.VERSION.SDK_INT >= 17) {
            display.getRealMetrics(dm);
            height = dm.heightPixels;
        } else {
            try {
                Method method = Class.forName("android.view.Display").getMethod("getRealMetrics", new Class[]{DisplayMetrics.class});
                method.invoke(display, new Object[]{dm});
                height = dm.heightPixels;
            } catch (Exception var6) {
                display.getMetrics(dm);
                height = dm.heightPixels;
            }
        }

        return height;
    }

    public static int getStatusBarHeightPixels(Context context) {
        int result = -1;
        Resources resources = context.getApplicationContext().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if(resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }

        return result;
    }

    public static int getNavigationBarHeightPixels(Context context) {
        int result = -1;
        Resources resources = context.getApplicationContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if(resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }

        return result;
    }

    public static void showKeyboard(Context context, View view) {
        if(view.requestFocus()) {
            @SuppressLint("WrongConstant") InputMethodManager imm = (InputMethodManager)context.getApplicationContext().getSystemService("input_method");
            imm.showSoftInput(view, 1);
        }

    }

    public static void hideKeyboard(Context context, View view) {
        @SuppressLint("WrongConstant") InputMethodManager imm = (InputMethodManager)context.getApplicationContext().getSystemService("input_method");
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboardWhenTouchOutside(final Activity context) {
        if(context.getWindow() != null) {
            context.getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ResourceType") public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm = (InputMethodManager)context.getSystemService("input_method");
                    if(context.getCurrentFocus() != null) {
                        imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
                    } else {
                        imm.hideSoftInputFromWindow(context.findViewById(16908290).getWindowToken(), 0);
                    }

                    return false;
                }
            });
        }
    }

    @TargetApi(19)
    public static void setStatusBarTranslucent(Activity activity) {
        if(Build.VERSION.SDK_INT >= 19 && activity.getWindow() != null) {
            activity.getWindow().addFlags(67108864);
        }
    }

    @TargetApi(19)
    public static void clearStatusBarTranslucent(Activity activity) {
        if(Build.VERSION.SDK_INT >= 19 && activity.getWindow() != null) {
            activity.getWindow().clearFlags(67108864);
        }
    }

    @TargetApi(21)
    public static void setStatusBarColor(Activity activity, @ColorRes int color) {
        if(Build.VERSION.SDK_INT >= 21 && activity.getWindow() != null) {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, color));
        }
    }

    @TargetApi(21)
    public static void setContentBehindStatusBar(Activity activity) {
        if(Build.VERSION.SDK_INT >= 21 && activity.getWindow() != null) {
            View decorView = activity.getWindow().getDecorView();
            int option = 1280;
            decorView.setSystemUiVisibility(option);
        }
    }

    @TargetApi(19)
    public static void setNavigationBarTranslucent(Activity activity) {
        if(Build.VERSION.SDK_INT >= 19 && activity.getWindow() != null) {
            activity.getWindow().addFlags(134217728);
        }
    }

    @TargetApi(19)
    public static void clearNavigationBarTranslucent(Activity activity) {
        if(Build.VERSION.SDK_INT >= 19 && activity.getWindow() != null) {
            activity.getWindow().clearFlags(134217728);
        }
    }

    @TargetApi(21)
    public static void setNavigationBarColor(Activity activity, @ColorRes int color) {
        if(Build.VERSION.SDK_INT >= 21 && activity.getWindow() != null) {
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, color));
        }
    }

    @TargetApi(16)
    public static void setFullscreenToggleable(Activity activity) {
        if(Build.VERSION.SDK_INT >= 16 && activity.getWindow() != null) {
            View decorView = activity.getWindow().getDecorView();
            int option = 6;
            decorView.setSystemUiVisibility(option);
        }
    }

    public static void setFullscreen(Activity activity) {
        activity.requestWindowFeature(1);
        activity.getWindow().setFlags(1024, 1024);
    }

    public static void openUrl(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        context.startActivity(intent);
    }

    public static void openFbMessenger(Context context, String fbUserId) {
        context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("fb://messaging/" + fbUserId)));
    }

    @SuppressLint("WrongConstant") public static void openLocalImage(Context context, String imagePath) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(268435456);
        Uri uri = Uri.fromFile(new File(imagePath));
        intent.setDataAndType(uri, "image/*");
        context.startActivity(intent);
    }

    @SuppressLint("WrongConstant") public static void openLocalVideo(Context context, String videoPath) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(67108864);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(videoPath));
        intent.setDataAndType(uri, "video/*");
        context.startActivity(intent);
    }

    private static File createImageFile(Context context) throws IOException {
        String imageFileName = "Image_" + UUID.randomUUID() + ".jpg";
        return new File(context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + imageFileName);
    }

    @TargetApi(18)
    public static void pickPhotoFromAlbum(Activity context, int requestCode) {
        Intent intent = new Intent();
        if(Build.VERSION.SDK_INT >= 18) {
            intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
        }

        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        context.startActivityForResult(Intent.createChooser(intent, ""), requestCode);
    }

    @TargetApi(18)
    public static List<Uri> getPhotosFromAlbum(Context context, Intent data) {
        if(Build.VERSION.SDK_INT < 18) {
            return Collections.emptyList();
        } else {
            ClipData clipData = data.getClipData();
            if(clipData == null) {
                return Collections.emptyList();
            } else {
                String[] filePathColumn = new String[]{"_data"};
                ArrayList<Uri> uris = new ArrayList();

                for(int i = 0; i < clipData.getItemCount(); ++i) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    uris.add(uri);
                    Cursor cursor = context.getApplicationContext().getContentResolver().query(uri, filePathColumn, (String)null, (String[])null, (String)null);
                    cursor.moveToFirst();
                    cursor.close();
                }

                return uris;
            }
        }
    }

    public static void takeVideo(Activity activity, int requestCode) {
        Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
        if(intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intent, requestCode);
        }

    }

    public static void takeVideo(Fragment fragment, int requestCode) {
        Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
        if(intent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
            fragment.startActivityForResult(intent, requestCode);
        }

    }

    public static Uri getTakenVideoUri(Intent intent) {
        return intent.getData();
    }

   /* public static void saveImageToAlbum(final Activity activity, final File file, final AppUtils.OnImageSaveListener listener) {
        AsyncTask.execute(new Runnable() {
            public void run() {
                try {
                    String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                    final File imageFile = new File(root, UUID.randomUUID().toString() + ".jpg");
                    if(!imageFile.exists()) {
                        imageFile.createNewFile();
                    }

                    FileUtils.copyFile(file, imageFile);
                    AppUtils.scanFile(activity, imageFile);
                    if(listener != null) {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                listener.onImageSaved(imageFile);
                            }
                        });
                    }
                } catch (IOException var3) {
                    var3.printStackTrace();
                }

            }
        });
    }*/

    public static void scanFile(Context context, File file) {
        MediaScannerConnection.scanFile(context.getApplicationContext(), new String[]{file.toString()}, (String[])null, (MediaScannerConnection.OnScanCompletedListener)null);
    }

   /* public static void saveImageToAppInternalDir(final Activity activity, final File file, final AppUtils.OnImageSaveListener listener) {
        AsyncTask.execute(new Runnable() {
            public void run() {
                try {
                    String root = Environment.getExternalStorageDirectory().toString();
                    final File imageFile = new File(root, UUID.randomUUID().toString() + ".jpg");
                    if(!imageFile.exists()) {
                        imageFile.createNewFile();
                    }

                    FileUtils.copyFile(file, imageFile);
                    if(listener != null) {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                listener.onImageSaved(imageFile);
                            }
                        });
                    }
                } catch (IOException var3) {
                    var3.printStackTrace();
                }

            }
        });
    }*/

    public static void copyText(Context context, CharSequence text) {
        @SuppressLint("WrongConstant") ClipboardManager clipboard = (ClipboardManager)context.getApplicationContext().getSystemService("clipboard");
        ClipData clip = ClipData.newPlainText("text", text);
        clipboard.setPrimaryClip(clip);
    }

    public static void goToPlayStore(Context context, String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent("android.intent.action.VIEW", uri);

        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException var5) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
        }

    }

    @SuppressLint("WrongConstant") @Nullable
    public static String getMetaDataValue(Context context, String name) {
        ApplicationInfo ai;
        try {
            ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
        } catch (PackageManager.NameNotFoundException var4) {
            return null;
        }

        return (String)ai.metaData.get(name);
    }

    public interface OnImageSaveListener {
        void onImageSaved(File var1);
    }
}

