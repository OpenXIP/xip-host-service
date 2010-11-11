/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;
import org.dcm4che2.data.Tag;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Rectangle;
import org.nema.dicom.wg23.Series;
import org.nema.dicom.wg23.State;
import org.nema.dicom.wg23.Study;
import org.nema.dicom.wg23.Uuid;
import edu.wustl.xipHost.avt2ext.ADQueryTarget;
import edu.wustl.xipHost.avt2ext.ADRetrieveTarget;
import edu.wustl.xipHost.avt2ext.AVTListener;
import edu.wustl.xipHost.avt2ext.AVTQuery;
import edu.wustl.xipHost.avt2ext.AVTRetrieve2;
import edu.wustl.xipHost.avt2ext.AVTRetrieve2Event;
import edu.wustl.xipHost.avt2ext.AVTRetrieve2Listener;
import edu.wustl.xipHost.avt2ext.AVTRetrieveEvent;
import edu.wustl.xipHost.avt2ext.AVTSearchEvent;
import edu.wustl.xipHost.avt2ext.AVTUtil;
import edu.wustl.xipHost.avt2ext.iterator.IterationTarget;
import edu.wustl.xipHost.avt2ext.iterator.IteratorElementEvent;
import edu.wustl.xipHost.avt2ext.iterator.IteratorEvent;
import edu.wustl.xipHost.avt2ext.iterator.NotificationRunner;
import edu.wustl.xipHost.avt2ext.iterator.TargetElement;
import edu.wustl.xipHost.avt2ext.iterator.TargetIteratorListener;
import edu.wustl.xipHost.avt2ext.iterator.TargetIteratorRunner;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.hostControl.Util;
import edu.wustl.xipHost.wg23.ClientToApplication;
import edu.wustl.xipHost.wg23.StateExecutor;
import edu.wustl.xipHost.wg23.WG23DataModel;
import edu.wustl.xipHost.worklist.WorklistEntry;

public class Application implements AVTListener, TargetIteratorListener, AVTRetrieve2Listener {	
	final static Logger logger = Logger.getLogger(Application.class);
	UUID id;
	String name;
	File exePath;
	String vendor;
	String version;
	File iconFile;
	String type;
	boolean requiresGUI;
	String wg23DataModelType;
	int concurrentInstances;
	IterationTarget iterationTarget;
	int numStateNotificationThreads = 2;
	ExecutorService exeService = Executors.newFixedThreadPool(numStateNotificationThreads);	
	AVTUtil util = new AVTUtil();
	
