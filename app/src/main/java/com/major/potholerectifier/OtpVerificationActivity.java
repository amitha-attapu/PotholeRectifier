package com.major.potholerectifier;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.major.potholerectifier.utils.AppConstants.BASE_URL;
import static com.major.potholerectifier.utils.AppConstants.REQ_PASSWORD;
import static com.major.potholerectifier.utils.AppConstants.REQ_PHONE;
import static com.major.potholerectifier.utils.AppConstants.REQ_STRING;
import static com.major.potholerectifier.utils.AppConstants.REQ_USERNAME;

public class OtpVerificationActivity extends AppCompatActivity {


    EditText otpText;
    Button otp_submit;
    String username,password,phone;
    private final String TAG="OtpVerificationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        otp_submit = findViewById(R.id.otp_submit_button);
        otpText = findViewById(R.id.otp_text);
        username = (String) getIntent().getStringExtra(REQ_USERNAME);
        password = (String) getIntent().getStringExtra(REQ_PASSWORD);
        phone = (String) getIntent().getStringExtra(REQ_PHONE);



        otp_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = otpText.getText().toString();
                if(otp.isEmpty()){
                    otpText.setError("Enter OTP hre");
                    return;
                }else if(otp.equals("1234")){
                    signupnow();

                }else{
                    otpText.setError("Enter Valid OTP");
                }
            }
        });

    }

    private void signupnow() {

//        showToast("making Sign up with \n un:"+userName+"\nps:"+password+"\n:ph"+phone);

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL+"/users/signup").newBuilder();
        urlBuilder.addQueryParameter("username", username);
        urlBuilder.addQueryParameter("password", password);
        urlBuilder.addQueryParameter("phone", phone);
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
            public void onResponse(Call call,final Response response) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        try {
                            if (response.code() == 200) {
                                String jsonData = response.body().string();
                                Log.e(TAG, "Response: " + jsonData);

                                try {
                                    final JSONObject userObject = new JSONObject(jsonData);
                                    OtpVerificationActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.e(TAG, "Details: " + userObject.toString());
                                            showToast("Successfully Signup, Login to continue");
                                            startActivity(new Intent(OtpVerificationActivity.this,MainActivity.class));
                                            finish();
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (response.code() == 409) {
                                showToast("Username/phone number already exits, Change and retry");
                                (OtpVerificationActivity.this).onBackPressed();
                            } else {
                                showToast("Unable to sign up");

                            }
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                });


            }
        });




    }
    void showToast(String msg) {
        Toast.makeText(OtpVerificationActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

}
