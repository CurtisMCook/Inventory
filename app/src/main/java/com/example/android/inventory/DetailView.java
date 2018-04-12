package com.example.android.inventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Jason on 04/07/2016.
 */
public class DetailView extends MainActivity {

    String nameProduct = "";
    int productQuantity = 0;
    TextView quantityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view);

        mDbHelper = new InventoryHelper(this);
        // Get the database in write mode
        database = mDbHelper.getWritableDatabase();
        mDbHelper.onCreate(database);

        Intent intent = getIntent();
        final String productName = intent.getStringExtra("name");
        productQuantity = intent.getIntExtra("quantity", 0);
        nameProduct = productName;

        TextView nameTextView = (TextView) findViewById(R.id.detail_view_product_name_text_view);
        nameTextView.setText(productName);
        quantityTextView = (TextView) findViewById(R.id.detail_view_quantity_text_view);
        quantityTextView.setText(String.valueOf(productQuantity));

        Button order = (Button) findViewById(R.id.detail_view_order_button);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_SUBJECT, "Inventory order for " + productName);
                intent.putExtra(Intent.EXTRA_TEXT, "I would like to order another batch of " +
                        productName);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });


        Button sale = (Button) findViewById(R.id.detail_view_sale_button);
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = mDbHelper.getReadableDatabase();

                // Which row to update, based on the ID
                String selection = InventoryContract.Inventory.PRODUCT_QUANTITY + " LIKE ?";
                String[] selectionArgs = {String.valueOf(productQuantity)};

                if (productQuantity > 0) {
                    productQuantity -= 1;
                } else {
                    productQuantity = 0;
                }

                // New value for one column
                ContentValues values = new ContentValues();
                values.put(InventoryContract.Inventory.PRODUCT_QUANTITY, productQuantity);

                int count = database.update(
                        InventoryContract.Inventory.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);

                quantityTextView.setText(String.valueOf(productQuantity));
            }
        });

        Button shipment = (Button) findViewById(R.id.detail_view_shipment_button);
        shipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = mDbHelper.getReadableDatabase();

                // Which row to update, based on the ID
                String selection = InventoryContract.Inventory.PRODUCT_QUANTITY + " LIKE ?";
                String[] selectionArgs = {String.valueOf(productQuantity)};

                productQuantity += 1;

                // New value for one column
                ContentValues values = new ContentValues();
                values.put(InventoryContract.Inventory.PRODUCT_QUANTITY, productQuantity);

                int count = database.update(
                        InventoryContract.Inventory.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);

                quantityTextView.setText(String.valueOf(productQuantity));
            }
        });

        Button delete = (Button) findViewById(R.id.detail_view_delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeletePressed();
            }
        });
    }

    public void delete() {
        // Define 'where' part of query.
        String selection = InventoryContract.Inventory.PRODUCT_NAME + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {nameProduct};
        // Issue SQL statement.
        database.delete(InventoryContract.Inventory.TABLE_NAME, selection, selectionArgs);

        Intent eventIntent = new Intent(this, MainActivity.class);
        startActivity(eventIntent);
    }

    /**
     * Source: http://stackoverflow.com/questions/2257963/how-to-show-a-dialog-to-confirm-that-the-user-wishes-to-exit-an-android-activity
     */
    public void onDeletePressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("DELETING")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}