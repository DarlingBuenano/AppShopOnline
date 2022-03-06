package software.appshoponline.business.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import software.appshoponline.Constantes;
import software.appshoponline.Dominio;
import software.appshoponline.R;
import software.appshoponline.client.adapters.Chat;
import software.appshoponline.client.adapters.ChatAdapter;

public class ChatsBusiFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    int usuario_id;
    int empresa_id;
    SharedPreferences pref;
    View root;
    RecyclerView recyclerChats;
    String url;
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    ArrayList<Chat> listaChats;

    public ChatsBusiFragment() {
        // Required empty public constructor
    }

    public static ChatsBusiFragment newInstance(String param1, String param2) {
        ChatsBusiFragment fragment = new ChatsBusiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        pref = getContext().getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        usuario_id = pref.getInt("usuario_id", 1);
        empresa_id = pref.getInt("empresa_id", 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_chats_busi, container, false);

        recyclerChats = root.findViewById(R.id.recyclerListaChatsBusi);
        recyclerChats.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        url = Dominio.URL_WebServie + Constantes.URL_Mostrar_Chats_x_Empresa + "/" + empresa_id;
        this.requestQueueGetVolley(url);
    }

    private void requestQueueGetVolley(String url){
        this.requestQueue = Volley.newRequestQueue(getContext());
        this.jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, responseErrorListener);
        this.requestQueue.add(this.jsonObjectRequest);
    }

    private Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                if (response.getBoolean("accion")){
                    JSONArray jsonListaChats = response.getJSONArray("datos");
                    listaChats = new ArrayList<Chat>();

                    for(int i = 0; i < jsonListaChats.length(); i++){
                        JSONObject chat = jsonListaChats.getJSONObject(i);
                        listaChats.add(new Chat(
                                chat.getInt("sala_id"),
                                chat.getInt("usuario_empresa_id"),
                                chat.getInt("usuario_id"),
                                chat.getInt("empresa_id"),
                                chat.getString("img_url"),
                                chat.getString("nombre_chat"),
                                chat.getString("mensaje"),
                                chat.getString("hora_ultimo_mensaje")));
                    }
                    ChatAdapter chatAdapter = new ChatAdapter(listaChats, getContext());
                    recyclerChats.setAdapter(chatAdapter);
                }
                else{
                    Toast.makeText(getContext(), "No tienes chats disponibles por el momento", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("Error al procesar los productos: " + e.getMessage());
            }
        }
    };

    private Response.ErrorListener responseErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    };
}