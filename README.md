[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/AppliKey/MoonRefresh/blob/master/LICENSE)
[![Twitter](https://img.shields.io/badge/contact-@Applikey_-blue.svg?style=flat)](https://twitter.com/Applikey_)

Made by [Applikey Solutions](https://applikeysolutions.com)

# Table of Contents
1. [Purpose](#purpose)
2. [Installation](#installation)
3. [Usage](#usage)
   * [Configure app level build.gradle:](#configure-app-level-build.gradle)
   * [Configure your Application class](#configure your Application class)
   * [Networks Connections](#networks connections)
4. [Demo](#demo)
5. [Release Notes](#release-notes)
6. [Contact Us](#contact-us)
7. [License](#license)


# Purpose

A reactive android library to easily implement social login (Facebook, Google, Twitter, Instagram) into your android project.

# Installation

```groovy
implementation 'com.github.applikey:socialmanager:1.0.0'
```

# Usage

### Configure app level build.gradle:
```groovy
android.buildTypes.debug.manifestPlaceholders = [
        twitterConsumerKey   : "Set your twitter application consumer key",
        twitterConsumerSecret: "Set your twitter application secret key",
        googleWebClientId    : "Set your google application web client id",
        facebookAppId        : "Set your facebook application id",
        instagramClientId    : "Set your instagram application client id",
        instagramClientSecret: "Set your instagram application client secret key id",
        instagramRedirectUri : "Set your instagram application redirect uri"
]
```
If you don't need some social networks, leave fields empty, but don't remove.

### Configure your Application class:
```java
public class MyApp extends Application {
    @Override public void onCreate() {
        super.onCreate();
        Authentication.init(this);
    }
}
```

### Networks Connections

Twitter connection:
```java
    void connectTwitter() {
        Authentication
                .getInstance()
                .connectTwitter()
                .login()
                .subscribe(networklUser -> {}),
                        throwable -> {});
    }
```
Google connection:
```java
    void connectGoogle() {
        List<String> scopes = Arrays.asList(
                "https://www.googleapis.com/auth/youtube",
                "https://www.googleapis.com/auth/youtube.upload"
        );

        Authentication
                .getInstance()
                .connectGoogle(scopes)
                .login()
                ..subscribe(networklUser -> {}),
                        throwable -> {});
    }
```
Facebook connection:
```java
    void connectFacebook() {
        List<String> scopes = Arrays.asList("user_birthday", "user_friends");
        Authentication
                .getInstance()
                .connectFacebook(scopes)
                .login()
                .subscribe(networklUser -> {}),
                        throwable -> {});

    }
```
Instagram connection:
```java
    void connectInstagram() {
        List<String> scopes = Arrays.asList("follower_list", "likes");
        Authentication
                .getInstance()
                .connectInstagram(scopes)
                .login()
                .subscribe(networklUser -> {}),
                        throwable -> {});
    }
```
# Release Notes

Version 1.0.0

- Release version.

# Contact Us

You can always contact us via github@applikey.biz We are open for any inquiries regarding our libraries and controls, new open-source projects and other ways of contributing to the community. If you have used our component in your project we would be extremely happy if you write us your feedback and let us know about it!

# License

    MIT License

    Copyright (c) 2018 Applikey Solutions

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
