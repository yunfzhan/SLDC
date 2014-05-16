package org.sldc;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.sldc.assist.CSQLBuildIns;

/**
 * @version 0.2
 * @author Yunfei
 * Extension loader. Now only save method is supported.
 */
public class CSQLExtensions implements FilenameFilter, ISaveInterface {
	private static final String extensionRoot = "org.sldc.extensions";
	
	private static ISaveInterface _instance = null;
	private static URLClassLoader cl = (URLClassLoader)CSQLExtensions.class.getClassLoader();
	
	private static Method addURL = initAddMethod();
	
	private static Method initAddMethod() {
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
            add.setAccessible(true);
            return add;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	private static void addURL(File file) {
        try {
            addURL.invoke(cl, new Object[] { file.toURI().toURL() });
        } catch (Exception e) {
        }
    }
	
	private static ArrayList<Class<?>> extLoadAssist(String relativePath) throws ClassNotFoundException {
		// Get the class path, to test if it works in jar.
		String root = new File("").getAbsolutePath()+File.separator;//CSQLExtensions.class.getProtectionDomain().getCodeSource().getLocation().getPath();// package root
		// add class search path
		addURL(new File(root));
		String path = root+relativePath.replace(".", File.separator); // directory where extensions are lying.
		File f = new File(path.substring(path.indexOf(':')+1));
		String[] names = f.list(new CSQLExtensions());
		ArrayList<Class<?>> clazz = null;
		if(names!=null) {
			clazz = new ArrayList<Class<?>>();
			for(String name : names)
			{
				// full class name
				name = relativePath+name.substring(0,name.indexOf(".class"));
				clazz.add(cl.loadClass(name));
			}
		}
		return clazz;
	}
	
	public static ISaveInterface createExtSaveClass() {
		final String extensionSave = extensionRoot+".save.";
		if(_instance==null)
		{
			try{
				Class<? extends ISaveInterface> csi = null;
				/*
				 * Search the first class that derives from ISaveInterface and initialize it.
				 */
				ArrayList<Class<?>> classes = extLoadAssist(extensionSave);
				for(Class<?> clazz : classes)
					try {
						csi = clazz.asSubclass(ISaveInterface.class);
						break;
					}catch(ClassCastException ex){
						continue;
					}
				_instance = csi.newInstance();
			}catch (Exception e) {
				_instance = new CSQLExtensions();
			}
		}
		return _instance;
	}

	@Override
	public boolean accept(File dir, String name) {
		if(new File(dir,name).isFile()) {
			return name.endsWith(".class");
		}
		return false;
	}

	/**
	 * Default implementation for save
	 */
	@Override
	public void save(Object o) {
		CSQLBuildIns.println(o);
	}
}
