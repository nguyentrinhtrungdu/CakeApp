package com.example.cakeapp.adapter;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cakeapp.R;
import com.example.cakeapp.model.SanPhamMoi;

import java.text.DecimalFormat;
import java.util.List;

public class SanPhamMoiAdapter extends RecyclerView.Adapter<SanPhamMoiAdapter.MyViewHolder> {
        Context context;
        List<SanPhamMoi>array;

    public SanPhamMoiAdapter(Context context,List<SanPhamMoi> array) {
        this.array = array;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sp_moi, parent,false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SanPhamMoi sanPhamMoi = array.get(position);

        holder.txtten.setText(sanPhamMoi.getTensp());
        DecimalFormat decimalFormat =new DecimalFormat("###,###,###");
        holder.txtgia.setText("Giá: "+decimalFormat.format(sanPhamMoi.getGiasp())+" Đ");

        Glide.with(context).load(sanPhamMoi.getHinhanh()).into(holder.imghinhanh);

    }

    @Override
    public int getItemCount() {
        return array.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtgia,txtten;
        ImageView imghinhanh;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtgia =itemView.findViewById(R.id.itemsp_gia);
            txtten =itemView.findViewById(R.id.itemsp_name);
            imghinhanh =itemView.findViewById(R.id.itemsp_image);

        }
    }
}
