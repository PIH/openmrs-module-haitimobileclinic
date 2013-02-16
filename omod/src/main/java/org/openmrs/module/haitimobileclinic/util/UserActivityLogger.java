package org.openmrs.module.haitimobileclinic.util;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.module.haitimobileclinic.UserActivity;
import org.openmrs.module.haitimobileclinic.service.HaitiMobileClinicService;
import org.openmrs.web.WebConstants;

/**
 * Logs user activity events
 */
public class UserActivityLogger {
	
	protected static final Log log = LogFactory.getLog(UserActivityLogger.class);

	/**
	 * Extracts the current context from the request, and logs this along with the activity
	 */
	public static void logActivity(HttpSession session, String activity) {
		logActivity(session, activity, null);
	}
	
	/**
	 * Extracts the current context from the request, and logs this along with the activity
	 */
	public static void logActivity(HttpSession session, String activity, String extraInfo) {
		try {
			UserActivity a = new UserActivity(activity);
			if (session != null) {
				a.setTask(HaitiMobileClinicWebUtil.getRegistrationTask(session));
				a.setLocation(HaitiMobileClinicWebUtil.getRegistrationLocation(session));
				a.setSessionId(session.getId());
				a.setIpAddress((String)session.getAttribute(WebConstants.OPENMRS_CLIENT_IP_HTTPSESSION_ATTR));
				a.setActivityGroup((String)session.getAttribute(HaitiMobileClinicConstants.SESSION_TASK_GROUP));
				a.setExtraInfo(extraInfo);
			}
			Context.getService(HaitiMobileClinicService.class).saveUserActivity(a);
		}
		catch (Throwable t) {
			log.warn("Error logging activity: " + activity);
		}
	}
	
	/**
	 * Start a new activity group
	 */
	public static void startActivityGroup(HttpSession session) {
		session.setAttribute(HaitiMobileClinicConstants.SESSION_TASK_GROUP, UUID.randomUUID().toString());
	}
	
	/**
	 * End the current activity group
	 */
	public static void endActivityGroup(HttpSession session) {
		session.removeAttribute(HaitiMobileClinicConstants.SESSION_TASK_GROUP);
	}
}
