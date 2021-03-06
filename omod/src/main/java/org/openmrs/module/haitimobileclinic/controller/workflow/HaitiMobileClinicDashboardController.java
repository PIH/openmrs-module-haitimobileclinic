package org.openmrs.module.haitimobileclinic.controller.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicGlobalProperties;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicUtil;
import org.openmrs.module.haitimobileclinic.controller.AbstractPatientDetailsController;
import org.openmrs.module.haitimobileclinic.service.HaitiMobileClinicService;
import org.openmrs.module.haitimobileclinic.util.DuplicatePatient;
import org.openmrs.module.haitimobileclinic.util.IDCardInfo;
import org.openmrs.module.haitimobileclinic.util.HaitiMobileClinicWebUtil;
import org.openmrs.module.haitimobileclinic.util.TaskProgress;
import org.openmrs.module.haitimobileclinic.util.UserActivityLogger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/module/haitimobileclinic/workflow/patientDashboard.form") 
public class HaitiMobileClinicDashboardController extends AbstractPatientDetailsController{
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showPatientInfo(HttpSession session, ModelMap model, 
			@RequestParam(value= "patientIdentifier", required = false) String patientIdentifier, 
			@RequestParam(value= "patientId", required = false) String patientId, 
			@RequestParam(value= "createEncounter", required = false) String encounterName,
			@RequestParam(value= "createId", required = false) String createId,
			@RequestParam(value= "scanIdCard", required = false) String scanIdCard,
			@RequestParam(value= "cardPrinted", required = false) String cardPrinted,
			@RequestParam(value= "nextTask", required = false) String nextTask
			) {	
		
			
		if (!HaitiMobileClinicWebUtil.confirmActiveHaitiMobileClinicSession(session)) {
			return new ModelAndView(HaitiMobileClinicConstants.WORKFLOW_FIRST_PAGE);
		}
				
		Patient patient = null;
		if(StringUtils.isNotBlank(patientIdentifier)){
			List<Patient> patients = Context.getPatientService().getPatients(null, patientIdentifier, null, true);
			if(patients!=null && patients.size()>0){
				patient = patients.get(0);
			}
		}
		if(StringUtils.isNotBlank(patientId)){
			try{
				patient = Context.getPatientService().getPatient(new Integer(patientId));
			}catch(Exception e){
				log.error("patient not found", e);
			}
		}		
		Location registrationLocation = HaitiMobileClinicWebUtil.getRegistrationLocation(session);	
		PatientIdentifier patientPreferredIdentifier = null;
		if (patient != null) {
			model.addAttribute("patient", patient);			
				
			PatientIdentifierType zlIdentifierType = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_PRIMARY_IDENTIFIER_TYPE();	
			if(zlIdentifierType!=null){
				patientPreferredIdentifier = HaitiMobileClinicUtil.getPreferredIdentifier(patient);				
				if(patientPreferredIdentifier==null ||
					(patientPreferredIdentifier!=null && (patientPreferredIdentifier.getIdentifierType().getId().compareTo(zlIdentifierType.getId())!=0))){
				 
					 patientPreferredIdentifier = new PatientIdentifier(null, zlIdentifierType, registrationLocation);
					 if(StringUtils.equalsIgnoreCase(createId, "true")){	
						 patientPreferredIdentifier.setIdentifier(HaitiMobileClinicUtil.assignIdentifier(zlIdentifierType)) ;
						 patientPreferredIdentifier.setPreferred(true);
						 patient.addIdentifier(patientPreferredIdentifier);	
						 String message = "Identifier: " + patientPreferredIdentifier.getIdentifier();
						 UserActivityLogger.logActivity(session, HaitiMobileClinicConstants.ACTIVITY_REGISTRATION_NEW_ZL_ID, message);
						 try{
							 Context.getPatientService().savePatient(patient);							 							
						 }
						 catch(Exception e){
							 log.error("failed to save patient", e);
						 }
					 }
				}
				model.addAttribute("preferredIdentifier", patientPreferredIdentifier);
				
			}
				 
		
			
			PatientIdentifierType identifierType = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_NUMERO_DOSSIER();
			if(identifierType!=null){				
				PatientIdentifier dossierIdentifier = HaitiMobileClinicUtil.getNumeroDossier(patient, registrationLocation);
				if(dossierIdentifier==null){
					dossierIdentifier = new PatientIdentifier("",identifierType, registrationLocation);
				}
				model.addAttribute(HaitiMobileClinicConstants.NUMERO_DOSSIER, dossierIdentifier);
			}
			
//			identifierType = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_DENTAL_DOSSIER();
//			if(identifierType!=null){				
//				PatientIdentifier dossierIdentifier = HaitiMobileClinicUtil.getDentalDossier(patient, registrationLocation);
//				if(dossierIdentifier==null){
//					dossierIdentifier = new PatientIdentifier("",identifierType, registrationLocation);
//				}
//				model.addAttribute(HaitiMobileClinicConstants.DENTAL_DOSSIER, dossierIdentifier);
//			}

			identifierType = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_PRIMARY_IDENTIFIER_TYPE();
			if(identifierType!=null){				
				PatientIdentifier dossierIdentifier = HaitiMobileClinicUtil.getPrimaryIdentifier(patient); //, registrationLocation);
				if(dossierIdentifier==null){
					dossierIdentifier = new PatientIdentifier("",identifierType, registrationLocation);
				}
				model.addAttribute(HaitiMobileClinicConstants.MOBILE_CLINIC_DOSSIER, dossierIdentifier);
			}
			Encounter registrationEncounter = null;
			if (StringUtils.isNotBlank(encounterName)){			
				EncounterType encounterType = HaitiMobileClinicUtil.findEncounterType(Context.getAdministrationService().getGlobalProperty(HaitiMobileClinicConstants.MODULE_NAME + "." + encounterName));				
				if (encounterType == null) {
					log.error( "encounterName=" + encounterName + " does not exist");
				}
				else {
					registrationEncounter = Context.getService(HaitiMobileClinicService.class).registerPatient(
							  patient
							, Context.getAuthenticatedUser().getPerson()
							, encounterType
							, registrationLocation);
					TaskProgress taskProgress = HaitiMobileClinicWebUtil.getTaskProgress(session);
					if(taskProgress!=null){
						taskProgress.setPatientId(patient.getId());
						taskProgress.setProgressBarImage(HaitiMobileClinicConstants.RETROSPECTIVE_PROGRESS_2_IMG);			
						Map<String, Integer> completedTasks = new HashMap<String, Integer>();
						completedTasks.put("registrationTask", new Integer(1));
						taskProgress.setCompletedTasks(completedTasks);
						HaitiMobileClinicWebUtil.setTaskProgress(session, taskProgress);
						model.addAttribute("taskProgress", taskProgress);
					}
					
				}
			}
			
			EncounterType registrationEncounterType = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_PATIENT_REGISTRATION_ENCOUNTER_TYPE();
			Boolean cardPrintedStatus = null;
			if(StringUtils.equalsIgnoreCase(cardPrinted, "true")){
				cardPrintedStatus = new Boolean(true);				
			}else if (StringUtils.equalsIgnoreCase(cardPrinted, "false")){
				cardPrintedStatus = new Boolean(false);				
			}	
			IDCardInfo cardInfo = null;
			if(patientPreferredIdentifier!=null){
				cardInfo = HaitiMobileClinicWebUtil.updatePrintingCardStatus(patient, registrationEncounterType, registrationEncounter, registrationLocation, cardPrintedStatus, null);
			}
			model.addAttribute("cardInfo", cardInfo);
			
			List<Encounter> encounters = new Vector<Encounter>();
			List<Encounter> encs = Context.getEncounterService().getEncountersByPatient(patient);
			if (encs != null && encs.size() > 0)
				encounters.addAll(encs);
			model.addAttribute("encounters", encounters);
			model.addAttribute("editURLs", HaitiMobileClinicWebUtil.getEncounterEditURLs());
			model.addAttribute("encounterLocale", HaitiMobileClinicWebUtil.getEncounterTypeLocale());
			if(StringUtils.equalsIgnoreCase(scanIdCard, "true")){
				model.addAttribute("scanIdCard", "true");
			}
		}
		String task = HaitiMobileClinicWebUtil.getRegistrationTask(session);
		if (StringUtils.isNotBlank(task)) {
			model.addAttribute("task", task);
		}
		
		List<DuplicatePatient> duplicatePatients = Context.getService(HaitiMobileClinicService.class).getDuplicatePatients(patient);
		if(duplicatePatients!=null && duplicatePatients.size()>0){				
			Set<Integer> pocFalseDuplicatesSet = HaitiMobileClinicWebUtil.getPOCFalsePatientDuplicates(patient);
			Set<Integer> pocDuplicatesSet = HaitiMobileClinicWebUtil.getPOCPatientDuplicates(patient);
			if((pocFalseDuplicatesSet!=null && pocFalseDuplicatesSet.size()>0) || 
					(pocDuplicatesSet!=null && pocDuplicatesSet.size()>0)){
				List<DuplicatePatient> modelDuplicates = new ArrayList<DuplicatePatient>();
				for(DuplicatePatient duplicatePatient : duplicatePatients){
					boolean addPatient = true;
					if(pocFalseDuplicatesSet!=null && pocFalseDuplicatesSet.contains(duplicatePatient.getPatientId())){
						addPatient = false;						
					}else if(pocDuplicatesSet!=null && pocDuplicatesSet.contains(duplicatePatient.getPatientId())){
						addPatient = false;						
					}					
					if(addPatient){
						modelDuplicates.add(duplicatePatient);
					}
				}
				model.addAttribute("duplicatePatients", modelDuplicates);
			}else{
				model.addAttribute("duplicatePatients", duplicatePatients);
			}
		}	
		if(StringUtils.isNotBlank(nextTask)){
			model.addAttribute("nextTask", nextTask);
		}
		return new ModelAndView("/module/haitimobileclinic/workflow/patientDashboard");
	}
	
//	@RequestMapping(params= "printIDCard", method = RequestMethod.POST)
//	public ModelAndView printIDCard(@ModelAttribute("patient") Patient patient, BindingResult result, HttpSession session , ModelMap model){
//		
//		if(patient!=null){			
//			// fetch the patient
//			patient = Context.getPatientService().getPatient((Integer) patient.getId());			
//			Location registrationLocation = HaitiMobileClinicWebUtil.getRegistrationLocation(session);
//			if(registrationLocation==null){
//				return new ModelAndView(HaitiMobileClinicConstants.WORKFLOW_FIRST_PAGE);
//			}
//			EncounterType registrationEncounterType = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_PATIENT_REGISTRATION_ENCOUNTER_TYPE();			
//			Encounter registrationEncounter = Context.getService(HaitiMobileClinicService.class).registerPatient(
//					  patient
//					, Context.getAuthenticatedUser().getPerson()
//					, registrationEncounterType
//					, registrationLocation);
//			boolean cardPrintedStatus = false;
//			boolean printingSuccessful = Context.getService(HaitiMobileClinicService.class).printIDCard(patient, registrationLocation);
//			if (printingSuccessful) {
//				UserActivityLogger.logActivity(session, HaitiMobileClinicConstants.ACTIVITY_ID_CARD_PRINTING_SUCCESSFUL);
//				cardPrintedStatus =true;
//			}
//			else {
//				UserActivityLogger.logActivity(session, HaitiMobileClinicConstants.ACTIVITY_ID_CARD_PRINTING_FAILED);
//				// TODO: Decide what else to do if this fails
//			}
//			IDCardInfo cardInfo = HaitiMobileClinicWebUtil.updatePrintingCardStatus(patient, registrationEncounterType, registrationEncounter, registrationLocation, new Boolean(cardPrintedStatus), null);
//			model.addAttribute("cardInfo", cardInfo);			
//			return new ModelAndView("redirect:/module/haitimobileclinic/workflow/patientDashboard.form?scanIdCard=true&patientId="+ patient.getId());
//		}else{
//			return new ModelAndView("redirect:/module/haitimobileclinic/workflow/patientRegistrationTask.form");
//		}
//		
//	}
//	
//	@RequestMapping(params= "printDossierLabel", method = RequestMethod.POST)
//	public ModelAndView printDossierLabel(@ModelAttribute("patient") Patient patient, BindingResult result, HttpSession session){
//		if (patient!=null) {
//			patient = Context.getPatientService().getPatient(new Integer(patient.getId()));
//			Location location = HaitiMobileClinicWebUtil.getRegistrationLocation(session);
//			boolean printingSuccessful = Context.getService(HaitiMobileClinicService.class).printRegistrationLabel(patient, location, 1);
//			if (printingSuccessful) {
//				UserActivityLogger.logActivity(session, HaitiMobileClinicConstants.ACTIVITY_DOSSIER_LABEL_PRINTING_SUCCESSFUL);
//			}
//			else {
//				UserActivityLogger.logActivity(session, HaitiMobileClinicConstants.ACTIVITY_DOSSIER_LABEL_PRINTING_FAILED);
//				// TODO: Decide what else to do if this fails
//			}
//			// print the second label which goes on the back of the ID card
//			printingSuccessful = Context.getService(HaitiMobileClinicService.class).printIDCardLabel(patient, location);
//			if (printingSuccessful) {
//				UserActivityLogger.logActivity(session, HaitiMobileClinicConstants.ACTIVITY_ID_CARD_LABEL_PRINTING_SUCCESSFUL);
//			}
//			else {
//				UserActivityLogger.logActivity(session, HaitiMobileClinicConstants.ACTIVITY_ID_CARD_LABEL_PRINTING_FAILED);
//				// TODO: Decide what else to do if this fails
//			}
//			return new ModelAndView("redirect:/module/haitimobileclinic/workflow/patientDashboard.form?patientId="+ patient.getId());							
//		}
//		else{
//			return new ModelAndView("redirect:/module/haitimobileclinic/workflow/patientRegistrationTask.form");
//		}
//		
//	}
//	@RequestMapping(params= "printDentalDossierLabel", method = RequestMethod.POST)
//	public ModelAndView printDentalDossierLabel(@ModelAttribute("patient") Patient patient, BindingResult result, HttpSession session){
//		if (patient!=null) {
//			patient = Context.getPatientService().getPatient(new Integer(patient.getId()));
//			Location location = HaitiMobileClinicWebUtil.getRegistrationLocation(session);
//			Context.getService(HaitiMobileClinicService.class).printIDCardLabel(patient, location);
//			return new ModelAndView("redirect:/module/haitimobileclinic/workflow/patientDashboard.form?patientId="+ patient.getId());							
//		}
//		else{
//			return new ModelAndView("redirect:/module/haitimobileclinic/workflow/patientRegistrationTask.form");
//		}
//		
//	}
	
}
