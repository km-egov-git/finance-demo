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


<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>
<%@ taglib prefix="egov" tagdir="/WEB-INF/tags"%>
<tr>
	<td style="width: 5%"></td>
	<s:if test="%{shouldShowHeaderField('fund')}">
		<td class="greybox"><s:text name="voucher.fund" /> <s:if
				test="%{isFieldMandatory('fund')}">
				<span class="bluebox"><span class="mandatory1">*</span></span>
			</s:if></td>
		<td class="greybox"><s:select name="fundId" id="fundId"
				list="dropdownData.fundList" listKey="id" listValue="name"
				onChange="populateSchemes(this);loadBank(this);"
				value="%{fundId.id}" /></td>
	</s:if>
	<s:else>
		<td class="greybox"></td>
		<td class="greybox"></td>
	</s:else>
	<s:if test="%{shouldShowHeaderField('department')}">
		<td class="greybox" id="deptLabel"><s:text
				name="voucher.department" /> <s:if
				test="%{isFieldMandatory('department')}">
				<span class="bluebox"><span class="mandatory1">*</span></span>
			</s:if></td>
		<td class="greybox"><s:select name="vouchermis.departmentcode" 
				id="vouchermis.departmentid" list="dropdownData.departmentList"
				listKey="code" listValue="name" 
				value="voucherHeader.vouchermis.departmentcode" /></td>
	</s:if>
	<s:else>
		<td class="greybox"></td>
		<td class="greybox"></td>
	</s:else>
</tr>
<s:if test="%{shouldShowHeaderField('scheme') || shouldShowHeaderField('subscheme')}">
	<tr>
		<td style="width: 5%"></td>
		<s:if test="%{shouldShowHeaderField('scheme')}">
			<egov:ajaxdropdown id="scheme" fields="['Text','Value']"
				dropdownId="schemeid" url="voucher/common-ajaxLoadSchemes.action" />
	
			<td class="greybox"><s:text name="voucher.scheme" /> <s:if
					test="%{isFieldMandatory('scheme')}">
					<span class="mandatory1">*</span>
				</s:if></td>
			<td class="greybox"><s:select list="dropdownData.schemeList"
					name="vouchermis.schemeid" id="schemeid" listKey="id"
					listValue="name" headerKey="-1" headerValue="%{getText('lbl.choose.options')}"
					onChange="populatesubSchemes(this)"
					value="voucherHeader.vouchermis.schemeid.id" /></td>
		</s:if>
		<s:else>
			<td class="greybox"></td>
			<td class="greybox"></td>
		</s:else>
		<s:if test="%{shouldShowHeaderField('subscheme')}">
			<egov:ajaxdropdown id="subscheme" fields="['Text','Value']"
				dropdownId="subschemeid"
				url="voucher/common-ajaxLoadSubSchemes.action" />
			<td class="bluebox"><s:text name="voucher.subscheme" /> <s:if
					test="%{isFieldMandatory('subscheme')}">
					<span class="mandatory1">*</span>
				</s:if></td>
			<td class="bluebox"><s:select name="vouchermis.subschemeid"
					id="subschemeid" list="dropdownData.subschemeList" listKey="id"
					listValue="name" headerKey="-1" headerValue="%{getText('lbl.choose.options')}"
					value="voucherHeader.vouchermis.subschemeid.id"
					onChange="populateFundSource(this)" /></td>
		</s:if>
		<s:else>
			<td class="greybox"></td>
			<td class="greybox"></td>
		</s:else>
	</tr>
</s:if>
<tr>
	<td style="width: 5%"></td>

	<s:if test="%{shouldShowHeaderField('fundsource')}">
		<egov:ajaxdropdown id="fundsource" fields="['Text','Value']"
			dropdownId="fundsourceId"
			url="voucher/common-ajaxLoadFundSource.action" />
		<td class="bluebox"><s:text name="voucher.fundsource" /> <s:if
				test="%{isFieldMandatory('fundsource')}">
				<span class="bluebox"><span class="mandatory1">*</span></span>
			</s:if></td>
		<td class="bluebox"><s:select name="vouchermis.fundsource"
				id="fundsourceId" list="dropdownData.fundsourceList" listKey="id"
				listValue="name" headerKey="-1" headerValue="%{getText('lbl.choose.options')}"
				value="voucherHeader.vouchermis.fundsource.id" /></td>
	</s:if>
	<s:else>
		<td class="greybox"></td>
		<td class="greybox"></td>
	</s:else>
	<s:if test="%{shouldShowHeaderField('field')}">
		<td class="greybox"><s:text name="voucher.field" /> <s:if
				test="%{isFieldMandatory('field')}">
				<span class="mandatory1">*</span>
			</s:if></td>
		<td class="greybox"><s:select name="vouchermis.divisionid"
				id="vouchermis.divisionid" list="dropdownData.fieldList"
				listKey="id" listValue="name" headerKey="-1"
				headerValue="%{getText('lbl.choose.options')}"
				value="voucherHeader.vouchermis.divisionid.id" /></td>
	</s:if>
	<s:else>
		<td class="greybox"></td>
		<td class="greybox"></td>
	</s:else>
