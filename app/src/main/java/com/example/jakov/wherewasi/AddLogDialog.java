package com.example.jakov.wherewasi;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import static android.support.constraint.Constraints.TAG;

public class AddLogDialog extends AppCompatDialogFragment {

    private EditText logNameET;
    private CheckBox activeCB;

    private AddLogDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.add_log_dialog,null);


        builder.setView(view).setTitle("Add log").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name=logNameET.getText().toString();
                boolean setActive = activeCB.isChecked();
                listener.addLog(name,setActive);

            }
        });

        logNameET = view.findViewById(R.id.logNameET);
        activeCB = view.findViewById(R.id.activeCB);


        return builder.create();
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener=(AddLogDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "mustImplementDialogListener");
        }

    }

    public interface AddLogDialogListener{
        void addLog(String logName, boolean setActive);
    }

}
