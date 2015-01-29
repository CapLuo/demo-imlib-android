package io.rong.imlib.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.demo.message.GroupInvitationNotification;
import io.rong.message.CommandNotificationMessage;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.ProfileNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;


public class MainActivity extends Activity implements View.OnClickListener, Handler.Callback {

    public static final String TOKEN = "eDiyfgBezw3nkykFcMjAWkmcbyeYIrXSDa0nFvL2mH8LR+1EmUiA/Vol1bK3Cvoy167uyKjlDD/WfsYXpExsWw==";//

    public static RongIMClient mRongIMClient;
    private Button connectButton;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button reqFriendButton;
    private Button profileNotificationButton;
    private Button commandeNotificationButton;
    private Button informationNotificationButton;
    private String mUserId;
    private Handler mHandler;
    private Handler mWorkHandler;
    private final static int IMAGEMESSAGE = 1;
    private final static int VOICEMESSAGE = 2;
    /**接收方Id,用于测试*/
    private String mUserIdTest = "1385";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HandlerThread mHandlerThread  = new HandlerThread("SendMessage");
        mHandlerThread.start();
        mWorkHandler = new Handler(mHandlerThread.getLooper());
        mHandler = new Handler(this);

        connectButton = (Button) findViewById(R.id.connect_button);
        button1 = (Button) findViewById(android.R.id.button1);
        button2 = (Button) findViewById(android.R.id.button2);
        button3 = (Button) findViewById(android.R.id.button3);
        button4 = (Button) findViewById(R.id.group_invitation_notification);
        reqFriendButton = (Button) findViewById(R.id.req_friend_notification);
        profileNotificationButton = (Button) findViewById(R.id.profile_notification);
        commandeNotificationButton = (Button) findViewById(R.id.command_notification);
        informationNotificationButton = (Button) findViewById(R.id.information_notification);

        connectButton.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        reqFriendButton.setOnClickListener(this);
        profileNotificationButton.setOnClickListener(this);
        commandeNotificationButton.setOnClickListener(this);
        informationNotificationButton.setOnClickListener(this);

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
                    DemoContext.getInstance().registerReceiveMessageListener();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case android.R.id.button1:

                TextMessage textMessage = TextMessage.obtain("这是消息。。。。。。春节快乐！！！！发送时间:" + System.currentTimeMillis());
                textMessage.setExtra("文字消息Extra");
                textMessage.setPushContent("push 内容setPushContent");
                sendMessage(textMessage);

                break;
            case android.R.id.button2:


                mHandler.post(new Runnable() {
                    @Override
                    public void run() {


                        mWorkHandler.post(new SendImageMessageRunnable());
                    }
                });

                break;
            case android.R.id.button3:

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        mWorkHandler.post(new SendVoiceMessageRunnable());
                    }
                });

                break;
            case R.id.group_invitation_notification:

                GroupInvitationNotification  group = new GroupInvitationNotification(mUserIdTest, "张三邀请你加入xxx群");
                sendMessage(group);

                break;
            case R.id.req_friend_notification://联系人（好友）操作通知消息

                ContactNotificationMessage contact = ContactNotificationMessage.obtain(ContactNotificationMessage.CONTACT_OPERATION_REQUEST,mUserId,mUserIdTest,"请加我好友");
                contact.setExtra("I'm Bob");
                sendMessage(contact);

                break;
            case R.id.profile_notification://资料变更通知消息

                ProfileNotificationMessage profile = ProfileNotificationMessage.obtain(mUserIdTest,"资料变更数据");
                profile.setExtra("资料变更通知消息");
                sendMessage(profile);

                break;
            case R.id.command_notification://命令通知消息，可以实现任意指令操作

                CommandNotificationMessage command  = CommandNotificationMessage.obtain("删除","command delete");
                sendMessage(command);

                break;
            case  R.id.information_notification:

                InformationNotificationMessage information  = InformationNotificationMessage.obtain("I'm bob");
                information.setExtra("hehe");
                sendMessage(information);
                break;
            default:
                break;
        }

    }

