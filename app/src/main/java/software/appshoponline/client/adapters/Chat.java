package software.appshoponline.client.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Chat {
    public int Sala_id;
    public int Usuario_id;
    public int Usuario_Empresa_id;
    public int Empresa_id;
    public String ImagenUrl;
    public String NombreChat;
    public String MensajeCorto;
    public String HoraUltimoMensaje;

    public Chat(int sala_id, int usuario_id, String imgUrl, String nombreChat, String mensajeCorto, String horaUltimoChat){
        this.Sala_id = sala_id;
        this.Usuario_id = usuario_id;
        this.ImagenUrl = imgUrl;
        this.NombreChat = nombreChat;
        this.MensajeCorto = mensajeCorto;
        this.HoraUltimoMensaje = horaUltimoChat;
    }

    public Chat(int sala_id, int usuario_id, int usuario_empresa_id, int empresa_id, String imgUrl, String nombreChat, String mensajeCorto, String horaUltimoChat){
        this.Sala_id = sala_id;
        this.Usuario_id = usuario_id;
        this.Usuario_Empresa_id = usuario_empresa_id;
        this.Empresa_id = empresa_id;
        this.ImagenUrl = imgUrl;
        this.NombreChat = nombreChat;
        this.MensajeCorto = mensajeCorto;
        this.HoraUltimoMensaje = horaUltimoChat;
    }
}
