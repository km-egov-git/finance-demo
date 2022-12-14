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
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%> 
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<div class="form-group">
	<c:choose>
     <c:when test="${refundable != null && !refundable.isEmpty()}">
      <c:choose>
		<c:when test="${headerFields.contains('fund')}">
			<c:choose>
				<c:when test="${mandatoryFields.contains('fund')}">
				<form:hidden path="" name="fundId" id="fundId" value="${egBillregister.egBillregistermis.fund.id }"/>
					<label class="col-sm-3 control-label text-right"><spring:message code="lbl.fund" />
						<span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.fund" data-first-option="false" id="fund" readonly="true" class="form-control" required="required"  >
							<form:options items="${funds}" itemValue="id" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.fund" cssClass="add-margin error-msg" />
					</div>
				</c:when>
				<c:otherwise>
				<form:hidden path="" name="fundId" id="fundId" value="${egBillregister.egBillregistermis.fund.id }"/>
					<label class="col-sm-3 control-label text-right"><spring:message code="lbl.fund" />
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.fund" data-first-option="false" id="fund" readonly="true" class="form-control"  >
							<form:options items="${funds}" itemValue="id" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.fund" cssClass="add-margin error-msg" />
					</div>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<label class="col-sm-3 control-label text-right"></label>
			<div class="col-sm-3 add-margin">
			</div>
		</c:otherwise>
	</c:choose>
     </c:when>
     <c:otherwise>
       <c:choose>
		<c:when test="${headerFields.contains('fund')}">
			<c:choose>
				<c:when test="${mandatoryFields.contains('fund')}">
				<form:hidden path="" name="fundId" id="fundId" value="${egBillregister.egBillregistermis.fund.id }"/>
					<label class="col-sm-3 control-label text-right"><spring:message code="lbl.fund" />
						<span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.fund" data-first-option="false" id="fund" class="form-control" required="required"  >
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${funds}" itemValue="id" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.fund" cssClass="add-margin error-msg" />
					</div>
				</c:when>
				<c:otherwise>
				<form:hidden path="" name="fundId" id="fundId" value="${egBillregister.egBillregistermis.fund.id }"/>
					<label class="col-sm-3 control-label text-right"><spring:message code="lbl.fund" />
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.fund" data-first-option="false" id="fund" class="form-control"  >
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${funds}" itemValue="id" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.fund" cssClass="add-margin error-msg" />
					</div>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<label class="col-sm-3 control-label text-right"></label>
			<div class="col-sm-3 add-margin">
			</div>
		</c:otherwise>
	</c:choose>
     </c:otherwise>
    </c:choose>


    <c:choose>
     <c:when test="${refundable != null && !refundable.isEmpty()}">
        <c:choose>
		<c:when test="${headerFields.contains('department')}">
			<c:choose>
				<c:when test="${mandatoryFields.contains('department')}">
					<label class="col-sm-2 control-label text-right"><spring:message code="lbl.department" />
						<span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.departmentcode" data-first-option="false" id="department" readonly="true" class="form-control" required="required">
							<form:options items="${departments}" itemValue="code" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.departmentcode" cssClass="add-margin error-msg" />
					</div>
				</c:when>
				<c:otherwise>
					<label class="col-sm-2 control-label text-right"><spring:message code="lbl.department" />
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.departmentcode" data-first-option="false" id="department" readonly="true" class="form-control">
							<form:options items="${departments}" itemValue="id" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.departmentcode" cssClass="add-margin error-msg" />
					</div>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<label class="col-sm-2 control-label text-right"></label>
			<div class="col-sm-3 add-margin">
			</div>
		</c:otherwise>
	</c:choose>
     </c:when>
     <c:otherwise>
	<c:choose>
		<c:when test="${headerFields.contains('department')}">
			<c:choose>
				<c:when test="${mandatoryFields.contains('department')}">
					<label class="col-sm-2 control-label text-right"><spring:message code="lbl.department" />
						<span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.departmentcode" data-first-option="false" id="department" class="form-control" required="required">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${departments}" itemValue="code" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.departmentcode" cssClass="add-margin error-msg" />
					</div>
				</c:when>
				<c:otherwise>
					<label class="col-sm-2 control-label text-right"><spring:message code="lbl.department" />
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.departmentcode" data-first-option="false" id="department" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${departments}" itemValue="id" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.departmentcode" cssClass="add-margin error-msg" />
					</div>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<label class="col-sm-2 control-label text-right"></label>
			<div class="col-sm-3 add-margin">
			</div>
		</c:otherwise>
	</c:choose>
   </c:otherwise>
  </c:choose>

	
