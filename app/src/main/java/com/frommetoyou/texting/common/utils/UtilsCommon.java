package com.frommetoyou.texting.common.utils;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.frommetoyou.texting.R;
import com.frommetoyou.texting.mainModule.view.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.fragment.app.FragmentActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class UtilsCommon {
    /*
    * Codificar un correo electrónico
    * */
    public static String getEmailEncoded(String email){
        String preKey = email.replace("_","__");
        return preKey.replace(".","_");
    }
    /*
    * Cargar imagenes a un target
    * */
    public static void loadImage(Context context, String photoUrl, ImageView target) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();
        Glide.with(context)
                .load(photoUrl)
                .apply(options)
                .into(target);
    }

    public static boolean validateEmail(Context context, TextInputEditText etEmail) {
        boolean isValid = true;
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()){
            etEmail.setError(context.getString(R.string.common_validate_field_required));
            etEmail.requestFocus();
            isValid = false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError(context.getString(R.string.common_validate_email_invalid));
            etEmail.requestFocus();
            isValid = false;
        }
        return isValid;
    }

    /*
    * Mensajes snackbar
    * */

    public static void showSnackbar(View root, int resMessage) {
        showSnackbar(root, resMessage, Snackbar.LENGTH_SHORT);
    }

    public static void showSnackbar(View root, int resMessage, int duration) {
        Snackbar.make(root, resMessage, duration).show();
    }

    public static boolean validateMessage(EditText etMessage) {
        return etMessage.getText() != null && !etMessage.getText().toString().trim().isEmpty();
    }
}
