package com.example.cakeapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
    private TextView txtdangki;
    private EditText email, pass;
    private Button btnlog;
    private ApiApp apiApp;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_login);
        initView();
        initControl();
    }

    private void initView() {
        Paper.init(this);
        apiApp = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiApp.class);
        txtdangki = findViewById(R.id.dangki);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        btnlog = findViewById(R.id.btnlog);

        // Read saved data
        String savedEmail = Paper.book().read("email", "");
        String savedPass = Paper.book().read("pass", "");
        boolean isLoggedIn = Paper.book().read("islogin", false);

        if (!TextUtils.isEmpty(savedEmail) && !TextUtils.isEmpty(savedPass) && isLoggedIn) {
            email.setText(savedEmail);
            pass.setText(savedPass);
            // Automatically login if credentials exist
            new Handler().postDelayed(() -> loginUser(savedEmail, savedPass), 1000);
        }
    }

    private void initControl() {
        txtdangki.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Sign.class);
            startActivity(intent);
        });

        btnlog.setOnClickListener(view -> {
            String strEmail = email.getText().toString().trim();
            String strPass = pass.getText().toString().trim();

            if (TextUtils.isEmpty(strEmail)) {
                showToast("Bạn chưa nhập Email");
            } else if (TextUtils.isEmpty(strPass)) {
                showToast("Bạn chưa nhập Pass");
            } else {
                // Save credentials
                Paper.book().write("email", strEmail);
                Paper.book().write("pass", strPass);
                loginUser(strEmail, strPass);
            }
        });
    }

    private void loginUser(String email, String pass) {
        compositeDisposable.add(apiApp.dangNhap(email, pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess() && userModel.getResult() != null && !userModel.getResult().isEmpty()) {
                                isLogin = true;
                                Paper.book().write("islogin", isLogin);
                                Utils.user_current = userModel.getResult().get(0);
                                Paper.book().write("id", Utils.user_current.getId());
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                showToast(userModel.getMessage());
                            }
                        },
                        throwable -> {
                            Log.e("Login", "Error: " + throwable.getMessage());
                            showToast("Đăng nhập không thành công. Lỗi: " + throwable.getMessage());
                        }
                ));
    }



    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
