package me.matt.chrome.acc.util;

import java.io.File;

import me.matt.chrome.acc.ChromePassDump;

public enum OperatingSystem {

	WINDOWS(System.getProperty("user.home") + File.separator + "AppData\\Local\\Google\\Chrome\\User Data\\",
			ChromePassDump.class.getProtectionDomain().getCodeSource().getLocation().toString().replace("%20", " ").replace("file:/", "").replace("/",
					File.separator)),
	MAC(System.getProperty("user.home") + File.separator + "Library/Application Support/Google/Chrome/",
			ChromePassDump.class.getProtectionDomain().getCodeSource().getLocation().toString().replace("%20", " ").replace("file:", "").replace("/",
					File.separator)),
	LINUX(System.getProperty("user.home") + File.separator + ".config/google-chrome/", ChromePassDump.class.getProtectionDomain().getCodeSource()
			.getLocation().toString().replace("%20", " ").replace("file:", "").replace("/", File.separator)),
	UNKNOWN("", "");

	public static OperatingSystem getOperatingsystem() {
		final String os = System.getProperty("os.name");
		if (os.contains("Mac")) {
			return MAC;
		} else if (os.contains("Windows")) {
			return WINDOWS;
		} else if (os.contains("Linux")) {
			return LINUX;
		} else {
			return UNKNOWN;
		}
	}

	private String path;

	private String runningPath;

	OperatingSystem(final String path, final String runningPath) {
		this.path = path;
		this.runningPath = runningPath;
	}

	public String getChromePath() {
		return path;
	}

	public String getSavePath() {
		return runningPath.substring(0, runningPath.lastIndexOf(File.separatorChar) + 1) + "Accounts";
	}

}
