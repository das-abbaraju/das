package com.picsauditing.importpqf;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditExtractOption;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditTransformOption;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

public abstract class ImportPqf {
	@Autowired
	private AuditDataDAO auditDataDAO;
	@Autowired
	private AuditPercentCalculator auditPercentCalculator;

	StringBuilder log = new StringBuilder();

	abstract public int getAuditType();

	/**
	 * Imports a PDF file, saves answers to 3rd party audit and PICS PQF
	 * 
	 * @param conAudit
	 * @param pdfFile
	 */
	public void calculate(ContractorAudit conAudit, File pdfFile) {
		{
			// Extract
			List<String> lines = getFileContents(pdfFile);
			List<AuditQuestion> questions = getQuestions(conAudit);

			extractAnswers(questions, lines);
			saveAnswers(conAudit, questions);
			// dumpQuestions(questions);
		}
		transformPqf(conAudit);
	}

	/**
	 * Transforms answers from 3rd party PQF to PICS PQF
	 * 
	 * @param conAudit
	 */
	private void transformPqf(ContractorAudit conAudit) {
		ContractorAudit pqfAudit = null;
		for (ContractorAudit audit : conAudit.getContractorAccount().getAudits()) {
			if (audit.getAuditType().isPqf()) {
				pqfAudit = audit;
				break;
			}
		}
		if (pqfAudit == null) {
			return;
		}

		List<AuditQuestion> questions = getQuestions(conAudit);
		AnswerMap auditAnswers = getAnswers(conAudit.getContractorAccount().getId(), questions);

		List<AuditQuestion> pqfQuestions = getQuestions(pqfAudit);
		AnswerMap pqfAnswers = getAnswers(conAudit.getContractorAccount().getId(), pqfQuestions);

		int questionCount = 0;
		int transformCount = 0;
		int processedCount = 0;
		for (AuditQuestion question : questions) {
			questionCount++;

			// loop through transforms
			for (AuditTransformOption option : question.getTransformOptions()) {
				transformCount++;

				AuditData data = auditAnswers.get(question.getId());
				if (data != null) {
					String answer = null;

					if (option.getComparisonIds().size() == 0) {
						answer = option.transformResponse(data.getAnswer());
					} else if (option.getComparison().isSameComparison()) {
						String response = data.getAnswer();
						boolean allSame = true;
						for (Integer id : option.getComparisonIds()) {
							AuditData data2 = auditAnswers.get(id.intValue());
							String otherResponse = null;
							if (data2 != null)
								otherResponse = data.getAnswer();
							if (!Strings.isEqualNullSafe(response, otherResponse)) {
								allSame = false;
								break;
							}
						}
						if (allSame)
							answer = option.transformResponse(response);
					} else if (option.getComparison().isOrComparison()) {
						if (containsNullSafe(data.getAnswer(), "Yes")) {
							answer = "Yes";
						} else {
							for (Integer id : option.getComparisonIds()) {
								AuditData data2 = auditAnswers.get(id.intValue());
								String otherResponse = null;
								if (data2 != null)
									otherResponse = data.getAnswer();
								if (containsNullSafe(otherResponse, "Yes")) {
									answer = "Yes";
									break;
								}
							}
						}
					}

					answer = processAnswer(option, answer, auditAnswers);

					if (answer != null) {
						processedCount++;
						AuditData pqfData = pqfAnswers.get(option.getDestinationQuestion().getId());
						if (pqfData == null) {
							pqfData = new AuditData();
							pqfData.setAudit(pqfAudit);
							pqfData.setQuestion(option.getDestinationQuestion());
							auditDataDAO.save(pqfData);
							pqfAnswers.add(pqfData);
						}

						if (!option.isCommentResponse()) {
							pqfData.setAnswer(answer);
						} else {
							pqfData.setComment(answer);
						}
					} else {
						log.append("\n* Question " + question.getId() + " had no response ("
								+ question.getName().toString() + ")");
					}
				}
			}
		}

		if (auditPercentCalculator == null) {
			auditPercentCalculator = (AuditPercentCalculator) SpringUtils.getBean("AuditPercentCalculator");
		}

		auditPercentCalculator.percentCalculateComplete(pqfAudit, true);

		int percent = 0;

		if (transformCount == 0) {
			log.append("\n* NO TRANSFORMS FOUND");
		} else {
			percent = (int) (processedCount * 100 / transformCount);
			log.append("\n" + percent + "% success populating PQF (" + processedCount + "/" + transformCount + ")");
		}
	}

