package io.rong.imlib.demo;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class FileUtil {

    private static final String TAG = "FileUtil";
    private static final String NOMEDIA = ".nomedia";

    public static InputStream getFileInputStream(String path) {

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileInputStream;
    }

    public static final String createDirectory(File storageDirectory) {

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
        }

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

        return storageDirectory.getPath();
    }


    public static byte[] getByteFromUri(Uri uri) {
        InputStream input = getFileInputStream(uri.getPath());
        try {
            int count = 0;
            while (count == 0) {
                count = input.available();
            }

            byte[] bytes = new byte[count];
            input.read(bytes);

            return bytes;
        } catch (Exception e) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static Uri writeByte(Uri uri, byte[] data) {
        File fileFolder = new File(uri.getPath().substring(0, uri.getPath().lastIndexOf("/")));
        fileFolder.mkdirs();
        File file = new File(uri.getPath());


        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            os.write(data);
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(file);
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }


    public static void createFile(String name, String dir) {

        File nomediaFile = new File(dir, name);

        if (!nomediaFile.exists()) {
            try {
                Log.d(TAG, "Created file: " + nomediaFile + " " + String.valueOf(nomediaFile.createNewFile()));
            } catch (IOException e) {
                Log.d(TAG, "Unable to create .nomedia file for some reason.", e);
                throw new IllegalStateException("Unable to create nomedia file.");
            }
        }

    }
}
