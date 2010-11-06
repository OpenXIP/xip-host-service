/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.worklist;

import java.util.List;
import javax.jws.WebService;

/**
 * @author Jaroslaw Krych
 *
 */
@WebService(name = "Worklist", targetNamespace = "http://edu.wustl.xipHost.worklist/")
public interface Worklist {
	public boolean addWorklistEntry(WorklistEntry entry);
	public boolean modifyWorklistEntry(WorklistEntry entry);
	public boolean deleteWorkListEntry(WorklistEntry entry);	
	public List<WorklistEntry> getWorklistEntries();
	public int getNumberOfWorklistEntries();
	public WorklistEntry getWorklistEntry(int i);	
}