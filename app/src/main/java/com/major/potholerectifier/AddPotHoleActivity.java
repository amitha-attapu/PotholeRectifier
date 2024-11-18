package com.major.potholerectifier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.major.potholerectifier.model.PotHole;
import com.major.potholerectifier.model.PotHoleLocation;
import com.major.potholerectifier.model.PotHoleStatus;
import com.major.potholerectifier.utils.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.major.potholerectifier.utils.AppConstants.BASE_URL;
import static java.sql.DriverManager.println;

public class AddPotHoleActivity extends AppCompatActivity {

    ImageView potHoleimageView;
    EditText location,landmark;
    Spinner localitySpinner;
    Button addPotHoleButton;
    private final int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/potholeRectifier";
    boolean capturedImage = false;
    LocationManager mLocationManager;
    PotHoleLocation currentLocation;
    ImageButton addLocation ;
    private final String TAG ="AddPotHoleActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pot_hole);


        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }


        requestMultiplePermissions(); // check permission
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        mLocationManager.requestLocationUpdates(mLocationRequest, this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            showToast("Give location permisson to continue further");
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                100, mLocationListener);


        potHoleimageView = (ImageView)findViewById(R.id.add_pot_hole_image_view);
        location = (EditText) findViewById(R.id.current_location_text);
        landmark = (EditText) findViewById(R.id.landmark_text);
        localitySpinner = (Spinner)findViewById(R.id.locality_spinner);
        addPotHoleButton = (Button)findViewById(R.id.add_pot_hole_submit_button);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item, AppConstants.localities);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        localitySpinner.setAdapter(spinnerArrayAdapter);
        addLocation = findViewById(R.id.pick_location_address_button);

        potHoleimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPictureDialog();
            }
        });


        addPotHoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!capturedImage){
                    showToast("Upload Image by clicking on image");
                    return;
                }

                String locationStr =location.getText().toString();
                String localityStr = (String)localitySpinner.getSelectedItem();
                String landmarkStr = landmark.getText().toString();
                if(locationStr.isEmpty()){
//                    showToast("Enter location");
                    location.setError("Enter Location");
                    return;
                }
                if(localityStr.equalsIgnoreCase("Select Locality")){
                    showToast("Select Locality");
                    return;
                }
                if(landmarkStr.isEmpty()){
                    landmark.setError("Enter the landmark");
                    return;
                }
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) potHoleimageView.getDrawable());
                Bitmap bitmap = bitmapDrawable .getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);

                savePotHole(imageInByte,currentLocation,localityStr,landmarkStr);



            }
        });

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLocation!=null){
                    String add = currentLocation.getAddress();
                    if(add!=null){
                        location.setText(currentLocation.getAddress());
                    }else{
                        showToast("Unable to pick location, Open google maps and check");

                        location.setError("Unable to fetch location");
                        location.setText("");
                    }

                }else{
                    showToast("Unable to pick location, Open google maps and check");
                    location.setError("Check GSP is ON");
                    location.setText("");

                }
            }
        });



    }

    private void savePotHole(byte[] imageInByte, PotHoleLocation locationStr, String localityStr, String landmarkStr) {


        PotHole potHole = new PotHole();
        if(locationStr!=null){
            potHole.setLocation(locationStr);

        }else{
            potHole.setLocation(new PotHoleLocation(17.2342,78.123123,"Test loaction"));

        }
        potHole.setStatus(PotHoleStatus.OPEN);
        potHole.setLandmark(landmarkStr);
        potHole.setCreatedBy(1L);
        potHole.setLocality(localityStr);
        // image is converted to Bolb
        String encoded = Base64.encodeToString(imageInByte,Base64.DEFAULT);
        potHole.setImage(encoded);
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        String json = gson.toJson(potHole);
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

                                showToast("Successfully added pot hole");
                                startActivity(new Intent(AddPotHoleActivity.this,PublicHomeActivity.class));
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








    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }
    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    showToast("Image Saved!");
                    potHoleimageView.setImageBitmap(bitmap);
                    capturedImage=true;

                } catch (IOException e) {
                    e.printStackTrace();
                    showToast("Failed!");
                }
            }
        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            potHoleimageView.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            capturedImage = true;
            Log.e(AddPotHoleActivity.class.getName(), "Image Saved!");
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        if (!wallpaperDirectory.exists()) {  // have the object build the directory structure, if needed.
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }
    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {  // check if all permissions are granted
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) { // check for permanent denial of any permission
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }


                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
    void showToast(String msg) {
        Toast.makeText(AddPotHoleActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(AddPotHoleActivity.this, Locale.getDefault());

            currentLocation = new PotHoleLocation();
            currentLocation.setLat(location.getLatitude());
            currentLocation.setLng(location.getLongitude());

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                currentLocation.setAddress(address);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


}
