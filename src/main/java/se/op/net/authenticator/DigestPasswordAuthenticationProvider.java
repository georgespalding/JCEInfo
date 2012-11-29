package se.op.net.authenticator;

import java.net.PasswordAuthentication;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestPasswordAuthenticationProvider implements
		PasswordAuthenticationProvider {
	private final PasswordAuthentication passwordAuthentication;

	public DigestPasswordAuthenticationProvider(String username,
			byte[] passwordAsBytes, int iterations, String digestAlg)
			throws NoSuchAlgorithmException {
		this(username, passwordAsBytes, iterations, MessageDigest
				.getInstance(digestAlg));
	}

	public DigestPasswordAuthenticationProvider(String username,
			byte[] passwordAsBytes, int iterations, MessageDigest digest) {
		long millis = System.currentTimeMillis();
		long nanos = System.nanoTime();

		for (int i = 0; i < iterations; i++) {
			digest.reset();
			passwordAsBytes = digest.digest(passwordAsBytes);
		}

		char[] password = toHex(passwordAsBytes);

		System.err.println("Time for " + iterations + "' iterations: "
				+ (System.nanoTime() - nanos) + " nanos. "
				+ (System.currentTimeMillis() - millis) + " ms. Hashed pwd:"
				+ new String(password));

		passwordAuthentication = new PasswordAuthentication(username, password);
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication(
			PasswordAuthenticationRequest request) {
		return passwordAuthentication;
	}

	private static final char[] digits = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private char[] toHex(byte[] barr) {
		char[] hexDigestPasswd = new char[barr.length * 2];
		for (int i = 0; i < barr.length; i++) {
			hexDigestPasswd[i * 2] = digits[(barr[i] & 0xF0) >> 4];
			hexDigestPasswd[i * 2 + 1] = digits[barr[i] & 0x0F];
		}
		return hexDigestPasswd;
	}

}
