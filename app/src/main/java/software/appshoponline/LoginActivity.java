package software.appshoponline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import software.appshoponline.business.HomeBusiActivity;

public class LoginActivity extends AppCompatActivity
        implements Response.Listener<JSONObject>, Response.ErrorListener{

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private CheckBox checkSesion;
    private EditText txtCorreo;
    private EditText txtContrasena;

    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.txtCorreo = findViewById(R.id.actlg_txtCorreo);
        this.txtContrasena = findViewById(R.id.actlg_txtContrasena);
        this.checkSesion = findViewById(R.id.actlg_checkSesion);

        pref = getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void onClick_btnIniciarSesion(View view){
        String url = Dominio.URL_WebServie + "/iniciar-sesion/" + txtCorreo.getText().toString() + "/" + txtContrasena.getText().toString();
        this.requestQueueGetVolley(url);
    }

    public void onClick_btnRegistrar(View view){
        Intent intent = new Intent(getApplicationContext(), RegisterContainer.class);
        startActivity(intent);
        finish();
    }

    private void requestQueueGetVolley(String url){
        this.requestQueue = Volley.newRequestQueue(getApplicationContext());
        this.jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        this.requestQueue.add(this.jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if (response.getBoolean("acceso")){
                if(checkSesion.isChecked()){
                    editor.putBoolean("sesion", true);
                }
                editor.putInt("usuario_id", response.getInt("usuario"));
                Intent intent;
                if (response.getInt("rol") == 1){
                    intent = new Intent(getApplicationContext(), HomeActivity.class);
                }
                else{
                    editor.putInt("empresa_id", response.getInt("empresa"));
                    intent = new Intent(getApplicationContext(), HomeBusiActivity.class);
                }
                editor.apply();
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error: Hubo un error al manejar el JSONObject de Volley");
        }
    }
}