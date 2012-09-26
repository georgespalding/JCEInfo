package se.op.jsse;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;

public class DebugX509KeyManager extends X509ExtendedKeyManager{
        Logger log=Logger.getLogger(DebugX509KeyManager.class.getName());

        X509KeyManager inst;
        DebugX509KeyManager(X509KeyManager inst){
            this.inst=inst;
        }
        @Override
        public String chooseClientAlias(String[] algs, Principal[] aprincipal, Socket socket) {
            log.log(Level.INFO,"call to chooseClientAlias("+ Arrays.toString(algs)+","
                    + Arrays.toString(aprincipal)+","
                    + "Socket"+ socket.getLocalAddress()+":"+socket.getLocalPort()+"->"+socket.getRemoteSocketAddress()+")");

            for (int i = 0; i < aprincipal.length; i++) {
                log.log(Level.FINE,"----- Remote Trusted CA Principal["+i+"]:"+aprincipal[i]);
            }

            String [] als=inst.getClientAliases("RSA", aprincipal);
            for (int i = 0; i < als.length; i++) {
                if(als[i].contains("George")){
                    log.log(Level.INFO,"----- Als["+i+"]:"+als[i]+" Cert:"+inst.getCertificateChain(als[i])[1].getSubjectDN());
                }
            }
            
            String ret=inst.chooseClientAlias(algs, aprincipal, socket);
            log.log(Level.INFO,"---- IMPL Wants to use ret:"+ret);
            ret="2.0.George Spalding";
            log.log(Level.INFO,"---- WE will use ret:"+ret);

            //ret="12.0.NORDEA_Frida.Kranstege_auth (Frida Kranstege)";
            //System.err.println("hardcoded ret="+ret+" ignore");
            DebugSSLSocketFactory.logCerts(inst.getCertificateChain(ret),"----==== chosen certs:");
            return ret;
        }

        @Override
        public String chooseServerAlias(String s, Principal[] aprincipal,
                Socket socket) {
            String ret=inst.chooseServerAlias(s,aprincipal, socket);
            log.log(Level.INFO,"call to chooseServerAlias("+s+","
                    + Arrays.toString(aprincipal)+","
                    + "Socket"+ socket.getLocalAddress()+":"+socket.getLocalPort()+"->"+socket.getRemoteSocketAddress()+"): ret="+ret);
            return ret;
        }

        @Override
        public X509Certificate[] getCertificateChain(String s) {
            X509Certificate [] ret=inst.getCertificateChain(s);
            DebugSSLSocketFactory.logCerts(ret, "call to getCertificateChain("+s+"): ret=");
            return ret;
        }

        @Override
        public String[] getClientAliases(String s, Principal[] aprincipal) {
            String[] ret=inst.getClientAliases(s,aprincipal);
            log.log(Level.INFO,"call to getClientAliases("+s+","+ Arrays.toString(aprincipal)+"):\n\tret="+Arrays.toString(ret));
            return ret;
        }

        @Override
        public PrivateKey getPrivateKey(String s) {
            PrivateKey ret=inst.getPrivateKey(s);
            log.log(Level.INFO,"call to getPrivateKey("+s+"): ret="+ret);
            return ret;
        }

        @Override
        public String[] getServerAliases(String s, Principal[] aprincipal) {
            String[] ret=inst.getServerAliases(s,aprincipal);
            log.log(Level.INFO,"call to getServerAliases("+s+","
                    + Arrays.toString(aprincipal)+"):\n\tret="+Arrays.toString(ret));
            return ret;
        }
}

