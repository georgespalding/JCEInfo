package se.op.concurrent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public abstract class SelfScheduling<V> implements SettingDoneListener, Callable<V> {
	ExecutorService se;

	private final Object lock=new Object();
	private final List<Settable<V>> settables=new LinkedList<Settable<V>>();
	
	void addSettable(Settable<V> settable){
		synchronized(lock){
			settables.add(settable);
		}
	}	
	
	public SelfScheduling(ExecutorService se) {
		this.se=se;
	}
	
	@Override
	public void settingsDone() {
		se.submit(new NotifyingFutureTask<V>(this, (Settable<V>[])settables.toArray()));
		System.err.println("Submitted NotifyingFutureTask for "+this);
	}

}
