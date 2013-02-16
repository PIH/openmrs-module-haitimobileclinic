/**
 * 
 */
package org.openmrs.module.haitimobileclinic.controller.workflow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicGlobalProperties;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicUtil;
import org.openmrs.module.haitimobileclinic.controller.AbstractPatientDetailsController;
import org.openmrs.module.haitimobileclinic.task.EncounterTaskItemQuestion;
import org.openmrs.module.haitimobileclinic.util.POCObservation;
import org.openmrs.module.haitimobileclinic.util.HaitiMobileClinicWebUtil;
import org.openmrs.module.haitimobileclinic.util.TaskProgress;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author cospih
 *
 */
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

		Map<Concept, String> conceptsNameByType =
				mappingConceptNamesByType(tbScreeningConcept, tbScreeningLabel);

//		Map<String, String> paymentAmounts = createMapWithPaymentAmounts();

		model.addAttribute("preferredIdentifier", HaitiMobileClinicUtil.getPreferredIdentifier(patient));
		model.addAttribute("tbScreening", getSelectTypeQuestionFrom(tbScreeningConcept, tbScreeningLabel));
//		model.addAttribute("paymentAmount", getSelectTypeQuestionsWithAnswersFrom(paymentAmountConcept, paymentAmountLabel, paymentAmounts));
//		model.addAttribute("receipt", getTextTypeQuestionFrom(receiptConcept, receiptLabel));

		Location registrationLocation = HaitiMobileClinicWebUtil.getRegistrationLocation(session);
		EncounterType encounterType = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_MOBILE_CLINIC_RECEPTION_ENCOUNTER_TYPE();	
		Date encounterDate = new Date();
		List<Obs> obs = new ArrayList<Obs>();

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
		if(!StringUtils.equalsIgnoreCase(currentTask, "retrospectiveEntry") || (editEncounter!=null)){
//			List<List<Obs>> paymentGroups = HaitiMobileClinicWebUtil.getPatientGroupPayment(patient, encounterType,
//					editEncounter, registrationLocation, encounterDate);
//
//			if(paymentGroups!=null && !paymentGroups.isEmpty()){
//				List<List<POCObservation>> pocPaymentGroups = new ArrayList<List<POCObservation>>();
//				for(List<Obs> paymentGroup : paymentGroups){
//					List<POCObservation> pocObs = new ArrayList<POCObservation>();
//					for(Obs ob: paymentGroup){
//						POCObservation pocObservation = buildPOCObservation(ob, paymentAmounts, paymentAmountConcept);
//						pocObservation.setConceptName(conceptsNameByType.get(ob.getConcept()));
//						pocObs.add(pocObservation);
//					}
//					pocPaymentGroups.add(pocObs);
//				}
//				model.addAttribute("pocPaymentGroups", pocPaymentGroups);
//			}
		}

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
		log.error("MobileClinicReception: substart " +  obsList);
		if(StringUtils.isNotBlank(obsList)){			
			Person user =Context.getPersonService().getPerson(patient.getPersonId());
			List<Obs> observations = HaitiMobileClinicUtil.parseObsList(obsList);
			log.error("MobileClinicReception: submit: " + observations);
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
					log.error("MobileClinicReception: obs: " + obs);

					obs.setObsDatetime(encounterDate.getTime());
					encounter.addObs(obs);
				}
				encounter.setEncounterDatetime(encounterDate.getTime());
				
				addLocationToEncounter(encounter, session);
				addChwNamesToEncounter(encounter, session);
				addNecNameToEncounter(encounter, session);

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
	
	private void addNecNameToEncounter(Encounter encounter, HttpSession session) {
		 Obs o = new Obs();
		 o.setValueText(session.getAttribute("sessionNecName") != null ? session.getAttribute("sessionNecName").toString() : "<not set>");
		 o.setConcept(Context.getConceptService().getConcept(6769));
		 encounter.addObs(o);
	}

	private void addChwNamesToEncounter(Encounter encounter, HttpSession session) {
		 Obs o = new Obs();
		 o.setValueText(session.getAttribute("sessionChwNames") != null ? session.getAttribute("sessionChwNames").toString() : "<not set>");
		 o.setConcept(Context.getConceptService().getConcept(6768));
		 encounter.addObs(o);
	}

	private void addLocationToEncounter(Encounter encounter, HttpSession session) {
		 Obs o = new Obs();
		 o.setValueText(session.getAttribute("sessionLocation") != null ? session.getAttribute("sessionLocation").toString() : "<not set>");
		 o.setConcept(Context.getConceptService().getConcept(6767));
		 encounter.addObs(o);
	}

	private Map<Concept, String> mappingConceptNamesByType(Concept tbScreeningConcept, String tbScreeningLabel) {
        Map<Concept, String> labelsByConcept = new HashMap<Concept, String>();
        labelsByConcept.put(tbScreeningConcept,tbScreeningLabel);
        return labelsByConcept;
    }
	
