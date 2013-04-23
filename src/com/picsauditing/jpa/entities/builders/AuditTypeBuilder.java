package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;

public class AuditTypeBuilder {
    private AuditType type = new AuditType();

    public AuditTypeBuilder id(int id) {
        type.setId(id);
        return this;
    }

    public AuditTypeBuilder auditClass(AuditTypeClass clazz) {
        type.setClassType(clazz);
        return this;
    }

    public AuditType build() {
        return type;
    }
}
