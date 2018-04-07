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
		System.out.println("FakturyViewController.initialze()");

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
		System.out.println("FakturyViewController.open()");

		setToDefault();

		//wczytujemy dane
		fakturyTable.setItems(FXCollections.observableArrayList(database.getFaktury()));
	}

	@Override
	public void close() {
		System.out.println("FakturyViewController.close()");

		try {
			database.zapiszZlecenia();
			database.zapiszFaktury();
		}
		catch (FileNotFoundException e) {
			Main.showErrorAlert(e.getMessage());
		}
	}

	@Override
	public void setToDefault() {
		System.out.println("defaulting!");

		//czyscimy zaznaczenie
		fakturyTable.getSelectionModel().clearSelection();

		//wylaczamy przyciski (to powinno sie w sumie dziac automatycznie ze wzgledu
		//na listenera podpietego do selectionmodelu w liscie, ale anyways
		editButton.setDisable(true);
		deleteButton.setDisable(true);
	}

    @FXML
    void newButtonClicked() {
        System.out.println("new button clicked");

        main.switchScene("faktury_create");
    }

    @FXML
    void editButtonClicked() {
        System.out.println("edit button clicked");
        Faktura temp = fakturyTable.getSelectionModel().getSelectedItem();

        //przekazujemy informacje do kontrolera edytora
		//TODO: napisac odpowiednia metode przekazujaca informacje
		((FakturyEditController) main.getManager().getScene("faktury_edit").getController()).setState(EditorState.EDIT);
		((FakturyEditController) main.getManager().getScene("faktury_edit").getController()).setEdited(temp);
		((FakturyEditController) main.getManager().getScene("faktury_edit").getController()).setEditedIndex(
		  fakturyTable.getSelectionModel().getSelectedIndex()
		);

		main.switchScene("faktury_edit");
    }

    @FXML
    void deleteButtonClicked() {
		System.out.println("delete button clicked");

		int selected = fakturyTable.getSelectionModel().getSelectedIndex();
		database.usunFakture(selected);
		fakturyTable.setItems(FXCollections.observableArrayList(database.getFaktury()));

		try {
			database.zapiszZlecenia();
			database.zapiszFaktury();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

    @FXML
    void backButtonClicked() {
		System.out.println("back button clicked");
        main.switchScene("menu");
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
			e.printStackTrace();
		}
	}

}
