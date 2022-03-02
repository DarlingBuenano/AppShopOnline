package software.appshoponline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import software.appshoponline.business.HomeBusiActivity;

public class WelcomeActivity extends AppCompatActivity {

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        pref = getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Boolean sesion = pref.getBoolean("sesion", false);
                Intent intent;
                if (sesion){
                    if (pref.getInt("empresa_id", 0) == 0)
                        intent = new Intent(getApplicationContext(), HomeActivity.class);
                    else
                        intent = new Intent(getApplicationContext(), HomeBusiActivity.class);
                }
                else{
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}