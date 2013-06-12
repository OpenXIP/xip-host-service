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

package edu.wustl.xipHost.hostControl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.Endpoint;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.nema.dicom.wg23.State;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.application.ApplicationManager;
import edu.wustl.xipHost.application.ApplicationManagerFactory;

public class HostConfigurator {
	final static Logger logger = Logger.getLogger(HostConfigurator.class);	 
	File hostTmpDir;
	File hostOutDir;
	File hostConfig;	
	ApplicationManager appMgr;
	File xipApplicationsConfig;	
	public static final String OS = System.getProperty("os.name");
	String userName;	 	
	
	public boolean runHostStartupSequence(){		
		logger.info("Launching XIPHost. Platform " + OS);
		hostConfig = new File("./config/xipConfig.xml");
		if(loadHostConfigParameters(hostConfig) == false){		
			System.exit(0);
		}	
		hostTmpDir = createSubTmpDir(getParentOfTmpDir());		
		hostOutDir = createSubOutDir(getParentOfOutDir());
				
    	appMgr = ApplicationManagerFactory.getInstance();    	
    	xipApplicationsConfig = new File("./config/applications.xml");	
    	try {
			appMgr.loadApplications(xipApplicationsConfig);
		} catch (JDOMException e) {
			System.exit(0);
		} catch (IOException e) {
			System.exit(0);
		}
		appMgr.setOutputDir(hostOutDir);		
		appMgr.setTmpDir(hostTmpDir);					
		return true;			
	}
			
	SAXBuilder builder = new SAXBuilder();
	Document document;
	Element root;
	String parentOfTmpDir;
	String parentOfOutDir;	
	Boolean displayStartup;
	/**	
	 * (non-Javadoc)
	 * @see edu.wustl.xipHost.hostControl.HostManager#loadHostConfigParameters()
	 */
	public boolean loadHostConfigParameters (File hostConfigFile) {								
		if(hostConfigFile == null){
			return false;
		}else if(!hostConfigFile.exists()){
			return false;
		}else{
			try{				
				document = builder.build(hostConfigFile);
				root = document.getRootElement();				
				//path for the parent of TMP directory
				if(root.getChild("tmpDir") == null){
					parentOfTmpDir = "";
				}else if(root.getChild("tmpDir").getValue().trim().isEmpty() ||
						new File(root.getChild("tmpDir").getValue()).exists() == false){
					parentOfTmpDir = "";
				}else{					
					parentOfTmpDir = root.getChild("tmpDir").getValue();																
				}
				if(root.getChild("outputDir") == null){
					parentOfOutDir = "";
				}else if(root.getChild("outputDir").getValue().trim().isEmpty() ||
						new File(root.getChild("outputDir").getValue()).exists() == false){
					parentOfOutDir = "";
				}else{
					//path for the parent of output directory. 
					//parentOfOutDir used to store data produced by the xip application													                                                                       		        							
					parentOfOutDir = root.getChild("outputDir").getValue();	    							    							        					
				}
				if(root.getChild("displayStartup") == null){
					displayStartup = new Boolean(true);
				}else{
					if(root.getChild("displayStartup").getValue().equalsIgnoreCase("true") ||
							root.getChild("displayStartup").getValue().trim().isEmpty() ||
							parentOfTmpDir.isEmpty() ||
							parentOfOutDir.isEmpty() || parentOfTmpDir.equalsIgnoreCase(parentOfOutDir)){
		        		if(parentOfTmpDir.equalsIgnoreCase(parentOfOutDir)){
		        			parentOfTmpDir = "";
		        			parentOfOutDir = "";
		        		}
						displayStartup = new Boolean(true);
		        	}else if (root.getChild("displayStartup").getValue().equalsIgnoreCase("false")){
		        		displayStartup = new Boolean(false);
		        	}else{
		        		displayStartup = new Boolean(true);
		        	}
				}	        	        	
		    } catch (JDOMException e) {				
				return false;
			} catch (IOException e) {
				return false;
			}			
		}
		return true;
    }
	
	File serverConfig = new File("./pixelmed-server-hsqldb/workstation1.properties");
	
