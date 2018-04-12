package com.example.android.inventory;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jason on 04/07/2016.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    Context context;
    InventoryHelper mDbHelper;
    SQLiteDatabase database;

    /**
     * Constructor
     *
     * @param context
     * @param products
     */
    public ProductAdapter(Activity context, ArrayList<Product> products, InventoryHelper mDbHelper,
                          SQLiteDatabase database) {
        super(context, 0, products);
        this.context = context;
        this.mDbHelper = mDbHelper;
        this.database = database;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if existing view is being reused, otherwise inflate view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        //Get the object located at this position
        final Product product = getItem(position);

        Uri imageUri = Uri.parse(product.getProductImage());
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.list_item_image_view);
        imageView.setImageURI(null);
        imageView.setImageURI(imageUri);


        // Find the TextView in the list_item.xml layout with the ID product_name_edit_text
        final TextView nameOfProduct = (TextView) listItemView.findViewById(R.id.list_item_product_name_text);
        // set the text on the name TextView
        nameOfProduct.setText(product.getProductName());

        // Find the TextView in the list_item.xml layout with the ID product_name_edit_text
        final TextView quantity = (TextView) listItemView.findViewById(R.id.list_item_quantity_text);
        // set the text on the name TextView
        quantity.setText(String.valueOf("Qty: " + product.getProductQuantity()));

        // Find the TextView in the list_item.xml layout with the ID product_name_edit_text
        TextView price = (TextView) listItemView.findViewById(R.id.list_item_price_text);
        // set the text on the name TextView
        price.setText(String.valueOf("Price: $" + product.getProductPrice()));

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventIntent = new Intent(context, DetailView.class);
                eventIntent.putExtra("name", product.getProductName());
                eventIntent.putExtra("quantity", product.getProductQuantity());
                context.startActivity(eventIntent);
            }
        });

        Button saleButton = (Button) listItemView.findViewById(R.id.list_item_sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                database = mDbHelper.getReadableDatabase();

                // Which row to update, based on the ID
                String selection = InventoryContract.Inventory.PRODUCT_QUANTITY + " LIKE ?";
                String[] selectionArgs = {String.valueOf(product.getProductQuantity())};

                product.setProductQuantity(product.getProductQuantity() - 1);

                // New value for one column
                ContentValues values = new ContentValues();
                values.put(InventoryContract.Inventory.PRODUCT_QUANTITY, product.getProductQuantity());

                int count = database.update(
                        InventoryContract.Inventory.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);

                quantity.setText(String.valueOf("Qty: " + product.getProductQuantity()));
            }
        });

        return listItemView;


    }
}

