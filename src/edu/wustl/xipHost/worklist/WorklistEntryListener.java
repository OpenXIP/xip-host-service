/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.worklist;

import java.util.EventListener;


/**
 * @author Jaroslaw Krych
 *
 */
public interface WorklistEntryListener extends EventListener {
	public void worklistEntryDataAvailable(WorklistEntryEvent e);
}
