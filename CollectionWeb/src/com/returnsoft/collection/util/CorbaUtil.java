package com.returnsoft.collection.util;

import java.io.InputStream;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;


public class CorbaUtil {

	public final String corbaPropertyFileName = "corba.properties";
	public InitialContext initialContext;

	public void openInitialContext() {

		try {
			Properties properties = new Properties();
			InputStream input = this.getClass().getClassLoader()
					.getResourceAsStream(corbaPropertyFileName);

			if (input != null) {
				properties.load(input);
			} else {
				System.out.println("CorbaFileNotFoundException");
				
			}

			initialContext = new InitialContext(properties);

		} catch (NamingException e) {
			e.printStackTrace();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}

	}

	public void closeInitialContext() {

		try {
			if (initialContext != null) {

				initialContext.close();

			}
		} catch (NamingException e) {
			e.printStackTrace();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}

	}
	
	public Object getService(String service) {

		try {

			openInitialContext();
			//System.out.println("antes del lookup");
			Object serverEventService = initialContext.lookup(service);
			//System.out.println("despues del lookup");
			closeInitialContext();

			return serverEventService;

		} catch (NamingException e) {
			e.printStackTrace();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;

	}
	


}
