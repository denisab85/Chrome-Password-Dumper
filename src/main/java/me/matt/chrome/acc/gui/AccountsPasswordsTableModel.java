package me.matt.chrome.acc.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.table.AbstractTableModel;

import me.matt.chrome.acc.wrappers.ChromeAccount;

public class AccountsPasswordsTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2532787061932398344L;

	private List<ChromeAccount> accounts;
	private List<ChromeAccount> displayedAccounts;

	private enum columnNames {
		Username, Password, URL
	}

	public void setAccounts(ArrayList<ChromeAccount> accounts) {
		this.accounts = accounts;
		this.displayedAccounts = accounts;
	}

	public List<ChromeAccount> getAccounts() {
		return displayedAccounts;
	}

	@Override
	public int getRowCount() {
		return displayedAccounts.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.values().length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columnNames.values()[columnIndex].name();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (displayedAccounts == null) {
			return null;
		}
		ChromeAccount account = displayedAccounts.get(rowIndex);

		switch (columnNames.values()[columnIndex]) {
		case Username:
			return account.getUsername();
		case Password:
			return account.getPassword();
		case URL:
			return account.getURL();
		default:
			break;
		}
		return null;
	}

	public void filter(String text, boolean caseSensitive) {
		if (caseSensitive) {
			displayedAccounts = accounts.stream().filter(
					account -> account.getUsername().contains(text) || account.getPassword().contains(text) || account.getURL().contains(text))
					.collect(Collectors.toList());
		} else {
			displayedAccounts = accounts.stream()
					.filter(account -> account.getUsername().toLowerCase().contains(text.toLowerCase())
							|| account.getPassword().toLowerCase().contains(text.toLowerCase())
							|| account.getURL().toLowerCase().contains(text.toLowerCase()))
					.collect(Collectors.toList());
		}
	}

}
