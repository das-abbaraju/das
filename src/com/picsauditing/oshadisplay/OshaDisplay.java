package com.picsauditing.oshadisplay;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.PICS.OshaOrganizer;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.Numbers;
import com.picsauditing.util.Strings;
import com.picsauditing.util.YearList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * This class is used for displaying safety statistics on the contractor
 * dashboard.
 * 
 * TODO: Turn this class into a bean and make it stateless.
 */
public class OshaDisplay {
	private static final Logger logger = LoggerFactory.getLogger(OshaDisplay.class);

	private static final String EMPTY_CELL = "";

	private OshaOrganizer oshaOrganizer;
	private ContractorAccount contractor;
	private Locale locale;
	private List<ContractorOperator> contractorOperators;
	private I18nCache i18nCache;
	private List<String> columnNames;
	private YearList yearList;

	@Autowired
	private NaicsDAO naicsDao;

	private static final MultiYearScope[] YEAR_SCOPES = { MultiYearScope.ThreeYearsAgo, MultiYearScope.TwoYearsAgo,
			MultiYearScope.LastYearOnly, MultiYearScope.ThreeYearAverage };

	public OshaDisplay(OshaOrganizer oshaOrganizer, Locale locale, List<ContractorOperator> contractorOperators,
			ContractorAccount contractor, NaicsDAO naicsDao) {
		this.oshaOrganizer = oshaOrganizer;
		this.locale = locale;
		this.contractorOperators = contractorOperators;
		this.contractor = contractor;
		this.naicsDao = naicsDao;
		this.i18nCache = I18nCache.getInstance();
	}

