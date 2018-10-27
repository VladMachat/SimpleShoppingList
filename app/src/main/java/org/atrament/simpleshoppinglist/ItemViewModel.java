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
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class ItemViewModel extends AndroidViewModel {
    private ItemRepository repository;
    private LiveData<List<Item>> shoppingList;
    private LiveData<List<Item>> historyList;


    public ItemViewModel(@NonNull Application application) {
        super(application);
        repository = new ItemRepository(application);
        shoppingList = repository.getShoppingList();
        historyList = repository.getHistoryList();
    }

    public LiveData<List<Item>> getShoppingList() {
        return shoppingList;
    }

    public LiveData<List<Item>> getHistoryList() {
        return historyList;
    }

    public void insert(Item item) {
        repository.insert(item);
    }

    public void update(List<Item> items) {
        repository.update(items);
    }

    public void update(Item item) {
        repository.update(item);
    }

    public void delete(List<Item> items) {
        repository.delete(items);
    }

    public void delete(Item item) {
        repository.delete(item);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public List<Item> getAllItems() {
        return repository.getAllItems();
    }
}
