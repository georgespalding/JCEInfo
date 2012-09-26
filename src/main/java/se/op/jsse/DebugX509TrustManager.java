package se.op.jsse;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */

public class DebugX509TrustManager implements X509TrustManager
{
    private X509TrustManager defaultTrustManager = null;

    /** Log object for this class. */
    private static final Logger LOG = Logger.getLogger(DebugX509TrustManager.class.getName());

    /**
     * Constructor for AuthSSLX509TrustManager.
     */
    public DebugX509TrustManager(final X509TrustManager defaultTrustManager) {
        super();
        if (defaultTrustManager == null) {
            throw new IllegalArgumentException("Trust manager may not be null");
        }
        this.defaultTrustManager = defaultTrustManager;
    }

    /**
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[],String authType)
     */
    public void checkClientTrusted(X509Certificate[] certificates,String authType) throws CertificateException {
        if (LOG.isLoggable(Level.CONFIG) && certificates != null) {
            for (int c = 0; c < certificates.length; c++) {
                X509Certificate cert = certificates[c];
                StringBuilder sb=new StringBuilder();
                sb.append(" Client certificate " + (c + 1) + ":").append('\n')
                .append("  Subject DN: " + cert.getSubjectDN()).append('\n')
                .append("  Signature Algorithm: " + cert.getSigAlgName()).append('\n')
                .append("  Valid from: " + cert.getNotBefore() ).append('\n')
                .append("  Valid until: " + cert.getNotAfter()).append('\n')
                .append("  Issuer: " + cert.getIssuerDN());
                LOG.config(sb.toString());
            }
        }
        defaultTrustManager.checkClientTrusted(certificates,authType);
    }

    /**
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[],String authType)
     */
    public void checkServerTrusted(X509Certificate[] certificates,String authType) throws CertificateException {
        if (LOG.isLoggable(Level.FINE) && certificates != null) {
            for (int c = 0; c < certificates.length; c++) {
                X509Certificate cert = certificates[c];
                StringBuilder sb=new StringBuilder();
                sb.append(" Server certificate " + (c + 1) + ":").append('\n')
                .append("  Subject DN: " + cert.getSubjectDN()).append('\n')
                .append("  Signature Algorithm: " + cert.getSigAlgName()).append('\n')
                .append("  Valid from: " + cert.getNotBefore() ).append('\n')
                .append("  Valid until: " + cert.getNotAfter()).append('\n')
                .append("  Issuer: " + cert.getIssuerDN());
                LOG.config(sb.toString());
            }
        }
        defaultTrustManager.checkServerTrusted(certificates,authType);
    }

    /**
     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    public X509Certificate[] getAcceptedIssuers() {
        return this.defaultTrustManager.getAcceptedIssuers();
    }
}
