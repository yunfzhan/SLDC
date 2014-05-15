package org.sldc;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.sldc.assist.CSQLBuildIns;

/**
 * @version 0.2
 * @author Yunfei
 * Extension loader. Now only save method is supported.
 */
public class CSQLExtensions implements FilenameFilter, CSQLSaveInterface {
	private static final String extensionRoot = "org.sldc.extensions";
	private static final String extensionSave = extensionRoot+".save.";
	
	private static CSQLSaveInterface _instance = null;
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
	
	private static void createExtSaveClass() {
		if(_instance==null)
		{
			// Get the class path, to test if it works in jar.
			String root = new File("").getAbsolutePath()+File.separator;//CSQLExtensions.class.getProtectionDomain().getCodeSource().getLocation().getPath();// package root
			// add class search path
			addURL(new File(root));
			String path = root+extensionSave.replace(".", File.separator); // directory where extensions are lying.
			File f = new File(path.substring(path.indexOf(':')+1));
			String[] names = f.list(new CSQLExtensions());
			try{
				Class<? extends CSQLSaveInterface> csi = null;
				/*
				 * Search the first class that derives from CSQLSaveInterface and initialize it.
				 */
				for(String name : names)
					try {
						// full class name
						name = extensionSave+name.substring(0,name.indexOf(".class"));
						
						Class<?> clazz = cl.loadClass(name);
						csi = clazz.asSubclass(CSQLSaveInterface.class);
						break;
					}catch(ClassCastException ex){
						continue;
					}
				_instance = csi.newInstance();
			}catch (Exception e) {
				_instance = new CSQLExtensions();
			}
		}
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
		createExtSaveClass();
		if(_instance==null)
			CSQLBuildIns.println(o);
		else
			_instance.save(o);
	}
}
