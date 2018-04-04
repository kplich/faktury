package faktury;

import javafx.beans.property.*;
import zlecenia.*;

import java.io.*;
import java.math.*;
import java.time.*;
import java.util.*;

public class Faktura implements Comparable<Faktura> {
	private SimpleObjectProperty<Id> id;
    private SimpleIntegerProperty iloscDni;
    private SimpleObjectProperty<LocalDate> dataWystawienia;
    private SimpleObjectProperty<LocalDate> dataSprzedazy;
    private SimpleObjectProperty<BigDecimal> wartosc;
	private SimpleObjectProperty<ArrayList<Zlecenie>> zlecenia;

	private LocalDate dataPlatnosci;
	private BigDecimal sumaVat;
	private BigDecimal sumaBrutto;

    private Faktura(Id id, int iloscDni, LocalDate dataWystawienia, LocalDate dataSprzedazy) {

    	//ustawiamy wlasnosci ktore trzeba po prostu podac
        this.id = new SimpleObjectProperty<>(id);
        this.iloscDni = new SimpleIntegerProperty(iloscDni);
        this.dataWystawienia = new SimpleObjectProperty<>(dataWystawienia);
        this.dataSprzedazy = new SimpleObjectProperty<>(dataSprzedazy);
        this.zlecenia = new SimpleObjectProperty<>(new ArrayList<>());
		this.wartosc = new SimpleObjectProperty<>(new BigDecimal(BigInteger.ZERO));

		this.dataPlatnosci = dataWystawienia.plusDays(iloscDni);
	}

    @Override
    public String toString() {
        return "FV" + id.get();
    }

    public String toStringSave() {
    	String temp =   id.get() + "\t"
					  + String.valueOf(iloscDni.get()) + "\t"
					  + dataWystawienia.get().toString() + "\t"
					  + dataSprzedazy.get().toString();

		return temp;
	}

	public void uwolnijZlecenia() {
    	for(Zlecenie z: getZlecenia()) {
    		z.setFakturaWykorzystujaca(null);
		}
	}

	public static Faktura validate(String[] fields, ArrayList<Zlecenie> listaZlecen) throws InvalidObjectException {
		Id id;
		int iloscDni;
		LocalDate dataWystawienia;
		LocalDate dataSprzedazy;

		id = Id.validate(fields[0]);

		try {
			iloscDni = Integer.parseInt(fields[1]);
			dataWystawienia = LocalDate.parse(fields[2]);
			dataSprzedazy = LocalDate.parse(fields[3]);
		}
		catch (NumberFormatException e) {
    		System.err.println(e.getMessage());
    		e.printStackTrace();
    		throw new InvalidObjectException("Blad danych.");
		}

		Faktura temp = new Faktura(id, iloscDni, dataWystawienia, dataSprzedazy);

		temp.znajdzZlecenia(listaZlecen);
		temp.przeliczWartosc();

		return temp;
	}

	public static Faktura validate(String[] fields, ArrayList<Zlecenie> listaZlecen, ArrayList<Faktura> listaFaktur)
	throws InvalidObjectException {
    	Faktura temp = validate(fields, listaZlecen);

    	for(Faktura f: listaFaktur) {
    		if(temp.getId().equals(f.getId())) throw new InvalidObjectException("Faktura o takim numerze juz istnieje");
		}

		return temp;
	}

	//
	private void znajdzZlecenia(ArrayList<Zlecenie> globalList) {
		for (Zlecenie z: globalList) {
			if(z.getFakturaWykorzystujaca() != null) {
				if (z.getFakturaWykorzystujaca().equals(this.getId())) {
					this.getZlecenia().add(z);
				}
			}
		}
	}

	public void przeliczWartosc() {
		for (Zlecenie z: getZlecenia()) {
			BigDecimal tempWartosc = getWartosc().add(z.getWartosc());
			this.wartosc = new SimpleObjectProperty<>(tempWartosc);
		}

		sumaVat = getWartosc().multiply(new BigDecimal("0.23")).setScale(2, RoundingMode.UP);
		sumaBrutto = getWartosc().add(sumaVat);
	}

	public Id getId() {
		return id.get();
	}

	public int getIloscDni() {
        return iloscDni.get();
    }

    public LocalDate getDataWystawienia() {
        return dataWystawienia.get();
    }

    public LocalDate getDataSprzedazy() {
        return dataSprzedazy.get();
    }

    public BigDecimal getWartosc() {
        return wartosc.get();
    }

    public ArrayList<Zlecenie> getZlecenia() {
        return zlecenia.get();
    }

	public LocalDate getDataPlatnosci() {
		return dataPlatnosci;
	}

	public BigDecimal getSumaVat() {
		return sumaVat;
	}

	public BigDecimal getSumaBrutto() {
		return sumaBrutto;
	}

	@Override
	public int compareTo(Faktura o) {
		return this.getId().compareTo(o.getId());
	}
}
