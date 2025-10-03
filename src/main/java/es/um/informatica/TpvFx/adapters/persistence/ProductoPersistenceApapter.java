package es.um.informatica.TpvFx.adapters.persistence;

import java.io.File;
import java.io.InputStream;

import es.um.informatica.TpvFx.Constantes;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class ProductoPersistenceApapter {

	private static ProductoPersistenceApapter productoPersistenceApapter;

	public static ProductoPersistenceApapter getInscante() {
		if (productoPersistenceApapter == null) {
			productoPersistenceApapter = new ProductoPersistenceApapter();
		}
		return productoPersistenceApapter;
	}

	private ProductoPersistenceApapter() {
	}

	public ListaProductosDTO cargarProductos(String rutaFichero) throws Exception {
		InputStream ficheroStream = getClass().getResourceAsStream(Constantes.RUTA_FICHERO);
		JAXBContext context = JAXBContext.newInstance(ListaProductosDTO.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (ListaProductosDTO) unmarshaller.unmarshal(ficheroStream);
	}

	public void guardarProductos(ListaProductosDTO listaProductos, String rutaFichero) throws Exception {
		JAXBContext context = JAXBContext.newInstance(ListaProductosDTO.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // bonito
		marshaller.marshal(listaProductos, new File(rutaFichero));
	}
}
