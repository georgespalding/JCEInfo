package se.op.util.producers;

import java.util.LinkedList;
import java.util.List;

public class Agurk<P> {
	public static void main(String[] args) {
		List<String> l=new LinkedList<>();
		System.err.println(""+l.getClass().getGenericInterfaces()[0].getClass().getName());
		
	}
	
}
