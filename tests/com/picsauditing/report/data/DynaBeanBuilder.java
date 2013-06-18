package com.picsauditing.report.data;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.report.DynaBeanListBuilder;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ContractorsModel;
import org.apache.commons.beanutils.BasicDynaBean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DynaBeanBuilder {
    AbstractModel model;
    List<Column> columns = new ArrayList<>();
    Map<String, Field> availableFields;

    public DynaBeanBuilder(Permissions permissions) {
        model = new ContractorsModel(permissions);
        availableFields = model.getAvailableFields();
    }

    public Column addColumn(String fieldName) {
        Column column = new Column(fieldName);
        columns.add(column);
        column.setField(availableFields.get(fieldName.toUpperCase()));
        return column;
    }

    static List<Column> makeColumns(Permissions permissions) {
        DynaBeanBuilder builder = new DynaBeanBuilder(permissions);
        builder.addColumn("AccountID");
        builder.addColumn("AccountName");
        builder.addColumn("AccountCreationDate");
        builder.addColumn("ContractorMembershipDate");

        Column membershipMonth = builder.addColumn("ContractorMembershipDate");
        membershipMonth.setName("ContractorMembershipDate__Month");
        membershipMonth.setSqlFunction(SqlFunction.Month);
        membershipMonth.getField().setUrl("Test.action?id={AccountZip}");

        builder.addColumn("ContractorLastUpgradeDate");

        return builder.columns;
    }

    static List<BasicDynaBean> createAccountQueryList(int count) {
        DynaBeanListBuilder builder = new DynaBeanListBuilder("account");
        builder.addProperty("AccountID", Long.class);
        builder.addProperty("AccountName", String.class);
        builder.addProperty("AccountCreationDate", Timestamp.class);
        builder.addProperty("ContractorMembershipDate", java.sql.Date.class);
        builder.addProperty("ContractorMembershipDate__Month", Integer.class);
        builder.addProperty("ContractorLastUpgradeDate", java.sql.Date.class);
        builder.addProperty("AccountZip", String.class);

        for (int i = 0; i < count; i++) {
            builder.addRow();
            long accountID = 1;
            long currentUnitTime = 1234567890;
            if (count > 1) {
                accountID = Math.round(Math.random() * 1000);
                currentUnitTime = new Date().getTime();
            }

            builder.setValue("AccountID", accountID);
            builder.setValue("AccountName", "Test " + accountID);
            builder.setValue("AccountCreationDate", new Timestamp(currentUnitTime));
            builder.setValue("ContractorMembershipDate", new java.sql.Date(currentUnitTime));
            builder.setValue("ContractorMembershipDate__Month", 1);
            builder.setValue("ContractorLastUpgradeDate", null);
            builder.setValue("AccountZip", "92614");
        }

        return builder.getRows();
    }

    static Column createColumn(Field field) {
        Column column = new Column(field.getName());
        column.setField(field);
        return column;
    }

}
