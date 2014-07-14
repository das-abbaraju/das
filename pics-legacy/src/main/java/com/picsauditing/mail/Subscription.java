package com.picsauditing.mail;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.Translatable;
import com.picsauditing.jpa.entities.User;

public enum Subscription implements Translatable {
	ContractorRegistration {
		public void initialize() {
			setTemplateID(62);
			setRequiredForOperator(true);
		}
	},
	ContractorDeactivation {
		public void initialize() {
			setTemplateID(51);
			setSupportedTimePeriods(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
					SubscriptionTimePeriod.Event });
			setDefaultTimePeriod(SubscriptionTimePeriod.Event);
			setRequiredForOperator(true);
		}
	},
	ContractorAdded {
		public void initialize() {
            setTemplateID(DYNAMIC_REPORTS_SUBSCRIPTION_TEMPLATE_ID);
			setRequiredForOperator(true);
		}
	},
	ContractorFinished {
		public void initialize() {
			setTemplateID(63);
			setSupportedTimePeriods(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
					SubscriptionTimePeriod.Event });
			setDefaultTimePeriod(SubscriptionTimePeriod.Event);
			setRequiredForOperator(true);
		}
	},
	ForcedFlags {
		public void initialize() {
			setTemplateID(165);
			setRequiredForOperator(true);
		}
	},
	FlagChanges {
		public void initialize() {
			setTemplateID(60);
			setRequiredForOperator(true);
		}
	},
	RedFlags {
		public void initialize() {
			setTemplateID(65);
			setRequiredForOperator(true);
		}
	},
	AmberFlags {
		public void initialize() {
			setTemplateID(65);
			setRequiredForOperator(true);
		}

	},
	GreenFlags {
		public void initialize() {
			setTemplateID(65);
			setRequiredForOperator(true);
		}
	},
	PendingInsuranceCerts {
		public void initialize() {
			setTemplateID(61);
			setRequiredPerms(OpPerms.InsuranceCerts);
			setRequiredForOperator(true);
		}
	},
	VerifiedInsuranceCerts {
		public void initialize() {
			setTemplateID(61);
			setRequiredPerms(OpPerms.InsuranceApproval);
			setRequiredForOperator(true);
		}
	},
	TrialContractorAccounts {
		public void initialize() {
			setTemplateID(71);
			setRequiredPerms(OpPerms.ViewTrialAccounts);
			setRequiredForOperator(true);
		}
	},
	ContractorInvoices {
		public void initialize() {
			setSupportedTimePeriods(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
					SubscriptionTimePeriod.Event });
			setDefaultTimePeriod(SubscriptionTimePeriod.Event);
			setRequiredForContractor(true);
		}
	},
	InsuranceExpiration {
		public void initialize() {
			setTemplateID(10);
			setSupportedTimePeriods(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
					SubscriptionTimePeriod.Event });
			setDefaultTimePeriod(SubscriptionTimePeriod.Event);
			setRequiredPerms(OpPerms.ContractorInsurance);
			setRequiredForContractor(true);
		}
	},
	AuditOpenRequirements {
		public void initialize() {
			setSupportedTimePeriods(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
					SubscriptionTimePeriod.Event });
			setDefaultTimePeriod(SubscriptionTimePeriod.Event);
			setRequiredForContractor(true);
		}
	},
	FinishPICSProcess {
		public void initialize() {
			setSupportedTimePeriods(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
					SubscriptionTimePeriod.Event });
			setDefaultTimePeriod(SubscriptionTimePeriod.Event);
			setRequiredForContractor(true);
		}
	},
	PICSSystemNotifications {
		public void initialize() {
			setSupportedTimePeriods(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
					SubscriptionTimePeriod.Event });
			setDefaultTimePeriod(SubscriptionTimePeriod.Event);
			setRequiredForOperator(true);
		}
	},
	OQChanges {
		public void initialize() {
			setTemplateID(130);
			setSupportedTimePeriods(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
					SubscriptionTimePeriod.Monthly });
			setRequiredPerms(OpPerms.ViewTrialAccounts);
			setRequiresOQ(true);
		}
	},
	Webinar {
		public void initialize() {
			setSupportedTimePeriods(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
					SubscriptionTimePeriod.Event });
			setDefaultTimePeriod(SubscriptionTimePeriod.Event);
			setRequiredForContractor(true);
		}
	},
	// Please use nightly_updates.sql for controlling opt-out subscription
	// inserts
	OpenTasks {
		public void initialize() {
			setTemplateID(168);
			setSupportedTimePeriods(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
					SubscriptionTimePeriod.Monthly });
			setRequiredForContractor(true);
			setSubjectViewableBy(Account.EVERYONE);
			setBodyViewableBy(Account.PICS_ID);
		}
	},
	RegistrationRequests {
		public void initialize() {
			setSupportedTimePeriods(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
					SubscriptionTimePeriod.Event });
			setDefaultTimePeriod(SubscriptionTimePeriod.Event);
			setRequiredForOperator(true);
		}
	},
	EmailCronFailure {
		public void initialize() {
			setTemplateID(181);
			setSupportedTimePeriods(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
					SubscriptionTimePeriod.Event });
			setDefaultTimePeriod(SubscriptionTimePeriod.Event);
			setRequiredForAdmin(true);
		}
	},
	CancelledScheduledAudits {
		public void initialize() {
			setTemplateID(220);
			setSupportedTimePeriods(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
					SubscriptionTimePeriod.Daily });
			setDefaultTimePeriod(SubscriptionTimePeriod.Daily);
			setRequiredForAdmin(true);
		}
	},
    DynamicReports {
        public void initialize() {
            setTemplateID(DYNAMIC_REPORTS_SUBSCRIPTION_TEMPLATE_ID);
            setDefaultTimePeriod(SubscriptionTimePeriod.None);
            setRequiredForAdmin(true);
            setRequiredForOperator(true);
            setRequiredForContractor(true);
        }
    },
	RejectedInsurance {
		public void initialize() {
			setTemplateID(248);
			setSupportedTimePeriods(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
					SubscriptionTimePeriod.Weekly });
			setDefaultTimePeriod(SubscriptionTimePeriod.Weekly);
			setRequiredForAdmin(true);
		}
		// TODO: Write up a PQF Submitted subscription, find out what that was
		// for.
		// },
		// PQFSubmitted {
		// public void initialize() {
		// setTemplateID(220);
		// setSupportedTimePeriods(new SubscriptionTimePeriod[] {
		// SubscriptionTimePeriod.None,
		// SubscriptionTimePeriod.Daily });
		// setDefaultTimePeriod(SubscriptionTimePeriod.Daily);
		// setRequiredForAdmin(true);
		// }
	};

    private static final int DYNAMIC_REPORTS_SUBSCRIPTION_TEMPLATE_ID = 350;

    private int templateID;
	private String description;
	private String longDescription;
	private SubscriptionTimePeriod[] supportedTimePeriods = { SubscriptionTimePeriod.None,
			SubscriptionTimePeriod.Daily, SubscriptionTimePeriod.Weekly, SubscriptionTimePeriod.Monthly };
	private boolean requiredForOperator = false;
	private boolean requiredForContractor = false;
	private boolean requiredForAdmin = false;
	private OpPerms requiredPerms = null;
	private boolean requiresOQ = false;
	private int subjectViewableBy = Account.PRIVATE;
	private int bodyViewableBy = Account.PRIVATE;
	private SubscriptionTimePeriod defaultTimePeriod = SubscriptionTimePeriod.Monthly;

	Subscription() {
		initialize();
	}

	/**
	 * This must be overridden by each of the Subscription types
	 */
	public abstract void initialize();

	public int getTemplateID() {
		return templateID;
	}

	public void setTemplateID(int templateID) {
		this.templateID = templateID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public SubscriptionTimePeriod[] getSupportedTimePeriods() {
		return supportedTimePeriods;
	}

	public void setSupportedTimePeriods(SubscriptionTimePeriod[] supportedTimePeriods) {
		this.supportedTimePeriods = supportedTimePeriods;
	}

	public boolean isRequiredForOperator() {
		return requiredForOperator;
	}

	public void setRequiredForOperator(boolean requiredForOperator) {
		this.requiredForOperator = requiredForOperator;
	}

	public boolean isRequiredForContractor() {
		return requiredForContractor;
	}

	public void setRequiredForContractor(boolean requiredForContractor) {
		this.requiredForContractor = requiredForContractor;
	}

	public OpPerms getRequiredPerms() {
		return requiredPerms;
	}

	public void setRequiredPerms(OpPerms requiredPerms) {
		this.requiredPerms = requiredPerms;
	}

	public void setRequiresOQ(boolean requiresOQ) {
		this.requiresOQ = requiresOQ;
	}

	public String getAppPropertyKey() {
		return "subscription." + this.toString();
	}

	public boolean isRequiredForAdmin() {
		return requiredForAdmin;
	}

	public void setRequiredForAdmin(boolean requiredForAdmin) {
		this.requiredForAdmin = requiredForAdmin;
	}

	public boolean isRequiresOQ() {
		return requiresOQ;
	}

	public void setSubjectViewableBy(int subjectViewableBy) {
		this.subjectViewableBy = subjectViewableBy;
	}

	public int getSubjectViewableBy() {
		return subjectViewableBy;
	}

	public void setBodyViewableBy(int bodyViewableBy) {
		this.bodyViewableBy = bodyViewableBy;
	}

	public int getBodyViewableBy() {
		return bodyViewableBy;
	}

	public void setDefaultTimePeriod(SubscriptionTimePeriod defaultTimePeriod) {
		this.defaultTimePeriod = defaultTimePeriod;
	}

	public SubscriptionTimePeriod getDefaultTimePeriod() {
		return defaultTimePeriod;
	}

	public EmailSubscription createEmailSubscription(User user) {
		EmailSubscription emailSubscription = new EmailSubscription();
		emailSubscription.setAuditColumns();
		emailSubscription.setSubscription(this);
		emailSubscription.setTimePeriod(getDefaultTimePeriod());
		emailSubscription.setUser(user);

		return emailSubscription;
	}

    @Override
	public String getI18nKey() {
		return (!getClass().getSimpleName().isEmpty() ? getClass().getSimpleName() : getClass().getSuperclass()
				.getSimpleName()) + "." + toString();
	}

	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
