package ru.kzkovich.laptopushka.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ru.kzkovich.laptopushka.R;

public class PasswordDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //passing null as the parent view because its going in a dialog layout
        builder.setView(inflater.inflate(R.layout.enter_password_dialog, null))
                .setPositiveButton(R.string.looks_like_it_is, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //send password to main activity
                    }
                })
                .setNegativeButton(R.string.cancel_fuck, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PasswordDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
