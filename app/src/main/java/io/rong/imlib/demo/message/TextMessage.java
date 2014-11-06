package io.rong.imlib.demo.message;

/**
 * Created by DragonJ on 14-8-19.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient;

/**
 * 文字消息，会存入消息历史记录。
 */
@MessageTag(value = "RC:TxtMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class TextMessage extends RongIMClient.MessageContent implements Parcelable {

    private String content;

    /**
     * 将本地消息对象序列化为消息数据。
     *
     * @return 消息数据。
     */
    @Override
    public byte[] encode() {

        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("content", getExpression(getContent()));

        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private String getExpression(String content) {

        Pattern pattern = Pattern.compile("\\[/u([0-9A-Fa-f]+)\\]");
        Matcher matcher = pattern.matcher(content);

        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(sb, toExpressionChar(matcher.group(1)));
        }

        matcher.appendTail(sb);
        Log.d("getExpression--", sb.toString());

        return sb.toString();
    }

    private String toExpressionChar(String expChar) {
        int inthex = Integer.parseInt(expChar, 16);
        return String.valueOf(Character.toChars(inthex));
    }

    protected TextMessage() {

    }

    public static TextMessage obtain(String text){
        TextMessage model =  new TextMessage();
        model.setContent(text);
        return model;
    }

    public TextMessage(byte[] data, RongIMClient.Message message) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {

        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            setContent(jsonObj.getString("content"));
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

    }

    /**
     * 设置文字消息的内容。
     *
     * @param content 文字消息的内容。
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 描述了包含在 Parcelable 对象排列信息中的特殊对象的类型。
     *
     * @return 一个标志位，表明Parcelable对象特殊对象类型集合的排列。
     */
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
        if (content != null) {
            dest.writeString(content);
        } else {
            dest.writeString("");
        }
    }

    /**
     * 构造函数。
     *
     * @param in 初始化传入的 Parcel。
     */
    public TextMessage(Parcel in) {
        content = in.readString();
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<TextMessage> CREATOR = new Creator<TextMessage>() {

        @Override
        public TextMessage createFromParcel(Parcel source) {
            return new TextMessage(source);
        }

        @Override
        public TextMessage[] newArray(int size) {
            return new TextMessage[size];
        }
    };

    /**
     * 构造函数。
     *
     * @param content 文字消息的内容。
     */
    public TextMessage(String content) {
        this.setContent(content);
    }

    /**
     * 获取文字消息的内容。
     *
     * @return 文字消息的内容。
     */
    public String getContent() {
        return content;
    }
}
