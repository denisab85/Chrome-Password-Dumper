package me.matt.chrome.acc.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalState {

	private ProfileData profile;

}
