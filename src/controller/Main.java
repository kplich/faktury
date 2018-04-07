package controller;

import framework.*;
import javafx.application.*;
import javafx.stage.*;

import java.io.*;
import java.util.*;

public class Main extends Application {
	//TODO: obslugiwac wyjatki, naprawde porzadnie
	//TODO: dodawac faktury do pliku!!!
	//TODO: poprawic grafike
	//TODO: nowy sposob na tworzenie faktury poprzez zaznaczenie wielu zlecen na raz
	//TODO: utworzyc foldery na faktury, szablony

    //TODO: ustalic formatowanie
	private static final File PLIK_ZLECENIA = new File("data/zlecenia.txt");
	private static final File PLIK_FAKTURY = new File("data/faktury.txt");

	private Database database; //obiekt zawierajacy baze faktur i zlecen

    private Stage stage;

    private SceneManager manager; //obiekt zawierajacy wszystkie sceny w programie i pozwalajacy na przelaczanie sie miedzy nimi

    //TODO: protokół zmiany scen

    @Override
    public void start(Stage primaryStage) {
		//wczytujemy dane
		try {
			database = new Database(PLIK_ZLECENIA, PLIK_FAKTURY);
			Logging.showConfirmationAlert("Wczytywanie zakończone powodzeniem!");
		}
		catch (FileNotFoundException e) {
			Logging.showErrorAlert(e.getMessage());
			System.exit(-1);
		}

    	manager = new SceneManager();

		//dodajemy sceny do menedzera
		try {
			manager.registerScene("menu", new SceneWrapper("/menu/menu.fxml", this, database));
			manager.registerScene("zlecenia", new SceneWrapper("/zlecenia/zlecenia.fxml", this, database));
			manager.registerScene("faktury_view", new SceneWrapper("/faktury/fakturyview.fxml", this, database));
			manager.registerScene("faktury_edit", new SceneWrapper("/faktury/fakturyedit.fxml", this, database));
			manager.registerScene("faktury_create", new SceneWrapper("/faktury/fakturycreate.fxml", this, database));
		}
		catch (IOException e) {
			Logging.showErrorAlert("Blad wczytywania interfejsu" + e.getCause().toString());
			System.exit(-1);
		}

        stage = primaryStage;

        switchScene("menu");

        stage.show();
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
    	//zmieniamy scene na te o podanym ID
		if(manager.getCurrent() != null) {
			manager.getCurrent().close();
		}

		//ustawiamy nowa scene i ja przygotowujemy - "otwieramy"
		try {
			manager.setCurrent(id); //TODO: enum dla ID scen
		}
		catch (NoSuchElementException e) {
			Logging.showErrorAlert(e.getMessage());
			return;
		}

		manager.getCurrent().open();

        stage.setScene(manager.getCurrent().getScene());
    }

    public static void main(String[] args) {
        launch(args);
    }

	public SceneManager getManager() {
		return manager;
	}

}
