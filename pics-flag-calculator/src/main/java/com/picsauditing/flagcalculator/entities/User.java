package com.picsauditing.flagcalculator.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.User")
@Table(name = "users")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class User extends BaseTable implements java.io.Serializable {
    public static int SYSTEM = 1;
<<<<<<< HEAD
//    public static int GROUP_ADMIN = 10;
//    public static int GROUP_AUDITOR = 11;
//    public static int GROUP_CSR = 959;
//    public static int GROUP_INSIDE_SALES = 71638;
//    public static int GROUP_MANAGER = 981;
//    public static int GROUP_MARKETING = 10801;
//    public static int GROUP_SALES_REPS = 96297;
//    public static int GROUP_DEVELOPER = 33885;
//    public static int GROUP_GC_FREE = 61460;
//    public static int GROUP_GC_FULL = 61461;
//    public static int GROUP_STAKEHOLDER = 64680;
//    public static int GROUP_BETATESTER = 64681;
//    public static int GROUP_SAFETY = 65744;
//    public static int CONTRACTOR = 12;
//    protected boolean needsIndexing = true;
//    private static final int GROUP_SU = 9; // Group that automatically has ALL
//    public static final int GROUP_ISR = 71638;
//    public static final int SELENIUM_MASTER_USER = 94545;
//    // permissions
//    public static int INDEPENDENT_CONTRACTOR = 11265;
//
//    // grant privileges
//
//    private AppUser appUser = new AppUser();
//    private YesNo isGroup;
//    private String email;
//    // TODO - read GMail to see if emails are bouncing and auto update this
//    // field
//    private Date emailConfirmedDate;
//    private String name;
//    private String firstName;
//    private String lastName;
//    private YesNo isActive = YesNo.Yes;
//    private Date lastLogin;
//    private Account account;
//    private String phone;
//    private String phoneIndex;
//    private String fax;
//
//    private Date passwordChanged = new Date();
//    private String resetHash;
//    private boolean forcePasswordReset;
//    private int failedAttempts = 0;
//    private Date lockUntil = null;
//    private TimeZone timezone = null;
//    private Locale locale = Locale.ENGLISH;
//    private String department;
//    private String apiKey;
//    private boolean usingDynamicReports = true;
//    private Date usingDynamicReportsDate = new Date();
//    private boolean usingVersion7Menus = true;
//    private Date usingVersion7MenusDate = new Date();
//    private int assignmentCapacity;
//    private Date reportsManagerTutorialDate;
//
//    private List<UserGroup> groups = new ArrayList<UserGroup>();
//    private List<UserGroup> members = new ArrayList<UserGroup>();
//    private List<UserAccess> ownedPermissions = new ArrayList<UserAccess>();
//    private List<UserSwitch> switchTos = new ArrayList<UserSwitch>();
//    private List<UserSwitch> switchFroms = new ArrayList<UserSwitch>();
//    private List<EmailSubscription> subscriptions = new ArrayList<EmailSubscription>();
//    private List<ContractorWatch> watchedContractors = new ArrayList<ContractorWatch>();
//    private List<Report> reports = new ArrayList<Report>();
//    private List<Locale> spokenLanguages = new ArrayList<Locale>();
//    private List<String> countriesServiced = new ArrayList<String>();
//
//    // Specifically for testing, DO NOT @Autowired
//    private InputValidator inputValidator;
//
//    // This is specifically used for testing, do not auto-wire
//    private FeatureToggle featureToggle;
//
//    @Transient
//    public boolean isSuperUser() {
//        return (id == GROUP_SU);
//    }
//
    public User() {
    }
//
//    public User(String name) {
//        this.name = name;
//    }
//
=======

>>>>>>> 7ae760b... US831 Deprecated old FDC
    public User(int id) {
        this.id = id;
    }
}