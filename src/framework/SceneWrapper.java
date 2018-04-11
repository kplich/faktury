package framework;

import controller.*;
import javafx.fxml.*;
import javafx.scene.*;

import java.io.*;

/**
 * Wrapper for a usual JavaFX Scene class. Created to enable sort of a protocol for switching scenes -
 * - initializing, opening and closing.
 */
public class SceneWrapper {
	private Controller controller;
    private Scene scene;

    public SceneWrapper(ScenePath path, Main main, Database database) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(path.getString()));
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
