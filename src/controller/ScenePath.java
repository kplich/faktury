package controller;

public enum ScenePath {
	MENU_PATH("/menu/menu.fxml"),
	ZLECENIA_PATH("/zlecenia/zlecenia.fxml"),
	FAKTURY_VIEW_PATH("/faktury/fakturyview.fxml"),
	FAKTURY_EDIT_PATH("/faktury/fakturyedit.fxml"),
	FAKTURY_CREATE_PATH("/faktury/fakturycreate.fxml");

	private String path;


	ScenePath(String path) {
		this.path = path;
	}

	public String getString() {
		return path;
	}
}
