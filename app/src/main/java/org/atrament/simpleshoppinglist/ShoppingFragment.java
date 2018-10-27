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


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingFragment extends Fragment {

    private ListView listView;
    private ItemViewModel model;
    private Button selectedToHistoryButton;
    private Button addButton;
    private AutoCompleteTextView textView;

    private static String TAG = "ShoppingFragment";

    public ShoppingFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);

        listView = view.findViewById(R.id.shoppingList);
        textView = view.findViewById(R.id.textView);
        addButton = view.findViewById(R.id.addButton);
        selectedToHistoryButton = view.findViewById(R.id.selectedToHistoryButton);


        listView.setFocusable(true);
        listView.setFocusableInTouchMode(true);
        listView.requestFocus();

        selectedToHistoryButton.setEnabled(false);
        selectedToHistoryButton.setOnClickListener(v -> {
            selectedToHistoryButton.setEnabled(false);
            SparseBooleanArray sba = listView.getCheckedItemPositions();
            for (int i = 0; i < listView.getCount(); i++) {
                if (sba.get(i)) {
                    Item item = model.getShoppingList().getValue().get(i);
                    item.setArchived(true);
                    listView.setItemChecked(i, false);
                    model.update(item);

                }
            }

        });

        addButton.setEnabled(false);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                addButton.setEnabled(s.length() >= 2);
            }
        });

        textView.setOnItemClickListener((parent, view13, position, id) -> addButton.performClick());
        textView.setOnKeyListener((v, keyCode, event) -> {
            if (textView.getText().toString().length() >= 2 && keyCode == KeyEvent.KEYCODE_ENTER) {
                addButton.performClick();
            }
            return false;
        });

        textView.setOnItemClickListener((parent, view14, position, id) -> {
            String selected = parent.getItemAtPosition(position).toString();
            for (Item item : model.getHistoryList().getValue()) {
                if (item.getName().equals(selected)) {
                    item.setArchived(false);
                    model.update(item);
                }
            }
            textView.setText("");
        });

        addButton.setOnClickListener(e -> {
            List<Item> allItems = model.getAllItems();

            String itemString = textView.getText().toString();
            List<Item> result = new ArrayList<>();
            for (Item i : allItems) {
                if (i.getName().equals(itemString)) {
                    result.add(i);
                }
            }
            if (result.size() == 0) {
                model.insert(new Item(itemString, false));
            }
            textView.setText("");
        });

        listView.setOnItemLongClickListener((parent, view1, position, id) -> {
            Item item = model.getShoppingList().getValue().get(position);
            item.setArchived(true);
            model.update(item);
            return true;
        });
        listView.setOnItemClickListener((parent, view12, position, id) -> updateButtons());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateButtons();
    }

    private void updateButtons() {
        selectedToHistoryButton.setEnabled((listView.getCheckedItemCount() > 0));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: started");
        model = ViewModelProviders.of(getActivity()).get(ItemViewModel.class);
        model.getShoppingList().observe(this, items -> {
            ArrayAdapter<Item> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, items);
            listView.setAdapter(adapter);
            if (savedInstanceState != null) {
                List<Integer> selected = savedInstanceState.getIntegerArrayList("selected");
                savedInstanceState.clear();
                if ((selected != null) && (selected.size() > 0)) {
                    for (Integer i : selected) {
                        listView.setItemChecked(i, true);
                    }
                }
            }
            updateButtons();
        });

        model.getHistoryList().

                observe(this, items ->

                {
                    ArrayAdapter<Item> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, items);
                    textView.setAdapter(adapter);
                });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < listView.getCount(); i++) {
            if (listView.isItemChecked(i)) {
                list.add(i);
                listView.setItemChecked(i, false);
            }
        }
        outState.putIntegerArrayList("selected", (ArrayList<Integer>) list);

    }
}
