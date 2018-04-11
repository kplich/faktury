package scenes.menu;

import controller.*;
import framework.*;
import javafx.fxml.*;

public class MenuController extends Controller {

    @FXML
	void fakturyClicked() {
        main.switchScene(SceneID.FAKTURY_VIEW);
    }

    @FXML
	void zleceniaClicked() {
        main.switchScene(SceneID.ZLECENIA);
    }

    @Override
	public void open() {
    	//do nothing
	}

	@Override
	public void close() {
    	//do nothing
	}

	@Override
	public void setToDefault() {
    	//do nothing
	}
}
