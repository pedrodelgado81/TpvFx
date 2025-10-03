module es.um.informatica.TpvFx {
    requires javafx.controls;
    requires javafx.fxml;
	requires jakarta.xml.bind;

    opens es.um.informatica.TpvFx to javafx.fxml;
    opens es.um.informatica.TpvFx.adapters to javafx.fxml;
    opens es.um.informatica.TpvFx.model to javafx.base;
    opens es.um.informatica.TpvFx.adapters.persistence to jakarta.xml.bind;

    exports es.um.informatica.TpvFx;
}
