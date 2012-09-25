package se.op.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class NotifyingFutureTask<V> extends FutureTask<V>{
	private Settable<V>[] settables;
	public NotifyingFutureTask(Callable<V> callable,Settable<V>... settables) {
		super(callable);
//		if(callable instanceof SelfScheduling){
//		throw new IllegalArgumentException("SelfScheduling objects should not be added to tasks");
//	}
		this.settables=settables;
	}

	@Override
	protected void done() {
		for(Settable<V> settable:settables){
			try{
				settable.set(get());
			}catch(Exception ee){
				throw new IllegalArgumentException("FIXME",ee);
			}
		}
	}

}
