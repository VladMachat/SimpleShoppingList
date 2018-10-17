/*
 * MIT License
 *
 * Copyright (c) 2018 Atrament
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.atrament.simpleshoppinglist;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingFragment extends Fragment implements DataObserver {

    private ListView listView;
    private MainActivity activity;

    public ShoppingFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);
        activity = (MainActivity) getActivity();
        listView = view.findViewById(R.id.shoppingList);
        listView.setFocusable(true);
        listView.setFocusableInTouchMode(true);
        listView.requestFocus();
        onDataChanged();
        Button selectedToHistoryButton = view.findViewById(R.id.selectedToHistoryButton);
        selectedToHistoryButton.setEnabled(false);
        selectedToHistoryButton.setOnClickListener(v -> {
            selectedToHistoryButton.setEnabled(false);
            SparseBooleanArray sba = listView.getCheckedItemPositions();
            List<ContentValues> selected = new ArrayList<>();
            for (int i = 0; i < listView.getCount(); i++) {
                if (sba.get(i)) {
                    ContentValues values = new ContentValues();
                    values.put("name", getNameFromCursorAt(i));
                    values.put("archived", 1);
                    selected.add(values);
                    listView.setItemChecked(i, false);
                }
            }
            activity.storeValues(selected);


        });

        AutoCompleteTextView textView = view.findViewById(R.id.textView);
        SimpleCursorAdapter hintAdapter = activity.getHintCursorAdapter();
        hintAdapter.setCursorToStringConverter(cursor -> {
            int col = cursor.getColumnIndex("name");
            return cursor.getString(col);

        });
        hintAdapter.setFilterQueryProvider(constraint -> {
            SQLiteDatabase db = new DbHelper(activity).getReadableDatabase();
            Cursor cursor = null;
            if (constraint != null) {
                cursor = db.rawQuery("SELECT _id, name FROM items WHERE archived = ? AND name like ?", new String[]{"1", constraint.toString() + "_%"});

            }

            return cursor;
        });

        textView.setAdapter(hintAdapter);

        Button addButton = view.findViewById(R.id.addButton);
        addButton.setEnabled(false);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addButton.setEnabled(count > 2);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textView.setOnItemClickListener((parent, view13, position, id) -> addButton.performClick());
        textView.setOnKeyListener((v, keyCode, event) -> {
            if (textView.getText().toString().length() >= 2 && keyCode == KeyEvent.KEYCODE_ENTER) {
                addButton.performClick();
            }
            return false;
        });



        addButton.setOnClickListener(e -> {
            ContentValues values = new ContentValues();
            values.put("name", textView.getText().toString());
            values.put("archived", 0);
            activity.storeValues(values);
            textView.setText("");

        });

        listView.setOnItemLongClickListener((parent, view1, position, id) -> {
            SQLiteCursor cursor = (SQLiteCursor) listView.getItemAtPosition(position);
            int columnIndex = cursor.getColumnIndex("name");
            String item = cursor.getString(columnIndex);
            ContentValues values = new ContentValues();
            values.put("name", item);
            values.put("archived", 1);
            activity.storeValues(values);
            return true;
        });
        listView.setOnItemClickListener((parent, view12, position, id) -> selectedToHistoryButton.setEnabled((listView.getCheckedItemCount() > 0)));


        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Shopping fragment", "started");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Shopping fragment", "onResume: resumed");
    }

    @Override
    public void onDataChanged() {
        listView.setAdapter(activity.getCursorAdapter(0));

    }

    private String getNameFromCursorAt(int position) {
        SQLiteCursor cursor = (SQLiteCursor) listView.getItemAtPosition(position);
        int columnIndex = cursor.getColumnIndex("name");
        return cursor.getString(columnIndex);
    }
}
