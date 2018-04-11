package controller;

import framework.*;
import javafx.application.*;
import javafx.stage.*;

import java.io.*;
import java.util.*;

/**
 * Entry-point class of the app.
 */
public class Main extends Application {
	//TODO: obslugiwac wyjatki, naprawde porzadnie
	//TODO: dodawac faktury do pliku!!!
	//TODO: poprawic grafike
	//TODO: nowy sposob na tworzenie faktury poprzez zaznaczenie wielu zlecen na raz
	//TODO: utworzyc foldery na faktury, szablony

    //TODO: ustalic formatowanie

	/**
	 * File constant containing order data.
	 */
	private static final File PLIK_ZLECENIA = new File("data/zlecenia.txt");

	/**
	 * File constant containing invoice data.
	 */
	private static final File PLIK_FAKTURY = new File("data/faktury.txt");

	/**
	 * Database.
	 */
	private Database database; //obiekt zawierajacy baze faktur i zlecen

	/**
	 * JavaFX boilerplate. Kept as a field to enable switching scenes.
	 */
    private Stage stage;

	/**
	 * Contains all the scenes of a program and allows switching between them.
	 */
	private SceneManager manager; //obiekt zawierajacy wszystkie sceny w programie i pozwalajacy na przelaczanie sie miedzy nimi

    //TODO: protokół zmiany scen

    @Override
    public void start(Stage primaryStage) {
		loadData();

		loadScenes();

        stage = primaryStage; //boilerplate JavaFX?

        switchScene("menu"); //ustawiamy scene na menu

        stage.show(); //odkrywamy interfejs
    }

    @Override
    public void stop() {
		try {
			database.zapiszZlecenia();
			database.zapiszFaktury();
		}
		catch (FileNotFoundException e) {
			Logging.showErrorAlert(e.getMessage());
		}
	}

    public void switchScene(String id) {
    	//zamykamy obecna scene (zapis danych, itp.) (zauwaz ze nie zmieniamy jeszcze referencji!)
		if(manager.getCurrent() != null) {
			manager.getCurrent().close();
		}

		//probujemy ustawic nowa scene
		try {
			manager.setCurrent(id); //TODO: enum dla ID scen
		}
		catch (NoSuchElementException e) {
			Logging.showErrorAlert(e.getMessage());
			return;
		}

		//otwieramy ja - ustawiamy domyslny stan pol, wczytujemy dane, itp.
		manager.getCurrent().open();

		//ustawiamy nowa scene w Stage
        stage.setScene(manager.getCurrent().getScene());
    }

    public static void main(String[] args) {
        launch(args);
    }

    //--------------------------------------------------------

	public SceneManager getManager() {
		return manager;
	}

	//---------------------------------------------------------

	private void loadData() {
		try {
			database = new Database(PLIK_ZLECENIA, PLIK_FAKTURY);
			Logging.showConfirmationAlert("Wczytywanie zakończone powodzeniem!");
		}
		catch (FileNotFoundException e) {
			Logging.showErrorAlert(e.getMessage());
			System.exit(-1);
		}
	}

	private void loadScenes() {
		manager = new SceneManager();

		//Nie wiem, jak sie zajac ta sekcja ;_;
		try {
			manager.registerScene("menu", new SceneWrapper("/menu/menu.fxml", this, database));
			manager.registerScene("zlecenia", new SceneWrapper("/zlecenia/zlecenia.fxml", this, database));
			manager.registerScene("faktury_view", new SceneWrapper("/faktury/fakturyview.fxml", this, database));
			manager.registerScene("faktury_edit", new SceneWrapper("/faktury/fakturyedit.fxml", this, database));
			manager.registerScene("faktury_create", new SceneWrapper("/faktury/fakturycreate.fxml", this, database));
		}
		catch (Exception e) {
			Logging.showErrorAlert("Blad wczytywania sceny" + e.getCause().toString());
			System.exit(-1);
		}
	}

}
