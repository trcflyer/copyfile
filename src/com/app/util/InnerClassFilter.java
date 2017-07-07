package com.app.util;

import java.io.File;
import java.io.FilenameFilter;

/** 
 * InnerClassFilter.java
 * 
 * @author  sun.jun
 * @version $Revision: 1294 $ $Date: 2014-12-19 23:25:10 +0800 (Fri, 19 Dec 2014) $
 * @serial 
 * @since 2010-9-26 下午04:56:10
 */
public class InnerClassFilter implements FilenameFilter {

	private String prefix;
	
	public InnerClassFilter(String prefix) {
		this.prefix = prefix;
	}
	
	public boolean accept(File dir, String name) {
		return name.startsWith(prefix) && name.endsWith(".class");
	}

}
