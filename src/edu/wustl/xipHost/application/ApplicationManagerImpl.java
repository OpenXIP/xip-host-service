/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

public class ApplicationManagerImpl implements ApplicationManager {		
	final static Logger logger = Logger.getLogger(ApplicationManagerImpl.class);
	List<Application> applications = new ArrayList<Application>();		
	Document document;
	SAXBuilder saxBuilder = new SAXBuilder();
	
	public boolean loadApplications (File xipAppFile) {					
		//TODO
		return true;			                                   		       
    }
		
	public boolean storeApplications(List<Application> applications, File xipAppFile){      	
		//TODO
		return true; 	                    
	}
				
	public boolean addApplication(Application newApplication){				
		try{
			applications.add(newApplication);
			logger.debug("Added application: " + newApplication.getId() + " " + newApplication.getName());
			return true;
		}catch(IllegalArgumentException e){
			logger.error(e, e);
			return false;
		}		
	}
	
	public boolean modifyApplication(UUID applicationUUID, Application modifiedApplication){				
		//TODO
		return true;		
	}
	
	public boolean removeApplication(UUID applicationUUID){
		//TODO
		return false;
	}		
		
	public Application getApplication(UUID uuid){
		//TODO
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
		//"http://localhost:8060/ApplicationInterface?wsdl"
		int portNum = 8060;
		String str1 = "http://localhost:";
		//String str2 = "/ApplicationInterface?wsdl";		
		String str2 = "/ApplicationInterface";
		URL url = null;
		try {
			url = new URL(str1 + portNum + str2);
		} catch (MalformedURLException e) {
			return url;
		}
		return url;
	}	
	
	public URL generateNewHostServiceURL(){
		//TODO
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

	@Override
	public boolean hasApplication(UUID uuid) {
		for(Application app : applications){
			if(app.getId().toString().equalsIgnoreCase(uuid.toString())){
				return true;
			}
		}
		return false;
	}	
}
