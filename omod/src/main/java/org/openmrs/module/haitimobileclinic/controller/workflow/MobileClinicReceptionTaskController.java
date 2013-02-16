package org.openmrs.module.haitimobileclinic.controller.workflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicGlobalProperties;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicUtil;
import org.openmrs.module.haitimobileclinic.controller.AbstractPatientDetailsController;
import org.openmrs.module.haitimobileclinic.task.EncounterTaskItem;
import org.openmrs.module.haitimobileclinic.task.EncounterTaskItemHandler;
import org.openmrs.module.haitimobileclinic.task.EncounterTaskItemQuestion;
import org.openmrs.module.haitimobileclinic.util.HaitiMobileClinicWebUtil;
import org.openmrs.module.haitimobileclinic.util.UserActivityLogger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MobileClinicReceptionTaskController extends AbstractPatientDetailsController{
	
	@RequestMapping(value = "/module/haitimobileclinic/workflow/mobileClinicReceptionTask.form", method = RequestMethod.GET)
	public ModelAndView showEnterPatientIdentifier(HttpSession session,  ModelMap model) {
		
		// confirm that we have an active session
		if (!HaitiMobileClinicWebUtil.confirmActiveHaitiMobileClinicSession(session)) {
			return new ModelAndView(HaitiMobileClinicConstants.WORKFLOW_FIRST_PAGE);
		}
				
		// reset the workflow because we are starting a new session
		HaitiMobileClinicWebUtil.resetHaitiMobileClinicWorkflow(session);
		
		return new ModelAndView("/module/haitimobileclinic/workflow/mobileClinicReceptionTask");
	}
	
	
	@RequestMapping(value = "/module/haitimobileclinic/workflow/mobileClinicReceptionCreateEncounterTaskItem.form", method = RequestMethod.GET)
	public ModelAndView showMobileClinicReceptionCreateEncounterTaskItem(HttpSession session, ModelMap model, @RequestParam("patientId") Integer patientId) {

		UserActivityLogger.startActivityGroup(session);
		UserActivityLogger.logActivity(session, HaitiMobileClinicConstants.ACTIVITY_MOBILE_CLINIC_RECEPTION_ENCOUNTER_STARTED);
		
		Patient patient = null;
		
		// fetch the patient
		patient = Context.getPatientService().getPatient(patientId);
		
		// make sure we have a patient
		if (patient == null) {
			throw new APIException("No valid patient passed to handleSubmitMobileClinicReceptionCreateEncounterTaskItem");
		}
		
		// add the patient and preferred identifier to the map
		model.addAttribute("patient", patient);
		model.addAttribute("preferredIdentifier", HaitiMobileClinicUtil.getPreferredIdentifier(patient));
		
		// Initialize the EncounterTaskItem
		EncounterTaskItem taskItem = initializeEncounterTaskItem(session);
		
		// call the EncounterTaskItem renderer
		return new EncounterTaskItemHandler().render(taskItem, model);
	}
	
	@RequestMapping(value = "/module/haitimobileclinic/workflow/mobileClinicReceptionCreateEncounterTaskItem.form", method = RequestMethod.POST)
	public ModelAndView handleSubmitmobileClinicReceptionCreateEncounterTaskItem(
			HttpSession session, 
			HttpServletRequest request, 
			ModelMap model, @RequestParam("patientId") Integer patientId) {

		Patient patient = null;
		
		// fetch the patient
		patient = Context.getPatientService().getPatient(patientId);
		
		// make sure we have a patient
		if (patient == null) {
			throw new APIException("No valid patient passed to handleSubmitMobileClinicReceptionCreateEncounterTaskItem");
		}
		
		// add the patient and preferred identifier to the map
		model.addAttribute("patient", patient);
		model.addAttribute("preferredIdentifier", HaitiMobileClinicUtil.getPreferredIdentifier(patient));
		
		// Initialize the EncounterTaskItem
		EncounterTaskItem taskItem = initializeEncounterTaskItem(session);
		
		// call the EncounterTaskItem renderer
		ModelAndView ret = new EncounterTaskItemHandler().handleSubmit(taskItem, patient, request, model);
		
		UserActivityLogger.logActivity(session, HaitiMobileClinicConstants.ACTIVITY_MOBILE_CLINIC_RECEPTION_ENCOUNTER_COMPLETED);
		UserActivityLogger.endActivityGroup(session);
		
		return ret;
	}
	
	/** 
	 * Utility functions
	 */
	private EncounterTaskItem initializeEncounterTaskItem(HttpSession session)  {
		
		EncounterTaskItem taskItem = new EncounterTaskItem();
		
		// set the mode to "create"
		taskItem.setMode(EncounterTaskItem.Mode.CREATE);
		
		taskItem.setEncounterDate(new Date());
		taskItem.setEncounterDateEditable(false);
		taskItem.setEncounterProvider(Context.getAuthenticatedUser().getPerson());
		taskItem.setEncounterProviderEditable(false);
		taskItem.setEncounterLocationEditable(false);
		
		EncounterType encounterType = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_MOBILE_CLINIC_RECEPTION_ENCOUNTER_TYPE();
		taskItem.setEncounterType(encounterType);
		taskItem.setEncounterTypeEditable(false);
		taskItem.setEncounterLocation(HaitiMobileClinicWebUtil.getRegistrationLocation(session));
		
		List<EncounterTaskItemQuestion> questions = new ArrayList<EncounterTaskItemQuestion>();
		
		EncounterTaskItemQuestion payment = new EncounterTaskItemQuestion();
		payment.setConcept(HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_PRIMARY_CARE_RECEPTION_PAYMENT_CONCEPT());
		String label = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_PRIMARY_CARE_RECEPTION_PAYMENT_CONCEPT_LOCALIZED_LABEL(Context.getLocale());
		if (label != null) {
			payment.setLabel(label);
		}
		payment.setType(EncounterTaskItemQuestion.Type.SELECT);
		payment.initializeAnswersFromConceptAnswers();
		questions.add(payment);
		
		EncounterTaskItemQuestion receipt = new EncounterTaskItemQuestion();
		receipt.setConcept(HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_PRIMARY_CARE_RECEPTION_RECEIPT_NUMBER_CONCEPT());
		label = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_PRIMARY_CARE_RECEPTION_RECEIPT_NUMBER_CONCEPT_LOCALIZED_LABEL(Context.getLocale());
		if (label != null) {
			receipt.setLabel(label);
		}
		receipt.setType(EncounterTaskItemQuestion.Type.TEXT);
		questions.add(receipt);
				
		taskItem.setQuestions(questions);
		
		taskItem.setConfirmDetails(true);
		taskItem.setBackUrl("/module/haitimobileclinic/workflow/mobileClinicReceptionTask.form");
		taskItem.setSuccessUrl("/module/haitimobileclinic/workflow/mobileClinicReceptionDossierNumber.form");
		
		return taskItem;
	}
			
}
