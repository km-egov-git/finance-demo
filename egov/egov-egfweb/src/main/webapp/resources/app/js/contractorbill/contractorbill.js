/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

var subLedgerDisplayName;
var detailTypeName;
var detailKeyName;
var debitCodes = new Array();
var debitAmountrowcount=0;
var creditAmoutrowcount=0;
var $workOrderId = 0;
var $contractorId = 0;
$(document).ready(function(){
	console.log("Browser Language ",navigator.language);
	$.i18n.properties({ 
		name: 'message', 
		path: '/services/EGF/resources/app/messages/', 
		mode: 'both',
		async: true,
	    cache: true,
		language: getLocale("locale"),
		callback: function() {
			console.log('File loaded successfully');
		}
	});
	
	$workOrderId = $('#workOrderId').val();
	$contractorId = $('#contractorId').val();
	patternvalidation(); 
	debitGlcode_initialize();
	creditGlcode_initialize();
	$('#fund').val($('#fund').val());
	if($contractorId){
		$('#contractor').val($contractorId);
		loadWorkOrder($contractorId);
	}
	if($workOrderId){
		$('#workOrder').val($workOrderId);
		loadMisAttributes($workOrderId);
	}
	creditAmoutrowcount=$("#creditAmoutrowcount").val() == undefined ? creditAmoutrowcount : $("#creditAmoutrowcount").val();
	debitAmountrowcount=$("#debitAmountrowcount").val() == undefined ? debitAmountrowcount : $("#debitAmountrowcount").val();
	calcualteNetpaybleAmount();
});

$('#contractor').change(function () {
	$workOrderId = "";
	$('#workOrder').empty();
	$('#workOrder').append($('<option>').text('Select from below').attr('value', ''));
	loadWorkOrder($('#contractor').val());
});

$('#workOrder').change(function () {
	//$('#fundId').val("");
	//$('#fundName').val("");
	//$('#departmentCode').val("");
	//$('#departmentName').val("");
	$('#schemeId').val("");
	$('#schemeName').val("");
	$('#subSchemeId').val("");
	$('#subSchemeName').val("");
	loadMisAttributes($('#workOrder').val());
});

$('.btn-wf-primary').click(function(){
	var button = $(this).attr('id');
	if (button != null && (button == 'Forward')) {
		if(!validateWorkFlowApprover(button))
			return false;
		if(!$("form").valid())
			return false;
		if(validate()){
			return true;
		}else
			return false;
		
	}else if (button != null && (button == 'CreateAndApprove')) {
		$('#approvalDepartment').removeAttr('required');
		$('#approvalDesignation').removeAttr('required');
		$('#approvalPosition').removeAttr('required');
		$('#approvalComent').removeAttr('required');
		if(!validateWorkFlowApprover(button))
			return false;
		if(!$("form").valid())
			return false;
		if(validate()){
			return true;
		}else
			return false;
	}else if (button != null && (button == 'SaveAsDraft')) {//added by Abhishek on 05042021
	       if(!validateWorkFlowApprover(button)){
	            return false;
	           }
	        if(true){
	            //deleteHiddenSubledgerRow();
	            return true;
	        }else{
	            return false;
	            }
	}else{
		if(!validateWorkFlowApprover(button))
			return false;
		if($("form").valid()){
			return true;
		}else
			return false;
	}
	return false;
});

function getCookie(name){
	let cookies = document.cookie;
	if(cookies.search(name) != -1){
		var keyValue = cookies.match('(^|;) ?' + name + '=([^;]*)(;|$)');
	    return keyValue ? keyValue[2] : null;
	}
}

function getLocale(paramName){
	return getCookie(paramName) ? getCookie(paramName) : navigator.language;
}

