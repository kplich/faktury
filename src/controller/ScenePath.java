package controller;

public enum ScenePath {
	MENU_PATH("/scenes/menu/menu.fxml"),
	ZLECENIA_PATH("/scenes/zlecenia/zlecenia.fxml"),
	FAKTURY_VIEW_PATH("/scenes/faktury/fakturyview.fxml"),
	FAKTURY_EDIT_PATH("/scenes/faktury/fakturyedit.fxml"),
	FAKTURY_CREATE_PATH("/scenes/faktury/fakturycreate.fxml");

	private String path;

	ScenePath(String path) {
		this.path = path;
	}

	public String getString() {
		return path;
	}
}
