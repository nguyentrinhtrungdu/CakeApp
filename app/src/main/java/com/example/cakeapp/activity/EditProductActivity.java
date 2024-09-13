package com.example.cakeapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cakeapp.R;
import com.example.cakeapp.model.ProductRespone;
import com.example.cakeapp.retrofit.ApiApp;
import com.example.cakeapp.retrofit.RetrofitClient;
import com.example.cakeapp.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Toolbar toolbar;
    private Map<Integer, String> categoryMap;
    private Map<String, Integer> categoryInverseMap; // To map category names back to IDs

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
        toolbar = findViewById(R.id.toolbar);
        // Initialize category maps
        categoryMap = new HashMap<>();
        categoryMap.put(1, "Bánh sinh nhật");
        categoryMap.put(2, "Bánh mì");
        categoryInverseMap = new HashMap<>();
        for (Map.Entry<Integer, String> entry : categoryMap.entrySet()) {
            categoryInverseMap.put(entry.getValue(), entry.getKey());
        }

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
        ActionToolBar();

        // Set up save button click listener
        buttonSave.setOnClickListener(v -> {
            // Get data from input fields
            String name = editTextName.getText().toString();
            String image = editTextImage.getText().toString();
            String description = editTextDescription.getText().toString();
            String priceStr = editTextPrice.getText().toString();
            int selectedCategory = categoryInverseMap.get(spinnerCategory.getSelectedItem().toString());

            if (name.isEmpty() || image.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(EditProductActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int price = Integer.parseInt(priceStr);
            updateProduct(productId, name, image, description, selectedCategory, price);
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
                                setupCategorySpinner(productResponse.getProduct().getLoai());
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

    private void setupCategorySpinner(int selectedCategory) {
        // Define categories
        List<String> categories = new ArrayList<>(categoryMap.values());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Set selected category
        String selectedCategoryName = categoryMap.get(selectedCategory);
        if (selectedCategoryName != null) {
            int position = categories.indexOf(selectedCategoryName);
            spinnerCategory.setSelection(position);
        }
    }

    private void updateProduct(int id, String name, String image, String description, int category, int price) {
        compositeDisposable.add(apiApp.updateProduct(id, name, image, description, category, price)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        updatedProductResponse -> {
                            if (updatedProductResponse.isSuccess()) {
                                Toast.makeText(EditProductActivity.this, "Sản phẩm đã được cập nhật", Toast.LENGTH_SHORT).show();

                                // Set result and finish activity
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("UPDATE_SUCCESS", true);
                                setResult(RESULT_OK, resultIntent);
                                finish(); // Close the activity
                            } else {
                                Toast.makeText(EditProductActivity.this, updatedProductResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Log.e("EditProductActivity", "Error updating product", throwable);
                            Toast.makeText(EditProductActivity.this, "Lỗi kết nối đến máy chủ: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }
    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
            toolbar.getNavigationIcon().setTint(getResources().getColor(R.color.black));
        }
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Đóng Activity hiện tại và quay lại Activity trước đó
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}


