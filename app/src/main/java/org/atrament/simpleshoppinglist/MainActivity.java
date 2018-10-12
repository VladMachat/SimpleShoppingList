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
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private FakeItemRepository items = new FakeItemRepository();
    private ArrayAdapter shoppingAdapter;
    private ArrayAdapter historyAdapter;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabs = findViewById(R.id.tabs);


        pager = findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new PageAdapter(getSupportFragmentManager(), tabs.getTabCount());
        pager.setAdapter(pagerAdapter);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Collections.sort(items.getShoppingList());
        Collections.sort(items.getHistoryList());

        historyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, items.getHistoryList());
        shoppingAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, items.getShoppingList());


    }

    public Adapter getHistoryAdapter() {
        return historyAdapter;
    }

    public Adapter getShoppingAdapter() {
        return shoppingAdapter;
    }

    public FakeItemRepository getItems() {
        return items;
    }


    public void moveItem(String item, List<String> from, List<String> to) {
        if (!to.contains(item)) {
            to.add(item);
        }
        if (from.contains(item)) {
            from.remove(item);
        }

        Collections.sort(to);
        historyAdapter.notifyDataSetChanged();
        shoppingAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        switch (pager.getCurrentItem()) {
            case 0:
                inflater.inflate(R.menu.shopping, menu);
                break;
            case 1:
                inflater.inflate(R.menu.history, menu);
                break;
        }


        return true;
    }
    
}
