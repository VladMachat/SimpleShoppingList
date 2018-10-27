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
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private ItemViewModel model;

    private ListView listView;
    private Button selectedToShoppingButton;
    private Button deleteSelectedButton;
    private Button deleteAllButton;

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        listView = view.findViewById(R.id.historyList);
        selectedToShoppingButton = view.findViewById(R.id.selectedToShoppingButton);
        deleteSelectedButton = view.findViewById(R.id.deleteSelectedButton);

        selectedToShoppingButton.setEnabled(false);
        selectedToShoppingButton.setOnClickListener(v -> {
            selectedToShoppingButton.setEnabled(false);
            deleteSelectedButton.setEnabled(false);
            SparseBooleanArray sba = listView.getCheckedItemPositions();

            for (int i = 0; i < listView.getCount(); i++) {
                if (sba.get(i)) {
                    Item item = model.getHistoryList().getValue().get(i);
                    item.setArchived(false);
                    model.update(item);
                }
            }

        });

        listView.setOnItemLongClickListener((parent, view1, position, id) -> {
            Item item = model.getHistoryList().getValue().get(position);
            item.setArchived(false);
            model.update(item);
            return true;
        });


        deleteSelectedButton.setEnabled(false);
        deleteSelectedButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.delete_dialog_title)
                    .setMessage(R.string.delete_dialog_message)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        selectedToShoppingButton.setEnabled(false);
                        deleteSelectedButton.setEnabled(false);
                        SparseBooleanArray sba = listView.getCheckedItemPositions();
                        for (int i = 0; i < listView.getCount(); i++) {
                            if (sba.get(i)) {
                                Item item = model.getHistoryList().getValue().get(i);
                                model.delete(item);
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });

        listView.setOnItemClickListener((parent, view12, position, id) -> {
            updateButtons();

        });
        deleteAllButton = view.findViewById(R.id.deleteAllButton);
        deleteAllButton.setOnClickListener((v) -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.delete_all_dialog_title)
                    .setMessage(R.string.delete_all_dialog_message)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        selectedToShoppingButton.setEnabled(false);
                        deleteSelectedButton.setEnabled(false);
                        model.deleteAll();

                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });
        return view;
    }

    private void updateButtons() {
        int selectedCount = listView.getCheckedItemCount();
        selectedToShoppingButton.setEnabled((selectedCount > 0));
        deleteSelectedButton.setEnabled((selectedCount > 0));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = ViewModelProviders.of(this).get(ItemViewModel.class);
        model.getHistoryList().observe(this, items -> {
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