	private List<String> getColumnNames(OshaType oshaType) {
		columnNames = new ArrayList<String>();
		
		if (oshaOrganizer.getOutOfScopeYear(oshaType) != null) {
			columnNames.add(oshaOrganizer.getOutOfScopeYear(oshaType).toString());
		}
		yearList = oshaOrganizer.mostRecentThreeYears(oshaType);
		StringBuilder yearsForAverageLabel = new StringBuilder();
		for (MultiYearScope yearScope : YEAR_SCOPES) {
			Integer year = yearList.getYearForScope(yearScope);
			if (year != null) {
				columnNames.add(year.toString());
				yearsForAverageLabel.append(", ");
				yearsForAverageLabel.append(year);
			}
		}
		yearsForAverageLabel.delete(0, 1);
		columnNames.add(i18nCache.getText("ContractorView.ContractorDashboard.AverageLabel", locale,
				yearsForAverageLabel.toString()));
		columnNames.add(i18nCache.getText("ContractorView.ContractorDashboard.Industry", locale));

		return columnNames;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map getInfoForParticularOshaType(OshaType oshaType) {
		Map info = new HashMap();
		info.put("columnNames", getColumnNames(oshaType));
		info.put("data", getData(oshaType));

		return info;
	}

	private List<OshaDisplayRow> getData(OshaType oshaType) {
		logger.info("Generating OSHA display for contractor ({}) {}",
				new Object[] { contractor.getId(), contractor.getName() });

		List<OshaDisplayRow> rows = new ArrayList<OshaDisplayRow>();
		for (OshaRateType rateType : oshaType.rates) {
			if (!isShowRow(rateType)) {
				continue;
			}

			StatisticsDisplayRow rateRow = new StatisticsDisplayRow();
			rateRow.setOshaRateType(rateType);
			
			if (oshaOrganizer.getOutOfScopeYear(oshaType) != null) {
				Double answer = oshaOrganizer.getRate(oshaType, oshaOrganizer.getOutOfScopeYear(oshaType), rateType);
				if (answer != null && answer >= 0) {
					rateRow.addCell(Numbers.printDouble(answer));
				} else {
					rateRow.addCell(EMPTY_CELL);
				}
			}

			for (MultiYearScope scope : YEAR_SCOPES) {
				Double answer = oshaOrganizer.getRate(oshaType, scope, rateType);
				if (yearList.getYearForScope(scope) != null || scope == MultiYearScope.ThreeYearAverage) {
					if (answer != null && answer >= 0) {
						rateRow.addCell(Numbers.printDouble(answer));
					} else {
						rateRow.addCell(EMPTY_CELL);
					}
				}
			}

			List<OshaDisplayRow> hurdleRateRows = new ArrayList<OshaDisplayRow>();
			if (rateType != OshaRateType.Fatalities) {
				hurdleRateRows = generateHurdleRates(rateType, oshaType);
			}

			if (rateType.isHasIndustryAverage() && hurdleRateRows.size() > 0) {
				String industryAverage = getIndustryAverage(oshaType, rateType);
				rateRow.addCell(industryAverage);
			} else {
				rateRow.addCell(EMPTY_CELL);
			}

			rows.add(rateRow);

			rows.addAll(hurdleRateRows);
		}

		return rows;
	}

	private boolean isShowRow(OshaRateType rateType) {
		if (!rateType.equals(OshaRateType.SeverityRate))
			return true;
		for (ContractorOperator conOp : contractorOperators) {
			if (conOp.getOperatorAccount().isOrIsDescendantOf(1436)) // Tesoro
				return true;
		}

		return false;
	}

	private String getIndustryAverage(OshaType oshaType, OshaRateType rateType) {
		if (rateType == OshaRateType.LwcrAbsolute) {
			return String.valueOf(Utilities.getIndustryAverage(true, contractor));
		} else if (rateType == OshaRateType.TrirAbsolute || rateType == OshaRateType.TrirNaics) {
			if (oshaType != OshaType.OSHA && oshaType != OshaType.MSHA) {
				return String.format("%.2g%n", contractor.getWeightedIndustryAverage()) + "*";
			}
			if (contractor.getNaics() == null || Strings.isEmpty(contractor.getNaics().getCode()))
				return String.format("%.2g%n", contractor.getWeightedIndustryAverage()) + "*";
			return String.valueOf(Utilities.getIndustryAverage(false, contractor));
		} else if (rateType == OshaRateType.TrirWIA) {
			return String.format("%.2g%n", contractor.getWeightedIndustryAverage()) + "*";
		} else if (rateType == OshaRateType.Dart) { 
			return String.valueOf(Utilities.getDartIndustryAverage(contractor.getNaics()));
		}
		return null;
	}

	private List<OshaDisplayRow> generateHurdleRates(OshaRateType oshaRateType, OshaType oshaType) {
		List<OshaDisplayRow> hurdleRateRows = new ArrayList<OshaDisplayRow>();

		Set<OperatorAccount> inheritedOperators = new LinkedHashSet<OperatorAccount>();
		for (ContractorOperator co : contractorOperators) {
			inheritedOperators.add(co.getOperatorAccount().getInheritFlagCriteria());
		}

		for (OperatorAccount o : inheritedOperators) {
			if (oshaType == o.getOshaType() || (oshaType.equals(OshaType.EMR) && hasOperatorEmrCriteria(o))) {
				Map<MultiYearScope, Set<FlagCriteriaOperator>> flagCriteriaForYear = generateOperatorFlagCriteriaMap(
						oshaRateType, oshaType, o);
				HurdleRateDisplayRow hurdleRow = new HurdleRateDisplayRow();
				hurdleRow.setOperator(o);

				boolean hasFlagCriteria = false;

				if (oshaOrganizer.getOutOfScopeYear(oshaType) != null) {
					hurdleRow.addCell(EMPTY_CELL);
				}

				for (MultiYearScope scope : YEAR_SCOPES) {
					Set<FlagCriteriaOperator> flagCriteriaForThisYear = flagCriteriaForYear.get(scope);
					if (yearList.getYearForScope(scope) != null || scope == MultiYearScope.ThreeYearAverage) {
						if (flagCriteriaForThisYear == null || flagCriteriaForThisYear.size() == 0) {
							hurdleRow.addCell(EMPTY_CELL);
						} else if (yearList.getYearForScope(scope) != null || scope == MultiYearScope.ThreeYearAverage) {
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
				}
				if (hasFlagCriteria) {
					hurdleRateRows.add(hurdleRow);
				}
			}
		}

		return hurdleRateRows;
	}

	private boolean hasOperatorEmrCriteria(OperatorAccount operator) {
		for (FlagCriteriaOperator fco : operator.getFlagCriteriaInherited(false)) {
			if (FlagCriteria.EMR_IDS.contains(fco.getCriteria().getId())) {
				return true;
			}
		}
		return false;
	}

	private Map<MultiYearScope, Set<FlagCriteriaOperator>> generateOperatorFlagCriteriaMap(OshaRateType oshaRateType,
			OshaType oshaType, OperatorAccount o) {
		Map<MultiYearScope, Set<FlagCriteriaOperator>> flagCriteriaForYear = new HashMap<MultiYearScope, Set<FlagCriteriaOperator>>();
		for (FlagCriteriaOperator fco : o.getFlagCriteriaInherited()) {
			if ((fco.getCriteria().getOshaType() == oshaType || (oshaType.equals(OshaType.EMR) && FlagCriteria.EMR_IDS
					.contains(fco.getCriteria().getId())))
					&& isEquivalentRateTypes(oshaType, fco.getCriteria().getOshaRateType(), oshaRateType)) {
				MultiYearScope scope = fco.getCriteria().getMultiYearScope();
				Set<FlagCriteriaOperator> flagCriteria = flagCriteriaForYear.get(scope);
				if (flagCriteria == null) {
					flagCriteria = new HashSet<FlagCriteriaOperator>();
					flagCriteriaForYear.put(scope, flagCriteria);
				}
				flagCriteria.add(fco);
			}
		}

		return flagCriteriaForYear;
	}

	private boolean isEquivalentRateTypes(OshaType oshaType, OshaRateType flagCriteriaRateType,
			OshaRateType requestedRateType) {
		if (flagCriteriaRateType == null && oshaType.equals(OshaType.EMR)) {
			return true;
		}

		if (flagCriteriaRateType == null) {
			return false;
		}
		
		if (flagCriteriaRateType == requestedRateType || (flagCriteriaRateType.isTrir() && requestedRateType.isTrir())
				|| (flagCriteriaRateType.isDart() && requestedRateType.isDart())) {
			return true;
		}

		return false;
	}

	private String getFlagDescription(FlagCriteriaOperator fco) {
		if (OshaRateType.TrirWIA.equals(fco.getCriteria().getOshaRateType())) {
			float hurdle = (fco.getHurdle() != null) ? Float.valueOf(fco.getHurdle()) : 100;
			return "<nobr class=\""
					+ fco.getFlag()
					+ "\">"
					+ fco.getCriteria().getComparison()
					+ " "
					+ Strings.formatDecimalComma(Double.toString(hurdle / 100
							* contractor.getWeightedIndustryAverage())) + "</nobr>";
		} else if (OshaRateType.TrirNaics.equals(fco.getCriteria().getOshaRateType())) {
			float hurdle = (fco.getHurdle() != null) ? Float.valueOf(fco.getHurdle()) : 100;
			return "<nobr class=\""
					+ fco.getFlag()
					+ "\">"
					+ fco.getCriteria().getComparison()
					+ " "
					+ Strings.formatDecimalComma(Double.toString(hurdle / 100
							* Utilities.getIndustryAverage(false, contractor))) + "</nobr>";

 		} else if (OshaRateType.DartNaics.equals(fco.getCriteria().getOshaRateType())) {
			float hurdle = (fco.getHurdle() != null) ? Float.valueOf(fco.getHurdle()) : 100;
			return "<nobr class=\""
					+ fco.getFlag()
					+ "\">"
					+ fco.getCriteria().getComparison()
					+ " "
					+ Strings.formatDecimalComma(Double.toString(hurdle / 100
							* naicsDao.getDartIndustryAverage(contractor.getNaics()))) + "</nobr>";
		} else
			return "<nobr class=\"" + fco.getFlag() + "\">" + fco.getShortDescription() + "</nobr>";
	}

	public List<OshaType> getSortedKeySet() {
		List<OshaType> list = new ArrayList<OshaType>();
		list.addAll(getStats().keySet());
		int index = list.indexOf(OshaType.OSHA);
		if (index > 0) {
			OshaType osha = list.get(index);
			list.remove(OshaType.OSHA);
			list.add(0, osha);
		}
		return list;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map getStats() {
		Map stats = new HashMap();
		// Map stats = new TreeMap<OshaType, Map>(new Comparator<OshaType>() {
		// @Override
		// public int compare(OshaType o1, OshaType o2) {
		// if (o1.equals(OshaType.OSHA))
		// return -1;
		// if (o2.equals(OshaType.OSHA))
		// return 1;
		// return o1.name().compareTo(o2.name());
		// }
		//
		// });
		for (OshaType oshaType : OshaType.values()) {
			try {
				if (oshaOrganizer.hasOshaType(oshaType)) {
					stats.put(oshaType, getInfoForParticularOshaType(oshaType));
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		for (Object o : stats.keySet()) {
			int k = 0;
			k++;
		}
		return stats;
	}
}
