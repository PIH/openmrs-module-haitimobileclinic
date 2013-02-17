package org.openmrs.module.haitimobileclinic.controller.workflow;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.module.haitimobileclinic.controller.AbstractPatientDetailsController;
import org.openmrs.module.haitimobileclinic.util.HaitiMobileClinicWebUtil;
import org.openmrs.module.haitimobileclinic.util.TaskProgress;
import org.openmrs.module.haitimobileclinic.util.UserActivityLogger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PatientRegistrationTaskController extends AbstractPatientDetailsController{
	
	@RequestMapping(value = "/module/haitimobileclinic/workflow/patientRegistrationTask.form", method = RequestMethod.GET)
	public ModelAndView showEnterPatientIdentifier(HttpSession session,  ModelMap model) {
		
		// confirm that we have an active session
		if (!HaitiMobileClinicWebUtil.confirmActiveHaitiMobileClinicSession(session)) {
			return new ModelAndView(HaitiMobileClinicConstants.WORKFLOW_FIRST_PAGE);
		}
		UserActivityLogger.logActivity(session, HaitiMobileClinicConstants.ACTIVITY_REGISTRATION_INITIATED);
				
		// reset the workflow because we are starting a new session
		HaitiMobileClinicWebUtil.resetHaitiMobileClinicWorkflow(session);
		
		return new ModelAndView("/module/haitimobileclinic/workflow/patientRegistrationTask");
	}
	
	@RequestMapping(value = "/module/haitimobileclinic/workflow/retrospectiveEntryTask.form", method = RequestMethod.GET)
	public ModelAndView scanPatientIdentifier(HttpSession session,  ModelMap model) {
		
		// confirm that we have an active session
		if (!HaitiMobileClinicWebUtil.confirmActiveHaitiMobileClinicSession(session)) {
			return new ModelAndView(HaitiMobileClinicConstants.WORKFLOW_FIRST_PAGE);
		}
		UserActivityLogger.logActivity(session, HaitiMobileClinicConstants.ACTIVITY_REGISTRATION_INITIATED);
				
		// reset the workflow because we are starting a new session
		HaitiMobileClinicWebUtil.resetHaitiMobileClinicWorkflow(session);
		
		TaskProgress taskProgress = new TaskProgress();
		taskProgress.setProgressBarImage(HaitiMobileClinicConstants.RETROSPECTIVE_PROGRESS_1_IMG);				
		HaitiMobileClinicWebUtil.setTaskProgress(session, taskProgress);
		model.addAttribute("taskProgress", taskProgress);
		
		model.put("nextTask", "primaryCareReceptionEncounter.form");	
		return new ModelAndView("/module/haitimobileclinic/workflow/patientRegistrationTask");
	}
	
	@RequestMapping(value = "/module/haitimobileclinic/workflow/mobileClinicTask.form", method = RequestMethod.GET)
	public ModelAndView mobileClinicTask(HttpSession session,  ModelMap model) {
		
		// confirm that we have an active session
		if (!HaitiMobileClinicWebUtil.confirmActiveHaitiMobileClinicSession(session)) {
			return new ModelAndView(HaitiMobileClinicConstants.WORKFLOW_FIRST_PAGE);
		}
		UserActivityLogger.logActivity(session, HaitiMobileClinicConstants.ACTIVITY_REGISTRATION_INITIATED);
				
		// reset the workflow because we are starting a new session
		HaitiMobileClinicWebUtil.resetHaitiMobileClinicWorkflow(session);
		
		TaskProgress taskProgress = new TaskProgress();
		taskProgress.setProgressBarImage(HaitiMobileClinicConstants.RETROSPECTIVE_PROGRESS_1_IMG);				
		HaitiMobileClinicWebUtil.setTaskProgress(session, taskProgress);
		model.addAttribute("taskProgress", taskProgress);
		
		model.put("nextTask", "mobileClinicReceptionEncounter.form");	
		return new ModelAndView("/module/haitimobileclinic/workflow/patientRegistrationTask");
	}
}
