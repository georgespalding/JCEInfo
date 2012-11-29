package se.op.jsse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocketFactory;

public class SSLSocketFactoryWrapper extends SSLSocketFactory{
	private static final String CLASS_NAME=X509KeyManagerWrapper.class.getName();
	private static final Logger LOG = Logger
			.getLogger(CLASS_NAME);

	private SSLSocketFactory wrapped;

	public SSLSocketFactoryWrapper(SSLSocketFactory sslSocketFactory) {
		this.wrapped=sslSocketFactory;
	}

	@Override
	public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
		LOG.entering(CLASS_NAME, "createSocket", new Object[]{s, host, port, autoClose});
		Socket ret = wrapped.createSocket(s, host, port, autoClose);
		LOG.exiting(CLASS_NAME, "createSocket", ret);
		return ret;
	}

	@Override
	public String[] getDefaultCipherSuites() {
		LOG.entering(CLASS_NAME, "getDefaultCipherSuites");
		String[] ret = wrapped.getDefaultCipherSuites();
		LOG.exiting(CLASS_NAME, "getDefaultCipherSuites", ret);
		return ret;
	}

	@Override
	public String[] getSupportedCipherSuites() {
		LOG.entering(CLASS_NAME, "getSupportedCipherSuites");
		String[] ret = wrapped.getSupportedCipherSuites();
		LOG.exiting(CLASS_NAME, "getSupportedCipherSuites", ret);
		return ret;
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		LOG.entering(CLASS_NAME, "createSocket", new Object[]{ host, port });
		Socket ret = wrapped.createSocket(host, port);
		LOG.exiting(CLASS_NAME, "createSocket", ret);
		return ret;
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		LOG.entering(CLASS_NAME, "createSocket", new Object[]{ host, port });
		Socket ret = wrapped.createSocket(host, port);
		LOG.exiting(CLASS_NAME, "createSocket", ret);
		return ret;
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
		LOG.entering(CLASS_NAME, "createSocket", new Object[]{host, port, localHost, localPort});
		Socket ret = wrapped.createSocket(host, port, localHost, localPort);
		LOG.exiting(CLASS_NAME, "createSocket", ret);
		return ret;
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		LOG.entering(CLASS_NAME, "createSocket", new Object[]{address, port, localAddress, localPort});
		Socket ret = wrapped.createSocket(address, port, localAddress, localPort);
		LOG.exiting(CLASS_NAME, "createSocket", ret);
		return ret;
	}

	@Override
	public Socket createSocket() throws IOException {
		LOG.entering(CLASS_NAME, "createSocket");
		Socket ret = wrapped.createSocket();
		LOG.exiting(CLASS_NAME, "createSocket", ret);
		return ret;
	}
}
