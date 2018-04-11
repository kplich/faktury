package faktury;

import controller.*;
import framework.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import printing.*;

import java.awt.*;
import java.io.*;
import java.math.*;
import java.time.*;

public class FakturyViewController implements Controller {
    private Main main;
    private Database database;

    @FXML private TableView<Faktura> fakturyTable;
    @FXML private TableColumn<Faktura, Id> idColumn;
    @FXML private TableColumn<Faktura, LocalDate> dateColumn;
    @FXML private TableColumn<Faktura, BigDecimal> wartoscColumn;

    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button printButton;


	@Override
	public void initialize(Main main, Database database) {
		this.main = main;
		this.database = database;

		//ustawiamy kolumny tabeli aby mogly wyswietlac dane
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("dataWystawienia"));
		wartoscColumn.setCellValueFactory(new PropertyValueFactory<>("wartosc"));

		//ustawiamy aktywnosc przyciskow edycji, drukowania i usuwania
		fakturyTable.getSelectionModel().selectedItemProperty().addListener(
		  (observable, oldValue, newValue) -> {
			if(newValue != null) {
				editButton.setDisable(false);
				deleteButton.setDisable(false);
				printButton.setDisable(false);
			}
			else {
				editButton.setDisable(true);
				deleteButton.setDisable(true);
				printButton.setDisable(true);
			}
		  }
		);
	}

	@Override
	public void open() {
		setToDefault();

		//wczytujemy dane
		fakturyTable.setItems(FXCollections.observableArrayList(database.getFaktury()));
	}

	@Override
	public void close() {
		try {
			database.zapiszZlecenia();
			database.zapiszFaktury();
		}
		catch (FileNotFoundException e) {
			Logging.showErrorAlert(e.getMessage());
		}
	}

	@Override
	public void setToDefault() {
		//czyscimy zaznaczenie
		fakturyTable.getSelectionModel().clearSelection();

		//wylaczamy przyciski (to powinno sie w sumie dziac automatycznie ze wzgledu
		//na listenera podpietego do selectionmodelu w liscie, ale anyways
		editButton.setDisable(true);
		deleteButton.setDisable(true);
	}

    @FXML
    void newButtonClicked() {
        main.switchScene(SceneID.FAKTURY_CREATE);
    }

    @FXML
    void editButtonClicked() {
        Faktura temp = fakturyTable.getSelectionModel().getSelectedItem();

        //przekazujemy informacje do kontrolera edytora
		//TODO: napisac odpowiednia metode przekazujaca informacje
		((FakturyEditController) main.getManager().getScene(SceneID.FAKTURY_EDIT).getController()).setState(EditorState.EDIT);
		((FakturyEditController) main.getManager().getScene(SceneID.FAKTURY_EDIT).getController()).setEdited(temp);
		((FakturyEditController) main.getManager().getScene(SceneID.FAKTURY_EDIT).getController()).setEditedIndex(
		  fakturyTable.getSelectionModel().getSelectedIndex()
		);

		main.switchScene(SceneID.FAKTURY_EDIT);
    }

    @FXML
    void deleteButtonClicked() {
		int selected = fakturyTable.getSelectionModel().getSelectedIndex();
		database.usunFakture(selected);
		fakturyTable.setItems(FXCollections.observableArrayList(database.getFaktury()));

		try {
			database.zapiszZlecenia();
			database.zapiszFaktury();
		}
		catch (FileNotFoundException e) {
			Logging.showErrorAlert(e.getMessage());
		}
	}

    @FXML
    void backButtonClicked() {
        main.switchScene(SceneID.MENU);
    }

    @FXML
    void printButtonClicked() {
		Faktura selected = fakturyTable.getSelectionModel().getSelectedItem();

		TextFile fakturaFile = TextFile.generateFaktura(selected);

		String filename = (selected.getId().toString()).replace('/', '_') + ".html"; TextFile.save(fakturaFile,
		  "faktury/" + filename);

		try {
			Desktop.getDesktop().open(new File("faktury/" + filename));
		}
		catch (IOException e) {
			Logging.showErrorAlert(e.getMessage());
		}
	}

}
