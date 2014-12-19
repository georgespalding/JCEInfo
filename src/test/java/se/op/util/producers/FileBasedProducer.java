package se.op.util.producers;

import java.io.File;
import java.util.Collection;

public interface FileBasedProducer<P,N> extends NamedProducer<P,N>{
	public Collection<File> getMaterialFiles();
}
