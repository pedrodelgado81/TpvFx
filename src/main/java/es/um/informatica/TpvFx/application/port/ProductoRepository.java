package es.um.informatica.TpvFx.application.port;

import java.util.List;
import java.util.stream.Collectors;

import es.um.informatica.TpvFx.Constantes;
import es.um.informatica.TpvFx.adapters.persistence.ProductoMapper;
import es.um.informatica.TpvFx.adapters.persistence.ProductoPersistenceApapter;
import es.um.informatica.TpvFx.model.Producto;

public interface ProductoRepository {

	/**
	 * Elimina del stock los productos en la lista de productosComprados
	 * @param productosComprados
	 */
	public void actualizaAlmacenConVenta(List<Producto> productosComprados);

	/**
	 * Carga del sistema de persistencia los productos
	 */
	public void cargaProductos();

	/**
	 * Obtiene los productos actualmente en el stock
	 * @return
	 */
	public List<Producto> getProductos();

	/**
	 * Aniade un producto al stock de productos cargados
	 * @param producto
	 */
	public void aniadirProducto(Producto producto);

	/**
	 * Elimina un producto del stock de productos cargados
	 * @param producto
	 */
	public void eliminarProducto(Producto producto);
}
