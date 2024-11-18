package com.major.potholerectifier;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.major.potholerectifier.model.UserDetails;
import com.major.potholerectifier.utils.DbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static com.major.potholerectifier.utils.AppConstants.BASE_URL;
import static com.major.potholerectifier.utils.AppConstants.GOVT_LOGIN;
import static com.major.potholerectifier.utils.AppConstants.PUBLIC_LOGIN;
import static com.major.potholerectifier.utils.AppConstants.PUBLIC_SIGNUP;
import static com.major.potholerectifier.utils.AppConstants.REQ_STRING;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences sharedPreferences = getSharedPreferences("POTHOLE",MODE_PRIVATE);
        int loggedin = sharedPreferences.getInt(REQ_STRING,-1);
        if(loggedin!=-1){

            if(loggedin == GOVT_LOGIN){
                Intent intent = new Intent(MainActivity.this,GovernmentHomeActivity.class);
                intent.putExtra(REQ_STRING,GOVT_LOGIN); // 3
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(MainActivity.this,PublicHomeActivity.class);
                intent.putExtra(REQ_STRING,PUBLIC_LOGIN); // 2
                startActivity(intent);
                finish();
            }



        }else{

            findViewById(R.id.main_govt_login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,LoginSignUpActivity.class);
                    intent.putExtra(REQ_STRING,GOVT_LOGIN); // 3
                    startActivity(intent);
                }
            });
            findViewById(R.id.main_public_login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,LoginSignUpActivity.class);
                    intent.putExtra(REQ_STRING,PUBLIC_LOGIN); // 1
                    startActivity(intent);
                }
            });
            findViewById(R.id.main_public_signup).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,LoginSignUpActivity.class);
                    intent.putExtra(REQ_STRING,PUBLIC_SIGNUP); //2
                    startActivity(intent);
                }
            });

            // setUpDateBase();
//        ConnectMySql connectMySql = new ConnectMySql();
//        connectMySql.execute("");
            run();
        }






    }

    private void run() {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(BASE_URL+"users")
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
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG,"Response: "+response.toString());

                    String jsonData = response.body().string();
                    try {
                        final JSONArray jarray =  new JSONArray(jsonData);
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                              for(int i=0;i<jarray.length();i++){
                                  Log.e(TAG,"Details: "+((JSONObject)jarray.opt(i)).toString());
//                                  Log.e(TAG,"Details: "+((JSONObject)jarray.opt(i)).optString("usernmae"));
                              }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });


    }

    private void setUpDateBase() {

        Log.e(TAG,"Setting database");
        DbHelper dbh = new DbHelper("jdbc:mysql://192.168.0.100:3306/pothole_rectifier","root","1234");
        try {
            Log.e(TAG,"Init database");
            dbh.init();
            dbh.testData();
//            List<UserDetails> users =  dbh.getUsers();
//            Log.e(TAG,"User Detaisl");
//            for(UserDetails us : users){
//                Log.e(TAG,"Details: "+us.toString());
//            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Please wait...", Toast.LENGTH_SHORT)
                    .show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://192.168.0.100:3306/pothole_rectifier","root","1234");
                Log.e(TAG,"Databaseection success");

                String result = "Database Connection Successful\n";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM pothole_rectifier.pr_user");
                ResultSetMetaData rsmd = rs.getMetaData();

                while (rs.next()) {
                    result += rs.getString(3).toString() + "\n";
                    Log.e(TAG,rs.getString(2).toString() );

                }

                res = result;
            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG,"Results:"+result);
        }
    }
}
