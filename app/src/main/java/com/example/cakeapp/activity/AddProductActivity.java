package com.example.cakeapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
import com.example.cakeapp.model.AddProductResponse;
import com.example.cakeapp.retrofit.ApiApp;
import com.example.cakeapp.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddProductActivity extends AppCompatActivity {

    private EditText editTextName, editTextImage, editTextDescription, editTextPrice;
    private Spinner spinnerCategory;
    private Button buttonSubmit;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        toolbar = findViewById(R.id.toolbar);
        editTextName = findViewById(R.id.edit_text_name);
        editTextImage = findViewById(R.id.edit_text_image);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextPrice = findViewById(R.id.edit_text_price);
        spinnerCategory = findViewById(R.id.spinner_category);
        buttonSubmit = findViewById(R.id.button_submit);

        // Initialize Spinner with product types
        setupSpinner();
        ActionToolBar();
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });
    }

    private void setupSpinner() {
        // Define the product types and their corresponding integer IDs
        final Map<Integer, String> productTypesMap = new HashMap<>();
        productTypesMap.put(1, "Bánh Sinh Nhật");
        productTypesMap.put(2, "Bánh Mì");


        // Create a list of product type names for the spinner
        List<String> productTypes = new ArrayList<>(productTypesMap.values());

        // Create an ArrayAdapter using the list of product type names and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, productTypes);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerCategory.setAdapter(adapter);

        // Set a tag on the spinner to store the mapping
        spinnerCategory.setTag(productTypesMap);
    }
    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
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



    private void addProduct() {
        String name = editTextName.getText().toString().trim();
        String image = editTextImage.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();

        // Lấy danh sách loại sản phẩm từ Spinner
        Map<Integer, String> productTypesMap = (Map<Integer, String>) spinnerCategory.getTag();
        int selectedPosition = spinnerCategory.getSelectedItemPosition();
        Integer categoryId = null;

        // Ánh xạ vị trí chọn trong Spinner thành ID loại sản phẩm
        if (selectedPosition >= 0 && selectedPosition < productTypesMap.size()) {
            categoryId = new ArrayList<>(productTypesMap.keySet()).get(selectedPosition);
        }

        if (categoryId == null) {
            Toast.makeText(this, "Vui lòng chọn loại sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || image.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int price = Integer.parseInt(priceStr);

        ApiApp apiApp = RetrofitClient.getInstance("YOUR_BASE_URL_HERE").create(ApiApp.class);
        apiApp.addProduct(name, image, description, categoryId, price)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if (response.isSuccess()) {
                                Toast.makeText(getApplicationContext(), "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();

                                // Tạo Intent để mở ProductListActivity
                                Intent intent = new Intent(AddProductActivity.this, ProductListActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Lỗi: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                );


    }
}
