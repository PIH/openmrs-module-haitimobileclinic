package org.openmrs.module.haitimobileclinic.controller.workflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicGlobalProperties;
import org.openmrs.module.haitimobileclinic.util.HaitiMobileClinicWebUtil;
import org.openmrs.module.haitimobileclinic.util.TaskProgress;
import org.openmrs.propertyeditor.LocationEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SelectLocationAndServiceController {
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@ModelAttribute("locations")
	public List<Location> getLocations() {
		List<Location> ret = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_REGISTRATION_LOCATIONS();
		if (ret.isEmpty()) {
			ret = Context.getLocationService().getAllLocations();
		}
		return ret;
	}
	
	@RequestMapping(value = "/module/haitimobileclinic/workflow/selectLocationAndService.form", method = RequestMethod.GET)
	public ModelAndView showSetEncounterTypeAndLocation(Model model, HttpSession session) {
		HaitiMobileClinicWebUtil.setTimeout(session);
		
		List<String> tasks = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_SUPPORTED_TASKS();
		for (Iterator<String> i = tasks.iterator(); i.hasNext();) {
			String task = i.next();
			if (!Context.hasPrivilege("Patient Registration Task - " + task)) {
				i.remove();
			}
		}
		
		model.addAttribute("tasks", tasks);
		return new ModelAndView("/module/haitimobileclinic/workflow/selectLocationAndService");
	}
	
	@RequestMapping(value = "/module/haitimobileclinic/workflow/selectLocationAndService.form", method = RequestMethod.POST)
	public String processSetEncounterTypeAndLocation(
			HttpSession session, HttpServletRequest request	
			, @RequestParam("location") Location location
			, @RequestParam("task") String task){
		
		HaitiMobileClinicWebUtil.setRegistrationLocation(session, location);
		HaitiMobileClinicWebUtil.setRegistrationTask(session, task);

		if (StringUtils.isNotBlank(task)) {	
			if(StringUtils.equalsIgnoreCase(task, HaitiMobileClinicConstants.RETROSPECTIVE_TASK)){
				TaskProgress taskProgress = new TaskProgress();
				taskProgress.setProgressBarImage(HaitiMobileClinicConstants.RETROSPECTIVE_PROGRESS_1_IMG);				
				HaitiMobileClinicWebUtil.setTaskProgress(session, taskProgress);
			}else{
				session.removeAttribute(HaitiMobileClinicConstants.SESSION_TASK_PROGRESS);
			}
			return "redirect:/module/haitimobileclinic/workflow/"+task + "Task.form";
		}
		else {
			return "/module/haitimobileclinic/workflow/selectLocationAndService";
		}
	}
}