//	private LinkedHashMap<String, String> createMapWithPaymentAmounts() {
//		LinkedHashMap<String, String> paymentAmounts = new LinkedHashMap<String, String>();
//		paymentAmounts.put("50 Gourdes", "50");
//		paymentAmounts.put("100 Gourdes", "100");
//		paymentAmounts.put("Exonere", "0");
//		paymentAmounts.put("Fonkoze", "0");
//		return paymentAmounts;
//	}
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
	private EncounterTaskItemQuestion getTextTypeQuestionFrom(Concept concept, String receiptLabel) {
		EncounterTaskItemQuestion receipt = new EncounterTaskItemQuestion();
		receipt.setConcept(concept);
		if (receiptLabel != null) {
			receipt.setLabel(receiptLabel);
		}
		receipt.setType(EncounterTaskItemQuestion.Type.TEXT);
		return receipt;
	}
	 private POCObservation buildPOCObservation(Obs ob, Map<String, String> paymentAmounts, Concept paymentAmountConcept) {
	        POCObservation pocObs = new POCObservation();
	        pocObs.setObsId(ob.getObsId());
	        if (ob.getConcept().getDatatype().isCoded()) {
	            Concept codedObs= ob.getValueCoded();
	            pocObs.setType(POCObservation.CODED);
	            pocObs.setId(codedObs.getId());
	            pocObs.setLabel(codedObs.getDisplayString());
	        }
	        else if (ob.getConcept().getDatatype().isText()) {
	            pocObs.setType(POCObservation.NONCODED);
	            pocObs.setId(new Integer(0));
	            pocObs.setLabel(ob.getValueText());
	        }
	        else if (ob.getConcept().getDatatype().isNumeric()) {
	            pocObs.setType(POCObservation.NUMERIC);
	            pocObs.setId(ob.getValueNumeric().intValue());
	            if (ob.getConcept().equals(paymentAmountConcept)) {
	                pocObs.setLabel(getLabelFromMap(paymentAmounts, pocObs.getId().toString()));
	            }
	        }
	        pocObs.setConceptId(ob.getConcept().getConceptId());
	        return pocObs;
	    }

	private EncounterTaskItemQuestion getSelectTypeQuestionsWithAnswersFrom(Concept paymentAmountConcept, String label, Map<String, String> paymentAmounts) {
		EncounterTaskItemQuestion paymentAmount = new EncounterTaskItemQuestion();
		paymentAmount.setConcept(paymentAmountConcept);
		if (label != null) {
			paymentAmount.setLabel(label);
		}
		paymentAmount.setType(EncounterTaskItemQuestion.Type.SELECT);
		paymentAmount.setAnswers(paymentAmounts);
		return paymentAmount;
	}

	private String getLabelFromMap(Map<String,String> labelToValueMap, String value) {
		for (Map.Entry<String, String> entry : labelToValueMap.entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}
		throw new IllegalArgumentException("Cannot find " + value + " in " + labelToValueMap);
	}
}
