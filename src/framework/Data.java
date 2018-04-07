package framework;

import javafx.scene.control.*;

import java.io.*;
import java.util.*;

public abstract class Data<T extends Comparable<T>> {
    protected File plik;
	protected ArrayList<T> lista = new ArrayList<>();

	public Data() {}

    public Data(File file) throws FileNotFoundException {
        plik = file;
		try {
			wczytaj(plik); //tu moze zostac rzucony wyjatek FNF
		}
		catch (FileNotFoundException e) {
			//zakladamy ze nie dzialaja zlecenia, w razie czego wyjatek zostanie zmieniony w konstruktorze bazy faktur
			throw new FileNotFoundException("Blad przy wczytywaniu zlecen.");
		}
        Collections.sort(lista);
    }

	protected void wczytaj(File file) throws FileNotFoundException {
        Scanner mainScanner = new Scanner(new BufferedReader(new FileReader(file)));

        int lineCount = 1;
        while (mainScanner.hasNextLine()) {
            try {
				lista.add(parseLine(mainScanner.nextLine()));
            }
            catch (InvalidObjectException e) {
                System.err.println(e.getMessage());
                System.err.println("linijka " + lineCount);
            }
            finally {
                ++lineCount;
            }
        }
    }

    protected abstract T parseLine(String line) throws InvalidObjectException;

    public abstract void zapisz() throws FileNotFoundException;

	public ArrayList<T> getLista() {
		return lista;
	}
}
