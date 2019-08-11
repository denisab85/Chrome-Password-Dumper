package me.matt.chrome.acc.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChromeProfile {

	private double active_time;
	private String avatar_icon;
	private boolean background_apps;
	private String gaia_given_name;
	private String gaia_id;
	private String gaia_name;
	private String gaia_picture_file_name;
	private boolean is_auth_error;
	private boolean is_ephemeral;
	private boolean is_omitted_from_profile_list;
	private boolean is_using_default_avatar;
	private boolean is_using_default_name;
	private String local_auth_credentials;
	private String managed_user_id;
	private int metrics_bucket_index;
	private String name;
	private String shortcut_name;
	private boolean use_gaia_picture;
	private String user_name;

}
