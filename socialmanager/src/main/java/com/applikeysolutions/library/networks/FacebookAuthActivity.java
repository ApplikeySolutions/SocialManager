package com.applikeysolutions.library.networks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.applikeysolutions.library.Authentication;
import com.applikeysolutions.library.AuthenticationActivity;
import com.applikeysolutions.library.NetworklUser;
import com.applikeysolutions.library.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FacebookAuthActivity extends AuthenticationActivity
        implements FacebookCallback<LoginResult>, GraphRequest.GraphJSONObjectCallback {

    private static final String PROFILE_PIC_URL = "https://graph.facebook.com/%1$s/picture?type=large";
    private static final List<String> DEFAULT_SCOPES = Arrays.asList("email", "public_profile");
    private CallbackManager callbackManager;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, FacebookAuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        if (Utils.isFacebookInstalled(this)) {
            LoginManager.getInstance().logOut();
        }
        LoginManager.getInstance().registerCallback(callbackManager, this);
        LoginManager.getInstance().logInWithReadPermissions(this, getScopes());
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override public void onSuccess(LoginResult loginResult) {
        showDialog();
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), this);
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,link");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override public void onCancel() {
        handCancel();
    }

    @Override public void onError(FacebookException error) {
        handleError(error);
        if (error instanceof FacebookAuthorizationException) {
            LoginManager.getInstance().logOut();
        }
    }

    @Override public void onCompleted(JSONObject object, GraphResponse response) {
        try {
            NetworklUser user = NetworklUser.newBuilder()
                    .userId(object.getString("id"))
                    .accessToken(AccessToken.getCurrentAccessToken().getToken())
                    .profilePictureUrl(String.format(PROFILE_PIC_URL, object.getString("id")))
                    .email(object.getString("email"))
                    .fullName(object.getString("name"))
                    .pageLink(object.getString("link"))
                    .build();

            dismissProgress();
            handleSuccess(user);
        } catch (JSONException e) {
            dismissProgress();
            handleError(e);
        }
    }

    @Override protected List<String> getAuthScopes() {
        return Authentication.getInstance().getFacebookScopes();
    }

    private List<String> getScopes() {
        List<String> scopes = new ArrayList<>(getAuthScopes());
        if (scopes.size() <= 0) {
            scopes = DEFAULT_SCOPES;
        } else if (!scopes.contains(DEFAULT_SCOPES.get(0))) {
            scopes.add(DEFAULT_SCOPES.get(0));
        } else if (!scopes.contains(DEFAULT_SCOPES.get(1))) {
            scopes.add(DEFAULT_SCOPES.get(1));
        }
        return scopes;
    }
}
