package org.openmrs.module.haitimobileclinic.controller.ajax;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.haitimobileclinic.util.UserActivityLogger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserActivityLoggingAjaxController {

	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping("/module/haitimobileclinic/ajax/logActivity.form")
	public void patientIdentifierSearch(HttpSession session, @RequestParam("activity") String activity) throws Exception {
		UserActivityLogger.logActivity(session, activity);
	}
}
