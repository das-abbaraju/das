package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaRateType;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.Strings;
import com.picsauditing.util.YearList;

/**
 * This class is used for display safety statistics on the contractor dashboard.
 * 
 * TODO: Turn this class into a bean and make it stateless.
 */
public class OshaDisplay {

	private OshaOrganizer oshaOrganizer;
	private ContractorAccount contractor;
	private Locale locale;
	private List<ContractorOperator> contractorOperators;
	private I18nCache i18nCache;

	@Autowired
	private NaicsDAO naicsDao;

	private static final MultiYearScope[] YEAR_SCOPES = {
			MultiYearScope.ThreeYearsAgo, MultiYearScope.TwoYearsAgo,
			MultiYearScope.LastYearOnly, MultiYearScope.ThreeYearAverage };

	public OshaDisplay(OshaOrganizer oshaOrganizer, Locale locale,
			List<ContractorOperator> contractorOperators,
			ContractorAccount contractor, NaicsDAO naicsDao) {
		this.oshaOrganizer = oshaOrganizer;
		this.locale = locale;
		this.contractorOperators = contractorOperators;
		this.contractor = contractor;
		this.naicsDao = naicsDao;
		this.i18nCache = I18nCache.getInstance();
	}

	@SuppressWarnings("unchecked")
	private List getColumnNames(OshaType oshaType) {
		List columnNames = new ArrayList();
		YearList yearList = oshaOrganizer.mostRecentThreeYears(oshaType);
		StringBuilder yearsForAverageLabel = new StringBuilder();
		for (MultiYearScope yearScope : YEAR_SCOPES) {
			Integer year = yearList.getYearForScope(yearScope);
			if (year != null) {
				columnNames.add(year);
				yearsForAverageLabel.append(", ");
				yearsForAverageLabel.append(year);
			}
		}
		yearsForAverageLabel.delete(0, 1);
		columnNames.add(yearsForAverageLabel);
		columnNames.add(i18nCache.getText(
				"ContractorView.ContractorDashboard.Industry", locale));

		return columnNames;
	}

	@SuppressWarnings("unchecked")
	private Map getInfoForParticularOshaType(OshaType oshaType) {
		Map info = new HashMap();
		info.put("columnNames", getColumnNames(oshaType));
		info.put("data", getData(oshaType));

		return info;
	}

	@SuppressWarnings("unchecked")
	private List getData(OshaType oshaType) {
		List rows = new ArrayList();
		for (OshaRateType rateType : oshaType.rates) {
			StatisticsDisplayRow rateRow = new StatisticsDisplayRow(false);

			rateRow.addCell(i18nCache.getText(rateType.getI18nKey(), locale));

			for (MultiYearScope scope : YEAR_SCOPES) {
				Double answer = oshaOrganizer.getRate(oshaType, scope, rateType);
				if (answer != null && answer >= 0) {
					rateRow.addCell(answer.toString());
				}
			}

			if (rateType.isHasIndustryAverage()) {
				Float industryAverage = getIndustryAverage(naicsDao
						.find(contractor.getNaics().getCode()), rateType);
				rateRow.addCell(industryAverage.toString());
			} else {
				rateRow.addCell("");
			}

			rows.add(rateRow);
			if (rateType != OshaRateType.Fatalities) {
				rows.addAll(generateHurdleRates(rateType, oshaType));
			}
		}

		return rows;
	}

	private Float getIndustryAverage(Naics naics, OshaRateType rateType) {
		if (rateType == OshaRateType.LwcrAbsolute)
			return naics.getLwcr();
		else if (rateType == OshaRateType.TrirAbsolute)
			return naics.getTrir();
		return null;
	}

