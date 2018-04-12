package com.example.android.inventory;

import android.provider.BaseColumns;

/**
 * Created by Jason on 04/07/2016.
 */
public class InventoryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public InventoryContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class Inventory implements BaseColumns {
        public static final String TABLE_NAME = "inventory";
        public static final String PRODUCT_NAME = "name";
        public static final String PRODUCT_IMAGE = "image";
        public static final String PRODUCT_QUANTITY = "quantity";
        public static final String PRODUCT_PRICE = "price";
    }
}
