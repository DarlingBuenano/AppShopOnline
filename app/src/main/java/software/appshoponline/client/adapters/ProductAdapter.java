package software.appshoponline.client.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import software.appshoponline.R;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    ArrayList<Product> ListaProductos;

    public ProductAdapter(ArrayList<Product> listaProductos){
        this.ListaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.asignarInformacion(ListaProductos.get(position));
    }

    @Override
    public int getItemCount() {
        return ListaProductos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        TextView txtEmpresa;
        TextView txtPrecio_Unidad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreProducto);
            txtEmpresa = itemView.findViewById(R.id.txtEmpresaProducto);
            txtPrecio_Unidad = itemView.findViewById(R.id.txtPrecioxUnidadProducto);
        }

        public void asignarInformacion(Product producto){
            txtNombre.setText(producto.Nombre);
            txtEmpresa.setText(producto.Empresa);
            String precio = String.valueOf(producto.Precio);
            txtPrecio_Unidad.setText("$ " + precio + " / " + producto.Unidad_Medida);
        }
    }
}
