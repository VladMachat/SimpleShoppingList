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
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment implements DataObserver {


    private ListView listView;
    private MainActivity activity;
    private Button selectedToShoppingButton;
    private Button deleteSelectedButton;

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        activity = (MainActivity) getActivity();


        selectedToShoppingButton = view.findViewById(R.id.selectedToShoppingButton);
        selectedToShoppingButton.setEnabled(false);
        selectedToShoppingButton.setOnClickListener(v -> {
            selectedToShoppingButton.setEnabled(false);
            deleteSelectedButton.setEnabled(false);

            activity.storeValues(getCheckedValues());


        });

        listView = view.findViewById(R.id.historyList);
        onDataChanged();
        listView.setOnItemLongClickListener((parent, view1, position, id) -> {
            ContentValues values = new ContentValues();
            values.put("name", getNameFromCursorAt(position));
            values.put("archived", 0);
            activity.storeValues(values);
            return true;
        });

        deleteSelectedButton = view.findViewById(R.id.deleteSelectedButton);
        deleteSelectedButton.setEnabled(false);
        deleteSelectedButton.setOnClickListener(v -> {
            new AlertDialog.Builder(activity)
                    .setTitle("Delete items")
                    .setMessage("Really delete selected items?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        selectedToShoppingButton.setEnabled(false);
                        deleteSelectedButton.setEnabled(false);
                        activity.deleteItems(getCheckedValues());
                        Toast.makeText(activity, "Deleted", Toast.LENGTH_SHORT).show();

                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });

        listView.setOnItemClickListener((parent, view12, position, id) -> {
            int selectedCount = listView.getCheckedItemCount();
            selectedToShoppingButton.setEnabled((selectedCount > 0));
            deleteSelectedButton.setEnabled((selectedCount > 0));

        });
        return view;
    }


    @Override
    public void onDataChanged() {
        listView.setAdapter(activity.getCursorAdapter(1));
    }

    private String getNameFromCursorAt(int position) {
        SQLiteCursor cursor = (SQLiteCursor) listView.getItemAtPosition(position);
        int columnIndex = cursor.getColumnIndex("name");
        return cursor.getString(columnIndex);
    }

    private List<ContentValues> getCheckedValues() {
        SparseBooleanArray sba = listView.getCheckedItemPositions();
        List<ContentValues> selected = new ArrayList<>();
        for (int i = 0; i < listView.getCount(); i++) {
            if (sba.get(i)) {
                ContentValues values = new ContentValues();
                values.put("name", getNameFromCursorAt(i));
                values.put("archived", 0);
                selected.add(values);
                listView.setItemChecked(i, false);
            }
        }
        return selected;
    }
}
