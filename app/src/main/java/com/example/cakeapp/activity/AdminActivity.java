package com.example.cakeapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cakeapp.R;

public class AdminActivity extends AppCompatActivity {

    private Button  btnAddCategory, btnCategoryList,btnAddProduct, btnProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize buttons
        btnAddCategory = findViewById(R.id.btn_add_category);
        btnCategoryList = findViewById(R.id.btn_category_list);
        btnAddProduct = findViewById(R.id.btn_add_product);
        btnProductList = findViewById(R.id.btn_product_list);

// Set OnClickListener to navigate to AddCategoryActivity
        btnAddCategory.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, AddCategoryActivity.class);
            startActivity(intent);
        });

        // Set OnClickListener to navigate to CategoryListActivity
        btnCategoryList.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, CategoryListActivity.class);
            startActivity(intent);
        });
        // Set OnClickListener to navigate to AddProductActivity
        btnAddProduct.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, AddProductActivity.class);
            startActivity(intent);
        });

        // Set OnClickListener to navigate to ProductListActivity
        btnProductList.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, ProductListActivity.class);
            startActivity(intent);
        });


    }
}
