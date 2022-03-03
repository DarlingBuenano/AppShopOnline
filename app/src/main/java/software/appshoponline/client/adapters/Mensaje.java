package software.appshoponline.client.adapters;

public class Mensaje {
    public int Usuario;
    public String Texto;
    public String Fecha;
    public String Hora;

    public Mensaje(int usuario, String texto, String fecha, String hora){
        this.Usuario = usuario;
        this.Texto = texto;
        this.Fecha = fecha;
        this.Hora = hora;
    }

    public Mensaje(int usuario, String texto, String hora){
        this.Usuario = usuario;
        this.Texto = texto;
        this.Hora = hora;
    }
}
