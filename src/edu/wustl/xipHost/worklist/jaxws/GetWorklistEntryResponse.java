
package edu.wustl.xipHost.worklist.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getWorklistEntryResponse", namespace = "http://edu.wustl.xipHost.worklist/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getWorklistEntryResponse", namespace = "http://edu.wustl.xipHost.worklist/")
public class GetWorklistEntryResponse {

    @XmlElement(name = "return", namespace = "")
    private edu.wustl.xipHost.worklist.WorklistEntry _return;

    /**
     * 
     * @return
     *     returns WorklistEntry
     */
    public edu.wustl.xipHost.worklist.WorklistEntry getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(edu.wustl.xipHost.worklist.WorklistEntry _return) {
        this._return = _return;
    }

}
