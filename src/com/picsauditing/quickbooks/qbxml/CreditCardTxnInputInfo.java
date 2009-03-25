//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.03.12 at 04:00:19 PM PDT 
//


package com.picsauditing.quickbooks.qbxml;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *         &lt;element name="CreditCardNumber">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="25"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{}ExpirationMonth"/>
 *         &lt;element ref="{}ExpirationYear"/>
 *         &lt;element name="NameOnCard">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="41"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CreditCardAddress" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="41"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CreditCardPostalCode" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="18"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CommercialCardCode" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="24"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{}TransactionMode" minOccurs="0"/>
 *         &lt;element ref="{}CreditCardTxnType" minOccurs="0"/>
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
    "creditCardNumber",
    "expirationMonth",
    "expirationYear",
    "nameOnCard",
    "creditCardAddress",
    "creditCardPostalCode",
    "commercialCardCode",
    "transactionMode",
    "creditCardTxnType"
})
@XmlRootElement(name = "CreditCardTxnInputInfo")
public class CreditCardTxnInputInfo {

    @XmlElement(name = "CreditCardNumber", required = true)
    protected String creditCardNumber;
    @XmlElement(name = "ExpirationMonth")
    protected int expirationMonth;
    @XmlElement(name = "ExpirationYear", required = true)
    protected BigInteger expirationYear;
    @XmlElement(name = "NameOnCard", required = true)
    protected String nameOnCard;
    @XmlElement(name = "CreditCardAddress")
    protected String creditCardAddress;
    @XmlElement(name = "CreditCardPostalCode")
    protected String creditCardPostalCode;
    @XmlElement(name = "CommercialCardCode")
    protected String commercialCardCode;
    @XmlElement(name = "TransactionMode", defaultValue = "CardNotPresent")
    protected String transactionMode;
    @XmlElement(name = "CreditCardTxnType")
    protected String creditCardTxnType;

    /**
     * Gets the value of the creditCardNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    /**
     * Sets the value of the creditCardNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditCardNumber(String value) {
        this.creditCardNumber = value;
    }

    /**
     * Gets the value of the expirationMonth property.
     * 
     */
    public int getExpirationMonth() {
        return expirationMonth;
    }

    /**
     * Sets the value of the expirationMonth property.
     * 
     */
    public void setExpirationMonth(int value) {
        this.expirationMonth = value;
    }

    /**
     * Gets the value of the expirationYear property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getExpirationYear() {
        return expirationYear;
    }

    /**
     * Sets the value of the expirationYear property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setExpirationYear(BigInteger value) {
        this.expirationYear = value;
    }

    /**
     * Gets the value of the nameOnCard property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameOnCard() {
        return nameOnCard;
    }

    /**
     * Sets the value of the nameOnCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameOnCard(String value) {
        this.nameOnCard = value;
    }

    /**
     * Gets the value of the creditCardAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditCardAddress() {
        return creditCardAddress;
    }

    /**
     * Sets the value of the creditCardAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditCardAddress(String value) {
        this.creditCardAddress = value;
    }

    /**
     * Gets the value of the creditCardPostalCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditCardPostalCode() {
        return creditCardPostalCode;
    }

    /**
     * Sets the value of the creditCardPostalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditCardPostalCode(String value) {
        this.creditCardPostalCode = value;
    }

    /**
     * Gets the value of the commercialCardCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommercialCardCode() {
        return commercialCardCode;
    }

    /**
     * Sets the value of the commercialCardCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommercialCardCode(String value) {
        this.commercialCardCode = value;
    }

    /**
     * Gets the value of the transactionMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionMode() {
        return transactionMode;
    }

    /**
     * Sets the value of the transactionMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionMode(String value) {
        this.transactionMode = value;
    }

    /**
     * Gets the value of the creditCardTxnType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditCardTxnType() {
        return creditCardTxnType;
    }

    /**
     * Sets the value of the creditCardTxnType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditCardTxnType(String value) {
        this.creditCardTxnType = value;
    }

}
