package com.example.cakeapp.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

    private RecyclerView recyclerView;
    private SanPhamListAdapter adapter;
    private List<SanPhamMoi> productList;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiApp apiApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

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
                            Toast.makeText(getApplicationContext(), "Lỗi kết nối đến máy chủ", Toast.LENGTH_LONG).show();
                        }
                ));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
