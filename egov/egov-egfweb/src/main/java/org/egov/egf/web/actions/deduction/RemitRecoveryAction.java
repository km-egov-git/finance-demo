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
/**
 *
 */
package org.egov.egf.web.actions.deduction;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.billsaccounting.services.CreateVoucher;
import org.egov.billsaccounting.services.VoucherConstant;
import org.egov.commons.Bank;
import org.egov.commons.Bankaccount;
import org.egov.commons.CFunction;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Functionary;
import org.egov.commons.Fund;
import org.egov.commons.Fundsource;
import org.egov.commons.Scheme;
import org.egov.commons.SubScheme;
import org.egov.commons.Vouchermis;
import org.egov.commons.dao.BankHibernateDAO;
import org.egov.commons.dao.FinancialYearHibernateDAO;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.dao.FundHibernateDAO;
import org.egov.commons.service.BankAccountService;
import org.egov.commons.service.FunctionService;
import org.egov.commons.utils.EntityType;
import org.egov.dao.voucher.VoucherHibernateDAO;
import org.egov.deduction.model.EgRemittance;
import org.egov.deduction.model.EgRemittanceDetail;
import org.egov.egf.commons.EgovCommon;
import org.egov.egf.model.TDSEntry;
import org.egov.egf.web.actions.payment.BasePaymentAction;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.config.persistence.datasource.routing.annotation.ReadOnly;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.script.entity.Script;
import org.egov.infra.script.service.ScriptService;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.model.bills.Miscbilldetail;
import org.egov.model.deduction.RemittanceBean;
import org.egov.model.instrument.InstrumentHeader;
import org.egov.model.payment.Paymentheader;
import org.egov.model.recoveries.Recovery;
import org.egov.model.service.RecoveryService;
import org.egov.model.voucher.CommonBean;
import org.egov.model.voucher.WorkflowBean;
import org.egov.payment.services.PaymentActionHelper;
import org.egov.services.deduction.RemitRecoveryService;
import org.egov.services.masters.BankService;
import org.egov.services.payment.PaymentService;
import org.egov.services.voucher.VoucherService;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.exilant.GLEngine.ChartOfAccounts;
import com.exilant.GLEngine.Transaxtion;
import com.opensymphony.xwork2.validator.annotations.Validation;

@ParentPackage("egov")
@Validation
@Results({ @Result(name = RemitRecoveryAction.NEW, location = "remitRecovery-" + RemitRecoveryAction.NEW + ".jsp"),
        @Result(name = "messages", location = "remitRecovery-messages.jsp"),
        @Result(name = "view", location = "remitRecovery-view.jsp"),
        @Result(name = RemitRecoveryAction.SUMMARYFORM, location = "remitRecovery-summaryForm.jsp"),
        //@Result(name = "edit", location = "remitRecovery-summaryForm.jsp"),
        @Result(name = "summaryResults", location = "remitRecovery-summaryResults.jsp"),
        @Result(name = "remitDetail", location = "remitRecovery-remitDetail.jsp") })
public class RemitRecoveryAction extends BasePaymentAction {

