package com.frommetoyou.texting.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;

import com.frommetoyou.texting.R;

import java.io.File;
import java.io.FileNotFoundException;

public class UtilsImage {
    public static Bitmap reduceBitmap(Context context, View container, String uri, int maxAncho, int maxAlto){
        try{
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(uri)),
                    null, options);
            options.inSampleSize = (int)Math.max(
                    Math.ceil(options.outWidth / maxAncho),
                    Math.ceil(options.outHeight / maxAlto));
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(Uri.parse(uri)), null, options);
        }catch (FileNotFoundException e){
            e.printStackTrace();
            UtilsCommon.showSnackbar(container, R.string.profile_error_notfound);
            return null;
        }
    }

    public static void deleteTempFiles(File file) {
        if (file.isDirectory()){
            File[] files = file.listFiles();
            if (files != null){
                for (File f : files){
                    if (f.isDirectory()) {
                        deleteTempFiles(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
    }
}
