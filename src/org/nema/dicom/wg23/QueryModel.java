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

package org.nema.dicom.wg23;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for queryModel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="queryModel">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="objUUIDs" type="{http://wg23.dicom.nema.org/}ArrayOfUUID"/>
 *         &lt;element name="modelXpaths" type="{http://wg23.dicom.nema.org/}ArrayOfString"/>
 *         &lt;element name="includeBulkDataPointers" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "queryModel", propOrder = {
    "objUUIDs",
    "modelXpaths",
    "includeBulkDataPointers"
})
public class QueryModel {

    @XmlElement(required = true)
    protected ArrayOfUUID objUUIDs;
    @XmlElement(required = true)
    protected ArrayOfString modelXpaths;
    protected boolean includeBulkDataPointers;

    /**
     * Gets the value of the objUUIDs property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUUID }
     *     
     */
    public ArrayOfUUID getObjUUIDs() {
        return objUUIDs;
    }

    /**
     * Sets the value of the objUUIDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUUID }
     *     
     */
    public void setObjUUIDs(ArrayOfUUID value) {
        this.objUUIDs = value;
    }

    /**
     * Gets the value of the modelXpaths property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getModelXpaths() {
        return modelXpaths;
    }

    /**
     * Sets the value of the modelXpaths property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setModelXpaths(ArrayOfString value) {
        this.modelXpaths = value;
    }

    /**
     * Gets the value of the includeBulkDataPointers property.
     * 
     */
    public boolean isIncludeBulkDataPointers() {
        return includeBulkDataPointers;
    }

    /**
     * Sets the value of the includeBulkDataPointers property.
     * 
     */
    public void setIncludeBulkDataPointers(boolean value) {
        this.includeBulkDataPointers = value;
    }

}
