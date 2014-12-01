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

import java.io.IOException;
import java.io.InputStream;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.demo.message.GroupInvitationNotification;
import io.rong.message.CommandNotificationMessage;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.ProfileNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;


public class MainActivity extends Activity implements View.OnClickListener, Handler.Callback {

//    public static final String TOKEN = "97s/gIdybGtqZbDJxj2nQRHrL+7N5sFEDNyWFhRUD5k/4OHS96t/nLvXrPfkMhZRTCJz6WRg93bcjXrO0RS6UA==";//dsafd
//    public static final String TOKEN = "mNh2iIH0UTaWurXt3NM79Mvm/o4XK5QTKgksvmQJQNbUzzIlNCGVtlczQEfB08dPHBCJKStjd+LHrl8DaMhz7Q==";//yb001
    public static final String TOKEN = "KIzXr65WBrQ5R9AxUxT0WCnX+HAGvjd6OaiiFOkZCUVGOMQcKnb0KDdlCXVYQbqMHoZrTQCApoC5TfELCP8oKg==";//ceshi token 1012


    public static RongIMClient mRongIMClient;


    private Button connectButton;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button reqFriendButton;
    private Button profileNotificationButton;
    private Button commandeNotificationButton;





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
        reqFriendButton = (Button) findViewById(R.id.req_friend_notification);
        profileNotificationButton = (Button) findViewById(R.id.profile_notification);
        commandeNotificationButton = (Button) findViewById(R.id.command_notification);


        connectButton.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        reqFriendButton.setOnClickListener(this);
        profileNotificationButton.setOnClickListener(this);
        commandeNotificationButton.setOnClickListener(this);


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

                textMessage.setExtra("文字消息Extra");
                textMessage.setPushContent("push 内容setPushContent");
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

            case R.id.req_friend_notification://联系人（好友）操作通知消息
                ContactNotificationMessage contact = ContactNotificationMessage.obtain(ContactNotificationMessage.CONTACT_OPERATION_REQUEST,mUserId,"1011","请加我好友");
                contact.setExtra("I'm Bob");
                sendMessage(contact);
                break;
            case R.id.profile_notification://资料变更通知消息
                ProfileNotificationMessage profile = ProfileNotificationMessage.obtain("","资料变更数据");
                profile.setExtra("资料变更通知消息");
                sendMessage(profile);
                break;
            case R.id.command_notification://命令通知消息，可以实现任意指令操作
                CommandNotificationMessage command  = CommandNotificationMessage.obtain("删除","command delete");
                sendMessage(command);
                break;
            default:
                break;
        }

    }


    private void sendMessage(final RongIMClient.MessageContent msg) {

        if (mRongIMClient != null) {

            mRongIMClient.sendMessage(RongIMClient.ConversationType.PRIVATE, mUserId, msg, new RongIMClient.SendMessageCallback() {

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
                    } else if (msg instanceof GroupInvitationNotification) {
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
        ImageMessage imageMessage = (ImageMessage) msg.obj;
        sendMessage(imageMessage);

        return false;
    }
}
