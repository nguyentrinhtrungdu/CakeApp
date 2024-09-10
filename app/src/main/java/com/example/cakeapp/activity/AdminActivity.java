package com.example.cakeapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cakeapp.R;
import com.example.cakeapp.utils.Utils;

import io.paperdb.Paper;

public class AdminActivity extends AppCompatActivity {

    private Button btnAddCategory, btnCategoryList, btnAddProduct, btnProductList, btnDangXuat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize buttons
        btnAddCategory = findViewById(R.id.btn_add_category);
        btnCategoryList = findViewById(R.id.btn_category_list);
        btnAddProduct = findViewById(R.id.btn_add_product);
        btnProductList = findViewById(R.id.btn_product_list);
        btnDangXuat = findViewById(R.id.btn_dangxuat);

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

        // Set OnClickListener to handle logout
        btnDangXuat.setOnClickListener(view -> logout());
    }

    private void logout() {
        // Xóa dữ liệu phiên của người dùng
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Hoặc sử dụng editor.remove("key") để xóa các mục cụ thể
        editor.apply();

        // Xóa bất kỳ dữ liệu phiên đặc biệt nào khác của ứng dụng
        Paper.book().delete("email");
        Paper.book().delete("pass");
        Paper.book().delete("islogin");
        Paper.book().delete("role");
        Paper.book().delete("user_id");
        Utils.user_current = null;


        // Chuyển hướng đến màn hình đăng nhập
        Intent intent = new Intent(AdminActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Đóng activity hiện tại
    }

}