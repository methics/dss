//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.05.06 at 11:35:02 AM CEST 
//


package eu.europa.esig.jaxb.policy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AlgoExpirationDate">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Algo" type="{http://dss.esig.europa.eu/validation/diagnostic}Algo" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="Format" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "algoExpirationDate"
})
public class Cryptographic {

    @XmlElement(name = "AlgoExpirationDate", required = true)
    protected AlgoExpirationDate algoExpirationDate;

    /**
     * Gets the value of the algoExpirationDate property.
     * 
     * @return
     *     possible object is
     *     {@link AlgoExpirationDate }
     *     
     */
    public AlgoExpirationDate getAlgoExpirationDate() {
        return algoExpirationDate;
    }

    /**
     * Sets the value of the algoExpirationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link AlgoExpirationDate }
     *     
     */
    public void setAlgoExpirationDate(AlgoExpirationDate value) {
        this.algoExpirationDate = value;
    }

}
