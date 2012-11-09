package se.op.util.producers;

public interface NamedProducer<P,N> extends Producer<P>{
	public P produce();
	public N getName();
}