function debitGlcode_initialize() {
	 var custom = new Bloodhound({
	    datumTokenizer: function(d) { return d.tokens; },
	    queryTokenizer: Bloodhound.tokenizers.whitespace,
		   remote: {
	            url: '/services/EGF/common/getcontractordebitcodes?glcode=',
	            dataType: "json",
	            replace: function (url, query) {
					return url + query ;
				},
	            filter: function (data) {
	            	var responseObj = JSON.parse(data);
	                return $.map(responseObj, function (ct) {
	                    return {
	                        id: ct.id,
	                        name: ct.name,
	                        glcode: ct.glcode,
	                        issubledger: ct.isSubLedger,
	                        glcodesearch: ct.glcode+' ~ '+ct.name
	                    };
	                });
	            }
	        }
  });

  custom.initialize();
  var dt = $('.debitGlcode').typeahead({
  	hint : true,
		highlight : true,
		minLength : 1
		
	}, {
        displayKey: 'glcodesearch',
        source: custom.ttAdapter()
  }).on('typeahead:selected typeahead:autocompleted', function (event, data) {
	   
	   var originalglcodeid = data.id;
	   var flag = false;
	   $('#tbldebitdetails  > tbody > tr:visible[id="debitdetailsrow"]').each(function(index) {
		   var glcodeid =document.getElementById('debitDetails['+index+'].glcodeid').value;
			if( glcodeid!= "" && originalglcodeid == glcodeid) {
				flag = true;
			}
	   });
	  if(flag){
			bootbox.alert($.i18n.prop('msg.debit.code.already.added'), function() {
				var index= dt.length - 1;
				if(document.getElementById('debitDetails['+index+'].debitGlcode'))
					document.getElementById('debitDetails['+index+'].debitGlcode').value = "";
			});
		}else{
		   	$(this).parents("tr:first").find('.debitdetailid').val(data.id);
		   	$(this).parents("tr:first").find('.debitdetailname').val(data.name);
		}
  });
}

function creditGlcode_initialize() {
	 var custom = new Bloodhound({
	    datumTokenizer: function(d) { return d.tokens; },
	    queryTokenizer: Bloodhound.tokenizers.whitespace,
		   remote: {
	            url: '/services/EGF/common/getcontractorcreditcodes?glcode=',
	            dataType: "json",
	            replace: function (url, query) {
					return url + query  ;
				},
	            filter: function (data) {
	            	var responseObj = JSON.parse(data);
	                return $.map(responseObj, function (ct) {
	                    return {
	                        id: ct.id,
	                        name: ct.name,
	                        glcode: ct.glcode,
	                        issubledger: ct.isSubLedger,
	                        glcodesearch: ct.glcode+' ~ '+ct.name
	                    };
	                });
	            }
	        }
  });

  custom.initialize();

  $('.creditGlcode').typeahead({
  	hint : true,
		highlight : true,
		minLength : 1
		
	}, {		    
        displayKey: 'glcodesearch',
        source: custom.ttAdapter()
  }).on('typeahead:selected typeahead:autocompleted', function (event, data) {
	  
	  var originalglcodeid = data.id;
	   var flag = false;
	   $('#tblcreditdetails  > tbody > tr:visible[id="creditdetailsrow"]').each(function(index) {
		   var glcodeid =document.getElementById('creditDetails['+index+'].glcodeid').value;
			if( glcodeid!= "" && originalglcodeid == glcodeid) {
				flag = true;
			}
	   });
	   if(flag){
			bootbox.alert($.i18n.prop('msg.credit.code.already.added'), function() {
				var index= dt.length - 1;
				document.getElementById('creditDetails['+index+'].creditGlcode').value = "";
			});
		}else{
		  	$(this).parents("tr:first").find('.creditdetailid').val(data.id);
		  	$(this).parents("tr:first").find('.creditdetailname').val(data.name);
		}
  });
}

function addDebitDetailsRow() { 
	
	$('.debitGlcode').typeahead('destroy');
	$('.debitGlcode').unbind();
	var rowcount = $("#tbldebitdetails tbody tr").length;
	if (rowcount < 30) {
		if (document.getElementById('debitdetailsrow') != null) {
			addRow('tbldebitdetails','debitdetailsrow');
			$('#tbldebitdetails tbody tr:eq('+rowcount+')').find('.debitDetailGlcode').val('');
			$('#tbldebitdetails tbody tr:eq('+rowcount+')').find('.debitdetailname').val('');
			$('#tbldebitdetails tbody tr:eq('+rowcount+')').find('.debitAmount').val('');
			$('#tbldebitdetails tbody tr:eq('+rowcount+')').blur(calcualteNetpaybleAmount);
			debitGlcode_initialize();
			++debitAmountrowcount;
		}
	} else {
		  bootbox.alert($.i18n.prop('msg.limit.reached'));
	}
}

function deleteDebitDetailsRow(obj) {
	var rowcount=$("#tbldebitdetails tbody tr").length;
    if(rowcount<=1) {
		bootbox.alert($.i18n.prop('msg.this.row.can.not.be.deleted'));
		return false;
	} else {
		deleteRow(obj,'tbldebitdetails');
		--debitAmountrowcount;
		calcualteNetpaybleAmount();
		return true;
	}
}

