package org.openmrs.module.haitimobileclinic.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ReferralsController {

	protected final Log log = LogFactory.getLog(getClass());

	@RequestMapping(value = "/module/haitimobileclinic/referralsHiv.form", method = RequestMethod.GET)
	public ModelAndView referralsHiv(@RequestParam(required = false) String country,
			ModelAndView mav) {
		Cohort cohort = Context.getPatientSetService().getAllPatients();
//		Cohort cohort = new Cohort("hivReferrals", "hivReferrals", arg2))
		mav.getModelMap().addAttribute("cohort", cohort);
		mav.getModelMap().addAttribute("memberIds", cohort.getMemberIds());
		return mav;
	}
}
