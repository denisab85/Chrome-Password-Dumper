package me.matt.chrome.acc.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;

import me.matt.chrome.acc.exception.DatabaseConnectionException;
import me.matt.chrome.acc.exception.DatabaseReadException;
import me.matt.chrome.acc.exception.UnsupportedOperatingSystemException;
import me.matt.chrome.acc.util.Dumper;

import javax.swing.JButton;
import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

public class ChromePassTool {

	private JFrame frmChromePassTool;
	private JTable tableProfiles;
	private JTable tableAccountsPasswords;
	private JScrollPane scrollPaneProfilesTable;
	private JFileChooser chooser;
	private JButton btnExport;
	private JButton btnExportAll;

	private ChromeProfilesTableModel profilesTableModel = new ChromeProfilesTableModel();
	private AccountsPasswordsTableModel accountsPasswordsTableModel = new AccountsPasswordsTableModel();

	private Dumper dumper;
	private JTextField textFieldSearchToken;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Environment.setUIFont(new FontUIResource("Tahoma", Font.PLAIN, 11));
					ChromePassTool window = new ChromePassTool();
					window.frmChromePassTool.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ChromePassTool() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmChromePassTool = new JFrame();
		BorderLayout borderLayout = (BorderLayout) frmChromePassTool.getContentPane().getLayout();
		borderLayout.setHgap(5);
		borderLayout.setVgap(5);
		frmChromePassTool.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				try {
					initializeData();
				} catch (DatabaseConnectionException | DatabaseReadException | InstantiationException | IOException
						| UnsupportedOperatingSystemException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(frmChromePassTool, e1.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		frmChromePassTool.setTitle("ChromePassTool");
		frmChromePassTool.setMinimumSize(new Dimension(450, 300));
		frmChromePassTool.setBounds(100, 100, 600, 450);
		frmChromePassTool.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panelChromeProfiles = new JPanel();
		panelChromeProfiles.setBorder(new TitledBorder(null, "Chrome Profiles", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		frmChromePassTool.getContentPane().add(panelChromeProfiles, BorderLayout.NORTH);
		panelChromeProfiles.setLayout(new BorderLayout(5, 5));

		JPanel panelProfilesButtons = new JPanel();
		panelProfilesButtons.setPreferredSize(new Dimension(100, 100));
		panelProfilesButtons.setBorder(null);
		panelChromeProfiles.add(panelProfilesButtons, BorderLayout.EAST);
		panelProfilesButtons.setLayout(null);

		btnExport = new JButton("Export");
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String profileName = getSelectedProfileName();
				if (profileName != null) {
					String userName = dumper.getProfiles().get(profileName).getUser_name();
					chooser = new JFileChooser();
					chooser.setDialogTitle("Save profile passwords to file - " + userName);
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setCurrentDirectory(new File("."));
					chooser.setSelectedFile(new File(Dumper.getFileNameForProfile(userName)));
					chooser.setAcceptAllFileFilterUsed(false);
					if (chooser.showSaveDialog(frmChromePassTool) == JFileChooser.APPROVE_OPTION) {
						try {
							dumper.saveToFile(profileName, chooser.getSelectedFile());
						} catch (DatabaseConnectionException | DatabaseReadException | UnsupportedOperatingSystemException | IOException e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(frmChromePassTool, e1.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});
		btnExport.setEnabled(false);
		btnExport.setBounds(0, 0, 100, 23);
		panelProfilesButtons.add(btnExport);

		btnExportAll = new JButton("Export All");
		btnExportAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("."));
				chooser.setDialogTitle("Save all passwords in folder");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showSaveDialog(frmChromePassTool) == JFileChooser.APPROVE_OPTION) {
					try {
						dumper.saveAllToFolder(chooser.getSelectedFile());
					} catch (DatabaseConnectionException | DatabaseReadException | UnsupportedOperatingSystemException | IOException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(frmChromePassTool, e1.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		btnExportAll.setEnabled(false);
		btnExportAll.setBounds(0, 34, 100, 23);
		panelProfilesButtons.add(btnExportAll);

		scrollPaneProfilesTable = new JScrollPane();
		scrollPaneProfilesTable.setPreferredSize(new Dimension(100, 100));
		panelChromeProfiles.add(scrollPaneProfilesTable, BorderLayout.CENTER);

		tableProfiles = new JTable();
		tableProfiles.setFillsViewportHeight(true);
		scrollPaneProfilesTable.setViewportView(tableProfiles);
		tableProfiles.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnExport.setEnabled(getSelectedProfileName() != null);
				displayAccountsPasswords();
			}
		});
		tableProfiles.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				btnExport.setEnabled(getSelectedProfileName() != null);
				displayAccountsPasswords();
			}
		});

		JPanel panelAccountsPasswords = new JPanel();
		panelAccountsPasswords.setBorder(new TitledBorder(null, "Accounts & Passwords", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		frmChromePassTool.getContentPane().add(panelAccountsPasswords, BorderLayout.CENTER);
		panelAccountsPasswords.setLayout(new BorderLayout(5, 5));

		JPanel panelAccountsPasswordsControls = new JPanel();
		panelAccountsPasswordsControls.setPreferredSize(new Dimension(100, 100));
		panelAccountsPasswordsControls.setBorder(null);
		panelAccountsPasswords.add(panelAccountsPasswordsControls, BorderLayout.EAST);
		panelAccountsPasswordsControls.setLayout(new BorderLayout(0, 0));

		JPanel panelAccountsPasswordsTopControls = new JPanel();
		panelAccountsPasswordsTopControls.setBorder(null);
		panelAccountsPasswordsControls.add(panelAccountsPasswordsTopControls, BorderLayout.NORTH);
		panelAccountsPasswordsTopControls.setLayout(new BorderLayout(0, 0));

		JButton btnEdit = new JButton("Edit");
		btnEdit.setEnabled(false);
		panelAccountsPasswordsTopControls.add(btnEdit, BorderLayout.NORTH);

		JPanel panelAccountsPasswordsBottomControls = new JPanel();
		panelAccountsPasswordsBottomControls.setBorder(null);
		panelAccountsPasswordsBottomControls.setPreferredSize(new Dimension(100, 30));
		panelAccountsPasswordsControls.add(panelAccountsPasswordsBottomControls, BorderLayout.SOUTH);
		panelAccountsPasswordsBottomControls.setLayout(null);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmChromePassTool.dispose();
			}
		});
		btnCancel.setBounds(0, 6, 100, 23);
		panelAccountsPasswordsBottomControls.add(btnCancel);

