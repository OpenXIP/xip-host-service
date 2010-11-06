/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.worklist;

import java.io.File;
import java.util.List;
import javax.jws.WebService;
import org.apache.log4j.Logger;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.application.ApplicationManager;
import edu.wustl.xipHost.application.ApplicationManagerFactory;

/**
 * @author Jaroslaw Krych
 *
 */
@WebService(
		serviceName = "Worklist",
        portName="WorklistPort",
        targetNamespace = "http://edu.wustl.xipHost.worklist/",
        endpointInterface = "edu.wustl.xipHost.worklist.Worklist")
public class WorklistImpl implements Worklist {
	final static Logger logger = Logger.getLogger(WorklistImpl.class);
	ApplicationManager appMgr = ApplicationManagerFactory.getInstance();
	@Override
	public boolean addWorklistEntry(WorklistEntry entry) {
		String studyInstanceUID = entry.getStudyInstanceUID();
		Application app = entry.getApplication();
		if(studyInstanceUID == null || studyInstanceUID.isEmpty()){
			logger.warn("Worklist recieved StudyInstanceUID: " + studyInstanceUID);
			return false;
		}
		if(logger.isDebugEnabled()){
			logger.debug("Worklist recieved StudyInstanceUID: " + studyInstanceUID);
			if(app != null){
				logger.debug("Worklist recieved application: ");
				logger.debug("ID: " + app.getId());
				logger.debug("Name: " + app.getName());
				logger.debug("Type:" + app.getType());
				logger.debug("Requires GUI: " + app.isRequiresGUI());
				logger.debug("Iteration target: " + app.getIterationTarget().toString());
				logger.debug("Allowable concurrent instances: " + app.getConcurrentInstances());
				logger.debug("WG-23 data model type: " + app.getWg23DataModelType());
			} else {
				logger.debug("Worklist recieved application: " + app);
			}
		}
		if(app != null && appMgr.hasApplication(app.getId()) == false){
			appMgr.addApplication(app);
		}
		appMgr.setTmpDir(new File("C:/TmpXIP"));
		appMgr.setOutputDir(new File("C:/OutXIP"));
		File tmpDir = appMgr.getTmpDir();
		File outDir = appMgr.getOutputDir();
		app.setApplicationTmpDir(tmpDir);
		app.setApplicationOutputDir(outDir);
		app.setWorklistEntry(entry);
		//appMgr.setEntryAvailable(entry);
		/*
		Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
		Map<String, Object> aimCriteria = new HashMap<String, Object>();			
		dicomCriteria.put(Tag.StudyInstanceUID, studyInstanceUID);
		AVTQuery avtQuery = new AVTQuery(dicomCriteria, aimCriteria, ADQueryTarget.STUDY, null, null);
		avtQuery.run();*/
		return true;
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
}
