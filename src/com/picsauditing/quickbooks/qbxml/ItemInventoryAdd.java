//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.03.12 at 04:00:19 PM PDT 
//


package com.picsauditing.quickbooks.qbxml;

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
 *         &lt;element name="Name">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="31"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{}IsActive" minOccurs="0"/>
 *         &lt;element ref="{}ParentRef" minOccurs="0"/>
 *         &lt;element name="ManufacturerPartNumber" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="31"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{}UnitOfMeasureSetRef" minOccurs="0"/>
 *         &lt;element ref="{}IsTaxIncluded" minOccurs="0"/>
 *         &lt;element ref="{}SalesTaxCodeRef" minOccurs="0"/>
 *         &lt;element name="SalesDesc" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="4095"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{}SalesPrice" minOccurs="0"/>
 *         &lt;element ref="{}IncomeAccountRef" minOccurs="0"/>
 *         &lt;element name="PurchaseDesc" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="4095"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{}PurchaseCost" minOccurs="0"/>
 *         &lt;element ref="{}PurchaseTaxCodeRef" minOccurs="0"/>
 *         &lt;element ref="{}COGSAccountRef" minOccurs="0"/>
 *         &lt;element ref="{}PrefVendorRef" minOccurs="0"/>
 *         &lt;element ref="{}AssetAccountRef" minOccurs="0"/>
 *         &lt;element ref="{}ReorderPoint" minOccurs="0"/>
 *         &lt;element ref="{}QuantityOnHand" minOccurs="0"/>
 *         &lt;element ref="{}TotalValue" minOccurs="0"/>
 *         &lt;element ref="{}InventoryDate" minOccurs="0"/>
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
    "name",
    "isActive",
    "parentRef",
    "manufacturerPartNumber",
    "unitOfMeasureSetRef",
    "isTaxIncluded",
    "salesTaxCodeRef",
    "salesDesc",
    "salesPrice",
    "incomeAccountRef",
    "purchaseDesc",
    "purchaseCost",
    "purchaseTaxCodeRef",
    "cogsAccountRef",
    "prefVendorRef",
    "assetAccountRef",
    "reorderPoint",
    "quantityOnHand",
    "totalValue",
    "inventoryDate"
})
@XmlRootElement(name = "ItemInventoryAdd")
public class ItemInventoryAdd {

    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "IsActive")
    protected String isActive;
    @XmlElement(name = "ParentRef")
    protected ParentRef parentRef;
    @XmlElement(name = "ManufacturerPartNumber")
    protected String manufacturerPartNumber;
    @XmlElement(name = "UnitOfMeasureSetRef")
    protected UnitOfMeasureSetRef unitOfMeasureSetRef;
    @XmlElement(name = "IsTaxIncluded")
    protected String isTaxIncluded;
    @XmlElement(name = "SalesTaxCodeRef")
    protected SalesTaxCodeRef salesTaxCodeRef;
    @XmlElement(name = "SalesDesc")
    protected String salesDesc;
    @XmlElement(name = "SalesPrice")
    protected String salesPrice;
    @XmlElement(name = "IncomeAccountRef")
    protected IncomeAccountRef incomeAccountRef;
    @XmlElement(name = "PurchaseDesc")
    protected String purchaseDesc;
    @XmlElement(name = "PurchaseCost")
    protected String purchaseCost;
    @XmlElement(name = "PurchaseTaxCodeRef")
    protected PurchaseTaxCodeRef purchaseTaxCodeRef;
    @XmlElement(name = "COGSAccountRef")
    protected COGSAccountRef cogsAccountRef;
    @XmlElement(name = "PrefVendorRef")
    protected PrefVendorRef prefVendorRef;
    @XmlElement(name = "AssetAccountRef")
    protected AssetAccountRef assetAccountRef;
    @XmlElement(name = "ReorderPoint")
    protected String reorderPoint;
    @XmlElement(name = "QuantityOnHand")
    protected String quantityOnHand;
    @XmlElement(name = "TotalValue")
    protected String totalValue;
    @XmlElement(name = "InventoryDate")
    protected String inventoryDate;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the isActive property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsActive() {
        return isActive;
    }

    /**
     * Sets the value of the isActive property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsActive(String value) {
        this.isActive = value;
    }

    /**
     * Gets the value of the parentRef property.
     * 
     * @return
     *     possible object is
     *     {@link ParentRef }
     *     
     */
    public ParentRef getParentRef() {
        return parentRef;
    }

    /**
     * Sets the value of the parentRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParentRef }
     *     
     */
    public void setParentRef(ParentRef value) {
        this.parentRef = value;
    }

    /**
     * Gets the value of the manufacturerPartNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManufacturerPartNumber() {
        return manufacturerPartNumber;
    }

    /**
     * Sets the value of the manufacturerPartNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManufacturerPartNumber(String value) {
        this.manufacturerPartNumber = value;
    }

    /**
     * Gets the value of the unitOfMeasureSetRef property.
     * 
     * @return
     *     possible object is
     *     {@link UnitOfMeasureSetRef }
     *     
     */
    public UnitOfMeasureSetRef getUnitOfMeasureSetRef() {
        return unitOfMeasureSetRef;
    }

    /**
     * Sets the value of the unitOfMeasureSetRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnitOfMeasureSetRef }
     *     
     */
    public void setUnitOfMeasureSetRef(UnitOfMeasureSetRef value) {
        this.unitOfMeasureSetRef = value;
    }

    /**
     * Gets the value of the isTaxIncluded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsTaxIncluded() {
        return isTaxIncluded;
    }

    /**
     * Sets the value of the isTaxIncluded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsTaxIncluded(String value) {
        this.isTaxIncluded = value;
    }

    /**
     * Gets the value of the salesTaxCodeRef property.
     * 
     * @return
     *     possible object is
     *     {@link SalesTaxCodeRef }
     *     
     */
    public SalesTaxCodeRef getSalesTaxCodeRef() {
        return salesTaxCodeRef;
    }

    /**
     * Sets the value of the salesTaxCodeRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesTaxCodeRef }
     *     
     */
    public void setSalesTaxCodeRef(SalesTaxCodeRef value) {
        this.salesTaxCodeRef = value;
    }

    /**
     * Gets the value of the salesDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSalesDesc() {
        return salesDesc;
    }

    /**
     * Sets the value of the salesDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSalesDesc(String value) {
        this.salesDesc = value;
    }

    /**
     * Gets the value of the salesPrice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSalesPrice() {
        return salesPrice;
    }

    /**
     * Sets the value of the salesPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSalesPrice(String value) {
        this.salesPrice = value;
    }

    /**
     * Gets the value of the incomeAccountRef property.
     * 
     * @return
     *     possible object is
     *     {@link IncomeAccountRef }
     *     
     */
    public IncomeAccountRef getIncomeAccountRef() {
        return incomeAccountRef;
    }

    /**
     * Sets the value of the incomeAccountRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link IncomeAccountRef }
     *     
     */
    public void setIncomeAccountRef(IncomeAccountRef value) {
        this.incomeAccountRef = value;
    }

    /**
     * Gets the value of the purchaseDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurchaseDesc() {
        return purchaseDesc;
    }

    /**
     * Sets the value of the purchaseDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPurchaseDesc(String value) {
        this.purchaseDesc = value;
    }

    /**
     * Gets the value of the purchaseCost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurchaseCost() {
        return purchaseCost;
    }

    /**
     * Sets the value of the purchaseCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPurchaseCost(String value) {
        this.purchaseCost = value;
    }

    /**
     * Gets the value of the purchaseTaxCodeRef property.
     * 
     * @return
     *     possible object is
     *     {@link PurchaseTaxCodeRef }
     *     
     */
    public PurchaseTaxCodeRef getPurchaseTaxCodeRef() {
        return purchaseTaxCodeRef;
    }

    /**
     * Sets the value of the purchaseTaxCodeRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link PurchaseTaxCodeRef }
     *     
     */
    public void setPurchaseTaxCodeRef(PurchaseTaxCodeRef value) {
        this.purchaseTaxCodeRef = value;
    }

    /**
     * Gets the value of the cogsAccountRef property.
     * 
     * @return
     *     possible object is
     *     {@link COGSAccountRef }
     *     
     */
    public COGSAccountRef getCOGSAccountRef() {
        return cogsAccountRef;
    }

    /**
     * Sets the value of the cogsAccountRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link COGSAccountRef }
     *     
     */
    public void setCOGSAccountRef(COGSAccountRef value) {
        this.cogsAccountRef = value;
    }

    /**
     * Gets the value of the prefVendorRef property.
     * 
     * @return
     *     possible object is
     *     {@link PrefVendorRef }
     *     
     */
    public PrefVendorRef getPrefVendorRef() {
        return prefVendorRef;
    }

    /**
     * Sets the value of the prefVendorRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrefVendorRef }
     *     
     */
    public void setPrefVendorRef(PrefVendorRef value) {
        this.prefVendorRef = value;
    }

    /**
     * Gets the value of the assetAccountRef property.
     * 
     * @return
     *     possible object is
     *     {@link AssetAccountRef }
     *     
     */
    public AssetAccountRef getAssetAccountRef() {
        return assetAccountRef;
    }

    /**
     * Sets the value of the assetAccountRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetAccountRef }
     *     
     */
    public void setAssetAccountRef(AssetAccountRef value) {
        this.assetAccountRef = value;
    }

    /**
     * Gets the value of the reorderPoint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReorderPoint() {
        return reorderPoint;
    }

    /**
     * Sets the value of the reorderPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReorderPoint(String value) {
        this.reorderPoint = value;
    }

    /**
     * Gets the value of the quantityOnHand property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuantityOnHand() {
        return quantityOnHand;
    }

    /**
     * Sets the value of the quantityOnHand property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuantityOnHand(String value) {
        this.quantityOnHand = value;
    }

    /**
     * Gets the value of the totalValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalValue() {
        return totalValue;
    }

    /**
     * Sets the value of the totalValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalValue(String value) {
        this.totalValue = value;
    }

    /**
     * Gets the value of the inventoryDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInventoryDate() {
        return inventoryDate;
    }

    /**
     * Sets the value of the inventoryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInventoryDate(String value) {
        this.inventoryDate = value;
    }

}
