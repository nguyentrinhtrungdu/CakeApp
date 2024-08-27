package com.example.cakeapp.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cakeapp.R;
import com.example.cakeapp.model.User;
import com.example.cakeapp.retrofit.ApiApp;
import com.example.cakeapp.retrofit.RetrofitClient;
import com.example.cakeapp.utils.Utils;



import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;




public class ProfileActivity extends AppCompatActivity {
    private EditText username, email, pass, num;
    private Button btnLuu;
    private ApiApp apiApp;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        int userId = Paper.book().read("id", -1);
        Paper.book().write("id", userId);


        // Kiểm tra và ghi log giá trị userId
        Log.d("ProfileActivity", "User ID: " + userId);

        // Kiểm tra nếu userId không hợp lệ
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy ID người dùng", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu không tìm thấy ID người dùng
            return;
        }
        initView();
        initControl();
        loadUserProfile(userId);
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiApp = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiApp.class);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        num = findViewById(R.id.num);
        btnLuu = findViewById(R.id.btnluu);
    }

    private void initControl() {
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });
    }

    private void loadUserProfile(int userId) {
        compositeDisposable.add(apiApp.getUserById(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if (response != null && response.isSuccess() && response.getResult() != null && !response.getResult().isEmpty()) {
                                User user = response.getResult().get(0); // Lấy người dùng đầu tiên trong danh sách
                                Log.d("ProfileActivity", "User ID: " + user.getId());
                                Log.d("ProfileActivity", "Username: " + user.getUsername());
                                Log.d("ProfileActivity", "Email: " + user.getEmail());
                                Log.d("ProfileActivity", "Phone: " + user.getNum());

                                // Cập nhật giao diện người dùng với dữ liệu người dùng
                                username.setText(user.getUsername());
                                email.setText(user.getEmail());
                                pass.setText(user.getPass());
                                num.setText(user.getNum());
                            } else {
                                // Thông báo không tìm thấy người dùng
                                Toast.makeText(getApplicationContext(), "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            // Xử lý lỗi
                            Log.e("ProfileActivity", "Error: " + throwable.getMessage());
                            Toast.makeText(getApplicationContext(), "Lỗi: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }






    private void updateUserProfile() {
        String strUsername = username.getText().toString().trim();
        String strEmail = email.getText().toString().trim();
        String strPass = pass.getText().toString().trim();
        String strNum = num.getText().toString().trim();

        if (TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strEmail) ||
                TextUtils.isEmpty(strPass) || TextUtils.isEmpty(strNum)) {
            Toast.makeText(getApplicationContext(), "Tất cả các trường đều bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = Utils.user_current.getId(); // Hoặc dùng Paper.book().read("user_id", -1);

        compositeDisposable.add(apiApp.updateUser(userId, strUsername, strEmail, strPass, strNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        updatedUser -> {
                            if (updatedUser != null) {
                                Toast.makeText(getApplicationContext(), "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Cập nhật hồ sơ không thành công", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Lỗi: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();  // Close the activity when the back button is pressed
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
