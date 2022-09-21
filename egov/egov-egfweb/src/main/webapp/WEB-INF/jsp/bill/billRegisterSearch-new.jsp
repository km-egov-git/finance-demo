<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  --%>


<%@ include file="/includes/taglibs.jsp"%>
<%@ page language="java"%>

<html>
<head>
<title><s:text name="bill.search.heading"></s:text></title>
</head>
<script>
/* $("#billresult").on('click', 'tbody tr td i.inbox-history', function (e) {
    $('.history-inbox').modal('show');
    historyTableContainer = $("#historyDetailTable");
    historyTableContainer.DataTable({
        "sDom": "<'row'<'col-xs-12 hidden col-right'f>r>t<'row buttons-margin'<'col-md-6 col-xs-12'i>" +
        "<'col-md-3 col-xs-6'l><'col-md-3 col-xs-6 text-right'p>>",
        //"aLengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
        "autoWidth": false,
        "paging": false,
        "destroy": true,
        "aaSorting": [],
        "oLanguage": {
            "sInfo": ""
        },
        "ajax": {
            "url": "inbox/history?stateId=" + tableContainer1.dataTable().fnGetData($(this).parent().parent(), 7),
            "dataSrc": ""
        },
        "columns": [
            {"data": "date", "width": "20%"},
            {"data": "sender", "width": "15%"},
            {"data": "task", "width": "20%"},
            {"data": "status", "width": "20%"},
            {"data": "details", "width": "20%"},
            {"data": "id", "visible": false, "searchable": false},
            {"data": "link", "visible": false, "searchable": false}

        ]
    });

    e.stopPropagation();
}); */

</script>
<body onload="changeMandatoryField()">
	<s:form name="billRegisterForm" action="billRegisterSearch"
		theme="simple">
		<jsp:include page="../budget/budgetHeader.jsp">
			<jsp:param name="heading" value="Voucher Search" />
		</jsp:include>
		<font style='color: red; font-weight: bold'>
			<p class="error-block" id="lblError"></p>
		</font>
		<span class="mandatory1"> <s:actionerror /> <s:fielderror />
			<s:actionmessage />
		</span>
		<div class="formmainbox">
			<div class="subheadnew">
				<s:text name="bill.search.heading"></s:text>
			</div>
			<table align="center" width="100%" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluebox">&nbsp;</td>
					<td class="bluebox"><s:text name="bill.search.expType" /> <span
						class="mandatory1">*</span></td>
					<td class="bluebox"><s:select name="expType" id="expType"
							list="dropdownData.expType" headerKey="-1"
							headerValue="----Choose----" value="%{expType}" /></td>
					<td class="greybox"><s:text name="bill.search.billnumber" /></td>
					<td class="greybox"><s:textfield name="billnumber"
							id="billnumber" value="%{billnumber}"
							onblur="changeMandatoryField()" /></td>
				</tr>
				<tr>
					<td class="bluebox">&nbsp;</td>
					<td class="greybox"><s:text name="bill.search.dateFrom" /> <span
						class="mandatory1" id="fromDateMandatory">*</span></td>

					<td class="greybox"><s:date name="billDateFrom"
							var="billDateFrom" format="dd/MM/yyyy" /> <s:textfield
							id="billDateFrom" name="billDateFrom" value="%{billDateFrom}"
							onkeyup="DateFormat(this,this.value,event,false,'3')"
							placeholder="DD/MM/YYYY" cssClass="form-control datepicker"
							data-inputmask="'mask': 'd/m/y'" /></td>

					<td class="greybox"><s:text name="bill.search.dateTo" /> <span
						class="mandatory1" id="toDateMandatory">*</span></td>

					<td class="greybox"><s:date name="billDateTo" var="billDateTo"
							format="dd/MM/yyyy" /> <s:textfield id="billDateTo"
							name="billDateTo" value="%{billDateTo}"
							onkeyup="DateFormat(this,this.value,event,false,'3')"
							placeholder="DD/MM/YYYY" cssClass="form-control datepicker"
							data-inputmask="'mask': 'd/m/y'" /></td>


				</tr>
				<jsp:include page="billSearchCommon-filter.jsp" />
			</table>
		</div>
		<div align="center" class="buttonbottom">
			<input type="submit" class="buttonsubmit" value='<s:text name="lbl.search"/>'  id="Search"
				name="button" onclick="return validateFormAndSubmit();" /> <input
				type="button" id="Close" value='<s:text name="lbl.close"/>' 
				onclick="javascript:window.parent.postMessage('close','*');" class="button" />
		</div>
		<br />
		<s:if test="%{billList.size!=0 || billList!=null}">
			<div id="listid" style="display: block">
				<table width="100%" align="center" cellpadding="0" cellspacing="0"
					class="setborder" style="border-collapse: inherit;" id="billresult">
					<tr>
						<th class="bluebgheadtd"><s:text name="lbl.sr.no"/>.</th>
						<th class="bluebgheadtd"><s:text name="lbl.expenditure.type"/></th>
						<th class="bluebgheadtd"><s:text name="lbl.bill.type"/></th>
						<th class="bluebgheadtd"><s:text name="bill.search.billnumber"/></th>
						<th class="bluebgheadtd"><s:text name="billDate"/></th>
						<th class="bluebgheadtd"><s:text name="lbl.bill.amount"/></th>
						<th class="bluebgheadtd"><s:text name="lbl.passed.amount"/></th>
						<th class="bluebgheadtd"><s:text name="lbl.bill.status"/></th>
						<th class="bluebgheadtd"><s:text name="lbl.pending.with"/></th>
						<!-- <th class="bluebgheadtd">History</th> -->
					</tr>

					<s:iterator var="p" value="billList" status="s">

						<tr>

							<td style="text-align: center"
								class="text-center bluebox setborder"><s:property
									value="#s.index+1" /></td>
							<td style="text-align: center"
								class="text-center bluebox setborder"><s:property
									value="%{expendituretype}" /></td>
							<td style="text-align: center"
								class="text-center bluebox setborder"><s:property
									value="%{billtype}" /></td>
							<td style="text-align: center"
								class="text-center bluebox setborder"><a href="#"
								onclick="openBill('<s:property value='%{sourcepath}' />')"><s:property
										value="%{billnumber}" /></a></td>
							<td style="text-align: center"
								class="text-center bluebox setborder"><s:date
									name="%{billdate}" format="dd/MM/yyyy" /></td>
							<td class="bluebox setborder" style="text-align: right"><s:text
									name="bill.format.number">
									<s:param value="%{billamount}" />
								</s:text></td>
							<td class="bluebox setborder" style="text-align: right"><s:text
									name="bill.format.number">
									<s:param value="%{passedamount}" />
								</s:text></td>

							<td style="text-align: center"
								class="text-center bluebox setborder"><s:property
									value="%{billstatus}" /></td>
							<td style="text-align: center"
								class="text-center bluebox setborder "><s:property
									value="%{ownerName}" /></td>
							<%-- <td>			
                			<button type="button" class="btn" value="<s:property
									value="%{billid}" />" onclick="getdata(this);"><i class="fa fa-history inbox-history history-size" class="tooltip-secondary" data-toggle="tooltip" title="History"></i>
                			</button>
           						</td> --%>
						</tr>
					</s:iterator>
				</table>
			</div>
		</s:if>