	/**
	 * Opens pdf file and returns the lines of text
	 * 
	 * @param pdfFile
	 * @return
	 */
	private List<String> getFileContents(File pdfFile) {
		FileInputStream inputStream = null;
		PdfReader reader = null;

		StringBuilder sb = new StringBuilder();
		ArrayList<String> lines = new ArrayList<String>();

		try {
			inputStream = new FileInputStream(pdfFile);
			reader = new PdfReader(inputStream);
			if (reader.isEncrypted()) {
				log.append("Unabled to process. File is encrypted.");
				return lines;
			}
			PdfReaderContentParser parser = new PdfReaderContentParser(reader);
			for (int page = 1; page <= reader.getNumberOfPages(); page++) {
				sb.append(
						preprocessPage(page, parser.processContent(page, new SimpleTextExtractionStrategy())
								.getResultantText())).append("\n");
			}
			for (String line : sb.toString().split("[\n\t\r]")) {
				line = line.trim();
				if (line.length() > 0)
					lines.add(line);
			}
		} catch (Exception x) {
			log.append(x);
		} catch (Error x) {
			log.append(x);
		} finally {
			try {
				if (reader != null) {
					reader.close();
					reader = null;
				}
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
			} catch (Exception ignore) {
			}
		}

		return lines;
	}

	private List<AuditQuestion> getQuestions(ContractorAudit conAudit) {
		List<AuditQuestion> list = new ArrayList<AuditQuestion>();

		ArrayList<AuditCategory> catList = new ArrayList<AuditCategory>();
		catList.addAll(conAudit.getAuditType().getTopCategories());
		Collections.sort(catList, new Comparator<AuditCategory>() {
			public int compare(AuditCategory o1, AuditCategory o2) {
				return o1.getNumber() - o2.getNumber();
			}
		});
		for (AuditCategory cat : catList) {
			list.addAll(getCategoryQuestions(cat));
		}
		return list;
	}

	private List<AuditQuestion> getCategoryQuestions(AuditCategory cat) {
		List<AuditQuestion> list = new ArrayList<AuditQuestion>();

		list.addAll(cat.getQuestions());

		ArrayList<AuditCategory> catList = new ArrayList<AuditCategory>();
		catList.addAll(cat.getSubCategories());
		Collections.sort(catList, new Comparator<AuditCategory>() {
			public int compare(AuditCategory o1, AuditCategory o2) {
				return o1.getNumber() - o2.getNumber();
			}
		});
		for (AuditCategory child : catList) {
			list.addAll(getCategoryQuestions(child));
		}
		return list;
	}

	private AnswerMap getAnswers(int conId, List<AuditQuestion> questions) {
		Collection<Integer> questionIds = new ArrayList<Integer>();
		for (AuditQuestion question : questions) {
			questionIds.add(question.getId());
		}

		auditDataDAO = (AuditDataDAO) SpringUtils.getBean("AuditDataDAO");
		return auditDataDAO.findCurrentAnswers(conId, questionIds);
	}

	/**
	 * Saves extracted answers to 3rd part pqf audit
	 * 
	 * @param conAudit
	 * @param questions
	 * @return
	 */
	private int saveAnswers(ContractorAudit conAudit, List<AuditQuestion> questions) {
		Collection<Integer> questionIds = new ArrayList<Integer>();
		for (AuditQuestion question : questions) {
			questionIds.add(question.getId());
		}

		auditDataDAO = (AuditDataDAO) SpringUtils.getBean("AuditDataDAO");
		AnswerMap currentAnswers = auditDataDAO
				.findCurrentAnswers(conAudit.getContractorAccount().getId(), questionIds);

		int answersProcessed = 0;

		for (AuditQuestion question : questions) {
			AuditExtractOption option = question.getExtractOption();
			if (option != null && option.isAnswerFound()) {
				answersProcessed++;

				AuditData data = currentAnswers.get(question.getId());
				if (data == null) {
					data = new AuditData();
					data.setAudit(conAudit);
					data.setQuestion(question);
					auditDataDAO.save(data);
				}

				data.setAnswer(option.getAnswer());
			}
		}

		return answersProcessed;
	}

