package se.op.net.authenticator;

import java.net.InetAddress;
import java.net.URL;
import java.net.Authenticator.RequestorType;

public final class PasswordAuthenticationRequest {
	private final String requestingHost;
  private final InetAddress requestingSite;
  private final int requestingPort;
  private final String requestingProtocol;
  private final String requestingPrompt;
  private final String requestingScheme;
  private final URL requestingURL;
  private final RequestorType requestorType;

  public PasswordAuthenticationRequest(String requestingHost,
			InetAddress requestingSite, int requestingPort,
			String requestingProtocol, String requestingPrompt,
			String requestingScheme, URL requestingURL,
			RequestorType requestorType) {
		this.requestingHost = requestingHost;
		this.requestingSite = requestingSite;
		this.requestingPort = requestingPort;
		this.requestingProtocol = requestingProtocol;
		this.requestingPrompt = requestingPrompt;
		this.requestingScheme = requestingScheme;
		this.requestingURL = requestingURL;
		this.requestorType = requestorType;
	}

	public Object getProperty(PasswordAuthenticationRequestProperty prop) {
		switch (prop) {
		case host: return getRequestingHost();
		case site: return getRequestingSite();
		case port: return getRequestingPort();
		case protocol: return getRequestingProtocol();
		case prompt: return getRequestingPrompt();
		case scheme: return getRequestingScheme();
		case url: return getRequestingURL();
		case type: return getRequestorType();
		}
		return null;
	}

	public String getRequestingHost() {
		return requestingHost;
	}

	public InetAddress getRequestingSite() {
		return requestingSite;
	}

	public int getRequestingPort() {
		return requestingPort;
	}

	public String getRequestingProtocol() {
		return requestingProtocol;
	}

	public String getRequestingPrompt() {
		return requestingPrompt;
	}

	public String getRequestingScheme() {
		return requestingScheme;
	}

	public URL getRequestingURL() {
		return requestingURL;
	}

	public RequestorType getRequestorType() {
		return requestorType;
	}  

	@Override
	public String toString() {
		return new StringBuilder("PasswordAuthenticationRequest; ")
		.append(" prompt:").append(getRequestingPrompt())
		.append(" proto:").append(getRequestingProtocol())
		.append(" host:").append(getRequestingHost())
		.append(" port:").append(getRequestingPort())
		.append(" scheme:").append(getRequestingScheme())
		.append(" site:").append(getRequestingSite())
		.append(" url:").append(getRequestingURL())
		.append(" type:").append(getRequestorType()).toString();
	}
}
