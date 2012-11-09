package se.op.concurrent;

import java.security.AccessController;
import java.security.PrivilegedAction;

import sun.misc.Unsafe;

public class UsafeStuff {
	public static void main(String[] args) {
		Unsafe u=AccessController.doPrivileged(new PrivilegedAction<Unsafe>() {

			@Override
			public Unsafe run() {
				return Unsafe.getUnsafe();
			}
			
		});
		System.err.println("adrsiz:"+u.addressSize());
	}
}
