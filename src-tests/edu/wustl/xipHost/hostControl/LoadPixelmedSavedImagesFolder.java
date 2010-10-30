/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import java.io.File;

import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class LoadPixelmedSavedImagesFolder extends TestCase {
	HostConfigurator hostConfigurator;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		hostConfigurator = new HostConfigurator();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	//HostConfigurator 1A - basic flow. serverConfig file exists 
	//and value for Application.SavedImageFolderName property can be converted into an existing directory.
	//Expected result: boolean true
	public void testLoadPixelmedSavedImagesFolder1A() {	
		File serverConfig = new File("./src-tests/edu/wustl/xipHost/hostControl/workstation1.properties");
		assertTrue("Config file exists but system was unable to load parameters.",hostConfigurator.loadPixelmedSavedImagesFolder(serverConfig));
		System.out.println("Value " + hostConfigurator.getPixelmedSavedImagesFolder());
		assertEquals("./src-tests/edu/wustl/xipHost/hostControl/WORKSTATION1_Test", hostConfigurator.getPixelmedSavedImagesFolder());		
	}
	
	//HostConfigurator 1Ba - basic flow. serverConfig file is null 
	//Expected result: boolean false
	public void testLoadPixelmedSavedImagesFolder1Ba() {	
		File serverConfig = null;
		assertFalse("Error when loading config file null.",hostConfigurator.loadPixelmedSavedImagesFolder(serverConfig));
	}
	
	//HostConfigurator 1Bb - basic flow. serverConfig file does not exist. 
	//Expected result: boolean false
	public void testLoadPixelmedSavedImagesFolder1Bb() {	
		File serverConfig = new File("./src-tests/edu/wustl/xipHost/hostControl/workstation1_nonexisting.properties");
		assertFalse("Error when loading with non existing config file.",hostConfigurator.loadPixelmedSavedImagesFolder(serverConfig));
	}
	
	//HostConfigurator 1C - basic flow. serverConfig file exists 
	//but Application.SavedImageFolderName cannot be used as an existing directory 
	//Expected result: boolean false
	public void testLoadPixelmedSavedImagesFolder1C() {	
		File serverConfig = new File("./src-tests/edu/wustl/xipHost/hostControl/workstation1C.properties");
		assertTrue("Specified dir does not exist but system did not detect this problem.",hostConfigurator.loadPixelmedSavedImagesFolder(serverConfig));
		//When directory does not exist empty string should be used for the value of teh directory
		assertEquals("", hostConfigurator.getPixelmedSavedImagesFolder());
	}

}
