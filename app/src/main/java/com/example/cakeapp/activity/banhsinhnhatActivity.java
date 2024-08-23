package com.example.cakeapp.activity;

import android.icu.text.ConstrainedFieldPosition;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cakeapp.R;
import com.example.cakeapp.adapter.BanhSinhNhatAdapter;
import com.example.cakeapp.model.SanPhamMoi;
import com.example.cakeapp.model.SanPhamMoiModel;
import com.example.cakeapp.retrofit.ApiApp;
import com.example.cakeapp.retrofit.RetrofitClient;
import com.example.cakeapp.utils.Utils;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class banhsinhnhatActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ApiApp apiApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    int page =1;
    int loai ;
    BanhSinhNhatAdapter adapterBsn;
    List<SanPhamMoi> sanPhamMoiList;
    LinearLayoutManager linearLayoutManager;
    Handler handler =new Handler();
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_banhsinhnhat);
        apiApp = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiApp.class);
        loai = getIntent().getIntExtra("loai",1);
        AnhXa();
        ActionToolBar();
        getData(page);
        addEventLoand();
    }

    private void addEventLoand() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    isLoading = true;
                    loadMore();


                }
            }

        });
    }

    private void loadMore() {
        // Thêm item null để hiển thị trạng thái loading
        sanPhamMoiList.add(null);
        adapterBsn.notifyItemInserted(sanPhamMoiList.size() - 1);

        // Giả lập độ trễ để tải thêm dữ liệu
        handler.postDelayed(() -> {
            // Xóa item null
            sanPhamMoiList.remove(sanPhamMoiList.size() - 1);
            adapterBsn.notifyItemRemoved(sanPhamMoiList.size());

            // Tăng số trang và tải dữ liệu tiếp theo
            page++;
            getData(page);
        }, 2000); // Thay đổi thời gian nếu cần
    }


    private void getData(int page) {
        compositeDisposable.add(apiApp.getSanPham(page, loai)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                if (adapterBsn == null) {
                                    sanPhamMoiList = sanPhamMoiModel.getResult();
                                    adapterBsn = new BanhSinhNhatAdapter(getApplicationContext(), sanPhamMoiList);
                                    recyclerView.setAdapter(adapterBsn);
                                } else {
                                    int vitri = sanPhamMoiList.size();
                                    sanPhamMoiList.addAll(sanPhamMoiModel.getResult());
                                    adapterBsn.notifyItemRangeInserted(vitri, sanPhamMoiModel.getResult().size());
                                }

                                if (sanPhamMoiModel.getResult().isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "Hết dữ liệu rồi", Toast.LENGTH_LONG).show();
                                    isLoading = true; // Dữ liệu đã hết
                                } else {
                                    isLoading = false; // Dữ liệu tiếp theo đã được tải
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Hết dữ liệu rồi", Toast.LENGTH_LONG).show();
                                isLoading = true; // Dữ liệu đã hết
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Không kết nối với server", Toast.LENGTH_LONG).show();
                            isLoading = false; // Đặt lại trạng thái tải khi có lỗi
                        }
                ));
    }


    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void AnhXa() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerview_bsn);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        sanPhamMoiList =new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}