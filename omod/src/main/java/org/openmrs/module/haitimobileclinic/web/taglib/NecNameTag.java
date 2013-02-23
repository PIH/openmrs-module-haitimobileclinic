package org.openmrs.module.haitimobileclinic.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.jfree.util.Log;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;

public class NecNameTag extends TagSupport {

	private static final long serialVersionUID = 4201389937632339892L;

	private Integer patientId = null;
	
	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}


	public NecNameTag() {
		// TODO Auto-generated constructor stub
	}

	public int doStartTag() throws JspException {
		
		JspWriter o = pageContext.getOut();
		try {
			Patient p = Context.getPatientService().getPatient(getPatientId());
			if (p == null) {
				o.write("(not found)");
			} else {
				o.write(p.getFamilyName() + ", " + p.getGivenName());
			}

		} catch (Exception e) {
			Log.error(e);
			try {
				o.write("(error: " + e.getMessage());
			} catch (IOException e1) {
			}
		}
		
		// reset the objects to null because taglibs are reused
		release();
		
		return SKIP_BODY;
	}
	
	public int doEndTag() {
		patientId = null;
		return EVAL_PAGE;
	}
}
