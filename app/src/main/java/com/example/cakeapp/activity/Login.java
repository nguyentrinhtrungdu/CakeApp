package com.example.cakeapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cakeapp.R;
import com.example.cakeapp.retrofit.ApiApp;
import com.example.cakeapp.retrofit.RetrofitClient;
import com.example.cakeapp.utils.Utils;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Login extends AppCompatActivity {
    TextView txtdangki;
    EditText email, pass;
    Button btnlog;
    ApiApp apiApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        initView();
        initControl();
    }

    private void initControl() {
        txtdangki.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Sign.class);
            startActivity(intent);
        });

        btnlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_email = email.getText().toString().trim();
                String str_pass = pass.getText().toString().trim();

                if (TextUtils.isEmpty(str_email)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập Email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(str_pass)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập Pass", Toast.LENGTH_SHORT).show();
                } else {
                    // Save credentials
                    Paper.book().write("email", str_email);
                    Paper.book().write("pass", str_pass);

                    compositeDisposable.add(apiApp.dangNhap(str_email, str_pass)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    userModel -> {

                                        if (userModel.isSuccess() && userModel.getResult() != null && !userModel.getResult().isEmpty()) {
                                            Utils.user_current = userModel.getResult().get(0);
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    throwable -> {
                                        Log.e("Login", "Error: " + throwable.getMessage());
                                        Toast.makeText(getApplicationContext(), "Đăng nhập không thành công. Lỗi: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                            ));

                }
            }
        });


    }

    private void initView() {
        Paper.init(this);
        apiApp = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiApp.class);
        txtdangki = findViewById(R.id.dangki);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        btnlog = findViewById(R.id.btnlog);

        // Read saved data
        if (Paper.book().read("email") != null && Paper.book().read("pass") != null) {
            email.setText(Paper.book().read("email"));
            pass.setText(Paper.book().read("pass"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.user_current != null) {
            if (Utils.user_current.getEmail() != null) {
                email.setText(Utils.user_current.getEmail());
            }
            if (Utils.user_current.getPass() != null) {
                pass.setText(Utils.user_current.getPass());
            }
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}