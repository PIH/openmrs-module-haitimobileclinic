package org.openmrs.module.haitimobileclinic.controller.workflow;

import javax.servlet.http.HttpSession;

import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.module.haitimobileclinic.controller.AbstractPatientDetailsController;
import org.openmrs.module.haitimobileclinic.util.HaitiMobileClinicWebUtil;
import org.openmrs.module.haitimobileclinic.util.UserActivityLogger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/module/haitimobileclinic/workflow/patientLookupTask.form")
public class PatientLookupTaskController extends AbstractPatientDetailsController{
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showEnterPatientIdentifier(HttpSession session,  ModelMap model) {
		
		UserActivityLogger.logActivity(session, HaitiMobileClinicConstants.ACTIVITY_PATIENT_LOOKUP_INITIATED);
		// confirm that we have an active session
		if (!HaitiMobileClinicWebUtil.confirmActiveHaitiMobileClinicSession(session)) {
			return new ModelAndView(HaitiMobileClinicConstants.WORKFLOW_FIRST_PAGE);
		}
				
		// reset the workflow because we are starting a new session
		HaitiMobileClinicWebUtil.resetHaitiMobileClinicWorkflow(session);
		
		return new ModelAndView("/module/haitimobileclinic/workflow/patientLookupTask");
	}
}
