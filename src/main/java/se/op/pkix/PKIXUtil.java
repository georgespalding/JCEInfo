package se.op.pkix;

import iaik.security.rsa.RSA;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PKIXUtil {
	
	public static void main(String[] args) throws CertificateException, FileNotFoundException, IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, KeyStoreException, CertPathValidatorException {
		// instantiate a KeyStore with type JKS
	    KeyStore ks = KeyStore.getInstance("JKS");
	    // load the contents of the KeyStore
	    try(InputStream is=new FileInputStream("/Users/geospa/Downloads/visma_pharmasolutions.jks")){
	    	// Add password of your knowledge
	    	ks.load(is,"wrong password".toCharArray());
	    }
	    // fetch certificate chain stored with alias "sean"
	    Certificate[] certArray = ks.getCertificateChain("pharmasolutions.se");
	    // convert chain to a List
	    List<Certificate> certList = Arrays.asList(certArray);
	    // instantiate a CertificateFactory for X.509
	    CertificateFactory cf = CertificateFactory.getInstance("X.509");
	    // extract the certification path from
	    // the List of Certificates
	    
	    CertPath cp = cf.generateCertPath(certList);
		
		File trusted=new File("/Users/geospa/Downloads/Entrust.net_CA_2048_2.pem");
		try(InputStream trustInputStream=new FileInputStream(trusted)){
			Certificate trustedCert=cf.generateCertificate(trustInputStream);
			if(trustedCert instanceof X509Certificate) {
				X509Certificate trustX509Cert=(X509Certificate) trustedCert;
				TrustAnchor ta=new TrustAnchor(trustX509Cert, null);
				PKIXParameters pkixpars=new PKIXParameters(Collections.singleton(ta));
				pkixpars.setRevocationEnabled(false);
				//pkixpars.setDate(date);
				CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
				CertPathValidatorResult res=cpv.validate(cp, pkixpars);
				System.err.println("Sucess!!!!! result:"+res);
				if(res instanceof PKIXCertPathValidatorResult) {
					PKIXCertPathValidatorResult pkixRes = (PKIXCertPathValidatorResult) res;
				}
			}else {
				System.err.println("Fail:"+trustedCert+" is not an x509 cert.");
			}
		}
	}
}
