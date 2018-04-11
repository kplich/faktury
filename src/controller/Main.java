package controller;

import framework.*;
import javafx.application.*;
import javafx.stage.*;

import java.io.*;
import java.util.*;

import static controller.SceneID.*;
import static controller.ScenePath.*;

/**
 * Entry-point class of the app.
 */
public class Main extends Application {
	//TODO: obslugiwac wyjatki, naprawde porzadnie
	//TODO: dodawac scenes.faktury do pliku!!!
	//TODO: poprawic grafike
	//TODO: nowy sposob na tworzenie scenes.faktury poprzez zaznaczenie wielu zlecen na raz
	//TODO: utworzyc foldery na scenes.faktury, szablony

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

        switchScene(MENU); //ustawiamy scene na scenes.menu

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

    public void switchScene(SceneID id) {
    	//zamykamy obecna scene (zapis danych, itp.) (zauwaz ze nie zmieniamy jeszcze referencji!)
		if(manager.getCurrent() != null) {
			manager.getCurrent().close();
		}

		//probujemy ustawic nowa scene
		try {
			manager.setCurrent(id);
		}
		catch (NoSuchElementException e) {
			Logging.showErrorAlert(e.getMessage());
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
			manager.registerScene(MENU, new SceneWrapper(MENU_PATH, this, database));
			manager.registerScene(ZLECENIA, new SceneWrapper(ZLECENIA_PATH, this, database));
			manager.registerScene(FAKTURY_VIEW, new SceneWrapper(FAKTURY_VIEW_PATH, this, database));
			manager.registerScene(FAKTURY_EDIT, new SceneWrapper(FAKTURY_EDIT_PATH, this, database));
			manager.registerScene(FAKTURY_CREATE, new SceneWrapper(FAKTURY_CREATE_PATH, this, database));
		}
		catch (Exception e) {
			Logging.showErrorAlert("Blad wczytywania sceny" + e.getCause().toString());
			System.exit(-1);
		}
	}

}
