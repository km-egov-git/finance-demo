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
<head>
<script>
function resetPage(){
	jQuery("#results").empty();
}
</script>
</head>
<div class="formmainbox">
	<div class="formheading"></div>
	<div class="subheadnew">Deduction report</div>

	<s:form action="pendingTDSReport" theme="simple"
		name="pendingTDSReport">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td class="greybox" width="10%">Recovery Code:<span
					class="mandatory1">*</span></td>
				<td class="greybox"><s:select name="recovery" id="recovery"
						list="dropdownData.recoveryListReport" listKey="id" listValue="type+'-'+recoveryName"
						headerKey="-1" headerValue="----Choose----" /></td>
				<td class="greybox" width="10%">Fund:<span class="mandatory1">*</span></td>
				<td class="greybox"><s:select name="fund" id="fund"
						list="dropdownData.fundList" listKey="id" listValue="name"
						headerKey="-1" headerValue="----Choose----" /></td>

			</tr>
			<tr>
				<td class="bluebox" width="10%"><s:text name="from.date" /><span
					class="mandatory1">*</span></td>
				<td class="bluebox"><s:textfield name="fromDate" id="fromDate"
						cssStyle="width:100px" value='%{getFormattedDate(fromDate)}'
						onkeyup="DateFormat(this,this.value,event,false,'3')" /><a
					href="javascript:show_calendar('pendingTDSReport.fromDate');"
					style="text-decoration: none">&nbsp;<img
						src="/services/egi/resources/erp2/images/calendaricon.gif" border="0" /></a>(dd/mm/yyyy)<br />
				</td>

				<td class="bluebox" width="10%">As On Date:<span
					class="mandatory1">*</span></td>
				<td class="bluebox"><s:textfield name="asOnDate" id="asOnDate"
						cssStyle="width:100px" value='%{getFormattedDate(asOnDate)}'
						onkeyup="DateFormat(this,this.value,event,false,'3')" /><a
					href="javascript:show_calendar('pendingTDSReport.asOnDate');"
					style="text-decoration: none">&nbsp;<img
						src="/services/egi/resources/erp2/images/calendaricon.gif" border="0" /></a>(dd/mm/yyyy)<br />
				</td>
			</tr>
			<tr>
				<td class="greybox" width="10%">Department:</td>
				<td class="greybox"><s:select name="department" id="department"
						list="dropdownData.departmentList" listKey="code"
						listValue="name" headerKey="-1" headerValue="----Choose----" />
				</td>
			</tr>
		</table>
		<br />
		<div class="buttonbottom">
			<input type="button" value="Search" class="buttonsubmit"
				onclick="return getDeductionData()" /> &nbsp;
			<s:reset name="button" type="submit" cssClass="button" id="button"
				value="Reset" onclick="resetPage();"/>
			<input type="button" value="Close" onclick="window.parent.postMessage('close','*');window.close();"
				Class="button" />
		</div>
		<s:hidden name="detailKey" id="detailKey"></s:hidden>
	</s:form>
</div>

<div id="results"></div>
