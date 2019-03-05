package com.example.jakov.wherewasi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import static android.support.constraint.Constraints.TAG;

public class SettingsDialog extends AppCompatDialogFragment {
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private EditText serviceTimeET;
    private SeekBar seekbar;
    private TextView seekbarTV;
    SharedPreferences prefs;
    String provider = "";
    int time = 0;
    int mapTime = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());


        prefs =  getActivity().getSharedPreferences("MyValues", 0);
        time = prefs.getInt("Service time", 15);
        mapTime = prefs.getInt("mapTime", 200);
        provider = prefs.getString("Provider", "Network");
        final String providerOriginal = "" + provider;

        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.layout_settings_dialog,null);

        builder.setView(view).setTitle("Settings").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor saveValue = prefs.edit();
                if (radioGroup.getCheckedRadioButtonId() == R.id.networkRadio) provider = "Network";
                else provider = "GPS";
                if (!serviceTimeET.getText().toString().isEmpty()) time = Integer.parseInt(serviceTimeET.getText().toString());
                saveValue.putInt("mapTime", mapTime);
                saveValue.putString("Provider", provider);
                saveValue.putInt("Service time", time);
                saveValue.apply();

                Toast.makeText(getActivity(),"Service time: " + (time < 60 ? time + " s" : time/60 + " min " + time%60 + " s") + "\nLocation provider: " + provider + "\nMap refresh time: " + (mapTime < 1000 ? mapTime + " ms" : mapTime / 1000.0 + " s" ) + (!provider.equals(providerOriginal) ? "\n\nrestart the app for provider settings to apply" : ""), Toast.LENGTH_SHORT ).show();

            }
        });

        radioGroup = view.findViewById(R.id.radioGroup);
        if (provider.equals("Network")) {
            radioGroup.check(R.id.networkRadio);
        } else {radioGroup.check(R.id.gpsRadio);

        }

        serviceTimeET = view.findViewById(R.id.serviceTimeET);
        serviceTimeET.setTransformationMethod(null);
        String timee = "" + time;
        serviceTimeET.setText(timee);
        seekbar = view.findViewById(R.id.seekBar);
        seekbarTV = view.findViewById(R.id.seekbarTV);
        seekbarTV.setText("Map refresh time: " + (mapTime < 1000 ? mapTime + " ms" : mapTime / 1000.0 + " s"));
        seekbar.setProgress(mapTime*100/2500);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mapTime = i == 0 ? 25 : i*25;
                seekbarTV.setText("Map refresh time: " + (mapTime < 1000 ? mapTime + " ms" : mapTime / 1000.0 + " s"));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return builder.create();



    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
}
