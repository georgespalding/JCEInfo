package se.op.jsse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

public class JSSEUtil {
	/** Log object for this class. */
	private static final Logger LOG = Logger
			.getLogger(SSLSocketFactoryWrapper.class.getName());

	public static final KeyStore createKeyStore(final File keyStoreFile,
			final char[] keyStorePassword) throws Exception {
		return createKeyStore(keyStoreFile, keyStorePassword, "jks");
	}

	public static final KeyStore createKeyStore(final File keyStoreFile,
			final char[] keyStorePassword, String keyStoreType) throws Exception {
		List<String> errs = new LinkedList<String>();
		if (keyStoreFile == null) {
			errs.add(" may not be null");
		}
		if (!keyStoreFile.exists()) {
			errs.add(" must point to an existing file");
		}
		if (!keyStoreFile.isFile()) {
			errs.add(" must be a plain file.");
		}
		if (!keyStoreFile.canRead()) {
			errs.add(" must be readable.");
		}
		if (!errs.isEmpty()) {
			throw new Exception("Configuration error - Keystore file " + keyStoreFile
					+ "' " + errs);
		}
		LOG.log(Level.CONFIG, "Initializing key store {0}", keyStoreFile);
		if (LOG.isLoggable(Level.FINEST)) {
			LOG.log(Level.FINEST, "Initializing key store using password {0}",
					new String(keyStorePassword));
		}
		try {
			KeyStore keystore = KeyStore.getInstance(keyStoreType);
			InputStream is = null;
			try {
				is = new FileInputStream(keyStoreFile);
				keystore.load(is, keyStorePassword != null ? keyStorePassword : null);
			} finally {
				if (is != null)
					is.close();
			}
			return keystore;
		} catch (Exception e) {
			throw new Exception("Failed to initialize keystore '" + keyStoreFile
					+ "'.", e);
		}
	}

	public static final KeyManager[] createKeyManagers(
			final KeyStore identityKeyStore, final char[] identityKeyPassword)
			throws Exception {
		if (identityKeyStore == null) {
			throw new IllegalArgumentException("Identity keystore may not be null.");
		}
		if (LOG.isLoggable(Level.CONFIG)) {
			LOG.config("Initializing key manager");
			LOG.log(Level.CONFIG, describe(identityKeyStore, "Identity"));
		}
		try {
			KeyManagerFactory kmfactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmfactory.init(identityKeyStore,
					identityKeyPassword != null ? identityKeyPassword : null);
			KeyManager[] keymanagers = kmfactory.getKeyManagers();
			if (LOG.isLoggable(Level.CONFIG)) {
				for (int i = 0; i < keymanagers.length; i++) {
					if (keymanagers[i] instanceof X509KeyManager) {
						keymanagers[i] = new X509KeyManagerWrapper(
								(X509KeyManager) keymanagers[i]);
					}
				}
			}
			return keymanagers;
		} catch (Exception e) {
			throw new Exception("Failed to initialize KeyManagerFactory.", e);
		}
	}

	public static final TrustManager[] createTrustManagers(
			final KeyStore trustKeyStore) throws Exception {
		if (trustKeyStore == null) {
			throw new IllegalArgumentException("Trust keystore may not be null");
		}
		if (LOG.isLoggable(Level.CONFIG)) {
			LOG.config("Initializing trust manager");
			LOG.log(Level.CONFIG, describe(trustKeyStore, "Trusted"));
		}
		try {
			TrustManagerFactory tmfactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmfactory.init(trustKeyStore);
			TrustManager[] trustmanagers = tmfactory.getTrustManagers();
			if (LOG.isLoggable(Level.CONFIG)) {
				for (int i = 0; i < trustmanagers.length; i++) {
					if (trustmanagers[i] instanceof X509TrustManager) {
						trustmanagers[i] = new X509TrustManagerWrapper(
								(X509TrustManager) trustmanagers[i]);
					}
				}
			}
			return trustmanagers;
		} catch (Exception e) {
			throw new Exception("Failed to initialize TrustManagerFactory.", e);
		}
	}

