package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public enum CommissionType {
	Basic {
		@Override
		public void initialize() {
			this.commissionPerOperatorMailing = new BigDecimal(600).setScale(2, BigDecimal.ROUND_DOWN);

			this.registrationCommissionTiers = new ArrayList<RegistrationCommissionTier>();
			this.registrationCommissionTiers.add(new RegistrationCommissionTier(0, 800, new BigDecimal(50).setScale(2,
					BigDecimal.ROUND_DOWN)));
			this.registrationCommissionTiers.add(new RegistrationCommissionTier(801, 1000000, new BigDecimal(76)
					.setScale(2, BigDecimal.ROUND_DOWN)));
		}
	},
	Senior {
		@Override
		public void initialize() {
			this.commissionPerOperatorMailing = new BigDecimal(800).setScale(2, BigDecimal.ROUND_DOWN);

			this.registrationCommissionTiers = new ArrayList<RegistrationCommissionTier>();
			this.registrationCommissionTiers.add(new RegistrationCommissionTier(0, 800, new BigDecimal(75).setScale(2,
					BigDecimal.ROUND_DOWN)));
			this.registrationCommissionTiers.add(new RegistrationCommissionTier(801, 1000000, new BigDecimal(105)
					.setScale(2, BigDecimal.ROUND_DOWN)));
		}
	};

	protected BigDecimal commissionPerOperatorMailing;
	protected List<RegistrationCommissionTier> registrationCommissionTiers;

	CommissionType() {
		initialize();
	}

	public abstract void initialize();
}