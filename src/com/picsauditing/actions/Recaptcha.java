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

		// ReCaptchaImpl can fail to initialize properly. If this is the case,
		// retry and then quit if initialization cannot be done
		for (int failures = 0; failures <= 4; failures++) {
			try {
				ReCaptchaImpl reCaptcha = new ReCaptchaImpl();

				reCaptcha.setPrivateKey("6Lev0woAAAAAAIrXy4lxh5eOtaUq0W42iqP767zx");

				String challenge = request.getParameter("recaptcha_challenge_field");
				String uresponse = request.getParameter("recaptcha_response_field");
				ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);

				return reCaptchaResponse.isValid();
			} catch (Exception e) {
				if (failures >= 4) {
					return null; // just quit, >100ms passed w/ no proper
					// initialization
				}
				try {
					Thread.sleep(20);
				} catch (InterruptedException e1) {
					// Should never get interrupted
					e1.printStackTrace();
				}
			}
		}

		return null;
	}

	public String getRecaptchaHtml() {
		recaptcha = ReCaptchaFactory.newReCaptcha("6Lev0woAAAAAAIOP_MmACTSBbKjynJ7_MkirU0rz",
				"6Lev0woAAAAAAIrXy4lxh5eOtaUq0W42iqP767zx", false);

		return recaptcha.createRecaptchaHtml(null, null);
	}

}
