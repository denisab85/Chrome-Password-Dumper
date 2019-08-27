package me.matt.chrome.acc.gui;

import java.util.List;
import java.util.stream.Collectors;
import javax.swing.table.AbstractTableModel;
import me.matt.chrome.acc.wrappers.ChromeLogin;

public class AccountsPasswordsTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2532787061932398344L;

	private List<ChromeLogin> accounts;
	private List<ChromeLogin> displayedAccounts;

	private enum columnNames {
		Username, Password, URL
	}

	public void setAccounts(List<ChromeLogin> accounts) {
		this.accounts = accounts;
		this.displayedAccounts = accounts;
	}

	public List<ChromeLogin> getAccounts() {
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
		ChromeLogin account = displayedAccounts.get(rowIndex);

		switch (columnNames.values()[columnIndex]) {
		case Username:
			return account.getUsernameValue();
		case Password:
			return account.getDecryptedPassword();
		case URL:
			return account.getActionUrl();
		default:
			break;
		}
		return null;
	}

	public void filter(String text, boolean caseSensitive) {
		if (caseSensitive) {
			displayedAccounts = accounts.stream().filter(account -> account.getUsernameValue().contains(text)
					|| account.getDecryptedPassword().contains(text) || account.getActionUrl().contains(text)).collect(Collectors.toList());
		} else {
			displayedAccounts = accounts.stream()
					.filter(account -> account.getUsernameValue().toLowerCase().contains(text.toLowerCase())
							|| account.getDecryptedPassword().toLowerCase().contains(text.toLowerCase())
							|| account.getActionUrl().toLowerCase().contains(text.toLowerCase()))
					.collect(Collectors.toList());
		}
	}

}
