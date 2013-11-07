/**
 * Address.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.strikeiron.www;

import com.strikeiron.www.api.BasicAddress;

public class Address extends BasicAddress implements java.io.Serializable {
    private String organization;

    private String department;

    private String contact;

    private String buildingName;

    private String streetNumber;

    private String streetName;

    private String POBox;

    private String locality;

    private String province;

    private String postalCode;

    private String formattedAddressLines;
    private int statusNbr;
    private String statusDescription;
    private String confidencePercentage;

    public Address() {
    }

    public Address(
            String streetAddressLines,
            String countrySpecificLocalityLine,
            String country,
            String organization,
            String department,
            String contact,
            String buildingName,
            String streetNumber,
            String streetName,
            String POBox,
            String locality,
            String province,
            String postalCode,
            String formattedAddressLines) {
        super(
                streetAddressLines,
                countrySpecificLocalityLine,
                country);
        this.organization = organization;
        this.department = department;
        this.contact = contact;
        this.buildingName = buildingName;
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.POBox = POBox;
        this.locality = locality;
        this.province = province;
        this.postalCode = postalCode;
        this.formattedAddressLines = formattedAddressLines;
    }


    /**
     * Gets the organization value for this GlobalAddress.
     *
     * @return organization
     */
    public String getOrganization() {
        return organization;
    }


    /**
     * Sets the organization value for this GlobalAddress.
     *
     * @param organization
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }


    /**
     * Gets the department value for this GlobalAddress.
     *
     * @return department
     */
    public String getDepartment() {
        return department;
    }


    /**
     * Sets the department value for this GlobalAddress.
     *
     * @param department
     */
    public void setDepartment(String department) {
        this.department = department;
    }


    /**
     * Gets the contact value for this GlobalAddress.
     *
     * @return contact
     */
    public String getContact() {
        return contact;
    }


    /**
     * Sets the contact value for this GlobalAddress.
     *
     * @param contact
     */
    public void setContact(String contact) {
        this.contact = contact;
    }


    /**
     * Gets the buildingName value for this GlobalAddress.
     *
     * @return buildingName
     */
    public String getBuildingName() {
        return buildingName;
    }


    /**
     * Sets the buildingName value for this GlobalAddress.
     *
     * @param buildingName
     */
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }


    /**
     * Gets the streetNumber value for this GlobalAddress.
     *
     * @return streetNumber
     */
    public String getStreetNumber() {
        return streetNumber;
    }


    /**
     * Sets the streetNumber value for this GlobalAddress.
     *
     * @param streetNumber
     */
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }


    /**
     * Gets the streetName value for this GlobalAddress.
     *
     * @return streetName
     */
    public String getStreetName() {
        return streetName;
    }


    /**
     * Sets the streetName value for this GlobalAddress.
     *
     * @param streetName
     */
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }


    /**
     * Gets the POBox value for this GlobalAddress.
     *
     * @return POBox
     */
    public String getPOBox() {
        return POBox;
    }


    /**
     * Sets the POBox value for this GlobalAddress.
     *
     * @param POBox
     */
    public void setPOBox(String POBox) {
        this.POBox = POBox;
    }


    /**
     * Gets the locality value for this GlobalAddress.
     *
     * @return locality
     */
    public String getLocality() {
        return locality;
    }


    /**
     * Sets the locality value for this GlobalAddress.
     *
     * @param locality
     */
    public void setLocality(String locality) {
        this.locality = locality;
    }


    /**
     * Gets the province value for this GlobalAddress.
     *
     * @return province
     */
    public String getProvince() {
        return province;
    }


    /**
     * Sets the province value for this GlobalAddress.
     *
     * @param province
     */
    public void setProvince(String province) {
        this.province = province;
    }


    /**
     * Gets the postalCode value for this GlobalAddress.
     *
     * @return postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }


    /**
     * Sets the postalCode value for this GlobalAddress.
     *
     * @param postalCode
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }


    /**
     * Gets the formattedAddressLines value for this GlobalAddress.
     *
     * @return formattedAddressLines
     */
    public String getFormattedAddressLines() {
        return formattedAddressLines;
    }


    /**
     * Sets the formattedAddressLines value for this GlobalAddress.
     *
     * @param formattedAddressLines
     */
    public void setFormattedAddressLines(String formattedAddressLines) {
        this.formattedAddressLines = formattedAddressLines;
    }

    private Object __equalsCalc = null;

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof Address)) return false;
        Address other = (Address) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) &&
                ((this.organization == null && other.getOrganization() == null) ||
                        (this.organization != null &&
                                this.organization.equals(other.getOrganization()))) &&
                ((this.department == null && other.getDepartment() == null) ||
                        (this.department != null &&
                                this.department.equals(other.getDepartment()))) &&
                ((this.contact == null && other.getContact() == null) ||
                        (this.contact != null &&
                                this.contact.equals(other.getContact()))) &&
                ((this.buildingName == null && other.getBuildingName() == null) ||
                        (this.buildingName != null &&
                                this.buildingName.equals(other.getBuildingName()))) &&
                ((this.streetNumber == null && other.getStreetNumber() == null) ||
                        (this.streetNumber != null &&
                                this.streetNumber.equals(other.getStreetNumber()))) &&
                ((this.streetName == null && other.getStreetName() == null) ||
                        (this.streetName != null &&
                                this.streetName.equals(other.getStreetName()))) &&
                ((this.POBox == null && other.getPOBox() == null) ||
                        (this.POBox != null &&
                                this.POBox.equals(other.getPOBox()))) &&
                ((this.locality == null && other.getLocality() == null) ||
                        (this.locality != null &&
                                this.locality.equals(other.getLocality()))) &&
                ((this.province == null && other.getProvince() == null) ||
                        (this.province != null &&
                                this.province.equals(other.getProvince()))) &&
                ((this.postalCode == null && other.getPostalCode() == null) ||
                        (this.postalCode != null &&
                                this.postalCode.equals(other.getPostalCode()))) &&
                ((this.formattedAddressLines == null && other.getFormattedAddressLines() == null) ||
                        (this.formattedAddressLines != null &&
                                this.formattedAddressLines.equals(other.getFormattedAddressLines())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getOrganization() != null) {
            _hashCode += getOrganization().hashCode();
        }
        if (getDepartment() != null) {
            _hashCode += getDepartment().hashCode();
        }
        if (getContact() != null) {
            _hashCode += getContact().hashCode();
        }
        if (getBuildingName() != null) {
            _hashCode += getBuildingName().hashCode();
        }
        if (getStreetNumber() != null) {
            _hashCode += getStreetNumber().hashCode();
        }
        if (getStreetName() != null) {
            _hashCode += getStreetName().hashCode();
        }
        if (getPOBox() != null) {
            _hashCode += getPOBox().hashCode();
        }
        if (getLocality() != null) {
            _hashCode += getLocality().hashCode();
        }
        if (getProvince() != null) {
            _hashCode += getProvince().hashCode();
        }
        if (getPostalCode() != null) {
            _hashCode += getPostalCode().hashCode();
        }
        if (getFormattedAddressLines() != null) {
            _hashCode += getFormattedAddressLines().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
            new org.apache.axis.description.TypeDesc(Address.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "Address"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("organization");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "Organization"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("department");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "Department"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contact");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "Contact"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buildingName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "BuildingName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("streetNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "StreetNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("streetName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "StreetName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("POBox");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "POBox"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("locality");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "Locality"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("province");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "Province"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("postalCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "PostalCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("formattedAddressLines");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "FormattedAddressLines"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
            String mechType,
            Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return
                new org.apache.axis.encoding.ser.BeanSerializer(
                        _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
            String mechType,
            Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return
                new org.apache.axis.encoding.ser.BeanDeserializer(
                        _javaType, _xmlType, typeDesc);
    }

    public void setStatusNbr(int statusNbr) {
        this.statusNbr = statusNbr;
    }

    public int getStatusNbr() {
        return statusNbr;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setConfidencePercentage(String confidencePercentage) {
        this.confidencePercentage = confidencePercentage;
    }

    public String getConfidencePercentage() {
        return confidencePercentage;
    }
}
