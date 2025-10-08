package es.um.informatica.TpvFx.adapters.display;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.um.informatica.TpvFx.App;
import es.um.informatica.TpvFx.adapters.persistence.ProductoDTO;
import es.um.informatica.TpvFx.adapters.repository.ProductoRepositoryImpl;
import es.um.informatica.TpvFx.application.port.ProductoRepository;
import es.um.informatica.TpvFx.model.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class StockController {

	private static final Logger logger = Logger.getLogger(StockController.class.getName());

	@FXML
	private TableView<Producto> tablaProductos;

	@FXML
	private TableColumn<Producto, String> colCodigo;
	@FXML
	private TableColumn<Producto, String> colDescripcion;
	@FXML
	private TableColumn<Producto, String> colCantidad;
	@FXML
	private TableColumn<Producto, Double> colPrecio;

	private Alert confirmarEliminacion;

	@FXML
	TextField aumentarCantidadTextField;

	@FXML
	TextField reducirCantidadTextField;

	Producto productoSeleccionado;

	private ProductoRepository productoRepository;

	@FXML
	// Metodo que inicializa la pantalla
	public void initialize() {
		// Persistence adapter para leer/guardar sobre XML
		productoRepository = ProductoRepositoryImpl.getInscante();
		// Creo los manejadores para los campos de las columnas
		colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
		colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
		colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
		colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

		// Cargo la lista de productos
		ObservableList<Producto> lista = null;
		try {
			lista = FXCollections.observableArrayList(productoRepository.getProductos());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error inicializando los productos", e);
		}

		// Listener para las filas seleccionadas
		tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				this.productoSeleccionado = newSelection;
			}
		});
		// Precargamos la informacion para el panel de confirmacion
		inicializaConfirmacion();
		// Pasar lista a la tabla
		tablaProductos.setItems(lista);

	}

	private void inicializaConfirmacion() {
		confirmarEliminacion = new Alert(AlertType.CONFIRMATION);
		confirmarEliminacion.setTitle("Eliminar producto de stock");
		confirmarEliminacion.setHeaderText("Va a eliminar un producto de su stock, esta tarea no se puede deshacer");
	}

	@FXML
	// Metodo para crear un nuevo producto y anadirlo al stock
	// Carga el componente visual para ello
	private void aniadirStockDialog() {
		Dialog<ProductoDTO> dialog = new Dialog<>();
		dialog.setTitle("Alta de nuevo Producto");
		dialog.setHeaderText("Introduce los datos del nuevo producto");

		ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		TextField descripcionArea = new TextField();
		Spinner<Integer> cantidadSpinner = new Spinner<>(1, 1000, 1);
		TextField precioField = new TextField();

		grid.add(new Label("Descripción:"), 0, 1);
		grid.add(descripcionArea, 1, 1);
		grid.add(new Label("Cantidad:"), 0, 2);
		grid.add(cantidadSpinner, 1, 2);
		grid.add(new Label("Precio (€):"), 0, 3);
		grid.add(precioField, 1, 3);

		dialog.getDialogPane().setContent(grid);

		// Conversor del resultado del dialogo a Producto
		dialog.setResultConverter(new Callback<ButtonType, ProductoDTO>() {
			@Override
			public ProductoDTO call(ButtonType button) {
				if (button == guardarButtonType) {
					try {
						// Obtenemos el ultimo producto para saber su codigo
						Producto producto = tablaProductos.getItems().get(tablaProductos.getItems().size() - 1);
						float precio = Float.parseFloat(precioField.getText());
						// Creo el nuevo producto
						Producto nuevoProducto = new Producto(
								String.valueOf(Integer.parseInt(producto.getCodigo()) + 1), descripcionArea.getText(),
								cantidadSpinner.getValue(), precio);
						// Actualizo el stock de productos
						productoRepository.aniadirProducto(nuevoProducto);
						// Actualizo el componente visual de la tabla
						tablaProductos.getItems().add(nuevoProducto);
						return null;
					} catch (NumberFormatException ex) {
						Alert error = new Alert(Alert.AlertType.ERROR, "Precio inválido", ButtonType.OK);
						error.showAndWait();
						return null;
					}
				}
				return null;
			}
		});

		// Muestra el dialogo y espera a que se cierre
		dialog.showAndWait().ifPresent(producto -> {
			logger.info("Producto creado: " + producto.getDescripcion());
		});
	}

	@FXML
	// Navega a la pantalla de tienda
	private void irATienda() throws IOException {
		App.setRoot("tiendaMain");
	}

	@FXML
	// Elimino el producto seleccionado del stock
	private void eliminarDeStock() {
		if (this.productoSeleccionado != null) {
			// Actualizo la descripcion del panel de confirmacion
			confirmarEliminacion.setContentText(
					"¿Está seguro que quiere eliminar " + this.productoSeleccionado.getDescripcion() + "?");
			// Abro el panel de confirmacion y espero la respuesta del usuario
			Optional<ButtonType> result = confirmarEliminacion.showAndWait();
			if (result.get() == ButtonType.OK) {
				// Elimino el producto del componente visual de la tabla
				tablaProductos.getItems().remove(this.productoSeleccionado);
				// Elimino el producto del stock de productos
				this.productoRepository.eliminarProducto(productoSeleccionado);
				this.productoSeleccionado = null;
				// Refresco la tabla para que se vea el valor actualizado
				tablaProductos.refresh();
				// Quito el elemento seleccionado de la tabla
				tablaProductos.getSelectionModel().clearSelection();

			}
		}

	}

	@FXML
	// Aumenta el número de stock de un elemento seleccionado
	private void aumentarStock() {
		if (this.productoSeleccionado != null && this.aumentarCantidadTextField.getText() != null
				&& this.aumentarCantidadTextField.getText().length() > 0) {
			try {
				int incremento = Integer.parseInt(aumentarCantidadTextField.getText());
				this.productoSeleccionado.setCantidad(this.productoSeleccionado.getCantidad() + incremento);
				// TODO: Falta trasladar el aumento a productoRepository
				aumentarCantidadTextField.setText("");
				tablaProductos.refresh();
			} catch (NumberFormatException e) {
				logger.log(Level.SEVERE, "Error con el formato numerico", e);
			}
		}
	}

	@FXML
	// Reduce la cantidad de stock del elemento seleccionado
	private void reducirStock() {
		if (this.productoSeleccionado != null && this.reducirCantidadTextField.getText() != null
				&& this.reducirCantidadTextField.getText().length() > 0) {
			try {
				int decremento = Integer.parseInt(reducirCantidadTextField.getText());
				if (decremento > this.productoSeleccionado.getCantidad()) {
					this.productoSeleccionado.setCantidad(0);
				} else {
					this.productoSeleccionado.setCantidad(this.productoSeleccionado.getCantidad() - decremento);
				}
				// TODO: Falta trasladar el aumento a productoRepository
				reducirCantidadTextField.setText("");
				tablaProductos.refresh();
			} catch (NumberFormatException e) {
				logger.log(Level.SEVERE, "Error con el formato numerico", e);
			}
		}
	}

}
