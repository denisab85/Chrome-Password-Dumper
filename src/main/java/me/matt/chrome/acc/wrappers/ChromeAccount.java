package me.matt.chrome.acc.wrappers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChromeAccount {

	private final String username;
	private final String password;
	private final String URL;

}