	private List<StatisticsDisplayRow> generateHurdleRates(
			OshaRateType oshaRateType, OshaType oshaType) {
		List<StatisticsDisplayRow> hurdleRateRows = new ArrayList<StatisticsDisplayRow>();

		Set<OperatorAccount> inheritedOperators = new LinkedHashSet<OperatorAccount>();
		for (ContractorOperator co : contractorOperators) {
			inheritedOperators.add(co.getOperatorAccount()
					.getInheritFlagCriteria());
		}

		for (OperatorAccount o : inheritedOperators) {
			if (oshaType == o.getOshaType()) {
				Map<MultiYearScope, Set<FlagCriteriaOperator>> flagCriteriaForYear = generateOperatorFlagCriteriaMap(
						oshaRateType, oshaType, o);
				StatisticsDisplayRow hurdleRow = new StatisticsDisplayRow(true);
				hurdleRow.addCell(o.getName());

				boolean hasFlagCriteria = false;

				for (MultiYearScope scope : YEAR_SCOPES) {
					Set<FlagCriteriaOperator> flagCriteriaForThisYear = flagCriteriaForYear
							.get(scope);
					if (flagCriteriaForThisYear == null
							|| flagCriteriaForThisYear.size() == 0) {
						hurdleRow.addCell("");
					} else {
						StringBuilder display = new StringBuilder();
						for (FlagCriteriaOperator fco : flagCriteriaForThisYear) {
							display.append(", ");
							display.append(getFlagDescription(fco));
						}
						display.delete(0, 1);
						hurdleRow.addCell(display.toString());

						hasFlagCriteria = true;
					}
				}
				if (hasFlagCriteria) {
					hurdleRateRows.add(hurdleRow);
				}
			}
		}

		return hurdleRateRows;
	}

	private Map<MultiYearScope, Set<FlagCriteriaOperator>> generateOperatorFlagCriteriaMap(
			OshaRateType oshaRateType, OshaType oshaType, OperatorAccount o) {
		Map<MultiYearScope, Set<FlagCriteriaOperator>> flagCriteriaForYear = new HashMap<MultiYearScope, Set<FlagCriteriaOperator>>();
		for (FlagCriteriaOperator fco : o.getFlagCriteriaInherited()) {
			if (fco.getCriteria().getOshaType() == oshaType
					&& fco.getCriteria().getOshaRateType() == oshaRateType) {
				MultiYearScope scope = fco.getCriteria().getMultiYearScope();
				Set<FlagCriteriaOperator> flagCriteria = flagCriteriaForYear
						.get(scope);
				if (flagCriteria == null) {
					flagCriteria = new HashSet<FlagCriteriaOperator>();
					flagCriteriaForYear.put(scope, flagCriteria);
				}
				flagCriteria.add(fco);
			}
		}

		return flagCriteriaForYear;
	}

	private String getFlagDescription(FlagCriteriaOperator fco) {
		if (OshaRateType.TrirWIA.equals(fco.getCriteria().getOshaRateType())) {
			float hurdle = (fco.getHurdle() != null) ? Float.valueOf(fco
					.getHurdle()) : 100;
			return "<nobr class=\""
					+ fco.getFlag()
					+ "\">"
					+ fco.getCriteria().getComparison()
					+ " "
					+ Strings.formatDecimalComma(Double.toString(hurdle / 100
							* contractor.getWeightedIndustryAverage()))
					+ "</nobr>";
		} else if (OshaRateType.TrirNaics.equals(fco.getCriteria()
				.getOshaRateType())) {
			float hurdle = (fco.getHurdle() != null) ? Float.valueOf(fco
					.getHurdle()) : 100;
			return "<nobr class=\""
					+ fco.getFlag()
					+ "\">"
					+ fco.getCriteria().getComparison()
					+ " "
					+ Strings.formatDecimalComma(Double.toString(hurdle
							/ 100
							* naicsDao.getIndustryAverage(false, contractor
									.getNaics()))) + "</nobr>";
		} else if (OshaRateType.DartNaics.equals(fco.getCriteria()
				.getOshaRateType())) {
			float hurdle = (fco.getHurdle() != null) ? Float.valueOf(fco
					.getHurdle()) : 100;
			return "<nobr class=\""
					+ fco.getFlag()
					+ "\">"
					+ fco.getCriteria().getComparison()
					+ " "
					+ Strings.formatDecimalComma(Double.toString(hurdle
							/ 100
							* naicsDao.getDartIndustryAverage(contractor
									.getNaics()))) + "</nobr>";
		} else
			return "<nobr class=\"" + fco.getFlag() + "\">"
					+ fco.getShortDescription() + "</nobr>";
	}

	@SuppressWarnings("unchecked")
	public Map getStats() {
		Map stats = new HashMap();
		for (OshaType oshaType : OshaType.values()) {
			try {
				oshaOrganizer.hasOshaType(oshaType);
				if (oshaOrganizer.hasOshaType(oshaType)) {
					stats.put(oshaType, getInfoForParticularOshaType(oshaType));
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return stats;
	}
}