function addCreditDetailsRow() { 
	
	$('.creditGlcode').typeahead('destroy');
	$('.creditGlcode').unbind();
	var rowcount = $("#tblcreditdetails tbody tr").length;
	if (rowcount < 30) {
		if (document.getElementById('creditdetailsrow') != null) {
			addRow('tblcreditdetails','creditdetailsrow');
			$('#tblcreditdetails tbody tr:eq('+rowcount+')').find('.creditDetailGlcode').val('');
			$('#tblcreditdetails tbody tr:eq('+rowcount+')').find('.creditdetailname').val('');
			$('#tblcreditdetails tbody tr:eq('+rowcount+')').find('.creditAmount').val('');
			$('#tblcreditdetails tbody tr:eq('+rowcount+')').find('.creditAmount').blur(calcualteNetpaybleAmount);
			creditGlcode_initialize();
			++creditAmoutrowcount;
		}
	} else {
		  bootbox.alert($.i18n.prop('msg.limit.reached'));
	}
}

function deleteCreditDetailsRow(obj) {
	var rowcount=$("#tblcreditdetails tbody tr").length;
    if(rowcount<=1) {
		bootbox.alert($.i18n.prop('msg.this.row.can.not.be.deleted'));
		return false;
	} else {
		deleteRow(obj,'tblcreditdetails');
		--creditAmoutrowcount;
		calcualteNetpaybleAmount();
		return true;
	}	
}


function validate(){
	var billamount = $("#billamount").val();
	var netpayableamount = $("#contractorNetPayableAmount")["0"].innerHTML;
	var debitamount = $("#contractorBillTotalDebitAmount")["0"].innerHTML;
	var creditamount = $("#contractorBillTotalCreditAmount")["0"].innerHTML;
	
	$("#passedamount").val(debitamount);
	
	if(debitamount != Number(Number(creditamount) + Number(netpayableamount)).toFixed(2)){
		bootbox.alert($.i18n.prop('msg.debit.and.credit.amount.is.not.matching'));
		return false;
	}
	
	if(debitamount == 0){
		bootbox.alert($.i18n.prop('msg.please.select.atleast.one.debit.details'));
		return false;
	}
	
	if(!$("#contractor-netPayableAmount").val())
	{
		bootbox.alert($.i18n.prop('msg.please.select.one.net.payable.account.detail'));
		return false;
	}
	
	if(parseFloat(billamount) < parseFloat(debitamount)){
		bootbox.alert($.i18n.prop('msg.bill.amount.should.not.greater.than.passed.amount'));
		return false;
	}
	
	if(parseFloat(debitamount) > parseFloat(billamount)){
		bootbox.alert($.i18n.prop('msg.passed.amount.should.not.be.greater.than.bill.amount'));
		return false;
	}
	
	return true;
}




