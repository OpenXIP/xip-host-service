
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
