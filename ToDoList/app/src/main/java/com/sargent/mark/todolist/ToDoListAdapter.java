package com.sargent.mark.todolist;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sargent.mark.todolist.data.Contract;

/**
 * Created by mark on 7/4/17.
 */

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ItemHolder> {

    private Cursor cursor;
    private ItemClickListener listener;
    private OnUpdateToDODoneStatusListener statusListener;
    private String TAG = "todolistadapter";
    private Context context;

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item, parent, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.bind(holder, position);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public interface ItemClickListener {
        void onItemClick(int pos, String description, String duedate, int category, long id);
    }

    public ToDoListAdapter(Cursor cursor, ItemClickListener listener, OnUpdateToDODoneStatusListener onUpdateToDODoneStatusListener) {
        this.cursor = cursor;
        this.listener = listener;
        this.statusListener = onUpdateToDODoneStatusListener;
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) cursor.close();
        cursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    //Interface which interacts with mainactivity on User Update his to-do item done status
    public interface OnUpdateToDODoneStatusListener {
        void updateDoneStatus(int isDone, long id);
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView descr;
        TextView due;
        String duedate;
        String description;
        CheckBox cb_markasdone;
        long id;
        int category;
        ImageView ivCategory;


        ItemHolder(View view) {
            super(view);
            descr = (TextView) view.findViewById(R.id.description);
            due = (TextView) view.findViewById(R.id.dueDate);
            cb_markasdone = (CheckBox) view.findViewById(R.id.mark_as_done);
            ivCategory= (ImageView) view.findViewById(R.id.image);
            view.setOnClickListener(this);
        }

        public void bind(ItemHolder holder, int pos) {
            cursor.moveToPosition(pos);
            id = cursor.getLong(cursor.getColumnIndex(Contract.TABLE_TODO._ID));
            Log.d(TAG, "deleting id: " + id);
            int isDone = cursor.getInt(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_IS_DONE));
            category = cursor.getInt(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY_TYPE));
            //Showcasing category type image based on to-do category selected by user while adding to-do
            switch(category){
                case 1:
                    ivCategory.setImageResource(R.drawable.shopping);
                    break;
                case 2:
                    ivCategory.setImageResource(R.drawable.travelling);
                    break;
                case 3:
                    ivCategory.setImageResource(R.drawable.meeting);
                    break;
                case 4:
                    ivCategory.setImageResource(R.drawable.work);
                    break;
            }

            //Showcasing to-do done status with checkbox as checked /unchecked
            if (isDone == 1) {
                cb_markasdone.setChecked(true);
            } else {
                cb_markasdone.setChecked(false);
            }
            duedate = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE));
            description = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION));
            descr.setText(description);
            due.setText(duedate);
            holder.itemView.setTag(id);

            //Here providing mark to-do as done /undone by checking checkbox
            cb_markasdone.setOnClickListener(this);
            cb_markasdone.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (((CheckBox) v).isChecked()) {
                        Log.d(TAG, "id: " + id);
                        statusListener.updateDoneStatus(1, id);//Updating done status here
                        Toast.makeText(context, "You have done the activity", Toast.LENGTH_LONG).show();
                    } else {
                        Log.d(TAG, "id: " + id);
                        statusListener.updateDoneStatus(0, id);//Updating done status here
                        Toast.makeText(context, "You have not done the activity", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


        @Override
        public void onClick(View v) {
            //Opening to-do for updating
            int pos = getAdapterPosition();
            listener.onItemClick(pos, description, duedate, category, id);
        }
    }

}
