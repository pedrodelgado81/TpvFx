package es.um.informatica.TpvFx.adapters.display;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.um.informatica.TpvFx.App;
import es.um.informatica.TpvFx.adapters.repository.ProductoRepository;
import es.um.informatica.TpvFx.model.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class TiendaController {

	private ProductoRepository productoRepository;

	private List<Producto> listaProductos;

	private Producto productoSeleccionado;

	@FXML
	private TextField totalField;
	
	@FXML
	private TableView<Producto> tablaProductos;

	@FXML
	private TableColumn<Producto, String> colDescripcion;
	@FXML
	private TableColumn<Producto, String> colCantidad;
	@FXML
	private TableColumn<Producto, Double> colPrecio;

	@FXML
	private ScrollPane panelProductos;

	@FXML
	private TextField filtroProductos;

	@FXML
	public void initialize() {
		productoRepository = ProductoRepository.getInscante();
		productoRepository.cargaProductos();
		listaProductos = productoRepository.getProductos();
		cargaProductos("");
		tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				this.productoSeleccionado = newSelection;
			}
		});
		
		colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
		colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
		colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

		// Cargo la lista de productos
		ObservableList<Producto> lista = null;
		try {
			lista = FXCollections.observableArrayList(new ArrayList<Producto>());
		} catch (Exception e) {
			// FIXME: Cambiar por log
			e.printStackTrace();
		}
		// Pasar lista a la tabla
		tablaProductos.setItems(lista);

	}

	private void cargaProductos(String filtro) {

		TilePane contenedor = new TilePane();
		contenedor.setPrefColumns(4); // máximo 3 productos por fila
		contenedor.setHgap(10); // separación horizontal
		contenedor.setVgap(10); // separación vertical
		contenedor.setStyle("-fx-padding: 10;");

		// Simulamos una lista de productos
		listaProductos.stream().filter(producto -> producto.getDescripcion().contains(filtro)).forEach(producto -> {

			// Crear labels
			Label labelNombre = new Label(producto.getDescripcion());
			Label labelPrecio = new Label(Float.toString(producto.getPrecio()));

			// Contenedor vertical para centrar cada fila
			VBox vbox = new VBox(5, labelNombre, labelPrecio);
			vbox.setAlignment(Pos.CENTER);

			// Botón con VBox como "gráfico"
			Button btnProducto = new Button();
			btnProducto.setPrefSize(100, 60);
			btnProducto.setGraphic(vbox);
			btnProducto.setOnAction(e -> {
				aniadeListaCompra(new Producto(producto.getCodigo(),producto.getDescripcion(),1,producto.getPrecio()));
			});

			contenedor.getChildren().add(btnProducto);

			return;
		});
		panelProductos.setContent(contenedor);

	}

	private void aniadeListaCompra(Producto producto) {
		producto.setCantidad(1);
		if (!tablaProductos.getItems().contains(producto)) {
			tablaProductos.getItems().add(producto);		
		} else {
			int productoEnLista = tablaProductos.getItems().indexOf(producto);
			Producto productoSeleccionado = tablaProductos.getItems().get(productoEnLista);
			productoSeleccionado.setCantidad(productoSeleccionado.getCantidad() + 1);
		}
		
		float total = Float.parseFloat(totalField.getText().isEmpty()?"0":totalField.getText());
		//TODO: Fijar en solo dos decimales
		totalField.setText(Float.toString(total+producto.getPrecio()));
		
		
		tablaProductos.refresh();

	}

	@FXML
	private void buscarProducto() {
		if (filtroProductos != null && filtroProductos.getText().length() > 0) {
			cargaProductos(filtroProductos.getText());
		}
	}

	@FXML
	private void limpiarFiltro() {
		cargaProductos("");
		filtroProductos.setText("");
	}

	@FXML
	private void irAStock() throws IOException {
		App.setRoot("tiendaStock");
	}

	@FXML
	private void irACarrito() throws IOException {
		App.setRoot("tiendaCarrito");
	}
}
