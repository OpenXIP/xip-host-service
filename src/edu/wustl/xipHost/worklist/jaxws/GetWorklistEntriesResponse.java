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

package edu.wustl.xipHost.worklist.jaxws;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getWorklistEntriesResponse", namespace = "http://edu.wustl.xipHost.worklist/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getWorklistEntriesResponse", namespace = "http://edu.wustl.xipHost.worklist/")
public class GetWorklistEntriesResponse {

    @XmlElement(name = "return", namespace = "")
    private List<edu.wustl.xipHost.worklist.WorklistEntry> _return;

    /**
     * 
     * @return
     *     returns List<WorklistEntry>
     */
    public List<edu.wustl.xipHost.worklist.WorklistEntry> getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(List<edu.wustl.xipHost.worklist.WorklistEntry> _return) {
        this._return = _return;
    }

}
