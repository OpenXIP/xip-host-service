/*
Copyright (c) 2013, Washington University in St.Louis
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package edu.wustl.xipHost.application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class ApplicationManagerImpl implements ApplicationManager {		
	final static Logger logger = Logger.getLogger(ApplicationManagerImpl.class);
	List<Application> applications = new ArrayList<Application>();		
	
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
		Application app = null;
		for(int i = 0; i < applications.size(); i++){
			if(applications.get(i).getId().equals(uuid)){
				app = applications.get(i);
			}
		}		
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
	
	Application defaultApplication;
	public Application getDefaultApplication(){
		return defaultApplication;
	}
	
	public void setDefaultApplication(Application application){
		defaultApplication = application;
	}
		
	public List<Application> getApplications(){
		return applications;
	}
	
	public int getNumberOfApplications(){
		return applications.size();
	}	
		
	int portNum = 8059;
	public URL generateNewApplicationServiceURL(){
		String str1 = "http://localhost:";		
		String str2 = "/ApplicationInterface";
		portNum++;			 			
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
	
	SAXBuilder saxBuilder = new SAXBuilder();
	Document documentHostConfig;
	Element root;
	String parentOfTmpDir;
	String parentOfOutDir;	
	public boolean loadHostConfigParameters (InputStream inputStream) {								
		if(inputStream == null){
			return false;
		}else{
			try{				
				documentHostConfig = saxBuilder.build(inputStream);
				root = documentHostConfig.getRootElement();				
				//path for the parent of TMP directory
				if(root.getChild("tmpDir") == null){
					parentOfTmpDir = "";
				}else if(root.getChild("tmpDir").getValue().trim().isEmpty() ||
						new File(root.getChild("tmpDir").getValue()).exists() == false){
					parentOfTmpDir = "";
					tmpDir = new File(parentOfTmpDir);
				}else{					
					parentOfTmpDir = root.getChild("tmpDir").getValue();
					tmpDir = new File(parentOfTmpDir);
				}
				if(root.getChild("outputDir") == null){
					parentOfOutDir = "";
					outDir = new File(parentOfOutDir);
				}else if(root.getChild("outputDir").getValue().trim().isEmpty() ||
						new File(root.getChild("outputDir").getValue()).exists() == false){
					parentOfOutDir = "";
					outDir = new File(parentOfOutDir);
				}else{
					//path for the parent of output directory. 
					//parentOfOutDir used to store data produced by the xip application													                                                                       		        							
					parentOfOutDir = root.getChild("outputDir").getValue();	
					outDir = new File(parentOfOutDir);
				}       	        	
		    } catch (JDOMException e) {				
				logger.error(e, e);
		    	return false;
			} catch (IOException e) {
				logger.error(e, e);
				return false;
			}			
		}
		logger.debug("Tmp dir: " + tmpDir.getAbsolutePath());
		logger.debug("Out dir: " + outDir.getAbsolutePath());
		return true;
    }
}
