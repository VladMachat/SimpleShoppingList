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
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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
        onDataChanged();
        Button selectedToHistortyButton = view.findViewById(R.id.selectedToHistoryButton);
        selectedToHistortyButton.setOnClickListener(v -> {
            selectedToHistortyButton.setEnabled(false);
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

        EditText newItemText = view.findViewById(R.id.textView);
        Button addButton = view.findViewById(R.id.addButton);

        addButton.setOnClickListener(e -> {
            //TODO zkontrolovat jestli bylo vůbec něco zadáno aby se nepřidávaly prázdné položky
            // nejlépe udělat aby se tlačítko add aktivovalo jenom když je něco zadáno
            ContentValues values = new ContentValues();
            values.put("name", newItemText.getText().toString());
            values.put("archived", 0);
            activity.storeValues(values);
            newItemText.setText("");

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
        listView.setOnItemClickListener((parent, view12, position, id) -> selectedToHistortyButton.setEnabled((listView.getCheckedItemCount() > 0)));

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
        listView.setAdapter(activity.getCursor(0));

    }

    private String getNameFromCursorAt(int position) {
        SQLiteCursor cursor = (SQLiteCursor) listView.getItemAtPosition(position);
        int columnIndex = cursor.getColumnIndex("name");
        return cursor.getString(columnIndex);
    }
}
