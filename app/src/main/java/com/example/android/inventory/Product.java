package com.example.android.inventory;

/**
 * Created by Jason on 04/07/2016.
 */
public class Product {

    private String mProductName = "";
    private String mProductImage = "";
    private int mProductQuantity = 0;
    private double mProductPrice = 0.00;

    public Product(String mProductName, String mProductImage, int mProductQuantity, double
            mProductPrice) {
        this.mProductName = mProductName;
        this.mProductImage = mProductImage;
        this.mProductQuantity = mProductQuantity;
        this.mProductPrice = mProductPrice;
    }

    /**
     * Gets the product name stored in the private mProductName member variable
     *
     * @return String
     */
    public String getProductName() {
        return mProductName;
    }

    /**
     * Gets the product image stored in the private mProductImage member variable
     *
     * @return String
     */
    public String getProductImage() {
        return mProductImage;
    }

    /**
     * Gets the product quantity stored in the private mProductQuantity member variable
     *
     * @return int
     */
    public int getProductQuantity() {
        return mProductQuantity;
    }

    public void setProductQuantity(int newValue) {
        if (this.mProductQuantity > 0) {
            this.mProductQuantity = newValue;
        } else {
            this.mProductQuantity = 0;
        }
    }

    /**
     * Gets the product price stored in the private mProductPrice member variable
     *
     * @return int
     */
    public double getProductPrice() {
        return mProductPrice;
    }
}
