$j(document).ready(function(){

    var prevDiv ='';
    var nextDiv='';
    var CODED = 'CODED';
    var NUMERIC = 'NUMERIC';
    var NONCODED = 'NON-CODED';
    var FOREVER = 180; // 3 minutes, longer than the normal session timeout
	var submitReception = false;
    var obsObject = new Object();
    obsObject.type='';
    obsObject.conceptName='';
    obsObject.conceptId=0;
    obsObject.id=0;
    obsObject.label='';
    var obsArray = new Array();

	$j.getObsId = function(searchConceptId) {
        var returnObject = null;
        for(var i =0; i<obsArray.length; i++){
            if(obsArray[i].conceptId == searchConceptId){
                returnObject = new Object();
                returnObject = obsArray[i];
                return returnObject;
            }
        }
        return returnObject;
    };

    $j.removeObs = function(conceptId) {
        for (var i = 0; i < obsArray.length; i++) {
            if (obsArray[i].conceptId == conceptId) {
                obsArray.splice(i, 1);
            }
        }
    };

    var alreadySubmitted = false;
    
    function submitData(){
		if (!alreadySubmitted) {
			alreadySubmitted = true;
		    var obsList='';
	        for(var i=0; i < obsArray.length; i++){
	            var obsItem = new Object();
	            obsItem = obsArray[i];
	            var obsCode=obsItem.type;
	            var codedId = obsItem.id;
	            if (obsCode === NONCODED) {
	                codedId = 0;
	            }
	            var obsItemLabel = obsItem.label;
				if(obsItemLabel.length<1){
					obsItemLabel =0;
				}
	            var obsId = parseInt(obsItem.obsId, 10);
	            if (isNaN(obsId)){
	                obsId = 0;
	            }
	
	            obsList =obsList + obsCode + ','
	                + codedId + ','
	                + obsItemLabel + ','
	                + obsItem.conceptId + ','
	                + obsId + ';';
	        }
	        $j('#listOfObs').val(obsList);
	        $j('#hiddenEncounterYear').val(encounterYear);
	        $j('#hiddenEncounterMonth').val(encounterMonth);
	        $j('#hiddenEncounterDay').val(encounterDay);
	        if(nextTask.length>0){
	            $j('#hiddenNextTask').val(nextTask);
	        }
	        alertUserAboutLeaving = false;
	        $j('#obsForm').submit();
    	}
    }

    var divItems = new Array("encounterDateDiv",
        "yearDiv",
        "monthDiv",
        "dayDiv",        
        "tbScreeningDiv",
        "confirmDiv",
        "dialog-confirm",
        "confirmMessageArea"
    );

    var leftMenuItems = new Array("encounterDateMenu",
        "tbScreeningMenu",
        "confirmMenu"
    );

    var navigationArrows = new Array("cross-red",
        "left-arrow-white",
        "right-arrow-white",
        "right-arrow-yellow",
        "checkmark-yellow"
    );

    $j.removeAllDiagnosis = function() {
        for(var i=0; i<obsArray.length; i++){
            obsArray.splice(i,1);
        }
        obsArray = new Array();
    };

    $j.hideAllDiv = function() {
        for(i=0; i<divItems.length; i++){
            var divItem = "#"+divItems[i];
            $j(divItem).hide();
        }
    };

    $j.removeHighlightedMenu = function() {
        for(i=0; i<leftMenuItems.length; i++){
            var menuItem = "#"+leftMenuItems[i];
            $j(menuItem).removeClass('highlighted');
        }
    };

    $j.hideNavigationArrows = function() {
        for(i=0; i<navigationArrows.length; i++){
            var arrowItem = "#"+navigationArrows[i];
            $j(arrowItem).hide();
        }
    };

    $j.setEncounterDateDiv = function() {
        prevDiv=null;
        nextDiv="tbScreeningDiv";
        $j('.encounterDateList').find('tr').removeClass('highlighted');
        $j("#todayDateRow").addClass('highlighted');
        var dateLabel = encounterDay + "-" +
            encounterMonthLabel + "-" +
            encounterYear;
        if(isToday(encounterDay, encounterMonth, encounterYear)){
            dateLabel = dateLabel + " (" + todayLabel +")";
        }
        $j("#todayEncounterDate").text(dateLabel);

        $j("#todayDateRow").focus();
        $j("#encounterDateMenu").addClass('highlighted');
        $j('#right-arrow-yellow').show();

    };

    // today/past date mouseovers
    $j('.dateListRow').mouseover(function(){
        $j(this).addClass('highlighted');
    });
    $j('.dateListRow').mouseout(function(){
        $j(this).removeClass('highlighted');
    });

    $j("#todayDateRow").click(function(event){
        $j.setupDiv('tbScreeningDiv');
    });
    $j("#pastDateRow").click(function(event){
        $j.setupDiv('yearDiv');
    });

    $j.setTbScreeningDiv = function() {
        prevDiv="encounterDateDiv";
        nextDiv="confirmDiv";
        $j("#tbScreeningMenu").addClass('highlighted');
        setSelectedTbScreening(0);
		
		var tbScreeningObject = $j.getObsId(tbScreeningConceptId);
        if(tbScreeningObject!=null){
            var dbId = tbScreeningObject.obsId;
            if (!isNaN(dbId)){
                $j("#tbScreeningObsId").val(dbId);
                var findTd= $tbScreeningRows.find('td:contains("' + tbScreeningObject.label +'")');
                if(findTd!=null){
                    var closestTr = findTd.closest('tr');
                    if(closestTr !=null){
                        selectedTbScreening = closestTr.prevAll().length;
                        setSelectedTbScreening(selectedTbScreening);
                    }
                }
            }
        }
		
        $j('#left-arrow-white').show();
        $j('#right-arrow-yellow').show();
    };

    $j('.tbScreeningListRow').mouseover(function(){
        $tbScreeningRows.find('tr').removeClass('highlighted');
        $j(this).addClass('highlighted');
    });
    $j('.tbScreeningListRow').mouseout(function(){
        $j(this).removeClass('highlighted');
    });

    $j(".tbScreeningListRow").click(function() {
        var tbScreeningSelectedId = $j(this).find("input").val();
        
        if(parseInt(tbScreeningSelectedId, 10) > 0){
            var selectedTbScreeningObject = new Object();
            selectedTbScreeningObject.type=CODED;
            selectedTbScreeningObject.id = tbScreeningSelectedId;
            selectedTbScreeningObject.conceptName = tbScreeningConceptName;
            selectedTbScreeningObject.conceptId = tbScreeningConceptId;
            var tbScreeningLabel = $j(this).find("td").text();            
            if(tbScreeningLabel.length > 0){
                selectedTbScreeningObject.label=tbScreeningLabel;
            }
			var tbScreeningObsId = $j("#tbScreeningObsId").val();
            if(parseInt(tbScreeningObsId, 10) > 0){
                selectedTbScreeningObject.obsId=tbScreeningObsId;
            }
            $j.removeObs(tbScreeningConceptId);
            obsArray.push(selectedTbScreeningObject);
			$j("#tbScreeningObsId").val("");
        }
        console.log("tbScreenListRow.click");
        $j('#right-arrow-yellow').click();
    });

    var $tbScreeningRows = $j("#tbScreeningTable");
    $tbScreeningRows.find('tr').removeClass('highlighted');
    var selectedTbScreening = null;
    var setSelectedTbScreening = function(item) {
        selectedTbScreening = item;
        if (selectedTbScreening !== null) {
            if (selectedTbScreening < 0) {
                selectedTbScreening = 0;
            }
            if (selectedTbScreening >= $tbScreeningRows.find('tr').length) {
                selectedTbScreening = $tbScreeningRows.find('tr').length - 1;
            }
            $tbScreeningRows.find('tr').removeClass('highlighted').eq(selectedTbScreening).addClass('highlighted');
        }
    };

    $j(document).keydown(function(event) {
        if ($j('#tbScreeningDiv').is(':visible') ){
            if (event.keyCode == KEYCODE_ARROW_UP){
                if(selectedTbScreening === null){
                    selectedTbScreening=1;
                }
                setSelectedTbScreening(selectedTbScreening - 1);
                event.preventDefault();
            }else if (event.keyCode == KEYCODE_ARROW_DOWN){
                if(selectedTbScreening === null){
                    setSelectedTbScreening(0);
                }else{
                    setSelectedTbScreening(selectedTbScreening + 1);
                }
                event.preventDefault();
            }else if (event.keyCode == KEYCODE_ENTER ) {
                //User pressed enter key.
                event.stopPropagation();
                event.preventDefault();
                event.stopImmediatePropagation();
                if((selectedTbScreening!==null)){
                    window.setTimeout(function(event){
                        var selectedRow = $tbScreeningRows.find('tr').eq(selectedTbScreening);
                        var selectedRowId = selectedRow.find("input").val();
                        console.log("selectedRowId=" + selectedRowId);
                        $j("#" + selectedRow.attr('id')).click();
                    }, 100);
                }
            }
        }
    });

    $j.setConfirmDiv = function() {
        prevDiv="tbScreeningDiv";
        nextDiv="confirmDiv";
        $j("#confirmMenu").addClass('highlighted');

        $j('#left-arrow-white').show();
        $j('#checkmark-yellow').show();
//        $j('.confirmPaymentTableList').find('tr').remove();
//
//        for(var i=0; i<paymentGroupArray.length; i++){
//            var paymentItem = paymentGroupArray[i];
//
//            var rowObs = $j(document.createElement('tr')).addClass('dateListRow paymentBiggerRow');
//            if (i % 2 == 0) {
//                rowObs.addClass('evenRow');
//            } else {
//                rowObs.addClass('oddRow');
//            }
//            rowObs.mouseover(function(event){
//                $j('.confirmPaymentTableList').find('tr').removeClass('highlighted');
//                $j(this).addClass('highlighted');
//            });
//            rowObs.mouseout(function(event){
//                $j(this).removeClass('highlighted');
//            });
//            var hiddenInput = $j(document.createElement('input')).addClass('paymentGroupArrayIdClass')
//                .attr({type: 'hidden', id: 'paymentGroupArrayId'+i})
//                .val(i);
//            rowObs.append(hiddenInput);
//
//            var obsTbScreening = paymentItem[0];
//			if(typeof(obsTbScreening) !=='undefined'){
//				rowObs.attr('id', 'obsConcept' + obsTbScreening.conceptName);
//				var columnLabel= " (" + paymentItem[1].label + ", "
//					+ paymentItem[2].conceptName + ": "
//					+ paymentItem[2].label + ")";
//
//				var biggerSpan = $j(document.createElement('span')).addClass('normalFont').text(obsTbScreening.label);
//				var smallerSpan = $j(document.createElement('span')).addClass('smallerFont greyColor').text(columnLabel);
//				var columnObs = $j(document.createElement('td')).addClass('questionAnswer');
//				columnObs.append(biggerSpan);
//				columnObs.append(smallerSpan)
//				rowObs.append(columnObs);
//
//				var editObsId = paymentItem[1].obsId;
//                if (isNaN(editObsId)){
//					//append the Delete button
//					var secondColumn = $j(document.createElement('td'));					
//					var deletePaymentGroupBtn = $j(document.createElement('button'))
//						.addClass('deletePaymentGroupClick deletePayment')
//						.click(function(event){
//							var paymentGroupArrayId = $j(this).closest('tr').find('.paymentGroupArrayIdClass').val();
//							var closestTr = $j(this).closest('tr');
//							paymentGroupArray.splice(paymentGroupArrayId,1);
//							closestTr.remove();	
//						});					
//					deletePaymentGroupBtn.attr('type', 'button');
//					deletePaymentGroupBtn.attr('id', 'deletePaymentGroupBtnId');
//					deletePaymentGroupBtn.attr('align', 'left');					
//					secondColumn.append(deletePaymentGroupBtn);
//					rowObs.append(secondColumn);
//                }else{
//                    //add the edit button
//                    var secondColumn = $j(document.createElement('td'));
//                    var editPaymentGroupBtn = $j(document.createElement('button'))
//                        .addClass('editPaymentGroupClick editPayment')
//                        .click(function(event){
//                            var paymentGroupArrayId = $j(this).closest('tr').find('.paymentGroupArrayIdClass').val();
//                            var closestTr = $j(this).closest('tr');
//                            obsArray = paymentGroupArray[paymentGroupArrayId];
//                            paymentGroupArray.splice(paymentGroupArrayId,1);
//                            closestTr.remove();
//                            $j.setupDiv('tbScreeningDiv');
//
//                        });
//                    editPaymentGroupBtn.attr('type', 'button');
//                    editPaymentGroupBtn.attr('id', 'editPaymentGroupBtnId');
//                    editPaymentGroupBtn.attr('align', 'left');
//
//                    secondColumn.append(editPaymentGroupBtn);
//                    rowObs.append(secondColumn);
//                }
//				$j('.confirmPaymentTableList').append(rowObs);
//			}
//        }
        $j('#checkmark-yellow').css('border', '5px solid #EFB420');
        $j('#checkmark-yellow').addClass("highCheckmarkYellow");

        $j('#checkmark-yellow').focus();
    };


    $j.setYearDiv = function() {
        prevDiv="encounterDateDiv";
        nextDiv="monthDiv";
        $j("#encounterDateMenu").addClass('highlighted');
        $j('#left-arrow-white').show();
        $j('#right-arrow-yellow').show();

        var tempYear =  parseInt(encounterYear, 10);
        console.log("encounterYear=" + tempYear);
        if(tempYear<1){
            var today=new Date();
            tempYear = parseInt(today.getFullYear(),10);
        }
        $j("#encounterYear").val(tempYear);

        $j('#encounterYear').focus();

    };

    $j('#encounterYear').keyup(function(event) {
        var tempYear = $j('#encounterYear').val();
        if (tempYear.length<4 ){
            $j('#right-arrow-white').show();
            $j('#right-arrow-yellow').hide();
        }else{
            $j('#right-arrow-white').hide();
            $j('#right-arrow-yellow').show();
        }
    }).keypress(function(event) {
            if(event.keyCode == 13){
                tempYear = $j('#encounterYear').val();
                if (tempYear.length!=4 ){
                    return false;
                }else{
                    $j('#right-arrow-yellow').click();
                }
            }
        });


    function selectRadioButton(element) {
        if(element !== null && element !=='undefined' && element.length>0){
            // make sure the proper button is highlighted
            $j('.radioItem').removeClass('highlighted');
            $j('.radioItem').find('.radioClass').attr('checked',false);
            $j(element).addClass('highlighted');
            $j(element).find('.radioClass').attr('checked',true);

            console.log("radioClass val=" + $j(element).find('.radioClass').val());
            console.log("radioLabel text=" + $j(element).find('.radioLabel').text());
        }else{
            console.log("selectradiobutton null");
        }
    }

    $j('.radioItem').click(function(event) {
        var monthValue = $j(this).find('.radioClass').val();
        $j('.radioItem').removeClass('highlighted');
        $j('.radioItem').find('.radioClass').attr('checked',false);
        var radioButton = $j('input[value="' + monthValue + '"]');
        if(radioButton.length>0){
            radioButton.attr('checked',true);
            var closestTr = radioButton.closest('tr');
            closestTr.addClass('highlighted');
        }
    });

    $j.setMonthDiv = function() {
        prevDiv="yearDiv";
        nextDiv="dayDiv";
        $j("#encounterDateMenu").addClass('highlighted');
        $j('#left-arrow-white').show();
        $j('#right-arrow-yellow').show();
        $j('.radioItem').removeClass('highlighted');
        $j('.radioItem').find('.radioClass').attr('checked',false);
        $j('.dateSpan').text(encounterYear);
        var tempMonth =  parseInt(encounterMonth, 10);
        console.log("encounterMonth=" + tempMonth);
        var radioButton = $j('input[value="' + tempMonth + '"]');
        if(radioButton.length>0){
            radioButton.attr('checked',true);
            var closestTr = radioButton.closest('tr');
            closestTr.addClass('highlighted');
        }
    };

    $j(document).keydown(function(event) {
        if ($j('#monthDiv').is(':visible') ){
            console.log("monthDiv is visible");
            var checkedRadioButton = $j("input[type='radio']:checked");
            var monthValue = 1;
            if(checkedRadioButton.length>0){
                monthValue = parseInt(checkedRadioButton.val(), 10);
            }
            if (event.keyCode == KEYCODE_ARROW_UP){
                event.preventDefault();
                selectRadioButton($j('input[value=' + (monthValue - 4) + ']').closest('.radioItem'));
            }else if (event.keyCode == KEYCODE_ARROW_DOWN){
                event.preventDefault();
                selectRadioButton($j('input[value=' + (monthValue + 4) + ']').closest('.radioItem'));
            }else if (event.keyCode == KEYCODE_ARROW_LEFT){
                event.preventDefault();
                selectRadioButton($j('input[value=' + (monthValue - 1) + ']').closest('.radioItem'));
            }else if (event.keyCode == KEYCODE_ARROW_RIGHT){
                event.preventDefault();
                selectRadioButton($j('input[value=' + (monthValue + 1) + ']').closest('.radioItem'));
            }else if (event.keyCode == KEYCODE_ENTER ) {
                //User pressed enter key.
                event.stopPropagation();
                event.preventDefault();
                window.setTimeout('$j("#right-arrow-yellow").click();', '100');
            }
        }
    });

    $j.setDayDiv = function() {
        prevDiv="monthDiv";
        nextDiv="tbScreeningDiv";
        $j("#encounterDateMenu").addClass('highlighted');
        $j('#left-arrow-white').show();
        $j('#right-arrow-yellow').show();
        $j('.dateSpan').text( encounterMonthLabel + "-" + encounterYear);
        var tempDay =  parseInt(encounterDay, 10);
        console.log("encounterDay=" + tempDay);
        if(tempDay<1){
            tempDay = 1;
        }
        $j("#encounterDay").val(tempDay);
        $j("#encounterDay").focus();
    };

    $j('#encounterDay').keyup(function(event) {
        var tempDay = $j('#encounterDay').val();
        if (tempDay.length<1 ){
            $j('#right-arrow-white').show();
            $j('#right-arrow-yellow').hide();
        }else{
            $j('#right-arrow-white').hide();
            $j('#right-arrow-yellow').show();
        }
    }).keypress(function(event) {
            if(event.keyCode == 13){
                var tempDay = $j('#encounterDay').val();
                if (tempDay.length<1 ){
                    return false;
                }else{
                    $j('#right-arrow-yellow').click();
                }
            }
        });

    var $dateList = $j('.encounterDateList');
    var selectedDate = 0;
    var setSelectedDate = function(item) {
        selectedDate = item;
        if (selectedDate !== null) {
            if (selectedDate < 0) {
                selectedDate = 0;
            }
            if (selectedDate >= $dateList.find('tr').length) {
                selectedDate = $dateList.find('tr').length - 1;
            }
            $dateList.find('tr').removeClass('highlighted').eq(selectedDate).addClass('highlighted');
        }
    };

    $j(document).keydown(function(event) {
        if ($j('#dialog-checkedInDiv').is(':visible') ){
            return;
        }else if ($j('#encounterDateDiv').is(':visible') ){
            if (event.keyCode == KEYCODE_ARROW_UP){
                //user pressed up arrow
                console.log("up arrow");
                //user pressed up arrow
                if(selectedDate === null){
                    selectedDate=1;
                }
                setSelectedDate(selectedDate - 1);
                event.preventDefault();
            }else if (event.keyCode == KEYCODE_ARROW_DOWN){
                //user pressed down arrow
                console.log("down arrow");
                //user pressed down arrow
                if(selectedDate === null){
                    setSelectedDate(0);
                }else{
                    setSelectedDate(selectedDate + 1);
                }
                event.preventDefault();
            }else if (event.keyCode == KEYCODE_ENTER ) {
                //User pressed enter key.
                event.stopPropagation();
                event.preventDefault();
                event.stopImmediatePropagation();
                if((selectedDate!==null)){
                    window.setTimeout(function(event){
                        var selectedRow = $dateList.find('tr').eq(selectedDate);
                        var selectedRowId = selectedRow.attr('id');
                        $j("#"+selectedRowId).click();
                    }, 100);

                }
            }
        }else if ($j('#dialog-confirm').is(':visible') ){
            //the delete confirmation modal dialog is displayed
            return;

        }
    });

    $j.validateTbScreeningDivData = function(){
    	console.log("validateTbScreeningDivData");
        for(var i=0; i<obsArray.length; i++){
            var obsItem = obsArray[i];
            if(obsItem.conceptId == tbScreeningConceptId){
                return true;
            }
        }
        return false;
    };

    $j.validateYearDivData = function() {

        var inputYear = parseInt($j('#encounterYear').val(),10);

        var $newDate = (1) + "/" + (1) + "/" + inputYear;
        try{
            var parsedDate =$j.datepicker.parseDate("m/d/yy", $newDate);
            var today=new Date();
            if(parsedDate>today){
                alert(scriptMessages['invalidBirthFuture']);
                return false;
            }else{
                if((parseInt(today.getFullYear(),10) - parseInt(parsedDate.getFullYear(), 10)) >120){
                    alert(scriptMessages['invalidBirthPast']);
                    return false;
                }
            }
        }catch(e){
            console.log(e + "newDate=" + $newDate);
            alert(scriptMessages['invalidBirthDate']);
            return false;
        }
        encounterYear = inputYear;
        return true;
    };

    $j.validateMonthDivData = function() {
        var checkedRadioButton = $j("input[type='radio']:checked");
        var monthValue = 1;
        var closestTr = null;
        if(checkedRadioButton.length>0){
            monthValue = parseInt(checkedRadioButton.val(), 10);
            var monthLabel = checkedRadioButton.closest('.radioItem').find('.radioLabel').text();
            if(monthLabel.length>0){
                console.log("monthLabel=" + monthLabel);
                encounterMonthLabel = monthLabel;
            }
        }
        if(monthValue<1){
            monthValue = 1;
        }
        encounterMonth = monthValue;
        return true;
    };

    $j.validateDayDivData = function() {
        var inputDay = parseInt($j('#encounterDay').val(),10);
        var $newDate = parseInt(encounterMonth, 10) + "/" + inputDay + "/" + encounterYear;
        try{
            var parsedDate =$j.datepicker.parseDate("m/d/yy", $newDate);
            var today=new Date();
            if(parsedDate>today){
                alert(scriptMessages['invalidBirthFuture']);
                return false;
            }else{
                if((parseInt(today.getFullYear(),10) - parseInt(parsedDate.getFullYear(), 10)) >120){
                    alert(scriptMessages['invalidBirthPast']);
                    return false;
                }
            }
        }catch(e){
            console.log(e + "newDate=" + $newDate);
            alert(scriptMessages['invalidBirthDate']);
            return false;
        }
        encounterDay = inputDay;
        $j.removeAllDiagnosis();
        //eventually I would like to make an AJAX call to see if any diagnosis have already be entered on this day
        return true;
    };
    
    $j.validateDivData = function() {
    	console.log("validateDivData");
        //place holder for validating entry data
        if ($j('#yearDiv').is(':visible') ){
            return $j.validateYearDivData();
        }else if ($j('#monthDiv').is(':visible') ){
            return $j.validateMonthDivData();
        }else if ($j('#dayDiv').is(':visible') ){
            return $j.validateDayDivData();
        }else if ($j('#tbScreeningDiv').is(':visible') ){
            return $j.validateTbScreeningDivData();
        }

        return true;
    };

    $j.setupDiv = function(devId) {
        $j.hideAllDiv();
        $j.removeHighlightedMenu();
        $j.hideNavigationArrows();
        $j('#cross-red').show();
        $j("#"+devId).css("visibility", "visible");
        $j("#"+devId).show();
        if(devId=='encounterDateDiv'){
            $j.setEncounterDateDiv();
        }else if(devId=='yearDiv'){
            $j.setYearDiv();
        }else if(devId=='monthDiv'){
            $j.setMonthDiv();
        }else if(devId=='dayDiv'){
            $j.setDayDiv();
        }else if(devId=='tbScreeningDiv'){
            $j.setTbScreeningDiv();
        }else if(devId=='confirmDiv'){
            $j.setConfirmDiv();
        }
    };


    $j('#right-arrow-yellow').click(function(event){
    	console.log("right arrow yellow: click")
        if($j.validateDivData()){            
            if(nextDiv !== null){
                $j.setupDiv(nextDiv);
            }
        }
    });

    $j('#checkmark-yellow').click(function(event){
			submitData();
    });

    $j('#left-arrow-white').click(function(event){
        if(prevDiv !== null){
            $j.setupDiv(prevDiv);
        }
    });

    $j.setupDiv('encounterDateDiv');
    //change the left lower corner red X with the reload image
    $j("#cross-red").attr('src', pageContextAddress + '/moduleResources/haitimobileclinic/images/reload-arrow.png');
    $j('#cross-red').click(function(event){
        alertUserAboutLeaving = false;
        window.location.href=pageContextAddress + '/module/haitimobileclinic/workflow/mobileClinicReceptionTask.form';
    });

//    if(createNew !== 'true' ){
//        if(paymentGroupsData.length>0 && obsArray.length<1){
//            for(var i=0; i< paymentGroupsData.length; i++){
//                var groupPayment = new Object();
//                groupPayment = paymentGroupsData[i];
//				if(typeof(groupPayment) !=='undefined' && groupPayment.length>0){
//					paymentGroupArray.push(groupPayment.sort(sortPaymentConcepts));
//				}
//            }
//            $j.setupDiv("confirmDiv");
//        }
//    }
    var alertUserAboutLeaving = true;

    $j(window).bind('beforeunload', function(e) {
        if (alertUserAboutLeaving) {
            return leavePageAlert;
        }else {
            return;
        }
    });

//    $j("#plusPaymentBtnId").click(function(event){
//        console.log("add new payment");
//        obsArray = new Array();
//        $j("#receiptInput").val("");
//        $j.setupDiv('tbScreeningDiv');
//    });
//
});