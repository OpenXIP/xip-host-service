/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.worklist;

import java.util.EventObject;

/**
 * @author Jaroslaw Krych
 *
 */
public class WorklistEntryEvent extends EventObject {
	public WorklistEntryEvent(WorklistEntry source){	
		super(source);
	}
}
