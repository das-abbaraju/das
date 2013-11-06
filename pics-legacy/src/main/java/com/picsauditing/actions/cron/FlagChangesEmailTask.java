package com.picsauditing.actions.cron;

import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.search.Database;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.business.OperatorUtil;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class FlagChangesEmailTask implements CronTask {
    Database database = new Database();
    @Autowired
    EmailQueueDAO emailQueueDAO;

    public String getDescription() {
        return "TODO";
    }

    public List<String> getSteps() {
        return null;
    }

    public CronTaskResult run() throws Exception {
        CronTaskResult results = new CronTaskResult(true, "");
        List<BasicDynaBean> data = getFlagChangeData();
        if (CollectionUtils.isEmpty(data)) {
            return results;
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
        return results;
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
