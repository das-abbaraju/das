package com.picsauditing.actions.auditType;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditQuestionFunction;
import com.picsauditing.jpa.entities.AuditQuestionFunctionWatcher;
import com.picsauditing.jpa.entities.QuestionFunctionType;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;

public class ManageQuestionFunctionWatcher extends PicsActionSupport {
    @Autowired
    AuditQuestionDAO auditQuestionDAO;

    protected int questionId;
    protected int id;
    protected AuditQuestion question;
    protected AuditQuestionFunctionWatcher watcher;
    protected String functionId;
    protected String code;


    public String execute() {
        loadQuestion();
        watcher = findWatcher();

        if (watcher == null) {
            id = 0;
            watcher = new AuditQuestionFunctionWatcher();
            functionId = "";
            code = "";
        } else {
            functionId = "" + watcher.getFunction().getId();
            code = watcher.getUniqueCode();
        }

        return SUCCESS;
    }

    public void loadQuestion() {
        if (questionId > 0) {
            question = auditQuestionDAO.find(questionId);
        }
    }

    public AuditQuestionFunctionWatcher findWatcher() {
        if (id != 0) {
            for (AuditQuestionFunctionWatcher watcher : question.getFunctionWatchers()) {
                if (watcher.getId() == id) {
                    return watcher;
                }
            }
        }

        return null;
    }

    public String save() {
        loadQuestion();
        AuditQuestionFunctionWatcher watcher = findWatcher();
        if (watcher == null) {
            watcher = new AuditQuestionFunctionWatcher();
        }
        AuditQuestionFunction function = null;
        try {
            function = dao.find(AuditQuestionFunction.class, Integer.parseInt(functionId));
        } catch (Exception e) {
        }
        if (function == null) {
            addActionError("Cannot find Function");
            return SUCCESS;
        }

        if (Strings.isEmpty(code)) {
            addActionError("Please provide a parameter name");
            return SUCCESS;
        }

        watcher.setId(id);
        watcher.setQuestion(question);
        watcher.setFunction(function);
        watcher.setUniqueCode(code);

        watcher.setAuditColumns(permissions);

        dao.save(watcher);
        return "save";
    }

    public String delete() {
        loadQuestion();

        AuditQuestionFunctionWatcher watcher = null;
        for(AuditQuestionFunctionWatcher w:question.getFunctionWatchers()) {
            if (w.getId() == id) {
                watcher = w;
                break;
            }
        }
//        }
        if (watcher != null)
            dao.remove(watcher);

        return "delete";
    }

    public AuditQuestionDAO getAuditQuestionDAO() {
        return auditQuestionDAO;
    }

    public void setAuditQuestionDAO(AuditQuestionDAO auditQuestionDAO) {
        this.auditQuestionDAO = auditQuestionDAO;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AuditQuestion getQuestion() {
        return question;
    }

    public void setQuestion(AuditQuestion question) {
        this.question = question;
    }

    public AuditQuestionFunctionWatcher getWatcher() {
        return watcher;
    }

    public void setWatcher(AuditQuestionFunctionWatcher watcher) {
        this.watcher = watcher;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