</div>

<div class="form-group">
	<c:choose>
     <c:when test="${refundable != null && !refundable.isEmpty()}">
        <c:choose>
		<c:when test="${headerFields.contains('scheme')}">
			<form:hidden path="" name="schemeId" id="schemeId" value="${egBillregister.egBillregistermis.scheme.id }"/>
			<c:choose>
				<c:when test="${mandatoryFields.contains('scheme')}">
					<label class="col-sm-3 control-label text-right"><spring:message code="lbl.scheme" />
						<span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.schemeId" disabled="true" data-first-option="false" id="scheme" class="form-control" required="required">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
						</form:select>
						<form:errors path="egBillregistermis.schemeId" cssClass="add-margin error-msg" />
					</div>
				</c:when>
				<c:otherwise>
					<label class="col-sm-3 control-label text-right"><spring:message code="lbl.scheme" />
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.schemeId" disabled="true" data-first-option="false" id="scheme" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
						</form:select>
						<form:errors path="egBillregistermis.schemeId" cssClass="add-margin error-msg" />
					</div>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<label class="col-sm-3 control-label text-right"></label>
			<div class="col-sm-3 add-margin">
			</div>
		</c:otherwise>
	</c:choose>
     </c:when>
     <c:otherwise>
      <c:choose>
		<c:when test="${headerFields.contains('scheme')}">
			<form:hidden path="" name="schemeId" id="schemeId" value="${egBillregister.egBillregistermis.scheme.id }"/>
			<c:choose>
				<c:when test="${mandatoryFields.contains('scheme')}">
					<label class="col-sm-3 control-label text-right"><spring:message code="lbl.scheme" />
						<span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.schemeId" data-first-option="false" id="scheme" class="form-control" required="required">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
						</form:select>
						<form:errors path="egBillregistermis.schemeId" cssClass="add-margin error-msg" />
					</div>
				</c:when>
				<c:otherwise>
					<label class="col-sm-3 control-label text-right"><spring:message code="lbl.scheme" />
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.schemeId" data-first-option="false" id="scheme" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
						</form:select>
						<form:errors path="egBillregistermis.schemeId" cssClass="add-margin error-msg" />
					</div>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<label class="col-sm-3 control-label text-right"></label>
			<div class="col-sm-3 add-margin">
			</div>
		</c:otherwise>
	</c:choose>
     </c:otherwise>
  </c:choose>
	

<c:choose>
     <c:when test="${refundable != null && !refundable.isEmpty()}">
     <c:choose>
		<c:when test="${headerFields.contains('subscheme')}">
		<form:hidden path="" name="subSchemeId" id="subSchemeId" value="${egBillregister.egBillregistermis.subScheme.id }"/>
			<c:choose>
				<c:when test="${mandatoryFields.contains('subscheme')}">
					<label class="col-sm-2 control-label text-right"><spring:message code="lbl.subscheme" />
						<span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.subSchemeId" disabled="true" data-first-option="false" id="subScheme" class="form-control" required="required">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${subschemes}" itemValue="id" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.subSchemeId" cssClass="add-margin error-msg" />
					</div>
				</c:when>
				<c:otherwise>
					<label class="col-sm-2 control-label text-right"><spring:message code="lbl.subscheme" />
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.subSchemeId" disabled="true" data-first-option="false" id="subScheme" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${subschemes}" itemValue="id" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.subSchemeId" cssClass="add-margin error-msg" />
					</div>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<label class="col-sm-2 control-label text-right"></label>
			<div class="col-sm-3 add-margin">
			</div>
		</c:otherwise>
	</c:choose>
     </c:when>
     <c:otherwise>
	<c:choose>
		<c:when test="${headerFields.contains('subscheme')}">
		<form:hidden path="" name="subSchemeId" id="subSchemeId" value="${egBillregister.egBillregistermis.subScheme.id }"/>
			<c:choose>
				<c:when test="${mandatoryFields.contains('subscheme')}">
					<label class="col-sm-2 control-label text-right"><spring:message code="lbl.subscheme" />
						<span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.subSchemeId" data-first-option="false" id="subScheme" class="form-control" required="required">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${subschemes}" itemValue="id" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.subSchemeId" cssClass="add-margin error-msg" />
					</div>
				</c:when>
				<c:otherwise>
					<label class="col-sm-2 control-label text-right"><spring:message code="lbl.subscheme" />
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.subSchemeId" data-first-option="false" id="subScheme" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${subschemes}" itemValue="id" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.subSchemeId" cssClass="add-margin error-msg" />
					</div>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<label class="col-sm-2 control-label text-right"></label>
			<div class="col-sm-3 add-margin">
			</div>
		</c:otherwise>
	</c:choose>
     </c:otherwise>
