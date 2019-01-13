package com.example.jakov.wherewasi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SearchDialog extends AppCompatDialogFragment {

    private EditText nameEditText;
    private EditText dateFromEditText;
    private EditText dateToEditText;
    private SearchDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.layout_search_dialog,null);


        builder.setView(view).setTitle("Search").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name=nameEditText.getText().toString();
                String dateFrom=dateFromEditText.getText().toString();
                String dateTo=dateToEditText.getText().toString();
                listener.applyText(name,dateFrom,dateTo);

            }
        });

        nameEditText = view.findViewById(R.id.nameEditText);


        return builder.create();
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener=(SearchDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "mustImplementDialogListener");
        }

    }

    public interface SearchDialogListener{
        void applyText(String logName, String dateFrom, String dateTo);
    }

}
