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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Map;
import org.apache.log4j.Logger;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import javax.xml.namespace.QName;
import edu.wustl.xipHost.caGrid.GridLocation;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.QueryTarget;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.iterator.Criteria;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.ncia.client.NCIACoreServiceClient;

/**
 * @author Jaroslaw Krych
 *
 */
public class GridQuery implements Query {
	final static Logger logger = Logger.getLogger(GridQuery.class);
	CQLQuery cql;
	GridLocation gridLocation;
	Map<Integer, Object> dicomCriteria;
	Map<String, Object> aimCriteria;
	QueryTarget target;
	SearchResult previousSearchResult;
	Object queriedObject;
	GridManager gridMgr = GridManagerFactory.getInstance();
	GridUtil gridUtil;
	CQLQueryResultsIterator iter;
	DataServiceClient dicomClient = null;
	NCIACoreServiceClient nciaClient = null;
	
	public GridQuery(GridLocation location){
		this.gridLocation = location;
		try {
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("NCIAModelMap.properties");
			gridUtil = gridMgr.getGridUtil();
			gridUtil.loadNCIAModelMap(inputStream);
			if(location != null && location.getProtocolVersion().equalsIgnoreCase("DICOM")){
				dicomClient = new DataServiceClient(location.getAddress());			
			}else if(location != null && location.getProtocolVersion().equalsIgnoreCase("NBIA-4.2")){
				nciaClient = new NCIACoreServiceClient(location.getAddress());
			}
		} catch (FileNotFoundException e) {
			logger.error(e, e);
		} catch (IOException e) {
			logger.error(e, e);		}
	}
	
	@Override
	public void setQuery(Map<Integer, Object> dicomCriteria, Map<String, Object> aimCriteria, QueryTarget target, SearchResult previousSearchResult, Object queriedObject) {
		this.dicomCriteria = dicomCriteria;
		logger.debug("Original criteria: ");
		Criteria.logDicomCriteria(dicomCriteria);
		Criteria.logAimCriteria(aimCriteria);
		CQLTargetName cqlTargetName = null;
		if(target.equals(QueryTarget.PATIENT)){
			cqlTargetName = CQLTargetName.PATIENT;
		} else if (target.equals(QueryTarget.STUDY)){
			cqlTargetName = CQLTargetName.STUDY;
		} else if (target.equals(QueryTarget.SERIES)){
			cqlTargetName = CQLTargetName.SERIES;
		}
		cql = gridUtil.convertToCQLStatement(dicomCriteria, cqlTargetName);
		this.aimCriteria = aimCriteria; 
		this.target = target; 
		this.previousSearchResult = previousSearchResult;
		this.queriedObject = queriedObject; 
		if(logger.isDebugEnabled()){
			String strCQL = "";
			try {
				strCQL = ObjectSerializer.toString(cql, new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery"));
			} catch (SerializationException e) {			
				logger.error(e, e);
			}
			logger.debug("CQL query statement: " + "\r\n" + "\r\n" + strCQL);
			logger.debug("Grid location: " + gridLocation.toString());
			if(previousSearchResult == null){
				logger.debug("Previous search result: " + previousSearchResult);
			}else{
				logger.debug("Previous search result: " + previousSearchResult.toString());
			}
			if(queriedObject == null){
				logger.debug("Queried object: " + queriedObject);
			}else if(queriedObject instanceof Patient){
				Patient patient = Patient.class.cast(queriedObject);
				logger.debug("Queried object: " + patient.toString());
			}else if(queriedObject instanceof Study){
				Study study = Study.class.cast(queriedObject);
				logger.debug("Queried object: " + study.toString());
			}
		}	
	}
	
	/**
	 * @param cql - CQL query statement
	 * @param gridLocation - GRID location e.g. caGRID or NBIA 
	 * @param previousSearchResult - null with first query call 
	 * @param queriedObject - null with first query call
	 */
	public GridQuery(CQLQuery cql, GridLocation gridLocation, SearchResult previousSearchResult, Object queriedObject){
		this.cql = cql;
		this.gridLocation = gridLocation;
		this.previousSearchResult = previousSearchResult;
		this.queriedObject = queriedObject;
		if(logger.isDebugEnabled()){
			String strCQL = "";
			try {
				strCQL = ObjectSerializer.toString(cql, new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery"));
			} catch (SerializationException e) {			
				logger.error(e, e);
			}
			logger.debug("CQL query statement: " + strCQL);
			logger.debug("Grid location: " + gridLocation.toString());
			if(previousSearchResult == null){
				logger.debug("Previous search result: " + previousSearchResult);
			}else{
				logger.debug("Previous search result: " + previousSearchResult.toString());
			}
			if(queriedObject == null){
				logger.debug("Queried object: " + queriedObject);
			}else if(queriedObject instanceof Patient){
				Patient patient = Patient.class.cast(queriedObject);
				logger.debug("Queried object: " + patient.toString());
			}else if(queriedObject instanceof Study){
				Study study = Study.class.cast(queriedObject);
				logger.debug("Queried object: " + study.toString());
			}
		}		
	}
		
	SearchResult searchResult;
	public void run() {
		long time1 = System.currentTimeMillis();
		logger.info("Executing GRID query.");
		final CQLQuery fcqlq = cql;		
		CQLQueryResults results = null;
		try {
			switch (target) {
		    	case PATIENT:	 
							
		    	case STUDY:
		    		
		    	case SERIES:
		    		
			}
			
			if(gridLocation != null && gridLocation.getProtocolVersion().equalsIgnoreCase("DICOM")){
				results = dicomClient.query(fcqlq);
			}else if(gridLocation != null && gridLocation.getProtocolVersion().equalsIgnoreCase("NBIA-4.2")){
				results = nciaClient.query(fcqlq);
			}						
	        iter = new CQLQueryResultsIterator(results);        	                
	        searchResult = GridUtil.convertCQLQueryResultsIteratorToSearchResult(iter, gridLocation, previousSearchResult, queriedObject);
			
	        if(previousSearchResult == null){
				Criteria originalCriteria = new Criteria(dicomCriteria, aimCriteria);
				searchResult.setOriginalCriteria(originalCriteria);
			}
		} catch (MalformedQueryExceptionType e) {
			logger.error(e, e);
		} catch (QueryProcessingExceptionType e) {
			logger.error(e, e);
		} catch (RemoteException e) {
			logger.error(e, e);
		}
		long time2 = System.currentTimeMillis();
		logger.info("GRID query finished in " + (time2 - time1) + " ms");
		fireResultsAvailable();	
	}	
	
	@Override
	public SearchResult getSearchResult(){
		return searchResult;
	}
	
	DataAccessListener listener;
	@Override
	public void addDataAccessListener(DataAccessListener l) {
		listener = l;    
		
	}
	
	void fireResultsAvailable(){
		QueryEvent event = new QueryEvent(this);         		
        listener.queryResultsAvailable(event);
	}
}
