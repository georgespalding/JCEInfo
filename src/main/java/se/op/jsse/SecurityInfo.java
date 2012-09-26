package se.op.jsse;

/**
 * george 23 feb 2008 16.42.35
 * ssltest.java
 */
import java.security.Provider;
import java.security.Security;
import java.security.Provider.Service;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author george
 * @since 23 feb 2008
 */
public class SecurityInfo {
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		ProviderRegistrator.main(args);
		
		Logger log = Logger.getAnonymousLogger();
		Map<String, Map<String, Set<Provider>>> type2alg2provs = new HashMap<String, Map<String, Set<Provider>>>();
		for (Provider prov : Security.getProviders()) {
			log.log(Level.FINE, "Provider {0} v{1}: {2}", new Object[] {
					prov.getName(), prov.getVersion(), prov.getInfo() });
			for (Service serv : prov.getServices()) {
				log.log(Level.FINER, "\tService {0} {1}", new Object[] {
						serv.getType(), serv.getAlgorithm() });
				Map<String, Set<Provider>> alg2provs = type2alg2provs.get(serv
						.getType());
				if (null == alg2provs) {
					alg2provs = new HashMap<String, Set<Provider>>();
					type2alg2provs.put(serv.getType(), alg2provs);
				}
				Set<Provider> provs = alg2provs.get(serv.getAlgorithm());
				if (null == provs) {
					provs = new HashSet<Provider>();
					alg2provs.put(serv.getAlgorithm(), provs);
				}
				provs.add(prov);
			}
		}
		log.info("====================================================");
		log.info("====================================================");
		log.info("====================================================");
		for (Map.Entry<String, Map<String, Set<Provider>>> type2alg2provsEntry : type2alg2provs
				.entrySet()) {
			log.info("Type:"+type2alg2provsEntry.getKey());
			for (Map.Entry<String, Set<Provider>> alg2provsEntry : type2alg2provsEntry
					.getValue().entrySet()) {
				log.log(Level.INFO, "\t{0}", alg2provsEntry.getKey());
				for (Provider prov : alg2provsEntry.getValue()) {
					log.log(Level.INFO, "\t\tProvider {0} v{1}: {2}",
							new Object[] { prov.getName(), prov.getVersion(),
									prov.getInfo() });
				}
			}
		}

		/*
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
		TrustManager[] tms = tmf.getTrustManagers();

		KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX");
		KeyManager[] kms = kmf.getKeyManagers();

		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.getClientSessionContext();
		ctx.init(kms, tms, SecureRandom.getInstance(""));
		*/
	}
}
