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

package edu.wustl.xipHost.avt2ext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.dicom.GeneralImage;
import com.siemens.scr.avt.ad.dicom.GeneralSeries;
import com.siemens.scr.avt.ad.dicom.GeneralStudy;
import com.siemens.scr.avt.ad.dicom.Patient;
import edu.wustl.xipHost.dataAccess.DataAccessListener;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.QueryEvent;
import edu.wustl.xipHost.dataAccess.QueryTarget;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.iterator.Criteria;

public class AVTQuery implements Query {
	final static Logger logger = Logger.getLogger(AVTQuery.class);
	ADFacade adService;	
	Map<Integer, Object> adDicomCriteria;
	Map<String, Object> adAimCriteria;
	QueryTarget target;
	SearchResult previousSearchResult;
	Object queriedObject;
	
	public AVTQuery(){
		
	}
	
	@Override
	public void setQuery(Map<Integer, Object> dicomCriteria, Map<String, Object> aimCriteria, QueryTarget target, SearchResult previousSearchResult, Object queriedObject) {
		this.adDicomCriteria = dicomCriteria; 
		this.adAimCriteria = aimCriteria; 
		this.target = target; 
		this.previousSearchResult = previousSearchResult;
		this.queriedObject = queriedObject;
		
	}
	
	public AVTQuery(Map<Integer, Object> adDicomCriteria, Map<String, Object> adAimCriteria, QueryTarget target, SearchResult previousSearchResult, Object queriedObject){
		this.adDicomCriteria = adDicomCriteria;
		this.adAimCriteria = adAimCriteria;
		this.target = target;
		this.previousSearchResult = previousSearchResult;
		this.queriedObject = queriedObject;
		adService = AVTFactory.getADServiceInstance();
		if(adService == null){
			logger.warn("AD database cannot be reached. adService=null.");
		}
		if(logger.isDebugEnabled()){
			if(adDicomCriteria == null){
				logger.debug("AD DICOM criteria: " + adDicomCriteria);
			}else{
				logger.debug("AD DICOM criteria:");				
				Set<Integer> keySet = adDicomCriteria.keySet();
				Iterator<Integer> iter = keySet.iterator();
				while(iter.hasNext()){
					Integer key = iter.next();					
					logger.debug("Hex Key: " + Integer.toHexString(key) + " Value: " + (String)adDicomCriteria.get(key));					
					logger.debug("Integer Key: " + key + " Value: " + (String)adDicomCriteria.get(key));
				}					
			}
			if(adAimCriteria == null){
				logger.debug("AD AIM criteria: " + adAimCriteria);
			}else{
				logger.debug("AD AIM criteria:");
				Set<String> keys = adAimCriteria.keySet();
				Iterator<String> iter = keys.iterator();
				while(iter.hasNext()){
					String key = iter.next();
					String value = (String) adAimCriteria.get(key);
					if(!value.isEmpty()){
						logger.debug("Key: " + key + " Value: " + value);
					}					
				}				
			}
			if(target == null){
				logger.debug("ADQueryTarget: " + target);
			}else{
				logger.debug("ADQueryTarget: " + target.toString());
			}
			if(previousSearchResult == null){
				logger.debug("Previous search result: " + previousSearchResult);
			}else{
				logger.debug("Previous search result: " + previousSearchResult.toString());
			}
			if(queriedObject == null){
				logger.debug("Queried object: " + queriedObject);
			}else if(queriedObject instanceof edu.wustl.xipHost.dataModel.Patient){
				edu.wustl.xipHost.dataModel.Patient patient = edu.wustl.xipHost.dataModel.Patient.class.cast(queriedObject);
				logger.debug("Queried object: " + patient.toString());
			}else if(queriedObject instanceof edu.wustl.xipHost.dataModel.Study){
				edu.wustl.xipHost.dataModel.Study study = edu.wustl.xipHost.dataModel.Study.class.cast(queriedObject);
				logger.debug("Queried object: " + study.toString());
			}else if(queriedObject instanceof edu.wustl.xipHost.dataModel.Series){
				edu.wustl.xipHost.dataModel.Series series = edu.wustl.xipHost.dataModel.Series.class.cast(queriedObject);
				logger.debug("Queried object: " + series.toString());
			}
		}
	}
	
