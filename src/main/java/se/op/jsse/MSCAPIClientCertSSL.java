package se.op.jsse;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.KeyStore.ProtectionParameter;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.net.ssl.*;

public class MSCAPIClientCertSSL {
    static KeyStore ks;
    static Logger log=Logger.getLogger(MSCAPIClientCertSSL.class.getName());
public static void main(String[] args) throws Exception {
    // Trust all invalid server certificate

    TrustManager[] trustMgr = DebugSSLSocketFactory.createTrustManagers(DebugSSLSocketFactory.createKeyStore(
            new File("P:/rsvutv/opt/rsv/pki/test/int_org/ca-cert-jks/root-Skatteverkets_Interna_ORG_CAs_TEST-crt.jks"),
            "Pa55ivitet".toCharArray()));

    SSLContext ctx = SSLContext.getInstance("TLS");
    
    //FIXME Variation
    KeyManagerFactory kmf = KeyManagerFactory.getInstance("NewSunX509");
    //KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");

    //GSP ks.load(new FileInputStream(keyFile), passPhrase);
    Provider p=Security.getProvider("SunMSCAPI");
    ks=KeyStore.getInstance("Windows-MY", p);
    ks.load(null);
    
    Enumeration<String> aliases=ks.aliases();
    int ai=0;
    while (aliases.hasMoreElements()) {
        String alias = (String) aliases.nextElement();
        if(alias.contains("eorge")){
            log.info("Alias i ks Windows-MY["+ai+"}:"+alias);
        }
        ai++;
    }
    
    java.security.SecureRandom x = new java.security.SecureRandom();
    x.setSeed(System.currentTimeMillis()%4711);

    //FIXME Variation
    //kmf.init(ks, null);
    //kmf.init(ks, "123456qwerty".toCharArray());
    
    kmf.init(new KeyStoreBuilderParameters(new KeyStore.Builder() {
        @Override
        public ProtectionParameter getProtectionParameter(String alias)
                throws KeyStoreException {
            System.err.println("XXXXXXXXXXXXX getProtectionParameter("+alias+")");
            return new TestPasswordProtectionParameter("165974".toCharArray());
        }
        
        @Override
        public KeyStore getKeyStore() throws KeyStoreException {
            System.err.println("XXXXXXXXXXXXX getKeyStore() of type "+ks.getType());
            return ks;
        }
    }));//.init(ks, null);

    KeyManager[] kms=kmf.getKeyManagers();
    for (int i = 0; i < kms.length; i++) {
        System.err.println("kms["+i+"]:"+kms[i].getClass());
        if(kms[i] instanceof X509KeyManager){
            X509KeyManager km=(X509KeyManager)kms[i];
            kms[i]=new DebugX509KeyManager(km);
        }
    } 
    ctx.init(kms, trustMgr, x);

    //FIXME variation
    //SSLSocketFactory sslSockFac= ctx.getSocketFactory();
    SSLSocketFactory sslSockFac= new DebugSSLSocketFactory(ctx.getSocketFactory());


    URL u=new URL("https://ssop2.rsv.se/");
    HttpURLConnection conn;
    try {
            conn = (HttpURLConnection) u.openConnection();
    } catch (IOException e) {
            System.err.println("Failed to connect to '" + u + "'"+e);
            throw e;
    }
    // förbered...
    if (conn instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            httpsConn.setSSLSocketFactory(sslSockFac);

            httpsConn.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String s, SSLSession sslsession) {
                    System.err.println("PASTRU ok:"+s);
                    return true;
                }
            });
    } 
    conn.setAllowUserInteraction(false);
    conn.setUseCaches(false);
    conn.setInstanceFollowRedirects(false);
    conn.setDoInput(true);
    conn.setDoOutput(false);
    conn.setConnectTimeout(5000);
    conn.setReadTimeout(10000);
    conn.setRequestMethod("HEAD");
    System.err.println("httpresp:"+conn.getResponseCode());
    System.err.println("httpresp:"+conn.getResponseMessage());
/*    Socket s=sslSockFac.createSocket(InetAddress.getByName("ssou2.rsv.se"),443);
    OutputStream os=s.getOutputStream();
    InputStream is=s.getInputStream();
    os.write("GET https://ssou2.rsv.se/za/za_ssofront/ HTTP/1.1\nHost: ssou2.rsv.se\n\n\n".getBytes());
    os.flush();
    int read;
    while((read=is.read())!=-1){
        System.err.print((char)read);
    }*/
}


}
