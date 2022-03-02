package software.appshoponline.business.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import software.appshoponline.Constantes;
import software.appshoponline.Dominio;
import software.appshoponline.R;
import software.appshoponline.Utilities;
import software.appshoponline.client.adapters.Product;

public class ProductBusiAdapter extends RecyclerView.Adapter<ProductBusiAdapter.ViewHolder> {

    private ArrayList<Product> ListaProductosBusi;
    private RequestQueue requestQueue;
    private Context context;

    public ProductBusiAdapter(ArrayList<Product> products, Context context){
        this.ListaProductosBusi = products;
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_busi, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.asignarInformacion(ListaProductosBusi.get(position));
    }

    @Override
    public int getItemCount() {
        return ListaProductosBusi.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtNombreProducto;
        TextView txtPrecioxUnidad;
        ImageView imgProducto;
        Button btnEditar;
        Button btnEliminar;
        JsonObjectRequest jsonObjectRequest;
        DecimalFormat formatoMoneda;
        AlertDialog.Builder builder;

        ImageView dialog_imgProducto;
        Button dialog_btnCambiarFoto;
        EditText dialog_txtNombreProducto;
        EditText dialog_txtPrecio;
        Spinner unidadesMedidas;
        String imagenString;
        Boolean seCambioLaFoto = false;
        Product product;

        String mensaje;
        String accion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreProducto = itemView.findViewById(R.id.txtNombreProductoBusi);
            txtPrecioxUnidad = itemView.findViewById(R.id.txtPrecioxUnidadProductoBusi);
            imgProducto = itemView.findViewById(R.id.imgProductoBusi);
            btnEditar = itemView.findViewById(R.id.btnEditarBusi);
            btnEliminar = itemView.findViewById(R.id.btnEliminarBusi);

            formatoMoneda = new DecimalFormat("#0.00");
        }

        public void asignarInformacion(Product product){
            this.product = product;
            txtNombreProducto.setText(product.Nombre);
            txtPrecioxUnidad.setText("$ " + formatoMoneda.format(product.Precio) + " / " + product.Unidad_Medida);
            btnEditar.setOnClickListener(clicBtnEditar);
            btnEliminar.setOnClickListener(clicBtnEliminar);

            ImageRequest imageRequest = new ImageRequest(product.UrlImagen,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            imgProducto.setImageBitmap(response);
                        }
                    }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(itemView.getContext(), "Error al traer la imagen", Toast.LENGTH_SHORT).show();
                    }
                });
            requestQueue.add(imageRequest);
        }

        private View.OnClickListener clicBtnEditar = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialog_layout = LayoutInflater.from(context).inflate(R.layout.dialog_edit_product, null);

                builder = new AlertDialog.Builder(context);
                builder.setView(dialog_layout);
                builder.setNegativeButton(R.string.dialog_cancelar, null);
                builder.setPositiveButton(R.string.dialog_aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HashMap<String, String> parametrosPost = new HashMap<>();
                        parametrosPost.put("id", String.valueOf(product.Id));
                        parametrosPost.put("nombre", dialog_txtNombreProducto.getText().toString());
                        parametrosPost.put("precio", dialog_txtPrecio.getText().toString());
                        parametrosPost.put("unidad_medida", Constantes.UNIDADES_DE_MDIDA[unidadesMedidas.getSelectedItemPosition()]);
                        if(seCambioLaFoto){
                            parametrosPost.put("imagen", imagenString);
                        }
                        String url = Dominio.URL_WebServie + Constantes.URL_Actualizar_Producto_x_Empresa;
                        JSONObject jsonObject = new JSONObject(parametrosPost);
                        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, responseListener, responseErrorListener);
                        requestQueue.add(jsonObjectRequest);
                        mensaje = "Producto actualizado con éxito";
                        accion = "Editar";
                    }
                });
                builder.create().show();

                dialog_imgProducto = dialog_layout.findViewById(R.id.dialog_imgProducto);
                dialog_imgProducto.setImageDrawable(imgProducto.getDrawable());

                dialog_btnCambiarFoto = dialog_layout.findViewById(R.id.dialog_btnCambiarImagen);
                dialog_btnCambiarFoto.setOnClickListener(clicBtnCambiarFoto);

                dialog_txtNombreProducto = dialog_layout.findViewById(R.id.dialog_txtNombreProducto);
                dialog_txtNombreProducto.setText(product.Nombre);

                dialog_txtPrecio = dialog_layout.findViewById(R.id.dialog_txtPrecio);
                dialog_txtPrecio.setText(String.valueOf(product.Precio));

                unidadesMedidas = dialog_layout.findViewById(R.id.dialog_spinnerUnidadMedida);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        dialog_layout.getContext(), R.array.spinner_unidades_de_medidas, android.R.layout.simple_spinner_dropdown_item);
                unidadesMedidas.setAdapter(adapter);
                unidadesMedidas.setSelection( indexUnidadMedida(product.Unidad_Medida) );
            }
        };

        private View.OnClickListener clicBtnEliminar = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new AlertDialog.Builder(context);
                builder.setMessage("¿Estás seguro de eliminar el producto? Esta acción no se puede revertir");
                builder.setNegativeButton(R.string.dialog_cancelar, null);
                builder.setPositiveButton(R.string.dialog_aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String url = Dominio.URL_WebServie + Constantes.URL_Eliminar_Producto_x_Empresa + "/" + product.Id;
                        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, responseErrorListener);
                        requestQueue.add(jsonObjectRequest);
                        mensaje = "Producto eliminado con éxito";
                        accion = "Eliminar";
                    }
                });
                builder.create().show();
            }
        };

        private int indexUnidadMedida(String unidadMedida){
            for (int i=0; i < Constantes.UNIDADES_DE_MDIDA.length; i++){
                if (Constantes.UNIDADES_DE_MDIDA[i].equals(unidadMedida))
                    return i;
            }
            return 0;
        }

        private View.OnClickListener clicBtnCambiarFoto = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities utilidades = new Utilities();
                utilidades.cargarFotoDesdeGaleria();
                if ( ! utilidades.nombreImagen.isEmpty()){
                    dialog_imgProducto.setImageBitmap(utilidades.imgFotoBitmap);
                    seCambioLaFoto = true;
                    imagenString = utilidades.imgFotoString;
                }
            }
        };

        private Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("accion")){
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
                        seCambioLaFoto = false;
                        if (accion.equals("Eliminar")){
                            ListaProductosBusi.remove(product);
                            notifyDataSetChanged();
                        }
                    }
                    else{
                        Toast.makeText(context, "No se puede borrar el producto, ya tiene registros de compras o está en proceso de compra.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        public Response.ErrorListener responseErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Ocurrió un error al intentar actualizar el producto", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
