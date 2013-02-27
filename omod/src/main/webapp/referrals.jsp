<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ taglib prefix="referrals" uri="/WEB-INF/view/module/haitimobileclinic/resources/referrals.tld"%>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

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
									<th>NEC name</th>
									<th>Mobile Visit date</th>
									<th>Static Visit date</th>
								</tr>
							</thead>
							<c:forEach var="patientId" items="${memberIds}" varStatus="loopStatus">
								<script type="text/javascript">
									$j(function() {
										$j('#enroll-${patientId}').hide();
									    $j('#staticVisitDate-${patientId}').change(function() {
									    	  if ($j('#staticVisitDate-${patientId}').val()) {
										            $j('#enroll-${patientId}').show();
									    	  } else {
										            $j('#enroll-${patientId}').hide();
									    	  }
									    });
									});
								</script>
								<c:set var="referralEncounterId">
									<referrals:referralEncounterId patientId="${patientId}" referralType="hiv"/>
								</c:set>
								<tbody>
									<tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
										<td><referrals:patientName patientId="${patientId}" /></td>
										<td><referrals:site referralEncounterId="${referralEncounterId}" /></td>
										<!-- <td><referrals:referralReason referralEncounterId="${referralEncounterId}" /></td>-->
										<td><referrals:chwNames referralEncounterId="${referralEncounterId}"/></td>
										<td><referrals:necName referralEncounterId="${referralEncounterId}" /></td>
										<td><referrals:mobileVisitDate referralEncounterId="${referralEncounterId}" /></td>
										<td>
											<span id='enrollmentSpan-${referralEncounterId}'>
												<input type="text" name="staticVisitDate" id="staticVisitDate-${patientId}" size="11" onfocus="showCalendar(this,60)" onChange="clearError('staticVisitDate-${patientId}');" />
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
</div>

<br>
&nbsp;
<br>

<%@ include file="/WEB-INF/template/footer.jsp"%>