package software.appshoponline.client.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import software.appshoponline.R;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.ViewHolder>{
    private ArrayList<Mensaje> ListaMensajes;
    private Context context;
    private RequestQueue requestQueue;
    private int Usuario_destino;

    public MensajeAdapter(ArrayList<Mensaje> listaMensajes, Context context, int usuario_destino){
        this.ListaMensajes = listaMensajes;
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        this.Usuario_destino = usuario_destino;
    }

    @NonNull
    @Override
    public MensajeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeAdapter.ViewHolder holder, int position) {
        holder.asignarInformacion(this.ListaMensajes.get(position));
    }

    @Override
    public int getItemCount() {
        return this.ListaMensajes.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        //Atributos de cada layout mensaje
        RelativeLayout msj_layout;
        TextView msj_txtMensaje;
        TextView msj_txtHora;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            msj_layout = itemView.findViewById(R.id.msj_layout);
            msj_txtMensaje = itemView.findViewById(R.id.msj_txtMensaje);
            msj_txtHora = itemView.findViewById(R.id.msj_txtHora);
        }

        public void asignarInformacion(Mensaje mensaje){
            msj_txtMensaje.setText(mensaje.Texto);
            msj_txtHora.setText(mensaje.Hora);

            if (mensaje.Usuario == Usuario_destino){
                msj_layout.setGravity(LinearLayout.LAYOUT_DIRECTION_LTR);
            }
        }
    }
}
