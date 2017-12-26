package com.applikeysolutions.library;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresPermission;
import android.support.annotation.StringRes;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.util.UUID;

public class DeviceUtils {
private DeviceUtils() {
}

    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getDeviceModel() {
        return Build.MODEL;
    }

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getApplicationContext().getContentResolver(), "android_id");
    }

    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    @RequiresPermission("android.permission.ACCESS_WIFI_STATE")
    public static String getMacAddress(Context context) {
        @SuppressLint("WrongConstant") WifiManager wifi = (WifiManager)context.getApplicationContext().getSystemService("wifi");
        WifiInfo info = wifi.getConnectionInfo();
        String macAddress = info.getMacAddress();
        return macAddress == null?"":macAddress;
    }

    public static boolean isFacebookInstalled(Context context) {
        Intent facebook = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
        return facebook != null;
    }

    public static boolean isTwitterInstalled(Context context) {
        Intent facebook = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.twitter.android");
        return facebook != null;
    }

    public static boolean isInstagramInstalled(Context context) {
        Intent facebook = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.instagram.android");
        return facebook != null;
    }

    public static boolean isWechatInstalled(Context context) {
        Intent facebook = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
        return facebook != null;
    }

    @SuppressLint("WrongConstant") public static void sendSMS(Context context, String phoneNumber, String smsContent) {
        if(!TextUtils.isEmpty(phoneNumber) && TextUtils.isDigitsOnly(phoneNumber)) {
            Uri uri = Uri.parse("smsto:" + phoneNumber);
            Intent it = new Intent("android.intent.action.SENDTO", uri);
            it.putExtra("sms_body", smsContent);
            it.setFlags(268435456);
            context.startActivity(it);
        }
    }

    @SuppressLint("WrongConstant") public static void makePhoneCall(Context context, String phoneNumber) {
        if(!TextUtils.isEmpty(phoneNumber) && TextUtils.isDigitsOnly(phoneNumber)) {
            Uri uri = Uri.parse("tel:" + phoneNumber);
            Intent intent = new Intent("android.intent.action.VIEW", uri);
            intent.setFlags(268435456);
            context.startActivity(intent);
        }
    }

    public static void sendEmail(Context context, String[] emails, String subject, File attachment) {
        Intent emailIntent = new Intent("android.intent.action.SEND");
        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra("android.intent.extra.EMAIL", emails);
        if(attachment != null) {
            emailIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(attachment));
        }

        emailIntent.putExtra("android.intent.extra.SUBJECT", subject);
        context.startActivity(Intent.createChooser(emailIntent, ""));
    }

    public static void sendEmail(Context context, String[] emails, String subject) {
        sendEmail(context, emails, subject, (File)null);
    }

    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    public static boolean isNetworkConnected(Context context) {
        @SuppressLint("WrongConstant") ConnectivityManager cm = (ConnectivityManager)context.getApplicationContext().getSystemService("connectivity");
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    public static boolean isUsingWifi(Context context) {
        @SuppressLint("WrongConstant") ConnectivityManager cm = (ConnectivityManager)context.getApplicationContext().getSystemService("connectivity");
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isAvailable() && activeNetwork.getType() == 1;
    }

    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    public static boolean isUsingMobileData(Context context) {
        @SuppressLint("WrongConstant") ConnectivityManager cm = (ConnectivityManager)context.getApplicationContext().getSystemService("connectivity");
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isAvailable() && activeNetwork.getType() == 0;
    }

    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    public static boolean isUsing4G(Context context) {
        @SuppressLint("WrongConstant") ConnectivityManager cm = (ConnectivityManager)context.getApplicationContext().getSystemService("connectivity");
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isAvailable() && activeNetwork.getSubtype() == 13;
    }

    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    public static void showNetworkDialogIfNotEnable(final Context context, @StringRes int msg, @StringRes int posText, @StringRes int negText) {
        if(!isNetworkConnected(context)) {
            (new AlertDialog.Builder(context)).setMessage(msg).setPositiveButton(posText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent(Build.VERSION.SDK_INT > 10?"android.settings.SETTINGS":"android.settings.WIRELESS_SETTINGS");
                    context.startActivity(intent);
                }
            }).setNegativeButton(negText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }

    }

    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    public static boolean isLocationEnabled(Context context) {
        @SuppressLint("WrongConstant") LocationManager lm = (LocationManager)context.getApplicationContext().getSystemService("location");
        boolean gpsEnabled = lm.getAllProviders().contains("gps") && lm.isProviderEnabled("gps");
        boolean networkEnabled = lm.getAllProviders().contains("network") && lm.isProviderEnabled("network");
        return gpsEnabled | networkEnabled;
    }

    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    public static void showLocationDialogIfNotEnable(final Context context, @StringRes int msg, @StringRes int posText, @StringRes int negText) {
        if(!isLocationEnabled(context)) {
            (new AlertDialog.Builder(context)).setMessage(msg).setPositiveButton(posText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
                    context.startActivity(intent);
                }
            }).setNegativeButton(negText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }

    }

    @RequiresPermission(
            allOf = {"android.permission.BLUETOOTH", "android.permission.BLUETOOTH_ADMIN"}
    )
    public static void setBluetoothEnabled(boolean enabled) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(enabled) {
            adapter.enable();
        } else {
            adapter.disable();
        }

    }

    @RequiresPermission("android.permission.BLUETOOTH")
    public static boolean isBluetoothEnabled() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter.isEnabled();
    }

    public static UUID getBluetoothDeviceID(byte[] recordData) {
        return UUID.nameUUIDFromBytes(recordData);
    }

    @RequiresPermission("android.permission.READ_PHONE_STATE")
    public static boolean isPhone(Context context) {
        @SuppressLint("WrongConstant") TelephonyManager tm = (TelephonyManager)context.getSystemService("phone");
        return tm != null && tm.getPhoneType() != 0;
    }

    @RequiresPermission("android.permission.READ_PHONE_STATE")
    public static String getIMEI(Context context) {
        @SuppressLint("WrongConstant") TelephonyManager tm = (TelephonyManager)context.getSystemService("phone");
        return tm != null?tm.getDeviceId():"";
    }

    @RequiresPermission("android.permission.READ_PHONE_STATE")
    public static boolean hasSimCard(Context context) {
        @SuppressLint("WrongConstant") TelephonyManager tm = (TelephonyManager)context.getSystemService("phone");
        return tm != null && tm.getSimState() == 5;
    }

    @RequiresPermission("android.permission.READ_PHONE_STATE")
    public static String getSimCardOperatorName(Context context) {
        @SuppressLint("WrongConstant") TelephonyManager tm = (TelephonyManager)context.getSystemService("phone");
        return tm != null?tm.getSimOperatorName():"";
    }

    @RequiresPermission("android.permission.READ_PHONE_STATE")
    public static String getSimCardOperatorCode(Context context) {
        @SuppressLint("WrongConstant") TelephonyManager tm = (TelephonyManager)context.getSystemService("phone");
        return tm != null?tm.getSimOperator():"";
    }

    /*public static String getPhoneStatus(Context context) {
        @SuppressLint("WrongConstant") TelephonyManager tm = (TelephonyManager)context.getSystemService("phone");
        String str = "";
        str = str + "DeviceId(IMEI) = " + tm.getDeviceId() + "\n";
        str = str + "DeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion() + "\n";
        str = str + "Line1Number = " + tm.getLine1Number() + "\n";
        str = str + "NetworkCountryIso = " + tm.getNetworkCountryIso() + "\n";
        str = str + "NetworkOperator = " + tm.getNetworkOperator() + "\n";
        str = str + "NetworkOperatorName = " + tm.getNetworkOperatorName() + "\n";
        str = str + "NetworkType = " + tm.getNetworkType() + "\n";
        str = str + "PhoneType = " + tm.getPhoneType() + "\n";
        str = str + "SimCountryIso = " + tm.getSimCountryIso() + "\n";
        str = str + "SimOperator = " + tm.getSimOperator() + "\n";
        str = str + "SimOperatorName = " + tm.getSimOperatorName() + "\n";
        str = str + "SimSerialNumber = " + tm.getSimSerialNumber() + "\n";
        str = str + "SimState = " + tm.getSimState() + "\n";
        str = str + "SubscriberId(IMSI) = " + tm.getSubscriberId() + "\n";
        str = str + "VoiceMailNumber = " + tm.getVoiceMailNumber() + "\n";
        return str;
    }*/

    @SuppressLint("WrongConstant") public static void setLandscape(Activity activity) {
        activity.setRequestedOrientation(0);
    }

    @SuppressLint("WrongConstant") public static void setPortrait(Activity activity) {
        activity.setRequestedOrientation(1);
    }

    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }

    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == 1;
    }

    public static boolean isSDCardEnable() {
        return "mounted".equals(Environment.getExternalStorageState());
    }
}

