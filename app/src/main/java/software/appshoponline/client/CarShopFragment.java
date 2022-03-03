package software.appshoponline.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import software.appshoponline.client.adapters.Empresa;
import software.appshoponline.client.adapters.EmpresaAdapter;
import software.appshoponline.client.adapters.Product;

public class CarShopFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private int usuario_id;
    private SharedPreferences pref;
    private String url;

    private ArrayList<Empresa> ListaEmpresas;
    private RecyclerView recycler;
    private View root;

    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;

    public CarShopFragment() {
        // Required empty public constructor
    }

    public static CarShopFragment newInstance(String param1, String param2) {
        CarShopFragment fragment = new CarShopFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_car_shop, container, false);

        recycler = root.findViewById(R.id.recyclerListaEmpresas);
        recycler.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        pref = getContext().getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        usuario_id = pref.getInt("usuario_id", 1);

        url = Dominio.URL_WebServie + Constantes.URL_Mostrar_Productos_del_Carrito + "/"+usuario_id;
        requestQueueGetVolley(url);
    }

    private void requestQueueGetVolley(String url){
        this.requestQueue = Volley.newRequestQueue(getContext());
        this.jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        this.requestQueue.add(this.jsonObjectRequest);
    }

    @Override
    public void onResponse(JSONObject response) {
        try{
            JSONArray jsonListaEmpresas = response.getJSONArray("empresas");
            this.ListaEmpresas = new ArrayList<Empresa>();

            for(int i = 0; i < jsonListaEmpresas.length(); i++){
                JSONObject empresa = jsonListaEmpresas.getJSONObject(i);

                ListaEmpresas.add(new Empresa(
                        empresa.getInt("id"),
                        empresa.getInt("usuario_empresa_id"),
                        empresa.getString("img_url"),
                        empresa.getString("nombre"),
                        empresa.getDouble("costo_envio"),
                        empresa.getJSONArray("productos")
                ));
            }
            EmpresaAdapter empresaAdapter = new EmpresaAdapter(ListaEmpresas, getContext(), getParentFragmentManager(), this.getViewLifecycleOwner(), usuario_id);
            this.recycler.setAdapter(empresaAdapter);
        }
        catch (JSONException e){
            System.out.println("Error al procesar los productos por comprar: " + e.getMessage());
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(root.getContext(), "No se ha podido cargar los datos", Toast.LENGTH_SHORT).show();
        System.out.println("**********************");
        System.out.println("Error: " + error);
        System.out.println("**********************");
    }
}