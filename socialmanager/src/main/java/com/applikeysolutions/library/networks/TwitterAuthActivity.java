package com.applikeysolutions.library.networks;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.applikeysolutions.library.Authentication;
import com.applikeysolutions.library.AuthenticationActivity;
import com.applikeysolutions.library.NetworklUser;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import java.util.List;

import retrofit2.Call;

public class TwitterAuthActivity extends AuthenticationActivity {

    private static final String PROFILE_PIC_URL = "https://twitter.com/%1$s/profile_image?size=original";
    private static final String PAGE_LINK = "https://twitter.com/%1$s";

    private TwitterAuthClient twitterAuthClient;
    private Callback<TwitterSession> callback = new Callback<TwitterSession>() {
        @Override public void success(Result<TwitterSession> result) {
            handleSuccess(result.data);
        }

        @Override public void failure(TwitterException exception) {
            handleError(exception);
        }
    };

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, TwitterAuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterSession activeSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (activeSession != null) {
            handleSuccess(activeSession);
        } else {
            getTwitterAuthClient().authorize(this, callback);
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            handleCancel();
            return;
        }
        if (requestCode == getTwitterAuthClient().getRequestCode()) {
            getTwitterAuthClient().onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void handleCancel() {
        getTwitterAuthClient().cancelAuthorize();
        super.handCancel();
    }

    @Override protected List<String> getAuthScopes() {
        return Authentication.getInstance().getTwitterScopes();
    }

    private TwitterAuthClient getTwitterAuthClient() {
        if (twitterAuthClient == null) {
            synchronized (TwitterAuthActivity.class) {
                if (twitterAuthClient == null) {
                    twitterAuthClient = new TwitterAuthClient();
                }
            }
        }
        return twitterAuthClient;
    }

    private void handleSuccess(final TwitterSession session) {
        showDialog();
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        AccountService accountService = twitterApiClient.getAccountService();

        Call<User> call = accountService.verifyCredentials(false, true, true);
        call.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> userResult) {
                dismissProgress();
                User data = userResult.data;
                NetworklUser user = NetworklUser.newBuilder().userId(String.valueOf(data.getId()))
                        .accessToken(session.getAuthToken().token)
                        .profilePictureUrl(String.format(PROFILE_PIC_URL, data.screenName))
                        .email(data.email != null ? data.email : "")
                        .fullName(data.name)
                        .username(data.screenName)
                        .pageLink(String.format(PAGE_LINK, data.screenName))
                        .build();

                handleSuccess(user);
            }

            public void failure(TwitterException error) {
                dismissProgress();
                handleError(error);
            }
        });
    }
}
