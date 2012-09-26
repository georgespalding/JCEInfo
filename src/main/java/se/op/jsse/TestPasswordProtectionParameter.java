package se.op.jsse;

import java.security.KeyStore.PasswordProtection;

import javax.security.auth.DestroyFailedException;

public class TestPasswordProtectionParameter extends PasswordProtection {

    public TestPasswordProtectionParameter(char[] arg0) {
        super(arg0);
    }

    @Override
    public synchronized void destroy() throws DestroyFailedException {
     System.err.println("XXX============= TestPasswordProtectionParameter destroy() ");
     super.destroy();
    }
    @Override
    public synchronized boolean isDestroyed() {
        boolean ret=super.isDestroyed();
        System.err.println("XXX============= TestPasswordProtectionParameter isDestroyed(): "+ret);
        return ret;
    }
    @Override
    public synchronized char[] getPassword() {
        char [] ret=super.getPassword();
        System.err.println("XXX============= TestPasswordProtectionParameter getPassword(): "+new String(ret));
        return ret;
    }
}