	/**
	 * Goes through Audit Questions and extracts info from lines
	 * 
	 * @param questions
	 * @param lines
	 * @return percent of how many questions were successfully extracted
	 */
	private int extractAnswers(List<AuditQuestion> questions, List<String> lines) {
		int answered = 0;
		int totalFound = 0;
		int lineIndex = 0;
		String match = "";

		for (int i = 0; i < questions.size(); i++) {
			AuditQuestion auditQuestion = questions.get(i);
			AuditExtractOption option = auditQuestion.getExtractOption();

			if (option == null) {
				log.append("\n* Question " + auditQuestion.getId() + " has no extraction defined ("
						+ auditQuestion.getName().toString() + ")");
				continue;
			}

			totalFound++;

			if (option.isProcessed()) {
				if (option.isAnswerFound())
					answered++;
				continue;
			}

			String question = auditQuestion.getName().toString();

			option.setAnswer(null);

			if (option.isStartAtBeginning()) {
				lineIndex = 0;
			}

			// advance to starting point
			if (!Strings.isEmpty(option.getStartingPoint())) {
				while (lineIndex < lines.size()) {
					if (lines.get(lineIndex).contains(option.getStartingPoint())) {
						break;
					}
					lineIndex++;
				}
			}

			if (lineIndex >= lines.size()) {
				lineIndex = 0;
			}

			// search for question
			while (lineIndex < lines.size() && !option.isQuestionFound()) {
				match += filterQuestionLine(lines.get(lineIndex));

				// check for next question
				if (isStoppingPoint(option, match)) {
					break;
				}

				if (question.equals(match)) { // exact match, answer on
												// following line
					option.setQuestionFound(true);
					match = "";
				} else if (match.startsWith(question)) { // match, answer on
															// line
					option.setQuestionFound(true);
					match = match.substring(question.length()).trim();
				} else if (isMatchPQFSpecific(question, match)) {
					option.setQuestionFound(true);
					match = trimPQFSpecific(question, match);
				} else if (isPartialMatchPQFSpecific(question, match)) {
					match += " ";
				} else if (match.length() == 0) { // blank line
					match = "";
				} else if (question.startsWith(match)) { // might have answer
					match += " ";
				} else {
					match = "";
				}

				lineIndex++;
			}

			if (option.isQuestionFound()) {
				// search for answer
				while (lineIndex < lines.size() && !option.isAnswerFound()) {
					// check for next question
					if (isStoppingPoint(option, match) || isNextQuestion(questions, i, match)) {
						break;
					}

					if (option.getStopAt().isStopAtSameLine()) {
						if (match.length() > 0 && isValidResponse(option, match)) {
							processAnswer(option, match.trim());
						}
						match = "";
						break;
					}

					String nextPart = lines.get(lineIndex).trim();

					if (nextPart.length() == 0) { // next is blank line
						if (match.length() > 0 && isValidResponse(option, match)) {
							processAnswer(option, match.trim());
						}
						match = "";
						lineIndex++;
						break;
					}

					// check for stopping point
					if (isStoppingPoint(option, nextPart)) {
						if (match.length() > 0 && isValidResponse(option, match)) {
							processAnswer(option, match.trim());
						}
						match = "";
						nextPart = nextPart.substring(option.getStoppingPoint().length());
						if (nextPart.trim().length() == 0)
							lineIndex++;
						break;
					}

					// check for next question
					if (isNextQuestion(questions, i, nextPart)) {
						if (match.length() > 0 && isValidResponse(option, match)) {
							processAnswer(option, match.trim());
						}
						match = "";
						break;
					}

					if (match.length() > 0)
						if (option.isCollectAsLines()) {
							match += "\n";
						} else {
							match += " ";
						}

					match += nextPart;

					if (option.getStopAt().isStopAtNextLine()) {
						if (match.length() > 0 && isValidResponse(option, match)) {
							processAnswer(option, match.trim());
						}
						match = "";
					}

					lineIndex++;
				}

				// catch last question answer
				if (match.length() > 0 && !option.isAnswerFound() && lineIndex >= lines.size()) {
					processAnswer(option, match.trim());
				}
			} else {
				log.append("\n* Question " + auditQuestion.getId() + " NOT FOUND ("
						+ auditQuestion.getName().toString() + ")");
			}

			if (option.isAnswerFound()) {
				answered++;
			} else if (option.isQuestionFound()) {
				log.append("\n* Answer to Question " + auditQuestion.getId() + " NOT FOUND ("
						+ auditQuestion.getName().toString() + ")");
			}
		}

		int percent = 0;

		if (answered == 0) {
			log.append("\n* NO ANSWERS FOUND");
			return 0;
		} else {
			percent = (int) (answered * 100 / totalFound);
			log.append("\n" + percent + "% success extracting 3rd party responses (" + answered + "/" + totalFound
					+ ")");
		}

		return percent;
	}

