package x7a.droid.signuplogin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    TextView tv_respond;
    Button register;
    EditText regEmail, regPassword;
    String strId, strRegEmail, strRegPassword, strToken_authentication;
    public static final String BASE_URL_REGISTER = "http://private-f4dc2-signuplogin.apiary-mock.com";
    View focusView = null;
    ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tv_respond = (TextView) findViewById(R.id.tv_respond);
        register = (Button) findViewById(R.id.btnRegister);
        regEmail = (EditText) findViewById(R.id.regEmail);
        regPassword = (EditText) findViewById(R.id.regPassword);
        pBar = (ProgressBar) findViewById(R.id.register_progress);

        //Button Reg Clicked//
        Button register = (Button) findViewById(R.id.btnRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strRegEmail = regEmail.getText().toString();
                strRegPassword = regPassword.getText().toString();
                strToken_authentication = "qwerty12345";
                attemptRegister();
            }

        });
    }

    private void attemptRegister() {
        boolean mCancel = this.registerValidation();
        if (mCancel) {
            focusView.requestFocus();
        } else {
            register_cek();
        }
    }

    // Validasi Register
    private boolean registerValidation() {
        //RESET ERROR
        boolean cancel = false;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(strRegEmail) && !isPasswordValid(strRegPassword)) {
            regPassword.setError(getString(R.string.error_invalid_password));
            focusView = regPassword;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(strRegEmail)) {
            regEmail.setError(getString(R.string.error_field_required));
            focusView = regEmail;
            cancel = true;
        } else if (!isEmailValid(strRegEmail)) {
            regEmail.setError(getString(R.string.error_invalid_email));
            focusView = regEmail;
            cancel = true;
        }

        return cancel;
    }

    private boolean isEmailValid(String strRegEmail) {
        return strRegEmail.contains("@");
    }

    private boolean isPasswordValid(String strRegPassword) {
        return strRegPassword.length() > 5;
    }

    private void register_cek() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'H:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_REGISTER)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        final InterfaceApi register_api = retrofit.create(InterfaceApi.class);
// implement Add User
//        User user = new User("777@7.com", "7777777Dqwe", "jiwej789(*dsjf");
        User user = new User(strRegEmail, strRegPassword, strToken_authentication);
        Call<User> call = register_api.saveUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                int status = response.code();
                tv_respond.setText("Login Response=" +String.valueOf(status));
                pBar.setVisibility(View.VISIBLE);
                pBar.getProgress();

                if(String.valueOf(status).equals("201")){
                    Toast.makeText(RegisterActivity.this, "SUCCEED, Try Relogin Now", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(RegisterActivity.this, "FAILED, Try Register Again", Toast.LENGTH_SHORT).show();

                }
//                finish();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                tv_respond.setText("Nilai Response API = "+String.valueOf(t));
                Log.d("Login", "Login Response-" + t.getMessage());
            }
        });
    }
}
