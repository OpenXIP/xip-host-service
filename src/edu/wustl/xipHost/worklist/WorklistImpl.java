/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.worklist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jws.WebService;
import org.dcm4che2.data.Tag;
import edu.wustl.xipHost.application.ApplicationManager;
import edu.wustl.xipHost.application.ApplicationManagerFactory;
import edu.wustl.xipHost.avt2ext.ADQueryTarget;
import edu.wustl.xipHost.avt2ext.AVTQuery;

/**
 * @author Jaroslaw Krych
 *
 */
@WebService(
		serviceName = "Worklist",
        portName="WorklistPort",
        targetNamespace = "http://edu.wustl.xipHost.worklist/",
        endpointInterface = "edu.wustl.xipHost.worklist.Worklist")
public class WorklistImpl implements Worklist{
	ApplicationManager appMgr = ApplicationManagerFactory.getInstance();
	@Override
	public boolean addWorklistEntry(WorklistEntry entry) {
		
		System.out.println(appMgr.hashCode());
		//Application app = entry.getApplication();
		//appMgr.addApplication(app);
		//String studyInstanceUID = entry.getStudyInstanceUID();
		Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
		Map<String, Object> aimCriteria = new HashMap<String, Object>();			
		dicomCriteria.put(Tag.StudyInstanceUID, "1.2.840.113704.1.111.3124.1224538480.7");
		AVTQuery avtQuery = new AVTQuery(dicomCriteria, aimCriteria, ADQueryTarget.STUDY, null, null);
		avtQuery.run();
		return false;
	}

	@Override
	public boolean deleteWorkListEntry(WorklistEntry entry) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getNumberOfWorklistEntries() {
		
		return 1;
	}

	@Override
	public List<WorklistEntry> getWorklistEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WorklistEntry getWorklistEntry(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean modifyWorklistEntry(WorklistEntry entry) {
		// TODO Auto-generated method stub
		return false;
	}
	
	WorklistEntryListener listener;
    public void addWorklistEntryListener(WorklistEntryListener l) {        
        listener = l;          
    }
	void fireResultsAvailable(WorklistEntry entry){
		
		
		//WorklistEntryEvent event = new WorklistEntryEvent(entry);         		
        //listener.worklistEntryDataAvailable(event);
	}

}
