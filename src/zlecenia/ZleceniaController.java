package zlecenia;

import controller.*;
import framework.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.*;

public class ZleceniaController implements Controller {
	private Main main;
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

    @Override
    public void initialize(Main main, Database database) {
        System.out.println("ZleceniaController.initialize()");
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

	@Override
	public void open() {
		System.out.println("ZleceniaController.open()");

		setToDefault();

		//wczytujemy elementy do listy
		listaZlecen.setItems(FXCollections.observableArrayList(database.getZlecenia()));
	}

	@Override
	public void close() {
		System.out.println("ZleceniaController.close()");

		try {
			database.zapiszZlecenia();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setToDefault() {
		System.out.println("defaulting!");

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
		System.out.println("new button clicked");
		listaZlecen.getSelectionModel().clearSelection();
		listAnchor.setDisable(true);
		enableBox(EditorState.NEW);

		numerField.setText(null);
		nazwaField.setText(null);
		wartoscField.setText(null);
	}

    @FXML
    private void editButtonClicked() {
        System.out.println("edit button clicked");

        if(listaZlecen.getSelectionModel().getSelectedItem() != null) {
			listAnchor.setDisable(true);
			enableBox(EditorState.EDIT);
		}
    }

	@FXML
	private void deleteButtonClicked() {
		System.out.println("delete button clicked");

		if(listaZlecen.getSelectionModel().getSelectedItem() != null) {
			int selectedIndex = listaZlecen.getSelectionModel().getSelectedIndex();
			database.getZlecenia().remove(selectedIndex);
			listaZlecen.setItems(FXCollections.observableArrayList(database.getZlecenia()));
		}

		setToDefault();
	}

	@FXML
	private void saveButtonClicked() {
		System.out.println("save button clicked");
		String numer = numerField.getText();
		String nazwa = nazwaField.getText();
		String wartosc = wartoscField.getText();

		Zlecenie temp;

		switch (boxState) {
			case NEW: {
				try {
					temp = Zlecenie.validate(numer, nazwa, wartosc);
					database.dodajZlecenie(temp);
				}
				catch (InvalidObjectException e) {
					System.err.println(e.getMessage());
				}
				break;
			}
			case EDIT: {
				int selectedIndex = listaZlecen.getSelectionModel().getSelectedIndex();
				Zlecenie copy = listaZlecen.getSelectionModel().getSelectedItem();
				database.getZlecenia().remove(selectedIndex);

				try {
					temp = Zlecenie.validate(numer, nazwa, wartosc);
				}
				catch (InvalidObjectException e) {
					System.err.println(e.getMessage());
					database.dodajZlecenie(selectedIndex, copy);
					setToDefault();
					return;
				}

				database.dodajZlecenie(selectedIndex, temp);
				break;
			}
		}

		listaZlecen.setItems(FXCollections.observableArrayList(database.getZlecenia()));
		setToDefault();
	}

	@FXML
	private void cancelButtonClicked() {
		System.out.println("cancel button clicked");
		setToDefault();
	}

	@FXML
	private void backButtonClicked() {
		System.out.println("back button clicked");
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
