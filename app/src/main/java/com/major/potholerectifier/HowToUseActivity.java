package com.major.potholerectifier;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.major.potholerectifier.utils.AppConstants;

public class HowToUseActivity extends AppCompatActivity {

    TextView howToUseText ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_use);
        int req = getIntent().getIntExtra(AppConstants.REQ_STRING,-1);
        howToUseText = (TextView)findViewById(R.id.how_to_use_text);

        if(req == AppConstants.PUBLIC_LOGIN){

            howToUseText.setText("- Signup/ login (using phone number) (during signup OTP should sent for verification)\n\n\n" +
                    "- After entering they should have following possible tabs\n" +
                    "   - A form to fill up to report about a pothole problem(What exactly should be there in the form is explained below)\n" +
                    "   - Status of the users's previously reported potholes\n\n\n" +
                    "   - How to use the app\n" +
                    "- Form contents:\n" +
                    "   - first an option to capture the picture of pothole\n" +
                    "   - select/capture the current location using google maps\n" +
                    "   - (Optional) Can give landmark details (There can be a word limit)\n" +
                    "   - select under which area does the location come under\n" +
                    "   - Submit the form\n");

        }else if(req == AppConstants.GOVT_LOGIN){

            howToUseText.setText("- Login (credentially will be given to them manually)\n" +
                    "- In their navigation bar, they should have two options\n" +
                    "  - See'pothole problem reports'(What exactly should be there in the form is explained below) \n" +
                    "  - How to use the app\n\n\n" +
                    "- In the tab 'pothole problem reports'\n" +
                    "  - there should two tabs (side by side, like in whatsapp)\n" +
                    "    - Open pothole problem reports (pothole problems which havent been addressed yet) and Inprogress pothole problem reports\n" +
                    "    - Completed pothole problem reports\n\n\n" +
                    "  - Each tab should have a feature to filter by location\n\n\n" +
                    "  - In all the three user should able to click to open it with full details (photo, exact location, reported time) and button (Mark it as completed)\n" +
                    "    - When clicked on mark it as completed, they should be redirected to a form, where they have to upload a picture of road after filling the pothole and submit it, to mark it as completed \n");

        }else{
            howToUseText.setText("No content to show");
        }

    }
}
