package com.applikeysolutions.library.networks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.applikeysolutions.library.Authentication;
import com.applikeysolutions.library.AuthenticationActivity;
import com.applikeysolutions.library.NetworklUser;
import com.applikeysolutions.library.R;
import com.applikeysolutions.library.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class GoogleAuthActivity extends AuthenticationActivity {

    private static final int RC_SIGN_IN = 1000;
    private GoogleSignInClient googleApiClient;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, GoogleAuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions.Builder gsoBuilder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Utils.getMetaDataValue(this, getString(R.string.vv_com_applikeysolutions_library_googleWebClientId)))
                .requestServerAuthCode(Utils.getMetaDataValue(this, getString(R.string.vv_com_applikeysolutions_library_googleWebClientId)))
                .requestId()
                .requestProfile()
                .requestEmail();

        setupScopes(gsoBuilder);

        googleApiClient = GoogleSignIn.getClient(this, gsoBuilder.build());
        signIn();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != RC_SIGN_IN || resultCode != RESULT_OK) {
            return;
        }
        final Task<GoogleSignInAccount> signedInAccountFromIntent = GoogleSignIn.getSignedInAccountFromIntent(data);
        handleSignInResult(signedInAccountFromIntent);
    }

    protected List<String> getAuthScopes() {
        return Authentication.getInstance().getGoogleScopes();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> result) {
        try {
            GoogleSignInAccount acct = result.getResult(ApiException.class);
            NetworklUser user = NetworklUser.newBuilder()
                    .userId(acct.getId())
                    .profilePictureUrl(acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : "")
                    .email(acct.getEmail())
                    .fullName(acct.getDisplayName())
                    .tokenId(acct.getIdToken())
                    .serverAuthCode(acct.getServerAuthCode())
                    .build();
            handleSuccess(user);
        } catch (ApiException e) {
            Throwable error = new Throwable(result.getException());
            handleError(error);
        }
    }

    private void signIn() {
        Intent signInIntent = googleApiClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void setupScopes(GoogleSignInOptions.Builder builder) {
        List<Scope> scopes = getScopes();
        if (scopes.size() == 1) {
            builder.requestScopes(scopes.get(0));
        } else if (scopes.size() > 1) {
            List<Scope> restScopes = scopes.subList(1, scopes.size());
            Scope[] restScopesArray = new Scope[restScopes.size()];
            restScopesArray = scopes.toArray(restScopesArray);
            builder.requestScopes(scopes.get(0), restScopesArray);
        }
    }

    private List<Scope> getScopes() {
        List<Scope> scopes = new ArrayList<>();
        for (String str : getAuthScopes()) {
            scopes.add(new Scope(str));
        }
        return scopes;
    }

    private void signOut() {
        googleApiClient.signOut()
                .addOnCompleteListener(this, task -> {
                });
    }
}
