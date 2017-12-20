package myapp.com.groceryshopmerchant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SubmitOTPActivity extends AppCompatActivity {
    Button SubmitOTP;
    EditText edtotp;
    String OTP, FROM;
    String EdtOTP;
    public static String verifyflag = "false";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_otp);

        edtotp = (EditText) findViewById(R.id.edtOTP);
        SubmitOTP = (Button) findViewById(R.id.btnSubmitOTP);
        Bundle extra = getIntent().getExtras();
        OTP = extra.getString("OTP");
      //  FROM = extra.getString("FROM");
        SubmitOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EdtOTP = edtotp.getText().toString();

                if (OTP.equals(EdtOTP)) {

                        Intent i = new Intent(SubmitOTPActivity.this, ChangePasswordActivity.class);
                        startActivity(i);

                } else {
                    edtotp.setError("Enter Correct OTP");
                }
            }
        });
    }
}

