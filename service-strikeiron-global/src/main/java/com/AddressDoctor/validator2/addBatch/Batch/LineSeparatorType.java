/**
 * LineSeparatorType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.AddressDoctor.validator2.addBatch.Batch;

public class LineSeparatorType implements java.io.Serializable {
    private String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected LineSeparatorType(String value) {
        _value_ = value;
        _table_.put(_value_, this);
    }

    public static final String _LST_LF = "LST_LF";
    public static final String _LST_SEMICOLON = "LST_SEMICOLON";
    public static final String _LST_COMMA = "LST_COMMA";
    public static final String _LST_TAB = "LST_TAB";
    public static final String _LST_NO_SEPARATOR = "LST_NO_SEPARATOR";
    public static final LineSeparatorType LST_LF = new LineSeparatorType(_LST_LF);
    public static final LineSeparatorType LST_SEMICOLON = new LineSeparatorType(_LST_SEMICOLON);
    public static final LineSeparatorType LST_COMMA = new LineSeparatorType(_LST_COMMA);
    public static final LineSeparatorType LST_TAB = new LineSeparatorType(_LST_TAB);
    public static final LineSeparatorType LST_NO_SEPARATOR = new LineSeparatorType(_LST_NO_SEPARATOR);

    public String getValue() {
        return _value_;
    }

    public static LineSeparatorType fromValue(String value)
            throws IllegalArgumentException {
        LineSeparatorType enumeration = (LineSeparatorType)
                _table_.get(value);
        if (enumeration == null) throw new IllegalArgumentException();
        return enumeration;
    }

    public static LineSeparatorType fromString(String value)
            throws IllegalArgumentException {
        return fromValue(value);
    }

    public boolean equals(Object obj) {
        return (obj == this);
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public String toString() {
        return _value_;
    }

    public Object readResolve() throws java.io.ObjectStreamException {
        return fromValue(_value_);
    }

    public static org.apache.axis.encoding.Serializer getSerializer(
            String mechType,
            Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return
                new org.apache.axis.encoding.ser.EnumSerializer(
                        _javaType, _xmlType);
    }

    public static org.apache.axis.encoding.Deserializer getDeserializer(
            String mechType,
            Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return
                new org.apache.axis.encoding.ser.EnumDeserializer(
                        _javaType, _xmlType);
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
            new org.apache.axis.description.TypeDesc(LineSeparatorType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "LineSeparatorType"));
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
