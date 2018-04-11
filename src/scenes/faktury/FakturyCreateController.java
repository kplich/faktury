package scenes.faktury;

import controller.*;
import framework.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import scenes.zlecenia.*;

import java.io.*;
import java.math.*;
import java.util.*;

public class FakturyCreateController extends Controller {
	@FXML private TextField numerField;
	@FXML private DatePicker dataWystField;
	@FXML private DatePicker dataSprzField;
	@FXML private TextField iloscDniField;

	@FXML private ListView<Zlecenie> zleceniaList;
	@FXML private Label wartoscLabel;
	@FXML private ComboBox<Zlecenie> zleceniaNieuzyteField;

	private ArrayList<Zlecenie> editedZlecenia;


	@Override
	public void initialize(Main main, Database database) {
		this.main = main;
		this.database = database;

		zleceniaList.itemsProperty().addListener(
		  (observable, oldValue, newValue) -> {
			if (zleceniaList.getItems().size() > 0) {
				BigDecimal tempWartosc = BigDecimal.ZERO;

				for (Zlecenie z : zleceniaList.getItems()) {
					tempWartosc = tempWartosc.add(z.getWartosc());
				}

				wartoscLabel.setText(tempWartosc.toString() + " zł");
			}
			else {
				wartoscLabel.setText(" - zł");
			}
		});
	}

	@Override
	public void open() {
		//pobieramy nieuzyte scenes.zlecenia
		zleceniaNieuzyteField.setItems(FXCollections.observableArrayList(database.getNieuzyteZlecenia()));

		setToDefault();

		numerField.setText(database.getOstatniNumer().nextNumber().toString());
	}

	@Override
	public void close() {
		//zamykajac scene, zapisujemy dane
		try {
			database.zapiszZlecenia();
			database.zapiszFaktury();
		}
		catch (FileNotFoundException e) {
			Logging.showErrorAlert(e.getMessage());
		}

		//ustawiamy odpowiednio stan sceny edytora
		//state = EditorState.DISABLED;
	}

	@Override
	public void setToDefault() {
		editedZlecenia = new ArrayList<>();
		zleceniaList.setItems(FXCollections.observableArrayList(editedZlecenia));

		numerField.setText(database.getOstatniNumer().nextNumber().toString());
		dataWystField.setValue(null);
		dataSprzField.setValue(null);
		iloscDniField.setText(null);
	}

	@FXML
	private void dodajZlecenieClicked() {
		Zlecenie added = zleceniaNieuzyteField.getSelectionModel().getSelectedItem(); //wybrane zlecenie

		if(added != null) {
			//zaznaczamy fakt ze zlecenie zostalo uzyte
			int addedIndex = database.getZlecenia().indexOf(added);
			database.getZlecenie(added.getNumer()).setFakturaWykorzystujaca(database.getOstatniNumer().nextNumber());

			//dodajemy zlecenie do tymczasowej listy
			editedZlecenia.add(added);
			zleceniaList.setItems(FXCollections.observableArrayList(editedZlecenia));
			zleceniaList.getSelectionModel().clearSelection();

			//poniewaz zaznaczono, ze zlecenie jest uzyte nie znajdzie sie w liscie zwroconej przez metode getNieuzyteZlecenia()
			zleceniaNieuzyteField.setItems(FXCollections.observableArrayList(database.getNieuzyteZlecenia()));
			zleceniaNieuzyteField.getSelectionModel().clearSelection();
		}
	}

	@FXML
	private void deleteZlecenieClicked() {
		Zlecenie deleted = zleceniaList.getSelectionModel().getSelectedItem();

		if(deleted != null) {
			//zaznaczamy fakt ze zlecenie nie jest juz uzywane
			int deletedIndex = database.getZlecenia().indexOf(deleted);
			database.getZlecenia().get(deletedIndex).setFakturaWykorzystujaca(null);

			//usuwamy zlecenie z tymczasowej listy
			editedZlecenia.remove(deleted);
			zleceniaList.setItems(FXCollections.observableArrayList(editedZlecenia));
			zleceniaList.getSelectionModel().clearSelection();

			//poniewaz zaznaczono ze zlecenie nie jest juz uzywane,
			//dlatego pojawi sie w liscie zwroconej przez metode getNieuzyteZlecenia()
			zleceniaNieuzyteField.setItems(FXCollections.observableArrayList(database.getNieuzyteZlecenia()));
			zleceniaNieuzyteField.getSelectionModel().clearSelection();
		}
	}

	@FXML
	private void saveButtonClicked() {
		//pobieramy dane
		String idString = numerField.getText();
		String dataWystString = dataWystField.getValue().toString();
		String dataSprzString = dataSprzField.getValue().toString();
		String iloscDniString = iloscDniField.getText();

		String[] fields = new String[4];

		fields[0] = idString;
		fields[1] = iloscDniString;
		fields[2] = dataSprzString;
		fields[3] = dataWystString;

		//tworzymy nowa fakture
		Faktura nowa = null;
		try {
			nowa = Faktura.validate(fields, database.getZlecenia(), database.getFaktury());
		}
		catch (InvalidObjectException e) {
			Logging.showErrorAlert(e.getMessage());
			cancelButtonClicked();
		}

		database.dodajFakture(nowa);
		database.incrementOstatniNumer();

		cancelButtonClicked();
	}

	@FXML
	private void cancelButtonClicked() {
		main.switchScene(SceneID.FAKTURY_VIEW);
	}
}
