package se.op.util.producers;

import java.util.LinkedList;
import java.util.List;

public class Agurk<P> {
	public void addProducer(Producer<?> p){
		System.err.println(""+p.getClass().getGenericInterfaces());
	}
	
	public static void main(String[] args) {
		List<String> l=new LinkedList<>();
		System.err.println(""+l.getClass().getGenericInterfaces()[0].);
		
	}
	
}
