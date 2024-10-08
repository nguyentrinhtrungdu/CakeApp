package com.example.cakeapp.activity;


import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.cakeapp.R;
import com.example.cakeapp.model.GioHang;
import com.example.cakeapp.model.SanPhamMoi;
import com.example.cakeapp.utils.Utils;
import com.nex3z.notificationbadge.NotificationBadge;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ChiTietActivity extends AppCompatActivity {
    TextView tensp , giasp, mota;
    Button btnthem;
    ImageView imghinhanh;
    Spinner spinner;
    Toolbar toolbar;
    SanPhamMoi sanPhamMoi;
    NotificationBadge badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chi_tiet);
        initView();
        ActionToolBar();
        initData();
        initControl();
    }

    private void initControl() {
        btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themGioHang();

            }
        });
    }

    private void themGioHang() {
        int soluong = Integer.parseInt(spinner.getSelectedItem().toString());
        if (Utils.manggiohang.size() > 0) {
            boolean flag = false;
            for (int i = 0; i < Utils.manggiohang.size(); i++) {
                if (Utils.manggiohang.get(i).getIdsp() == sanPhamMoi.getId()) {
                    Utils.manggiohang.get(i).setSoluong(soluong + Utils.manggiohang.get(i).getSoluong());
                    // Chỉ cập nhật tổng tiền, không thay đổi giá gốc
                    int tongtien = sanPhamMoi.getGiasp() * Utils.manggiohang.get(i).getSoluong();
                    Utils.manggiohang.get(i).setTongTien(tongtien);
                    flag = true;
                }
            }
            if (!flag) {
                int tongtien = sanPhamMoi.getGiasp() * soluong;
                GioHang gioHang = new GioHang();
                gioHang.setGiasp(sanPhamMoi.getGiasp());  // Giá gốc không thay đổi
                gioHang.setSoluong(soluong);
                gioHang.setTongTien(tongtien);  // Tổng tiền = giá gốc * số lượng
                gioHang.setIdsp(sanPhamMoi.getId());
                gioHang.setTensp(sanPhamMoi.getTensp());
                gioHang.setHinhsp(sanPhamMoi.getHinhanh());
                Utils.manggiohang.add(gioHang);
            }
        } else {
            int tongtien = sanPhamMoi.getGiasp() * soluong;
            GioHang gioHang = new GioHang();
            gioHang.setGiasp(sanPhamMoi.getGiasp());  // Giá gốc không thay đổi
            gioHang.setSoluong(soluong);
            gioHang.setTongTien(tongtien);  // Tổng tiền = giá gốc * số lượng
            gioHang.setIdsp(sanPhamMoi.getId());
            gioHang.setTensp(sanPhamMoi.getTensp());
            gioHang.setHinhsp(sanPhamMoi.getHinhanh());
            Utils.manggiohang.add(gioHang);
        }
        int totalItem = 0;
        for (int i = 0; i < Utils.manggiohang.size(); i++) {
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    private void initData() {
        sanPhamMoi = (SanPhamMoi) getIntent().getSerializableExtra("chitiet");
        if (sanPhamMoi != null) {


            tensp.setText(sanPhamMoi.getTensp());
            mota.setText(sanPhamMoi.getMota());
            Glide.with(getApplicationContext()).load(sanPhamMoi.getHinhanh()).into(imghinhanh);
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            giasp.setText("Giá: " + decimalFormat.format(sanPhamMoi.getGiasp())+" Đ" );

            Integer[] so = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
            ArrayAdapter<Integer> adapterspin = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, so);
            spinner.setAdapter(adapterspin);
        }
    }


    private void initView() {
        tensp = findViewById(R.id.txttensp);
        giasp = findViewById(R.id.txtgiasp);
        mota = findViewById(R.id.txtmotachitiet);
        btnthem = findViewById(R.id.btnthemvaogiohang);
        imghinhanh = findViewById(R.id.imgchitiet);
        spinner = findViewById(R.id.spinner);
        toolbar = findViewById(R.id.toolbar);
        badge =findViewById(R.id.menu_sl);
        FrameLayout frameLayoutgiohang = findViewById(R.id.framegiohang);
        frameLayoutgiohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent giohang=new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });
        if(Utils.manggiohang != null){
            int totalItem = 0;
            for(int i= 0;i<Utils.manggiohang.size();i++){
                totalItem= totalItem+Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
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
    protected void onResume() {
        super.onResume();
        if(Utils.manggiohang != null){
            int totalItem = 0;
            for(int i= 0;i<Utils.manggiohang.size();i++){
                totalItem= totalItem+Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
    }
}