package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmrStatistics;
import com.picsauditing.jpa.entities.OshaStatistics;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

import java.util.HashMap;

public class ContractorAuditTable extends AbstractTable {

	public static final String Contractor = "Contractor";
	public static final String Type = "Type";
	public static final String Auditor = "Auditor";
	public static final String ClosingAuditor = "ClosingAuditor";
	public static final String Data = "Data";
    public static final String SafetyManual = "SafetyManual";
    public static final String PreviousAudit = "PreviousAudit";

    public static final String GLEachOccurrence = "GLEachOccurrence";
    public static final String GLGeneralAggregate = "GLGeneralAggregate";
    public static final String ALCombinedSingle = "ALCombinedSingle";
    public static final String WCEachAccident = "WCEachAccident";
    public static final String EXEachOccurrence = "EXEachOccurrence";

    public static final String Fatalities = "Fatalities";
    public static final String Emr = "Emr";
    public static final String Lwcr = "Lwcr";
    public static final String Trir = "Trir";

    public static final HashMap<Integer,String> CEMEX_POST_EVAL_DATA;
    static
    {
        CEMEX_POST_EVAL_DATA = new HashMap<Integer, String>();
        CEMEX_POST_EVAL_DATA.put(17069,"Evaluator");
        CEMEX_POST_EVAL_DATA.put(17070,"Title");
        CEMEX_POST_EVAL_DATA.put(17071,"Site");
        CEMEX_POST_EVAL_DATA.put(17072,"Date");
        CEMEX_POST_EVAL_DATA.put(15440,"Readymix");
        CEMEX_POST_EVAL_DATA.put(15446,"ReadymixScotland");
        CEMEX_POST_EVAL_DATA.put(15447,"ReadymixNorthern");
        CEMEX_POST_EVAL_DATA.put(15448,"ReadymixCentral");
        CEMEX_POST_EVAL_DATA.put(15449,"ReadymixSouthern");
        CEMEX_POST_EVAL_DATA.put(16618,"ReadymixDSM");
        CEMEX_POST_EVAL_DATA.put(16619,"ReadymixAdmixtures");
        CEMEX_POST_EVAL_DATA.put(15454,"ReadymixScotlandCluster1");
        CEMEX_POST_EVAL_DATA.put(15450,"ReadymixNorthernCluster3");
        CEMEX_POST_EVAL_DATA.put(15451,"ReadymixNorthernCluster6");
        CEMEX_POST_EVAL_DATA.put(15452,"ReadymixNorthernCluster7");
        CEMEX_POST_EVAL_DATA.put(15453,"ReadymixNorthernCluster8");
        CEMEX_POST_EVAL_DATA.put(15455,"ReadymixCentralCluster12");
        CEMEX_POST_EVAL_DATA.put(15456,"ReadymixCentralCluster14");
        CEMEX_POST_EVAL_DATA.put(15457,"ReadymixCentralCluster15");
        CEMEX_POST_EVAL_DATA.put(15458,"ReadymixCentralCluster16");
        CEMEX_POST_EVAL_DATA.put(15459,"ReadymixSouthernCluster20");
        CEMEX_POST_EVAL_DATA.put(15460,"ReadymixSouthernCluster23");
        CEMEX_POST_EVAL_DATA.put(15461,"ReadymixSouthernCluster24");
        CEMEX_POST_EVAL_DATA.put(15438,"Cement");
        CEMEX_POST_EVAL_DATA.put(15441,"CementRugby");
        CEMEX_POST_EVAL_DATA.put(15442,"CementSouthFerriby");
        CEMEX_POST_EVAL_DATA.put(15443,"CementBarrington");
        CEMEX_POST_EVAL_DATA.put(15444,"CementTilbury");
        CEMEX_POST_EVAL_DATA.put(15445,"CementAsh");
        CEMEX_POST_EVAL_DATA.put(15439,"Aggregates");
        CEMEX_POST_EVAL_DATA.put(16606,"AggregatesScotland");
        CEMEX_POST_EVAL_DATA.put(16607,"AggregatesSE");
        CEMEX_POST_EVAL_DATA.put(16608,"AggregatesSW");
        CEMEX_POST_EVAL_DATA.put(16609,"AggregatesE");
        CEMEX_POST_EVAL_DATA.put(16610,"AggregatesMidlands");
        CEMEX_POST_EVAL_DATA.put(16611,"AggregatesNE");
        CEMEX_POST_EVAL_DATA.put(16612,"AggregatesNW");
        CEMEX_POST_EVAL_DATA.put(16613,"AggregatesDoveHoles");
        CEMEX_POST_EVAL_DATA.put(16614,"AggregatesMarine");
        CEMEX_POST_EVAL_DATA.put(16593,"Asphalt");
        CEMEX_POST_EVAL_DATA.put(16615,"AsphaltN");
        CEMEX_POST_EVAL_DATA.put(16616,"AsphaltS");
        CEMEX_POST_EVAL_DATA.put(16617,"AsphaltUrban");
        CEMEX_POST_EVAL_DATA.put(16594,"Logistics");
        CEMEX_POST_EVAL_DATA.put(16603,"LogisticsCement");
        CEMEX_POST_EVAL_DATA.put(16604,"LogisticsAggregates");
        CEMEX_POST_EVAL_DATA.put(16605,"LogisticsOther");
        CEMEX_POST_EVAL_DATA.put(16595,"Paving");
        CEMEX_POST_EVAL_DATA.put(16600,"PavingBirmingham");
        CEMEX_POST_EVAL_DATA.put(16601,"PavingSheffield");
        CEMEX_POST_EVAL_DATA.put(16602,"PavingWick");
        CEMEX_POST_EVAL_DATA.put(16596,"Building");
        CEMEX_POST_EVAL_DATA.put(16597,"BuildingConcrete");
        CEMEX_POST_EVAL_DATA.put(16598,"BuildingFloors");
        CEMEX_POST_EVAL_DATA.put(16599,"BuildingRail");
        CEMEX_POST_EVAL_DATA.put(14719,"Safety");
        CEMEX_POST_EVAL_DATA.put(14720,"Performance");
    }

