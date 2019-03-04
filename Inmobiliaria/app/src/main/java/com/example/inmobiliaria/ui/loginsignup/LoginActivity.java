package com.example.inmobiliaria.ui.loginsignup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.inmobiliaria.R;
import com.example.inmobiliaria.responses.LoginSignupResponse;
import com.example.inmobiliaria.retrofit.generator.ServiceGenerator;
import com.example.inmobiliaria.retrofit.services.LoginSignupService;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.inmobiliaria.ui.DashboardActivity;
import com.example.inmobiliaria.util.Util;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button btnLogin, btnRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.loginBtn);
        btnRegistro = findViewById(R.id.loginBtn2);
        doLogin();
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
            }
        });
    }

    public void onLoginSuccess(Call<LoginSignupResponse> call, Response<LoginSignupResponse> response) {
        Util.setData(LoginActivity.this, response.body().getToken(), response.body().getUser().get_id(),
                response.body().getUser().getEmail(),response.body().getUser().getName(),response.body().getUser().getPicture());
        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
        finish();
    }

    public void onLoginFail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setIcon(R.drawable.ic_cancelar);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setMessage(R.string.login_error)
                .setTitle(R.string.error);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void doLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Autenticando...");
                progressDialog.show();
                String emailTxt = email.getText().toString();
                String passwordTxt = password.getText().toString();
                String credentials = Credentials.basic(emailTxt, passwordTxt);
                LoginSignupService loginService = ServiceGenerator.createService(LoginSignupService.class);
                Call<LoginSignupResponse> call = loginService.login(credentials);
                call.enqueue(new Callback<LoginSignupResponse>() {
                    @Override
                    public void onResponse(final Call<LoginSignupResponse> call, final Response<LoginSignupResponse> response) {
                        if (response.isSuccessful()) {
                            Runnable progressRunnable = new Runnable() {

                                @Override
                                public void run() {
                                    progressDialog.cancel();
                                    onLoginSuccess(call, response);
                                }
                            };
                            Handler pdCanceller = new Handler();
                            pdCanceller.postDelayed(progressRunnable, 2000);
                        }
                        else {
                            progressDialog.cancel();
                            onLoginFail();
                        }
                    }
                    @Override
                    public void onFailure(Call<LoginSignupResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
