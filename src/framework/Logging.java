package framework;

import javafx.scene.control.*;

public class Logging {

	public static void showErrorAlert(String msg) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Blad.");
		alert.setHeaderText(null);
		alert.setContentText(msg);

		alert.showAndWait();
	}

	public static void showConfirmationAlert(String msg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(null);
		alert.setHeaderText(null);
		alert.setContentText(msg);

		alert.showAndWait();
	}
}
