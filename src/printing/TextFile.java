package printing;

import faktury.*;
import zlecenia.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.time.format.*;
import java.util.*;
import java.util.regex.*;

public class TextFile {
	private static File SINGLE_ROW_TEMPLATE = new File("templates/jeden wiersz template.txt");
	private static File INVOICE_TEMPLATE = new File("templates/template.htm");

	private StringBuilder file;

	//TODO: stale tagi oznaczajace pola w fakturze

	private TextFile(File file) {
		read(file);
	}

	private TextFile() {
		this.file = new StringBuilder();
	}

	private void read(File file) {
		this.file = new StringBuilder();

		Scanner scanner;
		try {
			scanner = new Scanner(file, "UTF-8");
		}
		catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return;
		}

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();

			this.file.append(line);
			this.file.append("\n");
		}
	}

	private StringBuilder getFile() {
		return file;
	}

	private void replace(String old, String next) {
		Pattern p = Pattern.compile(old);
		Matcher m = p.matcher(file.toString());
		StringBuilder sb = new StringBuilder();
		while (m.find()) {
			m.appendReplacement(sb, next);
		}
		m.appendTail(sb);

		file = sb;
	}

	private void replace(String old, TextFile next) {
		replace(old, next.getFile().toString());
	}

	private void append(String s) {
		file.append(s);
	}

	private void append(TextFile file) {
		append(file.getFile().toString());
	}

	public static void save(TextFile plik, String filename) {
		ArrayList<String> temp = new ArrayList<>();
		temp.add(plik.getFile().toString());
		try {
			Files.write(Paths.get(filename), temp, StandardCharsets.UTF_8);
		}
		catch (IOException e) {
			e.printStackTrace();
			//TODO: lepiej obslugiwac wyjatki
		}
	}

	public static TextFile generateFaktura(Faktura faktura) {
		TextFile plik = new TextFile(INVOICE_TEMPLATE);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");

		String dataW = faktura.getDataWystawienia().format(formatter); //TODO: formatowac date
		String id = faktura.getId().toString();
		String dataS = faktura.getDataSprzedazy().format(formatter);
		String dataP = faktura.getDataPlatnosci().format(formatter);

		TextFile zlecenia = generateRows(faktura.getZlecenia());

		String sumaN = faktura.getWartosc().toString();
		String sumaV = faktura.getSumaVat().toString();
		String sumaB = faktura.getSumaBrutto().toString();

		plik.replace("dataWystawienia", dataW);
		plik.replace("idFaktury", id);
		plik.replace("dataSprzedazy",dataS);
		plik.replace("dataPlatnosci", dataP);
		plik.replace("miejsceDoWstawieniaWierszy", zlecenia);
		plik.replace("sumaNetto", sumaN);
		plik.replace("sumaVat", sumaV);
		plik.replace("sumaBrutto", sumaB);

		return plik;
	}

	private static TextFile generateRows(ArrayList<Zlecenie> lista) {
		TextFile wiersze = new TextFile();

		for(int i = 0; i<lista.size(); ++i) {
			wiersze.append(generateRow(i, lista.get(i)));
			wiersze.append("\n");
		}

		return wiersze;
	}

	private static TextFile generateRow(int index, Zlecenie zlecenie) {
		TextFile wiersz = new TextFile(SINGLE_ROW_TEMPLATE);

		String lpZ = String.valueOf(index + 1);
		String nazwaZ = String.valueOf(zlecenie.getNumer()) + " " + zlecenie.getNazwa();
		String wartoscZ = zlecenie.getWartosc().toString().replace('.', ',');
		String vatZ = zlecenie.getVat().toString().replace('.', ',');

		wiersz.replace("numerZlecenia", lpZ);
		wiersz.replace("nazwaZlecenia", nazwaZ);
		wiersz.replace("wartoscZlecenia", wartoscZ);
		wiersz.replace("wartoscVat", vatZ);

		return wiersz;
	}
}
