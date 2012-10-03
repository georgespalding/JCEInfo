package se.op.util;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Checks the file for modification at most every <code>MinCheckIntervalMillis</code>
 * isReloadNeeded will not return true until a grace period of <code>MinCheckIntervalMillis</code> has passed after the last modification of the file.
 * TODO: Javadoc comments for this type
 */
public class FileChangeMonitor<T> {
	private static final String CLASS_NAME=FileChangeMonitor.class.getName();
	private static final Logger log=Logger.getLogger(CLASS_NAME);

	private final long iMinCheckIntervalMillis;
	private final File iTarget;
	private final AtomicLong iLastChecked = new AtomicLong();
	private final AtomicLong iLastLoaded = new AtomicLong();
	private final AtomicReference<T> iResult = new AtomicReference<T>();
	
	public FileChangeMonitor(File aTarget, long aMinCheckIntervalMillis){
		this.iTarget = aTarget;
		this.iMinCheckIntervalMillis = aMinCheckIntervalMillis;
	}

	public FileChangeMonitor(File aTarget, long aMinCheckIntervalMillis, T aInitialResult, long aCurrentTimeMillisWhenLoaded){
		this(aTarget, aMinCheckIntervalMillis);
		setLoaded(aInitialResult, aCurrentTimeMillisWhenLoaded);
	}

	public boolean isReloadNeeded(final long aCurrentTimeMillisNow){
		if(log.isLoggable(Level.FINER)){
			log.entering(CLASS_NAME, "isReloadNeeded",aCurrentTimeMillisNow);
		}
		if(null == iResult.get()){
			return true;
		}
		if(iMinCheckIntervalMillis >= 0 ){
			if(iLastChecked.get()+iMinCheckIntervalMillis > aCurrentTimeMillisNow){
				if(log.isLoggable(Level.FINEST)){
					log.logp(Level.FINEST,CLASS_NAME,"isReloadNeeded", "LastChecked("+iLastChecked.get()+") + MinCheckIntervalMillis("+iMinCheckIntervalMillis+ ") > aCurrentTimeMillisNow("+aCurrentTimeMillisNow+")");
				}
				// it is time to look at the file.
				if(iTarget.lastModified() > iLastLoaded.get()){
					if(log.isLoggable(Level.FINEST)){
						log.logp(Level.FINEST,CLASS_NAME,"isReloadNeeded", "Target.lastModified("+iTarget.lastModified() +") > LastLoaded("+iLastLoaded.get()+")");
					}
					// It can be very dangerous to access the file has changed very recently,
					// as it may be being written.
					// This problem could typically occur during high load.
					// Therefore We introduce a grace period 
					
					if(iTarget.lastModified()+iMinCheckIntervalMillis < aCurrentTimeMillisNow){
						if(log.isLoggable(Level.FINEST)){
							log.logp(Level.FINEST,CLASS_NAME,"isReloadNeeded", "Target.lastModified("+iTarget.lastModified() +") + MinCheckIntervalMillis("+iMinCheckIntervalMillis+") < CurrentTimeMillisNow("+aCurrentTimeMillisNow+")");
						}
						// The grace period has elapsed
						// From now on check every time until setLoaded is called.
						if(log.isLoggable(Level.FINER)){
							log.exiting(CLASS_NAME, "isReloadNeeded", true);
						}
						return true;
					} else {
						// postpone next check until after one more period has passed. (see setCheckDone below)
					}
				} else {
					// Nothing changed, wait another iMinCheckIntervalMillis (see setCheckDone below)
				}
				setCheckDone(aCurrentTimeMillisNow);
			}
		} 
		if(log.isLoggable(Level.FINER)){
			log.exiting(CLASS_NAME, "isReloadNeeded", true);
		}
		return false;
	}

	private void setCheckDone(long aCurrentTimeMillisWhenChecked){
		iLastChecked.set(aCurrentTimeMillisWhenChecked);
	}

	public void setLoaded(T aResult, long aCurrentTimeMillisWhenLoaded){
		iResult.set(aResult);
		iLastLoaded.set(aCurrentTimeMillisWhenLoaded);
		setCheckDone(aCurrentTimeMillisWhenLoaded);
	}
	
	public T getLoaded(){
		return iResult.get();
	}

	public File getTarget() {
		return iTarget;
	}
	
	@Override
	public String toString() {
		return super.toString()+" Target:"+iTarget+" Interval:"+iMinCheckIntervalMillis+" LastCheck:"+iLastChecked.get()+" LastLoad:"+iLastLoaded.get()+" Result:"+iResult.get();
	}
}
