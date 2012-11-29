package se.op.jsse;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */

public class X509TrustManagerWrapper implements X509TrustManager {
	private static final String CLASS_NAME=X509TrustManagerWrapper.class.getName();
	private static final Logger LOG = Logger
			.getLogger(CLASS_NAME);

	private final X509TrustManager wrapped;
	public X509TrustManagerWrapper(X509TrustManager wrapped){
		this.wrapped=wrapped;
	}

	public X509TrustManager getWrapped(){
		return wrapped;
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		LOG.entering(CLASS_NAME, "checkClientTrusted", new Object[]{chain, authType});
		wrapped.checkClientTrusted(chain, authType);
		LOG.exiting(CLASS_NAME, "checkClientTrusted");
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		LOG.entering(CLASS_NAME, "checkServerTrusted", new Object[]{chain, authType});
		wrapped.checkServerTrusted(chain, authType);
		LOG.exiting(CLASS_NAME, "checkServerTrusted");
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		LOG.entering(CLASS_NAME, "getAcceptedIssuers");
		X509Certificate[] ret = wrapped.getAcceptedIssuers();
		LOG.exiting(CLASS_NAME, "getAcceptedIssuers");
		return ret;
	}

}
