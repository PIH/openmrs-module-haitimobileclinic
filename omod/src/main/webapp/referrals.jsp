<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/module/haitimobileclinic/referrals.form" />
<%@ taglib prefix="referrals" uri="/WEB-INF/view/module/haitimobileclinic/resources/referrals.tld"%>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<script src="/openmrs/moduleResources/htmlformentry/htmlFormEntry.js" type="text/javascript"></script>

<script>
function enroll(fieldToUpdate, referralEncounter, enrollmentDate, enrollmentReason) {	
	$j.post(
			 '/openmrs/module/haitimobileclinic/enroll.form',
			 { 'referralEncounterId': referralEncounter, 'enrollmentDate': enrollmentDate, 'enrollmentReason': enrollmentReason },
			 function (data) {
						fieldToUpdate.empty();
						fieldToUpdate.html(data);
		  		}
		);
	}
</script>

<div id="findPatient" class="portlet">
	<div>
		<b class="boxHeader"><spring:message code="haitimobileclinic.pendingEnrollmentsFor"/> ${enrollmentReason}</b>
		<div class="box">
			<div class="searchWidgetContainer" id="referrals">
			<!-- 
			<span> 
				<form method="get" action="filterReferrals.form">
					Location: ${sessionStaticLocationName}	
					Mobile visit between: <input type="text" name="fromDate" id="fromDate" size="11" onfocus="showCalendar(this,60)" onChange="clearError('fromDate')" />
					and: <input type="text" name="toDate" id="toDate" size="11" onfocus="showCalendar(this,60)" onChange="clearError('toDate')" />
					<input type="submit" value="Filter"/>
				</form>
			</span>
			-->			
			<span class="openmrsSearchDiv" style="display: inline;">
					<div id="openmrsSearchTable_wrapper" class="dataTables_wrapper">
						<table id="openmrsSearchTable" cellspacing="0" cellpadding="2"
							style="width: 100%">
							<thead id="searchTableHeader">
								<tr>
									<th><spring:message code="haitimobileclinic.name"/></th>
									<th><spring:message code="haitimobileclinic.site"/></th>
									<!-- <th>Reason for referral</th>-->
									<th><spring:message code="haitimobileclinic.chwNames"/></th>
									<th><spring:message code="haitimobileclinic.necNames"/></th>
									<th><spring:message code="haitimobileclinic.mobileVisitDate"/></th>
									<th><spring:message code="haitimobileclinic.staticVisitDate"/></th>
								</tr>
							</thead>
							<c:forEach var="patientId" items="${memberIds}" varStatus="loopStatus">
								<script type="text/javascript">
									$j(function() {
										$j('#enroll-${patientId}').hide();
									    $j('#staticVisitDate-${patientId}-display').change(function() {
									    	  if ($j('#staticVisitDate-${patientId}-display').val()) {
										            $j('#enroll-${patientId}').show();
									    	  } else {
										            $j('#enroll-${patientId}').hide();
									    	  }
									    });
									});
								</script>
								<c:set var="referralEncounterId">
									<referrals:referralEncounterId patientId="${patientId}" referralType="${enrollmentReason}"/>
								</c:set>
								<tbody>
									<tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
										<td><referrals:patientName patientId="${patientId}" /></td>
										<td><referrals:site referralEncounterId="${referralEncounterId}" /></td>
										<!-- <td><referrals:referralReason referralEncounterId="${referralEncounterId}" /></td>-->
										<td><referrals:chwNames referralEncounterId="${referralEncounterId}"/></td>
										<td><referrals:necNames referralEncounterId="${referralEncounterId}" /></td>
										<td><referrals:mobileVisitDate referralEncounterId="${referralEncounterId}" /></td>
										<td>
											<span id='enrollmentSpan-${referralEncounterId}'>
												<referrals:datePicker id='staticVisitDate-${patientId}'/>
												<a id='enroll-${patientId}' href="javascript:enroll($j('#enrollmentSpan-${referralEncounterId}'), ${referralEncounterId}, $j('#staticVisitDate-${patientId}').val(), '${enrollmentReason}');">Enroll</a>
											</span>
										</td>
									</tr>
								</tbody>
							</c:forEach>
						</table>
					</div>
				</span>
			</div>
		</div>
	</div>
	<br>	
	<input type="button" onClick="window.print()" value="Print"/>
  	<br>
	<br>
	&nbsp;
	<br>
	<spring:message code="haitimobileclinic.referralsHivMalnutritionNote"/>
</div>

<br>
&nbsp;
<br>

<%@ include file="/WEB-INF/template/footer.jsp"%>