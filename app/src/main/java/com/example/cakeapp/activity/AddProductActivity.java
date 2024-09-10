package com.example.cakeapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cakeapp.R;
import com.example.cakeapp.model.AddProductResponse;
import com.example.cakeapp.retrofit.ApiApp;
import com.example.cakeapp.retrofit.RetrofitClient;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddProductActivity extends AppCompatActivity {

    private EditText editTextName, editTextImage, editTextDescription, editTextPrice;
    private Spinner spinnerCategory;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        editTextName = findViewById(R.id.edit_text_name);
        editTextImage = findViewById(R.id.edit_text_image);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextPrice = findViewById(R.id.edit_text_price);
        spinnerCategory = findViewById(R.id.spinner_category);
        buttonSubmit = findViewById(R.id.button_submit);

        // Initialize Spinner with product types
        setupSpinner();

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });
    }

    private void setupSpinner() {
        // Define the product types
        String[] productTypes = {"Select Type", "Type 1", "Type 2", "Type 3"};

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, productTypes);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerCategory.setAdapter(adapter);
    }

    private void addProduct() {
        String name = editTextName.getText().toString().trim();
        String image = editTextImage.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();

        // Get selected category (spinner category is 1-based, adjust accordingly)
        int category = spinnerCategory.getSelectedItemPosition();

        if (category == 0) {
            Toast.makeText(this, "Please select a product type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || image.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int price = Integer.parseInt(priceStr);

        ApiApp apiApp = RetrofitClient.getInstance("YOUR_BASE_URL_HERE").create(ApiApp.class);
        apiApp.addProduct(name, image, description, category, price)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if (response.isSuccess()) {
                                Toast.makeText(getApplicationContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to add product", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                );
    }
}
