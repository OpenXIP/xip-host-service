/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.ws.Endpoint;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.nema.dicom.wg23.State;
import org.xmldb.api.base.XMLDBException;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.application.ApplicationManager;
import edu.wustl.xipHost.application.ApplicationManagerFactory;
import edu.wustl.xipHost.avt2ext.iterator.IterationTarget;
import edu.wustl.xipHost.gui.ConfigPanel;
import edu.wustl.xipHost.gui.ExceptionDialog;
import edu.wustl.xipHost.gui.HostMainWindow;

public class HostConfigurator {
	final static Logger logger = Logger.getLogger(HostConfigurator.class);	 
	File hostTmpDir;
	File hostOutDir;
	File hostConfig;
	HostMainWindow mainWindow;
	ConfigPanel configPanel = new ConfigPanel(new JFrame()); 		//ConfigPanel is used to specify tmp and output dirs	
	ApplicationManager appMgr;
	File xipApplicationsConfig;	
	public static final String OS = System.getProperty("os.name");
	String userName;	 	
	
	public boolean runHostStartupSequence(){		
		logger.info("Launching XIPHost. Platform " + OS);
		hostConfig = new File("./config/xipConfig.xml");
		if(loadHostConfigParameters(hostConfig) == false){		
			new ExceptionDialog("Unable to load Host configuration parameters.", 
					"Ensure host config file and Pixelmed/HSQLQB config file are valid.",
					"Host Startup Dialog");
			System.exit(0);
		}
		//if config contains displayConfigDialog true -> displayConfigDialog
		if(getDisplayStartUp()){
			displayConfigDialog();			
		}		
		hostTmpDir = createSubTmpDir(getParentOfTmpDir());		
		hostOutDir = createSubOutDir(getParentOfOutDir());
				
    	appMgr = ApplicationManagerFactory.getInstance();    	
    	xipApplicationsConfig = new File("./config/applications.xml");	
    	try {
			appMgr.loadApplications(xipApplicationsConfig);
			//Load test applications, RECIST is currently supported on Windows only			
			boolean loadTestApps = false;
			if(loadTestApps){
				loadTestApplications();
			}
		} catch (JDOMException e) {
			new ExceptionDialog("Unable to load applications.", 
					"Ensure applications xml config file exists and is valid.",
					"Host Startup Dialog");
			System.exit(0);
		} catch (IOException e) {
			new ExceptionDialog("Unable to load applications.", 
					"Ensure applications xml config file exists and is valid.",
					"Host Startup Dialog");
			System.exit(0);
		}
		//hostOutDir and hostTmpDir are hold in static variables in ApplicationManager
		appMgr.setOutputDir(hostOutDir);		
		appMgr.setTmpDir(hostTmpDir);		
		
		//XindiceManager is used to register XMLDB database used to store and manage
		//XML Native Models
		XindiceManager xm = XindiceManagerFactory.getInstance();
		try {
			xm.startup();
		} catch (XMLDBException e) {
			//TODO Auto-generated catch block
			//Q: what to do if Xindice is not launched
			//1. Go with no native model support or 
			//2. prompt user and exit
			e.printStackTrace();
		}				
		mainWindow = new HostMainWindow();											
		mainWindow.setUserName(userName);			
		mainWindow.display();					
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
	
	
	void displayConfigDialog(){							
		configPanel.setParentOfTmpDir(getParentOfTmpDir());
		configPanel.setParentOfOutDir(getParentOfOutDir());
		configPanel.setDisplayStartup(getDisplayStartUp());
		configPanel.display();    		
		setParentOfTmpDir(configPanel.getParentOfTmpDir());
		setParentOfOutDir(configPanel.getParentOfOutDir());
		setDisplayStartUp(configPanel.getDisplayStartup());				
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
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());			
			//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		        				
		//Turn off commons loggin for better performance
		System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");		
		DOMConfigurator.configure("log4j.xml");
		hostConfigurator = new HostConfigurator();
		boolean startupOK = hostConfigurator.runHostStartupSequence();
		if(startupOK == false){			
			logger.fatal("XIPHost startup error. System exits.");
			System.exit(0);
		}		
		/*final long MEGABYTE = 1024L * 1024L;
		System.out.println("Total heap size: " + (Runtime.getRuntime().maxMemory())/MEGABYTE);
		System.out.println("Used heap size: " + (Runtime.getRuntime().totalMemory())/MEGABYTE);
		System.out.println("Free heap size: " + (Runtime.getRuntime().freeMemory())/MEGABYTE);*/
	}
	
	public static HostConfigurator getHostConfigurator(){
		return hostConfigurator;
	}
	
	public HostMainWindow getMainWindow(){
		return mainWindow;
	}
	