	/**
	 * This is here ONLY for use when the audit type only has a single cao such
	 * as Welcome Calls, Manual Audits, Implementation Audits, and PQF Specfic.
	 * With any other audits, please use the ContractorAudit Model
	 */
	public static final String SingleCAO = "Cao";

	public ContractorAuditTable() {
		super("contractor_audit");
		addFields(ContractorAudit.class);
		Field id = addPrimaryKey();
		id.setCategory(FieldCategory.DocumentsAndAudits);

        Field creationDate = addCreationDate();
        creationDate.setCategory(FieldCategory.DocumentsAndAudits);
        creationDate.setImportance(FieldImportance.Low);

		Field auditTypeName;
		auditTypeName = new Field("TypeName", "auditTypeID", FieldType.AuditType);
		auditTypeName.setTranslationPrefixAndSuffix("AuditType", "name");
		auditTypeName.setUrl("Audit.action?auditID={" + ReportOnClause.ToAlias + "ID}");
		auditTypeName.setImportance(FieldImportance.Required);
		auditTypeName.setCategory(FieldCategory.DocumentsAndAudits);
		auditTypeName.setWidth(200);
		addField(auditTypeName);
	}

	public void addJoins() {
		addJoinKey(new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("conID"))).setMinimumImportance(FieldImportance.Average);

		addJoinKey(new ReportForeignKey(Type, new AuditTypeTable(), new ReportOnClause("auditTypeID")))
				.setMinimumImportance(FieldImportance.Average);
		
		{
			ReportForeignKey auditorKey = addOptionalKey(new ReportForeignKey(Auditor, new UserTable(),
					new ReportOnClause("auditorID")));
			auditorKey.setMinimumImportance(FieldImportance.Required);
			auditorKey.setCategory(FieldCategory.Auditors);
		}

		{
			ReportForeignKey auditorKey = addOptionalKey(new ReportForeignKey(ClosingAuditor, new UserTable(),
					new ReportOnClause("closingAuditorID")));
			auditorKey.setMinimumImportance(FieldImportance.Required);
			auditorKey.setCategory(FieldCategory.Auditors);
		}

		ReportForeignKey caoKey = addRequiredKey(new ReportForeignKey(SingleCAO, new ContractorAuditOperatorTable(),
				new ReportOnClause("id", "auditID")));
		caoKey.setCategory(FieldCategory.DocumentsAndAudits);
		caoKey.setMinimumImportance(FieldImportance.Required);

		ReportForeignKey data = addOptionalKey(new ReportForeignKey(Data, new AuditDataTable(),
				new ReportOnClause("id", "auditID")));
		data.setCategory(FieldCategory.DocumentsAndAudits);
		data.setMinimumImportance(FieldImportance.Required);

        ReportForeignKey safetyManual = addOptionalKey(new ReportForeignKey(SafetyManual, new AuditDataTable(),
                new ReportOnClause("id", "auditID", ReportOnClause.ToAlias + ".questionID = " + AuditQuestion.MANUAL_PQF)));
        safetyManual.setCategory(FieldCategory.DocumentsAndAudits);
        safetyManual.setMinimumImportance(FieldImportance.Average);

