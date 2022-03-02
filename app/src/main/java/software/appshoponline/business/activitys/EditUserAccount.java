package software.appshoponline.business.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import software.appshoponline.Constantes;
import software.appshoponline.Dominio;
import software.appshoponline.R;

public class EditUserAccount extends AppCompatActivity {

    int usuario_id;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private SharedPreferences pref;

    TextView txtNombresUsuario;
    TextView txtApellidosUsuario;
    TextView txtCedula;
    TextView txtCiudad;
    TextView txtDireccion;
    TextView txtCelular;
    TextView txtCorreo;
    TextView txtContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_account);

        txtNombresUsuario = findViewById(R.id.actConfUser_txtNombres);
        txtApellidosUsuario = findViewById(R.id.actConfUser_txtApellidos);
        txtCedula = findViewById(R.id.actConfUser_txtCedula);
        txtCiudad = findViewById(R.id.actConfUser_txtCiudad);
        txtDireccion = findViewById(R.id.actConfUser_txtDireccion);
        txtCelular = findViewById(R.id.actConfUser_txtCelular);
        txtCorreo = findViewById(R.id.actConfUser_txtCorreo);
        txtContrasena = findViewById(R.id.actConfUser_txtContrasena);

        this.requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        cargarDatosDeUsuario();
    }

    private void cargarDatosDeUsuario(){
        pref = getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        usuario_id = pref.getInt("usuario_id", 1);

        String url = Dominio.URL_WebServie + Constantes.URL_Obtener_Datos_De_Usuario + "/" + usuario_id;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, responseErrorListener);
        requestQueue.add(jsonObjectRequest);
    }

    private void mostrarDatosEnLosInputs(JSONObject json){
        try{
            txtNombresUsuario.setText(json.getString("nombres"));
            txtApellidosUsuario.setText(json.getString("apellidos"));
            txtCedula.setText(json.getString("cedula"));
            txtCiudad.setText(json.getString("ciudad"));
            txtDireccion.setText(json.getString("direccion"));
            txtCelular.setText(json.getString("celular"));
            txtCorreo.setText(json.getString("correo_electronico"));
            txtContrasena.setText(json.getString("contrasena"));
        }
        catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error al leer los datos", Toast.LENGTH_SHORT).show();
        }
    }

    public void clicBtnActualizarDatosusuario(View view){
        HashMap<String, String> parametrosPost = new HashMap<>();
        parametrosPost.put("id", String.valueOf(usuario_id));
        parametrosPost.put("nombres", txtNombresUsuario.getText().toString());
        parametrosPost.put("apellidos", txtApellidosUsuario.getText().toString());
        parametrosPost.put("cedula", txtCedula.getText().toString());
        parametrosPost.put("ciudad", txtCiudad.getText().toString());
        parametrosPost.put("direccion", txtDireccion.getText().toString());
        parametrosPost.put("celular", txtCelular.getText().toString());
        parametrosPost.put("correo", txtCorreo.getText().toString());
        parametrosPost.put("contrasena", txtContrasena.getText().toString());

        String url = Dominio.URL_WebServie + Constantes.URL_Actualizar_Datos_Usuario;
        JSONObject jsonObject = new JSONObject(parametrosPost);

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, responseListener, responseErrorListener);
        requestQueue.add(jsonObjectRequest);
    }

    public void clicBtnCancelarActualizacion(View view){
        finish();
    }

    private Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                if (response.getBoolean("accion")){
                    if (response.getString("metodo").equals("actualizar")){
                        Toast.makeText(getApplicationContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        mostrarDatosEnLosInputs(response.getJSONObject("datos"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Hubo un error al actualizar los datos", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private Response.ErrorListener responseErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), "Ocurrió un error en el servidor", Toast.LENGTH_SHORT).show();
        }
    };
}