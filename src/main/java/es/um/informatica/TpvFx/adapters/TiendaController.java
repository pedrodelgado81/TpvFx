package es.um.informatica.TpvFx.adapters;

import java.io.IOException;

import es.um.informatica.TpvFx.App;
import javafx.fxml.FXML;

public class TiendaController {
	
	
	@FXML
	private void irAStock() throws IOException {
		App.setRoot("tiendaStock");
	}
	
	@FXML
	private void irACarrito() throws IOException {
		App.setRoot("tiendaCarrito");
	}
}
