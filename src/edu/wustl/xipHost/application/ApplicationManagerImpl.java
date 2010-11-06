/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.nema.dicom.wg23.State;

import edu.wustl.xipHost.avt2ext.iterator.IterationTarget;

public class ApplicationManagerImpl implements ApplicationManager{		
	static List<Application> applications = new ArrayList<Application>();		
	Document document;
	SAXBuilder saxBuilder = new SAXBuilder();
	
	public boolean loadApplications (File xipAppFile) {					
		return true;			                                   		       
    }
		
	public boolean storeApplications(List<Application> applications, File xipAppFile){      	
		return true; 	                    
	}
				
	public boolean addApplication(Application newApplication){				
		try{
			applications.add(newApplication);
			return true;
		}catch(IllegalArgumentException e){
			return false;
		}		
	}
	
	
	public boolean modifyApplication(UUID applicationUUID, Application modifiedApplication){				
		return true;		
	}
	
	/**
	 * Application can be removed when application's State is either null (not set yet) or EXIT
	 * @param applicationUUID
	 * @return
	 */
	public boolean removeApplication(UUID applicationUUID){
		return false;
	}		
		
	public Application getApplication(UUID uuid){
		Application app = null;
				
		return app;
	}		
	
	public Application getApplication(String applicationName){
		Application app = null;
		for(int i = 0; i < applications.size(); i++){
			if(applications.get(i).getName().equalsIgnoreCase(applicationName)){
				app = applications.get(i);
			}
		}
		return app;
	}
		
	public List<Application> getApplications(){
		return applications;
	}
	
	public int getNumberOfApplications(){
		return applications.size();
	}	
		
    
	public URL generateNewApplicationServiceURL(){
		return null;
	}	
	
	public URL generateNewHostServiceURL(){
		return null;
	}	
	
	File tmpDir;
	public void setTmpDir(File tmpDir){
		this.tmpDir = tmpDir;
	}
	public File getTmpDir(){
		return tmpDir;
	}
	
	File outDir;
	public void setOutputDir(File outDir){
		this.outDir = outDir;
	}
	public File getOutputDir(){
		return outDir;
	}	
}
