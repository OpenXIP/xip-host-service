/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.wg23;

import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.jws.WebService;
import org.apache.log4j.Logger;
import org.nema.dicom.wg23.ArrayOfObjectLocator;
import org.nema.dicom.wg23.ArrayOfQueryResult;
import org.nema.dicom.wg23.ArrayOfString;
import org.nema.dicom.wg23.ArrayOfUUID;
import org.nema.dicom.wg23.AvailableData;
import org.nema.dicom.wg23.Host;
import org.nema.dicom.wg23.ModelSetDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.Rectangle;
import org.nema.dicom.wg23.State;
import org.nema.dicom.wg23.Status;
import org.nema.dicom.wg23.Uid;
import org.nema.dicom.wg23.Uuid;
import edu.wustl.xipHost.application.Application;


/**
 * @author Jaroslaw Krych
 *
 */
@WebService(
		serviceName = "HostService",
        portName="HostPort",
        targetNamespace = "http://wg23.dicom.nema.org/",
        endpointInterface = "org.nema.dicom.wg23.Host")
public class HostImpl implements Host{	
	final static Logger logger = Logger.getLogger(HostImpl.class);
	Application app;
	List<ObjectLocator> objLocs;	
	
	public HostImpl(Application application){
		app = application;		
	}
	
	public HostImpl(){
		
	}
	
	public Uid generateUID() {
		Uid uid = new Uid();
		UUID id = UUID.randomUUID();
		uid.setUid(id.toString());
		return uid;
	}

	public ModelSetDescriptor getAsModels(ArrayOfUUID uuids, Uid classUID, Uid transferSyntaxUID) {			
		//TODO
		return  null;		
	}

	public Rectangle getAvailableScreen(Rectangle appPreferredScreen) {		
		//TODO
		return null;
	}

	public ArrayOfObjectLocator getDataAsFile(ArrayOfUUID uuids, boolean includeBulkData){ 			
		
		ArrayOfObjectLocator arrayObjLoc = new ArrayOfObjectLocator();
		List<Uuid> listUUIDs = uuids.getUuid();
		objLocs = app.retrieveAndGetLocators(listUUIDs);
		arrayObjLoc.getObjectLocator().addAll(objLocs);				
		return arrayObjLoc;
	}

	public ArrayOfObjectLocator getDataAsSpecificTypeFile(ArrayOfUUID objectUUIDs, String mimeType, Uid transferSyntaxUID, boolean includeBulkData) {
		//TODO
		return null;
	}	

	public String getOutputDir() {
		String appOutDir = null;		
		try {
			appOutDir = app.getApplicationOutputDir().toURI().toURL().toExternalForm();
		} catch (MalformedURLException e) {

		}		
		return appOutDir;
	}

	public String getTmpDir() {
		String appTmpDir = null;;
		try {
			appTmpDir = app.getApplicationTmpDir().toURI().toURL().toExternalForm();
		} catch (MalformedURLException e) {
			
		}		
		return appTmpDir;				
	}

	public boolean notifyDataAvailable(AvailableData availableData, boolean lastData) {					
		//TODO
		return false;
	}


	ExecutorService stateExeService = Executors.newFixedThreadPool(1);
	public void notifyStateChanged(State newState) {		
		logger.debug("Requested state change to " + newState.toString() + " of \"" + app.getName() + "\"");
		try {
			changeState(newState);					
		} catch (StateChangeException e) {			
			//TODO what to do when state cannot be changed
			e.printStackTrace();
		}
	}

		
	/**
	 * Method is an implementation of WG23Listener
	 * It changes application state. Each state change results in new action.
	 * Allowable changes: 
	 * null -> IDLE, COMPLETED -> IDLE,
	 * IDLE -> INPROGRESS, SUSPENDED -> INPROGRESS,
	 * INPROGRESS -> COMPLETED,
	 * INPROGRESS -> CANCELED, SUSPENDED -> CANCELED,
	 * INPROGRESS -> SUSPENDED,
	 * IDLE -> EXIT 
	 * @throws StateChangeException 
	 * @throws StateChangeException 
	 * 
	 */
	public void changeState(State state) throws StateChangeException  {		
		State currState = app.getState();		
		if(state == null){throw new StateChangeException("Requested state: " + state + ", current app state: " + currState);}
        switch (State.valueOf(state.toString())) {
            case IDLE:  
            	if(currState == null || currState.equals(State.COMPLETED) 
            			|| currState.equals(State.CANCELED) || currState.equals(State.EXIT)){            		
            		app.setState(state);            		          		
            	}else{
            		throw new StateChangeException("Requested state: " + state.toString() + ", current app state: " + currState);					
            	}
            	break;
            case INPROGRESS: 
            	if(currState != null && (currState.equals(State.IDLE) || currState.equals(State.SUSPENDED))){            		
            		app.setState(state);            		
            	}else{
            		throw new StateChangeException("Requested state: " + state.toString() + ", current app state: " + currState);					
            	}
            	break;            	
            case COMPLETED:  
            	if(currState != null && currState.equals(State.INPROGRESS)){
            		app.setState(state);
            	}else{
            		throw new StateChangeException("Requested state: " + state.toString() + ", current app state: " + currState);					
            	}
            	break;   
            case CANCELED:  
            	if(currState != null && (currState.equals(State.INPROGRESS) || currState.equals(State.SUSPENDED))){
            		app.setState(state);
            	}else{
            		throw new StateChangeException("Requested state: " + state.toString() + ", current app state: " + currState);					
            	}
            	break;   
            	
            case SUSPENDED:  
            	if(currState != null && currState.equals(State.INPROGRESS)){
            		app.setState(state);
            	}else{
            		throw new StateChangeException("Requested state: " + state.toString() + ", current app state: " + currState);					
            	}
            	break;   
            case EXIT:  
            	if(currState.equals(State.IDLE)){
            		app.setState(state);
            	}else{
            		throw new StateChangeException("Requested state: " + state.toString() + ", current app state: " + currState);					
            	}
            	break;   
            default: 
            	throw new StateChangeException("Requested state: " + state.toString() + ", current app state: " + currState);            	
        }
	}
	

	public void notifyStatus(Status newStatus) {
		// TODO Auto-generated method stub
	}

	public ArrayOfQueryResult queryModel(ArrayOfUUID objUUIDs, ArrayOfString modelXpaths, boolean includeBulkDataPointers) {
		//TODO
		return null;
	}
}
