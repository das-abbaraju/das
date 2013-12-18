package com.picsauditing.employeeguard.validators.duplicate;

import com.picsauditing.employeeguard.entities.duplicate.UniqueIndexable;

public interface DuplicateInfoProvider {

    UniqueIndexable getUniqueIndexable();

    Class<?> getType();

}
