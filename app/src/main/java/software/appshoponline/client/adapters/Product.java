package software.appshoponline.client.adapters;

public class Product {
    public int Id;
    public String UrlImagen;
    public String Nombre;
    public String Empresa;
    public double Precio;
    public String Unidad_Medida;
    public int Categoria;
    public boolean Like;
    public int Cantidad;

    public Product(){
        this.UrlImagen = "https://1.bp.blogspot.com/-lijDS2dEcyE/TqwUKcsEEaI/AAAAAAAAAE8/8t9rin0ZinE/s1600/coladamorada.jpeg";
        this.Nombre = "Primer producto";
        this.Empresa = "MiniEmprendedor Manuel";
        this.Precio = 1.50;
        this.Unidad_Medida = "unidad";
        this.Like = false;
    }

    public Product(int id, String url, String nombre, String empresa, double precio, String unidad_Medida, int categoria, boolean like){
        this.Id = id;
        this.UrlImagen = url;
        this.Nombre = nombre;
        this.Empresa = empresa;
        this.Precio = precio;
        this.Unidad_Medida = unidad_Medida;
        this.Categoria = categoria;
        this.Like = like;
    }

    public Product(int id, String url, String nombre, double precio, String unidad_Medida, int cantidad){
        this.Id = id;
        this.UrlImagen = url;
        this.Nombre = nombre;
        this.Precio = precio;
        this.Unidad_Medida = unidad_Medida;
        this.Categoria = cantidad;
    }
}
