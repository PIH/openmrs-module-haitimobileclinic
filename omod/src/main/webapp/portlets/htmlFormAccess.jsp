<%@ include file="/WEB-INF/template/include.jsp"%>

<c:set var="personId" value="${model.personId}" />
<c:set var="patientId" value="${model.patientId}" />

<%
String abc = "ABC";
%>

${abc}

<table cellspacing="0" cellpadding="2">
	<tr>
		<td>Consultation Sheet:</td>
		<td>
			<a href="/openmrs/module/htmlformentry/htmlFormEntry.form?personId=${personId}&patientId=${patientId}&returnUrl=&formId=1">Enter new sheet</a><br/>
			Edit previous: <a href="">01-Feb-2013</a> <a href="">14-Jan-2013</a> <a href="">01-Jan-2013</a> (goto Encounters to see all)
		</td>
	</tr>
	<tr>
		<td><br /></td>
	</tr>
	<tr>
		<td>Referrals:</td>
		<td></td>
	</tr>
	<tr>
		<td><br /></td>
	</tr>
	<tr>
		<td>TB results:</td>
		<td></td>
	</tr>
</table>
