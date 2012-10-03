package se.op.util;

import java.io.File;

public interface FileBasedProducer<T> {
	public T produce();
	public File[] getMaterialFiles();
}
