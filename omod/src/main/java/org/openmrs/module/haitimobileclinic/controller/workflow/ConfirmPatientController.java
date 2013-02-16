package org.openmrs.module.haitimobileclinic.controller.workflow;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicGlobalProperties;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicUtil;
import org.openmrs.module.haitimobileclinic.controller.AbstractPatientDetailsController;
import org.openmrs.module.haitimobileclinic.util.HaitiMobileClinicWebUtil;
import org.openmrs.module.haitimobileclinic.util.UserActivityLogger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ConfirmPatientController extends AbstractPatientDetailsController{
	
	@SuppressWarnings("unchecked")
    @RequestMapping("/module/haitimobileclinic/workflow/confirmPatient.form") 
	public ModelAndView showPatientInfo(HttpSession session, ModelMap model, 			
			@RequestParam(value= "patientIdentifier", required = false) String patientIdentifier, 
			@RequestParam(value= "patientId", required = false) String patientId) {
	
			
		if (!HaitiMobileClinicWebUtil.confirmActiveHaitiMobileClinicSession(session)) {
			return new ModelAndView(HaitiMobileClinicConstants.WORKFLOW_FIRST_PAGE);
		}
		
		String message = "Lookup patient with " + (patientIdentifier != null ? "identifier = " + patientIdentifier : "id = " + patientId);
		UserActivityLogger.logActivity(session, HaitiMobileClinicConstants.ACTIVITY_PATIENT_LOOKUP_INITIATED, message);
		
		Patient patient = null;
		if(StringUtils.isNotBlank(patientIdentifier)){
			List<Patient> patientList = null; 
			List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();			
			PatientIdentifierType preferredIdentifierType = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_PRIMARY_IDENTIFIER_TYPE();	
			if(preferredIdentifierType!=null){
				identifierTypes.add(preferredIdentifierType);
				patientList = Context.getPatientService().getPatients(null, patientIdentifier, identifierTypes, true);				
				if(patientList!=null && patientList.size()>0){
					patient = patientList.get(0);
				}
			}else{
				model.put("patientError", "Please set global property haitimobileclinic.primaryIdentifierType");
			}
			
		}
		if(StringUtils.isNotBlank(patientId)){
			try{
				patient = Context.getPatientService().getPatient(new Integer(patientId));
			}catch(Exception e){
				log.error("patient not found", e);
			}
		}		
		
		if (patient != null) {
			model.put("patient", patient);			
			// get the identifier we wish to display
			model.put("preferredIdentifier", HaitiMobileClinicUtil.getPreferredIdentifier(patient));
		}else{			
			model.put("patientError", "haitimobileclinic.noPatientFoundWithIdentifier");
		}
		
		return new ModelAndView("/module/haitimobileclinic/workflow/confirmPatient");
	}
}
