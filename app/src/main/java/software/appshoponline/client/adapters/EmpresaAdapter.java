package software.appshoponline.client.adapters;

import android.content.Context;
import android.content.Intent;
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
import software.appshoponline.MensajeActivity;
import software.appshoponline.R;

public class EmpresaAdapter extends RecyclerView.Adapter<EmpresaAdapter.ViewHolder> {

    private final ArrayList<Empresa> listaEmpresas;
    private final int usuario_id;
    private RecyclerView recycler;

    private final Context context;
    private View root;
    private final FragmentManager fragmentManager;
    private final DecimalFormat moneda;
    private final LifecycleOwner lifecycle;

    private final RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;

    public EmpresaAdapter(ArrayList<Empresa> listaEmpresas, Context context, FragmentManager fragmentManager, LifecycleOwner lifecycle, int usuario_id){
        this.listaEmpresas = listaEmpresas;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.lifecycle = lifecycle;
        this.usuario_id = usuario_id;
        this.moneda = new DecimalFormat("#0.00");
        this.requestQueue = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_compra_x_empresa, parent, false);

        recycler = root.findViewById(R.id.recyclerListaProductosXComprar);
        recycler.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));

        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.asignarInformacion(this.listaEmpresas.get(position));
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
        int venta_id = 0;
        Empresa empresa;
        double precio_productos = 0;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreEmpresa = itemView.findViewById(R.id.txtNombreEmpresa);
            txtCostoDeEnvio = itemView.findViewById(R.id.txtCostoDeEnvio);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            btnComprarAhora = itemView.findViewById(R.id.btnComprarAhora);
            btnComprarAhora.setOnClickListener(clicBtnComprarAhora);
        }

        public void asignarInformacion(Empresa empresa){
            this.empresa = empresa;
            txtNombreEmpresa.setText(empresa.Nombre);
            txtCostoDeEnvio.setText("Costo de envío: $ " + moneda.format(empresa.Costo_envio));

            //Asignar el otro recyclerView
            llenarRecyclerProductos();

            total = precio_productos + empresa.Costo_envio;
            txtTotal.setText("Total $ " + moneda.format(total));

            //Listener para escuchar los cambios en las cantidades*precio del producto
            fragmentManager.setFragmentResultListener(empresa.Nombre, lifecycle, new FragmentResultListener() {
                @Override
                public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                    if (requestKey.equals(empresa.Nombre)){
                        if (empresa.Productos.size() == 0){
                            listaEmpresas.remove(empresa);
                            notifyDataSetChanged();
                        }
                        else {
                            total = total + result.getDouble(requestKey);
                            txtTotal.setText("Total $ " + moneda.format(total));
                        }
                    }
                }
            });
        }

        private void llenarRecyclerProductos(){
            try{
                JSONArray jsonListaProductos = empresa.ProductosJson;
                empresa.Productos = new ArrayList<Product>();
                for(int j = 0; j < jsonListaProductos.length(); j++){
                    JSONObject producto = jsonListaProductos.getJSONObject(j);
                    empresa.Productos.add( new Product(
                            producto.getInt("id"),
                            producto.getString("url_imagen"),
                            producto.getString("nombre"),
                            producto.getDouble("precio"),
                            producto.getString("unidad_medida"),
                            1
                    ));
                    precio_productos = precio_productos + producto.getDouble("precio");
                }
                ProductXEmpresaAdapter productXEmpresaAdapter = new ProductXEmpresaAdapter(empresa.Productos, context, fragmentManager, empresa.Nombre);
                recycler.setAdapter(productXEmpresaAdapter);
            }catch (JSONException e) {
                e.printStackTrace();
                System.out.println("Error al mostrar los productos dentro de cada empresa: " + e.getMessage());
            }
        }

        private View.OnClickListener clicBtnComprarAhora = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = Dominio.URL_WebServie +
                        Constantes.URL_Registrar_Venta_de_Productos +
                        "/"+usuario_id +"/"+empresa.Id +"/"+moneda.format(total).replace(',', '.');
                requestQueueRegistrarVenta(url);
            }
        };

        private void requestQueueRegistrarVenta(String url){
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, onListenerRegistrarVenta, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error al registrar la venta: " + error.getMessage());
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
        private Response.Listener<JSONObject> onListenerRegistrarVenta = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    venta_id = response.getInt("registro");
                    String pedido = "Mi pedido es:\n";

                    for (int i = 0; i < empresa.Productos.size(); i++){
                        Product producto = empresa.Productos.get(i);
                        pedido = pedido + " * "+producto.Cantidad+ " "+producto.Nombre+ ", $"+producto.Precio+ "\n";
                        String url2 = Dominio.URL_WebServie +
                                Constantes.URL_Registrar_Detalle_de_Venta +
                                "/"+usuario_id +"/"+venta_id +"/"+producto.Id +"/"+producto.Cantidad;
                        requestQueueRegistrarDetalleVenta(url2);
                    }
                    pedido = pedido + "Costo de envío: $" + empresa.Costo_envio + "\n";
                    pedido = pedido + "Total: $" + total;

                    Intent intent = new Intent(context, MensajeActivity.class);
                    intent.putExtra("pedido", pedido);
                    intent.putExtra("usuario_id", usuario_id);
                    intent.putExtra("usuario_empresa_id", empresa.Usuario_Empresa_Id);
                    intent.putExtra("img_url", empresa.ImagenUrl);
                    intent.putExtra("empresa_id", empresa.Id);
                    intent.putExtra("nombre_chat", empresa.Nombre);
                    context.startActivity(intent);

                    listaEmpresas.remove(empresa);
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        private void requestQueueRegistrarDetalleVenta(String url){
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error al registrar el detalle de venta: " + error.getMessage());
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
    }
}
