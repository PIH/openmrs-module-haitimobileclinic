<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/module/haitimobileclinic/referralsSelect.htm" />

<div align="center">
	<br/><br/><br/>
	<table>
		<tr>
			<td><a href="/openmrs/module/haitimobileclinic/referrals.form?enrollmentReason=HIV">HIV</a><br/></td>
		</tr>
		<tr>
			<td><a href="/openmrs/module/haitimobileclinic/referralsTb.form">TB</a><br/></td>
		</tr>
		<tr>
			<td><a href="/openmrs/module/haitimobileclinic/referrals.form?enrollmentReason=Malnutrition">Malnutrition</a></td>
		</tr>
	</table>
</div>
<br>
&nbsp;
<br>

<%@ include file="/WEB-INF/template/footer.jsp"%>