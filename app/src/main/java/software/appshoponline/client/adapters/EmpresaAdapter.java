package software.appshoponline.client.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import software.appshoponline.Constantes;
import software.appshoponline.Dominio;
import software.appshoponline.R;

public class EmpresaAdapter extends RecyclerView.Adapter<EmpresaAdapter.ViewHolder> {

    private ArrayList<Empresa> listaEmpresas;
    private SharedPreferences pref;
    private int usuario;
    private RecyclerView recycler;

    private Context context;
    private View root;
    private FragmentManager fragmentManager;
    private DecimalFormat moneda;
    private LifecycleOwner lifecycle;

    public EmpresaAdapter(ArrayList<Empresa> listaEmpresas, Context context, FragmentManager fragmentManager, LifecycleOwner lifecycle){
        this.listaEmpresas = listaEmpresas;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.lifecycle = lifecycle;
        this.pref = context.getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        this.usuario = pref.getInt("usuario_id", 1);
        this.moneda = new DecimalFormat("#0.00");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_compra_x_empresa, null, false);

        recycler = root.findViewById(R.id.recyclerListaProductosXComprar);
        recycler.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));

        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.asignarInformacion(listaEmpresas.get(position));
    }

    @Override
    public int getItemCount() {
        return this.listaEmpresas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreEmpresa;
        TextView txtCostoDeEnvio;
        Button btnComprarAhora;
        TextView txtTotal;
        double total = 0;
        AlertDialog.Builder builderDialog;
        RequestQueue requestQueue;
        JsonObjectRequest jsonObjectRequest;
        ArrayList<Product> listaProductos;
        int venta_id = 0;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreEmpresa = itemView.findViewById(R.id.txtNombreEmpresa);
            txtCostoDeEnvio = itemView.findViewById(R.id.txtCostoDeEnvio);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            btnComprarAhora = itemView.findViewById(R.id.btnComprarAhora);
            builderDialog = new AlertDialog.Builder(context);
            requestQueue = Volley.newRequestQueue(context);
        }

        public void asignarInformacion(Empresa empresa){
            txtNombreEmpresa.setText(empresa.Nombre);
            txtCostoDeEnvio.setText("Costo de envío: $ " + moneda.format(empresa.Costo_envio));
            double precio_productos = 0;

            //Asignar el otro recyclerView
            JSONArray jsonListaProductos = empresa.ProductosJson;
            listaProductos = new ArrayList<Product>();
            for(int j = 0; j < jsonListaProductos.length(); j++){
                try {
                    JSONObject producto = jsonListaProductos.getJSONObject(j);
                    listaProductos.add( new Product(
                            producto.getInt("id"),
                            producto.getString("url_imagen"),
                            producto.getString("nombre"),
                            producto.getDouble("precio"),
                            producto.getString("unidad_medida"),
                            1
                    ));
                    precio_productos = precio_productos + producto.getDouble("precio");
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("Error al mostrar los productos dentro de cada empresa: " + e.getMessage());
                }
            }
            ProductXEmpresaAdapter productXEmpresaAdapter = new ProductXEmpresaAdapter(listaProductos, context, fragmentManager, empresa.Nombre);
            recycler.setAdapter(productXEmpresaAdapter);

            total = precio_productos + empresa.Costo_envio;
            txtTotal.setText("Total $ " + moneda.format(total));

            //Listener para escuchar los cambios en las cantidades*precio del producto
            fragmentManager.setFragmentResultListener(empresa.Nombre, lifecycle, new FragmentResultListener() {
                @Override
                public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                    total = total + result.getDouble(requestKey);
                    txtTotal.setText("Total $ " + moneda.format(total));
                }
            });

            btnComprarAhora.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = Dominio.URL_WebServie + Constantes.URL_Registrar_Venta_de_Productos + "/"+usuario +"/"+empresa.Id +"/"+moneda.format(total).replace(',', '.');
                    requestQueueRegistrarVenta(url);

                    builderDialog.setTitle(R.string.title_comprar)
                            .setMessage(R.string.message_comprar)
                            .setPositiveButton(R.string.alert_aceptar, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    listaEmpresas.remove(empresa);
                                    notifyDataSetChanged();
                                }
                            });
                    builderDialog.create().show();
                }
            });
        }

        private void requestQueueRegistrarVenta(String url){
            this.jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        venta_id = response.getInt("registro");
                        for (Product item:listaProductos) {
                            String url = Dominio.URL_WebServie + Constantes.URL_Registrar_Detalle_de_Venta + "/"+usuario +"/"+venta_id +"/"+item.Id +"/"+item.Cantidad;
                            requestQueueRegistrarDetalleVenta(url);
                        }
                        System.out.println("Venta id:" + response.getInt("registro"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error al registrar la venta: " + error.getMessage());
                }
            });
            this.requestQueue.add(this.jsonObjectRequest);
        }

        private void requestQueueRegistrarDetalleVenta(String url){
            this.jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        System.out.println("Detalle id:" + response.getInt("registro"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error al registrar el detalle de venta: " + error.getMessage());
                }
            });
            this.requestQueue.add(this.jsonObjectRequest);
        }
    }
}
