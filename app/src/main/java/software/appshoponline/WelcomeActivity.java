package software.appshoponline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            Thread.sleep(5000);
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);

        } catch (InterruptedException e) {
            System.out.println("Ocurrió un error al detener el hilo por 3 seg");
        }
    }
}