package x7a.droid.signuplogin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    //    private UserLoginTask mAuthTask = null;
    // UI references.
    public static final String PASSWORD_KEY = "password";
    public static final String EMAIL_KEY = "email";
    public static final String TOKEN_KEY = "token_authentication";
    private AutoCompleteTextView editEmail;
    private EditText editPass;
    private View mProgressView;
    private View mLoginFormView;
    private String strEmail, strPassword, strToken;
    TextView tv_respond, tv_result_api;
    ProgressBar pBar;
    ArrayList<User> arrayList = new ArrayList<>();
    //    private TextView status;
    public static final String BASE_URL_LOGIN = "http://private-f4dc2-signuplogin.apiary-mock.com";
    View focusView = null;
    BufferedReader reader = null;
    AlertDialog alert;
    Button sign, register;
    public Boolean status_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //koneksih
        if (!konekNet()) {
            Toast.makeText(this, "Ooopss, No Connection Detected", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this, "Connection Available", Toast.LENGTH_SHORT)
                    .show();
        }

        // Set up the login form.
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tv_respond = (TextView) findViewById(R.id.tv_respond);
        tv_result_api = (TextView) findViewById(R.id.tv_result_api);
        pBar = (ProgressBar)findViewById(R.id.login_progress);
        editEmail = (AutoCompleteTextView) findViewById(R.id.email);
                    populateAutoComplete();
        //SET VALUE SHARED PREFERENCES
//        //Shared Preferences can be Access From Different Activity
//        SharedPreferences IsLogin = getSharedPreferences("authentication", MODE_PRIVATE);
//        SharedPreferences.Editor IsLogin_editor = IsLogin.edit();
//        IsLogin_editor.putString("token_authentication", "fd@3jfD83#dfaksdfweqoru#LEWlkj");
//        IsLogin_editor.commit();
        //input editPass//
        editPass = (EditText) findViewById(R.id.password);
        editPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
//                    if(status_login == false){
                        Toast.makeText(LoginActivity.this, "Login Gagal", Toast.LENGTH_SHORT).show();
//                    }
                    return true;
                }
                return false;
            }

        });

        //Button Login Clicked//
        Button sign = (Button) findViewById(R.id.email_sign_in_button);
        sign.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                strEmail = editEmail.getText().toString();
                strPassword = editPass.getText().toString();
                attemptLogin();
            }
        });
        Button reg = (Button) findViewById(R.id.email_register_button);
        reg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);

            }
        });
    }

    private void populateAutoComplete() {
        String[] countries = getResources().getStringArray(R.array.autocomplete);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
        editEmail.setAdapter(adapter);
    }

    private void attemptLogin() {
        boolean mCancel = this.loginValidation();
        if (mCancel) {
            focusView.requestFocus();
        } else {
            login_cek(strEmail, strPassword);

        }
    }


    //
    private void login_cek(final String strEmail, final String strPassword) {
        //Try ti dieu
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'H:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_LOGIN)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        InterfaceApi login_api = retrofit.create(InterfaceApi.class);
        Call<Users> call = login_api.getUsers();
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                int status = response.code();
                tv_respond.setText("Nilai Response API = "+String.valueOf(status));
                for (Users.UserItem user : response.body().getUsers())
                {
//                    tv_result_api.append(
//                            "Id = " + String.valueOf(user.getId()) +
//                                    System.getProperty("line.separator") +
//                            "Email = " + user.getEmail() +
//                                    System.getProperty("line.separator") +
//                            "Password = " + user.getPassword() +
//                                    System.getProperty("line.separator") +
//                            "Token Authentication = " + user.getToken_authentication() +
//                                    System.getProperty("line.separator") +
//                                    System.getProperty("line.separator"));

                    status_login = false;
                    if (strEmail.equals(user.getEmail())&& strPassword.equals(user.getPassword())) {
                        status_login = true;
                        strToken = user.getToken_authentication();
                        status_login(status_login, strToken);
                        pBar.setVisibility(View.VISIBLE);
                        break;
                    }
                    //conditon true/false

//                    else{
//                        Toast.makeText(LoginActivity.this, "Something Wrong With Your Inputted", Toast.LENGTH_SHORT).show();
//                    }

                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                tv_respond.setText("Nilai Response API = "+String.valueOf(t));
                Log.d("Login", "Login Response-" + t.getMessage());
            }
        });
    }

    public void status_login(boolean status, String strToken){
    if(status) {

//                        Toast.makeText(LoginActivity.this,"Welcome Home", Toast.LENGTH_SHORT).show();
//
        //Shared Preferences can be Access From Different Activity
        SharedPreferences IsLogin = getSharedPreferences("authentication", MODE_PRIVATE);
        SharedPreferences.Editor IsLogin_editor = IsLogin.edit();
        IsLogin_editor.putString("token_authentication", strToken);
        IsLogin_editor.commit();

        Toast.makeText(LoginActivity.this, "Success Login", Toast.LENGTH_SHORT).show();
        //Start Activity
        Intent i = new Intent(LoginActivity.this, IntentActivity.class);
        startActivity(i);
        finish();

    }
    else Toast.makeText(LoginActivity.this, "Failed Login", Toast.LENGTH_SHORT).show();
    }
// Validasi Login
    private boolean loginValidation() {
        //RESET ERROR
        boolean cancel = false;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(strPassword) && !isPasswordValid(strPassword)) {
            editPass.setError(getString(R.string.error_invalid_password));
            focusView = editPass;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(strEmail)) {
            editEmail.setError(getString(R.string.error_field_required));
            focusView = editEmail;
            cancel = true;
        } else if (!isEmailValid(strEmail)) {
            editEmail.setError(getString(R.string.error_invalid_email));
            focusView = editEmail;
            cancel = true;
        }

        return cancel;
    }


    private boolean isEmailValid(String strEmail) {
        return strEmail.contains("@");
    }

    private boolean isPasswordValid(String strPassword) {
        return strPassword.length()>5;
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        editEmail.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    public boolean konekNet(){
        ConnectivityManager konek = (ConnectivityManager)this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = konek.getActiveNetworkInfo();
        if(netInfo !=null && netInfo.isConnectedOrConnecting()){
                return true;
        }else{
                return false;
        }
    }
}
