package com.frommetoyou.texting.chatModule.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.frommetoyou.texting.R;
import com.frommetoyou.texting.databinding.FragmentImageZoomBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ImageZoomFragment extends DialogFragment implements DialogInterface.OnShowListener {
    private FragmentImageZoomBinding binding;

    public ImageZoomFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_image_zoom, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogFragmentTheme_FullScreen)
                .setTitle(R.string.app_name)
                .setPositiveButton(R.string.common_label_ok, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(this);
        binding = FragmentImageZoomBinding.bind(view);
        return dialog;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (getActivity() != null) {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.ic_timer_sand_160);
            Glide.with(getContext())
                    .load(((OnImageZoom) getActivity()).getMessageSelected().getPhotoUrl())
                    .apply(options)
                    .into(binding.pvZoom);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null){
            window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
