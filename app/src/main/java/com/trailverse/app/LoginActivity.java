package com.trailverse.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.trailverse.app.model.LoginRequest;
import com.trailverse.app.model.UserDto;
import com.trailverse.app.network.ApiClient;
import com.trailverse.app.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editId, editPassword;
    private Button btnLogin;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editId = findViewById(R.id.editId);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        apiService = ApiClient.getClient().create(ApiService.class);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = editId.getText().toString();
                String pw = editPassword.getText().toString();

                LoginRequest request = new LoginRequest(id, pw);
                loginUser(request);
            }
        });
    }

    private void loginUser(LoginRequest request) {
        // 🔧 서버 없이 임시 로그인 통과용
        Toast.makeText(LoginActivity.this, "디버깅용 로그인 통과 🎯", Toast.LENGTH_SHORT).show();

        // SharedPreferences에 더미 userId 저장 (선택)
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userId", "morty");  // 또는 아무거나 테스트용
        editor.apply();

        // 메인 화면으로 진입
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    /*
    // 원래 서버 로그인 코드 (나중에 복구)
    apiService.login(request).enqueue(new Callback<UserDto>() {
        @Override
        public void onResponse(Call<UserDto> call, Response<UserDto> response) {
            if (response.isSuccessful()) {
                UserDto user = response.body();
                Toast.makeText(LoginActivity.this, "환영합니다 🎉" + user.getNickname(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<UserDto> call, Throwable t) {
            Log.e("LOGIN_ERROR", "서버 연결 실패: " + t.getMessage());
            Toast.makeText(LoginActivity.this, "서버 연결 오류", Toast.LENGTH_SHORT).show();
        }
    });
    */
    }

}
