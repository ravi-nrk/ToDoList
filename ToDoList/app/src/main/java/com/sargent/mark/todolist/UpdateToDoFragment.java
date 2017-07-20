package com.sargent.mark.todolist;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.sargent.mark.todolist.data.CategoryModel;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by mark on 7/5/17.
 */

public class UpdateToDoFragment extends DialogFragment {

    private Spinner sp_category;
    private SpinnerAdapter spinnerAdapter;
    int categoryId = 0;
    private EditText toDo;
    private DatePicker dp;
    private Button add;
    private final String TAG = "updatetodofragment";
    private long id;


    public UpdateToDoFragment() {
    }

    public static UpdateToDoFragment newInstance(int year, int month, int day, String descrpition, int category, long id) {
        UpdateToDoFragment f = new UpdateToDoFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        args.putLong("id", id);
        args.putString("description", descrpition);
        args.putInt("category", category);

        f.setArguments(args);

        return f;
    }
/*
    public interface OnCategoriesRequest {
        ArrayList<CategoryModel> categoriesRequest();
    }*/

    //To have a way for the activity to get the data from the dialog
    public interface OnUpdateDialogCloseListener {
        void closeUpdateDialog(int year, int month, int day, String description, int category, int isDone, long id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_adder, container, false);
        toDo = (EditText) view.findViewById(R.id.toDo);
        dp = (DatePicker) view.findViewById(R.id.datePicker);
        add = (Button) view.findViewById(R.id.add);
        sp_category = (Spinner) view.findViewById(R.id.category_sp);

        int year = getArguments().getInt("year");
        int month = getArguments().getInt("month");
        int day = getArguments().getInt("day");
        id = getArguments().getLong("id");
        String description = getArguments().getString("description");
        categoryId = getArguments().getInt("category", 0);
        dp.updateDate(year, month, day);

        toDo.setText(description);

        //Reteiving Categories List to show as spinner while updating to-do
        AddToDoFragment.OnCategoriesRequest activity = (AddToDoFragment.OnCategoriesRequest) getActivity();
        ArrayList<CategoryModel> categoryModels = new ArrayList<>();
        categoryModels = activity.categoriesRequest();
        spinnerAdapter = new SpinnerAdapter(getActivity(),R.layout.spinner_item, categoryModels);
        sp_category.setAdapter(spinnerAdapter);
        int position=0;
        //Getting already added to-do category position to set that category on spinner
        for (int i = 0; i < categoryModels.size(); i++) {
            if (categoryModels.get(i).getId() == categoryId) {
                position=i;
            }
        }
        //Here setting existing category position on spinner
        sp_category.setSelection(position);

        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryId = spinnerAdapter.getItem(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        add.setText("Update");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Checking whether user selected Category or not
                if (categoryId == 0) {
                    Toast.makeText(getActivity(), "Please select category", Toast.LENGTH_LONG).show();
                } else {
                    UpdateToDoFragment.OnUpdateDialogCloseListener activity = (UpdateToDoFragment.OnUpdateDialogCloseListener) getActivity();
                    Log.d(TAG, "id: " + id);
                    activity.closeUpdateDialog(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), toDo.getText().toString(), categoryId, 0, id);
                    UpdateToDoFragment.this.dismiss();
                }
            }
        });

        return view;
    }
}