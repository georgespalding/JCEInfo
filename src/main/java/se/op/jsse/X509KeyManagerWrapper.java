package se.op.jsse;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

import javax.net.ssl.X509KeyManager;

public class X509KeyManagerWrapper implements X509KeyManager {
	private static final String CLASS_NAME=X509KeyManagerWrapper.class.getName();
	private static final Logger LOG = Logger
			.getLogger(CLASS_NAME);

	private final X509KeyManager wrapped;
	X509KeyManagerWrapper(X509KeyManager wrapped){
		this.wrapped=wrapped;
	}

	public X509KeyManager getWrapped(){
		return wrapped;
	}

	@Override
	public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
		LOG.entering(CLASS_NAME, "chooseClientAlias", new Object[]{keyType, issuers, socket});
		String ret = wrapped.chooseClientAlias(keyType, issuers, socket);
		LOG.exiting(CLASS_NAME, "chooseClientAlias", ret);
		return ret;
	}

	@Override
	public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
		LOG.entering(CLASS_NAME, "chooseServerAlias", new Object[]{keyType, issuers, socket});
		String ret = wrapped.chooseServerAlias(keyType, issuers, socket);
		LOG.exiting(CLASS_NAME, "chooseServerAlias", ret);
		return ret;
	}

	@Override
	public X509Certificate[] getCertificateChain(String alias) {
		LOG.entering(CLASS_NAME, "getCertificateChain", alias);
		X509Certificate[] ret = wrapped.getCertificateChain(alias);
		LOG.exiting(CLASS_NAME, "getCertificateChain", ret);
		return ret;
	}

	@Override
	public String[] getClientAliases(String keyType, Principal[] issuers) {
		LOG.entering(CLASS_NAME, "getClientAliases", new Object[]{keyType, issuers});
		String[] ret = wrapped.getClientAliases(keyType, issuers);
		LOG.exiting(CLASS_NAME, "getClientAliases", ret);
		return ret;
	}

	@Override
	public PrivateKey getPrivateKey(String alias) {
		LOG.entering(CLASS_NAME, "getPrivateKey", alias);
		PrivateKey ret = wrapped.getPrivateKey(alias);
		LOG.exiting(CLASS_NAME, "getPrivateKey", ret);
		return ret;
	}

	@Override
	public String[] getServerAliases(String keyType, Principal[] issuers) {
		LOG.entering(CLASS_NAME, "getServerAliases", new Object[]{keyType, issuers});
		String[] ret = wrapped.getServerAliases(keyType, issuers);
		LOG.exiting(CLASS_NAME, "getServerAliases", ret);
		return ret;
	}

}
