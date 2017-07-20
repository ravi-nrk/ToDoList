package com.sargent.mark.todolist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


import com.sargent.mark.todolist.data.Category;
import com.sargent.mark.todolist.data.CategoryModel;
import com.sargent.mark.todolist.data.Contract;
import com.sargent.mark.todolist.data.DBHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddToDoFragment.OnDialogCloseListener, AddToDoFragment.OnCategoriesRequest, UpdateToDoFragment.OnUpdateDialogCloseListener {

    private RecyclerView rv;
    private FloatingActionButton button;
    private DBHelper helper;
    private Cursor cursor;
    private SQLiteDatabase db;
    ToDoListAdapter adapter;
    private final String TAG = "mainactivity";
    int categoryId = 0;
    private Spinner sp_category;
    private SpinnerAdapter spinnerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "oncreate called in main activity");
        button = (FloatingActionButton) findViewById(R.id.addToDo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                AddToDoFragment frag = new AddToDoFragment();
                frag.show(fm, "addtodofragment");
            }
        });
        rv = (RecyclerView) findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (db != null) db.close();
        if (cursor != null) cursor.close();
    }

    @Override
    protected void onStart() {
        super.onStart();

        helper = new DBHelper(this);
        db = helper.getWritableDatabase();
        //Based on categoryId list loaded,if the app in background it loads based on already filtered category
        if (categoryId == 0) {
            cursor = getAllItems(db);
        } else {
            cursor = getAllItems(db, categoryId);
        }

        loadTodo(cursor);

    }

    public void loadTodo(Cursor c) {
        adapter = new ToDoListAdapter(c, new ToDoListAdapter.ItemClickListener() {

            @Override
            public void onItemClick(int pos, String description, String duedate, int category, long id) {
                Log.d(TAG, "item click id: " + id);
                String[] dateInfo = duedate.split("-");
                int year = Integer.parseInt(dateInfo[0].replaceAll("\\s", ""));
                int month = Integer.parseInt(dateInfo[1].replaceAll("\\s", ""));
                int day = Integer.parseInt(dateInfo[2].replaceAll("\\s", ""));

                FragmentManager fm = getSupportFragmentManager();

                UpdateToDoFragment frag = UpdateToDoFragment.newInstance(year, month, day, description, category, id);
                frag.show(fm, "updatetodofragment");
            }
        }, new ToDoListAdapter.OnUpdateToDODoneStatusListener() {
            //Implemented isDone/unDone status from the Recyclerview checkbox on each ToDo
            @Override
            public void updateDoneStatus(int isDone, long id) {
                //Here updating the status on Each to-do item based CheckBox check status
                updateToDoDoneStatus(db, isDone, id);
                if (categoryId == 0) {
                    adapter.swapCursor(getAllItems(db));
                } else {
                    adapter.swapCursor(getAllItems(db, categoryId));
                }


            }
        });

        rv.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                Log.d(TAG, "passing id: " + id);
                removeToDo(db, id);
                if (categoryId == 0) {
                    adapter.swapCursor(getAllItems(db));
                } else {
                    adapter.swapCursor(getAllItems(db, categoryId));
                }

            }
        }).attachToRecyclerView(rv);
    }


    @Override
    public void closeDialog(int year, int month, int day, String description, int category) {
        addToDo(db, description, formatDate(year, month, day), category);
        if (categoryId == 0) {
            cursor = getAllItems(db);
        } else {
            cursor = getAllItems(db, categoryId);
        }

        adapter.swapCursor(cursor);
    }

    public String formatDate(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month + 1, day);
    }


    private Cursor getAllItems(SQLiteDatabase db) {
        return db.query(Contract.TABLE_TODO.TABLE_NAME, null, null, null, null, null, Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE);
    }

    private Cursor getAllItems(SQLiteDatabase db, int category) {
        //Here this method returns To-Do list based on selected Category

        String selection = Contract.TABLE_TODO.COLUMN_NAME_CATEGORY_TYPE + "=?";
        String[] selectionArgs = {"" + category};
        return db.query(Contract.TABLE_TODO.TABLE_NAME, null, selection, selectionArgs, null, null, Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE);
    }

    private long addToDo(SQLiteDatabase db, String description, String duedate, int category) {
        //Added 2 columns CategoryType for category
        //IsDone status is for to mark that task as done/undone
        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY_TYPE, category);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_IS_DONE, 0);
        return db.insert(Contract.TABLE_TODO.TABLE_NAME, null, cv);
    }

    private boolean removeToDo(SQLiteDatabase db, long id) {
        Log.d(TAG, "deleting id: " + id);
        return db.delete(Contract.TABLE_TODO.TABLE_NAME, Contract.TABLE_TODO._ID + "=" + id, null) > 0;
    }


    private int updateToDo(SQLiteDatabase db, int year, int month, int day, String description, int category, int isDone, long id) {
        //Here performing updation on db
        String duedate = formatDate(year, month - 1, day);

        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY_TYPE, category);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_IS_DONE, isDone);

        return db.update(Contract.TABLE_TODO.TABLE_NAME, cv, Contract.TABLE_TODO._ID + "=" + id, null);
    }

    private int updateToDoDoneStatus(SQLiteDatabase db, int isDone, long id) {

        //Here updating each to-do based on check status
        //IS_DONE=0 means unDone
        //IS_DONE=1 means itsDone

        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_IS_DONE, isDone);
        return db.update(Contract.TABLE_TODO.TABLE_NAME, cv, Contract.TABLE_TODO._ID + "=" + id, null);
    }

    @Override
    public void closeUpdateDialog(int year, int month, int day, String description, int category, int isDone, long id) {
        updateToDo(db, year, month, day, description, category, isDone, id);
        adapter.swapCursor(getAllItems(db));
    }


    @Override
    public ArrayList<CategoryModel> categoriesRequest() {
        //Here retreiving list of categories from database

        Cursor cursor = db.query(Category.TABLE_CATEGORY.TABLE_NAME, null, null, null, null, null, Category.TABLE_CATEGORY._ID);
        ArrayList<CategoryModel> categoryModels = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                CategoryModel model = new CategoryModel();
                model.setId(cursor.getInt(cursor.getColumnIndex(Category.TABLE_CATEGORY._ID)));
                model.setCategoryName(cursor.getString(cursor.getColumnIndex(Category.TABLE_CATEGORY.COLUMN_CATEGORY_NAME)));
                categoryModels.add(model);
            } while (cursor.moveToNext());

        }
        return categoryModels;

    }

    //This menu on mainactivity is for filtering To-Do based on selected category
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);

        MenuItem item = menu.findItem(R.id.filter);
        sp_category = (Spinner) MenuItemCompat.getActionView(item);

        ArrayList<CategoryModel> categoryModels = new ArrayList<>();
        //Default it should show All(category as 0)
        CategoryModel cm = new CategoryModel();
        cm.setId(0);
        cm.setCategoryName("All");
        categoryModels.add(cm);

        //Here adding categories list to the arraylist
        categoryModels.addAll(categoriesRequest());

        //Created spinner
        spinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_item, categoryModels);
        sp_category.setAdapter(spinnerAdapter);
        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //categoryid as we selected
                categoryId = spinnerAdapter.getItem(position).getId();
                Cursor c = null;
                if (categoryId != 0) {
                    c = getAllItems(db, categoryId);//retreving based on selected category id
                } else {
                    //if no category yet selected ,retreiving all to-do
                    c = getAllItems(db);
                }
                loadTodo(c);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return true;
    }
}
