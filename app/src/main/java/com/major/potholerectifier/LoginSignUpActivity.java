package com.major.potholerectifier;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.major.potholerectifier.utils.AppConstants.BASE_URL;
import static com.major.potholerectifier.utils.AppConstants.GOVT_LOGIN;
import static com.major.potholerectifier.utils.AppConstants.PUBLIC_LOGIN;
import static com.major.potholerectifier.utils.AppConstants.PUBLIC_SIGNUP;
import static com.major.potholerectifier.utils.AppConstants.REQ_PASSWORD;
import static com.major.potholerectifier.utils.AppConstants.REQ_PHONE;
import static com.major.potholerectifier.utils.AppConstants.REQ_STRING;
import static com.major.potholerectifier.utils.AppConstants.REQ_USERNAME;

public class LoginSignUpActivity extends AppCompatActivity {

    TextView titleTextView;
    LinearLayout loginForm, signupForm;
    Button loginSubmitButton, signSubmitButton;
    EditText userNameLogin,passwordLogin,userNameSignup,passwordSignup,phoneSignup;
    private final String TAG =  "LoginSignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signup_activity);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

        final int req = (int) getIntent().getIntExtra(REQ_STRING, -1); // reading the parameter
        if (req < 0) {
            showToast("Unknown Request, Sending to login");
            Intent intent = new Intent(LoginSignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        titleTextView = (TextView) findViewById(R.id.login_signup_text_view);
        loginForm = (LinearLayout) findViewById(R.id.login_form);
        signupForm = (LinearLayout) findViewById(R.id.signup_form);
        loginSubmitButton = (Button) findViewById(R.id.main_public_login_submit);
        signSubmitButton = (Button) findViewById(R.id.main_public_signup_submit);
        userNameLogin = (EditText)findViewById(R.id.login_public_username);
        passwordLogin = (EditText)findViewById(R.id.login_public_password);
        userNameSignup = (EditText)findViewById(R.id.signup_public_username);
        passwordSignup = (EditText)findViewById(R.id.signup_public_password);
        phoneSignup = (EditText)findViewById(R.id.signup_public_phone);



///
        switch (req) {
            case PUBLIC_LOGIN:
                titleTextView.setText("Public Login");
                loginForm.setVisibility(View.VISIBLE);
                signupForm.setVisibility(View.GONE);

                userNameLogin.setText("john");
                passwordLogin.setText("1234");


                break;
            case PUBLIC_SIGNUP:
                titleTextView.setText("Public Sign-up");
                loginForm.setVisibility(View.GONE);
                signupForm.setVisibility(View.VISIBLE);
                break;

            case GOVT_LOGIN:
                titleTextView.setText("Government Login");
                loginForm.setVisibility(View.VISIBLE);
                signupForm.setVisibility(View.GONE);
//                userNameLogin.setText("govt");
//                passwordLogin.setText("govt");
                break;
            default:
                titleTextView.setText("Unknown Access");

        }

        if (req == PUBLIC_LOGIN || req == GOVT_LOGIN) {
            loginSubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String userName = userNameLogin.getText().toString();
                    String password = passwordLogin.getText().toString();
                    if(userName.isEmpty()){
                        userNameLogin.setError("Username is required");
                        return;
                    }
                    if(password.isEmpty()){
                        passwordLogin.setError("Password  is required");
                        return;
                    }
                    loginAction(userName,password,req);
//                    showToast("Submitting login :" + (req == PUBLIC_LOGIN ? "Public" : "Government"));
                }
            });
        } else if (req == PUBLIC_SIGNUP) {
            signSubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    showToast("Submitting Signup : Public");
                    String userName = userNameSignup.getText().toString();
                    String password = passwordSignup.getText().toString();
                    String phone = phoneSignup.getText().toString();
                    if(userName.isEmpty()){
                        userNameSignup.setError("Username is required");
                        return;
                    }
                    if(password.isEmpty()){
                        passwordSignup.setError("Password  is required");
                        return;
                    }
                    if(phone.isEmpty()){
                        phoneSignup.setError("Phone  is required");
                        return;
                    }
                    signupAction(userName,password,phone);

                }
            });
        }


    }

    private void signupAction(String userName, String password, String phone) {
        Intent intent = new Intent(LoginSignUpActivity.this,OtpVerificationActivity.class);
        intent.putExtra(REQ_USERNAME,userName);
        intent.putExtra(REQ_PASSWORD,password);
        intent.putExtra(REQ_PHONE,phone);
        startActivity(intent);

    }

    private void loginAction(String userName, String password, final int req) {
        if(req == GOVT_LOGIN && userName.equalsIgnoreCase("govt") && password.equalsIgnoreCase("govt")){
            showToast("Successfully Loggedin as Govt");

            SharedPreferences.Editor editor = getSharedPreferences("POTHOLE",MODE_PRIVATE).edit();
            editor.putInt(REQ_STRING,req);
            editor.commit();
            Intent intent = new Intent(LoginSignUpActivity.this,GovernmentHomeActivity.class);
            startActivity(intent);
            finish();
        }else if(req == PUBLIC_LOGIN){


            OkHttpClient client = new OkHttpClient();

            HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL+"/users/login").newBuilder();
                    urlBuilder.addQueryParameter("username", userName);
                    urlBuilder.addQueryParameter("password", password);
            String url = urlBuilder.build().toString();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Log.e(TAG,"Calling: ");

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG,"Response: "+e.getMessage());
                    e.printStackTrace();
                    call.cancel();
                }
                @Override
                public void onResponse(Call call,final Response response) throws IOException {
//                    Log.e(TAG,"Response: "+response.body().toString());


                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                if (response.code() == 200) { // 200 - OK
                                    String jsonData = response.body().string();
                                    Log.e(TAG, "Response: " + jsonData);

                                    try {
                                        final JSONObject userObject = new JSONObject(jsonData);
                                        LoginSignUpActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.e(TAG, "Details: " + userObject.toString());
                                                SharedPreferences.Editor editor = getSharedPreferences("POTHOLE",MODE_PRIVATE).edit();
                                                editor.putInt(REQ_STRING,req);
                                                editor.commit();
                                                startActivity(new Intent(LoginSignUpActivity.this,PublicHomeActivity.class));
                                                finish();
                                            }
                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else if (response.code() == 401) { // 401 - UNAUTHORIZED
                                    showToast("Username/password incorrect");
                                } else {

                                }
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    });


                }
            });

        }else{
            showToast("Username/password incorrect");

        }
    }

    void showToast(String msg) {
        Toast.makeText(LoginSignUpActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


}