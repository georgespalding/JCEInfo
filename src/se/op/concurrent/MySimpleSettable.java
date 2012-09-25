package se.op.concurrent;

public class MySimpleSettable implements Settable<String>{
	String val;
	
	@Override
	public void set(String value) {
		val=value;
	}

}
