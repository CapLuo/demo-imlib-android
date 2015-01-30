package io.rong.imlib.demo;

import android.app.Application;

import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.demo.message.GroupInvitationNotification;
import io.rong.message.InformationNotificationMessage;

/**
 * Created by zhjchen on 14/11/6.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         *  IMLib SDK调用第一步 初始化
         * context上下文
         */
        RongIMClient.init(this);

        try {
            //注册自定义消息类型
            RongIMClient.registerMessageType(GroupInvitationNotification.class);
            RongIMClient.registerMessageType(InformationNotificationMessage.class);

        } catch (AnnotationNotFoundException e) {
            e.printStackTrace();
        }

        DemoContext.getInstance().init(this);

    }
}
