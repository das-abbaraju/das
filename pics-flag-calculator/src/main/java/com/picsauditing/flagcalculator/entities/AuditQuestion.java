package com.picsauditing.flagcalculator.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.AuditQuestion")
@Table(name = "audit_question")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "audit_cache")
public class AuditQuestion extends BaseHistory {

    static public final int EMR = 2034;
    static public final int CITATIONS = 3546;
    static public final int EXCESS_EACH = 2161;
    static public final int EXCESS_AGGREGATE = 2165;
    static public final int OSHA_KEPT_ID = 2064;
    static public final int COHS_KEPT_ID = 2066;
    static public final int UK_HSE_KEPT_ID = 9106;
    static public final int EMR_KEPT_ID = 2033;
    static public final int MEXICO_KEPT_ID = 15337;
    static public final int AUSTRALIA_KEPT_ID = 15214;
    static public final int IRELAND_KEPT_ID = 15660;
    static public final int SOUTH_AFRICA_KEPT_ID = 16282;
    static public final int SINGAPORE_MOM_KEPT_ID = 16590;
    static public final int TURKEY_KEPT_ID = 17168;
    static public final int SWITZERLAND_KEPT_ID = 16894;
    static public final int SPAIN_KEPT_ID = 17097;
    static public final int POLAND_KEPT_ID = 17141;
    static public final int AUSTRIA_KEPT_ID = 17126;
    static public final int ITALY_KEPT_ID = 17111;
    static public final int PORTUGAL_KEPT_ID = 17129;
    static public final int DENMARK_KEPT_ID = 17172;
    static public final int CZECH_KEPT_ID = 17183;
    static public final int HUNGARY_KEPT_ID = 17170;
    static public final int GREECE_KEPT_ID = 17203;

    private String visibleAnswer;
    private String questionType;
    private AuditOptionGroup option;
    private AuditQuestion visibleQuestion;
    private AuditCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryID", nullable = false)
    public AuditCategory getCategory() {
        return this.category;
    }

    public void setCategory(AuditCategory auditCategory) {
        this.category = auditCategory;
    }

    @Column(nullable = false)
    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    @ManyToOne
    @JoinColumn(name = "optionID")
    public AuditOptionGroup getOption() {
        return option;
    }

    public void setOption(AuditOptionGroup option) {
        this.option = option;
    }

    @ManyToOne
    @JoinColumn(name = "visibleQuestion")
    public AuditQuestion getVisibleQuestion() {
        return visibleQuestion;
    }

    public void setVisibleQuestion(AuditQuestion visibleQuestion) {
        this.visibleQuestion = visibleQuestion;
    }

    public String getVisibleAnswer() {
        return visibleAnswer;
    }

    public void setVisibleAnswer(String visibleAnswer) {
        this.visibleAnswer = visibleAnswer;
    }
}