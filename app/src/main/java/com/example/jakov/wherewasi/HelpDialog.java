package com.example.jakov.wherewasi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class HelpDialog extends AppCompatDialogFragment {
    private TextView helpTV;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());


        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.layout_help_dialog,null);

        builder.setView(view).setTitle("Help").setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Send us an e-mail", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "getodevs@gmail.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[WhereWasI]");

                startActivity(Intent.createChooser(emailIntent, "Send us an e-mail"));
            }
        });

        helpTV = view.findViewById(R.id.helpTV);

        helpTV.setMovementMethod(new ScrollingMovementMethod());
        return builder.create();



    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
}
