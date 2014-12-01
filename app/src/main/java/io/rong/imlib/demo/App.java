package io.rong.imlib.demo;

import android.app.Application;

import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.demo.message.GroupInvitationNotification;
import io.rong.message.CommandNotificationMessage;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.ProfileNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * Created by zhjchen on 14/11/6.
 */
public class App extends Application {

//    public static final String APP_KEY = "lmxuhwagxs4hd";//dsafd
//    public static final String APP_KEY = "x18ywvqf8djnc";//yb0012
    public static final String APP_KEY = "c9kqb3rdk79pj";//测试key



    @Override
    public void onCreate() {
        super.onCreate();



        RongIMClient.init(this, APP_KEY, R.drawable.ic_launcher);

        try {
            RongIMClient.registerMessageType(TextMessage.class);
            RongIMClient.registerMessageType(VoiceMessage.class);
            RongIMClient.registerMessageType(ImageMessage.class);
            RongIMClient.registerMessageType(GroupInvitationNotification.class);
            RongIMClient.registerMessageType(ContactNotificationMessage.class);
            RongIMClient.registerMessageType(ProfileNotificationMessage.class);
            RongIMClient.registerMessageType(CommandNotificationMessage.class);

        } catch (AnnotationNotFoundException e) {
            e.printStackTrace();
        }


        DemoContext.getInstance().init(this);

//        System.loadLibrary("imdemo");
    }
}
