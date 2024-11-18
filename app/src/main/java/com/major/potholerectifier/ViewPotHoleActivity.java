package com.major.potholerectifier;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.major.potholerectifier.model.PotHole;
import com.major.potholerectifier.model.PotHoleStatus;

import java.io.IOException;
import java.util.Date;

import static com.major.potholerectifier.utils.AppConstants.BASE_URL;
import static com.major.potholerectifier.utils.AppConstants.GOVT_LOGIN;
import static com.major.potholerectifier.utils.AppConstants.PUBLIC_LOGIN;
import static com.major.potholerectifier.utils.AppConstants.REQ_VIEW_PH;
import static com.major.potholerectifier.utils.AppConstants.REQ_VIEW_PH_FROM;
import static com.major.potholerectifier.utils.AppConstants.SDF;

public class ViewPotHoleActivity extends AppCompatActivity {

    ImageView potHoleimageView,completedImageView;
    TextView location,landmark;
    TextView localitySpinner,completedTime;
    Button updatePotHoleButton;
    PotHole ph;
    private final String TAG = "ViewPotHoleActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pot_hole);

        ph = (PotHole)getIntent().getSerializableExtra(REQ_VIEW_PH);
        if(ph==null){
            showToast("Unable to retieve data");
            this.onBackPressed();
            return;
        }

        int viewFrom = getIntent().getIntExtra(REQ_VIEW_PH_FROM,-1);
        potHoleimageView = (ImageView)findViewById(R.id.add_pot_hole_image_view);
        location = (TextView) findViewById(R.id.current_location_text_view);
        landmark = (TextView) findViewById(R.id.landmark_text_view);
        localitySpinner = (TextView)findViewById(R.id.locality_spinner_text_view);
        updatePotHoleButton = (Button)findViewById(R.id.view_pot_hole_button);
        localitySpinner.setText(ph.getLocality());
        landmark.setText(ph.getLocality());
        completedTime =  (TextView) findViewById(R.id.completed_time_text_view);
        completedImageView = findViewById(R.id.completed_pot_hole_image_view);

        byte[] decodedString = Base64.decode(ph.getImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        potHoleimageView.setImageBitmap(decodedByte);


        if(viewFrom == GOVT_LOGIN){
            if(ph.getStatus().equals(PotHoleStatus.OPEN)){
                updatePotHoleButton.setText("Mark as Inprogress");
                updatePotHoleButton.setVisibility(View.VISIBLE);

            }else if(ph.getStatus().equals(PotHoleStatus.INPROGRESS)){
                updatePotHoleButton.setText("Mark as Completed");
                updatePotHoleButton.setVisibility(View.VISIBLE);

            }else{
                updatePotHoleButton.setVisibility(View.GONE);
            }
        }else{
            updatePotHoleButton.setVisibility(View.GONE);
        }


        if(ph.getStatus().equals(PotHoleStatus.COMPLETED)){
            findViewById(R.id.completed_data).setVisibility(View.VISIBLE);
            completedTime.setText("Completed At: "+ph.getCompletedAt());

            byte[] completeddecodedString = Base64.decode(ph.getCompletedimage(), Base64.DEFAULT);
            Bitmap completeddecodedByte = BitmapFactory.decodeByteArray(completeddecodedString, 0, completeddecodedString.length);
            completedImageView.setImageBitmap(completeddecodedByte);
        }


        updatePotHoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ph.getStatus().equals(PotHoleStatus.OPEN)){
                    showToast("Marking as Inprogress");
                    updatePotHole(PotHoleStatus.INPROGRESS);
                }else if(ph.getStatus().equals(PotHoleStatus.INPROGRESS)){
                    updatePotHole(PotHoleStatus.COMPLETED);
                    showToast("Mark as Completed");
                }
            }
        });
    }

    private void updatePotHole(PotHoleStatus status) {
        if(status.equals(PotHoleStatus.COMPLETED)){

            showToast("Clicked on " + (ph.getLocality()));
            Intent intent = new Intent(ViewPotHoleActivity.this, CaptureActivity.class);
            intent.putExtra(REQ_VIEW_PH,  ph);
            intent.putExtra(REQ_VIEW_PH_FROM, PUBLIC_LOGIN);
            startActivity(intent);

        }else if(status.equals(PotHoleStatus.INPROGRESS)){
            ph.setStatus(PotHoleStatus.INPROGRESS);
            ph.setUpdatedAt(SDF.format(new Date()));

            if(ph.getId()!=0){
                OkHttpClient client = new OkHttpClient();
                Gson gson = new Gson();
                String json = gson.toJson(ph);
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, json);
                Request request = new Request.Builder()
                        .url(BASE_URL+"pothole")
                        .post(body)
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
                                    if (response.code() == 200) { // pothole is saved - OK
                                        String jsonData = response.body().string();
                                        Log.e(TAG, "Response: " + jsonData);
                                        showToast("Successfully updated pot hot");
                                        startActivity(new Intent(ViewPotHoleActivity.this,GovernmentHomeActivity.class));
                                        finish();
                                    } else { //
                                        showToast("Unable to upload pothole");

                                    }
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                        });


                    }
                });




            }else{
                showToast("Incomplete Data Recieved, try again");
            }
        }



    }

    void showToast(String msg) {
        Toast.makeText(ViewPotHoleActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

}
