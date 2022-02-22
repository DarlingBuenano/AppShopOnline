package software.appshoponline.client.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import software.appshoponline.R;

public class EmpresaAdapter extends RecyclerView.Adapter<EmpresaAdapter.ViewHolder> {

    private ArrayList<Empresa> listaEmpresas;
    private SharedPreferences pref;
    private int usuario;
    private RecyclerView recycler;
    private RequestQueue requestQueue;
    private Context context;

    public EmpresaAdapter(ArrayList<Empresa> listaEmpresas, Context context){
        this.listaEmpresas = listaEmpresas;
        this.context = context;
        this.pref = context.getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        this.usuario = pref.getInt("usuario_id", 1);
        requestQueue = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_compra_x_empresa, null, false);

        recycler = view.findViewById(R.id.recyclerListaProductosXComprar);
        recycler.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        return new ViewHolder(view);
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
        TextView txtTotal;
        Button btnComprarAhora;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreEmpresa = itemView.findViewById(R.id.txtNombreEmpresa);
            txtCostoDeEnvio = itemView.findViewById(R.id.txtCostoDeEnvio);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            btnComprarAhora = itemView.findViewById(R.id.btnComprarAhora);
        }

        public void asignarInformacion(Empresa empresa){
            txtNombreEmpresa.setText(empresa.Nombre);
            txtCostoDeEnvio.setText(empresa.Costo_envio.toString());
            txtTotal.setText(empresa.Costo_envio.toString());

            //Asignar el otro recyclerView
            JSONArray jsonListaProductos = empresa.ProductosJson;
            ArrayList<Product> listaProductos = new ArrayList<Product>();
            for(int j = 0; j < jsonListaProductos.length(); j++){
                JSONObject producto = null;
                try {
                    producto = jsonListaProductos.getJSONObject(j);
                    listaProductos.add( new Product(
                            producto.getInt("id"),
                            producto.getString("url_imagen"),
                            producto.getString("nombre"),
                            producto.getDouble("precio"),
                            producto.getString("unidad_medida")
                    ));
                    ProductXEmpresaAdapter productXEmpresaAdapter = new ProductXEmpresaAdapter(listaProductos, context);
                    recycler.setAdapter(productXEmpresaAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("Error al mostrar los productos dentro de cada empresa: " + e.getMessage());
                }
            }
        }
    }
}
