package software.appshoponline.client.adapters;

import org.json.JSONArray;

import java.util.ArrayList;

public class Empresa {
    public int Id;
    public int Usuario_Empresa_Id;
    public String ImagenUrl;
    public String Nombre;
    public Double Costo_envio;
    public ArrayList<Product> Productos;
    public JSONArray ProductosJson;

    public Empresa(int id, String nombre, Double costo_envio, ArrayList<Product> productos){
        this.Id = id;
        this.Nombre = nombre;
        this.Costo_envio = costo_envio;
        this.Productos = productos;
    }

    public Empresa(int id, int usuario_empresa_id, String imagenUrl, String nombre, Double costo_envio, JSONArray productos){
        this.Id = id;
        this.Usuario_Empresa_Id = usuario_empresa_id;
        this.ImagenUrl = imagenUrl;
        this.Nombre = nombre;
        this.Costo_envio = costo_envio;
        this.ProductosJson = productos;
    }
}