function validateWorkFlowApprover(name) {
	document.getElementById("workFlowAction").value = name;
	var button = document.getElementById("workFlowAction").value;
	if (button != null && button == 'Submit') {
		$('#approvalDepartment').attr('required', 'required');
		$('#approvalDesignation').attr('required', 'required');
		$('#approvalPosition').attr('required', 'required');
		$('#approvalComent').removeAttr('required');
	}
	if (button != null && button == 'Reject') {
		$('#approvalDepartment').removeAttr('required');
		$('#approvalDesignation').removeAttr('required');
		$('#approvalPosition').removeAttr('required');
		$('#approvalComent').attr('required', 'required');
	}
	if (button != null && button == 'Cancel') {
		$('#approvalDepartment').removeAttr('required');
		$('#approvalDesignation').removeAttr('required');
		$('#approvalPosition').removeAttr('required');
		$('#approvalComent').attr('required', 'required');
	}
	if (button != null && button == 'Forward') {
		$('#approvalDepartment').attr('required', 'required');
		$('#approvalDesignation').attr('required', 'required');
		$('#approvalPosition').attr('required', 'required');
		$('#approvalComent').removeAttr('required');
		$('#narration').attr('required', 'required');
	}
	 if (button != null && button == 'SaveAsDraft') {//added by Abhishek on 05042021
	        
	        $('#approvalDepartment').removeAttr('required');
	        $('#approvalDesignation').removeAttr('required');
	        $('#approvalPosition').removeAttr('required');
	        $('#approvalComent').removeAttr('required');
	     
	        $('#narration').removeAttr('required');
	        $('#partyBillNumber').removeAttr('required');
	        $('#partyBillDate').removeAttr('required');
	        $('#billamount').removeAttr('required');
	        $('#billtype').removeAttr('required');
	    }
	if (button != null && button == 'Approve') {
		$('#approvalComent').removeAttr('required');
	}
	if (button != null && button == 'CreateAndApprove') {
		return true;//validateCutOff();
	}else
		return true;
	
	return true;
}
function validateCutOff()
{
	var cutofdate = $("#cutOffDate").val();
	var billdate = $("#billdate").val();
	var cutOffDateArray=cutofdate.split("/");
	var billDateArray=billdate.split("/");
	var cutOffDate = new Date(cutOffDateArray[1] + "/" + cutOffDateArray[0] + "/"
			+ cutOffDateArray[2]);
	var billDate = new Date(billDateArray[1] + "/" + billDateArray[0] + "/"
			+ billDateArray[2]);
	if(billDate<=cutOffDate)
	{
		return true;
	}
	else
	{
		bootbox.alert($.i18n.prop('msg.cutoff.warnig.message',cutofdate));
		return false;
	}
	return false;
}
function calcualteNetpaybleAmount(){

	var debitamt = 0;
	var creditamt = 0;
	for (var count = 0; count <=debitAmountrowcount; ++count) {

		if (null != document.getElementById("debitDetails[" + count
				+ "].debitamount")) {
			var val = document.getElementById("debitDetails[" + count
					+ "].debitamount").value;
			if (val != "" && !isNaN(val)) {
//				debitamt = debitamt + parseFloat(val);
				debitamt = parseFloat(Number(debitamt) + Number(val)).toFixed(2);
				document.getElementById("debitDetails[" + count + "].debitamount").value = Number(val).toFixed(2);
			}
		}
	}

	for (var count = 0; count <=creditAmoutrowcount; ++count) {

		if (null != document.getElementById("creditDetails[" + count
				+ "].creditamount")) {
			var val = document.getElementById("creditDetails[" + count
					+ "].creditamount").value;
			if (val != "" && !isNaN(val)) {
//				creditamt = creditamt + parseFloat(val);
				creditamt = parseFloat(Number(creditamt) + Number(val)).toFixed(2);
				document.getElementById("creditDetails[" + count + "].creditamount").value = Number(val).toFixed(2);
			}
		}
	}
	netPayableAmount=amountConverter(debitamt-creditamt);
	$("#contractor-netPayableAmount").val(netPayableAmount);
	$("#contractorNetPayableAmount").html(netPayableAmount);
	$("#contractorBillTotalDebitAmount").html(debitamt);
	$("#contractorBillTotalCreditAmount").html(creditamt);
}
function amountConverter(amt) {
	var formattedAmt = amt.toFixed(2);
	return formattedAmt;
}

function loadWorkOrder(contractorId){
	if (!contractorId) {
		$('#workOrder').empty();
		$('#workOrder').append($('<option>').text('Select from below').attr('value', ''));
		$('#workOrder').empty();
		$('#workOrder').append($('<option>').text('Select from below').attr('value', ''));
		return;
	} else {
		
		$.ajax({
			method : "GET",
			url : "/services/EGF/common/getworkordersbycontractorid",
			data : {
				contractorId : contractorId
			},
			async : true
		}).done(
				function(response) {
					$('#workOrder').empty();
					$('#workOrder').append($("<option value=''>Select from below</option>"));
					$.each(response, function(index, value) {
						var selected="";
						if($workOrderId && $workOrderId==value.orderNumber)
						{
								selected="selected";
						}
						$('#workOrder').append($('<option '+ selected +'>').text(value.name).attr('value', value.orderNumber));
					});
				});

	}
}


function loadMisAttributes(orderNumber){
	if (!orderNumber) {
		//$('#fundId').val("");
		//$('#fundName').val("");
		//$('#departmentCode').val("");
		//$('#departmentName').val("");
		$('#schemeId').val("");
		$('#schemeName').val("");
		$('#subSchemeId').val("");
		$('#subSchemeName').val("");
		return;
	} else {
		
		$.ajax({
			method : "GET",
			url : "/services/EGF/common/getworkorderbyordernumber",
			data : {
				orderNumber : orderNumber
			},
			async : true
		}).done(
				function(response) {
					$.each(response, function(index, value) {
						//$('#fundId').val(value.fund.id);
						//$('#fundId').val(document.getElementById("fundnew").value);
						//$('#fundName').val(value.fund.name);
						//$('#fundName').val(document.getElementById("fundnamenew").value);
						//$('#departmentCode').val(value.department);
						//$('#departmentCode').val(document.getElementById("departmentnew").value);
						//$('#departmentName').val(value.description);
						//$('#departmentName').val(document.getElementById("departmentnamenew").value);
						$('#schemeId').val(value.scheme.id);
						$('#schemeName').val(value.scheme.name);
						$('#subSchemeId').val(value.subScheme.id);
						$('#subSchemeName').val(value.subScheme.name);
					});
				});

	}
}