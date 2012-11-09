package se.op.util.reload;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandlerFactory;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ParentLastResourceClassLoader extends URLClassLoader{
	private final ConcurrentHashMap<String,Long> iLoadedResources=new ConcurrentHashMap<>();
	
	public ParentLastResourceClassLoader(URL... urls) {
		super(urls);
	}

	public ParentLastResourceClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public ParentLastResourceClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory urlStreamHandlerFactory ) {
		super(urls, parent, urlStreamHandlerFactory);
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		// TODO Auto-generated method stub
		return super.getResourceAsStream(name);
	}
	
	@Override
	public URL getResource(String name) {
        URL url = findResource(name);
        if (url == null) {
        	url = super.getResource(name);
        } else {
        	iLoadedResources.put(name,System.currentTimeMillis());
        }
        return url;
    }

//	public boolean isModifiedSinceLoad(String name){
//		Long whenLoaded=iLoadedResources.get(name);
//		if (whenLoaded != null) {
//            URL url = findResource(name);
//            whenLoaded=lastModified(url);
//            iLoadedResources.put(name, whenLoaded);
//            return true;
//        } else 
//        	return whenLoaded;
//	}
	
	
	private Long lastModified(URL url){
        if (url == null) {
        	return null;
        }

        switch(url.getProtocol()){
/* for a rainy day
        case "http":
        case "https":
        	HttpURLConnection uc = null;
        	try{
        		uc = (HttpURLConnection)url.openConnection();
        		uc.setIfModifiedSince(whenLoaded.longValue());
        		uc.setAllowUserInteraction(false);
        		uc.setRequestMethod("HEAD");
        		uc.setDoInput(false);
        		uc.setDoOutput(false);
        		uc.connect();
        		switch(uc.getResponseCode()){
        		case HttpURLConnection.HTTP_NOT_MODIFIED:
        			return Long.valueOf(0L);
        		case HttpURLConnection.HTTP_OK:
        			return uc.getLastModified();
        		default:
        			//Debug when does this happen?
            			
        		}
        		uc.getResponseCode();
        	} catch (ProtocolException e) {
        		//FIXME
        		e.printStackTrace();
        	} catch (IOException e) {
        		//FIXME
        		e.printStackTrace();
        	}finally{
        		if(null!=uc){
        			uc.disconnect();
        		}
        	}
        	break;*/
        case "file":
        	File f=new File(url.getPath());
        	return f.lastModified();
        case "jar":
        	JarURLConnection juc = null;
        	try{
        		juc = (JarURLConnection)url.openConnection();
        		lastModified(juc.getJarFileURL());
        	} catch (IOException e) {
        		//FIXME
        		e.printStackTrace();
        	}
        	break;
        }
        
        return null;
	}

public static void main(String[] args) throws IOException, InterruptedException {
	final String RES="test.date";
	ParentLastResourceClassLoader p=new ParentLastResourceClassLoader(new URL("file:/tmp/test.jar"));
	System.err.println("test.date:"+p.getResource(RES)+": "+ new BufferedReader(new InputStreamReader(p.getResourceAsStream(RES))).readLine());
	TimeUnit.SECONDS.sleep(10);
	System.err.println("close");
	p.close();
	
	p=new ParentLastResourceClassLoader(new URL("file:/tmp/test.jar"));
	System.err.println("test.date:"+p.getResource(RES)+": "+ new BufferedReader(new InputStreamReader(p.getResourceAsStream(RES))).readLine());
	TimeUnit.SECONDS.sleep(10);
	System.err.println("test.date:"+p.getResource(RES)+": "+ new BufferedReader(new InputStreamReader(p.getResourceAsStream(RES))).readLine());
	System.err.println("close");
	p.close();
	
}
}
