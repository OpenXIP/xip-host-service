/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.rmi.RemoteException;
import java.util.Map;
import org.apache.axis.types.URI.MalformedURIException;
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
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.ncia.client.NCIACoreServiceClient;

/**
 * @author Jaroslaw Krych
 *
 */
public class GridQuery implements Runnable, Query {
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
	
	public GridQuery(GridLocation location){
		this.gridLocation = location;
		try {
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("NCIAModelMap.properties");
			gridUtil = gridMgr.getGridUtil();
			gridUtil.loadNCIAModelMap(inputStream);
			System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");		
		} catch (FileNotFoundException e) {
			logger.error(e, e);
		} catch (IOException e) {
			logger.error(e, e);		}
	}
	
	@Override
	public void setQuery(Map<Integer, Object> dicomCriteria, Map<String, Object> aimCriteria, QueryTarget target, SearchResult previousSearchResult, Object queriedObject) {
		this.dicomCriteria = dicomCriteria; 
		cql = gridUtil.convertToCQLStatement(dicomCriteria, CQLTargetName.PATIENT);
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
		logger.info("Executing GRID query.");
		try {		 
			searchResult = query(cql, gridLocation, previousSearchResult, queriedObject);
			logger.info("GRID query finished.");
			fireResultsAvailable();			
		} catch (MalformedURIException e) {
			logger.error(e, e);
			searchResult = null;
			return;
		} catch (RemoteException e) {
			logger.error(e, e);
			searchResult = null;
			return;
		} catch (ConnectException e) {
			logger.error(e, e);
			searchResult = null;
			return;
		}		
	}
	
	DataServiceClient dicomClient = null;
	NCIACoreServiceClient nciaClient = null;

	/**
	 * Method used to perform progressive GRID query. 
	 * @param cql - CQL query statement
	 * @param gridLocation - GRID location e.g. caGRID or NBIA 
	 * @param previousSearchResult - null with first query call 
	 * @param queriedObject - null with first query call
	 * @return SearchResult object, that becomes previousSearchResult in subsequent query calls.
	 */
	public SearchResult query(CQLQuery query, GridLocation location, SearchResult previousSearchResult, Object queriedObject) throws MalformedURIException, RemoteException, ConnectException{		
		CQLQueryResultsIterator iter;		
		if(location != null && location.getProtocolVersion().equalsIgnoreCase("DICOM")){
			dicomClient = new DataServiceClient(location.getAddress());			
		}else if(location != null && location.getProtocolVersion().equalsIgnoreCase("NBIA-4.2")){
			nciaClient = new NCIACoreServiceClient(location.getAddress());
		}else{
			return null;
		}
		final CQLQuery fcqlq = query;		
		CQLQueryResults results = null;
		if(location != null && location.getProtocolVersion().equalsIgnoreCase("DICOM")){
			results = dicomClient.query(fcqlq);
		}else if(location != null && location.getProtocolVersion().equalsIgnoreCase("NBIA-4.2")){
			results = nciaClient.query(fcqlq);
		}						
        iter = new CQLQueryResultsIterator(results);        
        //SearchResult result = GridUtil.convertCQLQueryResultsIteratorToSearchResult(iter, location);	                
        SearchResult result = GridUtil.convertCQLQueryResultsIteratorToSearchResult(iter, location, previousSearchResult, queriedObject);
        return result;			
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
