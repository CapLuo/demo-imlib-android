package io.rong.imlib.demo.message;

/**
 * Created by DragonJ on 14-8-19.
 */

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.sea_monster.core.resource.ResourceManager;
import com.sea_monster.core.resource.model.Resource;
import com.sea_monster.core.utils.ParcelUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;

import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.demo.BitmapUtils;
import io.rong.imlib.demo.DemoContext;
import io.rong.imlib.demo.FileUtil;
import io.rong.imlib.demo.Util;

/**
 * 图片消息，定义了图片缩略图和原图地址，会存入消息历史记录。
 */
@MessageTag(value = "RC:ImgMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class ImageMessage extends RongIMClient.MessageContent implements Parcelable {

    private Uri uri;
    private boolean upLoadExp = false;
    private Uri thumUri;

    /**
     * 将本地消息对象序列化为消息数据。
     */
    @Override
    public byte[] encode() {

        BitmapDrawable drawable = ResourceManager.getInstance().getDrawable(new Resource(thumUri));

        String base64 = BitmapUtils.getBase64FromBitmap(drawable.getBitmap());

        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("content", base64);
            jsonObj.put("imageUri", getUri().toString());
            if (upLoadExp) {
                jsonObj.put("exp", true);
            }
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

        return jsonObj.toString().getBytes();
    }

    protected ImageMessage() {

    }

    public ImageMessage(byte[] data, RongIMClient.Message message) {

        String jsonStr = new String(data);

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            uri = Uri.parse(jsonObj.getString("imageUri"));

            if (message != null) {

                thumUri = Util.obtainThumImageUri(DemoContext.getInstance().mContext, message);

                if (!ResourceManager.getInstance().containsInCache(new Resource(thumUri))) {
                    String base64Str = jsonObj.getString("content");
                    Bitmap bitmap = BitmapUtils.getBitmapFromBase64(base64Str);
                    if (bitmap != null) {
                        ResourceManager.getInstance().put(new Resource(thumUri), bitmap);
                    }
                }
            }

            if (jsonObj.has("exp")) {
                upLoadExp = true;
            }


            //TODO: render data thum
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }
    }

    /**
     * 构造函数。
     *
     * @param uri 消息中的图片原图 Uri。
     */
    public ImageMessage(Uri uri) throws FileNotFoundException {
        this.uri = uri;
        thumUri = uri.buildUpon().appendQueryParameter("thum", "true").build();

        try{
            Bitmap bitmap = BitmapUtils.getResizedBitmap(DemoContext.getInstance().mContext, uri, 240, 240);
            if (bitmap != null) {
                    ResourceManager.getInstance().put(new Resource(thumUri), bitmap);
            }else {
                throw new FileNotFoundException();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileNotFoundException();
        }
    }

    public boolean isUpLoadExp() {
        return upLoadExp;
    }

    public void setUpLoadExp(boolean upLoadExp) {
        this.upLoadExp = upLoadExp;
    }

    protected byte[] getImageStream() {
        return FileUtil.getByteFromUri(uri);
    }

    /**
     * 获取缩略图位图。
     *
     * @return 缩略图位图。
     */
    public Uri getThumUri() {
        return thumUri;
    }

    /**
     * 获取图片的 Uri。
     *
     * @return 图片的 Uri。
     */
    public Uri getUri() {
        return uri;
    }

    /**
     * 设置图片的 Uri。
     *
     * @param uri 图片的 Uri。
     */
    public void setUri(Uri uri) {
        this.uri = uri;
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
     * 构造函数。
     *
     * @param in 初始化传入的 Parcel。
     */
    public ImageMessage(Parcel in) {
        thumUri = ParcelUtils.readFromParcel(in, Uri.class);
        uri = ParcelUtils.readFromParcel(in, Uri.class);
    }

    /**
     * 将类的数据写入外部提供的 Parcel 中。
     *
     * @param dest  对象被写入的 Parcel。
     * @param flags 对象如何被写入的附加标志，可能是 0 或 PARCELABLE_WRITE_RETURN_VALUE。
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, thumUri);
        ParcelUtils.writeToParcel(dest, uri);
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<ImageMessage> CREATOR = new Creator<ImageMessage>() {

        @Override
        public ImageMessage createFromParcel(Parcel source) {
            return new ImageMessage(source);
        }

        @Override
        public ImageMessage[] newArray(int size) {
            return new ImageMessage[size];
        }
    };
}
