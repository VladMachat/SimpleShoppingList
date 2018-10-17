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
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment implements DataObserver {


    private ListView listView;
    private MainActivity activity;

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        activity = (MainActivity) getActivity();

        Button selectedToShoppingButton = view.findViewById(R.id.selectedToShoppingButton);
        selectedToShoppingButton.setEnabled(false);
        selectedToShoppingButton.setOnClickListener(v -> {
            selectedToShoppingButton.setEnabled(false);
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
            activity.storeValues(selected);


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
        listView.setOnItemClickListener((parent, view12, position, id) -> selectedToShoppingButton.setEnabled((listView.getCheckedItemCount() > 0)));
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
}
