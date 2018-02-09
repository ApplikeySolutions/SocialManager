package com.applikeysolutions.library.networks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.applikeysolutions.library.Authentication;
import com.applikeysolutions.library.AuthenticationActivity;
import com.applikeysolutions.library.NetworklUser;
import com.applikeysolutions.library.R;
import com.applikeysolutions.library.Utils;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InstagramAuthActivity extends AuthenticationActivity {

    private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/?client_id=%1$s&redirect_uri=%2$s&response_type=code&scope=%3$s";
    private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    private static final String PAGE_LINK = "https://www.instagram.com/%1$s/";

    private String clientId;
    private String clientSecret;
    private String redirectUri;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, InstagramAuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clientId = Utils.getMetaDataValue(this, getString(R.string.vv_com_applikeysolutions_library_instagramClientId));
        clientSecret = Utils.getMetaDataValue(this, getString(R.string.vv_com_applikeysolutions_library_instagramClientSecret));
        redirectUri = Utils.getMetaDataValue(this, getString(R.string.vv_com_applikeysolutions_library_instagramRedirectUri));
        String scopes = TextUtils.join("+", getAuthScopes());

        String url = String.format(AUTH_URL, clientId, redirectUri, scopes);

        WebView webView = new WebView(this);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {

            @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showDialog();
            }

            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dismissProgress();
            }

            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(redirectUri)) {
                    getCode(Uri.parse(url));
                    return true;
                }
                return false;
            }
        });

        setContentView(webView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Authentication.getInstance().onLoginCancel();
    }

    @Override protected List<String> getAuthScopes() {
        return Authentication.getInstance().getInstagramScopes();
    }

    private void getCode(Uri uri) {
        String code = uri.getQueryParameter("code");
        if (code != null) {
            getAccessToken(code);
        } else if (uri.getQueryParameter("error") != null) {
            String errorMsg = uri.getQueryParameter("error_description");
            handleError(new Throwable(errorMsg));
        }
    }

    private void getAccessToken(String code) {
        RequestBody formBody = new FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("grant_type", "authorization_code")
                .add("redirect_uri", redirectUri)
                .add("code", code)
                .build();

        Request request = new Request.Builder().post(formBody)
                .url(TOKEN_URL)
                .build();

        showDialog();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, final IOException e) {
                runOnUiThread(() -> {
                    dismissProgress();
                    Authentication.getInstance().onLoginError(new RuntimeException("login fail"));
                    finish();
                });
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        dismissProgress();
                        handleError(new Throwable("Failed to get access token."));
                    });
                    return;
                }

                String body = response.body().string();
                InstagramUser igUser = new Gson().fromJson(body, InstagramUser.class);

                final NetworklUser user = NetworklUser.newBuilder()
                        .accessToken(igUser.accessToken)
                        .userId(igUser.user.id)
                        .username(igUser.user.username)
                        .fullName(igUser.user.fullName)
                        .pageLink(String.format(PAGE_LINK, igUser.user.username))
                        .profilePictureUrl(igUser.user.profilePicture)
                        .build();

                runOnUiThread(() -> {
                    dismissProgress();
                    handleSuccess(user);
                });
            }
        });
    }

    private static class InstagramUser {
        @SerializedName("access_token")
        String accessToken;
        @SerializedName("user")
        User user;

        static class User {
            @SerializedName("id")
            String id;
            @SerializedName("username")
            String username;
            @SerializedName("full_name")
            String fullName;
            @SerializedName("profile_picture")
            String profilePicture;
        }
    }
}