<div class="modal fade history-inbox" id="mymodal">
    <div class="modal-dialog history">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_top">&times;</button>
                <h4 class="modal-title" align="center">Task History</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-12">
                        <table id="historyDetailTable" class="table table-bordered datatable dataTable no-footer">
                            <thead>
                            <tr>
                                <th>Date</th>
                                <th>Sender</th>
                                <th>Nature Of Task</th>
                                <th>Status</th>
                                <th>Comments</th>
                            </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal" id="close_btm">Cancel</button>
            </div>
        </div>
    </div>
</div>
		<div id="msgdiv" style="display: none">
			<table align="center" class="tablebottom" width="80%">
				<tr>
					<th class="bluebgheadtd" colspan="7"><s:text name="msg.no.record.found"/>
					</td>
				</tr>
			</table>
		</div>
		<div id="loading" class="loading"
			style="width: 700; height: 700; display: none" align="center">
			<blink style="color: red"><s:text name="msg.searching.processing.please.wait"/> </blink>
		</div>



	</s:form>
	<script>
	
	
	
	jQuery("#close_btm,#close_top").on("click",function(){
		jQuery("#historyDetailTable tbody").empty();
	});
	function getdata(obj){
		//jQuery('#mymodal').modal('show');
		console.log(obj);
		var billid= obj.value;
		jQuery("#historyDetailTable tbody").empty();
		var url="${pageContext.request.contextPath}/bill/billregisterhistory.action?billregisterid="+billid;
		if(null!=billid){
			
			jQuery.getJSON( url, function( data ) {
						
						
						console.log("data: "+data);
						//var tst = 
						console.log("Length--->>"+data.length);
						var result = JSON.parse(data);
						console.log(result);
						console.log(result.length);
						//var i =0
						for(i=0;i<=result.length;i++)
						{
							console.log(i);
							var r = result[i];
							//console.log(JSON.stringfy(data[i]));
							console.log("result[i] "+r);
							//if(null!=data || data!="")
							//{
								jQuery("#historyDetailTable tbody").append('<tr>'+'<td>'+r.date+'</td>'
										+'<td>'+r.sender+'</td>'
										+'<td>'+r.task+'</td>'
										+'<td>'+r.status+'</td>'
										+'<td>'+r.details+'</td>'
										+'</tr>');
							//}
								
								//});
						}
						
						jQuery('#mymodal').modal('show');
						//jQuery('#mymodal').show();
						
				});
			
			
		}
		
	}
	
	
	
	 function validateFormAndSubmit(){
		 var amount=document.getElementById('amount').value;
		 var party=document.getElementById('partyName').value;
		 if(jQuery('#billnumber').val()!="")
			 {
				 if(jQuery('#expType').val()==-1)
					 {
						 jQuery('#lblError').html("<s:text name='msg.please.select.expenditure.type'/>");
						 return false
					 }
			 }
		 else {
				 if(!validate())
					 return false;
		 	  }
	       	document.billRegisterForm.action='${pageContext.request.contextPath}/bill/billRegisterSearch-search.action';
		 	document.billRegisterForm.submit();
		 	return true;
		 }
	function validate(){
	
		document.getElementById('lblError').innerHTML ="";
		if(document.getElementById('expType').value == -1){
			document.getElementById('lblError').innerHTML = "<s:text name='msg.please.select.expenditure.type'/>";
			return false;
		}
		if(document.getElementById('billDateFrom').value.trim().length == 0){
			document.getElementById('lblError').innerHTML = "<s:text name='msg.please.select.bill.from.date'/>";
			return false;
		}
		if(document.getElementById('billDateTo').value.trim().length == 0){
			document.getElementById('lblError').innerHTML = "<s:text name='msg.please.select.bill.to.date'/>";
			return false;
			
		}

		var fromDate=document.getElementById('billDateFrom').value;
		var toDate=document.getElementById('billDateTo').value;
		if(!DateValidation(fromDate,toDate))
			return false;
		
		 <s:if test="%{isFieldMandatory('fund')}"> 
				 if(null != document.getElementById('fundId') && document.getElementById('fundId').value == -1){

					document.getElementById('lblError').innerHTML = "<s:text name='msg.please.select.fund'/>";
					return false;
				 }
			 </s:if>
			<s:if test="%{isFieldMandatory('department')}"> 
				 if(null!= document.getElementById('vouchermis.departmentcode') && document.getElementById('vouchermis.departmentcode').value == -1){

					document.getElementById('lblError').innerHTML = "<s:text name='msg.please.select.department'/>";
					return false;
				 }
			</s:if>
			<s:if test="%{isFieldMandatory('scheme')}"> 
				 if(null!=document.getElementById('schemeid') &&  document.getElementById('schemeid').value == -1){

					document.getElementById('lblError').innerHTML = "<s:text name='msg.please.select.scheme'/>";
					return false;
				 }
			</s:if>
			<s:if test="%{isFieldMandatory('subscheme')}"> 
				 if(null!= document.getElementById('subschemeid') && document.getElementById('subschemeid').value == -1){

					document.getElementById('lblError').innerHTML = "<s:text name='msg.please.select.sub.scheme'/>";
					return false;
				 }
			</s:if>
			<s:if test="%{isFieldMandatory('functionary')}"> 
				 if(null!=document.getElementById('vouchermis.functionary') &&  document.getElementById('vouchermis.functionary').value == -1){

					document.getElementById('lblError').innerHTML = "<s:text name='msg.please.select.functionary'/>";
					return false;
				 }
			</s:if>
			<s:if test="%{isFieldMandatory('fundsource')}"> 
				 if(null !=document.getElementById('fundsourceId') &&  document.getElementById('fundsourceId').value == -1){

					document.getElementById('lblError').innerHTML = "<s:text name='msg.please.select.fund.source'/>";
					return false;
				}
			</s:if>
			<s:if test="%{isFieldMandatory('field')}"> 
				 if(null!= document.getElementById('vouchermis.divisionid') && document.getElementById('vouchermis.divisionid').value == -1){

					document.getElementById('lblError').innerHTML = "<s:text name='msg.please.select.field'/>";
					return false;
				 }
			</s:if>
			
			
		return true;
	}
function openBill(url){
		
			window.open(url,'','width=900, height=700,scrollbars=1');
			
		}
function doAfterSubmit(){
		document.getElementById('loading').style.display ='block';
		dom.get('msgdiv').style.display='none';
		dom.get('listid').style.display='none';
	}
 
			
String.prototype.trim = function () {
    return this.replace(/^\s*/, "").replace(/\s*$/, "");
}

	<s:if test="%{billList.size<=0}">
				dom.get('msgdiv').style.display='block';
				dom.get('listid').style.display='none';
			</s:if>
	<s:if test="%{billList.size!=0}">
				dom.get('msgdiv').style.display='none';
				document.getElementById('loading').style.display ='none';
				dom.get('listid').style.display='block';
	</s:if>	
	
function changeMandatoryField()
	{
		if(jQuery('#billnumber').val()!="")
			{
				jQuery("#fromDateMandatory").html("");
				jQuery("#toDateMandatory").html("");
				jQuery("#fundDateMandatory").html("");
			}
		else
			{
				jQuery("#fromDateMandatory").html("*");
				jQuery("#toDateMandatory").html("*");
				jQuery("#fundDateMandatory").html("*");
			}
		
	}
</script>

</body>

</html>