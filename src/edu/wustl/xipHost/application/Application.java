/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.dcm4che2.data.Tag;
import edu.wustl.xipHost.avt2ext.ADQueryTarget;
import edu.wustl.xipHost.avt2ext.AVTListener;
import edu.wustl.xipHost.avt2ext.AVTQuery;
import edu.wustl.xipHost.avt2ext.AVTRetrieveEvent;
import edu.wustl.xipHost.avt2ext.AVTSearchEvent;
import edu.wustl.xipHost.avt2ext.iterator.IterationTarget;
import edu.wustl.xipHost.avt2ext.iterator.IteratorElementEvent;
import edu.wustl.xipHost.avt2ext.iterator.IteratorEvent;
import edu.wustl.xipHost.avt2ext.iterator.TargetElement;
import edu.wustl.xipHost.avt2ext.iterator.TargetIteratorListener;
import edu.wustl.xipHost.avt2ext.iterator.TargetIteratorRunner;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.hostControl.Util;
import edu.wustl.xipHost.worklist.WorklistEntry;

public class Application implements AVTListener, TargetIteratorListener {	
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
	
	public void launch(){
		
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
}
