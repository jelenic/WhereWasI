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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import static android.support.constraint.Constraints.TAG;

public class SearchDialog extends AppCompatDialogFragment {

    private EditText nameEditText;
    private TextView dateFromTextView;
    private TextView dateToTextView;

    private DatePickerDialog.OnDateSetListener mDateListenerFrom;
    private DatePickerDialog.OnDateSetListener mDateListenerTo;
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
                String dateFrom= dateFromTextView.getText().toString();
                String dateTo=dateToTextView.getText().toString();
                dateFrom = dateFrom.isEmpty() ? dateFrom : dateFrom.substring(0,10).replace(".","");
                dateTo = dateTo.isEmpty() ? dateTo : dateTo.substring(0,10).replace(".","");
                Log.d(TAG, "onClick: " + dateFrom + " " + dateTo);
                listener.applyText(name,dateTo,dateFrom);

            }
        });

        nameEditText = view.findViewById(R.id.nameEditText);
        dateFromTextView = view.findViewById(R.id.dateFromTextView);
        dateFromTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal= Calendar.getInstance();
                int year=cal.get(Calendar.YEAR);
                int month=cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog=new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Dialog_MinWidth,mDateListenerFrom,year,month,day);
                dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dateDialog.show();

            }
        });
        mDateListenerFrom = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String m,d;
                m=month+"";
                d=day+"";
                if (month<10){
                    m="0" + month;
                }
                if (day<10){
                    d="0"+day;
                }

                String dateFrom = year + "." +m + "." + d;
                dateFromTextView.setText(dateFrom);

            }
        };


        dateToTextView = view.findViewById(R.id.dateToTextView);
        dateToTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal= Calendar.getInstance();
                int year=cal.get(Calendar.YEAR);
                int month=cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog=new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Dialog_MinWidth,mDateListenerTo,year,month,day);
                dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dateDialog.show();

            }
        });
        mDateListenerTo = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String m,d;
                m=month+"";
                d=day+"";
                if (month<10){
                    m="0" + month;
                }
                if (day<10){
                    d="0"+day;
                }

                String dateTo = year + "." +m + "." + d;
                dateToTextView.setText(dateTo);

            }
        };

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
        void applyText(String logName, String dateTo, String dateFrom);
    }

}
