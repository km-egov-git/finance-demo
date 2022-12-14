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
package org.egov.services.instrument;

import java.util.Date;

import org.apache.log4j.Logger;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.instrument.DishonorCheque;
import org.egov.pims.commons.Position;
import org.egov.pims.service.EisUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class DishonorChequeService extends PersistenceService<DishonorCheque, Long> {

    private static final Logger LOGGER = Logger.getLogger("DishonorChequeService.class");
    private SimpleWorkflowService<DishonorCheque> dishonorChqWorkflowService;

    private EisUtilService eisService;
    private FinancialIntegrationService financialIntegrationService;
    @Autowired
	protected MicroserviceUtils microserviceUtils;

    public DishonorChequeService() {
        super(DishonorCheque.class);
    }

    public DishonorChequeService(Class<DishonorCheque> type) {
        super(type);
    }

    public DishonorCheque approve(final DishonorCheque dishonorChq, final String workFlowAction, final String approverComments)
    {
        startWorkflow(dishonorChq, workFlowAction, approverComments);
        return dishonorChq;
    }

    public void startWorkflow(final DishonorCheque dishonorCheque, final String workFlowAction, final String approverComments)
    {
        // Get cheque creator details

        if (null == dishonorCheque.getState()) {
            final Position pos = eisService.getPrimaryPositionForUser(dishonorCheque.getPayinSlipCreator().longValue(),
                    new Date());
            if (LOGGER.isDebugEnabled())
                LOGGER.error(pos.getName() + "" + pos.getId());
            // TODO call the updateSourceInstrumentVoucher here
            if (null != financialIntegrationService)
                financialIntegrationService.updateSourceInstrumentVoucher(
                        FinancialIntegrationService.EVENT_INSTRUMENT_DISHONOR_INITIATED, dishonorCheque.getInstrumentHeader()
                                .getId());
            dishonorCheque.transition().start().withOwner(pos).withComments("DishonorCheque Work flow started").withOwnerName((pos.getId() != null && pos.getId() > 0L) ? getEmployeeName(pos.getId()):"");
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("---------" + dishonorCheque);
            dishonorChqWorkflowService.transition("forward", dishonorCheque, "Created by SM");
        }

        if (null != workFlowAction && !"".equals(workFlowAction)) {
            final String comments = null == approverComments || "".equals(approverComments.trim()) ? "" : approverComments;
            dishonorChqWorkflowService.transition(workFlowAction.toLowerCase(), dishonorCheque, comments);
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.error("---------" + dishonorCheque.getState().getId());

    }

    /*
     * public void startChequeWorkflow(DishonorCheque dishonorCheque,String workFlowAction,String approverComments) { // Get
     * cheque creator details if(null == dishonorCheque.getState()){ Position pos =
     * eisService.getPrimaryPositionForUser(dishonorCheque.getApproverPositionId(),new Date()); dishonorCheque =
     * (DishonorCheque)dishonorCheque.start().pos, "DishonorCheque Work flow started");
     * dishonorChequeWorkflowService.transition("forward", dishonorCheque, "Created by SM"); } if((null != workFlowAction) &&
     * !"".equals(workFlowAction) ){ String comments= ((null == approverComments) ||
     * "".equals(approverComments.trim()))?"":approverComments;
     * dishonorChequeWorkflowService.transition(workFlowAction.toLowerCase(),dishonorCheque, comments); } }
     */

    public SimpleWorkflowService<DishonorCheque> getDishonorChqWorkflowService() {
        return dishonorChqWorkflowService;
    }

    public void setDishonorChqWorkflowService(
            final SimpleWorkflowService<DishonorCheque> dishonorChqWorkflowService) {
        this.dishonorChqWorkflowService = dishonorChqWorkflowService;
    }

    public EisUtilService getEisService() {
        return eisService;
    }

    public void setEisService(final EisUtilService eisService) {
        this.eisService = eisService;
    }

    public FinancialIntegrationService getFinancialIntegrationService() {
        return financialIntegrationService;
    }

    public void setFinancialIntegrationService(
            final FinancialIntegrationService financialIntegrationService) {
        this.financialIntegrationService = financialIntegrationService;
    }
    
    public String getEmployeeName(Long empId){
        
        return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
     }

}
