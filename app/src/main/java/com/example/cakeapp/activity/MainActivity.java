package com.example.cakeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cakeapp.R;
import com.example.cakeapp.adapter.LoaiSpAdapter;
import com.example.cakeapp.adapter.SanPhamMoiAdapter;
import com.example.cakeapp.model.LoaiSp;
import com.example.cakeapp.model.SanPhamMoi;
import com.example.cakeapp.model.SanPhamMoiModel;
import com.example.cakeapp.retrofit.ApiApp;
import com.example.cakeapp.retrofit.RetrofitClient;
import com.example.cakeapp.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
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
    List<SanPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout ;
    ImageView imgsearch,imgProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiApp = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiApp.class);
        Anhxa();
        ActionBar();

        if (isConnected(this)) {
            Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
            ActionViewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getEventClick();
        } else {
            Toast.makeText(getApplicationContext(), "không có internet, vui lòng kết nối", Toast.LENGTH_LONG).show();
        }
    }

    private void getEventClick() {
        listViewHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent trangchu= new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(trangchu);
                        break;
                    case 1:
                        Intent banhsinhnhat= new Intent(getApplicationContext(), banhsinhnhatActivity.class);
                        banhsinhnhat.putExtra("loai",1);
                        startActivity(banhsinhnhat);
                        break;
                    case 2:
                        Intent banhmi= new Intent(getApplicationContext(), banhsinhnhatActivity.class);
                        banhmi.putExtra("loai",0);
                        startActivity(banhmi);
                        break;


                }
            }
        });
    }

    private void getSpMoi() {
        compositeDisposable.add(apiApp.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            Log.d("Response", sanPhamMoiModel.toString());
                            if (sanPhamMoiModel.isSuccess()){
                                mangSpMoi = sanPhamMoiModel.getResult();
                                spAdapter = new SanPhamMoiAdapter(getApplicationContext(),mangSpMoi);
                                recyclerViewHome.setAdapter(spAdapter);
                            }
                        },
                        throwable -> {
                            Log.d("logg", "Error: " + throwable.getMessage());
                            Toast.makeText(getApplicationContext(), "Không kết nối được với server: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }


    private void getLoaiSanPham() {
        compositeDisposable.add(apiApp.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if (loaiSpModel.isSuccess()) {
                                mangloaisp = loaiSpModel.getResult();
                                loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(), mangloaisp);
                                listViewHome.setAdapter(loaiSpAdapter);
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
        imgsearch  = findViewById(R.id.imgsearch);
        toolbar = findViewById(R.id.toolbarhome);
        viewFlipper = findViewById(R.id.viewFlipper);
        recyclerViewHome = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager =new GridLayoutManager(this,2);
        recyclerViewHome.setLayoutManager(layoutManager);
        recyclerViewHome.setHasFixedSize(true);
        listViewHome = findViewById(R.id.listviewhome);
        navigationView = findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawerlayout);
        badge= findViewById(R.id.menu_sl);
        frameLayout=findViewById(R.id.framegiohang);
        imgProfile = findViewById(R.id.imgProfile);
        // Initialize list
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();
        // Initialize adapter
        if(Utils.manggiohang==null){
            Utils.manggiohang=new ArrayList<>();
        }else {
            int totalItem = 0;
            for(int i= 0;i<Utils.manggiohang.size();i++){
                totalItem= totalItem+Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });

        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
    }
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getTitle().toString()) {
                case "Thông Tin Của Bạn":
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    return true;
                case "Đăng Xuất":
                    logout();
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    private void logout() {
        // Clear user session data
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // or use editor.remove("key") to remove specific items
        editor.apply();

        // Clear any other app-specific session data
        Paper.book().delete("email");
        Paper.book().delete("pass");
        Paper.book().delete("islogin");
        Paper.book().delete("user_id");
        Paper.book().delete("role");
        Utils.user_current = null;
        Utils.manggiohang.clear();

        // Redirect to the login screen
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close the current activity
    }


    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for(int i= 0;i<Utils.manggiohang.size();i++){
            totalItem= totalItem+Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifi != null&&wifi.isConnected())||(mobile != null&&mobile.isConnected())){
            return true;
        }else{
            return false;
        }


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