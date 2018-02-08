package com.applikeysolutions.socialmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.applikeysolutions.library.Authentication;
import com.applikeysolutions.library.NetworklUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InfoActivity extends AppCompatActivity {

    @BindView(R.id.network)
    TextView network;
    @BindView(R.id.userView)
    TextView userView;
    @BindView(R.id.disconnectButton)
    Button disconnectButton;

    private static final String EXTRA_USER = "EXTRA_USER";
    private static final String EXTRA_TYPE = "EXTRA_TYPE";

    private String type;

    public static void start(Context context, String type, NetworklUser socialUser) {
        Intent intent = new Intent(context, InfoActivity.class);
        intent.putExtra(EXTRA_USER, socialUser);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);

        NetworklUser socialUser = getIntent().getParcelableExtra(EXTRA_USER);
        userView.setText(socialUser.toString());
        type = getIntent().getStringExtra(EXTRA_TYPE);
        network.setText(type);
    }

    @OnClick(R.id.disconnectButton)
    void disconnect() {
        if (MainActivity.FACEBOOK.equals(type)) {
            Authentication.getInstance().disconnectFacebook();
        } else if (MainActivity.GOOGLE.equals(type)) {
            Authentication.getInstance().disconnectGoogle();
        } else if (MainActivity.TWITTER.equals(type)) {
            Authentication.getInstance().disconnectTwitter();
        } else if (MainActivity.INSTAGRAM.equals(type)) {
            Authentication.getInstance().disconnectInstagram();
        }
        finish();
    }
}
