package org.sldc;

import java.io.File;
import java.io.FilenameFilter;

import org.sldc.assist.CSQLBuildIns;

/**
 * @version 0.2
 * @author Yunfei
 * Extension loader. Now only save method is supported.
 */
public class CSQLExtensions implements FilenameFilter, CSQLSaveInterface {
	private static final String extensionRoot = "org.sldc.extensions";
	private static final String extensionSave = extensionRoot+".save.";
	
	public static CSQLSaveInterface getExtSaveClass() {
		// Get the class path, to test if it works in jar.
		String path = CSQLExtensions.class.getProtectionDomain().getCodeSource().getLocation()+extensionSave.replace(".", File.separator);
		path = path.substring(path.indexOf(':')+1);
		File f = new File(path);
		String[] names = f.list(new CSQLExtensions());
		try{
			Class<? extends CSQLSaveInterface> csi = null;
			/*
			 * Search the first class that derives from CSQLSaveInterface and initialize it.
			 */
			for(int i=0;i<names.length;i++)
				try {
					String name = extensionSave+names[i];
					Class<?> clazz = Class.forName(name);
					csi = clazz.asSubclass(CSQLSaveInterface.class);
				}catch(ClassCastException ex){
					continue;
				}
			return csi.newInstance();
		}catch (Exception e) {
			return new CSQLExtensions();
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
		CSQLBuildIns.println(o);
	}
}
