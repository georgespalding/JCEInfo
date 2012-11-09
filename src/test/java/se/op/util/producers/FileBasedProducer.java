package se.op.util.producers;

import java.io.File;

public interface FileBasedProducer<P,N> extends NamedProducer<P,N>{
	public Iterable<File> getMaterialFiles();
}
