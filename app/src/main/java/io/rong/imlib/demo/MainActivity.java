package io.rong.imlib.demo;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.demo.message.ImageMessage;
import io.rong.imlib.demo.message.TextMessage;
import io.rong.imlib.demo.message.VoiceMessage;


public class MainActivity extends Activity implements View.OnClickListener {

    public static final String TOKEN = "dlZQXtLihq5mohiybibkaUmcbyeYIrXSDa0nFvL2mH/5zWOjUlJe+Aaszzzvx90roUr3nN+i0+Q=";
    public static RongIMClient mRongIMClient;


    private Button connectButton;
    private Button button1;
    private Button button2;
    private Button button3;


    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        connectButton = (Button) findViewById(R.id.connect_button);
        button1 = (Button) findViewById(android.R.id.button1);
        button2 = (Button) findViewById(android.R.id.button2);
        button3 = (Button) findViewById(android.R.id.button3);

        connectButton.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);


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
                    ImageMessage imageMessage = new ImageMessage(uri);
                    sendMessage(imageMessage);

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
                    VoiceMessage voiceMessage = new VoiceMessage(uri, 10 * 1000);

                    sendMessage(voiceMessage);

                } catch (IOException e) {
                    e.printStackTrace();
                }

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
                    }
                }

                @Override
                public void onError(ErrorCode errorCode) {
                    Log.d("sendMessage", "----发发发发发--发送消息失败----ErrorCode----" + errorCode.getValue());
                }
            });


        } else {
            Toast.makeText(this, "请先连接。。。", Toast.LENGTH_LONG).show();
        }

    }


}
