/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext.iterator;

import org.nema.dicom.wg23.AvailableData;
import edu.wustl.xipHost.application.Application;

/**
 * @author Jaroslaw Krych
 *
 */
public class NotificationRunner implements Runnable {
	Application application;
	/**
	 * 
	 */
	public NotificationRunner(Application application) {
		this.application = application;
	}

	AvailableData availableData;
	public void setAvailableData(AvailableData availableData){
		this.availableData = availableData;
	}
	
	@Override
	public void run() {
		application.getClientToApplication().notifyDataAvailable(availableData, true);
	}
}
