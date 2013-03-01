<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ taglib prefix="referrals" uri="/WEB-INF/view/module/haitimobileclinic/resources/referrals.tld"%>
<%@ taglib prefix="tbSuspect" uri="/WEB-INF/view/module/haitimobileclinic/resources/tbSuspect.tld"%>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<script>
function save(fieldToUpdate, tbSuspectEncounterId, sputumResult1, sputumResult1Date, sputumResult2, sputumResult2Date, sputumResult3, sputumResult3Date, status, statusDate, existingResultEncounterId) {	
	$j.post(
			 '/openmrs/module/haitimobileclinic/saveTbResult.form',
			 { 'tbSuspectEncounterId': tbSuspectEncounterId, 'existingResultEncounterId': existingResultEncounterId,
				 'sputumResult1': sputumResult1, 'sputumResult1Date': sputumResult1Date, 
				 'sputumResult2': sputumResult2, 'sputumResult2Date': sputumResult2Date, 
				 'sputumResult3': sputumResult3, 'sputumResult3Date': sputumResult3Date, 
				 'status': status, 'statusDate': statusDate },
			 function (data) {
						//fieldToUpdate.empty();
						//fieldToUpdate.html(data);
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
									<th>TB Result and status</th>
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
											<td>
																					<span id='span-${patientId}'>								
											
											<tbSuspect:tbResultAndStatus tbSuspectEncounterId="${tbSuspectEncounterId}" />
											
																					</span>
											
											</td>
											<td>
												<a id='save-${patientId}' href="javascript:save($j('#span-${patientId}'), ${tbSuspectEncounterId},
													$j('#sputum1-${patientId}').val(), $j('#sputumdate1-${patientId}').val(), 
													$j('#sputum2-${patientId}').val(), $j('#sputumdate2-${patientId}').val(), 
													$j('#sputum3-${patientId}').val(), $j('#sputumdate3-${patientId}').val(), 
													$j('#status-${patientId}').val(), $j('#statusdate-${patientId}').val(),
													$j('#resultEncounterId-${patientId}').val());">Save</a>
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