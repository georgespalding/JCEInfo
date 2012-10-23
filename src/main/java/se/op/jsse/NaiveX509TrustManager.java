package se.op.jsse;

import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.X509TrustManager;

public class NaiveX509TrustManager implements X509TrustManager{
    private Logger log=Logger.getLogger(NaiveX509TrustManager.class.getName());
    public NaiveX509TrustManager(){}
    
    private boolean boolXXXTrusted(String XXX,X509Certificate[] chain){
        StringBuilder sbCert=new StringBuilder();
        sbCert.append("\n");
        String indent="";
        for (X509Certificate cert : chain) {
            sbCert.append(indent).append(cert).append("\n");
            indent+="\t";
        }
        log.log(Level.WARNING,XXX+"Trusted("+sbCert+"): return true");
        return true;
    }

    public boolean checkClientTrusted(X509Certificate[] chain){
        return boolXXXTrusted("checkClient", chain);
    }

    public boolean isServerTrusted(java.security.cert.X509Certificate[] chain){
        return boolXXXTrusted("isServer", chain);
    }

    public boolean isClientTrusted(java.security.cert.X509Certificate[] chain){
        return boolXXXTrusted("isClient", chain);
    }


    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        log.log(Level.SEVERE,"Has no trusts to return!");
        return null;
    }

    private void checkXXXTrusted(String XXX, java.security.cert.X509Certificate[] chain, String authType){
        StringBuilder sbCert=new StringBuilder();
        sbCert.append("\n");
        String indent="";
        for (X509Certificate cert : chain) {
            sbCert.append(indent).append(cert).append("\n");
            indent+="\t";
        }
        log.log(Level.WARNING,"check"+XXX+"Trusted("+sbCert+","+authType+"): return true");
    }

    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType){
        checkXXXTrusted("Client", chain, authType);
    }

    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType){
        checkXXXTrusted("Server", chain, authType);
    }
}

