package com.returnsoft.collection.test;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.MailingService;
import com.returnsoft.collection.util.CorbaUtil;

public class MailingServiceTest {
	private static CorbaUtil corbaUtil;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		corbaUtil = new CorbaUtil();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		
        
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		try {
			
			MailingService mailingService = (MailingService)corbaUtil.getService("java:global/CollectionWeb/MailingServiceImpl!com.returnsoft.collection.service.MailingService");
			
			mailingService.mailerDaemon();
			
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
