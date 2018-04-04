package zlecenia;

import faktury.*;
import javafx.beans.property.*;

import java.io.*;
import java.math.*;
import java.util.regex.*;

public class Zlecenie implements Comparable<Zlecenie> {
    private SimpleIntegerProperty numer;
    private SimpleStringProperty nazwa; //TODO: dokladniejsze oznaczanie miast w zleceniach (nowa klasa Miasto)
	private SimpleObjectProperty<BigDecimal> wartosc;

	@Deprecated
    private SimpleBooleanProperty uzyty;

    private BigDecimal vat;

    private BigDecimal brutto;
    private Id fakturaWykorzystujaca;

	/**
	 * Konstruktor prywatny - zlecenia mozna stworzyc jedynie poprzez metode validate();
	 * Na podstawie danych oblicza takze wartosc podatku, oraz wartosc brutto.
	 *
	 * @param numer - numer zlecenia
	 * @param nazwa - opis zlecenia
	 * @param wartosc - wartosc zlecenia - liczba z dwoma miejscami po przecinku
	 */
	private Zlecenie(int numer, String nazwa, BigDecimal wartosc) {
        this.numer = new SimpleIntegerProperty(numer);
        this.nazwa = new SimpleStringProperty(nazwa);
		this.wartosc = new SimpleObjectProperty<>(wartosc);
        this.uzyty = new SimpleBooleanProperty(false);
        this.fakturaWykorzystujaca = null;

        vat = wartosc.multiply(new BigDecimal("0.23")).setScale(2, RoundingMode.HALF_UP);
        brutto = wartosc.add(vat);
    }

    public int getNumer() {
        return numer.get();
    }

    public String getNazwa() {
        return nazwa.get();
    }

	public BigDecimal getWartosc() {
        return wartosc.get();
    }

    public boolean getUzyty() {
        return uzyty.get();
    }

    public void setUzyty(boolean uzyty) {
        this.uzyty.set(uzyty);
    }

	public BigDecimal getVat() {
		return vat;
	}

	public BigDecimal getBrutto() {
		return brutto;
	}

	public Id getFakturaWykorzystujaca() {
		return fakturaWykorzystujaca;
	}

	public void setFakturaWykorzystujaca(Id fakturaWykorzystujaca) {
		this.fakturaWykorzystujaca = fakturaWykorzystujaca;
	}

	/**
	 * Metoda sprawdzajaca poprawnosc danych opisujacych zlecenie
	 *
	 * @param numer - string zawierajacy numer zlecenia - musi parsowac sie jako int
	 * @param nazwa - opis zlecenia
	 * @param wartosc - liczba z dwoma miejscami po przecinku
	 * @return obiekt typu Zlecenie, zainicjalizowany podanymi danymi
	 * @throws InvalidObjectException
	 */
	public static Zlecenie validate(String numer, String nazwa, String wartosc) throws InvalidObjectException {
		//sprawdzamy poprawnosc numeru
		int num;
		try {
			num = Integer.parseInt(numer);
		}
		catch (NumberFormatException e) {
			throw new InvalidObjectException("Nieprawidlowy numer zlecenia.");
		}

		String regex = "^\\d+[,.]\\d{2}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(wartosc);

		//sprawdzamy czy wprowadzono poprawna wartosc zlecenia
		if (!matcher.matches()) throw new InvalidObjectException("Nieprawidlowa wartosc zlecenia");

		//jesli potrzeba, zamieniamy separator dziesietny na kropke (do zamiany na BigDecimal)
		wartosc = wartosc.replace(',', '.');

		return new Zlecenie(num, nazwa, new BigDecimal(wartosc));
	}

	public static Zlecenie validate(String numer, String nazwa, String wartosc, String id) throws InvalidObjectException {
		Zlecenie temp = validate(numer, nazwa, wartosc);
		Id temp_id = Id.validate(id);

		temp.setFakturaWykorzystujaca(temp_id);

		return temp;
	}

    @Override
    public String toString() {
        return this.getNumer() + ", " + this.getNazwa() + ", " + this.getWartosc();
    }

    @Override
    public boolean equals(Object obj) {
        try {
            boolean equalNumer = ((Zlecenie) obj).getNumer() == this.getNumer();
            boolean equalNazwa = ((Zlecenie) obj).getNazwa().equals(this.getNazwa());
            boolean equalWartosc = ((Zlecenie) obj).getWartosc().equals(this.getWartosc());
            return (equalNumer && equalNazwa && equalWartosc);
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getNumer() + getNazwa().hashCode() + getWartosc().hashCode();
    }

	@Override
	public int compareTo(Zlecenie o) {
		return Integer.compare(this.getNumer(), o.getNumer());
	}
}
