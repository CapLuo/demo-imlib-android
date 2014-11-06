package io.rong.imlib.demo;

import io.rong.imlib.RongIMClient;


import android.annotation.SuppressLint;

import android.content.Context;

import android.net.Uri;


@SuppressLint("SimpleDateFormat")
public class Util {

    private static final String TAG = "Util";


    public static Uri obtainThumImageUri(Context context, RongIMClient.Message message) {
        Uri uri = Uri.parse("rong://" + context.getPackageName()).buildUpon().appendPath("image").appendPath("thum").appendPath(String.valueOf(message.getMessageId())).appendPath(message.getSenderUserId()).build();
        return uri;
    }

    public static Uri obtainVoiceUri(Context context, RongIMClient.Message message) {
        Uri uri = Uri.parse("rong://" + context.getPackageName()).buildUpon().appendPath("voice").appendPath(String.valueOf(message.getMessageId())).appendPath(message.getSenderUserId()).build();
        return uri;
    }


}
