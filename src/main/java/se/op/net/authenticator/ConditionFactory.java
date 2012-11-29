package se.op.net.authenticator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class ConditionFactory {
	private static abstract class CompositeCondition implements Condition{
		private final Condition[] parts;

		public CompositeCondition(Condition... parts){
			this.parts = parts==null
					?new Condition[0]
						:parts;
		}
		
		public Iterable<Condition> getChildren(){
			return new Iterable<Condition>() {
				
				@Override
				public Iterator<Condition> iterator() {
					List<Condition> ret=Arrays.asList(parts);
					return ret.iterator();
				}
			};
		}
	}

	public static Condition and(Condition... parts){
		return new CompositeCondition(parts) {
			@Override
			public boolean matchesFor(PasswordAuthenticationRequest req) {
				for (Condition af:getChildren()) {
					if(!af.matchesFor(req)){
						return false;
					}
				}
				return true;
			}
		};
	}

	public static Condition or(Condition... parts){
		return new CompositeCondition(parts) {
			@Override
			public boolean matchesFor(PasswordAuthenticationRequest auth) {
				for (Condition af:getChildren()) {
					if(af.matchesFor(auth)){
						return true;
					}
				}
				return false;
			}
		};
	}

	public static Condition not(final Condition nested){
		return new Condition(){
			@Override
			public boolean matchesFor(PasswordAuthenticationRequest auth) {
				return !nested.matchesFor(auth);
			}
		};
	}
	
	private static class PropertyCondition<T> implements Condition {
		private final PasswordAuthenticationRequestProperty prop;
		private final T expected;
		
		public PropertyCondition(PasswordAuthenticationRequestProperty p,T val){
			this.prop=p;
			this.expected=val;
		}
		
		@Override
		public boolean matchesFor(PasswordAuthenticationRequest req) {
			Object actual = req.getProperty(prop);
			return expected==null
					?actual==null
					:expected.equals(actual);
		}

	}

	public static Condition host(final String criteria){
		return new PropertyCondition<String>(PasswordAuthenticationRequestProperty.host, criteria);
	}

	public static Condition site(final String criteria){
		return new PropertyCondition<String>(PasswordAuthenticationRequestProperty.site, criteria);
	}

	public static Condition port(final Integer criteria){
		return new PropertyCondition<Integer>(PasswordAuthenticationRequestProperty.port, criteria);
	}

	public static Condition protocol(final String criteria){
		return new PropertyCondition<String>(PasswordAuthenticationRequestProperty.protocol, criteria);
	}

	public static Condition prompt(final String criteria){
		return new PropertyCondition<String>(PasswordAuthenticationRequestProperty.prompt, criteria);
	}

	public static Condition scheme(final String criteria){
		return new PropertyCondition<String>(PasswordAuthenticationRequestProperty.scheme, criteria);
	}

	public static Condition url(final String criteria){
		return new PropertyCondition<String>(PasswordAuthenticationRequestProperty.url, criteria);
	}

	public static Condition type(final String criteria){
		return new PropertyCondition<String>(PasswordAuthenticationRequestProperty.type, criteria);
	}

}
