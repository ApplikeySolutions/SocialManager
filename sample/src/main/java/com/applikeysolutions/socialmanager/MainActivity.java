package com.applikeysolutions.socialmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.applikeysolutions.library.Authentication;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final String FACEBOOK = "FACEBOOK";
    public static final String GOOGLE = "GOOGLE";
    public static final String TWITTER = "TWITTER";
    public static final String INSTAGRAM = "INSTAGRAM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_twitter)
    void connectTwitter() {
        Authentication
                .getInstance()
                .connectTwitter()
                .login()
                .subscribe(networklUser -> InfoActivity.start(MainActivity.this, TWITTER, networklUser),
                        throwable -> showToast(throwable.getMessage()));
    }

    @OnClick(R.id.button_google)
    void connectGoogle() {
        List<String> scopes = Arrays.asList(
                "https://www.googleapis.com/auth/youtube",
                "https://www.googleapis.com/auth/youtube.upload"
        );

        Authentication
                .getInstance()
                .connectGoogle(scopes)
                .login()
                .subscribe(networklUser -> InfoActivity.start(MainActivity.this, GOOGLE, networklUser),
                        throwable -> showToast(throwable.getMessage()));
    }

    @OnClick(R.id.button_facebook)
    void connectFacebook() {
        List<String> scopes = Arrays.asList("user_birthday", "user_friends");
        Authentication
                .getInstance()
                .connectFacebook(scopes)
                .login()
                .subscribe(user ->
                                InfoActivity.start(MainActivity.this, FACEBOOK, user),
                        throwable -> showToast(throwable.getMessage()));

    }

    @OnClick(R.id.button_instagram)
    void connectInstagram() {
        List<String> scopes = Arrays.asList("follower_list", "likes");
        Authentication
                .getInstance()
                .connectInstagram(scopes)
                .login()
                .subscribe(networklUser -> InfoActivity.start(this, INSTAGRAM, networklUser),
                        throwable -> showToast(throwable.getMessage()));
    }

    private void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
    }
}
