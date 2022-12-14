/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2018  eGovernments Foundation
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
package org.egov.eis.web.controller.workflow;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.microservice.models.Assignment;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.infra.workflow.matrix.entity.WorkFlowDeptDesgMap;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.matrix.service.CustomizedWorkFlowService;
import org.egov.infra.workflow.matrix.service.WorkFlowDeptDesgMapService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public abstract class GenericWorkFlowController {
	private static final String CURRENT_STATE = "currentState";
	private static final String ADDITIONALRULE = "additionalRule";
	
    @Autowired
    protected CustomizedWorkFlowService customizedWorkFlowService;

    @Autowired
    protected DepartmentService departmentService;
    
    @Autowired
    protected WorkFlowDeptDesgMapService workFlowDeptDesgMapService;

    @Autowired
    HttpServletRequest serRequest;

    @Autowired
    MicroserviceUtils microserviceUtils;

    @ModelAttribute(value = "approvalDepartmentList")
    public List<Department> addAllDepartments() {
        List<Department> deptlist = getDepartmentsFromMs();

        return deptlist;
    }

    @ModelAttribute("workflowcontainer")
    public WorkflowContainer getWorkflowContainer() {
        return new WorkflowContainer();
    }

    /**
     * @param prepareModel
     * @param model
     * @param container This method we are calling In GET Method..
     */
    public void prepareWorkflow(final Model prepareModel, final StateAware model,
            final WorkflowContainer container) {
    	if(null != model)
    	{
    		System.out.println("Type  : "+model.getStateType());
    	}
    	prepareWorkflow(prepareModel, model, container, false);
    }
    
    /**
     * @param prepareModel
     * @param model
     * @param container
     * @param isWfDeptFromMap
     */
    protected void prepareWorkflow(final Model prepareModel, final StateAware model,
            final WorkflowContainer container, boolean isWfDeptFromMap) {

    	if(isWfDeptFromMap) {
    		List<Department> departments=null;
    		String currentState = "";
    		String additionalRule = "";
    		String objectType= model.getStateType();

    		if(prepareModel.containsAttribute(CURRENT_STATE)) {
            	currentState = prepareModel.asMap().get(CURRENT_STATE).toString();
            }
    		if(prepareModel.containsAttribute(ADDITIONALRULE)) {
    			additionalRule = prepareModel.asMap().get(ADDITIONALRULE).toString();
            }
    		
    		List<WorkFlowDeptDesgMap> deptDesgMap = null;
    		if(!StringUtils.isBlank(additionalRule)) {
    			deptDesgMap = workFlowDeptDesgMapService.findByObjectTypeAndCurrentStateAndAddRule(objectType, currentState, additionalRule);
    		}else {
    			deptDesgMap = workFlowDeptDesgMapService.findByObjectTypeAndCurrentState(objectType, currentState);
    		}
    		if(!CollectionUtils.isEmpty(deptDesgMap)) {
    			String deptCodes = deptDesgMap.stream().map(WorkFlowDeptDesgMap::getNextDepartment).collect(Collectors.joining(","));
    			departments = getDepartmentsFromMs(deptCodes);
    		}
    		
    		prepareModel.addAttribute("approverDepartmentList", departments);
    	}else {
    		prepareModel.addAttribute("approverDepartmentList", addAllDepartments());
    	}
    	populateActions(prepareModel, model, container);
    }
    
    private void populateActions(final Model prepareModel, final StateAware model,
            final WorkflowContainer container) {

        prepareModel.addAttribute("validActionList", getValidActions(model, container));
        prepareModel.addAttribute("nextAction", getNextAction(model, container));

    }

    /**
     * @param model
     * @param container
     * @return NextAction From Matrix With Parameters Type,CurrentState,CreatedDate
     */
    public String getNextAction(final StateAware model, final WorkflowContainer container) {

        WorkFlowMatrix wfMatrix = null;
        if (model != null && model.getId() != null)
            if (model.getCurrentState() == null)
                wfMatrix = customizedWorkFlowService.getWfMatrix(model.getStateType(),
                        container.getWorkFlowDepartment(), container.getAmountRule(), container.getAdditionalRule(),
                        State.DEFAULT_STATE_VALUE_CREATED, container.getPendingActions(), model.getCreatedDate(),
                        container.getCurrentDesignation());
            else
                wfMatrix = customizedWorkFlowService.getWfMatrix(model.getStateType(),
                        container.getWorkFlowDepartment(), container.getAmountRule(), container.getAdditionalRule(),
                        model.getCurrentState().getValue(), container.getPendingActions(), model.getCreatedDate(),
                        container.getCurrentDesignation());
        if(null != wfMatrix)
        {
        	System.out.println("NextAction : "+wfMatrix.getNextAction());
        }
        
        return wfMatrix == null ? "" : wfMatrix.getNextAction();
    }

    /**
     * @param model
     * @param container
     * @return List of WorkFlow Buttons From Matrix By Passing parametres Type,CurrentState,CreatedDate
     */
    public List<String> getValidActions(final StateAware model, final WorkflowContainer container) {
        List<String> validActions = null;
        if (model == null || model.getId() == null || model.getCurrentState() == null
                || model.getCurrentState().getValue().equals("Closed")
                || model.getCurrentState().getValue().equals("END"))
            validActions = Arrays.asList("Forward");
        	 
         if (model.getCurrentState() != null)
        	 
        	
            validActions = customizedWorkFlowService.getNextValidActions(model.getStateType(),
                    container.getWorkFlowDepartment(), container.getAmountRule(), container.getAdditionalRule(),
                    model.getCurrentState().getValue(), container.getPendingActions(), model.getCreatedDate(),
                    container.getCurrentDesignation());
        
        		
        	
        else
            validActions = customizedWorkFlowService.getNextValidActions(model.getStateType(),
                    container.getWorkFlowDepartment(), container.getAmountRule(), container.getAdditionalRule(),
                    State.DEFAULT_STATE_VALUE_CREATED, container.getPendingActions(), model.getCreatedDate(),
                    container.getCurrentDesignation());
        System.out.println("Valid Actions : "+validActions);
        return validActions;
    }

    public List<Department> getDepartmentsFromMs() {

        List<Department> departments = microserviceUtils.getDepartments();

        return departments;

    }

    public List<Department> getDepartmentsFromMs(String codes) {

        List<Department> departments = microserviceUtils.getDepartments(codes);

        return departments;

    }
    
}
