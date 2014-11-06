package io.rong.imlib.demo;

import android.app.Application;

import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.demo.message.ImageMessage;
import io.rong.imlib.demo.message.TextMessage;
import io.rong.imlib.demo.message.VoiceMessage;

/**
 * Created by zhjchen on 14/11/6.
 */
public class App extends Application {

    public static final String APP_KEY = "z3v5yqkbv8v30";


    @Override
    public void onCreate() {
        super.onCreate();


        RongIMClient.init(this, APP_KEY, R.drawable.ic_launcher);


        try {
            RongIMClient.registerMessageType(TextMessage.class);
            RongIMClient.registerMessageType(ImageMessage.class);
            RongIMClient.registerMessageType(VoiceMessage.class);
        } catch (AnnotationNotFoundException e) {
            e.printStackTrace();
        }


        DemoContext.getInstance().init(this);


    }
}
