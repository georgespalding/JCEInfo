package se.op.concurrent;

import java.util.HashSet;
import java.util.Set;

class SettingDoneNotifier {
	final SettingDoneListener bean;
	private final Object lock=new Object();
	private final Set<Settable<?>> settables=new HashSet<Settable<?>>();
	
	public SettingDoneNotifier(SettingDoneListener bean) {
		this.bean=bean;
	}

	void addSettable(Settable<?> settable){
		synchronized(lock){
			settables.add(settable);
		}
	}
	
	void settingDone(Settable<?> settable){
		synchronized(lock){
			settables.remove(settable);
			if(settables.isEmpty())
				bean.settingsDone();
		}
	}
}
