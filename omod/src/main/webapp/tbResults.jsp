<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ taglib prefix="referrals" uri="/WEB-INF/view/module/haitimobileclinic/resources/referrals.tld"%>
<%@ taglib prefix="tbSuspect" uri="/WEB-INF/view/module/haitimobileclinic/resources/tbSuspect.tld"%>
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
			<span class="openmrsSearchDiv" style="display: inline;">
					<div id="openmrsSearchTable_wrapper" class="dataTables_wrapper">
						<table id="openmrsSearchTable" cellspacing="0" cellpadding="2"
							style="width: 100%">
							<thead id="searchTableHeader">
								<tr>
									<th>Name</th>
									<th>Site</th>
									<th>CHW names</th>
									<th>NEC name</th>
									<th>Screening visit</th>
									<th>Sputum result #1</th>
									<th>Sputum result #2</th>
									<th>Sputum result #3</th>
									<th>Overall status</th>
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
								<c:set var="tbSuspectEncounterId">
									<tbSuspect:tbSuspectEncounterId patientId="${patientId}"/>
								</c:set>
								<tbody>
									<tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
										<td><referrals:patientName patientId="${patientId}" /></td>
										<td><referrals:site referralEncounterId="${tbSuspectEncounterId}" /></td>
										<td><referrals:chwNames referralEncounterId="${tbSuspectEncounterId}"/></td>
										<td><referrals:necName referralEncounterId="${tbSuspectEncounterId}" /></td>
										<td><referrals:mobileVisitDate referralEncounterId="${tbSuspectEncounterId}" /></td>
										<td><tbSuspect:sputumResult test="1" tbSuspectEncounterId="${tbSuspectEncounterId}" /></td>
										<td><tbSuspect:sputumResult test="2" tbSuspectEncounterId="${tbSuspectEncounterId}" /></td>
										<td><tbSuspect:sputumResult test="3" tbSuspectEncounterId="${tbSuspectEncounterId}" /></td>
										<td><tbSuspect:overallTbStatus tbSuspectEncounterId="${tbSuspectEncounterId}" /></td>
										<td>
											<span id='enrollmentSpan-${tbSuspectEncounterId}'>
												<input type="text" name="staticVisitDate" id="staticVisitDate-${patientId}" size="11" onfocus="showCalendar(this,60)" onChange="clearError('staticVisitDate-${patientId}');" />
												<a id='enroll-${patientId}' href="javascript:enroll($j('#enrollmentSpan-${tbSuspectEncounterId}'), ${tbSuspectEncounterId}, $j('#staticVisitDate-${patientId}').val());">Enroll</a>
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