	public Application(){
		
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public File getExePath() {
		return exePath;
	}

	public void setExePath(File exePath) {
		this.exePath = exePath;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public File getIconFile() {
		return iconFile;
	}

	public void setIconFile(File iconFile) {
		this.iconFile = iconFile;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isRequiresGUI() {
		return requiresGUI;
	}

	public void setRequiresGUI(boolean requiresGUI) {
		this.requiresGUI = requiresGUI;
	}

	public String getWg23DataModelType() {
		return wg23DataModelType;
	}

	public void setWg23DataModelType(String wg23DataModelType) {
		this.wg23DataModelType = wg23DataModelType;
	}

	public int getConcurrentInstances() {
		return concurrentInstances;
	}

	public void setConcurrentInstances(int concurrentInstances) {
		this.concurrentInstances = concurrentInstances;
	}

	public IterationTarget getIterationTarget() {
		return iterationTarget;
	}

	public void setIterationTarget(IterationTarget iterationTarget) {
		this.iterationTarget = iterationTarget;
	}
	
	File appTmpDir;
	public void setApplicationTmpDir(File tmpDir){
		appTmpDir = tmpDir;
	}
	public File getApplicationTmpDir() {
		return appTmpDir;		
	}
	
	File appOutputDir;
	public void setApplicationOutputDir(File outDir){				
		try {
			appOutputDir = Util.create("xipOUT_" + getName() + "_", "", outDir);
		} catch (IOException e) {
			logger.error(e, e);
		}		
	}
	public File getApplicationOutputDir() {		
		return appOutputDir;
	}
	
	public Rectangle getApplicationPreferredSize() {		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();	
		Rectangle rect = new Rectangle();
		logger.debug("Screen width: " + new Double(screenSize.getWidth()).intValue());
		logger.debug("Screen height: " + new Double(screenSize.getHeight()).intValue());
		rect.setWidth(new Double(screenSize.getWidth()).intValue());
		rect.setHeight(new Double(screenSize.getHeight()).intValue());
		return rect;
	}
	
	URL hostServiceURL;
	URL appServiceURL;
	public URL getApplicationServiceURL(){
		return appServiceURL;
	}
	
	public void launch(URL hostServiceURL, URL appServiceURL){
		this.hostServiceURL = hostServiceURL;
		this.appServiceURL = appServiceURL;	
		if(logger.isDebugEnabled()){
			logger.info("Launching: " + getName());
			logger.debug("Host service URL: " + this.hostServiceURL.toExternalForm().toString());
			logger.debug("Hosted application service URL: " + this.appServiceURL.toExternalForm().toString());
			logger.debug("Exe path: " + this.getExePath().getAbsolutePath());
		}
		try {
			if(getExePath().toURI().toURL().toExternalForm().endsWith(".exe") || getExePath().toURI().toURL().toExternalForm().endsWith(".bat")){
				try {																							
					Runtime.getRuntime().exec("cmd /c start /min " + getExePath().toURI().toURL().toExternalForm() + " " + "--hostURL" + " " + hostServiceURL.toURI().toURL().toExternalForm() + " " + "--applicationURL" + " " + appServiceURL.toURI().toURL().toExternalForm());
				} catch (IOException e) {			
					logger.error(e, e);				
				} catch (URISyntaxException e) {
					logger.error(e, e);
				}			
			}
		} catch (MalformedURLException e) {
			logger.error(e, e);
		} 
	}
	
	State priorState = null;
	State state = null;
	boolean firstLaunch = true;
	int numberOfSentNotifications = 0;
	MultiMap wg23DataModelItems = new MultiValueMap();
	Thread threadNotification;
	@SuppressWarnings("unchecked")
	public void setState(State state){
		priorState = this.state;
		this.state = state;
		logger.debug("\"" + getName() + "\"" + " state changed to: " + this.state);
		if (state.equals(State.IDLE)){
			if(firstLaunch){
				startClientToApplication();
				firstLaunch = false;
				StateExecutor stateExecutor = new StateExecutor(this);
				stateExecutor.setState(State.INPROGRESS);
				exeService.execute(stateExecutor);
			} else {
				synchronized(this){
					if(iter != null){
						boolean doInprogress = true;
						synchronized(targetElements){
							if(numberOfSentNotifications == targetElements.size()){
								doInprogress = false;
							}
						}
						if(doInprogress == false){
							StateExecutor stateExecutor = new StateExecutor(this);
							stateExecutor.setState(State.EXIT);
							exeService.execute(stateExecutor);
						} else {
							StateExecutor stateExecutor = new StateExecutor(this);
							stateExecutor.setState(State.INPROGRESS);
							exeService.execute(stateExecutor);
						}			
					} else {
						StateExecutor stateExecutor = new StateExecutor(this);
						stateExecutor.setState(State.INPROGRESS);
						exeService.execute(stateExecutor);
					}
				}
			}
		} else if(state.equals(State.INPROGRESS)){
			synchronized(targetElements){
				while(targetElements.size() == 0 || targetElements.size() <= numberOfSentNotifications){
					try {
						targetElements.wait();
					} catch (InterruptedException e) {
						logger.error(e, e);
					}
				}
				TargetElement element = targetElements.get(numberOfSentNotifications);
				WG23DataModel wg23data = util.getWG23DataModel(element);
				//Extract all ObjectDescriptors UUIDs and construct MultiValueMap containing UUID (key), ObjectDescriptor (value), ObjectLocator (value) 
				List<ObjectDescriptor> objDescsTopLevel = wg23data.getAvailableData().getObjectDescriptors().getObjectDescriptor();
				for(ObjectDescriptor objDesc : objDescsTopLevel){
					String uuid = objDesc.getUuid().getUuid();
					wg23DataModelItems.put(uuid, objDesc);
				}
				List<org.nema.dicom.wg23.Patient> listPatients = wg23data.getAvailableData().getPatients().getPatient();
				for(org.nema.dicom.wg23.Patient patient : listPatients){
					List<ObjectDescriptor> objDescsPatientLevel = patient.getObjectDescriptors().getObjectDescriptor();
					for(ObjectDescriptor objDesc : objDescsPatientLevel){
						String uuid = objDesc.getUuid().getUuid();
						wg23DataModelItems.put(uuid, objDesc);
					}
					List<Study> listStudies = patient.getStudies().getStudy();
					for(Study study : listStudies){
						List<ObjectDescriptor> objDescsStudyLevel = study.getObjectDescriptors().getObjectDescriptor();
						for(ObjectDescriptor objDesc : objDescsStudyLevel){
							String uuid = objDesc.getUuid().getUuid();
							wg23DataModelItems.put(uuid, objDesc);
						}
						List<Series> listSeries = study.getSeries().getSeries();
						for(Series series : listSeries){
							List<ObjectDescriptor> objDescsSeriesLevel = series.getObjectDescriptors().getObjectDescriptor();
							for(ObjectDescriptor objDesc : objDescsSeriesLevel){
								String uuid = objDesc.getUuid().getUuid();
								wg23DataModelItems.put(uuid, objDesc);
							}	
						}
					}
				}
				Iterator<String> iterMultiValueMap = wg23DataModelItems.keySet().iterator();
				ObjectLocator[] objLocators = wg23data.getObjectLocators();
				while(iterMultiValueMap.hasNext()){
					String uuid = iterMultiValueMap.next();
					for(int i = 0; i < objLocators.length; i++){
						ObjectLocator objLoc = objLocators[i];
						String locUUID = objLoc.getUuid().getUuid();
						if(locUUID.equalsIgnoreCase(uuid)){
							wg23DataModelItems.put(uuid, objLoc);
						}
					}
				}
				NotificationRunner runner = new NotificationRunner(this);
				runner.setAvailableData(wg23data.getAvailableData());
				threadNotification = new Thread(runner);
				threadNotification.start();
				numberOfSentNotifications++;
			}
		} else if (state.equals(State.EXIT)){
			//Reset application parameters for subsequent launch
			firstLaunch = true;
			retrievedTargetElements.clear();
			targetElements.clear();
			iter = null;
			numberOfSentNotifications = 0;
			retrievedTargetElements.clear();
		}
	}
	
	public State getState(){
		return state;
	}
	public State getPriorState(){
		return priorState;
	}

	ClientToApplication clientToApplication;
	public void startClientToApplication(){
		clientToApplication = new ClientToApplication(appServiceURL);
	}
	
	public ClientToApplication getClientToApplication(){
		return clientToApplication;
	}
	
	AVTQuery avtQuery;
	public void setWorklistEntry(WorklistEntry entry) {
		String studyInstanceUID = entry.getStudyInstanceUID();
		Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
		Map<String, Object> aimCriteria = new HashMap<String, Object>();			
		dicomCriteria.put(Tag.StudyInstanceUID, studyInstanceUID);
		avtQuery = new AVTQuery(dicomCriteria, aimCriteria, ADQueryTarget.PATIENT, null, null);
		avtQuery.addAVTListener(this);
		Thread t = new Thread(avtQuery);
		t.start();		
	}
	
	SearchResult selectedDataSearchResult;
	@Override
	public void searchResultsAvailable(AVTSearchEvent e) {
		SearchResult selectedDataSearchResult = (SearchResult)e.getSource();
		logger.debug("Found patients: ");
		for(Patient patient : selectedDataSearchResult.getPatients()){
			logger.debug(patient.getPatientID() + " " + patient.getPatientName());
		}
		//Start TargetIteratorRunner
		TargetIteratorRunner targetIter = new TargetIteratorRunner(selectedDataSearchResult, getIterationTarget(), avtQuery, getApplicationTmpDir(), this);
		try {
			Thread t = new Thread(targetIter);
			t.start();
		} catch(Exception e1) {
			logger.error(e1, e1);
		}
	}

	@Override
	public void retriveResultsAvailable(AVTRetrieveEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void notifyException(String message) {
		// TODO Auto-generated method stub
		
	}

	Iterator<TargetElement> iter;
	@SuppressWarnings("unchecked")
	@Override
	public void fullIteratorAvailable(IteratorEvent e) {
		iter = (Iterator<TargetElement>)e.getSource();
		logger.debug("Full TargetIterator available at time " + System.currentTimeMillis());
	}

	List<TargetElement> targetElements = new ArrayList<TargetElement>();
	@Override
	public void targetElementAvailable(IteratorElementEvent e) {
		synchronized(targetElements){
			TargetElement element = (TargetElement) e.getSource();
			logger.debug("TargetElement available. ID: " + element.getId() + " at time " + System.currentTimeMillis());
			targetElements.add(element);
			targetElements.notify();
		}
	}
	
	public List<ObjectLocator> retrieveAndGetLocators(List<Uuid> listUUIDs){
		//Start data retrieval related to the element	
		ADRetrieveTarget retrieveTarget = ADRetrieveTarget.DICOM_AND_AIM;
		AVTRetrieve2 avtRetrieve = null;
		TargetElement element = null;
		try {
			synchronized(targetElements){
				element = targetElements.get(numberOfSentNotifications - 1);
				avtRetrieve = new AVTRetrieve2(element, retrieveTarget);
				avtRetrieve.addAVTListener(this);
			}
		} catch (IOException e1) {
			logger.error(e1, e1);
		}			
		Thread t = new Thread(avtRetrieve);
		t.start();
		//Wait for actual data being retrieved before sending file pointers
		String retrievedElementID = element.getId();
		synchronized(retrievedTargetElements){
			while(!retrievedTargetElements.contains(retrievedElementID)){
				try {
					retrievedTargetElements.wait();
				} catch (InterruptedException e) {
					logger.error(e, e);
				}
			}
		}		
		if(listUUIDs == null){
			return new ArrayList<ObjectLocator>();
		} else {
			ArrayList<ObjectLocator> listObjLocs = new ArrayList<ObjectLocator>();
			for(Uuid uuid : listUUIDs){
				String strUuid = uuid.getUuid();
				Collection<?> objs = (Collection<?>) wg23DataModelItems.get(strUuid);
				Iterator<?> iterObjs = objs.iterator();
				while(iterObjs.hasNext()){
					Object object = iterObjs.next();
					if(object instanceof ObjectLocator){
						ObjectLocator objLoc = (ObjectLocator)object;
						listObjLocs.add(objLoc);
						logger.debug("Item location: " + strUuid + " " + objLoc.getUri());
					}
				}
			}
			return listObjLocs;
		}		
	}
	
	List<String> retrievedTargetElements = new ArrayList<String>();
	@Override
	public void retriveCompleted(AVTRetrieve2Event e) {
		synchronized(retrievedTargetElements){
			String elementID = (String)e.getSource();
			retrievedTargetElements.add(elementID);
			retrievedTargetElements.notify();
			logger.debug("Data retrived for TargetElement: " + elementID + " at time: " + System.currentTimeMillis());
		}
	}
}
