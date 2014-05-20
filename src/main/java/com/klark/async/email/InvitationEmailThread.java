package com.klark.async.email;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.klark.common.Mail;
import com.klark.util.FileContentReader;
import com.klark.util.SendEmail;

/**
 * @author This class is provided to send a large number of invitation emails using a thread so as
 *         not to hinder the user experience.
 */
public class InvitationEmailThread implements Runnable {
    private static final Log logger = LogFactory.getLog(InvitationEmailThread.class.getName());

    private Map<String, String> substituteMap = new HashMap<String, String>();

    private final Mail mail;

    /**
     * Use ONLY to send "You have been added" emails to people that you KNOW are members and not
     * contacts of the userNumber.
     * 
     * @param mail
     * @param substituteMap
     */
    public InvitationEmailThread(Mail mail, Map<String, String> substituteMap) {
        this.substituteMap = substituteMap;
        this.mail = mail;
    }

    public void run() {
        SendEmail email = new SendEmail();
        try {
            String body = getRegBody();
            email.send(mail.getRegSubject(), body, substituteMap.get("to"));
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private String getRegBody() throws IOException, URISyntaxException {
        String content = FileContentReader.getContent("/template/CoregInvitation.html");
        return content.replaceAll("\\{firstName\\}", substituteMap.get("firstName"));
    }
}
