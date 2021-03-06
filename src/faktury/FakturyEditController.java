package faktury;

import controller.*;
import framework.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import zlecenia.*;

import java.io.*;
import java.math.*;
import java.util.*;

public class FakturyEditController implements Controller {
	private Main main;
	private Database database;

	@FXML private TextField numerField;
	@FXML private DatePicker dataWystField;
	@FXML private DatePicker dataSprzField;
	@FXML private TextField iloscDniField;

	@FXML private ListView<Zlecenie> zleceniaList;
	@FXML private Label wartoscLabel;
	@FXML private ComboBox<Zlecenie> zleceniaNieuzyteField;

	//TODO: czy to jest w ogole potrzebne???
	private EditorState state;

	private ArrayList<Zlecenie> editedZlecenia;
	private Faktura editedFaktura;
	private int editedIndex;

	@Override
	public void initialize(Main main, Database database) {
		this.main = main;
		this.database = database;
		state = EditorState.DISABLED;

		zleceniaList.itemsProperty().addListener(
		  (observable, oldValue, newValue) -> {
			if (zleceniaList.getItems().size() > 0) {
				BigDecimal tempWartosc = BigDecimal.ZERO;

				for (Zlecenie z: zleceniaList.getItems()) {
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
		//pobieramy nieuzyte zlecenia
		zleceniaNieuzyteField.setItems(FXCollections.observableArrayList(database.getNieuzyteZlecenia()));

		//otwieramy scene z zamiarem edycji - pobieramy dane z faktury
		editedZlecenia = editedFaktura.getZlecenia();
		zleceniaList.setItems(FXCollections.observableArrayList(editedZlecenia));

		numerField.setText(editedFaktura.getId().toString());
		dataWystField.setValue(editedFaktura.getDataWystawienia());
		dataSprzField.setValue(editedFaktura.getDataSprzedazy());
		iloscDniField.setText(String.valueOf(editedFaktura.getIloscDni()));

		//usuwamy fakture z bazy
		database.getFaktury().remove(editedFaktura);

	}

	@Override
	public void close() {
		//zamykajac scene, zapisujemy dane
		try {
			database.zapiszZlecenia();
			database.zapiszFaktury();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		//ustawiamy odpowiednio stan sceny edytora
		state = EditorState.DISABLED;
	}

	@Override
	public void setToDefault() {
		editedZlecenia = new ArrayList<>();
		zleceniaList.setItems(FXCollections.observableArrayList(editedZlecenia));

		numerField.setText(null);
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
			database.getZlecenia().get(addedIndex).setFakturaWykorzystujaca(editedFaktura.getId());

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
		System.out.println("save button clicked");
		String idString = numerField.getText();
		String dataWystString = dataWystField.getValue().toString();
		String dataSprzString = dataSprzField.getValue().toString();
		String iloscDniString = iloscDniField.getText();

		String[] fields = new String[4];

		fields[0] = idString;
		fields[1] = iloscDniString;
		fields[2] = dataSprzString;
		fields[3] = dataWystString;


		Faktura nowa = null;
		try {
			nowa = Faktura.validate(fields, database.getZlecenia(), database.getFaktury());
		}
		catch (InvalidObjectException e) {
			System.err.println(e.getMessage());
			cancelButtonClicked();
		}

		editedFaktura.przeliczWartosc();
		database.dodajFakture(editedIndex, nowa);

		main.switchScene("faktury_view");
	}

	@FXML
	private void cancelButtonClicked() {
		if(state == EditorState.EDIT) {
			//wrzucamy fakture z powrotem
			database.dodajFakture(editedIndex, editedFaktura);
		}

		main.switchScene("faktury_view");
	}

	public void setState(EditorState state) {
		this.state = state;
	}

	public void setEdited(Faktura edited) {
		this.editedFaktura = edited;
	}

	public void setEditedIndex(int index) {
		this.editedIndex = index;
	}
}
