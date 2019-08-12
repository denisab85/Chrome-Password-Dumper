package me.matt.chrome.acc.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import me.matt.chrome.acc.exception.ChromeNotFoundException;
import me.matt.chrome.acc.exception.DatabaseConnectionException;
import me.matt.chrome.acc.exception.DatabaseReadException;
import me.matt.chrome.acc.exception.UnsupportedOperatingSystemException;
import me.matt.chrome.acc.wrappers.ChromeAccount;
import me.matt.chrome.acc.wrappers.ChromeDatabase;
import me.matt.chrome.acc.wrappers.ChromeProfile;
import me.matt.chrome.acc.wrappers.LocalState;

public class Dumper {

	@Getter
	private final Map<String, ChromeProfile> profiles;
	private final Path chromeInstall;
	private final OperatingSystem os = OperatingSystem.getOperatingsystem();

	@Getter
	private File dumpLocation;

	public Dumper()
			throws DatabaseConnectionException, DatabaseReadException, IOException, UnsupportedOperatingSystemException, InstantiationException {
		final OperatingSystem os = OperatingSystem.getOperatingsystem();
		if (os == OperatingSystem.UNKNOWN) {
			throw new UnsupportedOperatingSystemException(System.getProperty("os.name") + " is not supported by this application!");
		}

		chromeInstall = Paths.get(os.getChromePath());

		if (Files.notExists(chromeInstall)) {
			throw new ChromeNotFoundException("Google chrome installation not found.");
		}

		final File chromeInfo = new File(chromeInstall.toFile(), "Local State");

		ObjectMapper mapper = new ObjectMapper();
		LocalState localState = mapper.readValue(chromeInfo, LocalState.class);
		profiles = localState.getProfile().getInfo_cache();

		if (profiles.isEmpty()) {
			throw new InstantiationException("No chrome profiles found!");
		}

	}

	public ArrayList<ChromeAccount> readDatabase(String profileName)
			throws DatabaseConnectionException, DatabaseReadException, UnsupportedOperatingSystemException {
		final File data = new File(chromeInstall.toString() + File.separator + profileName, "Login Data");
		final ChromeDatabase db = ChromeDatabase.connect(data);
		final ArrayList<ChromeAccount> accounts = db.selectAccounts();
		db.close();
		return accounts;
	}

	public int saveAllToDefaultFolder() throws DatabaseConnectionException, DatabaseReadException, IOException, UnsupportedOperatingSystemException {
		return saveAllToFolder(null);
	}

	public static String getFileNameForProfile(String profileName) {
		return "Accounts - " + profileName + ".txt";
	}

	public int saveAllToFolder(File dumpLocation)
			throws IOException, DatabaseConnectionException, DatabaseReadException, UnsupportedOperatingSystemException {
		int totalPasswords = 0;

		if (dumpLocation == null) {
			dumpLocation = new File(os.getSavePath());
		}

		for (final Map.Entry<String, ChromeProfile> profile : profiles.entrySet()) {
			File targetFile = new File(dumpLocation, getFileNameForProfile(profile.getValue().getUser_name()));
			totalPasswords += saveToFile(profile.getKey(), targetFile);
		}
		return totalPasswords;
	}

	public int saveToFile(String profileName, File targetFile)
			throws DatabaseConnectionException, DatabaseReadException, UnsupportedOperatingSystemException, IOException {
		if (targetFile.exists()) {
			targetFile.delete();
		}
		targetFile.getParentFile().mkdirs();
		targetFile.createNewFile();

		final ArrayList<ChromeAccount> accounts = readDatabase(profileName);
		if (accounts.size() > 0) {
			final List<String> lines = new ArrayList<>();
			for (final ChromeAccount account : accounts) {
				lines.add("URL: " + account.getURL());
				lines.add("Username: " + account.getUsername());
				lines.add("Password: " + account.getPassword());
				lines.add("");
			}
			lines.remove(lines.size() - 1);
			Files.write(targetFile.toPath(), lines, StandardCharsets.UTF_8);
		}
		return accounts.size();

	}
}
