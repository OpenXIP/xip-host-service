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

package edu.wustl.xipHost.dataModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
/**
 * @author Jaroslaw Krych
 *
 */
public class Study {
	String studyDate;
	String studyID;
	String studyDesc;
	String studyInstanceUID;
	List<Series> series = new ArrayList<Series>();
	List<Item> items = new ArrayList<Item>();
	Timestamp lastUpdated = null;
	
	public Study(String studyDate, String studyID, String studyDesc, String studyInstanceUID){
		this.studyDate = studyDate;
		this.studyID = studyID;
		this.studyDesc = studyDesc;
		this.studyInstanceUID = studyInstanceUID;
	}
	
	public String getStudyDate(){
		return this.studyDate;
	}
	
	public String getStudyID(){
		return this.studyID;
	}
	
	public String getStudyDesc(){
		return this.studyDesc;
	}
	
	public String getStudyInstanceUID(){
		return this.studyInstanceUID;
	}
	
	public String toString(){		
		String studyId;
		if(this.studyID == ""){
			studyId = this.studyInstanceUID;
		}else{
			studyId = this.studyID;
		}
		return new String("Study:" + this.studyDate + " " + studyId + " " + this.studyDesc);
	}
	
	public void addSeries(Series series){
		this.series.add(series);
	}
	
	public void removeSeries(Series seriesToRemove){
		this.series.remove(seriesToRemove);
	}
	
	public List<Series> getSeries(){
		return series;
	}
	
	public boolean contains(String seriesInstanceUID){
		List<Series> series = getSeries();
		for(int i = 0; i < series.size(); i++){
			if(series.get(i).getSeriesInstanceUID().equalsIgnoreCase(seriesInstanceUID)){return true;}
		}			
		return false;
	}
	
	public Series getSeries(String seriesInstanceUID){
		for(Series oneSeries : series){
			if(oneSeries.getSeriesInstanceUID().equalsIgnoreCase(seriesInstanceUID)){
				return oneSeries;
			}
		}
		return null;
	}
	
	public void addItem(Item item){
		this.items.add(item);
	}
	public List<Item> getItems(){
		return items;
	}
	public boolean containsItem(String itemID){
		for(int i = 0; i < items.size(); i++){
			if(items.get(i).getItemID().equalsIgnoreCase(itemID)){return true;}
		}			
		return false;
	}

	public Timestamp getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	@Override
	public boolean equals(Object objectStudy){
		Study study = null;
		if(objectStudy instanceof Study){
			study = Study.class.cast(objectStudy);
			String studyInstanceUID = study.getStudyInstanceUID();
			if(studyInstanceUID.equalsIgnoreCase(this.studyInstanceUID)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return this.studyInstanceUID.hashCode();
	}
}
