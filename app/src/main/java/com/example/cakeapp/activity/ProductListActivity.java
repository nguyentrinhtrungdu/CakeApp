package com.example.cakeapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cakeapp.R;
import com.example.cakeapp.adapter.SanPhamListAdapter;
import com.example.cakeapp.model.SanPhamMoi;
import com.example.cakeapp.model.SanPhamMoiModel;
import com.example.cakeapp.retrofit.ApiApp;
import com.example.cakeapp.retrofit.RetrofitClient;
import com.example.cakeapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
public class ProductListActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_EDIT_PRODUCT = 1;
    private RecyclerView recyclerView;
    private SanPhamListAdapter adapter;
    private List<SanPhamMoi> productList;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiApp apiApp;
    private Button button_delete;
    private Button button_edit;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        toolbar = findViewById(R.id.toolbar);
        ActionToolBar();
        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the product list
        productList = new ArrayList<>();

        // Set up the adapter
        adapter = new SanPhamListAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        // Initialize Retrofit API
        apiApp = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiApp.class);

        // Fetch data from API
        fetchData();

        // Initialize Toolbar and Buttons
        Toolbar toolbar = findViewById(R.id.toolbar);
        button_delete = toolbar.findViewById(R.id.button_delete);
        button_edit = toolbar.findViewById(R.id.button_edit);

        button_delete.setOnClickListener(v -> {
            List<SanPhamMoi> selectedProducts = adapter.getSelectedProducts();
            if (!selectedProducts.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc chắn muốn xóa các sản phẩm đã chọn?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            for (SanPhamMoi product : selectedProducts) {
                                deleteProduct(product);
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(), "Chưa chọn sản phẩm nào để xóa", Toast.LENGTH_SHORT).show();
            }
        });

        button_edit.setOnClickListener(v -> {
            List<SanPhamMoi> selectedProducts = adapter.getSelectedProducts();
            if (selectedProducts.size() == 1) {
                SanPhamMoi selectedProduct = selectedProducts.get(0);
                Intent intent = new Intent(ProductListActivity.this, EditProductActivity.class);
                intent.putExtra("PRODUCT_ID", selectedProduct.getId());
                startActivityForResult(intent, REQUEST_CODE_EDIT_PRODUCT); // Chú ý: Thay đổi từ startActivity() thành startActivityForResult()
            } else {
                Toast.makeText(getApplicationContext(), "Vui lòng chọn một sản phẩm để sửa", Toast.LENGTH_SHORT).show();
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EDIT_PRODUCT && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("UPDATE_SUCCESS", false)) {
                // Refresh the product list
                fetchData();
            }
        }
    }


    private void fetchData() {
        compositeDisposable.add(apiApp.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                // Update the product list and notify the adapter
                                productList.clear();
                                productList.addAll(sanPhamMoiModel.getResult());
                                adapter.notifyDataSetChanged();
                            } else {
                                // Handle case where fetching data was not successful
                                Toast.makeText(getApplicationContext(), "Không có sản phẩm nào", Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> {
                            // Handle error
                            Log.e("ProductListActivity", "Error fetching data", throwable);
                            Toast.makeText(getApplicationContext(), "Lỗi kết nối đến máy chủ: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void deleteProduct(SanPhamMoi product) {
        compositeDisposable.add(apiApp.deleteProduct(product.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if (response.isSuccess()) {
                                // Remove the item from the list and notify the adapter
                                productList.remove(product);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(), "Sản phẩm đã được xóa", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Xóa sản phẩm thất bại: " + response.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Log.e("ProductListActivity", "Error deleting product", throwable);
                            Toast.makeText(getApplicationContext(), "Lỗi kết nối đến máy chủ: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }
    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
