package com.picsauditing.actions.cron;

import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.Indexable;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.search.Database;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.IndexerEngine;
import com.picsauditing.util.Strings;
import com.picsauditing.util.business.OperatorUtil;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class FlagChangesEmailTask extends CronTask {
    private static String NAME = "FlagChangesEmail";
    private Database database = new Database();
    private EmailQueueDAO emailQueueDAO;

    public FlagChangesEmailTask(Database database, EmailQueueDAO emailQueueDAO) {
        super(NAME);
        this.database = database;
        this.emailQueueDAO = emailQueueDAO;
    }

    protected void run() throws Exception {
        List<BasicDynaBean> data = getFlagChangeData();
        if (CollectionUtils.isEmpty(data)) {
            return;
        }

        sendFlagChangesEmail(EmailAddressUtils.PICS_FLAG_CHANGE_EMAIL, data);

        Map<String, List<BasicDynaBean>> amMap = sortResultsByAccountManager(data);
        if (MapUtils.isNotEmpty(amMap)) {
            for (String accountMgr : amMap.keySet()) {
                if (!Strings.isEmpty(accountMgr) && amMap.get(accountMgr) != null && amMap.get(accountMgr).size() > 0) {
                    List<BasicDynaBean> flagChanges = amMap.get(accountMgr);
                    sendFlagChangesEmail(accountMgr, flagChanges);
                }
            }
        }
    }

    private List<BasicDynaBean> getFlagChangeData() throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("select id, operator, accountManager, changes, total, round(changes * 100 / total) as percent from ( ");
        query.append("select o.id, o.name operator, concat(u.name, ' <', u.email, '>') accountManager, ");
        query.append("count(*) total, sum(case when co.flag = co.baselineFlag THEN 0 ELSE 1 END) changes ");
        query.append("from contractor_operator co ");
        query.append("join accounts c on co.conID = c.id and c.status = 'Active' ");
        query.append("join accounts o on co.opID = o.id and o.status = 'Active' and o.type = 'Operator' and o.id not in ("
                + Strings.implode(OperatorUtil.operatorsIdsUsedForInternalPurposes()) + ") ");
        query.append("LEFT join account_user au on au.accountID = o.id and au.role = 'PICSAccountRep' and startDate < now() ");
        query.append("and endDate > now() ");
        query.append("LEFT join users u on au.userID = u.id ");
        query.append("group by o.id) t ");
        query.append("where changes >= 10 and changes/total > .05 ");
        query.append("order by percent desc ");

        List<BasicDynaBean> data = database.select(query.toString(), true);
        return data;
    }

    private Map<String, List<BasicDynaBean>> sortResultsByAccountManager(List<BasicDynaBean> data) {
        // Sorting results into buckets by AM to add as tokens into the email
        Map<String, List<BasicDynaBean>> amMap = new TreeMap<String, List<BasicDynaBean>>();

        if (CollectionUtils.isEmpty(data)) {
            return amMap;
        }

        for (BasicDynaBean bean : data) {
            String accountMgr = (String) bean.get("accountManager");
            if (accountMgr != null) {
                if (amMap.get(accountMgr) == null) {
                    amMap.put(accountMgr, new ArrayList<BasicDynaBean>());
                }

                amMap.get(accountMgr).add(bean);
            }
        }

        return amMap;
    }

    private void sendFlagChangesEmail(String accountMgr, List<BasicDynaBean> flagChanges) throws IOException {
        EmailBuilder emailBuilder = new EmailBuilder();
        emailBuilder.setTemplate(EmailTemplate.FLAG_CHANGES_EMAIL_TEMPLATE);
        emailBuilder.setFromAddress(EmailAddressUtils.PICS_SYSTEM_EMAIL_ADDRESS);
        emailBuilder.addToken("changes", flagChanges);
        int totalFlagChanges = sumFlagChanges(flagChanges);
        emailBuilder.addToken("totalFlagChanges", totalFlagChanges);
        emailBuilder.setToAddresses(accountMgr);
        EmailQueue email = emailBuilder.build();
        email.setVeryHighPriority();
        email.setSubjectViewableById(Account.PicsID);
        email.setBodyViewableById(Account.PicsID);
        emailQueueDAO.save(email);
        emailBuilder.clear();
    }

    private int sumFlagChanges(List<BasicDynaBean> flagChanges) {
        int totalChanges = 0;
        if (CollectionUtils.isEmpty(flagChanges)) {
            return totalChanges;
        }

        for (BasicDynaBean flagChangesByOperator : flagChanges) {
            try {
                Object operatorFlagChanges = flagChangesByOperator.get("changes");
                if (operatorFlagChanges != null) {
                    totalChanges += NumberUtils.toInt(operatorFlagChanges.toString(), 0);
                }
            } catch (Exception ignore) {
            }
        }

        return totalChanges;
    }


}
