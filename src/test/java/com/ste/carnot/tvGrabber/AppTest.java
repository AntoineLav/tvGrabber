package com.ste.carnot.tvGrabber;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

	private Logger logger = LoggerFactory.getLogger(AppTest.class);
	
	public void testParseDate() {
		Master master = new Master();
		try {
			Date date = new SimpleDateFormat("YYYYMMddhhmmss").parse("20120930005500");
			assertEquals(date, master.parseDate("20120930005500 +0200"));
		}
		catch(Exception e) {
			logger.error("Bug: {}", e.toString());
		}
		
	}
}
