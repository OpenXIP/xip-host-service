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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Jaroslaw Krych
 *
 */
public class Series {
	String seriesNumber;
	String modality;	
	String seriesDesc;
	String seriesInstanceUID;	
	List<Item> items = new ArrayList<Item>();
	Timestamp lastUpdated = null;
	
	public Series(String seriesNumber, String modality, String seriesDesc, String seriesInstanceUID){
		this.seriesNumber = seriesNumber;
		this.modality = modality;
		this.seriesDesc = seriesDesc;
		this.seriesInstanceUID = seriesInstanceUID;
	}
	public String getSeriesNumber(){
		return this.seriesNumber;
	}
	public String getModality(){
		return this.modality;
	}
	public String getSeriesDesc(){
		return this.seriesDesc;		
	}
	public String getSeriesInstanceUID(){
		return this.seriesInstanceUID;		
	}	
	public String toString(){
		String str = "";
		if(seriesNumber.isEmpty()){
			str = "Series:" + seriesInstanceUID + " " + modality; 
		}else{
			str = "Series:" + seriesNumber + " " + modality; 
		}			
		return new String(str);
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
	
	public void sort(){
		Collections.sort(items, new SortByUID());
	}
	
	class SortByUID implements Comparator<Item> {
		@Override
		public int compare(Item o1, Item o2) {			
			return o1.getItemID().compareTo(o2.getItemID());
		}
		
	}

	public Timestamp getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	@Override
	public boolean equals(Object objectSeries){
		Series series = null;
		if(objectSeries instanceof Series){
			series = Series.class.cast(objectSeries);
			String seriesInstanceUID = series.getSeriesInstanceUID();
			if(seriesInstanceUID.equalsIgnoreCase(this.seriesInstanceUID)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return this.seriesInstanceUID.hashCode();
	}
}
