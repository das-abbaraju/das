package com.picsauditing.actions.auditType;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditQuestionFunction;
import com.picsauditing.jpa.entities.AuditQuestionFunctionWatcher;
import com.picsauditing.jpa.entities.QuestionFunctionType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;

public class ManageQuestionFunction extends PicsActionSupport {
    @Autowired
    AuditQuestionDAO auditQuestionDAO;

    protected int questionId;
    protected int id;
    protected AuditQuestion question;
    protected AuditQuestionFunction function;


    public String execute() {
        loadQuestion();
        function = findFunction();

        if (function == null) {
            id = 0;
            function = new AuditQuestionFunction();
        }
        return SUCCESS;
    }

    public void loadQuestion() {
        if (questionId > 0) {
            question = auditQuestionDAO.find(questionId);
        }
    }

    public AuditQuestionFunction findFunction() {
        if (id != 0) {
            for (AuditQuestionFunction function : question.getFunctions()) {
                if (function.getId() == id) {
                    return function;
                }
            }
        }

        return null;
    }

    public String save() {
        loadQuestion();
        function.setId(id);
        if (function.getFunction() == null) {
            addActionError("You must select a Function");
            return SUCCESS;
        }
        if (function.getType() == null) {
            addActionError("You must select a Type");
            return SUCCESS;
        }
        saveFunction();
        return "save";
    }

    private void saveFunction() {
        loadQuestion();
        AuditQuestionFunction func = findFunction();
        if (func != null) {
            func.setType(function.getType());
            func.setFunction(function.getFunction());
            func.setOverwrite(function.isOverwrite());
            func.setExpression(function.getExpression());
        } else {
            func = function;
            func.setQuestion(question);
        }
        func.setAuditColumns(permissions);

        dao.save(func);
    }

    public String delete() {
        loadQuestion();
        AuditQuestionFunction function = findFunction();
        Iterator<AuditQuestionFunctionWatcher> iterator = function.getWatchers().iterator();
        while(iterator.hasNext()) {
            AuditQuestionFunctionWatcher watcher = iterator.next();
            dao.remove(watcher);
        }
        dao.remove(function);
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

    public AuditQuestionFunction getFunction() {
        return function;
    }

    public void setFunction(AuditQuestionFunction function) {
        this.function = function;
    }
}
