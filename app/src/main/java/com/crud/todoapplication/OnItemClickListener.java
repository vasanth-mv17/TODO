package com.crud.todoapplication;

import com.crud.todoapplication.model.TodoItem;

public interface OnItemClickListener {

    void onCheckBoxClick(TodoItem todoItem);
    void onCloseIconClick(final TodoItem todoItem);
}
