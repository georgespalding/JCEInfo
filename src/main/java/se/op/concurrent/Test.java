package se.op.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {
	public static void main(String[] args) throws SecurityException, NoSuchMethodException {
		OneSettable one=new OneSettable();
		SingleNotifyingSettable<String> stOne= new SingleNotifyingSettable<>(one, String.class);
		
		ExecutorService es=Executors.newSingleThreadExecutor();

		TwoSettable<String,Integer> twoFirst= new TwoSettable<>(es);
		SettableTargetGuard ssFirst=new SettableTargetGuard(twoFirst);
		Settable<String> st1A=ssFirst.addSettable(String.class,"setA");
		Settable<Integer> st1B=ssFirst.addSettable(Integer.class,"setB");

		TwoSettable<String,String> twoSecond= new TwoSettable<>(es);
		SettableTargetGuard ssSecond=new SettableTargetGuard(twoSecond);
		Settable<String> st2A=ssSecond.addSettable(String.class,"setA");
		Settable<String> st2B=ssSecond.addSettable(String.class,"setB");

		NotifyingFutureTask<String> tStr= new NotifyingFutureTask<>(new DumbCallable<>("HelloWorld!"), st1A, st2A, stOne);
		NotifyingFutureTask<Integer> tInt= new NotifyingFutureTask<>(new DumbCallable<>(1), st1B);
		
		System.err.println("Start!");
		es.submit(tStr);
		//es.submit(tStr2);
		es.submit(tInt);
		//es.shutdown();
	}
	
	static class DumbCallable<V> implements Callable<V> {
		V v;
		DumbCallable(V v){
			this.v=v;
		}
		public V call(){
			System.err.println("DumbCaller.call(): "+v);
			return v;
		}
	}
}