	protected void dumpQuestions(List<AuditQuestion> questions) {
		Logger logger = LoggerFactory.getLogger(ImportPqf.class);
		logger.info("Dump of Questions/Answers");
		for (AuditQuestion auditQuestion : questions) {
			AuditExtractOption option = auditQuestion.getExtractOption();

			if (option == null) {
				logger.info("* {} >-No ETL-<", auditQuestion.getName().toString());
			} else {
				if (option.isAnswerFound()) {
					logger.info("* {} > {} <", auditQuestion.getName().toString(), option.getAnswer());
				} else {
					logger.info("*** {} >-No Answer ({})-<", auditQuestion.getName().toString(), auditQuestion.getId());
				}
			}
		}
	}

	public String getLog() {
		return log.toString();
	}

	private boolean containsNullSafe(String source, String search) {
		if (source == null || search == null)
			return false;
		return (source.indexOf(search) >= 0);
	}

	/**
	 * Method for "last-chance" processing of answer. Used for hard-coding some
	 * question processing
	 * 
	 * @param option
	 * @param response
	 * @param auditAnswers
	 * @return
	 */
	protected String processAnswer(AuditTransformOption option, String response, AnswerMap auditAnswers) {
		return response;
	}

	/**
	 * Sets answer of option
	 * 
	 * @param option
	 * @param response
	 */
	protected void processAnswer(AuditExtractOption option, String response) {
		option.setAnswer(response);
	}

	/**
	 * Determine if match has reached the stopping point
	 * 
	 * @param option
	 * @param match
	 * @return
	 */
	protected boolean isStoppingPoint(AuditExtractOption option, String match) {
		String stoppingPoint = option.getStoppingPoint();
		if (match != null && !Strings.isEmpty(stoppingPoint) && match.contains(stoppingPoint)) {
			return true; // got to next question
		}

		return isStoppingPointPQFSpecific(option, match);
	}

	/**
	 * Determine if match has found next question
	 * 
	 * @param questions
	 * @param curIndex
	 * @param match
	 * @return
	 */
	protected boolean isNextQuestion(List<AuditQuestion> questions, int curIndex, String match) {
		if (Strings.isEmpty(match)) {
			return false;
		}

		if (curIndex + 1 < questions.size()) {
			String nextQuestion = questions.get(curIndex + 1).getName().toString();

			if (nextQuestion.equals(match)) { // exact match, answer on
												// following line
				return true;
			} else if (match.startsWith(nextQuestion)) { // match, answer on
															// line
				return true;
			} else if (nextQuestion.startsWith(match)) { // might have answer
				return true;
			}
		}

		return isNextQuestionPQFSpecific(questions, curIndex, match);
	}

	/**
	 * Last chance for PQF-specific import can determine if at answer stopping
	 * point
	 * 
	 * @param option
	 * @param match
	 * @return
	 */
	protected boolean isStoppingPointPQFSpecific(AuditExtractOption option, String match) {
		return false;
	}

	/**
	 * Last chance for PQF-specific import can determine if at next question
	 * 
	 * @param option
	 * @param match
	 * @return
	 */
	protected boolean isNextQuestionPQFSpecific(List<AuditQuestion> questions, int curIndex, String match) {
		return false;
	}

	/**
	 * Check for valid response. Can override
	 * 
	 * @param option
	 * @param response
	 * @return
	 */
	protected boolean isValidResponse(AuditExtractOption option, String response) {
		return true;
	}

	/**
	 * Used to determine if match is an "exact" match of a question for
	 * PQF-specific conditions
	 * 
	 * @param question
	 * @param match
	 * @return
	 */
	protected boolean isMatchPQFSpecific(String question, String match) {
		return false;
	}

	/**
	 * Trims match string of question
	 * 
	 * @param question
	 * @param match
	 * @return
	 */
	protected String trimPQFSpecific(String question, String match) {
		match = match.substring(0, match.indexOf(question)).trim() + " "
				+ match.substring(match.indexOf(question) + question.length());
		match = match.trim();
		return match;
	}

	/**
	 * Used to determine if match is a partial match of a question for
	 * PQF-specific conditions
	 * 
	 * @param question
	 * @param match
	 * @return
	 */
	protected boolean isPartialMatchPQFSpecific(String question, String match) {
		return false;
	}

	/**
	 * Used to process match based on PQF-specific conditions
	 * 
	 * @param question
	 * @param match
	 * @return
	 */
	protected String processMatchPQFSpecific(String question, String match) {
		return match;
	}

	/**
	 * Used to examine line before processing it for questions
	 * 
	 * @param match
	 * @return
	 */
	protected String filterQuestionLine(String match) {
		return match;
	}

	/**
	 * Used to preprocess page text such as removing headers/footers
	 * 
	 * @param page
	 * @param text
	 * @return
	 */
	protected String preprocessPage(int page, String text) {
		return text;
	}
}
