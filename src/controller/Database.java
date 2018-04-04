package controller;

import faktury.*;
import zlecenia.*;

import java.io.*;
import java.util.*;

public class Database {
	private BazaZlecen bazaZlecen;
	private BazaFaktur bazaFaktur;

	Database(File zlecenia, File faktury) throws FileNotFoundException {
		this.bazaZlecen = new BazaZlecen(zlecenia);

		this.bazaFaktur = new BazaFaktur(faktury, bazaZlecen.getLista());
	}

	public ArrayList<Zlecenie> getZlecenia() {
		return bazaZlecen.getLista();
	}

	public ArrayList<Zlecenie> getNieuzyteZlecenia() {
		ArrayList<Zlecenie> result = new ArrayList<>();

		for (Zlecenie z: this.getZlecenia()) {
			if (z.getFakturaWykorzystujaca() == null) result.add(z);
		}
		return result;
	}

	public Zlecenie getZlecenie(int numer) throws IllegalArgumentException {
		for(Zlecenie z: bazaZlecen.getLista()) {
			if(z.getNumer() == numer) return z;
		}

		throw new IllegalArgumentException("Nie istnieje zlecenie o takim numerze");
	}

	//TODO: zabezpieczyc metody ponizej
	public void usunZlecenie(int numer) throws IllegalArgumentException {
		Zlecenie toRemove = getZlecenie(numer);

		bazaZlecen.getLista().remove(toRemove);
	}

	public void usunZlecenie(Zlecenie z) {
		bazaZlecen.getLista().remove(z);
	}

	public void zamienZlecenie(int numer, Zlecenie nowe) {
		int index = bazaZlecen.getLista().indexOf(getZlecenie(numer));
		usunZlecenie(numer);
		dodajZlecenie(index, nowe);
	}

	public void dodajZlecenie(Zlecenie z) {
		if(bazaZlecen.zawieraZlecenie(z.getNumer())) throw new IllegalArgumentException("Zlecenie o takim numerze juz istnieje.");

		bazaZlecen.getLista().add(z);
		Collections.sort(bazaZlecen.getLista());
	}

	//TODO: wtf, dodawac zlecenie w konkretnym miejscu tylko po to, zeby zaraz znow posortowac liste?
	public void dodajZlecenie(int index, Zlecenie z) {
		if(bazaZlecen.zawieraZlecenie(z.getNumer())) throw new IllegalArgumentException("Zlecenie o takim numerze juz istnieje.");

		bazaZlecen.getLista().add(index, z);
		Collections.sort(bazaZlecen.getLista());
	}

	public void zapiszZlecenia() throws FileNotFoundException {
		bazaZlecen.zapisz();
	}

	public ArrayList<Faktura> getFaktury() {
		return bazaFaktur.getLista();
	}

	public Faktura getFaktura(Id id) {
		for (Faktura f: bazaFaktur.getLista()) {
			if(f.getId().equals(id)) return f;
		}

		throw new IllegalArgumentException("Nie istnieje zlecenie o takim numerze");
	}

	public void usunFakture(Id id) {
		Faktura toRemove = getFaktura(id);

		toRemove.uwolnijZlecenia();

		bazaFaktur.getLista().remove(toRemove);
	}

	public void usunFakture(Faktura f) {
		f.uwolnijZlecenia();

		bazaFaktur.getLista().remove(f);
	}

	public void usunFakture(int index) {
		bazaFaktur.getLista().get(index).uwolnijZlecenia();;
		bazaFaktur.getLista().remove(index);
	}

	public void zamienFakture(Id id, Faktura nowa) {
		int index = bazaFaktur.getLista().indexOf(getFaktura(id));
		usunFakture(id);
		dodajFakture(index, nowa);
	}

	public void dodajFakture(Faktura f) {
		if(bazaFaktur.zawieraFakture(f.getId())) throw new IllegalArgumentException("Faktura o takim numerze juz istnieje.");
		bazaFaktur.getLista().add(f);
		Collections.sort(bazaFaktur.getLista());
	}

	//TODO: ten sam mindfuck, co wyzej - sortowanie po dodaniu???
	public void dodajFakture(int index, Faktura f) {
		if (bazaFaktur.zawieraFakture(f.getId())) throw new IllegalArgumentException("Faktura o takim numerze juz istnieje.");
		bazaFaktur.getLista().add(index, f);
		Collections.sort(bazaFaktur.getLista());
	}

	public void zapiszFaktury() throws FileNotFoundException {
		bazaFaktur.zapisz();
	}

	public Id getOstatniNumer() {
		return bazaFaktur.getOstatniNumer();
	}

	public void incrementOstatniNumer() {
		bazaFaktur.incrementOstatniNumer();
	}
}
