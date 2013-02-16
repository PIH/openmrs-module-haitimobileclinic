package org.openmrs.module.haitimobileclinic.controller.workflow;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.openmrs.Patient;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.module.haitimobileclinic.controller.AbstractPatientDetailsController;
import org.openmrs.module.haitimobileclinic.util.HaitiMobileClinicWebUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewDuplicatesTaskController extends
		AbstractPatientDetailsController {

	@RequestMapping(value = "/module/haitimobileclinic/workflow/viewDuplicatesTask.form", method = RequestMethod.GET)
	public ModelAndView showDuplicates(HttpSession session,  ModelMap model) {
		
		// confirm that we have an active session
		if (!HaitiMobileClinicWebUtil.confirmActiveHaitiMobileClinicSession(session)) {
			return new ModelAndView(HaitiMobileClinicConstants.WORKFLOW_FIRST_PAGE);
		}
		List<Patient> pocDuplicates= HaitiMobileClinicWebUtil.getDistinctDuplicatePatients(session);
		if(pocDuplicates!=null && pocDuplicates.size()>0){
			model.addAttribute("pocDuplicates", pocDuplicates);
		}
		
		
		return new ModelAndView("/module/haitimobileclinic/workflow/viewDuplicatesTask");
	}
}
