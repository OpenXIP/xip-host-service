
package edu.wustl.xipHost.worklist.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "deleteWorkListEntry", namespace = "http://edu.wustl.xipHost.worklist/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteWorkListEntry", namespace = "http://edu.wustl.xipHost.worklist/")
public class DeleteWorkListEntry {

    @XmlElement(name = "arg0", namespace = "")
    private edu.wustl.xipHost.worklist.WorklistEntry arg0;

    /**
     * 
     * @return
     *     returns WorklistEntry
     */
    public edu.wustl.xipHost.worklist.WorklistEntry getArg0() {
        return this.arg0;
    }

    /**
     * 
     * @param arg0
     *     the value for the arg0 property
     */
    public void setArg0(edu.wustl.xipHost.worklist.WorklistEntry arg0) {
        this.arg0 = arg0;
    }

}
