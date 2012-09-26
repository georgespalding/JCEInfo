package se.op.jce;

public class Tuple<K,V>{
	public final K key;
	public final V value;
	public Tuple(K key,V value){
		this.key=key;
		this.value=value;
	}
	
	public String toString(){
		return key.toString();
	}
}

