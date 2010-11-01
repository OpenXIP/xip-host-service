/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import java.net.URL;
import javax.xml.ws.Endpoint;
import org.nema.dicom.wg23.Host;
import edu.wustl.xipHost.application.ApplicationManager;
import edu.wustl.xipHost.application.ApplicationManagerFactory;
import edu.wustl.xipHost.wg23.HostImpl;

/**
 * @author Jaroslaw Krych
 *
 */
public class HostServicePublisher {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Host host = new HostImpl();	
		ApplicationManager appMgr = ApplicationManagerFactory.getInstance();
		URL hostServiceURL = appMgr.generateNewHostServiceURL();
		System.out.println(hostServiceURL);
		Endpoint.publish(hostServiceURL.toString(), host);
	}

}
