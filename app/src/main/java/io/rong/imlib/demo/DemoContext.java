package io.rong.imlib.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.sea_monster.core.common.Const;
import com.sea_monster.core.common.DiscardOldestPolicy;
import com.sea_monster.core.network.DefaultHttpHandler;
import com.sea_monster.core.network.HttpHandler;
import com.sea_monster.core.resource.ResourceManager;
import com.sea_monster.core.resource.cache.ResourceCacheWrapper;
import com.sea_monster.core.resource.compress.ResourceCompressHandler;
import com.sea_monster.core.resource.io.FileSysHandler;
import com.sea_monster.core.resource.io.IFileSysHandler;
import com.sea_monster.core.resource.io.ResourceRemoteWrapper;
import com.sea_monster.core.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.demo.message.GroupInvitationNotification;
import io.rong.message.CommandNotificationMessage;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.ProfileNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import uk.co.senab.bitmapcache.BitmapLruCache;

public class DemoContext {

    private static final String TAG = "DemoContext";
    private static final String NOMEDIA = ".nomedia";

    private static DemoContext self;

    private BlockingQueue<Runnable> mWorkQueue;
    private ThreadFactory mThreadFactory;
    private static ThreadPoolExecutor sExecutor;

    private IFileSysHandler mFileSysHandler;

    private SharedPreferences sharedPreferences;
    ThreadPoolExecutor mExecutor;

    public Context mContext;

    private String mResourceDir;

    public RongIMClient mRongIMClient;

    public String userId;


    public static DemoContext getInstance() {

        if (self == null) {
            self = new DemoContext();
        }

        return self;
    }

    public DemoContext() {
    }

    public DemoContext(Context context) {
        self = this;
    }

    public void init(Context context) {

        mContext = context;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


        BlockingQueue<Runnable> mWorkQueue = new PriorityBlockingQueue<Runnable>(Const.SYS.WORK_QUEUE_MAX_COUNT);

        ThreadFactory mThreadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(Runnable r) {
                return new Thread(r, "ConnectTask #" + mCount.getAndIncrement());
            }
        };

        mExecutor = new ThreadPoolExecutor(Const.SYS.DEF_THREAD_WORDER_COUNT, Const.SYS.MAX_THREAD_WORKER_COUNT, Const.SYS.CREATE_THREAD_TIME_SPAN,
                TimeUnit.SECONDS, mWorkQueue, mThreadFactory);

        mExecutor.setRejectedExecutionHandler(new DiscardOldestPolicy());

        mFileSysHandler = new FileSysHandler(mExecutor, getResourceDir(mContext), "file", "rong");

        HttpHandler httpHandler = new DefaultHttpHandler(mContext, mExecutor);

        ResourceRemoteWrapper remoteWrapper = new ResourceRemoteWrapper(mContext, mFileSysHandler, httpHandler);

        File cacheFile = new File(getResourceDir(mContext), "cache");
        if (!cacheFile.exists())
            FileUtils.createDirectory(cacheFile, true);

        BitmapLruCache cache = new BitmapLruCache.Builder(mContext).setDiskCacheLocation(cacheFile).setMemoryCacheMaxSize(5 * 1024 * 1204).build();


        ResourceCompressHandler compressHandler = new ResourceCompressHandler(mContext, mFileSysHandler);

        ResourceCacheWrapper cacheWrapper = new ResourceCacheWrapper(mContext, cache, mFileSysHandler, compressHandler);

