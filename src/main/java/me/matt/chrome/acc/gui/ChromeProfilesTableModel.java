package me.matt.chrome.acc.gui;

import java.util.Map;

import javax.swing.table.AbstractTableModel;

import lombok.Getter;
import lombok.Setter;
import me.matt.chrome.acc.wrappers.ChromeProfile;

public class ChromeProfilesTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3096142758826604498L;
	@Getter
	@Setter
	private Map<String, ChromeProfile> profiles;

	public enum ColumnNames {
		Name, User, Email
	}

	@Override
	public int getRowCount() {
		return profiles.size();
	}

	@Override
	public int getColumnCount() {
		return ColumnNames.values().length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return ColumnNames.values()[columnIndex].name();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (profiles == null) {
			return null;
		}

		ChromeProfile profile = profiles.values().toArray(new ChromeProfile[profiles.size()])[rowIndex];

		switch (ColumnNames.values()[columnIndex]) {
		case Name:
			return profile.getName();
		case User:
			return profile.getGaia_name();
		case Email:
			return profile.getUser_name();
		default:
			break;
		}
		return null;
	}

}
