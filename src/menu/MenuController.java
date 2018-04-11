package menu;

import controller.*;
import framework.*;
import javafx.fxml.*;

public class MenuController implements Controller {
    private Main main;

    @FXML void fakturyClicked() {
        main.switchScene(SceneID.FAKTURY_VIEW);
    }

    @FXML void zleceniaClicked() {
        main.switchScene(SceneID.ZLECENIA);
    }

    @Override
    public void initialize(Main main, Database database) {
		this.main = main;
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
