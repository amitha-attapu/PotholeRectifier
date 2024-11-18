package com.major.potholerectifier;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.major.potholerectifier.adapter.PotHoleListAdapter;
import com.major.potholerectifier.model.PotHole;
import com.major.potholerectifier.model.PotHoleLocation;
import com.major.potholerectifier.model.PotHoleStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.major.potholerectifier.utils.AppConstants.BASE_URL;
import static com.major.potholerectifier.utils.AppConstants.PUBLIC_LOGIN;
import static com.major.potholerectifier.utils.AppConstants.REQ_STRING;
import static com.major.potholerectifier.utils.AppConstants.REQ_VIEW_PH;
import static com.major.potholerectifier.utils.AppConstants.REQ_VIEW_PH_FROM;

public class PublicHomeActivity extends AppCompatActivity {

    ListView potHolesListView;
    TextView noPotHolesText;
    FloatingActionButton addPotHoleButton;
    LocationManager mLocationManager;
    private final String TAG ="PublicHomeActivity";
    List<PotHole> potHoles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_home);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

        potHolesListView = (ListView)findViewById(R.id.pot_holes_public_list);
        noPotHolesText = (TextView)findViewById(R.id.no_pot_hole_text);
        addPotHoleButton = (FloatingActionButton)findViewById(R.id.add_pothole_fab);
        addPotHoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Clicked on fab");
                startActivity(new Intent(PublicHomeActivity.this,AddPotHoleActivity.class));

            }
        });

        getPotHoles();

        potHolesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                // TODO Auto-generated method stub
                try {
                    PotHole ph = potHoles.get(position);
                    showToast("Clicked on " + (ph.getLocality()));
                    Intent intent = new Intent(PublicHomeActivity.this, ViewPotHoleActivity.class);
                    intent.putExtra(REQ_VIEW_PH,  ph);
                    intent.putExtra(REQ_VIEW_PH_FROM, PUBLIC_LOGIN);
                    startActivity(intent);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

    }

    private void getPotHoles() {


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URL+"pothole?userid=0")
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
                            if (response.code() == 200) { // ok
                                String jsonData = response.body().string();
                                Log.e(TAG, "Response: " + jsonData);

                                try {
                                    final JSONArray potJsonArray = new JSONArray(jsonData);
                                    PublicHomeActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.e(TAG, "Details: " + potJsonArray.toString());
                                            if(potHoles!=null)
                                                potHoles.clear();
                                            potHoles = new ArrayList<>();
                                            for(int i=0;i<potJsonArray.length();i++){
                                                try {
                                                    JSONObject pot = (JSONObject) potJsonArray.opt(i);
                                                    Log.e(TAG, "Details: asdasd" + (pot));
                                                    PotHole potHole = new PotHole();
                                                    potHole.setLocality(pot.getString("locality"));
                                                    potHole.setCompletedAt(pot.getString("completedAt"));
                                                    potHole.setCompletedimage(pot.getString("completedimage"));
                                                    potHole.setCreatedAt(pot.getString("createdAt"));
                                                    potHole.setCreatedBy(pot.getLong("createdBy"));
                                                    potHole.setId(pot.getLong("id"));
                                                    potHole.setLandmark(pot.getString("landmark"));
                                                    potHole.setStatus(PotHoleStatus.valueOf(pot.getString("status")));

                                                    PotHoleLocation loc = new PotHoleLocation();
                                                    JSONObject locJson = pot.getJSONObject("location");
                                                    loc.setLng(locJson.getDouble("lng"));
                                                    loc.setLat(locJson.getDouble("lat"));
                                                    loc.setAddress(locJson.getString("address"));
                                                    potHole.setLocation(loc);
                                                    potHole.setUpdatedAt(pot.getString("updatedAt"));
                                                    potHole.setImage(pot.getString("image"));
                                                    // adding pothole to the list
                                                    potHoles.add(potHole);
                                                }catch (Exception ex){
                                                    ex.printStackTrace();

                                                }

                                            }
                                            if(potHoles.isEmpty()){
                                                noPotHolesText.setVisibility(View.VISIBLE);
                                            }else{
                                                // Setting Adapter to LISTVIEW
                                                PotHoleListAdapter adapter=new PotHoleListAdapter(PublicHomeActivity.this,potHoles);
                                                potHolesListView.setAdapter(adapter);
                                                noPotHolesText.setVisibility(View.GONE);
                                            }

                                        }
                                    });




                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (response.code() == 401) {
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



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.public_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.how_to_use_menu:

                Intent intent = new Intent(PublicHomeActivity.this,HowToUseActivity.class);
                intent.putExtra(REQ_STRING, PUBLIC_LOGIN);
                startActivity(intent);
                break;
            case R.id.logout_menu:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure to logout?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog

                        dialog.dismiss();

                        SharedPreferences.Editor editor = getSharedPreferences("POTHOLE",MODE_PRIVATE).edit();
                        editor.remove(REQ_STRING);
                        editor.commit();

                        Intent intent = new Intent(PublicHomeActivity.this,MainActivity.class);
                        intent.putExtra(REQ_STRING, PUBLIC_LOGIN);
                        startActivity(intent);
                        finish();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    void showToast(String msg) {
        Toast.makeText(PublicHomeActivity.this, msg, Toast.LENGTH_SHORT).show();
    }






}