</c:choose>     	
	
</div>

<div class="form-group">
<!-- fund source commented by abhishek on 07-04-2021 -->
	<%-- <c:choose>
		<c:when test="${headerFields.contains('fundsource')}">
		<form:hidden path="" name="fundSourceId" id="fundSourceId" value="${egBillregister.egBillregistermis.fundsource.id }"/>
			<c:choose>
				<c:when test="${mandatoryFields.contains('fundsource')}">
					<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sourcefund" />
						<span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.fundsource" data-first-option="false" id="fundSource" class="form-control" required="required">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${fundsources}" itemValue="id" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.fundsource" cssClass="add-margin error-msg" />
					</div>
				</c:when>
				<c:otherwise>
					<label class="col-sm-3 control-label text-right"><spring:message code="lbl.sourcefund" />
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.fundsource" data-first-option="false" id="fundSource" class="form-control" >
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${fundsources}" itemValue="id" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.fundsource" cssClass="add-margin error-msg" />
					</div>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<label class="col-sm-3 control-label text-right"></label>
			<div class="col-sm-3 add-margin">
			</div>
		</c:otherwise>
	</c:choose>
	 --%>
	<c:choose>
		<c:when test="${headerFields.contains('field')}">
			<c:choose>
				<c:when test="${mandatoryFields.contains('field')}">
					<label class="col-sm-2 control-label text-right"><spring:message code="lbl.field" />
						<span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.fieldid" data-first-option="false" id="field" class="form-control" required="required">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${fields}" itemValue="id" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.fieldid" cssClass="add-margin error-msg" />
					</div>
				</c:when>
				<c:otherwise>
					<label class="col-sm-2 control-label text-right"><spring:message code="lbl.functionary" />
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.fieldid" data-first-option="false" id="field" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${fields}" itemValue="id" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.fieldid" cssClass="add-margin error-msg" />
					</div>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<label class="col-sm-2 control-label text-right"></label>
			<div class="col-sm-3 add-margin">
			</div>
		</c:otherwise>
	</c:choose>
</div>

<div class="form-group">
	<c:choose>
		<c:when test="${headerFields.contains('functionary')}">
			<c:choose>
				<c:when test="${mandatoryFields.contains('functionary')}">
					<label class="col-sm-3 control-label text-right"><spring:message code="lbl.functionaryid" />
						<span class="mandatory"></span>
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.functionaryid" data-first-option="false" id="functionary" class="form-control" required="required">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${functionarys}" itemValue="id" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.functionaryid" cssClass="add-margin error-msg" />
					</div>
				</c:when>
				<c:otherwise>
					<label class="col-sm-3 control-label text-right"><spring:message code="lbl.functionaryid" />
					</label>
					<div class="col-sm-3 add-margin">
						<form:select path="egBillregistermis.functionaryid" data-first-option="false" id="functionary" class="form-control">
							<form:option value=""><spring:message code="lbl.select" /></form:option>
							<form:options items="${functionarys}" itemValue="id" itemLabel="name" />
						</form:select>
						<form:errors path="egBillregistermis.functionaryid" cssClass="add-margin error-msg" />
					</div>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<label class="col-sm-3 control-label text-right"></label>
			<div class="col-sm-3 add-margin">
			</div>
		</c:otherwise>
	</c:choose>
</div>