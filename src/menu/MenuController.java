package menu;

import controller.*;
import framework.*;
import javafx.fxml.*;

public class MenuController implements Controller {
    private Main main;

    @FXML void fakturyClicked() {
        System.out.println("faktury clicked");
        main.switchScene("faktury_view");
    }

    @FXML void zleceniaClicked() {
        System.out.println("zlecenia clicked");
        main.switchScene("zlecenia");
    }

    @Override
    public void initialize(Main main, Database database) {
		System.out.println("MenuController.initialize()");
		this.main = main;
    }

    @Override
	public void open() {
		System.out.println("MenuController.open()");
	}

	@Override
	public void close() {
		System.out.println("MenuController.close()");
	}

	@Override
	public void setToDefault() {
		System.out.println("MenuController.setToDefault()");
	}
}
