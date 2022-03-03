package software.appshoponline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import software.appshoponline.R;
import software.appshoponline.client.adapters.Chat;
import software.appshoponline.client.adapters.ChatAdapter;
import software.appshoponline.fragments.MensajesFragment;

public class MensajeActivity extends AppCompatActivity {

    FragmentTransaction transaction;
    Fragment frgMensajes;
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;

    int usuario_id;
    int usuario_empresa_id;
    int empresa_id;
    int sala_id;
    String img_url;
    String nombre_chat;
    String pedido;
    Bundle datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje);

        requestQueue = Volley.newRequestQueue(this);
        frgMensajes = new MensajesFragment();

        //consultar datos de sesion de usuario
        //SharedPreferences pref;
        //pref = getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        //usuario_id = pref.getInt("usuario_id", 1);

        //Obtener los datos
        datos = getIntent().getExtras();
        usuario_id = datos.getInt("usuario_id");
        usuario_empresa_id = datos.getInt("usuario_empresa_id");
        empresa_id = datos.getInt("empresa_id");
        nombre_chat = datos.getString("nombre_chat");
        img_url = datos.getString("img_url");
        pedido = datos.getString("pedido");
    }

    @Override
    protected void onStart() {
        super.onStart();
        consultar_o_Registrar_Sala();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void consultar_o_Registrar_Sala(){
        String url = Dominio.URL_WebServie + Constantes.URL_Consultar_o_Registrar_Sala + "/"+usuario_id + "/"+empresa_id;
        requestQueueGetVolley(url);
    }

    private void requestQueueGetVolley(String url){
        this.requestQueue = Volley.newRequestQueue(this);
        this.jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, responseErrorListener);
        this.requestQueue.add(this.jsonObjectRequest);
    }

    private Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                if (response.getBoolean("accion")){
                    sala_id = response.getInt("id");
                    irAlFragmentMensajes();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Response.ErrorListener responseErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    };

    protected void irAlFragmentMensajes() {
        Bundle args = new Bundle();
        args.putInt("usuario_id", usuario_id);
        args.putInt("empresa_id", empresa_id);
        args.putInt("usuario_empresa_id", usuario_empresa_id);
        args.putString("nombre_chat", nombre_chat);
        args.putString("img_url", img_url);
        args.putString("pedido", pedido);
        args.putInt("sala_id", sala_id);
        frgMensajes.setArguments(args);

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frg_container_mensajes, frgMensajes);
        transaction.commit();
    }
}