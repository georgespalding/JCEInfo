package se.op.net.authenticator;

import java.net.PasswordAuthentication;

public class BasicPasswordAuthenticationProvider implements PasswordAuthenticationProvider {
	private final PasswordAuthentication passwordAuthentication;

	public BasicPasswordAuthenticationProvider(String username,char[] password){
		passwordAuthentication = new PasswordAuthentication(username, password);
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication(PasswordAuthenticationRequest request) {
		return passwordAuthentication;
	}

}
