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

package edu.wustl.xipHost.iterator;

import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
import edu.wustl.xipHost.dataAccess.Util;

/**
 * @author Jaroslaw Krych
 *
 */
public class Criteria {
	final static Logger logger = Logger.getLogger(Criteria.class);
	Map<Integer, Object> dicomCriteria;
	Map<String, Object> aimCriteria;
	
	/**
	 * 
	 */
	public Criteria(Map<Integer, Object> dicomCriteria,Map<String, Object> aimCriteria) {
		this.dicomCriteria = dicomCriteria;
		this.aimCriteria = aimCriteria;
	}
	
	
	public Map<Integer, Object> getDICOMCriteria(){
		return dicomCriteria;
	}
	
	public Map<String, Object> getAIMCriteria(){
		return aimCriteria;
	}
	
	public static void logDicomCriteria(Map<Integer, Object> dicomCriteria){
		logger.debug("DICOM criteria:");
		Iterator<Integer> iter = dicomCriteria.keySet().iterator();
		while(iter.hasNext()){
			Integer key = iter.next();
			String keyHexValue = Util.toDicomHex(key);
			String value = (String)dicomCriteria.get(key);
			logger.debug(keyHexValue + " " + value);
		}
	}
	
	public static void logAimCriteria(Map<String, Object> aimCriteria){
		logger.debug("AIM criteria:");
		Iterator<String> iter = aimCriteria.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			String value = (String) aimCriteria.get(key);
			logger.debug(key + " " + value);
		}
	}
}
