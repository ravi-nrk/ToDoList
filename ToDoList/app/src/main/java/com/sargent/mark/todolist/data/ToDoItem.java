package com.sargent.mark.todolist.data;

/**
 * Created by mark on 7/4/17.
 */

public class ToDoItem {
    private String description;
    private String dueDate;
    private boolean isDone;
    private int categoryType;


    public ToDoItem(String description, String dueDate,boolean isDone,int categoryType) {
        this.description = description;
        this.dueDate = dueDate;
        this.isDone=isDone;
        this.categoryType=categoryType;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public int getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
