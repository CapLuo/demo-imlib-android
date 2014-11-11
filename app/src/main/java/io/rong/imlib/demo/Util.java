package io.rong.imlib.demo;

import io.rong.imlib.RongIMClient;


import android.annotation.SuppressLint;

import android.content.Context;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;


@SuppressLint("SimpleDateFormat")
public class Util {

    private static final String TAG = "Util";


    public static Uri obtainThumImageUri(Context context, RongIMClient.Message message) {
        Uri uri = Uri.parse("rong://" + context.getPackageName()).buildUpon().appendPath("image").appendPath("thum").appendPath(String.valueOf(message.getMessageId())).appendPath(message.getSenderUserId()).build();
        return uri;
    }

    public static Uri obtainVoiceUri(Context context, RongIMClient.Message message) {
        Uri uri = Uri.parse("rong://" + context.getPackageName()).buildUpon().appendPath("voice").appendPath(String.valueOf(message.getMessageId())).appendPath(message.getSenderUserId()).build();
        return uri;
    }


    public static Bitmap getResizedBitmap(Context context, Uri uri, int widthLimit, int heightLimit) throws IOException {

        String path = null;
        Bitmap result = null;

        if (uri.getScheme().equals("file")) {
            path = uri.getPath();
        } else if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            cursor.moveToFirst();
            path = cursor.getString(0);
            cursor.close();
        } else {
            return null;
        }

        ExifInterface exifInterface = new ExifInterface(path);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);

        if (orientation == ExifInterface.ORIENTATION_ROTATE_90
                || orientation == ExifInterface.ORIENTATION_ROTATE_270
                || orientation == ExifInterface.ORIENTATION_TRANSPOSE
                || orientation == ExifInterface.ORIENTATION_TRANSVERSE) {
            int tmp = widthLimit;
            widthLimit = heightLimit;
            heightLimit = tmp;
        }

        int width = options.outWidth;
        int height = options.outHeight;
        int sampleW = 1, sampleH = 1;
        while (width / 2 > widthLimit) {
            width /= 2;
            sampleW <<= 1;

        }

        while (height / 2 > heightLimit) {
            height /= 2;
            sampleH <<= 1;
        }
        int sampleSize = 1;

        options = new BitmapFactory.Options();
        if (widthLimit == Integer.MAX_VALUE || heightLimit == Integer.MAX_VALUE) {
            sampleSize = Math.max(sampleW, sampleH);
        } else {
            sampleSize = Math.max(sampleW, sampleH);
        }
        options.inSampleSize = sampleSize;

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeFile(path, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            options.inSampleSize = options.inSampleSize << 1;
            bitmap = BitmapFactory.decodeFile(path, options);
        }

        Matrix matrix = new Matrix();
        if (bitmap == null) {
            return result;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        if (orientation == ExifInterface.ORIENTATION_ROTATE_90
                || orientation == ExifInterface.ORIENTATION_ROTATE_270
                || orientation == ExifInterface.ORIENTATION_TRANSPOSE
                || orientation == ExifInterface.ORIENTATION_TRANSVERSE) {
            int tmp = w;
            w = h;
            h = tmp;
        }
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90, w / 2f, h / 2f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180, w / 2f, h / 2f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270, w / 2f, h / 2f);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.preScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.preScale(1, -1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90, w / 2f, h / 2f);
                matrix.preScale(1, -1);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(270, w / 2f, h / 2f);
                matrix.preScale(1, -1);
                break;
        }
        float xS = (float) widthLimit / bitmap.getWidth();
        float yS = (float) heightLimit / bitmap.getHeight();

        matrix.postScale(Math.min(xS, yS), Math.min(xS, yS));
        try {
            result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            Log.d("ResourceCompressHandler", "OOM" + "Height:" + bitmap.getHeight() + "Width:" + bitmap.getHeight() + "matrix:" + xS + " " + yS);
            return null;
        }
        return result;
    }


}
