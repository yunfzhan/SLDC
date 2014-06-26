package org.sldc;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.sldc.assist.CSQLBuildIns;

/**
 * @version 0.3
 * @author Yunfei
 * Extension loader.
 */
public class CSQLExtensions implements FilenameFilter, ISaveInterface {
	private static final String extensionRoot = "org.sldc.extensions";
	private static final String extJarLibrary = "extensions.jar";
	
	private static ISaveInterface _instance = null;
	private static ArrayList<Class<?>> classes = null;
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
		String root = new File("").getAbsolutePath()+File.separator;
		//String root = CSQLExtensions.class.getProtectionDomain().getCodeSource().getLocation().getPath();// package root
		ArrayList<Class<?>> clazz = null;
		
		File extLibrary = new File(root+extJarLibrary);
		if(extLibrary.exists()){
			//Load jar library. It has a fixed name 'extensions.jar'
			try {
				String path = relativePath.replace(".",File.separator);
				ZipFile jar = new ZipFile(extLibrary);
				Enumeration<? extends ZipEntry> entries = jar.entries();
				
				URLClassLoader jarcl = new URLClassLoader(new URL[] { new URL("file:"+extLibrary) }, cl);
				try{
					if(entries==null) throw new Exception();
					while(entries.hasMoreElements())
					{
						ZipEntry entry = entries.nextElement();
						String name = entry.getName();
						if(name.startsWith(path)&&name.endsWith(".class")) {
			                name = name.replace(File.separator, ".").substring(0,name.indexOf(".class"));
			                if(clazz==null) clazz = new ArrayList<Class<?>>();
							clazz.add(jarcl.loadClass(name));
						}
					}
				}finally{
					jarcl.close();
					jar.close();
				}
			} catch (Exception e) {
				throw new ClassNotFoundException();
			}
		}else{
			// add class search path
			addURL(new File(root));
			String path = root+relativePath.replace(".", File.separator); // directory where extensions are lying.
			File f = new File(path.substring(path.indexOf(':')+1));
			String[] names = f.list(new CSQLExtensions());
			
			if(names!=null) {
				clazz = new ArrayList<Class<?>>();
				for(String name : names)
				{
					// full class name
					name = relativePath+name.substring(0,name.indexOf(".class"));
					clazz.add(cl.loadClass(name));
				}
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

	public static Object execExtFunction(String name, Object[] params) throws NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		try {
			if(classes==null) classes = extLoadAssist(extensionRoot+".function.");
			Class<? extends IFunctionInterface> cfi = null;
			for(Class<?> clazz : classes)
				try{
					cfi = clazz.asSubclass(IFunctionInterface.class);
					IFunctionInterface instance = cfi.newInstance();
					Method m = instance.getMethodByNameParam(name, params);
					if(m!=null)
					{
						return m.invoke(instance, params);
					}
				}catch(ClassCastException ex){
					continue;
				} catch (InstantiationException e) {
					continue;
				} catch (IllegalAccessException e) {
					continue;
				} catch (NoSuchMethodException e) {
					continue;
				}
		} catch (Exception e) {}
		throw new NoSuchMethodException();
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
	public void save(SaveObject o) {
		CSQLBuildIns.print(o);
	}
}
