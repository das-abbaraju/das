package com.picsauditing.auditBuilder;

import com.picsauditing.util.Strings;

public class AuditQuestionSuggestion {
    String suggestion;
    String suggestedAnswer;
    String suggestedActionTitle;

    public boolean isHasSuggestedAction() {
        if (!Strings.isEmpty(suggestedAnswer) && !Strings.isEmpty(suggestedActionTitle)) {
            return true;
        }

        return false;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getSuggestedAnswer() {
        return suggestedAnswer;
    }

    public void setSuggestedAnswer(String suggestedAnswer) {
        this.suggestedAnswer = suggestedAnswer;
    }

    public String getSuggestedActionTitle() {
        return suggestedActionTitle;
    }

    public void setSuggestedActionTitle(String suggestedActionTitle) {
        this.suggestedActionTitle = suggestedActionTitle;
    }
}
