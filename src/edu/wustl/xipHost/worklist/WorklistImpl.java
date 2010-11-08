/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.worklist;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
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
	public boolean addWorklistEntry(final WorklistEntry entry) {
		final String studyInstanceUID = entry.getStudyInstanceUID();
		if(studyInstanceUID == null || studyInstanceUID.isEmpty()){
			logger.warn("Worklist recieved StudyInstanceUID: " + studyInstanceUID);
			return false;
		}
		Runnable runner = new Runnable() {
			@Override
			public void run() {
				Application app = entry.getApplication();
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
				URL hostServiceURL = null;
				try {
					hostServiceURL = new URL("http://localhost:8080/xiphostservice/host");
				} catch (MalformedURLException e) {
					logger.error(e, e);
				}
				URL appServiceURL = appMgr.generateNewApplicationServiceURL();
				
				
				app.launch(hostServiceURL, appServiceURL);
				app.setWorklistEntry(entry);
				
			}
		};
		Thread t = new Thread(runner);
		t.start();		
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
