package com.picsauditing.service;

import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.model.events.AuditDataSaveEvent;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class AuditDataService {

	private static final Logger logger = LoggerFactory.getLogger(AuditDataService.class);

	@Autowired
	private AuditDataDAO auditDataDAO;
	@Autowired
	private AuditQuestionDAO questionDao;

    public AuditData findAuditDataByAuditAndQuestion(AuditData auditData) throws Exception {
        AuditData databaseCopy;

        if (auditData.getAudit() == null) {
            throw new Exception("Missing Audit");
        }
        if (auditData.getQuestion() == null) {
            throw new Exception("Missing Question");
        }

        databaseCopy = auditDataDAO.findAnswerToQuestion(auditData.getAudit().getId(), auditData.getQuestion().getId());
        return databaseCopy;
    }

    public AuditData findAuditData(int auditDataId) {
		return auditDataDAO.find(auditDataId);
	}

	public AuditQuestion findAuditQuestion(int questionId) {
		return questionDao.find(questionId);
	}

	public AnswerMap findAnswers(ContractorAudit audit, List<Integer> questionIds) {
		return auditDataDAO.findAnswers(audit.getId(), questionIds);
	}

	public BaseTable saveAuditData(AuditData auditData) {
		return auditDataDAO.save(auditData);
	}

	public AnswerMap loadAnswerMap(AuditData auditData) {
		List<Integer> questionIds = new ArrayList<>();
		questionIds.add(auditData.getQuestion().getId());
		if (auditData.getQuestion().getRequiredQuestion() != null) {
			AuditQuestion q = auditData.getQuestion().getRequiredQuestion();
			while (q != null) {
				questionIds.add(q.getId());
				q = q.getRequiredQuestion();
			}
		}
		if (auditData.getQuestion().getVisibleQuestion() != null) {
			AuditQuestion q = auditData.getQuestion().getVisibleQuestion();
			while (q != null) {
				questionIds.add(q.getId());
				q = q.getVisibleQuestion();
			}
		}

		questionIds.addAll(auditData.getQuestion().getSiblingQuestionWatchers());

		AnswerMap answerMap = findAnswers(auditData.getAudit(), questionIds);
		return answerMap;
	}

	public void insertAuditData(AuditData auditData) {
		AuditDataSaveEvent auditDataSaveEvent = new AuditDataSaveEvent(auditData);
		SpringUtils.publishEvent(auditDataSaveEvent);
	}
}
