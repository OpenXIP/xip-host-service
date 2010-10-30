
package org.nema.dicom.wg23;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for rectangle complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rectangle">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="refPointX" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="refPointY" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="width" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="height" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rectangle", propOrder = {
    "refPointX",
    "refPointY",
    "width",
    "height"
})
public class Rectangle {

    protected int refPointX;
    protected int refPointY;
    protected int width;
    protected int height;

    /**
     * Gets the value of the refPointX property.
     * 
     */
    public int getRefPointX() {
        return refPointX;
    }

    /**
     * Sets the value of the refPointX property.
     * 
     */
    public void setRefPointX(int value) {
        this.refPointX = value;
    }

    /**
     * Gets the value of the refPointY property.
     * 
     */
    public int getRefPointY() {
        return refPointY;
    }

    /**
     * Sets the value of the refPointY property.
     * 
     */
    public void setRefPointY(int value) {
        this.refPointY = value;
    }

    /**
     * Gets the value of the width property.
     * 
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     */
    public void setWidth(int value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     * 
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     */
    public void setHeight(int value) {
        this.height = value;
    }

}
