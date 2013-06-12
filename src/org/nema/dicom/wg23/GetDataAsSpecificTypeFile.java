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
 * <p>Java class for getDataAsSpecificTypeFile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDataAsSpecificTypeFile">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="objectUUIDs" type="{http://wg23.dicom.nema.org/}ArrayOfUUID"/>
 *         &lt;element name="mimeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="transferSyntaxUID" type="{http://wg23.dicom.nema.org/}uid"/>
 *         &lt;element name="includeBulkData" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDataAsSpecificTypeFile", propOrder = {
    "objectUUIDs",
    "mimeType",
    "transferSyntaxUID",
    "includeBulkData"
})
public class GetDataAsSpecificTypeFile {

    @XmlElement(required = true)
    protected ArrayOfUUID objectUUIDs;
    @XmlElement(required = true)
    protected String mimeType;
    @XmlElement(required = true)
    protected Uid transferSyntaxUID;
    protected boolean includeBulkData;

    /**
     * Gets the value of the objectUUIDs property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUUID }
     *     
     */
    public ArrayOfUUID getObjectUUIDs() {
        return objectUUIDs;
    }

    /**
     * Sets the value of the objectUUIDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUUID }
     *     
     */
    public void setObjectUUIDs(ArrayOfUUID value) {
        this.objectUUIDs = value;
    }

    /**
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

    /**
     * Gets the value of the transferSyntaxUID property.
     * 
     * @return
     *     possible object is
     *     {@link Uid }
     *     
     */
    public Uid getTransferSyntaxUID() {
        return transferSyntaxUID;
    }

    /**
     * Sets the value of the transferSyntaxUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Uid }
     *     
     */
    public void setTransferSyntaxUID(Uid value) {
        this.transferSyntaxUID = value;
    }

    /**
     * Gets the value of the includeBulkData property.
     * 
     */
    public boolean isIncludeBulkData() {
        return includeBulkData;
    }

    /**
     * Sets the value of the includeBulkData property.
     * 
     */
    public void setIncludeBulkData(boolean value) {
        this.includeBulkData = value;
    }

}
