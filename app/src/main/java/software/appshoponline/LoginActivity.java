package software.appshoponline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText txtCorreo = findViewById(R.id.actlg_txtCorreo);
        EditText txtContrasena = findViewById(R.id.actlg_txtContrasena);

        System.out.println("Correo: " + txtCorreo.getText() + "; Contraseña: " + txtContrasena.getText());
    }

    public void onClick_btnIniciarSesion(View view){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    public void onClick_btnRegistrar(View view){
        Toast.makeText(getApplicationContext(), "Acividad de registro no implementada", Toast.LENGTH_SHORT).show();
    }
}