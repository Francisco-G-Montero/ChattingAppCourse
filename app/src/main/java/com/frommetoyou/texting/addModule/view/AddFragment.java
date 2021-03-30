package com.frommetoyou.texting.addModule.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.frommetoyou.texting.R;
import com.frommetoyou.texting.addModule.AddPresenter;
import com.frommetoyou.texting.addModule.AddPresenterClass;
import com.frommetoyou.texting.common.utils.UtilsCommon;
import com.frommetoyou.texting.databinding.FragmentAddBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;


public class AddFragment extends DialogFragment implements DialogInterface.OnShowListener, AddView {
    private FragmentAddBinding binding;
    private Button positiveDialogButton;

    private AddPresenter mPresenter;

    public AddFragment() {
        mPresenter = new AddPresenterClass(this);
    }

    @NonNull
    @Override
    public AlertDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(R.string.addFriend_title)
                .setPositiveButton(R.string.common_label_accept, null)
                .setNegativeButton(R.string.common_label_cancel, null);
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_add, null);
        binding=FragmentAddBinding.bind(view);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(this);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            positiveDialogButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveDialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UtilsCommon.validateEmail(getActivity(), binding.etEmail))
                        mPresenter.addFriend(binding.etEmail.getText().toString().trim());
                }
            });
            Button negativeDialogButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            negativeDialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        mPresenter.onShow();
    }


    /*
    * AddView
    * */

    @Override
    public void enableUIElements() {
        binding.etEmail.setEnabled(true);
        positiveDialogButton.setEnabled(true);
    }

    @Override
    public void disableUIElements() {
        binding.etEmail.setEnabled(false);
        positiveDialogButton.setEnabled(false);
    }

    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void friendAdded() {
        Toast.makeText(getActivity(), R.string.addFriend_message_request_dispatched, Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @Override
    public void friendNotAdded() {
        binding.etEmail.setText(R.string.addFriend_error_message);
        binding.etEmail.requestFocus();

    }
}