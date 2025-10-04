package es.um.informatica.TpvFx.adapters.repository;

import java.util.List;
import java.util.stream.Collectors;

import es.um.informatica.TpvFx.Constantes;
import es.um.informatica.TpvFx.adapters.persistence.ProductoMapper;
import es.um.informatica.TpvFx.adapters.persistence.ProductoPersistenceApapter;
import es.um.informatica.TpvFx.model.Producto;

public class ProductoRepository {

	private static ProductoRepository productoRepository;

	private List<Producto> productos;

	public static ProductoRepository getInscante() {
		if (productoRepository == null) {
			productoRepository = new ProductoRepository();			
		}
		return productoRepository;
	}

	private ProductoRepository() {
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