	public String getParentOfOutDir(){
		return parentOfOutDir;
	}
	public void setParentOfOutDir(String newDir){
		parentOfOutDir = newDir;
	}
	
	public String getParentOfTmpDir(){
		return parentOfTmpDir;
	}
	
	public File getHostTmpDir(){
		return hostTmpDir;
	}
	
	public void setParentOfTmpDir(String newDir){
		parentOfTmpDir = newDir;
	}
	
	public Boolean getDisplayStartUp(){
		return displayStartup;
	}
	public void setDisplayStartUp(Boolean display){
		displayStartup = display;
	}
	
	public String getUser(){
		return userName;
	}
	
	/**
	 * method creates subdirectory under parent of tmp directory.
	 * Creating sub directory is meant to prevent situations when tmp dirs
	 * would be created directly on system main dir path e.g. C:\ 
	 * @return
	 */
	File createSubTmpDir(String parentOfTmpDir){
		if(parentOfTmpDir == null || parentOfTmpDir.trim().isEmpty()){return null;}
		try {			
			File tmpFile = Util.create("TmpXIP", ".tmp", new File(parentOfTmpDir));			
			tmpFile.deleteOnExit();			
			return tmpFile;					
		} catch (IOException e) {						
			return null;
		}
	}	
	
	/**
	 * method creates subdirectory under parent of output directory.
	 * Creating sub directory is meant to prevent situations when output dirs
	 * would be created directly on system main dir path e.g. C:\ 
	 * @return
	 */
	File createSubOutDir(String parentOfOutDir){
		if(parentOfOutDir == null || parentOfOutDir.trim().isEmpty()){return null;}		
		if(new File(parentOfOutDir).exists() == false){return null;}
		File outFile = new File(parentOfOutDir, "OutputXIP");
		if(!outFile.exists()){
			outFile.mkdir();
		}
		return outFile;
	}
	
	public void storeHostConfigParameters(File hostConfigFile) {      			
		root.getChild("tmpDir").setText(parentOfTmpDir);
		root.getChild("outputDir").setText(parentOfOutDir);
		root.getChild("displayStartup").setText(displayStartup.toString());	
		try {
			FileOutputStream outStream = new FileOutputStream(hostConfigFile);
			XMLOutputter outToXMLFile = new XMLOutputter();
	    	outToXMLFile.output(document, outStream);
	    	outStream.flush();
	    	outStream.close();                       
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
	}
		
	List<URL []> EPRs = new ArrayList<URL []>();
    int numOfDeplayedServices = 0;   
	Endpoint ep;
        
	static HostConfigurator hostConfigurator;
	public static void main (String [] args){		
		DOMConfigurator.configure("log4j.xml");
		hostConfigurator = new HostConfigurator();
		boolean startupOK = hostConfigurator.runHostStartupSequence();
		if(startupOK == false){			
			logger.fatal("XIPHost startup error. System exits.");
			System.exit(0);
		}		
	}
	
	public static HostConfigurator getHostConfigurator(){
		return hostConfigurator;
	}
	
	public void runHostShutdownSequence(){		
		//TODO
		//Host can terminate only if no applications are running (verify applications are not running)
		List<Application> applications = appMgr.getApplications();
		for(Application app : applications){			
			
		}		
		logger.info("Shutting down XIP Host.");
		//Store Host configuration parameters
		storeHostConfigParameters(hostConfig);
		//Store Applications		
		appMgr.storeApplications(applications, xipApplicationsConfig);
		
		//Clear content of TmpDir but do not delete TmpDir itself
		File dir = new File(getParentOfTmpDir());				
		boolean bln = Util.deleteHostTmpFiles(dir);
		if(bln == false){
			
		}			
		Util.delete(new File("./db"));
		logger.info("XIPHost exits. Thank you for using XIP Host.");
		
		
		ThreadGroup root = Thread.currentThread().getThreadGroup().getParent(); 
    	while (root.getParent() != null) {
    		root = root.getParent();
        }
        // Visit each thread group  
        System.exit(0);	
	}
	
}
