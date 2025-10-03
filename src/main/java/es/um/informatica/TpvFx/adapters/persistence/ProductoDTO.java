package es.um.informatica.TpvFx.adapters.persistence;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "producto")
public class ProductoDTO implements Serializable {
    private String codigo;
    private String descripcion;
    private int cantidad;
    private float precio;

    // Constructor vacío obligatorio para JAXB
    public ProductoDTO() {}

    public ProductoDTO(String codigo, String descripcion, int cantidad, float precio) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    @XmlElement
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    @XmlElement
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @XmlElement
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    @XmlElement
    public float getPrecio() { return precio; }
    public void setPrecio(float precio) { this.precio = precio; }
}
