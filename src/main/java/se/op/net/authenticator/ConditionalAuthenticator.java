package se.op.net.authenticator;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.LinkedList;
import java.util.List;

public class ConditionalAuthenticator extends Authenticator{
	private final List<Tuple<Condition,PasswordAuthenticationProvider>> items;
	private boolean modifiable=true;
	
	public ConditionalAuthenticator(){
		items = new LinkedList<Tuple<Condition,PasswordAuthenticationProvider>>();
	}
	
	public void addConditionalPasswordAuthenticatorProvider(Condition c,PasswordAuthenticationProvider p){
		if(modifiable){
			items.add(new Tuple<Condition,PasswordAuthenticationProvider>(c,p));
		}else{
			throw new UnsupportedOperationException("ConditionalAuthenticator has been made readonly.");
		}
	}
	
	public void setReadonly(){
		modifiable = false;
	};
	
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		PasswordAuthenticationRequest req=new PasswordAuthenticationRequest(getRequestingHost(), getRequestingSite(), getRequestingPort(), getRequestingProtocol(), getRequestingPrompt(), getRequestingScheme(), getRequestingURL(), getRequestorType());
		
		for (Tuple<Condition,PasswordAuthenticationProvider> each : items) {
			if(each.get1().matchesFor(req)){
				return each.get2().getPasswordAuthentication(req);
			}
		}
		return super.getPasswordAuthentication();
	}

	class Tuple<T1,T2> {
		private final T1 t1;
		private final T2 t2;
			
		public Tuple(T1 t1,T2 t2){
			this.t1=t1;
			this.t2=t2;
		}

		public T1 get1() {
			return t1;
		}

		public T2 get2() {
			return t2;
		}
	}

}