	SearchResult result;
	public void run() {											
		long time1 = System.currentTimeMillis();
		logger.info("Executing AVT query.");		
		switch (target) {
        	case PATIENT:
        		List<Patient> patients;
        		try{
        			patients = adService.findPatientByCriteria(adDicomCriteria, adAimCriteria);
        			result = AVTUtil.convertToSearchResult(patients, null, null);
        		} catch (Exception e){
        			patients = new ArrayList<Patient>();
        			logger.error(e, e);        		
        			return;
        		}        		
        		break;
        	case STUDY:
        		List<GeneralStudy> studies;
        		try{
        			studies = adService.findStudiesByCriteria(adDicomCriteria, adAimCriteria);
        			result = AVTUtil.convertToSearchResult(studies, previousSearchResult, queriedObject);
        		} catch (Exception e){
        			studies = new ArrayList<GeneralStudy>();
        			logger.error(e, e);        			
        			return;
        		}
        		break;
			case SERIES: 
				List<GeneralSeries> series;
				try{
					series = adService.findSeriesByCriteria(adDicomCriteria, adAimCriteria);
					result = AVTUtil.convertToSearchResult(series, previousSearchResult, queriedObject);
				} catch (Exception e){
					series = new ArrayList<GeneralSeries>();
        			logger.error(e, e);        		
        			return;
				}
				break;
			case ITEM: 
				List<GeneralImage> images;
				List<String> annotations;
				try{
					images = adService.findImagesByCriteria(adDicomCriteria, adAimCriteria);				
					annotations = adService.findAnnotations(adDicomCriteria, adAimCriteria);
					Set<String> uniqueAnnots = new HashSet<String>(annotations);
					List<Object> listOfObjects = new ArrayList<Object>();
					listOfObjects.addAll(images);
					listOfObjects.addAll(uniqueAnnots);
					result = AVTUtil.convertToSearchResult(listOfObjects, previousSearchResult, queriedObject);
				} catch (Exception e){
					images = new ArrayList<GeneralImage>();
					annotations = new ArrayList<String>();
					logger.error(e, e);
        			return;
				}				
				break;
			default: logger.warn("Unidentified ADQueryTarget");break;
		}		
		//Set original criteria on SearchResult.
		if(previousSearchResult == null){
			Criteria originalCriteria = new Criteria(adDicomCriteria, adAimCriteria);
			result.setOriginalCriteria(originalCriteria);
		}
		if(logger.isDebugEnabled()){
			Criteria criteria = result.getOriginalCriteria();
			Map<Integer, Object> dicomCriteria = criteria.getDICOMCriteria();
			Map<String, Object> aimCriteria = criteria.getAIMCriteria();
			if(dicomCriteria == null){
				logger.debug("AD original DICOM criteria: " + adDicomCriteria);
			}else{
				logger.debug("AD original DICOM criteria:");				
				Set<Integer> keySet = dicomCriteria.keySet();
				Iterator<Integer> iter = keySet.iterator();
				while(iter.hasNext()){
					Integer key = iter.next();					
					logger.debug("Hex Key: " + Integer.toHexString(key) + " Value: " + (String)dicomCriteria.get(key));					
					logger.debug("Integer Key: " + key + " Value: " + (String)dicomCriteria.get(key));
				}					
			}
			if(adAimCriteria == null){
				logger.debug("AD original AIM criteria: " + aimCriteria);
			}else{
				logger.debug("AD original AIM criteria:");
				Set<String> keys = aimCriteria.keySet();
				Iterator<String> iter = keys.iterator();
				while(iter.hasNext()){
					String key = iter.next();
					String value = (String) aimCriteria.get(key);
					if(!value.isEmpty()){
						logger.debug("Key: " + key + " Value: " + value);
					}					
				}				
			}
		}
		long time2 = System.currentTimeMillis();
		logger.info("AVT query finished in: " + (time2 - time1) + " ms");
		fireResultsAvailable();
	}	
	
	@Override
	public SearchResult getSearchResult() {
		return result;
	}
	
	void fireResultsAvailable(){
		QueryEvent event = new QueryEvent(this);         		
        listener.queryResultsAvailable(event);
	}

	DataAccessListener listener;
	@Override
	public void addDataAccessListener(DataAccessListener l) {
		listener = l;
	}
}
