package com.example.cakeapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cakeapp.R;
import com.example.cakeapp.model.ProductRespone;
import com.example.cakeapp.retrofit.ApiApp;
import com.example.cakeapp.retrofit.RetrofitClient;
import com.example.cakeapp.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EditProductActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiApp apiApp;
    private int productId;

    private EditText editTextName;
    private EditText editTextImage;
    private EditText editTextDescription;
    private EditText editTextPrice;
    private Spinner spinnerCategory;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        // Initialize Retrofit API
        apiApp = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiApp.class);

        // Find views
        editTextName = findViewById(R.id.edit_text_name);
        editTextImage = findViewById(R.id.edit_text_image);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextPrice = findViewById(R.id.edit_text_price);
        spinnerCategory = findViewById(R.id.spinner_category);
        buttonSave = findViewById(R.id.button_save);

        // Get product ID from Intent
        if (getIntent() != null) {
            productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        }

        // Fetch product data
        if (productId != -1) {
            fetchProductData(productId);
        } else {
            Toast.makeText(this, "ID sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
        }

        // Set up save button click listener
        buttonSave.setOnClickListener(v -> {
            // Handle save product logic here
        });
    }

    private void fetchProductData(int id) {
        compositeDisposable.add(apiApp.getProductById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        productResponse -> {
                            if (productResponse.isSuccess()) {
                                // Update UI with product data
                                editTextName.setText(productResponse.getProduct().getTensp());
                                editTextImage.setText(productResponse.getProduct().getHinhanh());
                                editTextDescription.setText(productResponse.getProduct().getMota());
                                editTextPrice.setText(String.valueOf(productResponse.getProduct().getGiasp()));

                                // Set up spinner category
                                setupCategorySpinner();
                            } else {
                                Toast.makeText(getApplicationContext(), productResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Log.e("EditProductActivity", "Error fetching product data", throwable);
                            Toast.makeText(getApplicationContext(), "Lỗi kết nối đến máy chủ: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void setupCategorySpinner() {
        // Fetch and set up categories here
        // Example:
        String[] categories = {"Category 1", "Category 2", "Category 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