    private static final String DESIGNATION_ID = "designationId";
    private static final String DESIGNATION_NAME = "designationName";
    private static final String DESIGNATION_LIST = "designationList";
    private static final String UNCHECKED = "unchecked";
    private static final String MESSAGES = "messages";
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(RemitRecoveryAction.class);
    protected List<String> headerFields = new ArrayList<String>();
    protected List<String> mandatoryFields = new ArrayList<String>();
    private RemittanceBean remittanceBean = new RemittanceBean();
    private RemitRecoveryService remitRecoveryService;
    private VoucherService voucherService;
    private List<RemittanceBean> listRemitBean;
    private String message = "";
    private String selectedRows;
    private Long functionId;
    private String backdateentry;
    private String fileno;
    @Autowired
    @Qualifier("remittanceRecoveryService")
    private RecoveryService recoveryService;
    private Map<String, String> modeOfCollectionMap = new HashMap<String, String>();
    private PaymentService paymentService;
    private Paymentheader paymentheader = new Paymentheader();
    private static final String PAYMENTID = "paymentid";
    private static final String VIEW = "view";
    public boolean showApprove = false;
    private CommonBean commonBean;
    private String modeOfPayment;
    private String selectedPartialDeductionRows = "";
    private String typeOfAccount;
    @Autowired
    private FunctionService functionService;
    @Autowired
    private FundHibernateDAO fundHibernateDAO;
    @Autowired
    private FunctionDAO functionDAO;
    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
    @Autowired
    CreateVoucher createVoucher;
    private String departmentId;
    private String wfitemstate;
    private String comments;
    private static final String DD_MMM_YYYY = "dd-MMM-yyyy";
    private static final String FAILED = "Transaction failed";
    private static final String SAVE_EXCEPTION = "Exception while saving data";
    // private String typeOfAccount;
    private Recovery recovery;
    private VoucherHibernateDAO voucherHibDAO;
    private EgovCommon egovCommon;
    private boolean showCancel = false;
    private boolean showButtons = true;
    private BigDecimal balance;
    private String remittedTo = "";
    private final boolean remit = false;
    private List<InstrumentHeader> instrumentHeaderList = new ArrayList<InstrumentHeader>();
    private String cutOffDate;
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Constants.LOCALE);
    DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
    DateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");

    Date date;
    private ScriptService scriptService;
    @Autowired
    private PaymentActionHelper paymentActionHelper;
    @Autowired
    private ChartOfAccounts chartOfAccounts;
    @Autowired
    private EgovMasterDataCaching masterDataCache;

    @Autowired
    @Qualifier("bankService")
    private BankService bankService;

    @Autowired
    @Qualifier("bankAccountService")
    private BankAccountService bankAccountService;
    @Autowired
    private BankHibernateDAO bankHibernateDAO;
    private Boolean isPartialPaymentEnabled = false;
    private boolean isNonControlledCodeTds = false;
    private String defaultPaymentMode = null;
    private String mode = "";
    private Recovery recoveryAssign = new Recovery();
    private Fund fund = new Fund();
    private Department department = new Department();
    private Date asOnDate = new Date();
    private Date fromDate = new Date();
    private Integer detailKey;
    private FinancialYearHibernateDAO financialYearDAO;
    private String type = "";
    private List<TDSEntry> remittedTDS = new ArrayList<TDSEntry>();
    List<Long> remDtlIds;
	List<String> remAssignNumbers;
	private String fundnew="";
    private String departmentnew="";
    private String functionnew="";
	private String firstsignatory="-1";
    private String secondsignatory="-1";
    
    public String getFundnew() {
		return fundnew;
	}

	public void setFundnew(String fundnew) {
		this.fundnew = fundnew;
	}

	public String getDepartmentnew() {
		return departmentnew;
	}

	public void setDepartmentnew(String departmentnew) {
		this.departmentnew = departmentnew;
	}

	public String getFunctionnew() {
		return functionnew;
	}

	public void setFunctionnew(String functionnew) {
		this.functionnew = functionnew;
	}

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(final BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public StateAware getModel() {
        voucherHeader = (CVoucherHeader) super.getModel();
        voucherHeader.setType(FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT);
        return voucherHeader;
    }

    public RemitRecoveryAction() {
        voucherHeader.setVouchermis(new Vouchermis());
        addRelatedEntity("fundId", Fund.class);
        addRelatedEntity("vouchermis.schemeid", Scheme.class);
        addRelatedEntity("vouchermis.subschemeid", SubScheme.class);
        addRelatedEntity("vouchermis.functionary", Functionary.class);
        addRelatedEntity("fundsourceId", Fundsource.class);
        addRelatedEntity("vouchermis.divisionid", Boundary.class);
    }

    @Override
    public void prepare() {
    	System.out.println("1");
        super.prepare();
        System.out.println("2");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Inside Prepare method");
        final List<Recovery> listRecovery = recoveryService.getAllActiveRecoverys();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("RemitRecoveryAction | Tds list size : " + listRecovery.size());
        //modified abhishek on 22032022 for branch id not showing in bank
        //List<Bank> activeBanks = bankHibernateDAO.getAllBankHavingBranchAndAccounts();
        typeOfAccount = FinancialConstants.TYPEOFACCOUNT_PAYMENTS + ","
				+ FinancialConstants.TYPEOFACCOUNT_RECEIPTS_PAYMENTS;
        List<Map<String, Object>> bankBranchList = new ArrayList<Map<String, Object>>();
		int index = 0;
		String[] strArray = null;
		final StringBuffer query = new StringBuffer();
		query.append(
				"select DISTINCT concat(concat(bank.id,'-'),bankBranch.id) as bankbranchid,concat(concat(bank.name,' '),bankBranch.branchname) as bankbranchname ")
				.append("FROM Bank bank,Bankbranch bankBranch,Bankaccount bankaccount where  bank.isactive=true  and bankBranch.isactive=true and ")
				.append(" bankaccount.isactive=true and bank.id = bankBranch.bank.id and bankBranch.id = bankaccount.bankbranch.id ");
		if (voucherHeader.getFundId() != null)
			query.append("and bankaccount.fund.id=? and bankaccount.type in(");
		else
			query.append("and bankaccount.type in(");
		if (typeOfAccount.indexOf(",") != -1) {
			strArray = typeOfAccount.split(",");
			for (final String type : strArray) {
				index++;
				query.append("'").append(type).append("'");
				if (strArray.length > index)
					query.append(",");

			}
		} else
			query.append("'").append(typeOfAccount).append("'");
		query.append(") order by 2 ");
		try {
			List<Object[]> bankBranch = null;
			if (voucherHeader.getFundId() != null)
				bankBranch = getPersistenceService().findAllBy(query.toString(), voucherHeader.getFundId().getId());
			else
				bankBranch = getPersistenceService().findAllBy(query.toString());

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Bank list size is " + bankBranch.size());

			Map<String, Object> bankBrmap;
			for (final Object[] element : bankBranch) {
				bankBrmap = new HashMap<String, Object>();
				bankBrmap.put("bankBranchId", element[0].toString());
				bankBrmap.put("bankBranchName", element[1].toString());
				bankBranchList.add(bankBrmap);
			}

		} catch (final HibernateException e) {
			LOGGER.error("Exception occured while getting the data for bank dropdown " + e.getMessage(),
					new HibernateException(e.getMessage()));

		} catch (final Exception e) {
			LOGGER.error("Exception occured while getting the data for bank dropdown " + e.getMessage(),
					new Exception(e.getMessage()));
		}

		addDropdownData("bankList", bankBranchList);
        //addDropdownData("bankList", activeBanks);
        addDropdownData("branchList", Collections.EMPTY_LIST);
        addDropdownData("recoveryList", listRecovery);
        addDropdownData("accNumList", Collections.EMPTY_LIST);
        modeOfCollectionMap.put(FinancialConstants.MODEOFPAYMENT_CASH, getText("cash.consolidated.cheque"));
        this.setPartialPayment("deduction");
        System.out.println("3");
    }

    @Override
    @Action(value = "/deduction/remitRecovery-newform")
    public String newform() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("RemitRecoveryAction | newform | start");
        reset();
        loadDefalutDates();
        try {
            Map<String, String> fundCodeNameMap = new HashMap<>();
            Map<String, String> deptCodeNameMap = new HashMap<>();
            CFunction function = new CFunction();
            Fund fund=new Fund();
            List<Fund> fundList = fundHibernateDAO.findAllActiveFunds();
            if (fundList != null)
                for (Fund f : fundList) {
                    fundCodeNameMap.put(f.getCode(), f.getName());
                }

            
        List<AppConfigValues> appConfigValuesList =appConfigValuesService.getConfigValuesByModuleAndKey("EGF","fund");
        for(AppConfigValues value:appConfigValuesList)
        {
        	   //voucherTypeBean.setFundnew(value.getValue());
           	fund = fundHibernateDAO.fundByCode(value.getValue());
           	setFundnew(String.valueOf(fund.getId()));
        }
        appConfigValuesList=null;
        appConfigValuesList =appConfigValuesService.getConfigValuesByModuleAndKey("EGF","department");
        for(AppConfigValues value:appConfigValuesList)
        {
        	setDepartmentnew(value.getValue());
        }
        appConfigValuesList=null;
        appConfigValuesList =appConfigValuesService.getConfigValuesByModuleAndKey("EGF","function");
        for(AppConfigValues value:appConfigValuesList)
        {
        	   function= functionDAO.getFunctionByCode(value.getValue());
        	   setFunctionnew(function.getId().toString());
           }
            }catch(Exception e) {
            	e.printStackTrace();
        }
        return NEW;
    }

    private void reset() {
        commonBean.reset();
        voucherHeader.reset();
        remittanceBean = new RemittanceBean();
        if (remit)
            listRemitBean = new ArrayList<RemittanceBean>();
    }

    @ValidationErrorPage(value = NEW)
    @Action(value = "/deduction/remitRecovery-search")
    public String search() {
        listRemitBean = new ArrayList<RemittanceBean>();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("RemitRecoveryAction | Search | Start");
        if(remitRecoveryService.isNonControlledCodeTds(remittanceBean)){
            isNonControlledCodeTds = true;
            listRemitBean = remitRecoveryService.getRecoveryDetailsForNonControlledCode(remittanceBean, voucherHeader);
        }else{
            listRemitBean = remitRecoveryService.getRecoveryDetails(remittanceBean, voucherHeader);
        }
        if (listRemitBean == null || listRemitBean.isEmpty())
            listRemitBean = new ArrayList<RemittanceBean>();
        else {
            departmentId = listRemitBean.get(0).getDepartmentId();
            functionId = listRemitBean.get(0).getFunctionId();
        }
        return NEW;
    }

    public void prepareRemit() {
        addDropdownData("userList", Collections.EMPTY_LIST);
        loadDefalutDates();
    }

    @ValidationErrorPage(value = "new")
    @Action(value = "/deduction/remitRecovery-remit")
    public String remit() {
        try{
            prepareListRemitBean(selectedRows);
            final List<AppConfigValues> cutOffDateconfigValue = appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
                    "DataEntryCutOffDate");
            if (cutOffDateconfigValue != null && !cutOffDateconfigValue.isEmpty())
                try {
                    date = df.parse(cutOffDateconfigValue.get(0).getValue());
                    setCutOffDate(formatter.format(date));
                } catch (final ParseException e) {

                }
            
            
            voucherHeader.setType(FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT);
            if (voucherHeader.getVouchermis().getDepartmentcode() == null) {
                voucherHeader.getVouchermis().setDepartmentcode(departmentId.toString());
            }
            if (voucherHeader.getVouchermis().getFunction() == null && functionId!=null) {
                final CFunction function = functionService.findOne(functionId);
                voucherHeader.getVouchermis().setFunction(function);
            }
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("RemitRecoveryAction | remit | start");
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("RemitRecoveryAction | remit | size before filter" + listRemitBean.size());
            final Recovery recov = (Recovery) persistenceService.find("from Recovery where id=?",
                    remittanceBean.getRecoveryId());
            if (recov != null)
                remittedTo = recov.getRemitted();
            for (final RemittanceBean rbean : listRemitBean)
                rbean.setPartialAmount(rbean.getAmount());
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("RemitRecoveryAction | remit | size after filter" + listRemitBean.size());
            setModeOfPayment(modeOfPayment);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("RemitRecoveryAction | remit | end");
            if (getBankBalanceCheck() == null || "".equals(getBankBalanceCheck()))
                addActionMessage(getText("payment.bankbalance.controltype"));
        }catch (final ValidationException e) {
            search();
            LOGGER.error(e.getMessage(), e);
            final List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.add(new ValidationError("exp", e.getErrors().get(0).getMessage()));
            throw new ValidationException(errors);
        } catch (final Exception e) {
            search();
            LOGGER.error(e.getMessage());
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
        }

        
        return "remitDetail";
    }

    private void prepareListRemitBean(final String selectedRows) {
        setDefaultPaymentMode(modeOfPayment);
        if(remitRecoveryService.isNonControlledCodeTds(remittanceBean)){
            isNonControlledCodeTds = true;
            listRemitBean = remitRecoveryService.getRecoveryDetailsForNonControlledCode(selectedRows);
        }else{
            listRemitBean = remitRecoveryService.getRecoveryDetails(selectedRows);
        }
        if (listRemitBean == null)
            listRemitBean = new ArrayList<RemittanceBean>();

    }

    @ValidationErrorPage(value = "remitDetail")
    @Action(value = "/deduction/remitRecovery-create")
    public String create() {
        try {
        	
        	String fSignatory =firstsignatory;
        	String sSignatory = secondsignatory;
        	String bentry=backdateentry;
        	String filen=fileno;
        	System.out.println("::::backdate:: "+bentry+"::::fileno:: "+filen);
        	System.out.println("In create Function....>>"+fSignatory+"..."+sSignatory);
        	
            final String vdate = parameters.get("voucherDate")[0];
            final Date date1 = sdf1.parse(vdate);
            final String voucherDate = formatter1.format(date1);
            String cutOffDate1 = null;
            prepareListRemitBean(selectedRows);
            if(listRemitBean != null && !listRemitBean.isEmpty())
            {
            	for(RemittanceBean row : listRemitBean)
            	{
            		System.out.println("voucher name :: "+row.getVoucherName());
            		System.out.println("voucher number :: "+row.getVoucherNumber());
            	}
            }
            if(isPartialPaymentEnabled){
                setPartialAmountForSelectedDeduction();
            }
            System.out.println("Part 1");
            validateFields();
            System.out.println("Part 2");
            voucherHeader.setType(FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT);
            voucherHeader.setName(FinancialConstants.PAYMENTVOUCHER_NAME_REMITTANCE);
            voucherHeader.setBackdateentry(bentry);
            voucherHeader.setFileno(filen);
            //added by prasanta for save signotory
            voucherHeader.setFirstsignatory(fSignatory);
            voucherHeader.setSecondsignatory(sSignatory);
            
            final HashMap<String, Object> headerDetails = createHeaderAndMisDetails();
            recovery = (Recovery) persistenceService.find("from Recovery where id=?", remittanceBean.getRecoveryId());
            populateWorkflowBean();
            System.out.println("Part 3");
            paymentheader = paymentActionHelper.createRemittancePayment(paymentheader, voucherHeader,
                    Integer.valueOf(commonBean.getAccountNumberId()), getModeOfPayment(),
                    remittanceBean.getTotalAmount(), listRemitBean, recovery, remittanceBean, remittedTo, workflowBean,
                    headerDetails, commonBean);

            if (!cutOffDate.isEmpty() && cutOffDate != null)
                try {
                    date = sdf1.parse(cutOffDate);
                    cutOffDate1 = formatter1.format(date);
                } catch (final ParseException e) {
                    //
                }
            if (cutOffDate1 != null && voucherDate.compareTo(cutOffDate1) <= 0
                    && FinancialConstants.CREATEANDAPPROVE.equalsIgnoreCase(workflowBean.getWorkFlowAction()))
                addActionMessage(getText("remittancepayment.transaction.success")
                        + paymentheader.getVoucherheader().getVoucherNumber());
            else {
                addActionMessage(getText("remittancepayment.transaction.success")
                        + paymentheader.getVoucherheader().getVoucherNumber());

                addActionMessage(getText("payment.voucher.approved", new String[] {this.getEmployeeName(paymentheader.getState()
                        .getOwnerPosition()) }));
            }

        } catch (final ValidationException e) {
            loadAjaxedDropDowns();
            LOGGER.error(e.getMessage(), e);
            final List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.add(new ValidationError("exp", e.getErrors().get(0).getMessage()));
            throw new ValidationException(errors);
        } catch (final Exception e) {
            loadAjaxedDropDowns();
            LOGGER.error(e.getMessage());
            throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
        }
        return MESSAGES;
    }

    private void setPartialAmountForSelectedDeduction() {
        if(!selectedPartialDeductionRows.isEmpty()){
            String[] selectedList = selectedPartialDeductionRows.trim().split(",");
            Map<Integer, BigDecimal> recovIdPartialAmtMap = new HashMap<>();
            for(String idAmnt : selectedList){
                String[] split = idAmnt.split("~");
                recovIdPartialAmtMap.put(Integer.parseInt(split[0]), new BigDecimal(split[1]));
            }
            List<RemittanceBean> tempListBean = new ArrayList<>();
            for(RemittanceBean rb : listRemitBean){
                rb.setPartialAmount(recovIdPartialAmtMap.get(!isNonControlledCodeTds ? rb.getRemittance_gl_dtlId() : rb.getRemittance_gl_Id()));
                tempListBean.add(rb);
            }
            listRemitBean.clear();
            listRemitBean.addAll(tempListBean);
        }
    }

    /**
     *
     *
     * 1.Creates one EgRemittance with paymentvoucher is set 2.updates every EgRemittanceGldtl for selected Bills 3.Creates
     * EgRemittanceDetail per selected Bill
     *
     */
    private List<HashMap<String, Object>> addSubledgerGroupBy(final List<HashMap<String, Object>> subledgerDetails,
            final String glcode) {
        final Map<Integer, List<Integer>> detailTypesMap = new HashMap<Integer, List<Integer>>();
        Integer detailTypeId = null;
        final List<Integer> detailTypeList = new ArrayList<Integer>();
        HashMap<String, Object> subledgertDetailMap = null;
        for (final RemittanceBean rbean : listRemitBean) {
            detailTypeId = rbean.getDetailTypeId();
            if (detailTypeList.contains(detailTypeId)) {
                if (detailTypesMap.get(detailTypeId).contains(rbean.getDetailKeyid()))
                    continue;
                else
                    detailTypesMap.get(detailTypeId).add(rbean.getDetailKeyid());

            } else {
                detailTypeList.add(detailTypeId);
                detailTypesMap.put(detailTypeId, new ArrayList<Integer>());
                detailTypesMap.get(detailTypeId).add(rbean.getDetailKeyid());

            }

        }
        final Set<Entry<Integer, List<Integer>>> entrySet = detailTypesMap.entrySet();
        final List<RemittanceBean> tempRemitBean = listRemitBean;
        for (final Entry<Integer, List<Integer>> o : entrySet) {
            final List<Integer> value = o.getValue();
            for (final Integer detailKey : value) {
                BigDecimal sumPerDetailKey = BigDecimal.ZERO;
                // int lastIndexOf = tempRemitBean.lastIndexOf(detailKey);
                for (final RemittanceBean remittanceBean2 : tempRemitBean)
                    if (remittanceBean2.getDetailKeyid() != null && remittanceBean2.getDetailKeyid().equals(detailKey))
                        sumPerDetailKey = sumPerDetailKey.add(remittanceBean2.getPartialAmount());
                subledgertDetailMap = new HashMap<String, Object>();
                subledgertDetailMap.put(VoucherConstant.DEBITAMOUNT, sumPerDetailKey);
                subledgertDetailMap.put(VoucherConstant.CREDITAMOUNT, BigDecimal.ZERO);
                subledgertDetailMap.put(VoucherConstant.DETAILTYPEID, o.getKey().toString());
                subledgertDetailMap.put(VoucherConstant.DETAILKEYID, detailKey);
                subledgertDetailMap.put(VoucherConstant.GLCODE, glcode);
                subledgerDetails.add(subledgertDetailMap);
            }

        }
        return subledgerDetails;
    }

    public Paymentheader getPayment() {
        String paymentid = null;
        if (parameters.get(PAYMENTID) == null) {
            final Object obj = getSession().get(PAYMENTID);
            if (obj != null)
                paymentid = (String) obj;
        } else
            paymentid = parameters.get(PAYMENTID)[0];
        if (paymentheader == null && paymentid != null)
            paymentheader = paymentService.find("from Paymentheader where id=?", Long.valueOf(paymentid));
        if (paymentheader == null)
            paymentheader = new Paymentheader();

        return paymentheader;
    }

    @ValidationErrorPage(value = VIEW)
    @SkipValidation
    @Action(value = "/deduction/remitRecovery-sendForApproval")
    public String sendForApproval() {
        paymentheader = paymentService.find("from Paymentheader where id=?", Long.valueOf(paymentid));
        populateWorkflowBean();
        paymentheader = paymentActionHelper.sendForApproval(paymentheader, workflowBean);

        if (FinancialConstants.BUTTONREJECT.equalsIgnoreCase(workflowBean.getWorkFlowAction()))
            addActionMessage(getText("payment.voucher.rejected", new String[] {this.getEmployeeName(paymentheader.getState()
                    .getOwnerPosition())}));
        if (FinancialConstants.BUTTONFORWARD.equalsIgnoreCase(workflowBean.getWorkFlowAction()))
            addActionMessage(getText("payment.voucher.approved", new String[] {this.getEmployeeName(paymentheader.getState()
            		.getOwnerPosition()) }));
        if (FinancialConstants.BUTTONCANCEL.equalsIgnoreCase(workflowBean.getWorkFlowAction()))
            addActionMessage(getText("payment.voucher.cancelled"));
        else if (FinancialConstants.BUTTONAPPROVE.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
                addActionMessage(getText("payment.voucher.final.approval"));
            setAction(workflowBean.getWorkFlowAction());

        }

        return MESSAGES;
    }

    @ValidationErrorPage(value = VIEW)
    @SkipValidation
    @Action(value = "/deduction/remitRecovery-viewInboxItem")
    public String viewInboxItem() {
        paymentheader = paymentService.find("from Paymentheader where id=?", Long.valueOf(paymentid));
        /*
         * if (paymentheader.getState().getValue() != null && !paymentheader.getState().getValue().isEmpty() &&
         * paymentheader.getState().getValue().contains("Reject")) {
         * voucherHeader.setId(paymentheader.getVoucherheader().getId()); showCancel = true; return beforeEdit(); }
         */
        showApprove = true;
        voucherHeader.setId(paymentheader.getVoucherheader().getId());
        if(paymentheader.getVoucherheader().getVoucherDate()!=null) {
            Date voucherDates=paymentheader.getVoucherheader().getVoucherDate();
           if( chartOfAccounts.isClosedForPosting(df.format(voucherDates))){
               finanicalYearAndClosedPeriodCheckIsClosed=true;
           }
        }
        prepareForViewModifyReverse();
        // loadApproverUser(voucherHeader.getType());
        return VIEW;
    }

    /**
     * @return
     */
    /*public String beforeEdit() {
        showApprove = true;
        // voucherHeader.setId(paymentheader.getVoucherheader().getId());
        prepareForViewModifyReverse();
        // loadApproverUser(voucherHeader.getType());
        return EDIT;
    }*/

    //@ValidationErrorPage(value = EDIT)
    @Action(value = "/deduction/remitRecovery-edit")
    public String edit() {
        try {
            validateFields();
            voucherHeader = voucherService.updateVoucherHeader(voucherHeader);
            reCreateLedger();
            updateRemittanceDetail();
            paymentheader = (Paymentheader) persistenceService.find("from Paymentheader where voucherheader=?",
                    voucherHeader);
            paymentService.updatePaymentHeader(paymentheader, voucherHeader,
                    Integer.valueOf(commonBean.getAccountNumberId()), getModeOfPayment(),
                    remittanceBean.getTotalAmount());
            final Miscbilldetail miscbillDetail = (Miscbilldetail) persistenceService
                    .find(" from Miscbilldetail where payVoucherHeader=?", voucherHeader);
            miscbillDetail.setPaidto(remittedTo);
            // persistenceService.setType(Miscbilldetail.class);
            persistenceService.persist(miscbillDetail);
            // updateMiscBillDetail();

            sendForApproval();
            addActionMessage(getText("remittancepayment.transaction.success") + voucherHeader.getVoucherNumber());
        } catch (final ValidationException e) {
            // loadApproverUser(voucherHeader.getType());
            loadAjaxedDropDowns();
            throw e;
        }
        return MESSAGES;
    }

    /**
     *
     */
    private void updateRemittanceDetail() {
        for (final RemittanceBean rbean : listRemitBean) {
            final EgRemittanceDetail egrDetail = (EgRemittanceDetail) persistenceService
                    .find("from EgRemittanceDetail where id=?", rbean.getRemittanceId());
            egrDetail.setRemittedamt(rbean.getPartialAmount());
            // persistenceService.setType(EgRemittanceDetail.class);
            persistenceService.persist(egrDetail);
        }

    }

    /**
     *
     *
     */
    private void reCreateLedger() {

        try {
            createVoucher.deleteVoucherdetailAndGL(voucherHeader);
            persistenceService.getSession().flush();
            HashMap<String, Object> detailMap = null;
            final List<HashMap<String, Object>> accountdetails = new ArrayList<HashMap<String, Object>>();
            List<HashMap<String, Object>> subledgerDetails = new ArrayList<HashMap<String, Object>>();

            detailMap = new HashMap<String, Object>();
            detailMap.put(VoucherConstant.CREDITAMOUNT, remittanceBean.getTotalAmount().toString());
            detailMap.put(VoucherConstant.DEBITAMOUNT, ZERO);
            final Bankaccount account = (Bankaccount) persistenceService.find("from Bankaccount where id=?",
                    Integer.valueOf(commonBean.getAccountNumberId()));
            detailMap.put(VoucherConstant.GLCODE, account.getChartofaccounts().getGlcode());
            accountdetails.add(detailMap);
            detailMap = new HashMap<String, Object>();
            detailMap.put(VoucherConstant.CREDITAMOUNT, ZERO);
            detailMap.put(VoucherConstant.DEBITAMOUNT, remittanceBean.getTotalAmount().toString());
            recovery = (Recovery) persistenceService.find("from Recovery where id=?", remittanceBean.getRecoveryId());
            detailMap.put(VoucherConstant.GLCODE, recovery.getChartofaccounts().getGlcode());
            accountdetails.add(detailMap);
            subledgerDetails = addSubledgerGroupBy(subledgerDetails, recovery.getChartofaccounts().getGlcode());

            final List<Transaxtion> transactions = createVoucher.createTransaction(null, accountdetails,
                    subledgerDetails, voucherHeader);
            persistenceService.getSession().flush();

            Transaxtion txnList[] = new Transaxtion[transactions.size()];
            txnList = transactions.toArray(txnList);
            final SimpleDateFormat formatter = new SimpleDateFormat(DD_MMM_YYYY);
            if (!chartOfAccounts.postTransaxtions(txnList, formatter.format(voucherHeader.getVoucherDate())))
                throw new ValidationException(
                        Arrays.asList(new ValidationError("Exception While Saving Data", "Transaction Failed")));

        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ValidationException(Arrays.asList(new ValidationError(SAVE_EXCEPTION, FAILED)));
        }

    }

    @Action(value = "/deduction/remitRecovery-beforeView")
    public String beforeView() {
        prepareForViewModifyReverse();
        wfitemstate = "END"; // requird to hide the approver drop down when view
                             // is form source
        showApprove = true;
        showButtons = false;
        // loadApproverUser(voucherHeader.getType());
        return VIEW;
    }

    @SuppressWarnings(UNCHECKED)
    private void prepareForViewModifyReverse() {
        final StringBuffer instrumentQuery = new StringBuffer(100);
        instrumentQuery
                .append("select  distinct ih from InstrumentHeader ih join ih.instrumentVouchers iv where iv.voucherHeaderId.id=?")
                .append(" order by ih.id");
        remittanceBean = new RemittanceBean();
        voucherHeader = (CVoucherHeader) persistenceService.find("from CVoucherHeader where id=?",
                voucherHeader.getId());
        paymentheader = (Paymentheader) persistenceService.find("from Paymentheader where voucherheader=?",
                voucherHeader);
        final Miscbilldetail miscBill = (Miscbilldetail) persistenceService
                .find("from Miscbilldetail where payVoucherHeader=?", voucherHeader);
        remittedTo = miscBill.getPaidto();
        commonBean.setAmount(paymentheader.getPaymentAmount());
        commonBean.setAccountNumberId(paymentheader.getBankaccount().getId().toString());
        commonBean.setAccnumnar(paymentheader.getBankaccount().getNarration());

        final String bankBranchId = paymentheader.getBankaccount().getBankbranch().getBank().getId() + "-"
                + paymentheader.getBankaccount().getBankbranch().getId();

        commonBean.setBankId(bankBranchId);
        setModeOfPayment(paymentheader.getType());
        remittanceBean.setTotalAmount(paymentheader.getPaymentAmount());
        setComments(getText("payment.comments", new String[] { paymentheader.getPaymentAmount().toPlainString() }));
        getRemittanceFromVoucher();

        loadAjaxedDropDowns();
        // find it last so that rest of the data loaded
        if (!"view".equalsIgnoreCase(showMode)) {
            // validateUser("balancecheck");
        } else {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("fetching cheque detail ------------------------");
            instrumentHeaderList = getPersistenceService().findAllBy(instrumentQuery.toString(),
                    paymentheader.getVoucherheader().getId());
        }
    }

    private void getRemittanceFromVoucher() {
        listRemitBean = new ArrayList<RemittanceBean>();

        final List<EgRemittance> remitList = persistenceService.findAllBy("from EgRemittance where voucherheader=?",
                voucherHeader);
        for (final EgRemittance remit : remitList) {
            RemittanceBean rbean = null;
            final Set<EgRemittanceDetail> egRemittanceDetail = remit.getEgRemittanceDetail();
            for (final EgRemittanceDetail remitDtl : egRemittanceDetail) {
                rbean = new RemittanceBean();
                rbean.setPartialAmount(remitDtl.getRemittedamt());
                rbean.setAmount(remitDtl.getRemittedamt());
                if (remitDtl.getEgRemittanceGldtl() != null) {
                    rbean.setDetailTypeId(
                            remitDtl.getEgRemittanceGldtl().getGeneralledgerdetail().getDetailTypeId().getId());
                    rbean.setDetailKeyid(
                            remitDtl.getEgRemittanceGldtl().getGeneralledgerdetail().getDetailKeyId().intValue());
                    rbean.setRemittance_gl_dtlId(remitDtl.getEgRemittanceGldtl().getId());
                    rbean.setDeductionAmount(remitDtl.getEgRemittanceGldtl().getGldtlamt());
                } else
                    rbean.setDeductionAmount(remitDtl.getRemittedamt());
                rbean.setRemittanceId(remitDtl.getId());
                BigDecimal calculatedEarlierPayment = BigDecimal.ZERO;
                if (remitDtl.getEgRemittanceGldtl() != null)
                    calculatedEarlierPayment = calculateEarlierPayment(remitDtl);
                if (calculatedEarlierPayment.compareTo(BigDecimal.ZERO) == 0)
                    rbean.setEarlierPayment(BigDecimal.ZERO);
                else if (remit.getVoucherheader().getStatus().intValue() == FinancialConstants.CANCELLEDVOUCHERSTATUS
                        .intValue()
                        || remit.getVoucherheader().getStatus().intValue() == FinancialConstants.REVERSALVOUCHERSTATUS
                                .intValue())
                    rbean.setEarlierPayment(calculatedEarlierPayment);
                else
                    rbean.setEarlierPayment(calculatedEarlierPayment.subtract(remitDtl.getRemittedamt()));
                final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                if (remitDtl.getEgRemittanceGldtl() != null) {
                    final EntityType entity = voucherHibDAO.getEntityInfo(
                            remitDtl.getEgRemittanceGldtl().getGeneralledgerdetail().getDetailKeyId().intValue(),
                            remitDtl.getEgRemittanceGldtl().getGeneralledgerdetail().getDetailTypeId().getId());
                    rbean.setPartyCode(entity.getCode());
                    rbean.setPartyName(entity.getName());
                    rbean.setPanNo(entity.getPanno());
                    rbean.setVoucherDate(sdf.format(remitDtl.getEgRemittanceGldtl().getGeneralledgerdetail()
                            .getGeneralLedgerId().getVoucherHeaderId().getVoucherDate()));
                    rbean.setVoucherNumber(remitDtl.getEgRemittanceGldtl().getGeneralledgerdetail().getGeneralLedgerId()
                            .getVoucherHeaderId().getVoucherNumber());
                    rbean.setVoucherName(remitDtl.getEgRemittanceGldtl().getGeneralledgerdetail().getGeneralLedgerId()
                            .getVoucherHeaderId().getName());
                } else if (remitDtl.getGeneralLedger().getVoucherHeaderId() != null) {
                    isNonControlledCodeTds = true;
                    rbean.setVoucherDate(sdf.format(remitDtl.getGeneralLedger().getVoucherHeaderId().getVoucherDate()));
                    rbean.setVoucherNumber(remitDtl.getGeneralLedger().getVoucherHeaderId().getVoucherNumber());
                    rbean.setVoucherName(remitDtl.getGeneralLedger().getVoucherHeaderId().getName());
                }
                listRemitBean.add(rbean);

            }

            if (remittanceBean.getRecoveryId() == null)
                remittanceBean.setRecoveryId(remit.getRecovery().getId());

        }
    }

    /**
     * @param remitDtl
     * @return
     */
    private BigDecimal calculateEarlierPayment(final EgRemittanceDetail remitDtl) {
        final BigDecimal sum = (BigDecimal) persistenceService.find(
                "select sum(egr.remittedamt) from EgRemittanceDetail egr where "
                        + " egr.egRemittanceGldtl=?  and egr.egRemittance.voucherheader.status  NOT in (?,?)",
                remitDtl.getEgRemittanceGldtl(), FinancialConstants.CANCELLEDVOUCHERSTATUS,
                FinancialConstants.REVERSEDVOUCHERSTATUS);
        if (sum == null)
            return BigDecimal.ZERO;
        else
            return sum;
    }

    @SuppressWarnings(UNCHECKED)
    @SkipValidation
    public boolean validateUser(final String purpose) {
        final Script validScript = (Script) getPersistenceService()
                .findAllByNamedQuery(Script.BY_NAME, "Paymentheader.show.bankbalance").get(0);
        final List<String> list = (List<String>) scriptService.executeScript(validScript,
                ScriptService.createContext("persistenceService", paymentService, "purpose", purpose));
        if (list.get(0).equals("true"))
            try {
                canCheckBalance = true;
                commonBean.setAvailableBalance(egovCommon.getAccountBalance(new Date(),
                        paymentheader.getBankaccount().getId(), paymentheader.getPaymentAmount(), paymentheader.getId(),
                        paymentheader.getBankaccount().getChartofaccounts().getId()));
                balance = commonBean.getAvailableBalance();
                return true;
            } catch (final Exception e) {
                balance = BigDecimal.valueOf(-1);

                return true;
            }
        else
            return false;
    }

    private void loadAjaxedDropDowns() {
        loadSchemeSubscheme();
        //loadBankBranchForFundAndType();
        loadBankAccountNumberForFundAndType();
    }

    private void loadBankBranchForFundAndType() {
        addDropdownData("bankList",
                bankService.getBankByFundAndType(voucherHeader.getFundId().getId(), "RECEIPTS_PAYMENTS,PAYMENTS"));
    }

    private void loadBankAccountNumberForFundAndType() {
        Bankaccount bankaccount = null;

        if (paymentheader != null && paymentheader.getBankaccount() != null)
            bankaccount = paymentheader.getBankaccount();
        else if (commonBean.getAccountNumberId() != null && !commonBean.getAccountNumberId().equals("-1")
                && !commonBean.getAccountNumberId().equals(""))
            bankaccount = (Bankaccount) persistenceService.find("from Bankaccount where id=?",
                    Long.valueOf(commonBean.getAccountNumberId()));
        if (bankaccount.getBankbranch().getId() != null)
            addDropdownData("accNumList",
                    bankAccountService.getBankAccounts(voucherHeader.getFundId().getId(),
                            bankaccount.getBankbranch().getId(), bankaccount.getBankbranch().getBank().getId(),
                            "RECEIPTS_PAYMENTS,PAYMENTS"));
        else
            addDropdownData("accNumList", Collections.EMPTY_LIST);
    }

    /*
     * not used private void updateMiscBillDetail() { Miscbilldetail miscbillDetail = (Miscbilldetail) persistenceService.find(
     * " from Miscbilldetail where payVoucherHeader=?", voucherHeader);
     * miscbillDetail.setBillamount(remittanceBean.getTotalAmount());
     * miscbillDetail.setPaidamount(remittanceBean.getTotalAmount());
     * miscbillDetail.setPassedamount(remittanceBean.getTotalAmount()); miscbillDetail.setPayVoucherHeader(voucherHeader);
     * //miscbillDetail.setBillVoucherHeader(vhid); miscbillDetail.setPaidto(recovery.getRemitted());
     * //persistenceService.setType(Miscbilldetail.class); persistenceService.persist(miscbillDetail); }
     */

    @Override
    protected void loadDefalutDates() {
        final Date currDate = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            voucherHeader.setVoucherDate(sdf.parse(sdf.format(currDate)));
        } catch (final ParseException e) {
            throw new ValidationException(Arrays
                    .asList(new ValidationError("Exception while formatting voucher date", "Transaction failed")));
        }
    }

    @Override
    public List<String> getValidActions() {
        List<String> validActions = Collections.emptyList();
        final List<AppConfigValues> cutOffDateconfigValue = appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
                "DataEntryCutOffDate");
        if (cutOffDateconfigValue != null && !cutOffDateconfigValue.isEmpty()) {
            if (null == paymentheader || null == paymentheader.getId()
                    || paymentheader.getCurrentState().getValue().endsWith("NEW"))
                validActions = Arrays.asList(FinancialConstants.BUTTONFORWARD);
            else if (paymentheader.getCurrentState() != null)
                validActions = customizedWorkFlowService.getNextValidActions(paymentheader.getStateType(),
                        getWorkFlowDepartment(), getAmountRule(), getAdditionalRule(),
                        paymentheader.getCurrentState().getValue(), getPendingActions(),
                        paymentheader.getCreatedDate());
        } else if (null == paymentheader || null == paymentheader.getId()
                || paymentheader.getCurrentState().getValue().endsWith("NEW"))
            validActions = Arrays.asList(FinancialConstants.BUTTONFORWARD);
        else if (paymentheader.getCurrentState() != null)
            validActions = customizedWorkFlowService.getNextValidActions(paymentheader.getStateType(),
                    getWorkFlowDepartment(), getAmountRule(), getAdditionalRule(),
                    paymentheader.getCurrentState().getValue(), getPendingActions(),
                    paymentheader.getCreatedDate());
        return validActions;
    }

    @Override
    public String getNextAction() {
        WorkFlowMatrix wfMatrix = null;
        if (paymentheader.getId() != null)
            if (paymentheader.getCurrentState() != null)
                wfMatrix = customizedWorkFlowService.getWfMatrix(paymentheader.getStateType(),
                        getWorkFlowDepartment(), getAmountRule(), getAdditionalRule(),
                        paymentheader.getCurrentState().getValue(), getPendingActions(),
                        paymentheader.getCreatedDate());
            else
                wfMatrix = customizedWorkFlowService.getWfMatrix(paymentheader.getStateType(),
                        getWorkFlowDepartment(), getAmountRule(), getAdditionalRule(),
                        State.DEFAULT_STATE_VALUE_CREATED, getPendingActions(), paymentheader.getCreatedDate());
        return wfMatrix == null ? "" : wfMatrix.getNextAction();
    }
    
    public void setPartialPayment(String type){
        isPartialPaymentEnabled  = isPartialPaymentEnabled(type);
    }

    @Override
    public CVoucherHeader getVoucherHeader() {
        return voucherHeader;
    }

    @Override
    public void setVoucherHeader(final CVoucherHeader voucherHeader) {
        this.voucherHeader = voucherHeader;
    }

    public RemittanceBean getRemittanceBean() {
        return remittanceBean;
    }

    public void setRemittanceBean(final RemittanceBean remittanceBean) {
        this.remittanceBean = remittanceBean;
    }

    public void setRemitRecoveryService(final RemitRecoveryService remitRecoveryService) {
        this.remitRecoveryService = remitRecoveryService;
    }

    public List<RemittanceBean> getListRemitBean() {
        return listRemitBean;
    }

    public void setListRemitBean(final List<RemittanceBean> listRemitBean) {
        this.listRemitBean = listRemitBean;
    }

    public Paymentheader getPaymentheader() {
        return paymentheader;
    }

    public void setPaymentheader(final Paymentheader paymentheader) {
        this.paymentheader = paymentheader;
    }

    public void setPaymentService(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void setPaymentWorkflowService(final SimpleWorkflowService<Paymentheader> paymentWorkflowService) {
    }

    public void setVoucherService(final VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    public void setCommonBean(final CommonBean commonBean) {
        this.commonBean = commonBean;
    }

    public CommonBean getCommonBean() {
        return commonBean;
    }

    public void setFinancialYearDAO(final FinancialYearHibernateDAO financialYearDAO) {
    }

    public Map<String, String> getModeOfCollectionMap() {
        return modeOfCollectionMap;
    }

    public void setModeOfCollectionMap(final Map<String, String> modeOfCollectionMap) {
        this.modeOfCollectionMap = modeOfCollectionMap;
    }

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(final String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public void setVoucherHibDAO(final VoucherHibernateDAO voucherHibDAO) {
        this.voucherHibDAO = voucherHibDAO;
    }

    public void setEgovCommon(final EgovCommon egovCommon) {
        this.egovCommon = egovCommon;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(final String comments) {
        this.comments = comments;
    }

    public String getWfitemstate() {
        return wfitemstate;
    }

    public void setWfitemstate(final String wfitemstate) {
        this.wfitemstate = wfitemstate;
    }

    public boolean isShowButtons() {
        return showButtons;
    }

    public void setShowButtons(final boolean showButtons) {
        this.showButtons = showButtons;
    }

    public boolean isShowCancel() {
        return showCancel;
    }

    public void setShowCancel(final boolean showCancel) {
        this.showCancel = showCancel;
    }

    public void setRemittedTo(final String remittedTo) {
        this.remittedTo = remittedTo;
    }

    public String getRemittedTo() {
        return remittedTo;
    }

    public List<InstrumentHeader> getInstrumentHeaderList() {
        return instrumentHeaderList;
    }

    public void setInstrumentHeaderList(final List<InstrumentHeader> instrumentHeaderList) {
        this.instrumentHeaderList = instrumentHeaderList;
    }

    public ScriptService getScriptService() {
        return scriptService;
    }

    public void setScriptService(final ScriptService scriptService) {
        this.scriptService = scriptService;
    }

    public ChartOfAccounts getChartOfAccounts() {
        return chartOfAccounts;
    }

    public void setChartOfAccounts(final ChartOfAccounts chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    public WorkflowBean getWorkflowBean() {
        return workflowBean;
    }

    public void setWorkflowBean(final WorkflowBean workflowBean) {
        this.workflowBean = workflowBean;
    }

    @Override
    public String getCurrentState() {
        return paymentheader.getState().getValue();
    }

    public String getCutOffDate() {
        return cutOffDate;
    }

    public void setCutOffDate(final String cutOffDate) {
        this.cutOffDate = cutOffDate;
    }

    public String getSelectedRows() {
        return selectedRows;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setSelectedRows(final String selectedRows) {
        this.selectedRows = selectedRows;
    }

    public void setFunctionId(final Long functionId) {
        this.functionId = functionId;
    }

    public void setDepartmentId(final String departmentId) {
        this.departmentId = departmentId;
    }

    public Boolean getIsPartialPaymentEnabled() {
        return isPartialPaymentEnabled;
    }

    public void setIsPartialPaymentEnabled(Boolean isPartialPaymentEnabled) {
        this.isPartialPaymentEnabled = isPartialPaymentEnabled;
    }

    public String getSelectedPartialDeductionRows() {
        return selectedPartialDeductionRows;
    }

    public void setSelectedPartialDeductionRows(String selectedPartialDeductionRows) {
        this.selectedPartialDeductionRows = selectedPartialDeductionRows;
    }

    public boolean getIsNonControlledCodeTds() {
        return isNonControlledCodeTds;
    }

    public void setNonControlledCodeTds(boolean isNonControlledCodeTds) {
        this.isNonControlledCodeTds = isNonControlledCodeTds;
    }

    public String getDefaultPaymentMode() {
        return defaultPaymentMode;
    }

    public void setDefaultPaymentMode(String defaultPaymentMode) {
        this.defaultPaymentMode = defaultPaymentMode;
    }
    public String getEmployeeName(Long empId){
        
        return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
     }
    
    
    @Action(value = "/deduction/remitRecovery-assignNumber")
    public String assignNumber()  {
    	System.out.println("YYYY");
    	prepareAssign();
    	System.out.println("XXXX");
        return SUMMARYFORM;
    }
    
    
    public void prepareAssign()
    {
    	System.out.println("5");
    	persistenceService.getSession().setDefaultReadOnly(true);
        persistenceService.getSession().setFlushMode(FlushMode.MANUAL);
        super.prepare();
//        addDropdownData("departmentList", persistenceService.findAllBy("from Department order by name"));
        addDropdownData("departmentList",this.masterDataCache.get("egi-department"));
        addDropdownData("fundList", persistenceService.findAllBy(" from Fund where isactive=true and isnotleaf=false order by name"));  

        addDropdownData("recoveryList",
                persistenceService.findAllBy(" from Recovery where isactive=true order by chartofaccounts.glcode"));
    }

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Recovery getRecoveryAssign() {
		return recoveryAssign;
	}

	public void setRecoveryAssign(Recovery recoveryAssign) {
		this.recoveryAssign = recoveryAssign;
	}

	public Fund getFund() {
		return fund;
	}

	public void setFund(Fund fund) {
		this.fund = fund;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Date getAsOnDate() {
		return asOnDate;
	}

	public void setAsOnDate(Date asOnDate) {
		this.asOnDate = asOnDate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Integer getDetailKey() {
		return detailKey;
	}

	public void setDetailKey(Integer detailKey) {
		this.detailKey = detailKey;
	}
	
	@Action(value = "/deduction/remitRecovery-ajaxLoadSummaryData")
    public String ajaxLoadSummaryData() {
		System.out.println("CCCCCC");
		prepareAssign();
        populateSummaryData();
        System.out.println("AAAAAAA");
        return "summaryResults";
    }
	
	@ReadOnly
    private void populateSummaryData() {
		System.out.println("recoveryAssign.getId()--->"+recoveryAssign.getId());
        recoveryAssign = (Recovery) persistenceService.find("from Recovery where id=?", recoveryAssign.getId());
        System.out.println("recoveryAssign.getChartofaccounts().getId()-->"+recoveryAssign.getChartofaccounts().getId());
        type = recoveryAssign.getType();
        String deptQuery = "";
        String partyNameQuery = "";
        if (department.getCode() != null && !department.getCode().equals("-1"))
            deptQuery = " and mis.departmentcode='" + department.getCode()+"'";
        if (detailKey != null && detailKey != -1)
            partyNameQuery = " and gld.detailkeyid=" + detailKey;
        List<Object[]> result = new ArrayList<Object[]>();
        List<Object[]> resultTolDeduction = new ArrayList<Object[]>();
        try {
            RemittanceBean remittanceBean = new RemittanceBean();
            remittanceBean.setRecoveryId(recoveryAssign.getId());
            if(remitRecoveryService.isNonControlledCodeTds(remittanceBean)){
                final String qry = "select vh.name,erd.remittedamt,er.month ,erd.assign_number, erd.id "
                        + "FROM eg_remittance_detail erd,voucherheader vh1 RIGHT OUTER JOIN eg_remittance er ON vh1.id=er.paymentvhid,"
                        + "voucherheader vh,vouchermis mis,generalledger gl,fund f, eg_remittance_gl ergl WHERE erd.remittanceglid = ergl.id AND "
                        + "erd.remittanceid=er.id  AND gl.glcodeid ="+recoveryAssign.getChartofaccounts().getId()+" AND vh.id =mis.voucherheaderid AND vh1.status =0  AND "
                        + "gl.id = ergl.glid  AND gl.voucherheaderid     =vh.id  AND er.fundid =f.id AND f.id = "+fund.getId()+" AND vh.status =0 AND "
                        + "vh.voucherDate <= to_date('"+Constants.DDMMYYYYFORMAT2.format(asOnDate)+"','dd/MM/yyyy') and  "
                        + "vh.voucherDate >= to_date('"+Constants.DDMMYYYYFORMAT2.format(fromDate)+"','dd/MM/yyyy') "+ deptQuery
                        + " order by er.month,vh.name";
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug(qry);
                result = persistenceService.getSession().createSQLQuery(qry).list();
                
             // Query to get total deduction
                final String qryTolDeduction = "SELECT type,MONTH,glamt FROM (SELECT DISTINCT er.month AS MONTH,ergl.glamt AS glamt,ergl.glid as glid,vh.name AS type "
                        + "FROM eg_remittance_detail erd,voucherheader vh1 RIGHT OUTER JOIN eg_remittance er ON vh1.id=er.paymentvhid,"
                        + "voucherheader vh,vouchermis mis,generalledger gl,fund f, eg_remittance_gl ergl WHERE erd.remittanceglid = ergl.id AND "
                        + "erd.remittanceid=er.id  AND gl.glcodeid = "+recoveryAssign.getChartofaccounts().getId()+" AND vh.id =mis.voucherheaderid AND vh1.status =0  AND "
                        + "gl.id = ergl.glid  AND gl.voucherheaderid     =vh.id  AND er.fundid =f.id AND f.id = "+fund.getId()+" AND vh.status =0 AND "
                        + "vh.voucherDate <= to_date('"+Constants.DDMMYYYYFORMAT2.format(asOnDate)+"','dd/MM/yyyy') and  "
                        + "vh.voucherDate >= to_date('"+Constants.DDMMYYYYFORMAT2.format(fromDate)+"','dd/MM/yyyy')) "
                        + "as temptable order by type,month";
                resultTolDeduction = persistenceService.getSession().createSQLQuery(qryTolDeduction).list();
            }else{
                final String qry = "select vh.name,erd.remittedamt,er.month,erd.assign_number,erd.id  from eg_remittance_detail erd,"
                        +
                        " voucherheader vh1 right outer join eg_remittance er on vh1.id=er.paymentvhid,voucherheader vh,vouchermis mis,generalledger gl,generalledgerdetail gld,fund f,eg_remittance_gldtl ergl where "
                        +
                        " erd.remittancegldtlid= ergl.id and erd.remittanceid=er.id and gl.glcodeid="
                        + recoveryAssign.getChartofaccounts().getId()
                        + " and vh.id=mis.voucherheaderid and "
                        +
                        "  vh1.status=0 and ergl.gldtlid=gld.id and gl.id=gld.generalledgerid and gl.voucherheaderid=vh.id and er.fundid=f.id and f.id="
                        + fund.getId() +
                        " and vh.status=0 and vh.voucherDate <= to_date('" + Constants.DDMMYYYYFORMAT2.format(asOnDate)
                        + "','dd/MM/yyyy') and " + "vh.voucherDate >= to_date('"
                        + Constants.DDMMYYYYFORMAT2.format(fromDate)
                        + "','dd/MM/yyyy') " + deptQuery + partyNameQuery + "  order by er.month,vh.name";
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug(qry);
                result = persistenceService.getSession().createSQLQuery(qry).list();
                // Query to get total deduction
                final String qryTolDeduction = "SELECT type,MONTH,gldtamt FROM (SELECT DISTINCT er.month AS MONTH,ergl.gldtlamt AS gldtamt,"
                        +
                        "ergl.gldtlid as gldtlid,vh.name AS type FROM eg_remittance_detail erd,voucherheader vh1 RIGHT OUTER JOIN eg_remittance er ON vh1.id=er.paymentvhid,"
                        +
                        "voucherheader vh,vouchermis mis,generalledger gl,generalledgerdetail gld,fund f, eg_remittance_gldtl ergl WHERE erd.remittancegldtlid= ergl.id"
                        +
                        " AND erd.remittanceid=er.id  AND gl.glcodeid ="
                        + recoveryAssign.getChartofaccounts().getId()
                        + " AND vh.id =mis.voucherheaderid AND vh1.status =0 "
                        +
                        " AND ergl.gldtlid =gld.id  AND gl.id = gld.generalledgerid  AND gl.voucherheaderid     =vh.id  AND er.fundid =f.id"
                        +
                        " AND f.id ="
                        + fund.getId()
                        + " AND vh.status =0 AND vh.voucherDate <= to_date('"
                        + Constants.DDMMYYYYFORMAT2.format(asOnDate)
                        + "','dd/MM/yyyy') and "
                        + " vh.voucherDate >= to_date('"
                        + Constants.DDMMYYYYFORMAT2.format(fromDate)
                        + "','dd/MM/yyyy') " + deptQuery + partyNameQuery + ") as temptable order by type,month";
                resultTolDeduction = persistenceService.getSession().createSQLQuery(qryTolDeduction).list();
            }
        } catch (final ApplicationRuntimeException e) {
        	e.printStackTrace();
        	message = e.getMessage();
            return;
        } catch (final Exception e) {
        	e.printStackTrace();
        	message = e.getMessage();
            return;
        }
        for (final Object[] entry : result)
            for (final Object[] dedentry : resultTolDeduction) {
                final TDSEntry tds = new TDSEntry();
                final String monthChk = DateUtils.getAllMonthsWithFullNames().get(Integer.valueOf(entry[2].toString()) + 1);
                if (monthChk.equalsIgnoreCase(DateUtils.getAllMonthsWithFullNames().get(
                        Integer.valueOf(dedentry[1].toString()) + 1))
                        && dedentry[0].toString().equalsIgnoreCase(entry[0].toString()) && dedentry[2].toString().equalsIgnoreCase(entry[1].toString())) {
                    tds.setNatureOfDeduction(entry[0].toString());
                    tds.setTotalRemitted(new BigDecimal(entry[1].toString()));
                    tds.setMonth(DateUtils.getAllMonthsWithFullNames().get(Integer.valueOf(entry[2].toString()) + 1));
                    if(entry[3] != null)
                    {
                    	tds.setAutoAssignNumber(entry[3].toString());
                    }
                    else
                    {
                    	tds.setAutoAssignNumber(null);
                    }
                    
                    final BigDecimal totDeduction = new BigDecimal(dedentry[2].toString());
                    tds.setTotalDeduction(totDeduction);
                    tds.setRemDtlId(Long.valueOf(entry[4].toString()));
                    remittedTDS.add(tds);
                }
            }
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<TDSEntry> getRemittedTDS() {
		return remittedTDS;
	}

	public void setRemittedTDS(List<TDSEntry> remittedTDS) {
		this.remittedTDS = remittedTDS;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getFormattedDate(final Date date) {
        return Constants.DDMMYYYYFORMAT2.format(date);
    }

	public List<Long> getRemDtlIds() {
		return remDtlIds;
	}

	public void setRemDtlIds(List<Long> remDtlIds) {
		this.remDtlIds = remDtlIds;
	}

	public List<String> getRemAssignNumbers() {
		return remAssignNumbers;
	}

	public void setRemAssignNumbers(List<String> remAssignNumbers) {
		this.remAssignNumbers = remAssignNumbers;
	}
	
	public void prepareSaveAssignNumbers() {
		prepareAssign();
    }
	
	@ValidationErrorPage(value = RemitRecoveryAction.SUMMARYFORM)
	@Action(value = "/deduction/remitRecovery-saveAssignNumbers")
    public String saveAssignNumbers() {
		System.out.println("Enter");
		prepareAssign();
		int i=0;
		try
		{
			for(String number:remAssignNumbers)
			{
				if(number != null && !number.isEmpty())
				{
					persistenceService.getSession()
	                .createSQLQuery(
	                        "update eg_remittance_detail  set assign_number = :assignNumber where id =:dtlId ")
	                .setLong("dtlId", remDtlIds.get(i)).setString("assignNumber", number)
	                .executeUpdate();
				}
				i++;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done");
		message="Acknowledge Number(s) has been saved successfully";
        return assignNumber();
    }

	public String getFirstsignatory() {
		return firstsignatory;
	}

	public void setFirstsignatory(String firstsignatory) {
		this.firstsignatory = firstsignatory;
	}

	public String getSecondsignatory() {
		return secondsignatory;
	}

	public void setSecondsignatory(String secondsignatory) {
		this.secondsignatory = secondsignatory;
	}

	
	public String getFileno() {
		return fileno;
	}

	public void setFileno(String fileno) {
		this.fileno = fileno;
	}

	public String getBackdateentry() {
		return backdateentry;
	}

	public void setBackdateentry(String backdateentry) {
		this.backdateentry = backdateentry;
	}

	public String getTypeOfAccount() {
		return typeOfAccount;
	}

	public void setTypeOfAccount(String typeOfAccount) {
		this.typeOfAccount = typeOfAccount;
	}

	
	
	
	
    
}