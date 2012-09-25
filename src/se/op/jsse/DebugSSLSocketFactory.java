package se.op.jsse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class DebugSSLSocketFactory extends SSLSocketFactory{

    /** Log object for this class. */
    private static final Logger LOG = Logger.getLogger(DebugSSLSocketFactory.class.getName());

    public static final KeyStore createKeyStore(final File keyStoreFile, final char [] keyStorePassword) throws Exception {
        return createKeyStore(keyStoreFile, keyStorePassword,"jks");
    }

    public static final KeyStore createKeyStore(final File keyStoreFile, final char [] keyStorePassword,String keyStoreType) throws Exception {
        List<String> errs=new LinkedList<String>();
        if (keyStoreFile == null) {
        	errs.add(" may not be null");
        }
        if(!keyStoreFile.exists()){
            errs.add(" must point to an existing file");
        }
        if(!keyStoreFile.isFile()){
            errs.add(" must be a plain file.");
        }
        if(!keyStoreFile.canRead()){
            errs.add(" must be readable.");
        }
        if(!errs.isEmpty()){
            throw new Exception("Configuration error - Keystore file "+keyStoreFile+"' "+errs);
        }
        LOG.log(Level.CONFIG,"Initializing key store {0}",keyStoreFile);
        if(LOG.isLoggable(Level.FINEST)){
        	LOG.log(Level.FINEST,"Initializing key store using password {0}",new String(keyStorePassword));
        }
        try{
	        KeyStore keystore  = KeyStore.getInstance(keyStoreType);
	        InputStream is = null;
	        try {
	            is = new FileInputStream(keyStoreFile); 
	            keystore.load(is, keyStorePassword != null ? keyStorePassword: null);
	        } finally {
	            if (is != null) is.close();
	        }
	        return keystore;
        }catch(Exception e){
        	throw new Exception("Failed to initialize keystore '"+keyStoreFile+"'.",e);        	
        }
    }

    public static final KeyManager[] createKeyManagers(final KeyStore identityKeyStore, final char [] identityKeyPassword)throws Exception{
        if (identityKeyStore == null) {
            throw new IllegalArgumentException("Identity keystore may not be null.");
        }
        if (LOG.isLoggable(Level.CONFIG)) {
        	LOG.config("Initializing key manager");
        	logIdentityKeyStore(identityKeyStore);
        }
        try{
	        KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	        kmfactory.init(identityKeyStore, identityKeyPassword != null ? identityKeyPassword: null);
	        return kmfactory.getKeyManagers();
        }catch (Exception e) {
        	throw new Exception("Failed to initialize KeyManagerFactory.",e);        	
        }
    }

    public static final TrustManager[] createTrustManagers(final KeyStore trustKeyStore)
        throws Exception
    { 
        if (trustKeyStore == null) {
            throw new IllegalArgumentException("Trust keystore may not be null");
        }        
        if (LOG.isLoggable(Level.CONFIG)) {
        	LOG.config("Initializing trust manager");
        	logTrustsInKeyStore(trustKeyStore);
        }
        try{
	        TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(
	            TrustManagerFactory.getDefaultAlgorithm());
	        tmfactory.init(trustKeyStore);
	        TrustManager[] trustmanagers = tmfactory.getTrustManagers();
	        if(LOG.isLoggable(Level.CONFIG)){
	        	for (int i = 0; i < trustmanagers.length; i++) {
	        		if (trustmanagers[i] instanceof X509TrustManager) {
	        			trustmanagers[i] = new DebugX509TrustManager(
	        					(X509TrustManager)trustmanagers[i]);
	        		}
	        	}
	        }
	        return trustmanagers;
        }catch (Exception e) {
        	throw new Exception("Failed to initialize TrustManagerFactory.",e);        	
        }
    }

    /**
     * Creates SSL context för server side ssl (no client auth)
     * @param trustStoreFile
     * @param trustStorePassword
     * @return
     * @throws EcException
     */
    public static final SSLContext createSSLContext(final File trustStoreFile, final char [] trustStorePassword) throws Exception{
    	return createSSLContext(null, createTrustManagers(createKeyStore(trustStoreFile, trustStorePassword)));
    }

    /**
     * Creates SSL context för two way ssl (client auth)
     * @param identityKeyStoreFile
     * @param identityKeyStorePassword
     * @param identityKeyPassword
     * @param trustStoreFile
     * @param trustStorePassword
     * @return
     * @throws EcException
     */
    public static final SSLContext createSSLContext(
    		final File identityKeyStoreFile, final char [] identityKeyStorePassword, final char [] identityKeyPassword,
    		final File trustStoreFile, final char [] trustStorePassword) throws Exception{
    	return createSSLContext(
    			createKeyManagers(
    					createKeyStore(identityKeyStoreFile, identityKeyStorePassword), identityKeyPassword), 
    			createTrustManagers(
    					createKeyStore(trustStoreFile, trustStorePassword)));
    }

    public static final SSLContext createSSLContext(
    		KeyManager[] identityKeyMgrs,
    		TrustManager [] trustManagers
    ) throws Exception{
    	try {
    		SSLContext sslcontext = SSLContext.getInstance("SSL");
    		//TODO skall vi hämta någon särskild secure random?
			sslcontext.init(identityKeyMgrs, trustManagers, null);
	    	return sslcontext;
		} catch (Exception e) {
			throw new Exception("Failed to create SSL context.");
		}
    }
    
    public static final void logTrustsInKeyStore(KeyStore keystore){
        Enumeration<?> aliases;
		try {
			aliases = keystore.aliases();
		} catch (KeyStoreException e) {
			LOG.log(Level.WARNING,"Failed to get aliases from trust keystore.",e);
			return;
		}
        while (aliases.hasMoreElements()) {
	            String alias = (String)aliases.nextElement();                        
	    		try {
	            Certificate[] certs = keystore.getCertificateChain(alias);
	            LOG.log(Level.CONFIG,"Trusted certificate ''{0}'':",alias);
	            if (certs != null) {
	                for (int c = 0; c < certs.length; c++) {
	                	logCert(certs[c]," Certificate[" + c + "]:");
	                }
	            }else{
	            	logCert(keystore.getCertificate(alias)," Certificate:");
	            }
    		} catch (KeyStoreException e) {
    			LOG.log(Level.WARNING,"Failed to get trusted certificate for alias '"+alias+"' from keystore.",e);
    		}
        }
    }

	public static final void logIdentityKeyStore(KeyStore keystore) {
        Enumeration<?> aliases; 
		try {
			aliases = keystore.aliases();
		} catch (KeyStoreException e) {
			LOG.log(Level.WARNING,"Failed to get aliases from identity keystore.",e);
			return;
		}
        while (aliases.hasMoreElements()) {
            String alias = (String)aliases.nextElement();
    		try {
	            Certificate[] certs = keystore.getCertificateChain(alias);
	            if (certs != null) {
	                LOG.log(Level.CONFIG,"Identity Certificate chain ''{0}'':",alias);
	                for (int c = 0; c < certs.length; c++) {
	                	logCert(certs[c]," Certificate[" + c + "]:");
	                }
	            }
    		} catch (KeyStoreException e) {
    			LOG.log(Level.WARNING,"Failed to get identity certificate for alias '"+alias+"' from keystore.",e);
    		}
        }
    }

        public static final void logCerts(Certificate [] certs,String start){
            for (int j = 0; j < certs.length; j++) {
                logCert(certs[j],start+"["+j+"]");
            }
        }

        public static final void logCert(Certificate cert,String start){
        StringBuilder sb=new StringBuilder();
        sb.append(start).append('\n');
        if (cert instanceof X509Certificate) {
            X509Certificate X509cert = (X509Certificate)cert;
            sb.append("  Subject DN: " + X509cert.getSubjectDN()).append('\n')
             .append("  Signature Algorithm: " + X509cert.getSigAlgName()).append('\n')
             .append("  Valid from: " + X509cert.getNotBefore()).append('\n')
             .append("  Valid until: " + X509cert.getNotAfter()).append('\n')
             .append("  Issuer: " + X509cert.getIssuerDN());
        }else{
        	sb.append("  Not an X509Certificate. Type:"+cert.getType());
        }
        LOG.config(sb.toString());
    }

	private SSLSocketFactory sslSocketFactory;

	public DebugSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
		this.sslSocketFactory=sslSocketFactory;
	}

	@Override
	public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
		if(LOG.isLoggable(Level.CONFIG)){
			LOG.log(Level.CONFIG,"createSocket({0},{1},{2},{3})",new Object[]{s,host,port,autoClose});
		}
		return sslSocketFactory.createSocket(s,host,port,autoClose);
	}

	@Override
	public String[] getDefaultCipherSuites() {
		String [] ret=sslSocketFactory.getDefaultCipherSuites();
		if(LOG.isLoggable(Level.CONFIG)){
			LOG.log(Level.CONFIG,"getDefaultCipherSuites():"+Arrays.toString(ret));
		}
		return ret;
	}

	@Override
	public String[] getSupportedCipherSuites() {
		String [] ret=sslSocketFactory.getSupportedCipherSuites();
		if(LOG.isLoggable(Level.CONFIG)){
			LOG.log(Level.CONFIG,"getSupportedCipherSuites():"+Arrays.toString(ret));
		}
		return ret;
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		if(LOG.isLoggable(Level.CONFIG)){
			LOG.log(Level.CONFIG,"createSocket({0},{1})",new Object[]{host,port});
		}
		return sslSocketFactory.createSocket(host,port);
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		if(LOG.isLoggable(Level.CONFIG)){
			LOG.log(Level.CONFIG,"createSocket({0},{1})",new Object[]{host,port});
		}
		return sslSocketFactory.createSocket(host,port);
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
		if(LOG.isLoggable(Level.CONFIG)){
			LOG.log(Level.CONFIG,"createSocket({0},{1},{2},{3})",new Object[]{host,port,localHost,localPort});
		}
		return sslSocketFactory.createSocket(host,port,localHost,localPort);
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		if(LOG.isLoggable(Level.CONFIG)){
			LOG.log(Level.CONFIG,"createSocket({0},{1},{2},{3})",new Object[]{address,port,localAddress,localPort});
		}
		return sslSocketFactory.createSocket(address,port,localAddress,localPort);
	}

	@Override
	public Socket createSocket() throws IOException {
		return sslSocketFactory.createSocket();
	}
}
