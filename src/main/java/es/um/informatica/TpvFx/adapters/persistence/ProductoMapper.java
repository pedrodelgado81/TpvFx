package es.um.informatica.TpvFx.adapters.persistence;

import es.um.informatica.TpvFx.model.Producto;

public class ProductoMapper {

	public static Producto fromProuctoDTO(ProductoDTO productoDTO) {
		return new Producto(productoDTO.getCodigo(), productoDTO.getDescripcion(), productoDTO.getCantidad(),
				productoDTO.getPrecio());
	}

	public static ProductoDTO ofProducto(Producto producto) {
		return new ProductoDTO(producto.getCodigo(), producto.getDescripcion(), producto.getCantidad(),
				producto.getPrecio());
	}

}
