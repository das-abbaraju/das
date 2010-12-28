package com.picsauditing.jpa.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("D")
public class AuditCategoryMatrixDesktop extends AuditCategoryMatrix {

}
