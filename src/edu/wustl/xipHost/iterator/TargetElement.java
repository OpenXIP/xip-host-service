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

import java.util.List;

/**
 * @author Jaroslaw Krych
 *
 */
public class TargetElement {

	//id is patientId, studyInstanceUID or seriesInstanceUID
	String id;
	List<SubElement> subElements;
	IterationTarget target;
	
	public TargetElement(String id, List<SubElement> subElements, IterationTarget target) {
		this.id = id;
		this.subElements = subElements;
		this.target = target;
	}

	public String getId() {
		return id;
	}

	public List<SubElement> getSubElements() {
		return subElements;
	}

	public IterationTarget getTarget() {
		return target;
	}

}
