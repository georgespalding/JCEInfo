package se.op.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestFileChangeDetector {
	File existingFile=new File("existingFile.tmp");
	File newFile=new File("newFile.tmp");
	File missing=new File("missing");

	@Before
	public void setup() throws IOException{
		existingFile.createNewFile();
		existingFile.deleteOnExit();
	}
	
	@After
	public void tearDown() throws IOException{
		existingFile.delete();
		newFile.delete();
	}

	/**
	 * En fil som finns och kollas varje ögonblick men ska bara laddas om var 10:e ögonblick
	 */
	@Test
	public void testNormal() throws IOException{
		File file=File.createTempFile("test", "temp");
		file.deleteOnExit();
		
		int numChanges=0;
		int numDetectedReloads=0;
		long now=1;
		file.setLastModified(now);
		FileChangeDetector fdc=new FileChangeDetector(existingFile, 10, now);
		for(;now < 101;now++){
			if(fdc.shouldReload(now)){
				fdc.setLoaded(now);
				assertFalse("Should not signal reload right after load", fdc.shouldReload(now));
				numDetectedReloads++;
			}
			if(now%10==0){
				numChanges++;
				file.setLastModified(now);
			}
		}
		
		assertEquals("Unexpected number of reloads",numChanges, numDetectedReloads);
	}

	/**
	 * En ändrad fil som finns och kollas varje ögonblick men aldrig skall laddas om
	 */
	@Test
	public void testNever() throws IOException{
		File file=File.createTempFile("test", "temp");
		file.deleteOnExit();
		file.setLastModified(1);
		
		int numReloads=0;
		long now=1;
		FileChangeDetector fdc=new FileChangeDetector(existingFile, -1, now);
		for(;now < 111;now++){
			assertFalse("Should never reload",fdc.shouldReload(now));
			if(now%10==0){
				file.setLastModified(now);
			}
		}
		
	}

	@Test
	public void testExistingChanged() throws InterruptedException{
		existingFile.setLastModified( TimeUnit.SECONDS.toMillis(10));

		FileChangeDetector fdc=new FileChangeDetector(existingFile, TimeUnit.SECONDS.toMillis(5), TimeUnit.SECONDS.toMillis(11));
		assertFalse("False signal to reload", fdc.shouldReload(TimeUnit.SECONDS.toMillis(13)));
		
		assertFalse("Missing signal to reload", !fdc.shouldReload(TimeUnit.SECONDS.toMillis(17)));

		existingFile.setLastModified( TimeUnit.SECONDS.toMillis(10));
		
		fdc.setLoaded();
		assertFalse("False signal to reload after load", fdc.shouldReload());
		
		assertFalse("False signal to reload after load and sleep", fdc.shouldReload());
	}

	@Test
	public void testMissing() throws InterruptedException{
		FileChangeDetector fdc=new FileChangeDetector(missing, TimeUnit.SECONDS.toMillis(1));
		
		assertFalse("False signal to reload", fdc.shouldReload());
		
		fdc.setLoaded();
		assertFalse("False signal to reload after load directly", fdc.shouldReload());

		TimeUnit.MILLISECONDS.sleep(100);
		assertFalse("False signal to reload after load and sleep", fdc.shouldReload());
	}
}
