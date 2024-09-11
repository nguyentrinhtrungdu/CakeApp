package com.example.cakeapp.retrofit;


import com.example.cakeapp.model.AddProductResponse;
import com.example.cakeapp.model.DeleteProductResponse;
import com.example.cakeapp.model.LoaiSpModel;
import com.example.cakeapp.model.SanPhamMoiModel;
import com.example.cakeapp.model.User;
import com.example.cakeapp.model.UserModel;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiApp {
    @GET("getloaisp.php")
    Observable<LoaiSpModel> getLoaiSp();

    @GET("getspmoi.php")
    Observable<SanPhamMoiModel> getSpMoi();

    @POST("chitiet.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> getSanPham(
      @Field("page") int page,
      @Field("loai") int loai
    );
    @POST("dangki.php")
    @FormUrlEncoded
    Observable<UserModel> dangKi(
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("username") String username,
            @Field("num") String num
    );
    @POST("dangnhap.php")
    @FormUrlEncoded
    Observable<UserModel> dangNhap(
            @Field("email") String email,
            @Field("pass") String pass
    );

    @POST("donhang.php")
    @FormUrlEncoded
    Observable<UserModel> createOder(
            @Field("email") String email,
            @Field("num") String  num,
            @Field("tongtien") String tongtien,
            @Field("iduser") int id,
            @Field("diachi") String diachi,
            @Field("soluong") int soluong,
            @Field("chitiet") String chitiet


    );
    @POST("timkiem.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> search(
            @Field("search") String search
    );

    @GET("getUserById.php")
    Observable<UserModel> getUserById(@Query("id") int userId);



    @POST("updateUser.php")
    @FormUrlEncoded
    Observable<User> updateUser(
            @Field("id") int id,
            @Field("username") String username,
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("num") String num
    );

    @POST("themsanphammoi.php")
    @FormUrlEncoded
    Observable<AddProductResponse> addProduct(
            @Field("tensp") String tensp,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("loai") int loai,
            @Field("giasp") int giasp
    );

    @DELETE("xoa_sp.php")
    Observable<DeleteProductResponse> deleteProduct(@Query("id") int id);




}
