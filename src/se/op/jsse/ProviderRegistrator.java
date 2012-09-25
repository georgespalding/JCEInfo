package se.op.jsse;

import java.security.Provider;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ProviderRegistrator {

	private static Map<String, String> shortProviders=new HashMap<String, String>();
	static {
		shortProviders.put("IAIK", "iaik.security.provider.IAIK");
		shortProviders.put("BCPROV", "org.bouncycastle.jce.provider.BouncyCastleProvider");
	}
	
	private enum Option{
		shortname,
		classname;
	}

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		for (int i = 0; i < args.length; i++) {
			if(args[i].startsWith("-") && args[i].contains("=")){
				String [] cmd=args[i].split("=",2);
				try{
					switch (Option.valueOf(cmd[0].substring(1))) {
					case shortname:
						addShortProvider(cmd[1]);
						break;
					case classname:
						addProvider(cmd[1]);
						break;
					default:
						throw new IllegalArgumentException(getUsageText("No arguments given."));
					}
				}catch(IllegalArgumentException iae){
					throw new IllegalArgumentException(getUsageText("'"+cmd[0]+"' is not a valid option."),iae);
				}
			}else{
				throw new IllegalArgumentException(getUsageText("Malformed argument '"+args[i]+"'."));
			}
		}
	}

	public static void addProvider(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		Security.addProvider((Provider)(Class.forName(className).newInstance()));
		Logger.getAnonymousLogger().severe("Added '"+className+"' as provider.");
	}

	public static void addShortProvider(String shortName) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		String className=shortProviders.get(shortName);
		if(null==className){
			throw new IllegalArgumentException(getUsageText("Unknown shortname '"+shortName+"' given."));
		}
		addProvider(className);
	}
	
	public static void addIAIKProvider() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		addShortProvider("IAIK");
	}
	
	public static void addPCPROVProvider() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		addShortProvider("BCPROV");
	}
	
	public static String getUsageText(String msg){
		return msg+"\nUsage:"
			+"\n\t"+ProviderRegistrator.class.getName()+" -"+Option.shortname+"=["+shortProviders.keySet().toString()+"]"
			+"\n\t"+ProviderRegistrator.class.getName()+" -"+Option.classname+"=<name.of.class.to.load>"
			;
	}
}
