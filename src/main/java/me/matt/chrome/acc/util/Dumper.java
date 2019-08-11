package me.matt.chrome.acc.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.matt.chrome.acc.exception.ChromeNotFoundException;
import me.matt.chrome.acc.exception.DatabaseConnectionException;
import me.matt.chrome.acc.exception.DatabaseReadException;
import me.matt.chrome.acc.exception.UnsupportedOperatingSystemException;
import me.matt.chrome.acc.wrappers.ChromeAccount;
import me.matt.chrome.acc.wrappers.ChromeDatabase;
import me.matt.chrome.acc.wrappers.ChromeProfile;
import me.matt.chrome.acc.wrappers.LocalState;

public class Dumper {

	public static Dumper dumpAccounts()
			throws DatabaseConnectionException, DatabaseReadException, IOException, UnsupportedOperatingSystemException, InstantiationException {
		final OperatingSystem os = OperatingSystem.getOperatingsystem();
		if (os == OperatingSystem.UNKNOWN) {
			throw new UnsupportedOperatingSystemException(System.getProperty("os.name") + " is not supported by this application!");
		}
		final Path chromeInstall = Paths.get(os.getChromePath());

		final File chromeInfo = new File(chromeInstall.toString(), "Local State");

		if (Files.notExists(chromeInstall)) {
			throw new ChromeNotFoundException("Google chrome installation not found.");
		}

		Map<String, ChromeProfile> profiles;
		ObjectMapper mapper = new ObjectMapper();
		LocalState localState = mapper.readValue(chromeInfo, LocalState.class);
		profiles = localState.getProfile().getInfo_cache();

		final String pathToSave = OperatingSystem.getOperatingsystem().getSavePath();
		final HashMap<File, ChromeAccount[]> accounts = new HashMap<File, ChromeAccount[]>();
		for (final Map.Entry<String, ChromeProfile> profile : profiles.entrySet()) {
			final File loginData = new File(chromeInstall.toString() + File.separator + profile.getKey(), "Login Data");
			accounts.put(new File(pathToSave, "Accounts - " + profile.getValue().getName() + ".txt"), Dumper.readDatabase(loginData));
		}
		if (profiles.size() < 1 || accounts.isEmpty()) {
			throw new InstantiationException("No chrome profiles found!");
		}
		return new Dumper(accounts);
	}

	private static ChromeAccount[] readDatabase(final File data)
			throws DatabaseConnectionException, DatabaseReadException, UnsupportedOperatingSystemException {
		final ChromeDatabase db = ChromeDatabase.connect(data);
		final ArrayList<ChromeAccount> accounts = db.selectAccounts();
		db.close();
		return accounts.toArray(new ChromeAccount[] {});
	}

	private final Map<File, ChromeAccount[]> profiles;

	private Dumper(final Map<File, ChromeAccount[]> profiles) {
		this.profiles = profiles;
	}

	public int getAmountOfProfiles() {
		return profiles.keySet().size();
	}

	public String getDumpLocation() {
		return profiles.keySet().iterator().next().getParent();
	}

	public int getDumpSize() {
		return profiles.values().stream().mapToInt(b -> b.length).sum();
	}

	public boolean saveToFile() throws IOException {
		for (final File file : profiles.keySet()) {
			if (file.exists()) {
				file.delete();
			}
			file.getParentFile().mkdirs();
			file.createNewFile();
			final ChromeAccount[] accounts = profiles.get(file);
			if (accounts.length > 0) {
				final List<String> lines = new ArrayList<>();
				for (final ChromeAccount account : accounts) {
					lines.add("URL: " + account.getURL());
					lines.add("Username: " + account.getUsername());
					lines.add("Password: " + account.getPassword());
					lines.add("");
				}
				lines.remove(lines.size() - 1);
				Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
			}
		}
		return true;
	}
}
