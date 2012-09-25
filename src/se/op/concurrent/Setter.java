package se.op.concurrent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Setter<B,V> implements Settable<V>{
	protected B bean;
	private Method meth;
	protected Setter(B bean, Class<V> type) throws SecurityException, NoSuchMethodException {
		this(bean,type,"set");
	}
	
	protected Setter(B bean, Class<V> type, String method) throws SecurityException, NoSuchMethodException{
		this.bean=bean;
		// to handle type erasure of generics
		Class<?> cls=type;
		while(meth==null && cls!=null){
			try{
				this.meth=bean.getClass().getDeclaredMethod(method, cls);
			}catch(NoSuchMethodException nsme){
				System.err.println("No meth:"+nsme.getMessage()+" with arg type "+cls.getSimpleName());
				cls=cls.getSuperclass();
			}
		}
		if(meth==null)
			throw new NoSuchMethodException(type.getName()+"."+method);
	}
	
	@Override
	public void set(V value) {
		try {
			System.err.println("Settable: "+meth.getName()+"("+value+") on "+bean);
			meth.invoke(bean, value);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
