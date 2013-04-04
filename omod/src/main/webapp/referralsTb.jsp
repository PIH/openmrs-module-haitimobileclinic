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
		<b class="boxHeader">Pending enrollments for ${enrollmentReason}</b>
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
									<th>Name</th>
									<th>Site</th>
									<!-- <th>Reason for referral</th>-->
									<th>CHW names</th>
									<th>NEC names</th>
									<th>Mobile Visit date</th>
									<th>Confirmative TB date</th>
									<th>Static Visit date</th>
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
									<referrals:referralEncounterId patientId="${patientId}" referralType="tb"/>
								</c:set>
								<tbody>
									<tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
										<td><referrals:patientName patientId="${patientId}" /></td>
										<td><referrals:site referralEncounterId="${referralEncounterId}" /></td>
										<!-- <td><referrals:referralReason referralEncounterId="${referralEncounterId}" /></td>-->
										<td><referrals:chwNames referralEncounterId="${referralEncounterId}"/></td>
										<td><referrals:necNames referralEncounterId="${referralEncounterId}" /></td>
										<td><referrals:mobileVisitDate referralEncounterId="${referralEncounterId}" /></td>
										<td><referrals:confirmativeTbDate referralEncounterId="${referralEncounterId}" /></td>
										<td>
											<span id='enrollmentSpan-${referralEncounterId}'>
												<referrals:datePicker id='staticVisitDate-${patientId}'/>
												<a id='enroll-${patientId}' href="javascript:enroll($j('#enrollmentSpan-${referralEncounterId}'), ${referralEncounterId}, $j('#staticVisitDate-${patientId}').val(), 'tb');">Enroll</a>
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
	&nbsp;
	<br>
	For HIV and Malnutrition:
	Only patients with pending referrals are listed. If a patient is missing, <a href="/openmrs/findPatient.htm">Find the patient</a> and check the referral section of the last Mobile Clinic Consultation Sheet(s) and make sure the patient is not yet enrolled with a matching Static Clinic Enrollment.
	<br><br>
	For TB:
	Only patients with pending referrals and a confirmed BT status are listed. If a patient is missing, <a href="/openmrs/findPatient.htm">Find the patient</a> and check the referral section of the last Mobile Clinic Consultation Sheet(s) and confirm the <a href="/openmrs/module/haitimobileclinic/tbResults.form">TB status</a> for this patient.  
</div>

<br>
&nbsp;
<br>

<%@ include file="/WEB-INF/template/footer.jsp"%>