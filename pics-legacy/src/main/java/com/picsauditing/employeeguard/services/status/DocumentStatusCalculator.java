package com.picsauditing.employeeguard.services.status;

import com.picsauditing.employeeguard.entities.IntervalType;
import com.picsauditing.employeeguard.util.DateUtil;

import java.util.Date;

public class DocumentStatusCalculator {
	public DocumentStatus calculate(final Date expirationDate) {
		if (expirationDate != null) {
			Date now = new Date();
			if (now.after(expirationDate)) {
				return DocumentStatus.Expired;
			}

			if (DateUtil.isAboutToExpire(expirationDate, 30, IntervalType.DAY)) {
				return DocumentStatus.Expiring;
			}

			return DocumentStatus.Complete;
		}

		return DocumentStatus.Expired;
	}
}