package com.example.cakeapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cakeapp.R;
import com.example.cakeapp.activity.ChiTietActivity;
import com.example.cakeapp.model.SanPhamMoi;

import java.text.DecimalFormat;
import java.util.List;

public class SanPhamListAdapter extends RecyclerView.Adapter<SanPhamListAdapter.MyViewHolder> {

    private Context context;
    private List<SanPhamMoi> productList;

    public SanPhamListAdapter(Context context, List<SanPhamMoi> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pr, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SanPhamMoi sanPhamMoi = productList.get(position);

        // Bind data to views
        holder.txtTen.setText(sanPhamMoi.getTensp());
        holder.txtMota.setText(sanPhamMoi.getMota());
        holder.txtLoai.setText(sanPhamMoi.getLoai());// Set the type

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtGia.setText(decimalFormat.format(sanPhamMoi.getGiasp()) + " Đ");

        Glide.with(context)
                .load(sanPhamMoi.getHinhanh())
                .into(holder.imgHinhAnh);

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChiTietActivity.class);
            intent.putExtra("chitiet", sanPhamMoi);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTen, txtGia, txtMota, txtLoai;
        ImageView imgHinhAnh;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTen = itemView.findViewById(R.id.itemsp_name);
            imgHinhAnh = itemView.findViewById(R.id.itemsp_image);
            txtMota = itemView.findViewById(R.id.itemsp_description);
            txtLoai = itemView.findViewById(R.id.itemsp_type); // Initialize type TextView
            txtGia = itemView.findViewById(R.id.itemsp_gia);
        }
    }
}
