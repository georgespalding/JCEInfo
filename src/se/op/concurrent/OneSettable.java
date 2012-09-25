package se.op.concurrent;

import java.util.concurrent.Callable;

public class OneSettable implements SettingDoneListener,Callable<String>{
	String v1;
	public void set(String v){
		v1=v;
	}

	@Override
	public String call() throws Exception {
		return v1;
	}

	@Override
	public void settingsDone() {
		System.err.println("OneSettable setup");
	}
}
