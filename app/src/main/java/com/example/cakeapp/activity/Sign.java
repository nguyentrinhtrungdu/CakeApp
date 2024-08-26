package com.example.cakeapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cakeapp.R;
import com.example.cakeapp.retrofit.ApiApp;
import com.example.cakeapp.retrofit.RetrofitClient;
import com.example.cakeapp.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;

public class Sign extends AppCompatActivity {
    EditText username,email,pass,cfpass,num;
    Button button;
    ApiApp apiApp;
    CompositeDisposable compositeDisposable=new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign);
        initView();
        initControl();

        TextView dangnhap = findViewById(R.id.dangnhap); // Updated ID
        dangnhap.setOnClickListener(v -> {
            Intent intent = new Intent(Sign.this, Login.class);
            startActivity(intent);
        });
    }

    private void initControl() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dangKi();
            }
        });
    }

    private void dangKi() {
        String str_username = username.getText().toString().trim();
        String str_email = email.getText().toString().trim();
        String str_pass = pass.getText().toString().trim();
        String str_cfpass = cfpass.getText().toString().trim();
        String str_num = num.getText().toString().trim();

        if (TextUtils.isEmpty(str_username)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập User Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_email)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập Email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_pass)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập Password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_cfpass)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa xác nhận lại Password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_num)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
        } else {
            if (str_pass.equals(str_cfpass)) {
                compositeDisposable.add(apiApp.dangKi(str_email, str_pass, str_username, str_num)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                userModel -> {
                                    // Log response for debugging

                                    if (userModel.isSuccess()) {
                                        Utils.user_current.setEmail(str_email);
                                        Utils.user_current.setPass(str_pass);
                                        Intent intent =new Intent(getApplicationContext(), Login.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                },
                                throwable -> {
                                    Toast.makeText(getApplicationContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                                }
                        ));
            } else {
                Toast.makeText(getApplicationContext(), "Mật khẩu nhập lại chưa đúng", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void initView() {
        apiApp= RetrofitClient.getInstance(Utils.BASE_URL).create(ApiApp.class);

        username=findViewById(R.id.username);
        email =findViewById(R.id.email);
        pass=findViewById(R.id.pass);
        cfpass=findViewById(R.id.cfpass);
        num=findViewById(R.id.num);
        button=findViewById(R.id.btnsign);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
