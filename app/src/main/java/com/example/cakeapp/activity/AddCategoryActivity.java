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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AddCategoryActivity extends AppCompatActivity {

    private EditText editTextCategoryName, editTextImageUrl;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        // Initialize UI elements
        editTextCategoryName = findViewById(R.id.edit_text_category_name);
        editTextImageUrl = findViewById(R.id.edit_text_image_url);
        buttonSubmit = findViewById(R.id.button_submit_category);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });
    }
    private void addCategory() {
        String name = editTextCategoryName.getText().toString().trim();
        String image = editTextImageUrl.getText().toString().trim();





        ApiApp apiApp = RetrofitClient.getInstance("YOUR_BASE_URL_HERE").create(ApiApp.class);
        apiApp.addCategory(name, image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if (response.isSuccess()) {
                                Toast.makeText(getApplicationContext(), "Category added successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to add category", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                );
    }
}
