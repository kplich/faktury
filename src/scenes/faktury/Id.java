package scenes.faktury;

import java.io.*;
import java.util.regex.*;

public class Id implements Comparable<Id> {
	private int numer;
	private int rok;

	public Id(int numer, int rok) {
		this.numer = numer;
		this.rok = rok;
	}

	private int getNumer() {
		return numer;
	}

	private int getRok() {
		return rok;
	}

	public Id nextNumber() {
		return new Id(this.getNumer() + 1, this.getRok());
	}

	@Override
	public String toString() {
		return this.numer + "/" + this.rok;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Id) {
			Id temp = (Id) obj;

			return temp.getNumer() == this.numer && temp.getRok() == this.rok;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return numer * numer + rok * 31;
	}

	@Override
	public int compareTo(Id o) {
		if(this.numer == o.getNumer()) {
			return Integer.compare(this.rok, o.getRok());
		}
		else {
			return Integer.compare(this.numer, o.getNumer());
		}
	}

	public static Id validate(String string) throws InvalidObjectException {
		String regex = "^(\\d+)/(\\d{4})$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(string);

		if (matcher.matches()) {
			int numer = Integer.parseInt(matcher.group(1));
			int rok = Integer.parseInt(matcher.group(2));

			return new Id(numer, rok);
		}
		else throw new InvalidObjectException("Nieprawidlowy numer scenes.faktury");
	}
}