private void sendMessage(final RongIMClient.MessageContent msg) {
    if (mRongIMClient != null) {
            mRongIMClient.sendMessage(RongIMClient.ConversationType.PRIVATE, mUserIdTest, msg, new RongIMClient.SendMessageCallback() {

                @Override
                public void onSuccess(int id) {

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
                    }else if(msg instanceof  LocationMessage){
                        LocationMessage location  = (LocationMessage) msg;
                        Log.d("sendMessage", "VoiceMessage--发发发发发--发送了一条【语音消息】---uri--" + location.getPoi());
                    }else if (msg instanceof GroupInvitationNotification) {
                        GroupInvitationNotification groupInvitationNotification = (GroupInvitationNotification) msg;
                        Log.d("sendMessage", "VoiceMessage--发发发发发--发送了一条【群组邀请消息】---message--" + groupInvitationNotification.getMessage());
                    }else if(msg instanceof  ContactNotificationMessage){
                        ContactNotificationMessage mContactNotificationMessage = (ContactNotificationMessage) msg;
                        Log.d("sendMessage", "ContactNotificationMessage--发发发发发--发送了一条【联系人（好友）操作通知消息】---message--" + mContactNotificationMessage.getMessage());
                    }else if(msg instanceof  ProfileNotificationMessage){
                        ProfileNotificationMessage mProfileNotificationMessage = (ProfileNotificationMessage) msg;
                        Log.d("sendMessage", "ProfileNotificationMessage--发发发发发--发送了一条【资料变更通知消息】---message--" + mProfileNotificationMessage.getData());
                    }else if(msg instanceof  CommandNotificationMessage){
                        CommandNotificationMessage mCommandNotificationMessage = (CommandNotificationMessage) msg;
                        Log.d("sendMessage", "CommandNotificationMessage--发发发发发--发送了一条【命令通知消息】---message--" + mCommandNotificationMessage.getData());
                    }else if(msg instanceof  InformationNotificationMessage){
                        InformationNotificationMessage mInformationNotificationMessage = (InformationNotificationMessage) msg;
                        Log.d("sendMessage", "InformationNotificationMessage--发发发发发--发送了一条【小灰条消息】---message--" + mInformationNotificationMessage.getMessage());

                    }
                }

                @Override
                public void onError(int id,ErrorCode errorCode) {
                    Log.d("sendMessage", "----发发发发发--发送消息失败----ErrorCode----" + errorCode.getValue());
                }

                @Override
                public void onProgress(int id,int i) {
                    Log.d("sendMessage", "----发发发发发--发送消息进度-------%" +  i);
                }
            });

        } else {
            Toast.makeText(this, "请先连接。。。", Toast.LENGTH_LONG).show();
        }

    }



    @Override
    public boolean handleMessage(Message msg) {

        if (msg.what == IMAGEMESSAGE){
            ImageMessage imageMessage = (ImageMessage) msg.obj;
            sendMessage(imageMessage);
        }else if(msg.what == VOICEMESSAGE){

        }

        return false;
    }

    private class SendVoiceMessageRunnable implements  Runnable{

        @Override
        public void run() {

            try {
                InputStream is = getResources().openRawResource(R.raw.huihui);
                String path = DemoContext.getInstance().getResourceDir();
                FileUtil.createFile("voice", path);
                Uri uri = Uri.parse(path + "/voice");
                uri = FileUtil.writeByte(uri, FileUtil.toByteArray(is));
                VoiceMessage voiceMessage = VoiceMessage.obtain(uri, 10 * 5);
                sendMessage(voiceMessage);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class SendImageMessageRunnable implements  Runnable{

        @Override
        public void run() {
            try {

                InputStream is = getResources().openRawResource(R.raw.pics);
                String path = DemoContext.getInstance().getResourceDir();
                FileUtil.createFile("pic", path);
                Uri uri = Uri.parse(path + "/pic");

                uri = FileUtil.writeByte(uri, FileUtil.toByteArray(is));

                Bitmap bitmap = BitmapUtils.getResizedBitmap(MainActivity.this, uri, 960, 960);

                if (bitmap != null) {

                    Uri thumUri = uri.buildUpon().appendQueryParameter("thum", "true").build();

                    ImageMessage imageMessage = ImageMessage.obtain(thumUri, uri);

                    sendMessage(imageMessage);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


