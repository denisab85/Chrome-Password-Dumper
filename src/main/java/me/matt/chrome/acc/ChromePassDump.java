package me.matt.chrome.acc;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import me.matt.chrome.acc.exception.DatabaseException;
import me.matt.chrome.acc.exception.UnsupportedOperatingSystemException;
import me.matt.chrome.acc.util.Dumper;

public class ChromePassDump {

	public static void main(final String[] args)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		String message;
		final boolean silent = args.length > 0 ? args[0].equalsIgnoreCase("silent") : false;
		try {
			final Dumper dumper = new Dumper();
			int totalPasswords = dumper.saveAllToDefaultFolder();
			message = totalPasswords > 0
					? "Finished dumping " + totalPasswords + " accounts from " + dumper.getProfiles().size() + " profiles to "
							+ dumper.getDumpLocation()
					: dumper.getDumpLocation() + " file already exists. Please rename or remove it before trying again.";
		} catch (DatabaseException | IOException | UnsupportedOperatingSystemException e) {
			e.printStackTrace();
			message = e.getMessage();
		}
		if (!silent) {
			JOptionPane.showMessageDialog(null, message);
		}
	}
}