	public void runHostShutdownSequence(){		
		//TODO
		//Host can terminate only if no applications are running (verify applications are not running)
		List<Application> applications = appMgr.getApplications();
		for(Application app : applications){			
			State state = app.getState();			
			if(state != null && state.equals(State.EXIT) == false ){
				if(app.shutDown() == false){
					new ExceptionDialog(app.getName() + " cannot be terminated by host.", 
							"Application current state: " + app.getState().toString() + ".",
							"Host Shutdown Dialog");
					return;
				}
			}
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
			new ExceptionDialog("Not all content of Host TMP directory " + hostTmpDir + " was cleared.", 
					"Only subdirs starting with 'TmpXIP' and ending with '.tmp' and their content is deleted.",
					"Host Shutdown Dialog");
		}			
		XindiceManagerFactory.getInstance().shutdown();
		//Clear Xindice directory. Ensures all documents and collections are cleared even when application
		//does not terminate properly
		Util.delete(new File("./db"));
		logger.info("XIPHost exits. Thank you for using XIP Host.");
		
		
		ThreadGroup root = Thread.currentThread().getThreadGroup().getParent(); 
    	while (root.getParent() != null) {
    		root = root.getParent();
        }
        // Visit each thread group  
        System.exit(0);	
        visit(root, 0);
	}
	
	void loadTestApplications(){
		if(OS.contains("Windows")){								
			if(appMgr.getApplication("TestApp_WG23FileAccess") == null){
				try {
					String pathExe = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/XIPApplication_WashU_3.bat").getCanonicalPath();
					File exeFile = new File(pathExe);
					String pathIcon = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/ApplicationIcon-16x16.png").getCanonicalPath();
					File iconFile = new File(pathIcon);				
					appMgr.addApplication(new Application("TestApp_WG23FileAccess", exeFile, "", "", iconFile, "analytical", true, "files", 1, IterationTarget.SERIES));
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(appMgr.getApplication("TestApp_WG23NativeModel") == null){
				try{	
					String pathExe = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/XIPAppNativeModel.bat").getCanonicalPath();
					File exeFile = new File(pathExe);
					String pathIcon = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/ApplicationIcon-16x16.png").getCanonicalPath();
					File iconFile = new File(pathIcon);
					appMgr.addApplication(new Application("TestApp_WG23NativeModel", exeFile, "", "", iconFile, "analytical", true, "native", 1, IterationTarget.SERIES));
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(appMgr.getApplication("RECIST_Adjudicator") == null){				
				try {
					String pathExe = new File("../XIPApp/bin/RECISTFollowUpAdjudicator.bat").getCanonicalPath();
					File exeFile = new File(pathExe);
					String pathIcon = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/ApplicationIcon-16x16.png").getCanonicalPath();
					File iconFile = new File(pathIcon);
					appMgr.addApplication(new Application("RECIST_Adjudicator", exeFile, "", "", iconFile, "rendering", true, "files", 1, IterationTarget.SERIES));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}				
		}else{
			if(appMgr.getApplication("TestApp_WG23FileAccess") == null){
				try {
					String pathExe = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/XIPApplication_WashU_3.sh").getCanonicalPath();
					File exeFile = new File(pathExe);
					String pathIcon = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/ApplicationIcon-16x16.png").getCanonicalPath();
					File iconFile = new File(pathIcon);				
					appMgr.addApplication(new Application("TestApp_WG23FileAccess", exeFile, "", "", iconFile, "analytical", true, "files", 1, IterationTarget.SERIES));
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(appMgr.getApplication("TestApp_WG23NativeModel") == null){
				try{	
					String pathExe = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/XIPAppNativeModel.sh").getCanonicalPath();
					File exeFile = new File(pathExe);
					String pathIcon = new File("./../XIPApp/bin/edu/wustl/xipApplication/samples/ApplicationIcon-16x16.png").getCanonicalPath();
					File iconFile = new File(pathIcon);
					appMgr.addApplication(new Application("TestApp_WG23NativeModel", exeFile, "", "", iconFile, "analytical", true, "native", 1, IterationTarget.SERIES));
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
		
	public static int adjustForResolution(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
		int height = (int)screenSize.getHeight();
		int preferredHeight = 600;
		if (height < 768 && height >= 600 ){
			preferredHeight = 350;
		}else if(height < 1024 && height >= 768 ){
			preferredHeight = 470;
		}else if (height >= 1024 && height < 1200){
			preferredHeight = 600;
		}else if(height > 1200 && height <= 1440){
			preferredHeight = 800;
		}
		return preferredHeight;		
	}
	
	public static void visit(ThreadGroup group, int level) {
        // Get threads in `group'
        int numThreads = group.activeCount();
        Thread[] threads = new Thread[numThreads * 2];
        numThreads = group.enumerate(threads, false);
 
        // Enumerate each thread in `group'
        for (int i = 0; i < numThreads; i++) {
            // Get thread
            Thread thread = threads[i];
            System.out.println(thread.getName());
        }
 
        // Get thread subgroups of `group'
        int numGroups = group.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[numGroups * 2];
        numGroups = group.enumerate(groups, false);
 
        // Recursively visit each subgroup
        for (int i = 0; i < numGroups; i++) {
            visit(groups[i], level + 1);
        }
    }
	
}
