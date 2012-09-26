package se.op.jsse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class PKCS12toJks {
	static Logger log=Logger.getLogger(PKCS12toJks.class.getName());
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, IOException, UnrecoverableKeyException, CertificateException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		//TODO gör alla dessa till parametrar!
		File infile=new File("C:/WS/sso/src/etc/sso-ssl.p12");
		char[] inpwd="501nav3ryh0t".toCharArray();
		String intype="PKCS12";
		String inprovider="SunJSSE";

		File outfile=new File("C:/WS/sso/src/etc/sso-ssl-identity.jks");
		char[] outstorepwd="binglirka".toCharArray();
		char[] outkeypwd="glirkabin".toCharArray();
		String outtype="JKS";
		String outprovider="SUN";
		
		Set<String> aliasesToMove=new HashSet<String>();
		
		// registrera iaik	
		ProviderRegistrator.addIAIKProvider();		
		KeyStore inKeyStore=KeyStore.getInstance(intype, inprovider);
		inKeyStore.load(new FileInputStream(infile), inpwd);
		log.info("Loaded in KeyStore type:'"+inKeyStore.getType()+"', provider: '"+inKeyStore.getProvider()+"'.");

		KeyStore outKeyStore=KeyStore.getInstance(outtype,outprovider);
		outKeyStore.load(null, null);
		log.info("Created empty out KeyStore type:'"+outKeyStore.getType()+"', provider: '"+outKeyStore.getProvider()+"'.");

		Enumeration<String> aliases=inKeyStore.aliases();
		while (aliases.hasMoreElements()) {
			String alias=aliases.nextElement();

			if(aliasesToMove.isEmpty()||aliasesToMove.contains(alias)){
				if(inKeyStore.isKeyEntry(alias)){
					Key identity=inKeyStore.getKey(alias, inpwd);
					Certificate[] chain=inKeyStore.getCertificateChain(alias);
					outKeyStore.setKeyEntry(alias, identity, outkeypwd, chain);
					log.info("Added key '"+alias+"' to out KeyStore.");

					/* TODO även exportera till .pem
						KeyFactory kf=KeyFactory.getInstance(identity.getAlgorithm());
						PKCS8EncodedKeySpec pkcs8=new PKCS8EncodedKeySpec(identity.getEncoded());
						PrivateKey pk=kf.generatePrivate(pkcs8);
						OutputStream out=new FileOutputStream(outname+"-key.pem");
						out.write(Base64.encodeBase64(pk.getEncoded()));
						out.close();
						System.err.println("saved privatekey in '"+outfile+"'.");
					 */
				}else{
					log.info("Alias '"+alias+"' is not a key entry. Won't be moved.");
				}
			}else{
				log.info("Alias '"+alias+"' was not meant to be exported.");
			}
		}
		
		outKeyStore.store(new FileOutputStream(outfile), outstorepwd);
		System.err.println("saved keystore in '"+outfile+"'.");

	}
	
}
