package zlecenia;

import framework.*;

import java.io.*;
import java.util.regex.*;

public class BazaZlecen extends Data<Zlecenie> {

    public BazaZlecen(File file) throws FileNotFoundException {
    	super(file);
    }

    public Zlecenie parseLine(String line) throws InvalidObjectException {
        String regex = "(\\d+)\\t(.+)\\t(\\d+[.,]\\d{2})((\\t)(.+))?";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(line);

        if (m.matches()) {
            String numer = m.group(1);
            String nazwa = m.group(2);
            String wartosc = m.group(3);
            String id = m.group(6);

            //TODO: strzelic wyjatek
            if(!this.zawieraZlecenie(Integer.parseInt(numer))) {
            	if(id == null) {
					return Zlecenie.validate(numer, nazwa, wartosc);
				}
				else {
            		return Zlecenie.validate(numer, nazwa, wartosc, id);
				}
			}

        }

        throw new InvalidObjectException("Blad danych.");
    }

    public void zapisz() throws FileNotFoundException {
        Writer mainWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(plik)));

		for (Zlecenie z: lista) {
            try {
                mainWriter.write(String.valueOf(z.getNumer())); //problem z liczbami traktowanymi jak znaki
                mainWriter.write("\t");
                mainWriter.write(z.getNazwa());
                mainWriter.write("\t");
                mainWriter.write(z.getWartosc().toString());

                if(z.getFakturaWykorzystujaca() != null) {
					mainWriter.write("\t");
					mainWriter.write(z.getFakturaWykorzystujaca().toString());
				}

                mainWriter.write("\n");
                mainWriter.flush();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

	/**
	 * Metoda sprawdza czy zlecenie o podanym numerze znajduje sie w bazie.
	 *
	 * @param numer - numer zlecenia, ktorego szukamy
	 * @return true, jesli zlecenie o podanym numerze jest juz w bazie,
	 * 		   false, jesli zlecenia niema
	 */
	public boolean zawieraZlecenie(int numer) {
    	for(Zlecenie z: lista) {
    		if(z.getNumer() == numer) return true;
		}

		return false;
	}
}
