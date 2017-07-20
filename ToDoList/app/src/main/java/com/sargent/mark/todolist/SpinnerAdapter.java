package com.sargent.mark.todolist;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sargent.mark.todolist.data.CategoryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RAVI on 7/18/2017.
 */

public class SpinnerAdapter extends ArrayAdapter<CategoryModel> {

    Context context;
    ArrayList<CategoryModel> categoryModelArray;

    //Showing spinner items of categories
    public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<CategoryModel> objects) {
        super(context, textViewResourceId, (List<CategoryModel>) objects);

        this.context = context;
        categoryModelArray = objects;
    }

    @Override
    public int getCount() {
        return categoryModelArray.size();
    }

    @Nullable
    @Override
    public CategoryModel getItem(int position) {
        return categoryModelArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textview = (TextView) inflater.inflate(R.layout.spinner_item, null);
        //Setting Category name and id
        textview.setText(categoryModelArray.get(position).getCategoryName());
        textview.setTag(categoryModelArray.get(position).getId());

        return textview;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textview = (TextView) inflater.inflate(R.layout.spinner_item, null);

        //Setting Category name and id
        textview.setText(categoryModelArray.get(position).getCategoryName());
        textview.setTag(categoryModelArray.get(position).getId());
        return textview;
    }

}
