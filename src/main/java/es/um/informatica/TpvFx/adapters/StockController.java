package es.um.informatica.TpvFx.adapters;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

import es.um.informatica.TpvFx.App;
import es.um.informatica.TpvFx.Constantes;
import es.um.informatica.TpvFx.adapters.persistence.ProductoDTO;
import es.um.informatica.TpvFx.adapters.persistence.ProductoMapper;
import es.um.informatica.TpvFx.adapters.persistence.ProductoPersistenceApapter;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class StockController {

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

	private ProductoPersistenceApapter productoPersistenceAdapter;

	// Este metodo se ejecuta al cargar la pantalla correspondiente
	@FXML
	public void initialize() {
		// Persistence adapter para leer/guardar sobre XML
		productoPersistenceAdapter = ProductoPersistenceApapter.getInscante();

		// Creo los manejadores para los campos de las columnas
		colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
		colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
		colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
		colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

		// Cargo la lista de productos
		ObservableList<Producto> lista = null;
		try {
			lista = FXCollections.observableArrayList(
					productoPersistenceAdapter.cargarProductos(Constantes.RUTA_FICHERO).getProductos().stream()
							.map(producto -> ProductoMapper.fromProuctoDTO(producto)).collect(Collectors.toList()));
		} catch (Exception e) {
			// FIXME: Cambiar por log
			e.printStackTrace();
		}

		// Listener para las filas seleccionadas
		tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				this.productoSeleccionado = newSelection;
			}
		});
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
	private void aniadirStockDialog() {
		Dialog<ProductoDTO> dialog = new Dialog<>();
		dialog.setTitle("Alta de nuevo Producto");
		dialog.setHeaderText("Introduce los datos del nuevo producto");

		ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

		// --- Contenido del formulario ---
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

		// --- Convertir el resultado en un objeto Product ---
		dialog.setResultConverter(new Callback<ButtonType, ProductoDTO>() {
			@Override
			public ProductoDTO call(ButtonType button) {
				if (button == guardarButtonType) {
					try {
						Producto producto = tablaProductos.getItems().get(tablaProductos.getItems().size()-1);						
						float precio = Float.parseFloat(precioField.getText());
						tablaProductos.getItems().add(new Producto(String.valueOf(Integer.parseInt(producto.getCodigo())+1), descripcionArea.getText(), cantidadSpinner.getValue(), precio));
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

		// --- Mostrar y obtener el resultado ---
		dialog.showAndWait().ifPresent(producto -> {
			System.out.println("Producto creado: " + producto);
		});
	}

	@FXML
	private void irATienda() throws IOException {
		App.setRoot("tiendaMain");
	}

	@FXML
	private void eliminarDeStock() {
		if (this.productoSeleccionado != null) {
			confirmarEliminacion.setContentText(
					"¿Está seguro que quiere eliminar " + this.productoSeleccionado.getDescripcion() + "?");
			Optional<ButtonType> result = confirmarEliminacion.showAndWait();
			if (result.get() == ButtonType.OK) {
				tablaProductos.getItems().remove(this.productoSeleccionado);
				this.productoSeleccionado = null;
				tablaProductos.refresh();
			}
		}

	}

	@FXML
	private void aumentarStock() {
		if (this.productoSeleccionado != null && this.aumentarCantidadTextField.getText() != null
				&& this.aumentarCantidadTextField.getText().length() > 0) {
			try {
				int incremento = Integer.parseInt(aumentarCantidadTextField.getText());
				this.productoSeleccionado.setCantidad(this.productoSeleccionado.getCantidad() + incremento);
				aumentarCantidadTextField.setText("");
				tablaProductos.refresh();
			} catch (NumberFormatException e) {
				// FIXME log
				e.printStackTrace();
			}
		}
	}

	@FXML
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
				reducirCantidadTextField.setText("");
				tablaProductos.refresh();
			} catch (NumberFormatException e) {
				// FIXME log
				e.printStackTrace();
			}
		}
	}

}
