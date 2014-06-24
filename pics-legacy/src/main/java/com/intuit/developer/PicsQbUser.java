package com.intuit.developer;

import com.picsauditing.jpa.entities.Currency;

public enum PicsQbUser {
    PICSQBLOADER("PICSQBLOADER", QbListId.QB_LIST_ID, Currency.USD, true),
    PICSQBLOADERCAN("PICSQBLOADERCAN", QbListId.QB_LIST_CAID, Currency.CAD, true),
    PICSQBLOADERUK("PICSQBLOADERUK", QbListId.QB_LIST_UKID, Currency.GBP, true),
    PICSQBLOADEREU("PICSQBLOADEREU", QbListId.QB_LIST_EUID, Currency.EUR, true),
    PICSQBLOADERDKK("PICSQBLOADERDKK", QbListId.QB_LIST_EUID, Currency.EUR, true),
    PICSQBLOADERSEK("PICSQBLOADERSEK", QbListId.QB_LIST_EUID, Currency.EUR, true),
    PICSQBLOADERZAR("PICSQBLOADERZAR", QbListId.QB_LIST_EUID, Currency.EUR, true),
    PICSQBLOADERNOK("PICSQBLOADERNOK", QbListId.QB_LIST_EUID, Currency.EUR, true),
    PICSQBLOADERCHF("PICSQBLOADERCHF", QbListId.QB_LIST_CHID, Currency.CHF, true),
    PICSQBLOADERPLN("PICSQBLOADERPLN", QbListId.QB_LIST_PLID, Currency.PLN, true),
    PICSQBLOADEROTHERS("PICSQBLOADEROTHERS", null, null, false);

    private String qbUsername;
    private String qbListId;
    private Currency currency;
    private boolean knownQBWCUsername;

    PicsQbUser(String qbUserName, String qbListId, Currency currency, boolean knownQBWCUsername) {
        this.qbUsername = qbUserName;
        this.qbListId = qbListId;
        this.currency = currency;
        this.knownQBWCUsername = knownQBWCUsername;
    }

    public String getQbUsername() {
        return qbUsername;
    }

    public String getQbListId() {
        return qbListId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public boolean isKnownQBWCUsername() {
        return !this.equals(PICSQBLOADEROTHERS);
    }

    public static PicsQbUser fromQbUsername(String qbUserName) {
        try {
            return PicsQbUser.valueOf(qbUserName);
        } catch (IllegalArgumentException e) {
            return PICSQBLOADEROTHERS;
        }
    }

    private static class QbListId {
        public static final String QB_LIST_ID = "qbListID";
        public static final String QB_LIST_CAID = "qbListCAID";
        public static final String QB_LIST_UKID = "qbListUKID";
        public static final String QB_LIST_EUID = "qbListEUID";
        public static final String QB_LIST_CHID = "qbListCHID";
        public static final String QB_LIST_PLID = "qbListPLID";
    }
}
