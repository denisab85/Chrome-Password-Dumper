package me.matt.chrome.acc.wrappers;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileData {

	private Map<String, ChromeProfile> info_cache;

	private List<String> last_active_profiles;

	private String last_used;

	private int profiles_created;

}
