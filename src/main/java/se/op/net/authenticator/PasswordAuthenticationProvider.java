package se.op.net.authenticator;

import java.net.PasswordAuthentication;

public interface PasswordAuthenticationProvider {
	public PasswordAuthentication getPasswordAuthentication(PasswordAuthenticationRequest request);
}
