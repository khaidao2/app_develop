package com.example.k23411t_app;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Giỏ hàng phía client — giữ trong bộ nhớ suốt phiên chạy (singleton tĩnh).
 */
public class Cart {
    public static class Item {
        public String productId;
        public String productName;
        public double price;
        public long stock;   // tồn kho tại thời điểm thêm, dùng để trừ kho khi checkout
        public int qty;

        public double subtotal() {
            return price * qty;
        }
    }

    private static final LinkedHashMap<String, Item> items = new LinkedHashMap<>();

    public static void add(String productId, String productName, double price, long stock) {
        Item it = items.get(productId);
        if (it == null) {
            it = new Item();
            it.productId = productId;
            it.productName = productName;
            it.price = price;
            it.stock = stock;
            it.qty = 0;
            items.put(productId, it);
        }
        it.qty++;
    }

    public static Collection<Item> items() {
        return items.values();
    }

    public static int count() {
        int c = 0;
        for (Item i : items.values()) c += i.qty;
        return c;
    }

    public static double total() {
        double t = 0;
        for (Item i : items.values()) t += i.subtotal();
        return t;
    }

    public static void remove(String productId) {
        items.remove(productId);
    }

    public static void clear() {
        items.clear();
    }

    public static boolean isEmpty() {
        return items.isEmpty();
    }
}
