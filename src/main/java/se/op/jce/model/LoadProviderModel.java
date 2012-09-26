package se.op.jce.model;

import java.io.File;
import java.util.List;

import se.op.jce.swing.GenericListModel;

public class LoadProviderModel {
	private boolean loaded=true;

	private String className;
	private GenericListModel<File> classPath=new GenericListModel<File>();
	private String name;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<File> getClassPath() {
		return classPath.items();
	}
	
	public GenericListModel<File> getClassPathModel(){
		return classPath;
	}
	
	public String toString(){
		return "Provider: "+className+" Extra classpath:"+classPath.items();
	}
	
	public void setLoaded(boolean lded){
		loaded=lded;
	}

	public boolean isLoaded(){
		return loaded;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
