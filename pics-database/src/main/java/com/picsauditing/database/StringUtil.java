package com.picsauditing.database;

import com.picsauditing.database.domain.RowsIdentifiableByKey;
import com.picsauditing.util.Strings;

import java.util.Collection;

public class StringUtil {

    public static String implodeIDs(Collection<? extends RowsIdentifiableByKey> collection) {
        if (collection == null)
            return Strings.EMPTY_STRING;

        StringBuffer buffer = new StringBuffer();
        for (RowsIdentifiableByKey o : collection) {
            if (buffer.length() > 0) {
                buffer.append(",");
            }

            buffer.append(o.getId());
        }

        return buffer.toString();
    }

}
