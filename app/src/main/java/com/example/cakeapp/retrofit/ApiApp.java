package com.example.cakeapp.retrofit;


import com.example.cakeapp.model.LoaiSpModel;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface ApiApp {
    @GET("getloaisp.php")
    Observable<LoaiSpModel> getLoaiSp();

}
