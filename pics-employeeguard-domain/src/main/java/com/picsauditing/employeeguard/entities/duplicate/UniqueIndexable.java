package com.picsauditing.employeeguard.entities.duplicate;

import java.util.Map;

public interface UniqueIndexable {

    Map<String, Object> getUniqueIndexableValues();

    int getId();

}
