package com.example.cakeapp.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cakeapp.R;
import com.example.cakeapp.adapter.LoaiSpAdapter;
import com.example.cakeapp.model.LoaiSp;
import com.example.cakeapp.retrofit.ApiApp;
import com.example.cakeapp.retrofit.RetrofitClient;
import com.example.cakeapp.utils.Utils;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewHome;
    NavigationView navigationView;
    ListView listViewHome;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiApp apiApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        apiApp = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiApp.class);
        Anhxa();
        ActionBar();
        ActionViewFlipper();
        if (isConnected(this)) {
            Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
            getLoaiSanPham();
        } else {
            Toast.makeText(getApplicationContext(), "không có internet, vui lòng kết nối", Toast.LENGTH_LONG).show();
        }
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiApp.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if (loaiSpModel.isSuccess()) {
                                Toast.makeText(getApplicationContext(), loaiSpModel.getResult().get(0).getTensanpham(), Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> {
                            // Handle error here
                            Toast.makeText(getApplicationContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void ActionViewFlipper() {
        List<String> maquangcao = new ArrayList<>();
        maquangcao.add("https://storage.googleapis.com/cake-prod-website/homepage/ngay_thuong_logo_9865fa29f2/ngay_thuong_logo_9865fa29f2.png");
        maquangcao.add("https://storage.googleapis.com/cake-prod-website/homepage/vietlott_crm_1728_640_1_cfb0f432d9/vietlott_crm_1728_640_1_cfb0f432d9.png");
        maquangcao.add("https://storage.googleapis.com/cake-prod-website/homepage/1728_640_1_692e380d7c/1728_640_1_692e380d7c.png");
        for (String imageUrl : maquangcao) {
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(imageUrl).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private void Anhxa() {
        toolbar = findViewById(R.id.toolbarhome);
        viewFlipper = findViewById(R.id.viewFlipper);
        recyclerViewHome = findViewById(R.id.recyclerview);
        listViewHome = findViewById(R.id.listviewhome);
        navigationView = findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawerlayout);
        // Initialize list
        mangloaisp = new ArrayList<>();
        // Initialize adapter
        loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(), mangloaisp);
        listViewHome.setAdapter(loaiSpAdapter);
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                return networkCapabilities != null &&
                        (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            } else {
                // Fallback for older Android versions
                NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                return (wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected());
            }
        }
        return false;
    }

    private void ActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
}
