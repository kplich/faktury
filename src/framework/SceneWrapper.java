package framework;

import controller.*;
import javafx.fxml.*;
import javafx.scene.*;

import java.io.*;

public class SceneWrapper {
	private Controller controller;
    private Scene scene;

    public SceneWrapper(String filename, Main main, Database database) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(filename));
		Parent root = loader.load();

		controller = loader.getController();
		controller.initialize(main, database);

		scene = new Scene(root, 800, 600);
    }

    public Controller getController() {
        return controller;
    }

    public Scene getScene() {
        return scene;
    }

    public void open() {
    	controller.open();
	}

	public void close() {
    	controller.close();
	}
}
