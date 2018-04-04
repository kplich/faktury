package faktury;

import framework.*;
import zlecenia.*;

import java.io.*;
import java.util.*;

public class BazaFaktur extends Data<Faktura> {
    private ArrayList<Zlecenie> listaZlecen;
    private Id ostatniNumer;

    public BazaFaktur(File file, ArrayList<Zlecenie> listaZlecen) throws FileNotFoundException {
		plik = file;
		this.listaZlecen = listaZlecen;

		try {
			wczytaj(plik); //tu moze zostac rzucony FNFException
		}
		catch (FileNotFoundException e) {
			throw new FileNotFoundException("Blad przy wczytywaniu faktur.");
		}

		Collections.sort(lista);

		if(lista.size() == 0) ostatniNumer = new Id(0, 2018);
		else ostatniNumer = getLista().get(getLista().size() - 1).getId(); //TODO: zmienic na ostatni najwiekszy numer
    }

    @Override
    public Faktura parseLine(String line) throws InvalidObjectException {
        String[] fields = line.split("\\t");

        return Faktura.validate(fields, listaZlecen);
    }

    public void zapisz() throws FileNotFoundException {
        Writer mainWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(plik)));

		for (Faktura f: lista) {
            try {
                mainWriter.write(f.toStringSave());
                mainWriter.write("\n");
                mainWriter.flush();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public boolean zawieraFakture(Id id) {
    	for(Faktura f: lista) {
    		if(f.getId().equals(id)) return true;
		}

		return false;
	}

	public Id getOstatniNumer() {
		return ostatniNumer;
	}

	public void incrementOstatniNumer() {
    	ostatniNumer = ostatniNumer.nextNumber();
	}
}
