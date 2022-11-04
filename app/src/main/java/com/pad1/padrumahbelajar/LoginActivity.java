package com.pad1.padrumahbelajar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pad1.padrumahbelajar.databinding.ActivityMainBinding;
import com.pad1.padrumahbelajar.tutorial.TutorialActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText editTextUsrnm;
    EditText editTextPw;
    Context mContext;
    BaseApiService mApiService;
    ProgressDialog loading;
    TextView textViewSignup;
    SharedPrefManager sharedPrefManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        mApiService = UtilsApi.getAPIService();

        editTextPw = findViewById(R.id.editTextPw);
        editTextUsrnm = findViewById(R.id.editTextUsrnm);
        textViewSignup = findViewById(R.id.textViewSignup);
        sharedPrefManager = new SharedPrefManager(this);

        textViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        if (sharedPrefManager.getSPSudahLogin()){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }





    }




    public void loginBtn(View view) {
        requestLogin();
    }

    private void requestLogin() {
        mApiService.loginRequest(editTextUsrnm.getText().toString(), editTextPw.getText().toString())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
//                            loading.dismiss();
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("status").equals("success")) {

                                    String role = jsonRESULTS.getString("role");
                                    Log.d("role",role);
                                    String nama = jsonRESULTS.getString("nama");
                                    sharedPrefManager.saveSPString(SharedPrefManager.SP_NAMA,nama);
                                    String userName = jsonRESULTS.getString("username");
                                    sharedPrefManager.saveSPString(SharedPrefManager.SP_USERNAME, userName);
                                    String token = jsonRESULTS.getString("token");
                                    sharedPrefManager.saveSPString(SharedPrefManager.SP_TOKEN,token);
                                    sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN,true);

                                    // Jika login berhasil maka data nama yang ada di response API
                                    // akan diparsing ke activity selanjutnya.
                                    Intent intent = new Intent(getApplicationContext(), TutorialActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(), "Login Berhasil", Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(mContext, "BERHASIL LOGIN", Toast.LENGTH_SHORT).show();
//                                    String nama = jsonRESULTS.getJSONObject("user").getString("nama");
////                                    sharedPrefManager.saveSPString(SharedPrefManager.SP_NAMA, nama);
////                                    // Shared Pref ini berfungsi untuk menjadi trigger session login
////                                    sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, true);
//                                    startActivity(new Intent(mContext, MainActivity.class)
//                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
//                                    finish();
                                } else {
                                    // Jika login gagal
//                                    String error_message = jsonRESULTS.getString("error_msg");
                                    Toast.makeText(getApplicationContext(), "GAGAL LOGIN", Toast.LENGTH_SHORT).show();
                                    String msg = jsonRESULTS.getString("message");
                                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                                    alertDialog.setTitle("Failed to log in");
                                    alertDialog.setMessage(msg);
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    alertDialog.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "GAGAL LOGIN", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
//                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug", "onFailure: ERROR > " + t.toString());
                        loading.dismiss();
                    }
                });

    }


}
