package me.matt.chrome.acc.wrappers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.matt.chrome.acc.util.ChromeSecurity;
import me.matt.chrome.acc.util.OperatingSystem;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "logins")
public class ChromeLogin {

	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "origin_url")
	private String originUrl;

	@Column(name = "action_url")
	private String actionUrl;

	@Column(name = "username_element")
	private String usernameElement;

	@Column(name = "username_value")
	private String usernameValue;

	@Column(name = "password_element")
	private String passwordElement;

	@Column(name = "password_value")
	private byte[] passwordValue;

	@Column(name = "submit_element")
	private String submitElement;

	@Column(name = "signon_realm")
	private String signonRealm;

	@Column(name = "preferred")
	private Integer preferred;

	@Column(name = "date_created")
	private Integer dateCreated;

	@Column(name = "blacklisted_by_user")
	private Integer blacklistedByUser;

	@Column(name = "scheme")
	private Integer scheme;

	@Column(name = "password_type")
	private Integer passwordType;

	@Column(name = "times_used")
	private Integer timesUsed;

	@Column(name = "form_data")
	private byte[] formData;

	@Column(name = "date_synced")
	private Integer dateSynced;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "icon_url")
	private String iconUrl;

	@Column(name = "federation_url")
	private String federationUrl;

	@Column(name = "skip_zero_click")
	private Integer skipZeroClick;

	@Column(name = "generation_upload_status")
	private Integer generationUploadStatus;

	@Column(name = "possible_username_pairs")
	private byte[] possibleUsernamePairs;

	public String getDecryptedPassword() {
		switch (OperatingSystem.getOperatingsystem()) {
		case WINDOWS:
			return ChromeSecurity.getWin32Password(passwordValue);
		case MAC:
			return ChromeSecurity.getOSXKeychainPasswordAsAdmin(actionUrl);
		default:
			return null;
		}
	}
}
