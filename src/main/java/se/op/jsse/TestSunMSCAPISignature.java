package se.op.jsse;

import java.io.IOException;
import java.security.*;
import java.util.Enumeration;
import org.apache.commons.codec.binary.Base64;

public class TestSunMSCAPISignature {
    public static void main(String[] args) throws KeyStoreException, GeneralSecurityException, IOException {
        Provider p=Security.getProvider("SunMSCAPI");
        KeyStore k=KeyStore.getInstance("Windows-MY", p);
        k.load(null);
        for (Enumeration<String> iter=k.aliases();iter.hasMoreElements();) {
            String alias=iter.nextElement();
            System.err.println("alias: "+alias);
        }
        Signature sig = Signature.getInstance("SHA1withRSA", p);
        sig.initSign((PrivateKey)k.getKey("Frida Kranstege - underskrift", null));
        sig.update(new byte[]{'f','f','f'});
        byte[] signature = sig.sign();
        System.err.println(Base64.encodeBase64String(signature));
    }
}
