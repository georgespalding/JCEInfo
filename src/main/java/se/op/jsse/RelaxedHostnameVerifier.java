package se.op.jsse;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

public class RelaxedHostnameVerifier implements HostnameVerifier {
	private static final Logger log=Logger.getLogger(RelaxedHostnameVerifier.class.getName());
	
	public boolean verify(String hostname, SSLSession session) {
		try {
			Principal peer = session.getPeerPrincipal();
			LdapName peerDn=new LdapName(peer.getName());
		
			String cn=null;
			for (Rdn rdn : peerDn.getRdns()) {
				if("cn".equalsIgnoreCase(rdn.getType())
						||"commonName".equalsIgnoreCase(rdn.getType())){
					log.log(Level.CONFIG,"RDN relevant for HostnameVerification ''{0}''",rdn);
					cn=rdn.getValue().toString();
				}else{
					log.log(Level.FINE,"RDN not relevant for HostnameVerification ''{0}''",rdn);
				}
			}
			

			if(null!=cn){
				boolean ok=verifyHostname(hostname, cn);
				if(!ok){
					//FIXME fulfix för skv irr med gamla domäner och ssl cert...
					Pattern patt=Pattern.compile("(?:\\.rsv){2}");
					String patchedHostname=patt.matcher(hostname).replaceAll(".rsv");
					String patchedCn=patt.matcher(cn).replaceAll(".rsv");
					ok=verifyHostname(patchedHostname, patchedCn);
					if(ok){
						log.log(Level.WARNING,"Hostname verification Accepted for '"+hostname+"' translated into '"+patchedHostname+"' did match cn='"+hostname+"' translated into '"+patchedHostname+"'.");
					}
				}
				return ok;
			}else{
				log.log(Level.WARNING,"Hostname verification failed, no commonName found in peer subject ''{0}''",peer);
			}
		} catch (SSLPeerUnverifiedException e) {
			log.log(Level.WARNING,"Hostname verification failed, SSLPeerUnverifiedException:"+e.getMessage(),e);
		} catch (InvalidNameException e) {
			log.log(Level.WARNING,"Hostname verification failed, InvalidNameException:"+e.getMessage(),e);
		}
		return false;
	}

	private static boolean verifyHostname(String hostname, String cn) {
		if(cn.startsWith("*.")){
			String fixedCn=cn.substring(1);
			int lastPos=hostname.lastIndexOf(fixedCn);
			int firstPos=hostname.indexOf(fixedCn);
			if(-1!=firstPos && firstPos==lastPos){
				//endast ett .rsv.rsv.se förekommer...
				String hostPart=hostname.substring(0,firstPos);
				if(-1==hostPart.indexOf('.')){
					log.log(Level.FINE,"Hostname verification on: ''{0}'' matches cn=''{1}''.", new Object[]{hostname,cn});
					return true;
				}else{
					log.log(Level.WARNING,"Hostname verification for ''{0}'' failed. cn=''{1}'' with star matched, but hostpart ''{2}'' had remaining domainname.", new Object[]{hostname,cn,hostPart});
				}
			}else{
				log.log(Level.WARNING,"Hostname verification for ''{0}'' failed. cn=''{1}'' with star was contained in hostname more than once, or not at all.", new Object[]{hostname,cn});
			}
		}else{
			if(hostname.equals(cn)){
				log.log(Level.FINE,"Hostname verification on: ''{0}'' matches cn=''{1}''.", new Object[]{hostname,cn});
				return true;
			}else{
				log.log(Level.WARNING,"Hostname verification for ''{0}'' did NOT match cn=''{1}''.", new Object[]{hostname,cn});
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		String hostname="u30907.rsv.se";
		String cn="*.rsv.rsv.se";
		boolean ok=verifyHostname(hostname, cn);
		if(!ok){
			//FIXME fulfix för skv irr med gamla domäner och ssl cert...
			Pattern patt=Pattern.compile("(?:\\.rsv){2}");
			String patchedHostname=patt.matcher(hostname).replaceAll(".rsv");
			String patchedCn=patt.matcher(cn).replaceAll(".rsv");
			ok=verifyHostname(patchedHostname, patchedCn);
			if(ok){
				log.log(Level.WARNING,"Hostname verification Accepted for '"+hostname+"' translated into '"+patchedHostname+"' did match cn='"+cn+"' translated into '"+patchedCn+"'.");
			}
		}
	}
}
