package com.example.cakeapp.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cakeapp.R;
import com.example.cakeapp.adapter.BanhSinhNhatAdapter;
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

public class SearchActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    EditText editsearch;
    ApiApp apiApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    BanhSinhNhatAdapter adapterBsn;
    List<SanPhamMoi> sanPhamMoiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        ActionToolBar();
    }

    private void initView() {
        sanPhamMoiList = new ArrayList<>();
        apiApp = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiApp.class);

        editsearch = findViewById(R.id.editsearch);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerview_search);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        editsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    sanPhamMoiList.clear();
                    if (adapterBsn != null) {
                        adapterBsn.notifyDataSetChanged();
                    }
                } else {
                    getDataSearch(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No action needed here
            }
        });
    }

    private void getDataSearch(String searchQuery) {
        compositeDisposable.add(apiApp.search(searchQuery)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            // Xử lý phản hồi từ API
                            if (sanPhamMoiModel.isSuccess()) {
                                List<SanPhamMoi> results = sanPhamMoiModel.getResult();
                                if (results != null && !results.isEmpty()) {
                                    // Có kết quả tìm kiếm
                                    sanPhamMoiList.clear();
                                    sanPhamMoiList.addAll(results);
                                    if (adapterBsn == null) {
                                        adapterBsn = new BanhSinhNhatAdapter(getApplicationContext(), sanPhamMoiList);
                                        recyclerView.setAdapter(adapterBsn);
                                    } else {
                                        adapterBsn.notifyDataSetChanged();
                                    }
                                } else {
                                    // Không có sản phẩm, chỉ hiển thị thông báo
                                    sanPhamMoiList.clear(); // Đảm bảo rằng danh sách sản phẩm trống
                                    if (adapterBsn != null) {
                                        adapterBsn.notifyDataSetChanged(); // Cập nhật adapter
                                    }
                                    Toast.makeText(getApplicationContext(), "Không có sản phẩm tìm kiếm", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Thông báo lỗi từ API
                                sanPhamMoiList.clear(); // Đảm bảo rằng danh sách sản phẩm trống
                                if (adapterBsn != null) {
                                    adapterBsn.notifyDataSetChanged(); // Cập nhật adapter
                                }
                                Toast.makeText(getApplicationContext(), sanPhamMoiModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            // Xử lý lỗi
                            Log.e("SearchActivity", "Error: ", throwable);
                            sanPhamMoiList.clear(); // Đảm bảo rằng danh sách sản phẩm trống
                            if (adapterBsn != null) {
                                adapterBsn.notifyDataSetChanged(); // Cập nhật adapter
                            }
                            Toast.makeText(getApplicationContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }


    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onDetachedFromWindow() {
        compositeDisposable.clear();
        super.onDetachedFromWindow();
    }
}
