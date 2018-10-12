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


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingFragment extends Fragment {

    public ShoppingFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shopping, container, false);
        MainActivity activity = (MainActivity) getActivity();
        ListView listView = view.findViewById(R.id.shoppingList);

        listView.setAdapter((ArrayAdapter) activity.getShoppingAdapter());
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemLongClickListener((parent, view1, position, id) -> {
            String item = activity.getItems().getShoppingList().get(position);
            activity.moveItem(item, activity.getItems().getShoppingList(), activity.getItems().getHistoryList());
            return true;
        });

        EditText editText = (AutoCompleteTextView) view.findViewById(R.id.textView);
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, activity.getItems().getHistoryList());
        ((AutoCompleteTextView) editText).setAdapter(autoCompleteAdapter);
        Button addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(e -> {
            if (!editText.getText().toString().equals("")) {
                activity.moveItem(editText.getText().toString(), activity.getItems().getHistoryList(), activity.getItems().getShoppingList());
                editText.setText("");
            }
        });


        return view;
    }

}
