package com.picsauditing.actions;

import javax.servlet.http.HttpServletRequest;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.apache.struts2.ServletActionContext;

@SuppressWarnings("serial")
public class Recaptcha extends PicsActionSupport {
	private ReCaptcha recaptcha;
	static private final String PRIVATE_KEY = "6LfJpcMSAAAAAHrOAmxBr-HT5xhOPE4ffYp8lBIb";
	static private final String PUBLIC_KEY = "6LfJpcMSAAAAAM7eGED7ItAEsS88YaMczh_JRRMX";
	// Account under info@picsauditing.com
	// https://www.google.com/recaptcha/admin/site?siteid=314811849

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	public ReCaptcha getRecaptcha() {
		return recaptcha;
	}

	public void setRecaptcha(ReCaptcha recaptcha) {
		this.recaptcha = recaptcha;
	}

	public Boolean isRecaptchaResponseValid() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String remoteAddr = ServletActionContext.getRequest().getRemoteAddr();

		ReCaptchaImpl reCaptcha = new ReCaptchaImpl();

		reCaptcha.setPrivateKey(PRIVATE_KEY);

		String challenge = request.getParameter("recaptcha_challenge_field");
		String uresponse = request.getParameter("recaptcha_response_field");
		for (int failures = 0; failures <= 4; failures++) {
			try {
				// ReCaptchaImpl can fail to checkAnswer. If this is the case,
				// retry and then quit if initialization cannot be done
				ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);
				return reCaptchaResponse.isValid();
			} catch (Exception e) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e1) {
					// Should never get interrupted
					e1.printStackTrace();
				}
			}
		}

		return null;
	}

	public String getRecaptchaHtml() {
		recaptcha = ReCaptchaFactory.newReCaptcha(PUBLIC_KEY, PRIVATE_KEY, false);

		return recaptcha.createRecaptchaHtml(null, null);
	}
}
