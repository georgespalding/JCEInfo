package se.op.util;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class FileChangeDetector {
	private final long iMinCheckIntervalMillis;
	private final File iTarget; 
	private volatile long iDoNotCheckBefore;
	private volatile long iLastLoaded;

	//private final ConcurrentHashMap<String, V>
	public FileChangeDetector(File aTarget, long aMinCheckIntervalMillis, long aCurrentTimeMillis){
		iTarget = aTarget;
		iMinCheckIntervalMillis = aMinCheckIntervalMillis;
		checkDone(aCurrentTimeMillis);
	}

	
	private void checkDone(long aCurrentTimeMillis){
		iDoNotCheckBefore = aCurrentTimeMillis + iMinCheckIntervalMillis;
		System.err.println("checkDone");
	}
	
	public boolean shouldReload(long aCurrentTimeMillis){
		if(iMinCheckIntervalMillis >= 0 
			&& aCurrentTimeMillis > iDoNotCheckBefore){
			checkDone(aCurrentTimeMillis);
			return iTarget.lastModified() > iLastLoaded;
		} else {
			return false;
		}
	}
	
	public void setLoaded(long aCurrentTimeMillis){
		iLastLoaded = aCurrentTimeMillis;
	}
	
	public FileChangeDetector(File aTarget, long aMinCheckInterval){
		this(aTarget, aMinCheckInterval, System.currentTimeMillis());
	}
	
	public boolean shouldReload(){
		return shouldReload(System.currentTimeMillis());
	}

	public void setLoaded(){
		setLoaded(System.currentTimeMillis());
	}

}
