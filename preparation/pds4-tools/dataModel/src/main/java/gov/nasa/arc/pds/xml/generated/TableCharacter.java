//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.09.19 at 05:23:51 PM PDT 
//


package gov.nasa.arc.pds.xml.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 *  The Table Character class is an extension of table base and defines a simple character table. 
 * 
 * <p>Java class for Table_Character complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Table_Character">
 *   &lt;complexContent>
 *     &lt;extension base="{http://pds.nasa.gov/pds4/pds/v03}Table_Base">
 *       &lt;sequence>
 *         &lt;element name="record_delimiter" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed"/>
 *         &lt;element name="Uniformly_Sampled" type="{http://pds.nasa.gov/pds4/pds/v03}Uniformly_Sampled" minOccurs="0"/>
 *         &lt;element name="Record_Character" type="{http://pds.nasa.gov/pds4/pds/v03}Record_Character"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Table_Character", propOrder = {
    "recordDelimiter",
    "uniformlySampled",
    "recordCharacter"
})
@XmlSeeAlso({
    TransferManifest.class
})
public class TableCharacter
    extends TableBase
{

    @XmlElement(name = "record_delimiter", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String recordDelimiter;
    @XmlElement(name = "Uniformly_Sampled")
    protected UniformlySampled uniformlySampled;
    @XmlElement(name = "Record_Character", required = true)
    protected RecordCharacter recordCharacter;

    /**
     * Gets the value of the recordDelimiter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecordDelimiter() {
        return recordDelimiter;
    }

    /**
     * Sets the value of the recordDelimiter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecordDelimiter(String value) {
        this.recordDelimiter = value;
    }

    /**
     * Gets the value of the uniformlySampled property.
     * 
     * @return
     *     possible object is
     *     {@link UniformlySampled }
     *     
     */
    public UniformlySampled getUniformlySampled() {
        return uniformlySampled;
    }

    /**
     * Sets the value of the uniformlySampled property.
     * 
     * @param value
     *     allowed object is
     *     {@link UniformlySampled }
     *     
     */
    public void setUniformlySampled(UniformlySampled value) {
        this.uniformlySampled = value;
    }

    /**
     * Gets the value of the recordCharacter property.
     * 
     * @return
     *     possible object is
     *     {@link RecordCharacter }
     *     
     */
    public RecordCharacter getRecordCharacter() {
        return recordCharacter;
    }

    /**
     * Sets the value of the recordCharacter property.
     * 
     * @param value
     *     allowed object is
     *     {@link RecordCharacter }
     *     
     */
    public void setRecordCharacter(RecordCharacter value) {
        this.recordCharacter = value;
    }

}