/**
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.hsqldb.Server;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import com.pixelmed.database.DatabaseInformationModel;
import com.pixelmed.database.PatientStudySeriesConcatenationInstanceModel;
import com.pixelmed.database.StudySeriesInstanceModel;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SetOfDicomFiles;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.VerificationSOPClassSCU;
import com.pixelmed.query.QueryInformationModel;
import com.pixelmed.query.QueryResponseGenerator;
import com.pixelmed.query.QueryResponseGeneratorFactory;
import com.pixelmed.query.QueryTreeModel;
import com.pixelmed.query.QueryTreeRecord;
import com.pixelmed.query.RetrieveResponseGenerator;
import com.pixelmed.query.RetrieveResponseGeneratorFactory;
import com.pixelmed.query.StudyRootQueryInformationModel;
import com.pixelmed.server.DicomAndWebStorageServer;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;

public class DicomManagerImpl implements DicomManager{
	Document documentPacs;
	Element rootPacs;
	SAXBuilder builder = new SAXBuilder();
	List<PacsLocation> pacsLocations = new ArrayList<PacsLocation>();
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.globalSearch.DicomManager#loadPacsLocations(java.io.File)
	 */
	public boolean loadPacsLocations(File file) throws IOException, JDOMException {				
		documentPacs = builder.build(file);
		rootPacs = documentPacs.getRootElement();										
		List children = rootPacs.getChildren("pacs_location");				
		for (int i = 0; i < children.size(); i++){
			String address = (((Element)children.get(i)).getChildText("hostAddress"));
			int port = Integer.valueOf((((Element)children.get(i)).getChildText("hostPort")));
			String aeTitle = (((Element)children.get(i)).getChildText("hostAETitle"));
			String shortName = (((Element)children.get(i)).getChildText("hostShortName"));
			/*System.out.println(address);
			System.out.println(port);
			System.out.println(aeTitle);*/						
			try {
				PacsLocation loc = new PacsLocation(address, port, aeTitle, shortName);
				pacsLocations.add(loc);
			} catch (IllegalArgumentException e) {
				//Prints invalid location and proceeds to load next one
				System.out.println("Unable to load: " + address + " " + port + " " + aeTitle + " " + shortName + " - invalid location.");				
			}																									
		}
		return true;							
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.globalSearch.DicomManager#storePacsLocations(java.util.List, java.io.File)
	 */
	public boolean storePacsLocations(List<PacsLocation> locations, File file) throws FileNotFoundException {					
		Element rootSave = new Element("locations");
		Document document = new Document();
		document.setRootElement(rootSave);		
		if(locations == null){return false;}
		for(int i = 0; i < locations.size(); i++){						
			Element pacsElem = new Element("pacs_location");
			Element addressElem = new Element("hostAddress");
			Element portElem = new Element("hostPort");
			Element aeTitleElem = new Element("hostAETitle");					
			Element shortNameElem = new Element("hostShortName");
			pacsElem.addContent(addressElem);
			pacsElem.addContent(portElem);
			pacsElem.addContent(aeTitleElem);
			pacsElem.addContent(shortNameElem);
			rootSave.addContent(pacsElem);
			addressElem.addContent(locations.get(i).getAddress());
			portElem.addContent(String.valueOf(locations.get(i).getPort()));
			aeTitleElem.addContent(locations.get(i).getAETitle());
			shortNameElem.addContent(locations.get(i).getShortName());
		}
		try {
			FileOutputStream outStream = new FileOutputStream(file);
			XMLOutputter outToXMLFile = new XMLOutputter();
			outToXMLFile.setFormat(Format.getPrettyFormat());
	    	outToXMLFile.output(document, outStream);
	    	outStream.flush();
	    	outStream.close();                       
		} catch (IOException e) {
			return false;
		} 
		return true;
	}
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.globalSearch.DicomManager#addPacsLocation(edu.wustl.xipHost.globalSearch.PacsLocation)
	 */
	public boolean addPacsLocation(PacsLocation pacsLocation){		
		try {
			if(!pacsLocations.contains(pacsLocation)){
				return pacsLocations.add(pacsLocation);
			} else {
				return false;
			}
		} catch (IllegalArgumentException e){
			return false;
		}								
	}	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.globalSearch.DicomManager#modifyPacsLocation(edu.wustl.xipHost.globalSearch.PacsLocation, edu.wustl.xipHost.globalSearch.PacsLocation)
	 */
	public boolean modifyPacsLocation(PacsLocation oldPacsLocation, PacsLocation newPacsLocation) {
		//validate method is used to check if parameters are valid, are notmissing, 
		//do not contain empty strings or do not start from white spaces		
		try {
			int i = pacsLocations.indexOf(oldPacsLocation);
			if (i != -1){
				pacsLocations.set(i, newPacsLocation);
				return true;
			} else{
				return false;
			}
		} catch (IllegalArgumentException e){
			return false;
		}
	}	
	
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.globalSearch.DicomManager#removePacsLocation(edu.wustl.xipHost.globalSearch.PacsLocation)
	 */
	public boolean removePacsLocation(PacsLocation pacsLocation){
		//System.out.println(pacsLocations.indexOf(pacsLocation));
		try {
			return pacsLocations.remove(pacsLocation);
		} catch (IllegalArgumentException e){
			return false;
		}		
	}

	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.globalSearch.DicomManager#getPacsLocations()
	 */
	public List<PacsLocation> getPacsLocations(){
		return pacsLocations;
	}	

	QueryTreeModel mTree = null;
	QueryInformationModel mModel = null;
	String calledAETitle;
	/* (non-Javadoc)
	 * @see edu.wustl.xipHost.globalSearch.DicomManager#query(com.pixelmed.dicom.AttributeList, edu.wustl.xipHost.globalSearch.PacsLocation)
	 */
	public SearchResult query(AttributeList criteria, PacsLocation location) {										 
	    if(criteria == null || location == null){
	    	return null;
	    }		
	    //Connecting to the server	    
		String hostName = location.getAddress();
		int port = location.getPort();
		calledAETitle = location.getAETitle();
		//ensure callingAETitle is not empty (not all stations allow empty value). 
		// TODO get AETitle from config
		String callingAETitle = "defaultXIPhost";
		mModel = new StudyRootQueryInformationModel(hostName, port, calledAETitle, callingAETitle, 0);		
		try {
		    new VerificationSOPClassSCU(hostName, port, calledAETitle, callingAETitle, false, 0);
		}catch (Exception e) {
		    return null;
		}
		SearchResult returnResult = null;
		try {			
			//TODO
			//mTree hangs when supplied callingAETitle is an empty string
			mTree = mModel.performHierarchicalQuery(criteria);			
			result = new SearchResult(location.hostShortName);
			Object root = mTree.getRoot();			
			returnResult = (SearchResult) resolveToSearchResult(root);
			
		
			/*int size = returnResult.getStudies().size();
	        for(int i = 0; i < size; i++){
	        	Study study = returnResult.getStudies().get(i);
	        	System.out.println(study.toString());	        	
	        	int size2 = study.getSeries().size();
	        	System.out.println("Number of studies :" + size2);
	        	for (int j = 0; j < size2; j++){
	        		Series series = study.getSeries().get(j);
	        		System.out.println(series.toString());
	        		int size3 = series.getImages().size();
	        		for (int jj = 0; jj < size3; jj++){
		        		Image image = series.getImages().get(jj);
		        		System.out.println(image.toString());
		        	}
	        	}
	        }*/
		} catch (IOException e) {
			return null;
		} catch (DicomException e) {
			return null;
		} catch (DicomNetworkException e) {
			return null;
		} 
		return returnResult;
	}	

	Patient patient = null;
	Study study = null;
	Series series = null;	
	SearchResult result;
	SearchResult resolveToSearchResult(Object node) {										
		int numChildren = mTree.getChildCount(node);		
		for(int i = 0; i < numChildren; i++){
			Object child = mTree.getChild(node, i);
			//find if child is Study, Series or Image
			//Case approach
			String level = getRetrieveLevel(child);					
			if(level.equalsIgnoreCase("Study")){
				String patientName = attValues.get("(0x0010,0x0010)");
				if(patientName == null){patientName = "";}
				String patientID = attValues.get("(0x0010,0x0020)");
				if(patientID == null){patientID = "";}
				String patientBirthDate = attValues.get("(0x0010,0x0030)");
				if(patientBirthDate == null){patientBirthDate = "";}
				patient = new Patient(patientName, patientID, patientBirthDate); 
				result.addPatient(patient);
				String studyDate = attValues.get("(0x0008,0x0020)");
				if(studyDate == null){studyDate = "";}
				String studyID = attValues.get("(0x0020,0x0010)");
				if(studyID == null){studyID = "";}
				String studyDesc = attValues.get("(0x0008,0x1030)");
				if(studyDesc == null){studyDesc = "";}				
				String studyInstanceUID = attValues.get("(0x0020,0x000D)");				
				if(studyInstanceUID == null){studyInstanceUID = "";}				
				study = new Study(studyDate, studyID, studyDesc, studyInstanceUID);
				patient.addStudy(study);
				resolveToSearchResult(child);
			}else if(level.equalsIgnoreCase("Series")){
				String seriesNumber = attValues.get("(0x0020,0x0011)");
				if(seriesNumber == null){seriesNumber = "";}
				String modality = attValues.get("(0x0008,0x0060)");
				if(modality == null){modality = "";}
				String seriesDesc = attValues.get("(0x0008,0x103E)");
				if(seriesDesc == null){seriesDesc = "";}
				String seriesInstanceUID = attValues.get("(0x0020,0x000E)");
				if(seriesInstanceUID == null){seriesInstanceUID = "";}
				series = new Series(seriesNumber, modality, seriesDesc, seriesInstanceUID);				
				study.addSeries(series);
				resolveToSearchResult(child);
			}else if(level.equalsIgnoreCase("Image")){
				String imageNumber = attValues.get("(0x0020,0x0013)");
				if(imageNumber == null){imageNumber = "";}								
				Item image = new ImageItem(imageNumber);				
				series.addItem(image);
			}else{
				return null;
			}
		}
		return result;
	}			
	
	Map<String, String> attValues;
	/**
	 * 
	 * @param node
	 * @return
	 */
	String getRetrieveLevel(Object node){
		attValues = new HashMap<String, String>();
		if (node instanceof QueryTreeRecord) {
			QueryTreeRecord r = (QueryTreeRecord)node;
			AttributeList keys = r.getUniqueKeys();									
			String seriesInstanceUID = Attribute.getSingleStringValueOrNull(keys, TagFromName.SeriesInstanceUID);
			String studyInstanceUID = Attribute.getSingleStringValueOrNull(keys, TagFromName.StudyInstanceUID);									
	        attValues.put("(0x0020,0x000D)", studyInstanceUID);
	        attValues.put("(0x0020,0x000E)", seriesInstanceUID);
			AttributeList identifier = r.getAllAttributesReturnedInIdentifier();
	        DicomDictionary dictionary = AttributeList.getDictionary();
	        Iterator iter = dictionary.getTagIterator();        
	        String strAtt = null;
	        String attValue = null;
	        while(iter.hasNext()){
				AttributeTag attTag  = (AttributeTag)iter.next();
				strAtt = attTag.toString();									
				attValue = Attribute.getSingleStringValueOrEmptyString(identifier, attTag);			
				//put only those attributes non empty and non null		
				if(!attValue.isEmpty()){
					//System.out.println(strAtt + " " + attValue);
					attValues.put(strAtt, attValue);
				}										
	        }	        
		}
		String level = attValues.get("(0x0008,0x0052)");
		return level;				
	}
	
	List<URI> retrievedFiles;
	/**
	 * 1. Method performed hierarchical move from remote location to calling location first.
	 * 2. To get retrieved files' URIs method performs query but on the local server (calling)
	 */
	public List<URI> retrieve(AttributeList criteria, PacsLocation called, PacsLocation calling) {
		retrievedFiles = new ArrayList<URI>();
		System.out.println("Retrieving ...");		    	    		    	    		        
        try {
        	String hostName = called.getAddress();
        	int port = called.getPort();
        	String calledAETitle = called.getAETitle(); 
        	String callingAETitle = calling.getAETitle();
        	StudyRootQueryInformationModel mModel = new StudyRootQueryInformationModel(hostName, port, calledAETitle, callingAETitle, 0);
        	mModel.performHierarchicalMoveTo(criteria, calling.getAETitle());			
		} catch (IOException e1) {
			return null;
		} catch (DicomException e1) {
			return null;
		} catch (DicomNetworkException e1) {						
			return null;
		}        	        	    	    			
		
		try {						    		
			//System.out.println("Local server is: " + getDBFileName());
			StudySeriesInstanceModel mDatabase = new StudySeriesInstanceModel(getDBFileName());			
    		//RetrieveResposeGeneratorFactory provides access to files URLs stored in hsqldb    		    	
    		RetrieveResponseGeneratorFactory mRetrieveResponseGeneratorFactory = mDatabase.getRetrieveResponseGeneratorFactory(0);
    		QueryResponseGeneratorFactory mQueryResponseGeneratorFactory = mDatabase.getQueryResponseGeneratorFactory(0);			
    		RetrieveResponseGenerator mRetrieveResponseGenerator = mRetrieveResponseGeneratorFactory.newInstance();
			QueryResponseGenerator mQueryResponseGenerator = mQueryResponseGeneratorFactory.newInstance();						
			mQueryResponseGenerator.performQuery("1.2.840.10008.5.1.4.1.2.2.1", criteria, true);	// Study Root						
			AttributeList localResults = mQueryResponseGenerator.next();			
			while(localResults != null) {							 					
				mRetrieveResponseGenerator.performRetrieve("1.2.840.10008.5.1.4.1.2.2.3", localResults, true);	// Study Root		
				SetOfDicomFiles dicomFiles = mRetrieveResponseGenerator.getDicomFiles();
				Iterator it = dicomFiles.iterator();			  
				while (it.hasNext() ) {
					SetOfDicomFiles.DicomFile x  = (SetOfDicomFiles.DicomFile)it.next();
					//System.out.println("Dicom file: " + x.getFileName());			    
					retrievedFiles.add(new File(x.getFileName()).toURI());
				}		
				localResults = mQueryResponseGenerator.next();
			}			
    	} catch (Exception e) {
    		return null;
    	}      	    	    	
    	return retrievedFiles;
	}
	
	BasicDicomParser2 parser = new BasicDicomParser2();
	public boolean submit(File[] dicomFiles, PacsLocation location) {		
		for(int i = 0; i < dicomFiles.length; i++){
			AttributeList attList = parser.parse(dicomFiles[i]);					
			try {
				dbModel.insertObject(attList, dicomFiles[i].getCanonicalPath());
			} catch (DicomException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}		
		return true;
	}

	File xmlPacsLocFile = new File("./config/pacs_locations.xml");
	String dbFileName;
	public boolean runDicomStartupSequence() {
		startHSQLDB();		
		startPixelmedServer();
		dbFileName = prop.getProperty("Application.DatabaseFileName");		
		setDBModel(dbFileName);
		
		if(xmlPacsLocFile == null ){return false;}
		try {
			loadPacsLocations(xmlPacsLocFile);				
		} catch (IOException e) {
			// TODO Auto-generated catch block				
			System.out.println("DICOM module startup sequence error. " + 
			"System could not find: pacs_locations.xml");
			return false;
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			System.out.println("GlobalSearch startup sequence error. " + 
			"Error when processing pacs_locations.xml");
			return false;
		}
		return true;
	}
	
	public boolean runShutDownSequence(){
		//closeDicomServer();		
		return true;
	}
	
	
	Server hsqldbServer;
	public void startHSQLDB() {
		hsqldbServer = new Server();
		hsqldbServer.putPropertiesFromFile("./pixelmed-server-hsqldb/server");		
		hsqldbServer.start();
	}
	
	DicomAndWebStorageServer server;
	Properties prop = new Properties();
	public boolean startPixelmedServer(){		
		try {
			prop.load(new FileInputStream("./pixelmed-server-hsqldb/workstation1.properties"));
			server = new DicomAndWebStorageServer(prop);			
		} catch (FileNotFoundException e) {			
			return false;
		} catch (IOException e) {
			return false;
		} catch (DicomException e) {
			return false;
		} catch (DicomNetworkException e) {
			return false;
		}
		return true;
	}
	
	DatabaseInformationModel dbModel = null;
	public DatabaseInformationModel getDBModel(){
		return dbModel;
	}
	
	public void setDBModel(String dbModel){
		try {			
			this.dbModel = new PatientStudySeriesConcatenationInstanceModel(dbModel);			
		} catch (DicomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getDBFileName(){
		return dbFileName;
	}
	
	/*public void closeDicomServer(){						
		dbModel.close();		
	}*/
	public void closeHSQLDB(){
		hsqldbServer.shutdown();
	}
	
	public static void main(String[] args){
		DicomManagerImpl dicomMgr = new DicomManagerImpl();
		dicomMgr.runDicomStartupSequence();
		dicomMgr.runShutDownSequence();
		dicomMgr.startPixelmedServer();
	}
}