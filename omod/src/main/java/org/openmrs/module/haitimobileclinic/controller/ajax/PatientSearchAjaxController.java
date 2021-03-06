package org.openmrs.module.haitimobileclinic.controller.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicGlobalProperties;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicUtil;
import org.openmrs.module.haitimobileclinic.service.HaitiMobileClinicService;
import org.openmrs.module.haitimobileclinic.util.HaitiMobileClinicWebUtil;
import org.openmrs.module.haitimobileclinic.util.SortableValueMap;
import org.openmrs.module.haitimobileclinic.util.UserActivityLogger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class PatientSearchAjaxController {

	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping("/module/haitimobileclinic/ajax/patientIdentifierSearch.form")
	public void patientIdentifierSearch(@RequestParam("patientIdentifier") String patientIdentifier,
			ModelMap model, 
			HttpServletRequest request, 
			HttpServletResponse response,
			@RequestParam(value = "resultsCounter", required = false) Integer resultsCounter  ) 
	throws Exception {
	
		List<Patient> patientList = null; 
		
		if(StringUtils.isNotBlank(patientIdentifier)){			
			List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();			
			PatientIdentifierType preferredIdentifierType = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_PRIMARY_IDENTIFIER_TYPE();	
			if(preferredIdentifierType!=null){				
				identifierTypes.add(preferredIdentifierType);
				patientList = Context.getPatientService().getPatients(null, patientIdentifier, identifierTypes, true);
			}
			PatientIdentifierType dossierType = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_NUMERO_DOSSIER();	
			if(dossierType!=null){
				List<Patient> patientWithDossier = null; 
				identifierTypes = new ArrayList<PatientIdentifierType>();	
				identifierTypes.add(dossierType);			
				patientWithDossier = Context.getPatientService().getPatients(null, patientIdentifier, identifierTypes, true);
				if(patientWithDossier!=null && patientWithDossier.size()>0){
					patientWithDossier = Context.getPatientService().getPatients(null, patientIdentifier, identifierTypes, false);
				}
				if(patientWithDossier!=null){
					if(patientList==null){
						patientList=patientWithDossier;
					}else{
						patientList.addAll(patientWithDossier);
					}
				}
			}
			
		}
		
		
		response.setContentType("application/json");
    	response.setCharacterEncoding("UTF-8");
    	PrintWriter out = response.getWriter();
    	int counter=1;
    	// start the JSON
    	out.print("[");
    	if(patientList!=null){
	    	Iterator<Patient> i = patientList.iterator();    	
	    	while(i.hasNext()) {	    		
	    		out.print(HaitiMobileClinicUtil.convertPatientToJSON(i.next()));	  
	    		if(resultsCounter!=null && resultsCounter.intValue()<counter){
	    			break;
	    		}
	    		counter++;
	    		if (i.hasNext()) {
	    			out.print(",");
	    		}
	    	}
    	}
    	// close the JSON
		out.print("]");
		
	}
	@RequestMapping("/module/haitimobileclinic/ajax/patientSearch.form")
	public void patientSearch(@ModelAttribute("patientName") PersonName patientName, ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		long start = System.currentTimeMillis();
		log.debug("patientSearch start: " + start);
		List<Patient> patientList = Context.getService(HaitiMobileClinicService.class).exactSearch(patientName);
	
		HaitiMobileClinicUtil.convertPatientListToJson(patientList, response);
	}
	
	@RequestMapping("/module/haitimobileclinic/ajax/patientSoundexSearch.form")
	public void patientSoundexSearch(@ModelAttribute("patientName") PersonName patientName, ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
			
		List<Patient> patientList = Context.getService(HaitiMobileClinicService.class).exactSearch(patientName);	
		AdministrationService as = Context.getAdministrationService();
    	String processorName =  as.getGlobalProperty("namephonetics.givenNameStringEncoder");
    	
		String encodedFirstName = patientName.getGivenName();
		if(StringUtils.isNotBlank(encodedFirstName)){
			encodedFirstName = HaitiMobileClinicUtil.encodeName(encodedFirstName, processorName);
			if(log.isDebugEnabled()){
				log.debug(String.format("firstName=%s; encodedFirstName=", patientName.getGivenName(), encodedFirstName ));
			}
		}
		String encodedLastName = patientName.getFamilyName();
		if(StringUtils.isNotBlank(encodedLastName)){
			encodedLastName = HaitiMobileClinicUtil.encodeName(encodedLastName, processorName);
			if(log.isDebugEnabled()){
				log.debug(String.format("lastName=%s; encodedLastName=", patientName.getFamilyName(), encodedLastName ));
			}
		}
		
		List<Integer> personNameId =  Context.getService(HaitiMobileClinicService.class).getPhoneticsPersonId(encodedFirstName, encodedLastName);		
		List<Patient> patientPhonetics = null;
		if(personNameId!=null && personNameId.size()>0){			
			patientPhonetics = Context.getService(HaitiMobileClinicService.class).getPatientsByNameId(personNameId);			
		}
		if(patientList!=null && patientPhonetics!=null){
			patientPhonetics.removeAll(patientList);
			patientList.addAll(patientPhonetics);
			
		}else{
			patientList = patientPhonetics;
		}
		HaitiMobileClinicUtil.convertPatientListToJson(patientList, response);
		
	}
	

	
	
	@RequestMapping("/module/haitimobileclinic/ajax/identifierExactLookup.form")
	public void identifierExactLookup(@RequestParam("lookupInfo") String searchString
			, ModelMap model
			, HttpServletRequest request
			, HttpServletResponse response) throws Exception {
	
		
		List<Patient> patientList = null;
		if(StringUtils.isNotBlank(searchString)){
			patientList = Context.getPatientService().getPatients(null, searchString, null, true);						
		}
		HaitiMobileClinicUtil.convertPatientListToJson(patientList, response);
	}
	
	@RequestMapping("/module/haitimobileclinic/ajax/identifierPartialLookup.form")
	public void identifierPartialLookup(@RequestParam("lookupInfo") String searchString
			, ModelMap model
			, HttpServletRequest request
			, HttpServletResponse response) throws Exception {
	
		
		List<Patient> patientList = null;
		if(StringUtils.isNotBlank(searchString)){
			patientList = Context.getPatientService().getPatients(null, searchString, null, false);						
		}
		HaitiMobileClinicUtil.convertPatientListToJson(patientList, response);
	}
	
	@RequestMapping("/module/haitimobileclinic/ajax/removeFalseDuplicates.form")
	public void removeFalseDuplicate(
			@RequestParam(value = "patientId", required = true) Integer patientId
			, @RequestParam(value = "duplicateId", required = true) Integer duplicateId
			, ModelMap model
			, HttpServletRequest request
			, HttpServletResponse response) throws Exception {
	
				
		HaitiMobileClinicWebUtil.addPocFalseDuplicatePatient(patientId, duplicateId, request.getSession());
		HaitiMobileClinicWebUtil.addPocFalseDuplicatePatient(duplicateId, patientId, request.getSession());
	
		response.setContentType("text/html");
    	response.setCharacterEncoding("UTF-8");
    	PrintWriter out=null;
		try {
			out = response.getWriter();
			out.write("ok");
		} catch (IOException e) {
			log.error("failed to get the servlet response writer", e);
			throw new Exception(e);
		}
	}
	@RequestMapping("/module/haitimobileclinic/ajax/addPatientDuplicate.form")
	public void addPatientDuplicate(
			@RequestParam(value = "patientId", required = true) Integer patientId
			, @RequestParam(value = "duplicateId", required = true) Integer duplicateId
			, ModelMap model
			, HttpServletRequest request
			, HttpServletResponse response) throws Exception {
		
		HaitiMobileClinicWebUtil.addPocDuplicatePatient(patientId, duplicateId, request.getSession());
		HaitiMobileClinicWebUtil.addPocDuplicatePatient(duplicateId, patientId, request.getSession());
		
		response.setContentType("text/html");
    	response.setCharacterEncoding("UTF-8");
    	PrintWriter out=null;
		try {
			out = response.getWriter();
			out.write("ok");
		} catch (IOException e) {
			log.error("failed to get the servlet response writer", e);
			throw new Exception(e);
		}
	}
	
	@RequestMapping("/module/haitimobileclinic/ajax/patientNameLookup.form")
	public void patientNameLookup(@RequestParam("lookupInfo") String searchString
			, ModelMap model
			, HttpServletRequest request
			, HttpServletResponse response) throws Exception {
	
		
		List<Patient> patientList = null;
		if(StringUtils.isNotBlank(searchString)){
			String names[]= StringUtils.split(searchString);
			PersonName personName = new PersonName();
			if(names!=null && names.length>0){
				personName.setGivenName(names[0]);
				if(names.length>1){
					personName.setFamilyName(names[1]);
				}
				patientList = Context.getService(HaitiMobileClinicService.class).search(personName);
			}										
		}
		HaitiMobileClinicUtil.convertPatientListToJson(patientList, response);
	}
	
	@RequestMapping("/module/haitimobileclinic/ajax/patientLookup.form")
	public void patientLookup(@RequestParam("lookupInfo") String searchString
			, ModelMap model
			, HttpServletRequest request
			, HttpServletResponse response) throws Exception {
	
		String message = "Lookup patient <" + searchString + ">";
		UserActivityLogger.logActivity(request.getSession(), HaitiMobileClinicConstants.ACTIVITY_PATIENT_LOOKUP_STARTED, message);
		List<Patient> patientList = null;
		if(StringUtils.isNotBlank(searchString)){
			List<Patient> patientIds = Context.getPatientService().getPatients(null, searchString, null, false);
			if(patientIds!=null && patientIds.size()>0){
				patientList= patientIds; //(List<Patient>) ListUtils.sum(patientList, patientIds);
			}else{		
				/*
				patientIds = Context.getPatientService().getPatients(null, searchString, null, false); 
				if(patientIds!=null && patientIds.size()>0){
					patientList= patientIds; //(List<Patient>) ListUtils.sum(patientList, patientIds);
				}else{
				*/	
					String names[]= StringUtils.split(searchString);
					PersonName personName = new PersonName();
					if(names!=null && names.length>0){
						personName.setGivenName(names[0]);
						if(names.length>1){
							personName.setFamilyName(names[1]);
						}
						patientList = Context.getService(HaitiMobileClinicService.class).search(personName);
					}
				//}
			}
			
		}
		
		HaitiMobileClinicUtil.convertPatientListToJson(patientList, response);
		message = "Found " + (patientList == null ? 0 : patientList.size()) + " patients";
		UserActivityLogger.logActivity(request.getSession(), HaitiMobileClinicConstants.ACTIVITY_PATIENT_LOOKUP_COMPLETED, message);
	}
	
	@RequestMapping("/module/haitimobileclinic/ajax/patientNameSearch.form")
	public void patientFirstNameSearch(@RequestParam("patientInputName") String patientName, ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
			
		Set<String> patientNames= Context.getService(HaitiMobileClinicService.class).searchNames(patientName, "givenName");
		
		Set<String> familyNames= Context.getService(HaitiMobileClinicService.class).searchNames(patientName, "familyName");
		
		if(patientNames!=null){
			if(familyNames!=null){
				patientNames.addAll(familyNames);
			}
		}else if(familyNames!=null){
			patientNames=familyNames;
		}
		
		response.setContentType("application/json");
    	response.setCharacterEncoding("UTF-8");
    	PrintWriter out = response.getWriter();

    	// start the JSON
    	out.print("[");
    	
    	Iterator<String> i = patientNames.iterator();
    	
    	while(i.hasNext()) {    		
    		out.print(HaitiMobileClinicUtil.convertPatientNameToJSON(i.next()));
    	
    		if (i.hasNext()) {
    			out.print(",");
    		}
    	}
    	
    	// close the JSON
		out.print("]");
	}
	@RequestMapping("/module/haitimobileclinic/ajax/patientNameOccurrencesSearch.form")
	public void patientNameOccurenceSearch(
			  @RequestParam("searchFieldName") String patientName
			, @RequestParam(value = "resultsCounter", required = false) Integer resultsCounter  
			, ModelMap model
			, HttpServletRequest request
			, HttpServletResponse response) 
	throws Exception {
		
		Map<String, Integer> patientFirstNames = Context.getService(HaitiMobileClinicService.class).searchNamesByOccurence(patientName, "givenName");		
		Map<String, Integer> patientLastNames = Context.getService(HaitiMobileClinicService.class).searchNamesByOccurence(patientName, "familyName");
		
		SortableValueMap<String, Integer> nameOccurrences = null;
				
		if(patientFirstNames!=null && patientFirstNames.size()>0){
			nameOccurrences = new SortableValueMap<String, Integer>(patientFirstNames);
		}	
		if(patientLastNames!=null && patientLastNames.size()>0){
			if(nameOccurrences!=null){
				for(String keyName : patientLastNames.keySet()){
					Integer firstCounter = nameOccurrences.get(keyName);
					Integer lastNameCounter = patientLastNames.get(keyName);
					if(firstCounter!=null){
						nameOccurrences.put(keyName, firstCounter + lastNameCounter);
					}else{
						nameOccurrences.put(keyName, lastNameCounter);
					}
				}
			}else{
				nameOccurrences = new SortableValueMap<String, Integer>(patientLastNames);
			}
		}
		
		if(nameOccurrences!=null){
			nameOccurrences.sortByValue();
		}
		
		response.setContentType("application/json");
    	response.setCharacterEncoding("UTF-8");
    	PrintWriter out = response.getWriter();

    	// start the JSON
    	out.print("[");
    	if(nameOccurrences!=null && nameOccurrences.size()>0){
    		boolean firstRecord = true;
    		int nameCounter=1;
	    	for(String name : nameOccurrences.keySet()){
	    		if(resultsCounter!=null && resultsCounter.intValue()<nameCounter){
	    			break;
	    		}
	    		Integer counter = nameOccurrences.get(name);
	    		if(!firstRecord){
	    			out.print(",");
	    		}else{
	    			firstRecord=false;
	    		}
	    		out.print(HaitiMobileClinicUtil.convertPatientNameOccurrencesToJSON(name, counter));
	    		nameCounter++;
	    	}
    	}
    	
    	// close the JSON
		out.print("]");
	}
}