</tr>
<tr>
	<td style="width: 5%"></td>
	<s:if test="%{shouldShowHeaderField('function')}">
		<td id="functionnametext" class="bluebox"><s:text
				name="voucher.function" /> 
				<span class="bluebox"><span class="mandatory1">*</span></span>
			</td>
		<td class="bluebox"><s:select name="vouchermis.function"
				id="vouchermis.function" list="dropdownData.functionList"
				listKey="id" listValue="name" value="%{vouchermis.function.id}" /></td>
	</s:if>

	<s:else>
		<td class="greybox"></td>
		<td class="greybox"></td>
	</s:else>
	<s:if test="%{shouldShowHeaderField('functionary')}">
		<td class="bluebox"><s:text name="voucher.functionary" /> <s:if
				test="%{isFieldMandatory('functionary')}">
				<span class="bluebox"><span class="mandatory1">*</span></span>
			</s:if></td>
		<td class="bluebox"><s:select name="vouchermis.functionary"
				id="vouchermis.functionary" list="dropdownData.functionaryList"
				listKey="id" listValue="name" headerKey="-1"
				headerValue="%{getText('lbl.choose.options')}"
				value="voucherHeader.vouchermis.functionary.id" /></td>
	</s:if>
	<s:else>
		<td class="greybox"></td>
		<td class="greybox"></td>
	</s:else>
	<input type="hidden" name="voucherHeader.allowNegetive" id="voucherHeader.allowNegetive" />

</tr>
<script type="text/javascript">
function populateSchemes(fund){
	if(null != document.getElementById("schemeid")){
		//populateschemeid({fundId:fund.options[fund.selectedIndex].value});
		populateschemeid({fundId:"1"});
		populatesubschemeid({schemeId:-1});
	//	populatefundsourceId({subSchemeId:-1});	
	}
		
}
function populatesubSchemes(scheme){
	
	populatesubschemeid({schemeId:scheme.options[scheme.selectedIndex].value});	
	populatefundsourceId({subSchemeId:-1});
}
function populateFundSource(subSchemeId){
	
	populatefundsourceId({subSchemeId:subSchemeId.options[subSchemeId.selectedIndex].value});	
}
function validateMIS(){
	// Javascript validation of the MIS Manadate attributes.
			<s:if test="%{isFieldMandatory('vouchernumber')}"> 
				 if(null != document.getElementById('voucherNumber') && document.getElementById('voucherNumber').value.trim().length == 0 ){
					 bootbox.alert("Please enter a voucher number");
					//document.getElementById('lblError').innerHTML = "<s:text name='msg.please.enter.voucher.number'/> ";
					return false;
				 }
			 </s:if>
		 <s:if test="%{isFieldMandatory('voucherdate')}"> 
				 if(null != document.getElementById('voucherDate') && document.getElementById('voucherDate').value.trim().length == 0){
					 bootbox.alert("Please enter the voucher date");
					//document.getElementById('lblError').innerHTML = "<s:text name='msg.please.enter.voucher.date'/>";
					return false;
				 }
			 </s:if>
		 <s:if test="%{isFieldMandatory('fund')}"> 
				 if(null != document.getElementById('fundId') && document.getElementById('fundId').value == -1){
					 bootbox.alert("Please Select a fund");
					//document.getElementById('lblError').innerHTML = "<s:text name='msg.please.select.fund'/>";
					return false;
				 }
			 </s:if>
			<s:if test="%{isFieldMandatory('department')}"> 
				 if(null!= document.getElementById('vouchermis.departmentid') && document.getElementById('vouchermis.departmentid').value == -1){
					 alert("dep2");
						bootbox.alert("Please select a department");
					//document.getElementById('lblError').innerHTML = "<s:text name='msg.please.select.department'/>";
					return false;
				 }
			</s:if>
			<s:if test="%{isFieldMandatory('scheme')}"> 
				 if(null!=document.getElementById('schemeid') &&  document.getElementById('schemeid').value == -1){
					 bootbox.alert("Please select a scheme");
					//document.getElementById('lblError').innerHTML = "<s:text name='msg.please.select.scheme'/>";
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
					 bootbox.alert("Please select a functionary");
					//document.getElementById('lblError').innerHTML = "<s:text name='msg.please.select.functionary'/>";
					return false;
				 }
			</s:if>
			<s:if test="%{isFieldMandatory('fundsource')}"> 
				 if(null !=document.getElementById('fundsourceId') &&  document.getElementById('fundsourceId').value == -1){
					 bootbox.alert("Please select a fundsource");
					//document.getElementById('lblError').innerHTML = "<s:text name='msg.please.select.fundsource'/>";
					return false;
				}
			</s:if>
			<s:if test="%{isFieldMandatory('field')}"> 
				 if(null!= document.getElementById('vouchermis.divisionid') && document.getElementById('vouchermis.divisionid').value == -1){
					 bootbox.alert("Please select a field");
					//document.getElementById('lblError').innerHTML = "<s:text name='msg.please.select.field'/>";
					return false;
				 }
			</s:if>
			<s:if test="%{isFieldMandatory('function')}">                     
				 if(null!= document.getElementById('functionId') && document.getElementById('functionId').value == -1){
					 alert("function11");
						bootbox.alert("msg.please.select.function")
					// document.getElementById('lblError').innerHTML = "<s:text name='msg.please.select.function'/>";                                   
					return false;
				 }            
			</s:if>
			return  true;
}
	</script>
