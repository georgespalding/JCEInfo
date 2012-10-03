package se.op.util;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Used for detecting whether the Main Menu definition has changed on disk.
 */
public class MenuCache<T> {
	private static final Logger log=Logger.getLogger(MenuCache.class.getName());

	private final long iMinTimeBetweenChecksMillis;

	// list of main menu 
	private final ConcurrentHashMap<String, FileChangeMonitor<T>> iMainMenuMap = new ConcurrentHashMap<String, FileChangeMonitor<T>>();
	private final FileBasedProducer<T> iMenuProvider;
	
	public MenuCache(FileBasedProducer<T> mmp, long aMinTimeBetweenChecksMillis){
		iMinTimeBetweenChecksMillis = aMinTimeBetweenChecksMillis;
		this.iMenuProvider=mmp;
	}

	public T getMenu(Locale aLocale) {
		File tFile = iMenuProvider.getMaterialFiles();
		String tMainMenuKey = tFile+"_"+aLocale;

		long tNow = System.currentTimeMillis();
		FileChangeMonitor<T> tFileChangeMon = iMainMenuMap.get(tMainMenuKey);
		if( tFileChangeMon==null ){
			tFileChangeMon = new FileChangeMonitor<T>(tFile, iMinTimeBetweenChecksMillis);
			iMainMenuMap.put(tMainMenuKey, tFileChangeMon);
		}

		if(tFileChangeMon.isReloadNeeded(tNow)){
			log.config("Load Front Office Portal Main Menu from "+tFileChangeMon.getTarget()+".");
			T tResult=iMenuProvider.produce(aLocale);
			tFileChangeMon.setLoaded(tResult, tNow);
			return tResult;
		} else {
			return tFileChangeMon.getLoaded();
		}
	}
	
}
