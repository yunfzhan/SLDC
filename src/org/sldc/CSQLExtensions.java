package org.sldc;

import java.io.File;
import java.io.FilenameFilter;
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
	
	public static CSQLSaveInterface getExtSaveClass() {
		if(_instance==null)
		{
			// Get the class path, to test if it works in jar.
			String root = CSQLExtensions.class.getProtectionDomain().getCodeSource().getLocation().toString();// package root
			String path = root+extensionSave.replace(".", File.separator); // directory where extensions are lying.
			File f = new File(path.substring(path.indexOf(':')+1));
			String[] names = f.list(new CSQLExtensions());
			try{
				URLClassLoader ucl = new URLClassLoader(new URL[]{new URL(root)});
				Class<? extends CSQLSaveInterface> csi = null;
				/*
				 * Search the first class that derives from CSQLSaveInterface and initialize it.
				 */
				for(String name : names)
					try {
						// full class name
						name = extensionSave+name.substring(0,name.indexOf(".class"));
						Class<?> clazz = ucl.loadClass(name);
						csi = clazz.asSubclass(CSQLSaveInterface.class);
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
