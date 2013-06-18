package com.picsauditing.report.data

import com.picsauditing.jpa.entities.Column
import com.picsauditing.report.fields.Field
import com.picsauditing.report.fields.FieldType
import com.picsauditing.report.fields.SqlFunction

class PivotTestData {

    static List<Column> getColumnsForFlagChart() {
        [
                newColumn("ContractorCount", FieldType.Integer, SqlFunction.Count),
                newColumn("FlagColor", FieldType.FlagColor),
                newColumn("ClientSite")
        ]
    }

    static List<List<Serializable>> getDataForFlagChart() {
        [
                [12, "Red", "Suncor"],
                [6, "Amber", "Suncor"],
                [18, "Green", "Suncor"],
                [4, "Red", "BASF"],
                [1, "Amber", "BASF"],
                [12, "Green", "Tesoro"]
        ]
    }

    static Column newColumn(String fieldName) {
        newColumn(fieldName, FieldType.String)
    }

    static Column newColumn(String fieldName, FieldType type) {
        newColumn(fieldName, type, null)
    }

    static Column newColumn(String fieldName, FieldType type, SqlFunction function) {
        Column column = new Column(fieldName)
        column.setField(new Field(fieldName, fieldName, type))
        column.setSqlFunction(function)
        return column
    }
}