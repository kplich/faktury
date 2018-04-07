package zlecenia;

import controller.*;
import framework.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.*;

public class ZleceniaController implements Controller {
	/**
	 * A reference to the Main class, used in order to switch scenes.
	 */
	private Main main;

	/**
	 * A reference to the database containing all the orders and invoices.
	 */
	private Database database;

	@FXML private ListView<Zlecenie> listaZlecen;

	@FXML private TextField numerField;
	@FXML private TextField nazwaField;
	@FXML private TextField wartoscField;

	@FXML private AnchorPane listAnchor;
	private EditorState boxState;

	@FXML private Button editButton;
	@FXML private Button deleteButton;
	@FXML private Button saveButton;
	@FXML private Button cancelButton;

	/**
	 * Initializes the controller. Sets up the listeners of fields in the right panel to show details of each order.
	 * @param main a reference to the main application class
	 * @param database a reference to the database
	 */
    @Override
    public void initialize(Main main, Database database) {
		this.main = main;
		this.database = database;

		//ustawiamy wyswietlanie danych zlecenia wraz z jego zaznaczeniem
		listaZlecen.getSelectionModel().selectedItemProperty().addListener(
          (observable, oldValue, newValue) -> {
			  if (newValue == null) {
				  numerField.setText(null);
				  nazwaField.setText(null);
				  wartoscField.setText(null);

				  editButton.setDisable(true);
				  deleteButton.setDisable(true);
			  }
              else {
				  numerField.setText(String.valueOf(newValue.getNumer()));
				  nazwaField.setText(newValue.getNazwa());
				  wartoscField.setText(newValue.getWartosc().toString());

				  editButton.setDisable(false);
				  deleteButton.setDisable(false);
			  }
		  }
        );
	}

	/**
	 *
	 */
	@Override
	public void open() {
		setToDefault();

		//wczytujemy elementy do listy
		listaZlecen.setItems(FXCollections.observableArrayList(database.getZlecenia()));
	}

	@Override
	public void close() {
		try {
			database.zapiszZlecenia();
		}
		catch (FileNotFoundException e) {
			Logging.showErrorAlert(e.getMessage());
		}
	}

	@Override
	public void setToDefault() {
		//czyscimy zaznaczenie
		listaZlecen.getSelectionModel().clearSelection();

		//usuwamy tekst z pol
		numerField.setText(null);
		nazwaField.setText(null);
		wartoscField.setText(null);

		//wylaczamy przyciski
		editButton.setDisable(true);
		deleteButton.setDisable(true);

		//wylaczamy czesc z polami
		listAnchor.setDisable(false);
		disableBox();
	}

	@FXML
	private void newButtonClicked() {
		listaZlecen.getSelectionModel().clearSelection();
		listAnchor.setDisable(true);
		enableBox(EditorState.NEW);

		numerField.setText(null);
		nazwaField.setText(null);
		wartoscField.setText(null);
	}

    @FXML
    private void editButtonClicked() {
        if(listaZlecen.getSelectionModel().getSelectedItem() != null) {
			listAnchor.setDisable(true);
			enableBox(EditorState.EDIT);
		}
    }

	@FXML
	private void deleteButtonClicked() {
		if(listaZlecen.getSelectionModel().getSelectedItem() != null) {
			int selectedIndex = listaZlecen.getSelectionModel().getSelectedIndex();
			database.getZlecenia().remove(selectedIndex);
			listaZlecen.setItems(FXCollections.observableArrayList(database.getZlecenia()));
		}

		setToDefault();
	}

	@FXML
	private void saveButtonClicked() {
    	//pobieramy wartości pól wpisane przez użytkownika
		String numer = numerField.getText();
		String nazwa = nazwaField.getText();
		String wartosc = wartoscField.getText();

		//zlecenie, ktore bedziemy dodawac
		Zlecenie temp;

		//albo dodajemy nowe zlecenie, albo uaktualniamy istniejące
		switch (boxState) {
			case NEW: {
				try {
					temp = Zlecenie.validate(numer, nazwa, wartosc);
					database.dodajZlecenie(temp);
				}
				catch (InvalidObjectException e) {
					Logging.showErrorAlert(e.getMessage());
				}
				break;
			}
			case EDIT: {
				//zapamietujemy, na ktorym miejscu znajduje sie zlecenie, ktore edytujemy
				int selectedIndex = listaZlecen.getSelectionModel().getSelectedIndex();

				//zachowujemy kopie zlecenia na wypadek niepowodzenia
				Zlecenie backUp = listaZlecen.getSelectionModel().getSelectedItem();

				//usuwamy zlecenie z bazy
				database.getZlecenia().remove(selectedIndex);

				//probujemy dodac nowe zlecenie na miejscu poprzedniego
				//jesli sie nie uda, dodajemy stare zlecenie spowrotem
				try {
					temp = Zlecenie.validate(numer, nazwa, wartosc);
					database.dodajZlecenie(selectedIndex, temp);
				}
				catch (InvalidObjectException e) {
					Logging.showErrorAlert(e.getMessage());
					database.dodajZlecenie(selectedIndex, backUp);
				}
				break;
			}
		}

		//ustawiamy nowa liste zlecen do wyswietlania
		listaZlecen.setItems(FXCollections.observableArrayList(database.getZlecenia()));
		setToDefault();
	}

	@FXML
	private void cancelButtonClicked() {
		setToDefault();
	}

	@FXML
	private void backButtonClicked() {
		main.switchScene("menu");
	}

	private void disableBox() {
    	numerField.setEditable(false);
    	numerField.setMouseTransparent(true);
    	numerField.setFocusTraversable(false);

    	nazwaField.setEditable(false);
    	nazwaField.setMouseTransparent(true);
    	nazwaField.setFocusTraversable(false);

    	wartoscField.setEditable(false);
    	wartoscField.setMouseTransparent(true);
    	wartoscField.setFocusTraversable(false);

    	saveButton.setDisable(true);
    	cancelButton.setDisable(true);


    	boxState = EditorState.DISABLED;
	}

	private void enableBox(EditorState state) {
		numerField.setEditable(true);
		numerField.setMouseTransparent(false);
		numerField.setFocusTraversable(true);

		nazwaField.setEditable(true);
		nazwaField.setMouseTransparent(false);
		nazwaField.setFocusTraversable(true);

		wartoscField.setEditable(true);
		wartoscField.setMouseTransparent(false);
		wartoscField.setFocusTraversable(true);

		saveButton.setDisable(false);
		cancelButton.setDisable(false);

		boxState = state;
	}
}
