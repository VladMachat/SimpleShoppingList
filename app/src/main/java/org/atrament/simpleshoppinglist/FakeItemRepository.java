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

import java.util.ArrayList;
import java.util.List;

public class FakeItemRepository implements Repository<Item> {

    private List<Item> items;

    public FakeItemRepository() {
        items = new ArrayList<>();
        items.add(new Item("Pivo", false));
        items.add(new Item("Chleba", false));
        items.add(new Item("Rohlíky", true));
        items.add(new Item("Mléko", true));

    }

    @Override
    public void saveItem(Item item) {
        if (!items.contains(item)) {
            items.add(item);
        }

    }

    @Override
    public void removeItem(Item item) {
        if (!items.contains(item)) {
            items.remove(item);
        }

    }

    @Override
    public List<Item> getItems() {
        return items;
    }

    @Override
    public int getSize() {
        return items.size();
    }
}
