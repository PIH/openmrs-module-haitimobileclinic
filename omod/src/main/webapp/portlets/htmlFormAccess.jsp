<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="formAccess" uri="/WEB-INF/view/module/haitimobileclinic/resources/formAccessFromDashboard.tld"%>

<c:set var="personId" value="${model.personId}" />
<c:set var="patientId" value="${model.patientId}" />

<table cellspacing="0" cellpadding="2">
	<tr>
		<td>Mobile Clinic Consultations:</td>
		<td><formAccess:accessFromDashboard patientId='${patientId}' formId='1'/></td>
	</tr>
	<tr>
		<td><br /></td>
	</tr>
	<tr>
		<td>Static Clinic Enrollments:</td>
		<td><formAccess:accessFromDashboard patientId='${patientId}' formId='3'/></td>
	</tr>
	<tr>
		<td><br /></td>
	</tr>
	<tr>
		<td>TB results:</td>
		<td><formAccess:accessFromDashboard patientId='${patientId}' formId='4'/></td>
	</tr>
</table>
