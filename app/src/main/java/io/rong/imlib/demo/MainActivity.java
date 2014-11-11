package io.rong.imlib.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sea_monster.core.resource.ResourceManager;
import com.sea_monster.core.resource.model.Resource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.demo.message.GroupInvitationNotification;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;


public class MainActivity extends Activity implements View.OnClickListener, Handler.Callback {

    //    public static final String TOKEN = "dlZQXtLihq5mohiybibkaUmcbyeYIrXSDa0nFvL2mH/5zWOjUlJe+Aaszzzvx90roUr3nN+i0+Q=";
    public static final String TOKEN = "Nq0rE9bKGLY9XeG5fu2sHySW8Ko1xVl7xb1sdjGIzGe29n812klvkTQfbO0/JNSvTgXiktpF5d9W1IhzqUB0bg==";

    public static RongIMClient mRongIMClient;


    private Button connectButton;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;


    private String mUserId;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        connectButton = (Button) findViewById(R.id.connect_button);
        button1 = (Button) findViewById(android.R.id.button1);
        button2 = (Button) findViewById(android.R.id.button2);
        button3 = (Button) findViewById(android.R.id.button3);
        button4 = (Button) findViewById(R.id.group_invitation_notification);

        connectButton.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);

        mHandler = new Handler(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.connect_button:

                try {

                    mRongIMClient = RongIMClient.connect(TOKEN, new RongIMClient.ConnectCallback() {

                        @Override
                        public void onSuccess(String userId) {
                            Log.d("App", "--connect--onSuccess----userId---" + userId);

                            mUserId = userId;
                            DemoContext.getInstance().userId = mUserId;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "连接成功！", Toast.LENGTH_LONG).show();
                                    connectButton.setText("连接服务器成功!");
                                }
                            });
                        }

                        @Override
                        public void onError(ErrorCode errorCode) {
                            Log.d("App", "--connect--errorCode-------" + errorCode.getValue());

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "连接失败！", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });

                    DemoContext.getInstance().setRongIMClient(mRongIMClient);
                    DemoContext.getInstance().registerMessage();

                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;

            case android.R.id.button1:
                TextMessage textMessage = TextMessage.obtain("明天不上班。。。。。。今天加班到天亮！！！！发送时间:" + System.currentTimeMillis());
                sendMessage(textMessage);

                break;
            case android.R.id.button2:

                try {

                    InputStream is = getResources().openRawResource(R.raw.pic);
                    String path = DemoContext.getInstance().getResourceDir();
                    FileUtil.createFile("pic", path);
                    Uri uri = Uri.parse(path + "/pic");

                    uri = FileUtil.writeByte(uri, FileUtil.toByteArray(is));

                    Bitmap bitmap = BitmapUtils.getResizedBitmap(this, uri, 240, 240);

                    if (bitmap != null) {

                        Uri thumUri = uri.buildUpon().appendQueryParameter("thum", "true").build();

                        ImageMessage imageMessage = ImageMessage.obtain(thumUri, uri);

                        sendMessage(imageMessage);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case android.R.id.button3:

                try {
                    InputStream is = getResources().openRawResource(R.raw.huihui);
                    String path = DemoContext.getInstance().getResourceDir();
                    FileUtil.createFile("voice", path);
                    Uri uri = Uri.parse(path + "/voice");
                    uri = FileUtil.writeByte(uri, FileUtil.toByteArray(is));
                    VoiceMessage voiceMessage = VoiceMessage.obtain(uri, 10 * 1000);

                    sendMessage(voiceMessage);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.group_invitation_notification:
                GroupInvitationNotification group = new GroupInvitationNotification("123456789", "张三邀请你加入xxx群");
                sendMessage(group);
                break;
            default:
                break;
        }

    }


    private void sendMessage(final RongIMClient.MessageContent msg) {

        if (mRongIMClient != null) {

            mRongIMClient.sendMessage(RongIMClient.ConversationType.PRIVATE, mUserId, msg, new RongIMClient.SendMessageCallback() {

                @Override
                public void onSuccess() {

                    if (msg instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) msg;
                        Log.d("sendMessage", "TextMessage---发发发发发--发送了一条【文字消息】-----" + textMessage.getContent());
                    } else if (msg instanceof ImageMessage) {
                        ImageMessage imageMessage = (ImageMessage) msg;
                        Log.d("sendMessage", "ImageMessage--发发发发发--发送了一条【图片消息】--uri---" + imageMessage.getThumUri());
                    } else if (msg instanceof VoiceMessage) {
                        VoiceMessage voiceMessage = (VoiceMessage) msg;
                        Log.d("sendMessage", "VoiceMessage--发发发发发--发送了一条【语音消息】---uri--" + voiceMessage.getUri());
                        Log.d("sendMessage", "VoiceMessage--发发发发发--发送了一条【语音消息】--长度---" + voiceMessage.getDuration());
                    } else if (msg instanceof GroupInvitationNotification) {
                        GroupInvitationNotification groupInvitationNotification = (GroupInvitationNotification) msg;
                        Log.d("sendMessage", "VoiceMessage--发发发发发--发送了一条【群组邀请消息】---message--" + groupInvitationNotification.getMessage());
                    }
                }

                @Override
                public void onError(ErrorCode errorCode) {
                    Log.d("sendMessage", "----发发发发发--发送消息失败----ErrorCode----" + errorCode.getValue());
                }

                @Override
                public void onProgress(int i) {
                    Log.d("sendMessage", "----发发发发发--发送消息进度-------%" +  i);
                }
            });


        } else {
            Toast.makeText(this, "请先连接。。。", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public boolean handleMessage(Message msg) {
        ImageMessage imageMessage = (ImageMessage) msg.obj;
        sendMessage(imageMessage);

        return false;
    }
}