        ResourceManager.init(mContext, remoteWrapper, cacheWrapper);
    }


    public String getFileSysDir(String dir) {

        if (!TextUtils.isEmpty(mResourceDir)) {
            return mResourceDir;
        }

        File environmentPath = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            environmentPath = Environment.getExternalStorageDirectory();
        } else {
            environmentPath = mContext.getFilesDir();
        }

        File baseDirectory = new File(environmentPath, dir);

        createDirectory(baseDirectory);

        return mResourceDir = baseDirectory.getAbsolutePath();

    }

    private static final void createDirectory(File storageDirectory) {

        if (!storageDirectory.exists()) {

            Log.d(TAG, "Trying to create storageDirectory: " + String.valueOf(storageDirectory.mkdirs()));

            Log.d(TAG, "Exists: " + storageDirectory + " " + String.valueOf(storageDirectory.exists()));
            Log.d(TAG, "State: " + Environment.getExternalStorageState());
            Log.d(TAG, "Isdir: " + storageDirectory + " " + String.valueOf(storageDirectory.isDirectory()));
            Log.d(TAG, "Readable: " + storageDirectory + " " + String.valueOf(storageDirectory.canRead()));
            Log.d(TAG, "Writable: " + storageDirectory + " " + String.valueOf(storageDirectory.canWrite()));

            File tmp = storageDirectory.getParentFile();

            Log.d(TAG, "Exists: " + tmp + " " + String.valueOf(tmp.exists()));
            Log.d(TAG, "Isdir: " + tmp + " " + String.valueOf(tmp.isDirectory()));
            Log.d(TAG, "Readable: " + tmp + " " + String.valueOf(tmp.canRead()));
            Log.d(TAG, "Writable: " + tmp + " " + String.valueOf(tmp.canWrite()));

            tmp = tmp.getParentFile();

            Log.d(TAG, "Exists: " + tmp + " " + String.valueOf(tmp.exists()));
            Log.d(TAG, "Isdir: " + tmp + " " + String.valueOf(tmp.isDirectory()));
            Log.d(TAG, "Readable: " + tmp + " " + String.valueOf(tmp.canRead()));
            Log.d(TAG, "Writable: " + tmp + " " + String.valueOf(tmp.canWrite()));

            File nomediaFile = new File(storageDirectory, NOMEDIA);

            if (!nomediaFile.exists()) {
                try {
                    Log.d(TAG, "Created file: " + nomediaFile + " " + String.valueOf(nomediaFile.createNewFile()));
                } catch (IOException e) {
                    Log.d(TAG, "Unable to create .nomedia file for some reason.", e);
                    throw new IllegalStateException("Unable to create nomedia file.");
                }
            }

            if (!(storageDirectory.isDirectory() && nomediaFile.exists())) {
                throw new RuntimeException("Unable to create storage directory and nomedia file.");
            }
        }

    }


    public String getResourceDir() {
        return mResourceDir;
    }

    private final File getResourceDir(Context context) {


        File environmentPath = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            environmentPath = Environment.getExternalStorageDirectory();
            environmentPath = new File(environmentPath, "Android/data/" + context.getPackageName());
        } else {
            environmentPath = context.getFilesDir();
        }

        File baseDirectory = new File(environmentPath, "RongCloud");

        if (!baseDirectory.exists()) {
            baseDirectory.mkdirs();
        }

        mResourceDir = baseDirectory.getPath();

        return baseDirectory;
    }


    public void setRongIMClient(RongIMClient rongIMClient) {
        mRongIMClient = rongIMClient;
    }

    public void registerMessage() {

        mRongIMClient.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {

            @Override
            public void onReceived(RongIMClient.Message message, int left) {

                if (message.getContent() instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message.getContent();

                    Log.d("onReceived", "TextMessage---收收收收--接收到一条【文字消息】-----" + textMessage.getContent()+",getExtra:"+textMessage.getExtra());
                    Log.d("onReceived", "TextMessage---收收收收--接收到一条【文字消息】getPushContent-----" + textMessage.getPushContent());

                } else if (message.getContent() instanceof ImageMessage) {

                    final ImageMessage imageMessage = (ImageMessage) message.getContent();
                    Log.d("onReceived", "ImageMessage--收收收收--接收到一条【图片消息】---ThumUri--" + imageMessage.getLocalUri());
                    Log.d("onReceived", "ImageMessage--收收收收--接收到一条【图片消息】----Uri--" + imageMessage.getRemoteUri());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            mRongIMClient.downloadMedia(RongIMClient.ConversationType.PRIVATE, userId, RongIMClient.MediaType.IMAGE, imageMessage.getRemoteUri().toString(), new RongIMClient.DownloadMediaCallback() {

                                @Override
                                public void onProgress(int i) {
                                    Log.d("downloadMedia", "onProgress:" + i);
                                }

                                @Override
                                public void onSuccess(String s) {
                                    Log.d("downloadMedia", "onSuccess:" + s);
                                }

                                @Override
                                public void onError(ErrorCode errorCode) {
                                    Log.d("downloadMedia", "onError:" + errorCode.getValue());
                                }
                            });
                        }
                    }).start();

                } else if (message.getContent() instanceof VoiceMessage) {

                    final VoiceMessage voiceMessage = (VoiceMessage) message.getContent();

                    Log.d("onReceived", "VoiceMessage--收收收收--接收到一条【语音消息】-----" + voiceMessage.getUri());

                    new Thread(new Runnable() {

                        @Override
                        public void run() {

                            MediaPlayer mMediaPlayer = new MediaPlayer();
                            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.start();
                                }
                            });

                            try {
                                mMediaPlayer.setDataSource(mContext, voiceMessage.getUri());
                                mMediaPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else if (message.getContent() instanceof GroupInvitationNotification) {

                    GroupInvitationNotification groupInvitationNotification = (GroupInvitationNotification) message.getContent();

                    Log.d("onReceived", "GroupInvitationNotification--收收收收--接收到一条【群组邀请消息】-----" + groupInvitationNotification.getMessage());

                }else if(message.getContent() instanceof ContactNotificationMessage){
                    ContactNotificationMessage mContactNotificationMessage = (ContactNotificationMessage) message.getContent();
                    Log.d("onReceived", "mContactNotificationMessage--收收收收--接收到一条【联系人（好友）操作通知消息】-----"+mContactNotificationMessage.getMessage()+",getExtra:"+mContactNotificationMessage.getExtra());

                }else if(message.getContent() instanceof ProfileNotificationMessage){
                    ProfileNotificationMessage mProfileNotificationMessage = (ProfileNotificationMessage) message.getContent();
                    Log.d("onReceived", "GroupNotificationMessage--收收收收--接收到一条【资料变更通知消息】-----"+mProfileNotificationMessage.getData()+",getExtra:"+mProfileNotificationMessage.getExtra());

                }else  if(message.getContent() instanceof CommandNotificationMessage){
                    CommandNotificationMessage mCommandNotificationMessage = (CommandNotificationMessage) message.getContent();
                    Log.d("onReceived", "GroupNotificationMessage--收收收收--接收到一条【命令通知消息】-----"+mCommandNotificationMessage.getData()+",getName:"+mCommandNotificationMessage.getName());
                }

            }
        });
    }


}
