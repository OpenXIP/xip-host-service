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

package edu.wustl.xipHost.caGrid;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.ivi.helper.DICOMDataServiceHelper;
import gov.nih.nci.ivi.helper.NCIADataServiceHelper;

/**
 * @author Jaroslaw Krych
 *
 */
public class GridRetrieve implements Runnable {	
	CQLQuery cqlQuery;
	GridLocation gridLoc;
	File importDir;
	
	public GridRetrieve(CQLQuery cql, GridLocation gridLocation, File importLocation) throws IOException {
		cqlQuery = cql;
		gridLoc = gridLocation;
		if(importLocation == null){
			throw new NullPointerException();
		}else if(importLocation.exists() == false){
			throw new IOException();
		}else{
			importDir = importLocation;
		}		
	}	
	
	List<File> files;
	public void run(){		
		try {
			files = retrieveDicomData(cqlQuery, gridLoc, importDir);
			notifyDicomAvailable();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}					
	}
	
	public List<File> getRetrievedFiles(){
		return files;
	}
	
	GridRetrieveListener listener;
    public void addGridRetrieveListener(GridRetrieveListener l) {        
        listener = l;          
    }
	void notifyDicomAvailable(){
		GridRetrieveEvent event = new GridRetrieveEvent(this);         		
        listener.importedFilesAvailable(event);
	}
	
	DICOMDataServiceHelper dicomHelper = new DICOMDataServiceHelper();					
	//NCIADataServiceHelper nciaHelper = new NCIADataServiceHelper();
	public List<File> retrieveDicomData(CQLQuery cqlQuery, GridLocation location, File importDir) throws IOException {						
		if(importDir == null){
			throw new NullPointerException();
		}		
		List<File> dicomFiles = new ArrayList<File>();		
		File inputDir = File.createTempFile("DICOM-XIPHOST", null, importDir);			
		importDir = inputDir;		
		inputDir.delete();
		if(importDir.exists() == false){
			importDir.mkdir();
		}
		try {
			System.err.println(ObjectSerializer.toString(cqlQuery, 
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));
		} catch (SerializationException e) {			
			e.printStackTrace();
		}		
		try{						
			if(location.getProtocolVersion().equalsIgnoreCase("DICOM")){
				dicomHelper.retrieveDICOMData(cqlQuery, location.getAddress(), inputDir.getCanonicalPath());				
			}else if(location.getProtocolVersion().equalsIgnoreCase("NBIA-4.2")){
				//nciaHelper does not retrieve data as of 10/08/2009
				//nciaHelper.retrieveDICOMData(cqlQuery, location.getAddress(), importDir.getCanonicalPath());
			}else{
				
			}			
		}catch(Exception e){									
			return dicomFiles;
		}
		/* Record what files were retrieved */
		File retFilesDir = importDir;
		String [] retrievedFiles = retFilesDir.list();
		for(int i = 0; i < retrievedFiles.length; i++){								
			dicomFiles.add(new File(importDir + File.separator + retrievedFiles[i]));
		}
		return dicomFiles;
	}	
}
