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

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ItemRepository {

    private ItemDao itemDao;
    private LiveData<List<Item>> shoppingList;
    private LiveData<List<Item>> historyList;

    public ItemRepository(Application app) {
        AppDatabase db = AppDatabase.getDatabase(app);
        itemDao = db.itemDao();
        shoppingList = itemDao.getAllByArchived(false);
        historyList = itemDao.getAllByArchived(true);
    }

    public LiveData<List<Item>> getShoppingList() {
        return shoppingList;
    }

    public LiveData<List<Item>> getHistoryList() {
        return historyList;
    }

    public void insert(Item item) {
        AsyncTask.execute(() -> itemDao.insert(item));
    }

    public void update(List<Item> items) {
        AsyncTask.execute(() -> {
            for (Item item : items)
                itemDao.updateItem(item);
        });

    }

    public void update(Item item) {
        AsyncTask.execute(() -> itemDao.updateItem(item));
    }

    public void delete(List<Item> items) {
        AsyncTask.execute(() -> {
            for (Item item : items) {
                itemDao.deleteItem(item);
            }
        });

    }

    public void deleteAll() {
        AsyncTask.execute(() -> {
            itemDao.deleteAll();
        });

    }

    public void delete(Item item) {
        AsyncTask.execute(() -> {
            itemDao.deleteItem(item);
        });

    }

    public List<Item> getAllItems() {
        try {
            return new GetAllTask(itemDao).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static class GetAllTask extends AsyncTask<Void, Void, List<Item>> {

        private ItemDao dao;

        public GetAllTask(ItemDao dao) {
            this.dao = dao;
        }

        @Override
        protected List<Item> doInBackground(Void... voids) {
            return dao.getAll();
        }
    }
}
