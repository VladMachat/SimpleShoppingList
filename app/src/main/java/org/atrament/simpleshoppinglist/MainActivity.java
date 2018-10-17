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
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.facebook.stetho.Stetho;

import java.util.List;


public class MainActivity extends AppCompatActivity {


    private DbHelper dbHelper;
    private Pager pagerAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Stetho.initializeWithDefaults(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DbHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabs = findViewById(R.id.tabs);


        ViewPager pagerView = findViewById(R.id.pager);
        pagerAdapter = new Pager(getSupportFragmentManager(), tabs.getTabCount());
        pagerView.setAdapter(pagerAdapter);

        pagerView.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pagerView.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public void storeValues(ContentValues values) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            int result = (int) db.insertWithOnConflict("items", null, values, SQLiteDatabase.CONFLICT_IGNORE);
            if (result == -1) {
                db.update("items", values, "name=?", new String[]{values.getAsString("name")});
            }
        }
        pagerAdapter.updatePages();
    }

    public void storeValues(List<ContentValues> values) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            try {
                db.beginTransaction();
                for (ContentValues v : values) {
                    int result = (int) db.insertWithOnConflict("items", null, v, SQLiteDatabase.CONFLICT_IGNORE);
                    if (result == -1) {
                        db.update("items", v, "name=?", new String[]{v.getAsString("name")});
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        pagerAdapter.updatePages();

    }

    public SimpleCursorAdapter getCursorAdapter(int archived) {
        SimpleCursorAdapter result;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("items",
                new String[]{"_id", "name", "archived"},
                "archived=?",
                new String[]{Integer.toString(archived)}, null, null, "name ASC");
        result = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_multiple_choice,
                cursor, new String[]{"name"},
                new int[]{android.R.id.text1},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        return result;
    }

    public SimpleCursorAdapter getHintCursorAdapter() {
        SimpleCursorAdapter result;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("items",
                new String[]{"_id", "name"},
                "archived=?",
                new String[]{Integer.toString(1)}, null, null, "name ASC");
        result = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                cursor, new String[]{"name"},
                new int[]{android.R.id.text1},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        return result;
    }

}