        ReportForeignKey previousAudit = addOptionalKey(new ReportForeignKey(PreviousAudit, new ContractorAuditTable(),
                new ReportOnClause("previousAuditID", "id")));
        previousAudit.setCategory(FieldCategory.DocumentsAndAudits);
        previousAudit.setMinimumImportance(FieldImportance.Required);

        // Insurance
        {
            // General Liability
            ReportForeignKey glEachOccurrence = addOptionalKey(new ReportForeignKey(GLEachOccurrence, new AuditDataTable(),
                    new ReportOnClause("id", "auditID", ReportOnClause.ToAlias + ".questionID = 2074")));
            glEachOccurrence.setCategory(FieldCategory.DocumentsAndAudits);
            glEachOccurrence.setMinimumImportance(FieldImportance.Required);

            ReportForeignKey glGeneralAggregate = addOptionalKey(new ReportForeignKey(GLGeneralAggregate, new AuditDataTable(),
                    new ReportOnClause("id", "auditID", ReportOnClause.ToAlias + ".questionID = 2079")));
            glGeneralAggregate.setCategory(FieldCategory.DocumentsAndAudits);
            glGeneralAggregate.setMinimumImportance(FieldImportance.Required);

            // Auto Liability
            ReportForeignKey alCombinedSingle = addOptionalKey(new ReportForeignKey(ALCombinedSingle, new AuditDataTable(),
                    new ReportOnClause("id", "auditID", ReportOnClause.ToAlias + ".questionID = 2155")));
            alCombinedSingle.setCategory(FieldCategory.DocumentsAndAudits);
            alCombinedSingle.setMinimumImportance(FieldImportance.Required);

            // Workers' Comp
            ReportForeignKey wcEachAccident = addOptionalKey(new ReportForeignKey(WCEachAccident, new AuditDataTable(),
                    new ReportOnClause("id", "auditID", ReportOnClause.ToAlias + ".questionID = 2149")));
            wcEachAccident.setCategory(FieldCategory.DocumentsAndAudits);
            wcEachAccident.setMinimumImportance(FieldImportance.Required);

            // Excess/Umbrella Liability
            ReportForeignKey exEachOccurrence = addOptionalKey(new ReportForeignKey(EXEachOccurrence, new AuditDataTable(),
                    new ReportOnClause("id", "auditID", ReportOnClause.ToAlias + ".questionID = 2161")));
            exEachOccurrence.setCategory(FieldCategory.DocumentsAndAudits);
            exEachOccurrence.setMinimumImportance(FieldImportance.Required);
        }

        // Annual Updates
        {
            ReportForeignKey fatalities = addOptionalKey(new ReportForeignKey(Fatalities, new AuditDataTable(),
                    new ReportOnClause("id", "auditID", ReportOnClause.ToAlias + ".questionID = " + OshaStatistics.QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR)));
            fatalities.setCategory(FieldCategory.DocumentsAndAudits);
            fatalities.setMinimumImportance(FieldImportance.Required);

            ReportForeignKey emr = addOptionalKey(new ReportForeignKey(Emr, new AuditDataTable(),
                    new ReportOnClause("id", "auditID", ReportOnClause.ToAlias + ".questionID = " + EmrStatistics.QUESTION_ID_EMR_FOR_THE_GIVEN_YEAR)));
            emr.setCategory(FieldCategory.DocumentsAndAudits);
            emr.setMinimumImportance(FieldImportance.Required);

            ReportForeignKey lwcr = addOptionalKey(new ReportForeignKey(Lwcr, new AuditDataTable(),
                    new ReportOnClause("id", "auditID", ReportOnClause.ToAlias + ".questionID = " + OshaStatistics.QUESTION_ID_LWCR_FOR_THE_GIVEN_YEAR)));
            lwcr.setCategory(FieldCategory.DocumentsAndAudits);
            lwcr.setMinimumImportance(FieldImportance.Required);

            ReportForeignKey trir = addOptionalKey(new ReportForeignKey(Trir, new AuditDataTable(),
                    new ReportOnClause("id", "auditID", ReportOnClause.ToAlias + ".questionID = " + OshaStatistics.QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR)));
            trir.setCategory(FieldCategory.DocumentsAndAudits);
            trir.setMinimumImportance(FieldImportance.Required);
        }

        // CEMEX
        {
            for (Integer questionID : CEMEX_POST_EVAL_DATA.keySet()) {
                ReportForeignKey cemexData = addOptionalKey(new ReportForeignKey(CEMEX_POST_EVAL_DATA.get(questionID), new AuditDataTable(),
                        new ReportOnClause("id", "auditID", ReportOnClause.ToAlias + ".questionID = " + questionID)));
                cemexData.setMinimumImportance(FieldImportance.Required);
            }
        }
    }
}