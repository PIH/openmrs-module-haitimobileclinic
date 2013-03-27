/**
 * 
 */
package org.openmrs.module.haitimobileclinic.controller.workflow;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.APIException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicGlobalProperties;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicUtil;
import org.openmrs.module.haitimobileclinic.controller.AbstractPatientDetailsController;
import org.openmrs.module.haitimobileclinic.task.EncounterTaskItemQuestion;
import org.openmrs.module.haitimobileclinic.util.HaitiMobileClinicWebUtil;
import org.openmrs.module.haitimobileclinic.util.TaskProgress;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/module/haitimobileclinic/workflow/mobileClinicReceptionEncounter.form")
public class MobileClinicReceptionEncounterController extends AbstractPatientDetailsController {
	
	@ModelAttribute("patient")
    public Patient getPatient(HttpSession session, 
    		@RequestParam(value= "patientIdentifier", required = false) String patientIdentifier, 
    		@RequestParam(value= "patientId", required = false) String patientId) {
			
		Patient patient = HaitiMobileClinicUtil.getPatientByAnId(patientIdentifier, patientId);
	
		if (patient == null) {
			throw new APIException("Invalid patient passed to MobileClinicReceptionEncounterController");			
		}
				
		for (PatientIdentifier pi : patient.getIdentifiers()) {
			pi.getIdentifier();
		}
				
		return patient;
    }

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showSelectPatient(
			@ModelAttribute("patient") Patient patient		
			, @RequestParam(value= "encounterId", required = false) String encounterId
			, @RequestParam(value= "createNew", required = false) String createNew
			, @RequestParam(value= "nextTask", required = false) String nextTask
			, HttpSession session
			, ModelMap model) {


		// confirm that we have an active session
		if (!HaitiMobileClinicWebUtil.confirmActiveHaitiMobileClinicSession(session)) {
			return new ModelAndView(HaitiMobileClinicConstants.WORKFLOW_FIRST_PAGE);
		} 
		model.addAttribute("registration_task", "mobileClinicReception");

		if (patient == null) {
			return new ModelAndView("redirect:/module/haitimobileclinic/workflow/mobileClinicReceptionTask.form");
		}

		Concept tbScreeningConcept = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_MOBILE_CLINIC_RECEPTION_TB_SCREENING_CONCEPT();

		Locale locale = Context.getLocale();

		String tbScreeningLabel = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_MOBILE_CLINIC_RECEPTION_TB_SCREENING_CONCEPT_LOCALIZED_LABEL(locale);

		model.addAttribute("preferredIdentifier", HaitiMobileClinicUtil.getPreferredIdentifier(patient));
		model.addAttribute("tbScreening", getSelectTypeQuestionFrom(tbScreeningConcept, tbScreeningLabel));
		Date encounterDate = new Date();

		Encounter editEncounter = null;
		if(StringUtils.isNotBlank(encounterId)){
			Integer editEncounterId = Integer.parseInt(encounterId);
			try{
				editEncounter = Context.getEncounterService().getEncounter(editEncounterId);
				if(editEncounter!=null){
					encounterDate = editEncounter.getEncounterDatetime();
				}
			}catch(Exception e){
				log.error("failed to retrieve encounter.", e);
			}
		}
		String currentTask = HaitiMobileClinicWebUtil.getRegistrationTask(session);

		if(StringUtils.equals(createNew, "true")){
			model.addAttribute("createNew", true);
		}

		if(StringUtils.isNotBlank(nextTask)){
			model.addAttribute("nextTask", nextTask);
		}else if(StringUtils.equalsIgnoreCase(currentTask, "retrospectiveEntry")){
			model.addAttribute("nextTask", "primaryCareVisitEncounter.form");
		}

		if(StringUtils.isNotBlank(currentTask)) {
			model.addAttribute("currentTask", currentTask);
		}

		model.addAttribute("encounterDate", HaitiMobileClinicUtil.clearTimeComponent(encounterDate));
		return new ModelAndView("/module/haitimobileclinic/workflow/mobileClinicReceptionEncounter");	
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processPayment(
			@ModelAttribute("patient") Patient patient		
			,@RequestParam("listOfObs") String obsList	
			,@RequestParam("hiddenEncounterYear") String encounterYear	
			,@RequestParam("hiddenEncounterMonth") String encounterMonth	
			,@RequestParam("hiddenEncounterDay") String encounterDay
			,@RequestParam(value="hiddenNextTask", required = false) String nextTask
			, HttpSession session			
			, ModelMap model) {
		if(StringUtils.isNotBlank(obsList)){			
			List<Obs> observations = HaitiMobileClinicUtil.parseObsList(obsList);
			Encounter encounter = null;
			if(observations!=null && observations.size()>0){				
				encounter =observations.get(0).getEncounter();
				//void existing observations
				Location registrationLocation = HaitiMobileClinicWebUtil.getRegistrationLocation(session);					
								
				EncounterType encounterType = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_MOBILE_CLINIC_RECEPTION_ENCOUNTER_TYPE();						
				Calendar encounterDate = Calendar.getInstance();
				
				// only process if we have values for all three fields
				if (StringUtils.isNotBlank(encounterYear) && StringUtils.isNotBlank(encounterMonth) && StringUtils.isNotBlank(encounterDay)) {					
					Integer year;
					Integer month;
					Integer day;
						
					try {
						year = Integer.valueOf(encounterYear);
						month = Integer.valueOf(encounterMonth);
						day = Integer.valueOf(encounterDay);
					}
					catch (Exception e) {
						throw new APIException("Unable to parse encounter date", e);
					}										
					
					// if everything is good, create the new encounter date and update it on the encounter we are creating					
					encounterDate.set(Calendar.YEAR, year);
					encounterDate.set(Calendar.MONTH, month - 1);  // IMPORTANT that we subtract one from the month here
					encounterDate.set(Calendar.DAY_OF_MONTH, day);				
				}				
				if(encounter==null){
					encounter = new Encounter();									
					encounter.setEncounterType(encounterType);
					encounter.setProvider(Context.getAuthenticatedUser().getPerson());
					encounter.setLocation(registrationLocation);
					encounter.setPatient(patient);				
				}
				for(Obs obs : observations){
					obs.setObsDatetime(encounterDate.getTime());
					encounter.addObs(obs);
				}
				encounter.setEncounterDatetime(encounterDate.getTime());
				
				HaitiMobileClinicWebUtil.addDataEntryDefaultsToEncounter(encounter, session);

				Encounter e = Context.getService(EncounterService.class).saveEncounter(encounter);

				TaskProgress taskProgress = HaitiMobileClinicWebUtil.getTaskProgress(session);
				if(taskProgress!=null){
					taskProgress.setPatientId(patient.getId());
					taskProgress.setProgressBarImage(HaitiMobileClinicConstants.RETROSPECTIVE_PROGRESS_3_IMG);			
					Map<String, Integer> completedTasks = taskProgress.getCompletedTasks();
					if(completedTasks == null){
						completedTasks = new HashMap<String, Integer>();
					}					
					completedTasks.put("receptionTask", new Integer(1));
					taskProgress.setCompletedTasks(completedTasks);
					HaitiMobileClinicWebUtil.setTaskProgress(session, taskProgress);
				}				
			}
			if(StringUtils.isNotBlank(nextTask)){
				return new ModelAndView("redirect:/module/haitimobileclinic/workflow/" + nextTask + "?patientId=" + patient.getPatientId(), model);
			}else{
				return new ModelAndView("redirect:/module/haitimobileclinic/workflow/patientDashboard.form?patientId=" + patient.getPatientId(), model);
			}
		
		}
		return new ModelAndView("redirect:/module/haitimobileclinic/workflow/mobileClinicReceptionTask.form");	
	}
	
	private EncounterTaskItemQuestion getSelectTypeQuestionFrom(Concept concept, String label) {
		EncounterTaskItemQuestion itemQuestion = new EncounterTaskItemQuestion();
		itemQuestion.setConcept(concept);
		if (label != null) {
			itemQuestion.setLabel(label);
		}
		itemQuestion.setType(EncounterTaskItemQuestion.Type.SELECT);
		itemQuestion.initializeAnswersFromConceptAnswers();
		return itemQuestion;
	}
}
