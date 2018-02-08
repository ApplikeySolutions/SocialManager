package com.applikeysolutions.library;

import android.os.Parcel;
import android.os.Parcelable;

public class NetworklUser implements Parcelable {

    public static final Creator<NetworklUser> CREATOR = new Creator<NetworklUser>() {
        @Override public NetworklUser createFromParcel(Parcel source) {
            return new NetworklUser(source);
        }

        @Override public NetworklUser[] newArray(int size) {
            return new NetworklUser[size];
        }
    };
    private String userId;
    private String accessToken;
    private String profilePictureUrl;
    private String userName;
    private String fullName;
    private String email;
    private String pageLink;
    private String tokenId;
    private String serverAuthCode;

    private NetworklUser(Builder builder) {
        userId = builder.userId;
        accessToken = builder.accessToken;
        profilePictureUrl = builder.profilePictureUrl;
        userName = builder.username;
        fullName = builder.fullName;
        email = builder.email;
        pageLink = builder.pageLink;
        tokenId = builder.tokenId;
        serverAuthCode = builder.serverAuthCode;
    }

    protected NetworklUser(Parcel in) {
        this.userId = in.readString();
        this.accessToken = in.readString();
        this.profilePictureUrl = in.readString();
        this.userName = in.readString();
        this.fullName = in.readString();
        this.email = in.readString();
        this.pageLink = in.readString();
        this.tokenId = in.readString();
        this.serverAuthCode = in.readString();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworklUser that = (NetworklUser) o;

        return userId != null ? userId.equals(that.userId) : that.userId == null;
    }

    @Override public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder("NetworklUser").append("\n\n");
        sb.append("userId=").append(userId).append("\n\n");
        sb.append("userName=").append(userName).append("\n\n");
        sb.append("fullName=").append(fullName).append("\n\n");
        sb.append("email=").append(email).append("\n\n");
        sb.append("profilePictureUrl=").append(profilePictureUrl).append("\n\n");
        sb.append("pageLink=").append(pageLink).append("\n\n");
        sb.append("accessToken=").append(accessToken).append("\n\n");
        sb.append("tokenId=").append(tokenId).append("\n\n");
        sb.append("serverAuthCode=").append(serverAuthCode).append("\n\n");
        return sb.toString();
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.accessToken);
        dest.writeString(this.profilePictureUrl);
        dest.writeString(this.userName);
        dest.writeString(this.fullName);
        dest.writeString(this.email);
        dest.writeString(this.pageLink);
        dest.writeString(this.tokenId);
        dest.writeString(this.serverAuthCode);
    }

    public static final class Builder {
        private String userId;
        private String accessToken;
        private String profilePictureUrl;
        private String username;
        private String fullName;
        private String email;
        private String pageLink;
        private String tokenId;
        private String serverAuthCode;

        private Builder() {
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder profilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder pageLink(String pageLink) {
            this.pageLink = pageLink;
            return this;
        }

        public Builder tokenId(String tokenId) {
            this.tokenId = tokenId;
            return this;
        }

        public Builder serverAuthCode(String serverAuthCode) {
            this.serverAuthCode = serverAuthCode;
            return this;
        }

        public NetworklUser build() {
            return new NetworklUser(this);
        }
    }
}
