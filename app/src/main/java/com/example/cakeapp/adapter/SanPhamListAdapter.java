package com.example.cakeapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cakeapp.R;
import com.example.cakeapp.activity.ChiTietActivity;
import com.example.cakeapp.model.SanPhamMoi;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
public class SanPhamListAdapter extends RecyclerView.Adapter<SanPhamListAdapter.MyViewHolder> {

    private Context context;
    private List<SanPhamMoi> productList;
    private List<SanPhamMoi> selectedProducts = new ArrayList<>();

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
        holder.textViewStt.setText(String.valueOf(position + 1));
        // Bind data to views
        holder.txtTen.setText(sanPhamMoi.getTensp());
        holder.txtMota.setText(sanPhamMoi.getMota());

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtGia.setText(decimalFormat.format(sanPhamMoi.getGiasp()) + " Đ");

        Glide.with(context)
                .load(sanPhamMoi.getHinhanh())
                .into(holder.imgHinhAnh);

        // Set item view background based on selection state
        if (selectedProducts.contains(sanPhamMoi)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        }

        // Set click listener for item view
        holder.itemView.setOnClickListener(v -> {
            if (selectedProducts.contains(sanPhamMoi)) {
                selectedProducts.remove(sanPhamMoi);
                notifyItemChanged(position);  // Update the specific item
            } else {
                selectedProducts.add(sanPhamMoi);
                notifyItemChanged(position);  // Update the specific item
            }
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public List<SanPhamMoi> getSelectedProducts() {
        return selectedProducts;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTen, txtGia, txtMota,textViewStt;
        ImageView imgHinhAnh;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStt = itemView.findViewById(R.id.itemsp_stt);
            txtTen = itemView.findViewById(R.id.itemsp_name);

            imgHinhAnh = itemView.findViewById(R.id.itemsp_image);
            txtMota = itemView.findViewById(R.id.itemsp_description);
            txtGia = itemView.findViewById(R.id.itemsp_gia);
        }
    }
}

