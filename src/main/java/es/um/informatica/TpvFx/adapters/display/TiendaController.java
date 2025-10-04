package es.um.informatica.TpvFx.adapters.display;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import es.um.informatica.TpvFx.App;
import es.um.informatica.TpvFx.adapters.persistence.ProductoDTO;
import es.um.informatica.TpvFx.adapters.repository.ProductoRepository;
import es.um.informatica.TpvFx.model.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
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
		
		//Es necesario reemplazar , por . ya que String.format usa el formato espanio para los decimales
		float total = Float.parseFloat(totalField.getText().isEmpty()?"0":totalField.getText().replace(",", "."));
		
		totalField.setText(String.format("%.2f", total+producto.getPrecio()));
		
		
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
	private void confirmarCompra() throws IOException {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Resumen de la compra");
		dialog.setHeaderText("Confirme su compra");

		ButtonType confirmarCombraBtn = new ButtonType("Comprar", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(confirmarCombraBtn, ButtonType.CANCEL);		
	    
		//Se tiene que copiar la tabla inicial porque JavaFX traslada el elemento de nodo y no lo copia
		//Para conservar la tabla en el otro panel se tiene que copiar
		TableView<Producto> tablaDialog = new TableView<>();
		tablaDialog.getColumns().addAll(tablaProductos.getColumns());
		tablaDialog.setItems(tablaProductos.getItems()); // reutiliza los datos
		
	 // Total
	    double total = tablaDialog.getItems().stream().mapToDouble(producto ->producto.getPrecio() * producto.getCantidad()).sum();
	    Label lblTotal = new Label("Total: " + String.format("%.2f €", total));
	    
	    VBox content = new VBox(10, tablaDialog, lblTotal);
	    content.setPrefSize(335, 400);
	    dialog.getDialogPane().setContent(content);

	 // Mostrar diálogo y comprobar respuesta
	    Optional<ButtonType> response = dialog.showAndWait();
	    if (response.isPresent() && response.get() == confirmarCombraBtn) {
	        System.out.println("Compra confirmada");
	        // procesar compra aquí
	    } else {
	        System.out.println("Compra cancelada");
	    }

		
	}
}
