<s:if test="caos.keySet().size > 1">
    <s:if test="allCaosAre(@com.picsauditing.jpa.entities.AuditStatus@Submitted)">
        <div>
            <button class="picsbutton negative" name="button" onclick="return changeAuditStatus(<s:property value="conAudit.id"/>,'Incomplete','Reject All',<s:property value="allCaoIDs" />);">Reject All</button>
            <button style="display: <s:property value="#showApproveButton" />" class="picsbutton positive approveButton" name="button" onclick="return changeAuditStatus(<s:property value="conAudit.id"/>,'Complete','Complete All',<s:property value="allCaoIDs" />);">Complete All</button>
        </div>
    </s:if>
    <s:elseif test="allCaosAre(@com.picsauditing.jpa.entities.AuditStatus@Resubmitted)">
        <div>
            <button class="picsbutton warning" name="button" onclick="return changeAuditStatus(<s:property value="conAudit.id"/>,'Resubmit','Reject All',<s:property value="allCaoIDs" />);">Resubmit All</button>
            <button style="display: <s:property value="#showApproveButton" />" class="picsbutton positive approveButton" name="button" onclick="return changeAuditStatus(<s:property value="conAudit.id"/>,'Complete','Complete All',<s:property value="allCaoIDs" />);">Complete All</button>
        </div>
    </s:elseif>
</s:if>