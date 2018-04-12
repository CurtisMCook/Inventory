package com.example.android.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.InventoryContract.Inventory;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    InventoryHelper mDbHelper;
    SQLiteDatabase database;
    String imagePath = "";
    LinearLayout addProductListViewContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addProductListViewContainerView = (LinearLayout) findViewById(R.id
                .add_product_container_view);

        //Instantiate InventoryHelper
        mDbHelper = new InventoryHelper(this);
        // Get the database in write mode
        database = mDbHelper.getWritableDatabase();
        mDbHelper.onCreate(database);
        readFromDatabase();

        checkIfDatabaseTableHasValues(database);

        // Set onClickListener for the add a product image button
        Button uploadImageButton = (Button) findViewById(R.id.product_upload_image_button);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        // Set onClickListener for the submit a new product button
        Button submitButton = (Button) findViewById(R.id.product_submit_to_database_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateUserInput();
            }
        });

    }

    // Check to see if table is empty. If it is, display a default message to the user.
    private void checkIfDatabaseTableHasValues(SQLiteDatabase database) {
        database = mDbHelper.getWritableDatabase();

        TextView textView = (TextView) findViewById(R.id.default_text_view);

        String countQuery = "SELECT count(*) FROM " + Inventory.TABLE_NAME;
        Cursor cursor = database.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int itemCount = cursor.getInt(0);
        if (itemCount > 0) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Validates user input to make sure that the:
     * productName contains a non empty String
     * productQuantity returns a valid integer
     * productPrice returns a valid float
     * productImage returns a Uri
     * <p>
     * If validation succeeds, creates an object of type Product. Calls the insertIntoDatabase
     * method and passes the newly created Product object as a parameter
     * Returns a Toast message to the user if validation fails
     */
    private void validateUserInput() {
        Toast errorMessage;

        EditText productNameEditText = (EditText) findViewById(R.id.product_name_edit_text);
        String productName = productNameEditText.getText().toString();

        EditText productQuantityEditText = (EditText) findViewById(R.id.product_quantity_edit_text);
        String productQuantityString = productQuantityEditText.getText().toString();
        int productQuantity = 0;

        EditText productPriceEditText = (EditText) findViewById(R.id.product_price_edit_text);
        String productPriceString = productPriceEditText.getText().toString();
        double productPrice = 0.00;


        //Check to see if the product name input contains a value
        if (productName == null || productName.isEmpty()) {
            errorMessage = Toast.makeText(this, "Please enter product name", Toast.LENGTH_SHORT);
            errorMessage.show();
            return;
        }
        //Check to see if the product quantity input contains a value
        if (productQuantityString == null || productQuantityString.isEmpty()) {
            errorMessage = Toast.makeText(this, "Please enter a quantity", Toast
                    .LENGTH_SHORT);
            errorMessage.show();
            return;
        } else {
            try {
                productQuantity = Integer.parseInt(productQuantityString);
            } catch (Exception e) {
                errorMessage = Toast.makeText(this, "Please enter a integer value for the " +
                        "quantity eg: 5", Toast.LENGTH_SHORT);
                errorMessage.show();
                return;
            }
        }
        //Check to see if the product price input contains a value
        if (productPriceString == null || productPriceString.isEmpty()) {
            errorMessage = Toast.makeText(this, "Please enter a price", Toast.LENGTH_SHORT);
            errorMessage.show();
            return;
        } else {
            try {
                productPrice = Double.parseDouble(productPriceString);
            } catch (Exception e) {
                errorMessage = Toast.makeText(this, "Please enter a float value for the price eg:" +
                        " 9.99", Toast.LENGTH_SHORT);
                errorMessage.show();
                return;
            }
        }
        //Check to see if the product image contains a valid Uri
        if (imagePath == null || imagePath.isEmpty()) {
            errorMessage = Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT);
            errorMessage.show();
            return;
        }

        // Create a new Product using the users input data as parameters
        // Call the insertDatabase method passing in the newly created Product object as a parameter
        Product product = new Product(productName, imagePath, productQuantity, productPrice);
        insertIntoDatabase(product);

        // remove data from input fields
        productNameEditText.setText("");
        productQuantityEditText.setText("");
        productPriceEditText.setText("");
        imagePath = null;
        // hide add product view from user
        addProductListViewContainerView.setVisibility(View.GONE);

        // Calls the readFromDatabase method which populates the users list view
        readFromDatabase();
    }

    /**
     * Insert values into database
     *
     * @param product
     */
    public void insertIntoDatabase(Product product) {
        // Gets the data repository in write mode
        database = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Inventory.PRODUCT_NAME, product.getProductName());
        values.put(Inventory.PRODUCT_QUANTITY, product.getProductQuantity());
        values.put(Inventory.PRODUCT_PRICE, product.getProductPrice());
        values.put(Inventory.PRODUCT_IMAGE, product.getProductImage());

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = database.insert(
                Inventory.TABLE_NAME,
                Inventory.PRODUCT_NAME,
                values);

        checkIfDatabaseTableHasValues(database);
    }

    /**
     * Read database values and populate the list view
     */
    public void readFromDatabase() {

        // Create an ArrayList of type Place and add newly created Place objects to it
        final ArrayList<Product> products = new ArrayList<Product>();

        database = mDbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                Inventory.PRODUCT_NAME,
                Inventory.PRODUCT_QUANTITY,
                Inventory.PRODUCT_PRICE,
                Inventory.PRODUCT_IMAGE};

        // How you want the results sorted in the resulting Cursor
        String sortOrder = Inventory.PRODUCT_NAME + " DESC";

        Cursor cursor = database.query(Inventory.TABLE_NAME, projection, null, null, null, null,
                sortOrder);

        //if the cursor isnt null we will essentially iterate over rows and then columns
        //to form a table of data as per database.
        if (cursor != null) {

            //more to the first row
            cursor.moveToFirst();

            //iterate over rows
            for (int i = 0; i < cursor.getCount(); i++) {
                String name = "";
                String quantityString = "";
                String priceString = "";
                String image = "";
                //iterate over the columns
                for (int j = 0; j < cursor.getColumnNames().length; j++) {

                    switch (cursor.getColumnName(j)) {
                        case "name":
                            name = cursor.getString(j);
                            break;
                        case "quantity":
                            quantityString = cursor.getString(j);
                            break;
                        case "price":
                            priceString = cursor.getString(j);
                            break;
                        case "image":
                            image = cursor.getString(j);
                            break;
                        default:
                            break;
                    }
                }
                int quantity = Integer.parseInt(quantityString);
                double price = Double.parseDouble(priceString);
                products.add(new Product(name, image, quantity, price));
                //move to the next row
                cursor.moveToNext();
            }
            //close the cursor
            cursor.close();
        }

        ArrayAdapter<Product> itemsAdapter = new ProductAdapter(this, products, mDbHelper, database);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(itemsAdapter);
    }

    /**
     * Callback of the uploadImageButton onClickListener
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //imagePath = data.getDataString();
            imagePath = getRealPathFromURI(this, data.getData());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inventory_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        addProductListViewContainerView.setVisibility(View.VISIBLE);
        return true;
    }

    /**
     * Source: http://stackoverflow.com/questions/3401579/get-filename-and-path-from-uri-from-mediastore
     *
     * @param context
     * @param contentUri
     * @return
     */
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}

