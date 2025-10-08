package es.um.informatica.TpvFx.adapters.repository;

import java.util.List;
import java.util.stream.Collectors;

import es.um.informatica.TpvFx.Constantes;
import es.um.informatica.TpvFx.adapters.persistence.ProductoMapper;
import es.um.informatica.TpvFx.adapters.persistence.ProductoPersistenceApapter;
import es.um.informatica.TpvFx.application.port.ProductoRepository;
import es.um.informatica.TpvFx.model.Producto;

public class ProductoRepositoryImpl implements ProductoRepository{

	private static ProductoRepositoryImpl productoRepository;

	private List<Producto> productos;

	public static ProductoRepositoryImpl getInscante() {
		if (productoRepository == null) {
			productoRepository = new ProductoRepositoryImpl();
			productoRepository.cargaProductos();
		}
		return productoRepository;
	}

	private ProductoRepositoryImpl() {
	}
	
	public void aniadirProducto(Producto producto) {
		productos.add(producto);
	}
	
	public void eliminarProducto(Producto producto) {
		productos.remove(producto);
	}

	public void actualizaAlmacenConVenta(List<Producto> productosComprados) {
		for (Producto productoComprado : productosComprados) {
			Producto productoEnAlmacen = productos.stream().filter(prodAlmacen -> prodAlmacen.equals(productoComprado))
					.findFirst().orElse(null);
			if(productoEnAlmacen!=null) {
				productoEnAlmacen.setCantidad(productoEnAlmacen.getCantidad() - productoComprado.getCantidad());				
				//Si no queda stock del producto, lo elimino del almacen
				if(productoEnAlmacen.getCantidad()<=0) {
					eliminarProducto(productoEnAlmacen);
				}
			}
		}
	}

	public void cargaProductos() {
		try {
			this.productos = ProductoPersistenceApapter.getInscante().cargarProductos(Constantes.RUTA_FICHERO)
					.getProductos().stream().map(producto -> ProductoMapper.fromProuctoDTO(producto))
					.collect(Collectors.toList());
		} catch (Exception e) {
			// TODO Falta log
			e.printStackTrace();
		}
	}

	public List<Producto> getProductos() {
		return productos;
	}

	public void setProductos(List<Producto> productos) {
		this.productos = productos;
	}

}
