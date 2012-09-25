package se.op.concurrent;

import java.util.concurrent.ExecutorService;

public class TwoSettable<A,B> extends SelfScheduling<String> {
	public TwoSettable(ExecutorService se) {
		super(se);
	}

	private A v1;
	private B v2;
	
	public void setA(A v1) {
		this.v1 = v1;
	}

	public void setB(B v2) {
		this.v2 = v2;
	}

	@Override
	public String call() throws Exception {
		System.err.println("call(): RETURN "+v1+v2+" on "+this);
		return ""+v1+v2;
	}

}
