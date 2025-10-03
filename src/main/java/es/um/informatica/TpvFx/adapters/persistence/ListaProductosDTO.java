package es.um.informatica.TpvFx.adapters.persistence;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="listaProductos")
public class ListaProductosDTO {
	private List<ProductoDTO> productos = new ArrayList<>();

	@XmlElement(name = "producto")
	public List<ProductoDTO> getProductos() {
		return productos;
	}

	public void setProductos(List<ProductoDTO> productos) {
		this.productos = productos;
	}
}
