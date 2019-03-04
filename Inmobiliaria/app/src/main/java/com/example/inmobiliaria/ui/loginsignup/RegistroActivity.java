package com.example.inmobiliaria.ui.loginsignup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.inmobiliaria.R;
import com.example.inmobiliaria.models.register;
import com.example.inmobiliaria.responses.LoginSignupResponse;
import com.example.inmobiliaria.retrofit.generator.ServiceGenerator;
import com.example.inmobiliaria.retrofit.services.LoginSignupService;
import com.example.inmobiliaria.ui.DashboardActivity;
import com.example.inmobiliaria.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {
    private EditText nameSignup, emailSignup, passwordSignup, repit_passwordSignup;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        nameSignup = findViewById(R.id.signupName);
        emailSignup = findViewById(R.id.signupEmail);
        passwordSignup = findViewById(R.id.signupPassword);
        repit_passwordSignup = findViewById(R.id.signupRepitpassword);
        btnSignup = findViewById(R.id.signupBtn);
        doRegister();
    }

    public void onRegisterSuccess(Call<LoginSignupResponse> call, Response<LoginSignupResponse> response) {
        Util.setData(RegistroActivity.this, response.body().getToken(), response.body().getUser().get_id(),
                response.body().getUser().getEmail(),response.body().getUser().getName(),response.body().getUser().getPicture());
                startActivity(new Intent(RegistroActivity.this, DashboardActivity.class));
    }

    public void onRegisterFail(int tipoError) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroActivity.this);
        builder.setIcon(R.drawable.ic_cancelar);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setMessage(tipoError).setTitle(R.string.error);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void doRegister() {
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(RegistroActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Registrando...");
                progressDialog.show();
                String name = nameSignup.getText().toString().trim();
                String email = emailSignup.getText().toString().trim();
                String password = passwordSignup.getText().toString().trim();
                String compassword = repit_passwordSignup.getText().toString().trim();
                if(password.length() < 6){
                    onRegisterFail(R.string.register_contraseña_no_segura);
                } else if(!password.equals(compassword)){
                    onRegisterFail(R.string.register_contraseña_incorrecta);
                } else {
                    register datosRegistro = new register(name, email, password);
                    createUser(datosRegistro, progressDialog);
                }
            }
        });
    }

    public void createUser(register registro, final ProgressDialog progressDialog){
        LoginSignupService service = ServiceGenerator.createService(LoginSignupService.class);
        Call<LoginSignupResponse> registerResponseCall = service.register(registro);
        registerResponseCall.enqueue(new Callback<LoginSignupResponse>() {
            @Override
            public void onResponse(Call<LoginSignupResponse> call, Response<LoginSignupResponse> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    onRegisterSuccess(call, response);
                } else {
                    progressDialog.dismiss();
                    onRegisterFail(R.string.error);
                }
            }
            @Override
            public void onFailure(Call<LoginSignupResponse> call, Throwable t) {
                Log.e("NetworkFail", t.getMessage());
                Toast.makeText(RegistroActivity.this, getString(R.string.Error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
