package io.rong.imlib.demo.message;

/**
 * Created by DragonJ on 14-8-19.
 */

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import com.sea_monster.core.resource.ResourceManager;
import com.sea_monster.core.resource.model.Resource;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.demo.DemoContext;
import io.rong.imlib.demo.FileUtil;
import io.rong.imlib.demo.Util;

/**
 * 语音消息，会存入消息历史记录。
 */
@MessageTag(value = "RC:VcMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class VoiceMessage extends RongIMClient.MessageContent implements Parcelable {

    private Uri uri;

    private int duration;

    /**
     * 将本地消息对象序列化为消息数据。
     */
    @Override
    public byte[] encode() {

        byte[] voiceData = FileUtil.getByteFromUri(uri);

        if (voiceData == null) {
            Log.d("publishVoide--", "voiceData is null");
            return null;
        }

        String voiceStr = Base64.encodeToString(voiceData, Base64.NO_WRAP);
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("content", voiceStr);
            jsonObj.put("duration", duration);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

        return jsonObj.toString().getBytes();
    }

    protected VoiceMessage() {

    }

    public VoiceMessage(byte[] data, RongIMClient.Message message) {
        String jsonStr = new String(data);

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            setDuration(jsonObj.getInt("duration"));

            if(message!=null)
            {

            Uri uri = Util.obtainVoiceUri(DemoContext.getInstance().mContext, message);

            if(!ResourceManager.getInstance().containsInDisk(new Resource(uri))){
                byte[] audio = Base64.decode(jsonObj.getString("content"), Base64.NO_WRAP);
                InputStream stream = new ByteArrayInputStream(audio);
                ResourceManager.getInstance().put(new Resource(uri),stream);
            }

            this.uri = ResourceManager.getInstance().getFileUri(new Resource(uri));}

        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }
    }

    /**
     * 构造函数。
     *
     * @param uri      音频文件的 Uri。
     * @param duration 音频片段时长，单位为秒。
     */
    public VoiceMessage(Uri uri, int duration) {
        this.uri = uri;
        this.duration = duration;
    }

    /**
     * 获取音频文件的 Uri。
     *
     * @return 音频文件的 Uri。
     */
    public Uri getUri() {
        return uri;
    }

    /**
     * 设置音频文件的 Uri。
     *
     * @param uri 音频文件的 Uri。
     */
    public void setUri(Uri uri) {
        this.uri = uri;
    }

    /**
     * 获取音频片段的时长。
     *
     * @return 音频片段的时长。
     */
    public int getDuration() {
        return duration;
    }

    /**
     * 设置音频片段的时长。
     *
     * @param duration 音频片段的时长。
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * 描述了包含在 Parcelable 对象排列信息中的特殊对象的类型。
     *
     * @return 一个标志位，表明Parcelable对象特殊对象类型集合的排列。
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 将类的数据写入外部提供的 Parcel 中。
     *
     * @param dest  对象被写入的 Parcel。
     * @param flags 对象如何被写入的附加标志，可能是 0 或 PARCELABLE_WRITE_RETURN_VALUE。
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (uri == null || uri.getPath() == null) {
            dest.writeString("");
        } else {
            dest.writeString(uri.getPath());
        }

        dest.writeInt(duration);
    }

    /**
     * 构造函数。
     *
     * @param in 初始化传入的 Parcel。
     */
    public VoiceMessage(Parcel in) {
        uri = Uri.parse(in.readString());
        duration = in.readInt();
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<VoiceMessage> CREATOR = new Creator<VoiceMessage>() {

        @Override
        public VoiceMessage createFromParcel(Parcel source) {
            return new VoiceMessage(source);
        }

        @Override
        public VoiceMessage[] newArray(int size) {
            return new VoiceMessage[size];
        }
    };
}