		JPanel panelAccountsPasswordsData = new JPanel();
		panelAccountsPasswordsData.setBorder(null);
		panelAccountsPasswords.add(panelAccountsPasswordsData, BorderLayout.CENTER);
		panelAccountsPasswordsData.setLayout(new BorderLayout(0, 5));

		JPanel panelSearch = new JPanel();
		panelSearch.setBorder(null);
		panelAccountsPasswordsData.add(panelSearch, BorderLayout.NORTH);
		panelSearch.setLayout(new BorderLayout(0, 0));

		JLabel lblSearch = new JLabel("Search:");
		panelSearch.add(lblSearch, BorderLayout.WEST);

		JCheckBox chckbxMacthCase = new JCheckBox("macth case");
		panelSearch.add(chckbxMacthCase, BorderLayout.EAST);

		textFieldSearchToken = new JTextField();
		textFieldSearchToken.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accountsPasswordsTableModel.filter(textFieldSearchToken.getText(), chckbxMacthCase.isSelected());
				accountsPasswordsTableModel.fireTableDataChanged();
			}
		});

		panelSearch.add(textFieldSearchToken, BorderLayout.CENTER);
		textFieldSearchToken.setColumns(10);

		JScrollPane scrollPaneAccountsTable = new JScrollPane();
		panelAccountsPasswordsData.add(scrollPaneAccountsTable, BorderLayout.CENTER);

		tableAccountsPasswords = new JTable();
		tableAccountsPasswords.setFillsViewportHeight(true);
		scrollPaneAccountsTable.setViewportView(tableAccountsPasswords);

	}

	private void initializeData()
			throws DatabaseConnectionException, DatabaseReadException, InstantiationException, IOException, UnsupportedOperatingSystemException {
		dumper = new Dumper();

		profilesTableModel.setProfiles(dumper.getProfiles());
		tableProfiles.setModel(profilesTableModel);
		tableProfiles.repaint();

		btnExportAll.setEnabled(!dumper.getProfiles().isEmpty());
	}

	private String getSelectedProfileName() {
		int index = 0;

		try {
			index = tableProfiles.convertRowIndexToModel(tableProfiles.getSelectedRow());
		} catch (IndexOutOfBoundsException e) {
			// Ignore
		}
		if (index >= 0 && index < dumper.getProfiles().size()) {
			return profilesTableModel.getProfiles().keySet().toArray(new String[profilesTableModel.getProfiles().size()])[index];
		}

		return null;
	}

	protected void displayAccountsPasswords() {
		String profileName = getSelectedProfileName();

		if (profileName != null) {
			try {
				accountsPasswordsTableModel.setAccounts(dumper.readDatabase(profileName));
				tableAccountsPasswords.setModel(accountsPasswordsTableModel);
				accountsPasswordsTableModel.fireTableDataChanged();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frmChromePassTool, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			tableAccountsPasswords.setModel(new AccountsPasswordsTableModel());
		}

	}
}
