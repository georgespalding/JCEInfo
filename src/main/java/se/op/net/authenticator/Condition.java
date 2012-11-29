package se.op.net.authenticator;

public interface Condition {
		public boolean matchesFor(PasswordAuthenticationRequest auth);
}