	/**
	 * Creates SSL context f?r server side ssl (no client auth)
	 * 
	 * @param trustStoreFile
	 * @param trustStorePassword
	 * @return
	 * @throws EcException
	 */
	public static final SSLContext createSSLContext(final File trustStoreFile,
			final char[] trustStorePassword) throws Exception {
		return createSSLContext(null,
				createTrustManagers(createKeyStore(trustStoreFile, trustStorePassword)));
	}

	/**
	 * Creates SSL context f?r two way ssl (client auth)
	 * 
	 * @param identityKeyStoreFile
	 * @param identityKeyStorePassword
	 * @param identityKeyPassword
	 * @param trustStoreFile
	 * @param trustStorePassword
	 * @return
	 * @throws EcException
	 */
	public static final SSLContext createSSLContext(
			final File identityKeyStoreFile, final char[] identityKeyStorePassword,
			final char[] identityKeyPassword, final File trustStoreFile,
			final char[] trustStorePassword) throws Exception {
		return createSSLContext(
				createKeyManagers(
						createKeyStore(identityKeyStoreFile, identityKeyStorePassword),
						identityKeyPassword),
				createTrustManagers(createKeyStore(trustStoreFile, trustStorePassword)));
	}

	public static final SSLContext createSSLContext(KeyManager[] identityKeyMgrs,
			TrustManager[] trustManagers) throws Exception {
		try {
			SSLContext sslcontext = SSLContext.getInstance("SSL");
			// TODO skall vi h?mta n?gon s?rskild secure random?
			sslcontext.init(identityKeyMgrs, trustManagers, null);
			return sslcontext;
		} catch (Exception e) {
			throw new Exception("Failed to create SSL context.");
		}
	}

		public static final void logIdentityKeyStore(KeyStore keystore) {
		LOG.log(Level.CONFIG, describe(keystore, "Identity"));
	}

	public static final String describe(KeyStore keystore,String prefix) {
		StringBuilder sb=new StringBuilder();
		sb.append(prefix);
		try {
			Enumeration<?> aliases = keystore.aliases();
			while (aliases.hasMoreElements()) {
				String alias = (String) aliases.nextElement();
				sb.append(prefix).append(" certificate chain '").append("':").append(alias);
				try {
					Certificate[] certs = keystore.getCertificateChain(alias);
					if (certs != null) {
						sb.append("\n").append(describe(certs, "\tCertificate"));
					}
				} catch (KeyStoreException e) {
					LOG.log(Level.WARNING, "Failed to get identity certificate for alias '"+ alias + "' from keystore.", e);
					sb.append(" Not readable: ").append(e.getMessage()).append("\n");
				}
			}
		} catch (KeyStoreException e) {
			LOG.log(Level.WARNING, "Failed to get aliases from identity keystore.", e);
			sb.append("No aliases found '").append(e.getMessage()).append("'.");
		}
		return sb.toString();
	}

	public static final String describe(Certificate[] certs, String start) {
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < certs.length; j++) {
			sb.append(describe(certs[j], start + "[" + j + "]"));
		}
		return sb.toString();
	}

	public static final String describe(Certificate cert, String start) {
		StringBuilder sb = new StringBuilder();
		sb.append(start).append('\n');
		if (cert instanceof X509Certificate) {
			X509Certificate X509cert = (X509Certificate) cert;
			sb.append("  Subject DN: " + X509cert.getSubjectDN()).append('\n')
					.append("  Signature Algorithm: " + X509cert.getSigAlgName())
					.append('\n').append("  Valid from: " + X509cert.getNotBefore())
					.append('\n').append("  Valid until: " + X509cert.getNotAfter())
					.append('\n').append("  Issuer: " + X509cert.getIssuerDN());
		} else {
			sb.append("  Not an X509Certificate. Type:" + cert.getType());
		}
		return sb.toString();
	}

}
