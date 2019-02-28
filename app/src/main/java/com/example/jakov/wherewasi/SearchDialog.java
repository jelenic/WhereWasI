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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static android.support.constraint.Constraints.TAG;

public class SearchDialog extends AppCompatDialogFragment {

    private EditText nameEditText;
    private TextView LogsTV;
    private TextView dateFromTextView;
    private TextView dateToTextView;
    private DatePickerDialog.OnDateSetListener mDateListenerFrom;
    private DatePickerDialog.OnDateSetListener mDateListenerTo;
    private SearchDialogListener listener;
    private ArrayList<String> logs;
    private ArrayList<String> logList;




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.layout_search_dialog,null);

        logs = new ArrayList<>();
        logList = (ArrayList<String>) getArguments().getSerializable("logs");


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
                Log.d(TAG, "onClick: " + dateFrom + " " + dateTo + " " + name + " " + logs.size());
                if (!name.isEmpty() || !dateFrom.isEmpty() || !dateTo.isEmpty() || logs.size() > 0) listener.applyText(name,dateTo,dateFrom, logs);

            }
        });




        nameEditText = view.findViewById(R.id.nameEditText);
        LogsTV = view.findViewById(R.id.LogsTV);
        LogsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


                final boolean[] checkedItems = new boolean[logList.size()];
                Arrays.fill(checkedItems, Boolean.FALSE);
                builder.setTitle("Delete logs");
                String[] logsArray = new String[logList.size()];
                logsArray = logList.toArray(logsArray);
                //set multichoice
                builder.setMultiChoiceItems(logsArray, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                });
                // Set the positive/yes button click listener
                builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when click positive button
                        for (int i = 0; i<checkedItems.length; i++){
                            boolean checked = checkedItems[i];
                            if (checked) {
                                String logName = logList.get(i);
                                logs.add(logName);
                                LogsTV.append(logName + " ");

                            }
                        }

                    }
                });
                // Set the neutral/cancel button click listener
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when click the neutral button
                    }
                });
                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface
                dialog.show();
            }
        });
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
        void applyText(String name, String dateTo, String dateFrom, ArrayList<String> logs);
    }

}
