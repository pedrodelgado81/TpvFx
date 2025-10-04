package es.um.informatica.TpvFx.adapters.display;

import java.io.IOException;

import es.um.informatica.TpvFx.App;
import javafx.fxml.FXML;

public class CarritoController {
	
	
	@FXML
	private void irATienda() throws IOException {
		App.setRoot("tiendaMain");
	}
}
