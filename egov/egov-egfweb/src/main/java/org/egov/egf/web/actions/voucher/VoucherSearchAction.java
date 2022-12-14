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
package org.egov.egf.web.actions.voucher;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Functionary;
import org.egov.commons.Fund;
import org.egov.commons.Fundsource;
import org.egov.commons.Scheme;
import org.egov.commons.SubScheme;
import org.egov.commons.Vouchermis;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.commons.repository.AccountDetailKeyRepository;
import org.egov.egf.commons.VoucherSearchUtil;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.exception.ApplicationException;
import org.egov.infra.microservice.models.BillDetail;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.Receipt;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.persistence.utils.Page;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infra.web.utils.EgovPaginatedList;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.model.bills.EgBillregistermis;
import org.egov.model.payment.Paymentheader;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.egov.utils.VoucherHelper;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@ParentPackage("egov")
@Results({ @Result(name = VoucherSearchAction.SEARCH, location = "voucherSearch-search.jsp"),
		@Result(name = com.opensymphony.xwork2.Action.SUCCESS, type = "redirect", location = "voucherSearch.action") })
public class VoucherSearchAction extends BaseFormAction {
	private static final Logger LOGGER = Logger.getLogger(VoucherSearchAction.class);
	private static final long serialVersionUID = 1L;
	public CVoucherHeader voucherHeader = new CVoucherHeader();
	public static final String SEARCH = "search";
	public List<Map<String, Object>> voucherList;
	private List<Object> schemeList;
	public Map<String, String> nameList = new LinkedHashMap<String, String>();
	public final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Constants.LOCALE);
	public final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Constants.LOCALE);
	@Autowired
	protected AppConfigValueService appConfigValuesService;
	@Autowired
	protected MicroserviceUtils microserviceUtils;
	@Autowired
	protected EgovMasterDataCaching masterDataCache;
	@Autowired
	AccountDetailKeyRepository accountDetailKeyRepository;
	
	@Autowired
        @Qualifier("voucherHelper")
        private VoucherHelper voucherHelpers;

	private final List<String> headerFields = new ArrayList<String>();
	private final List<String> mandatoryFields = new ArrayList<String>();
	public Date fromDate = new Date();
	public Date toDate;
	private String showMode;
	private BigDecimal amount;
	private String partyName;
	private VoucherSearchUtil voucherSearchUtil;
	private final Map<Integer, String> sourceMap = new HashMap<Integer, String>();
	private Integer page = 1;
	private Integer pageSize = 30;
	private EgovPaginatedList pagedResults;
	List<String> voucherTypes = VoucherHelper.VOUCHER_TYPES;
	Map<String, List<String>> voucherNames;
	private FinancialYearDAO financialYearDAO;
	private Department deptImpl = new Department();
	private Map<Long,String> paymentVoucherMap=new HashMap<Long,String>();

	@Override
	public Object getModel() {
		return voucherHeader;
	}

	public VoucherSearchAction() {
		LOGGER.error("creating instance of VoucherSearchAction ");
		voucherHeader.setVouchermis(new Vouchermis());
		addRelatedEntity("vouchermis.departmentcode", String.class);
		addRelatedEntity("fundId", Fund.class);
		addRelatedEntity("vouchermis.schemeid", Scheme.class);
		addRelatedEntity("vouchermis.subschemeid", SubScheme.class);
		addRelatedEntity("vouchermis.functionary", Functionary.class);
		addRelatedEntity("fundsourceId", Fundsource.class);
		addRelatedEntity("vouchermis.divisionid", Boundary.class);
	}

	@Override
	public void prepare() {
		super.prepare();
		getHeaderFields();
		populateSourceMap();
		if (headerFields.contains("department"))
			addDropdownData("departmentList", masterDataCache.get("egi-department"));
		if (headerFields.contains("functionary"))
			addDropdownData("functionaryList",
					persistenceService.findAllBy(" from Functionary where isactive=true order by name"));
		if (headerFields.contains("fund"))
			addDropdownData("fundList",
					persistenceService.findAllBy(" from Fund where isactive=true and isnotleaf=false order by name"));
		if (headerFields.contains("fundsource"))
			addDropdownData("fundsourceList",
					persistenceService.findAllBy(" from Fundsource where isactive=true order by name"));
		if (headerFields.contains("field"))
			addDropdownData("fieldList",
					persistenceService.findAllBy(" from Boundary b where lower(b.boundaryType.name)='ward' "));
		if (headerFields.contains("scheme"))
			addDropdownData("schemeList", Collections.EMPTY_LIST);
		if (headerFields.contains("subscheme"))
			addDropdownData("subschemeList", Collections.EMPTY_LIST);

		if (null != parameters.get("showMode")) {
			showMode = parameters.get("showMode")[0];
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("show mode  :" + showMode);
			if (showMode.equalsIgnoreCase("nonBillPayment")) {
				final List<String> typeList = new ArrayList<String>();
				typeList.add(FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL);
				addDropdownData("typeList", typeList);
				voucherHeader.setType(FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL);
				nameList = new LinkedHashMap<String, String>();
				nameList.put(FinancialConstants.JOURNALVOUCHER_NAME_CONTRACTORJOURNAL,
						FinancialConstants.JOURNALVOUCHER_NAME_CONTRACTORJOURNAL);
				nameList.put(FinancialConstants.JOURNALVOUCHER_NAME_SUPPLIERJOURNAL,
						FinancialConstants.JOURNALVOUCHER_NAME_SUPPLIERJOURNAL);
				nameList.put(FinancialConstants.JOURNALVOUCHER_NAME_SALARYJOURNAL,
						FinancialConstants.JOURNALVOUCHER_NAME_SALARYJOURNAL);
			} else
				addDropdownData("typeList", VoucherHelper.VOUCHER_TYPES);
			// addDropdownData("typeList",
			// persistenceService.findAllBy(" select distinct vh.type from
			// CVoucherHeader vh where vh.status!=4 order by vh.type"));
		} else
			addDropdownData("typeList", VoucherHelper.VOUCHER_TYPES);
		voucherNames = voucherHelpers.getVoucherNamesAndTypes();
		// addDropdownData("typeList",
		// persistenceService.findAllBy(" select distinct vh.type from
		// CVoucherHeader vh where vh.status!=4 order by vh.type"));
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Number of  MIS attributes are :" + headerFields.size());
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Number of mandate MIS attributes are :" + mandatoryFields.size());

	}

	private void populateSourceMap() {
		List<Object[]> sourceList = new ArrayList<Object[]>();
		;
		sourceList = persistenceService.findAllBy(
				" select distinct m.id,m.name from CVoucherHeader  vh, EgModules m where m.id=vh.moduleId and vh.status!=4 order by m.name");

		for (final Object[] obj : sourceList)
			sourceMap.put((Integer) obj[0], (String) obj[1]);
		// For vouchers created from the financial module
		sourceMap.put(-2, "Internal");
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Added sourceMap of size -" + sourceMap.size());
	}

	public Map<String, String> getVoucherNameMap(final String type) {
		final List<Object> voucherNameList = getPersistenceService()
				.findAllBy("select  distinct name from  CVoucherHeader where type=?", type);
		nameList = new LinkedHashMap<String, String>();

		for (final Object voucherName : voucherNameList)
			nameList.put((String) voucherName, (String) voucherName);
		return nameList;
	}

	@SkipValidation
	@Action(value = "/voucher/voucherSearch-beforesearch")
	public String beforesearch() {
		finYearDate();
		if (showMode != null && showMode.equalsIgnoreCase("nonBillPayment")) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("nonBillPayment");
		} else if (voucherHeader.getType() != null && !voucherHeader.getType().equals("-1"))
			getVoucherNameMap(voucherHeader.getType());

		return SEARCH;

	}

	public void prepareSearch() {

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Voucher Search Action | prepareSearch");
		if (showMode != null && !showMode.equalsIgnoreCase("nonbillPayment"))
			if (null != parameters.get("type") && !parameters.get("type")[0].equalsIgnoreCase("-1"))
				nameList = getVoucherNameMap(parameters.get("type")[0]);

	}

	@ValidationErrorPage(value = SEARCH)
	@Action(value = "/voucher/voucherSearch-search")
	public String search() throws ApplicationException, ParseException {
		LOGGER.info("amount :::"+amount);
		LOGGER.info("partyName  :::"+partyName);
		boolean ismodifyJv = false;
		voucherList = new ArrayList<Map<String, Object>>();
		Map<String, Object> voucherMap = null;
		voucherHeader.getVouchermis().setDepartmentcode(deptImpl.getCode());
		if (null != parameters.get("showMode"))
			showMode = parameters.get("showMode")[0];
		if (voucherHeader.getModuleId() != null && voucherHeader.getModuleId() == -1)
			voucherHeader.setModuleId(null);
		// validate if mode is edit and financial year is not active
		if (null != showMode && showMode.equalsIgnoreCase("edit")) {
			final boolean validateFinancialYearForPosting = voucherSearchUtil.validateFinancialYearForPosting(fromDate,
					toDate);
			if (!validateFinancialYearForPosting)
				throw new ValidationException(Arrays.asList(new ValidationError(
						"Financial Year  Not active for Posting(either year or date within selected date range)",
						"Financial Year  Not active for Posting(either year or date within selected date range)")));
		}

		List<CVoucherHeader> list = new ArrayList<CVoucherHeader>();
		List<CVoucherHeader> filterlist =new ArrayList<CVoucherHeader>();
		List<Query> qryObj;
		
		// for view voucher implementing paginated result
		if (null == showMode || showMode.equals("")) {
			System.out.println("before query search");
			qryObj = voucherSearchUtil.voucherSearchQuery(voucherHeader, fromDate, toDate, showMode);
			final Query qry = qryObj.get(0);
			final Long count = (Long) persistenceService.find(qryObj.get(1).getQueryString());
			//final Page resPage = new Page(qry, page, pageSize);
			//pagedResults = new EgovPaginatedList(resPage, count.intValue());
			try
			{
				System.out.println("before executing");
			list = qry.list();
			if(list !=null)
			{
				System.out.println("size --->"+list.size());
			}
			System.out.println("after executing");
			}catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("quey executed");
		} else if (showMode.equalsIgnoreCase("nonbillPayment"))
		{
			list = voucherSearchUtil.searchNonBillVouchers(voucherHeader, fromDate, toDate, showMode);
			if(list !=null)
			{
				System.out.println("size1 --->"+list.size());
			}
		}
		else
		{
			list = voucherSearchUtil.search(voucherHeader, fromDate, toDate, showMode);
			if(list !=null)
			{
				System.out.println("size2 --->"+list.size());
			}
		}
		if (null == showMode || showMode.equals("")) {
			paymentVoucherMap.clear();
			
			Map<Long,List<String>> voucherPartyMap=new HashMap<Long,List<String>>();
			Map<String,String> receiptPaidByMapping=new HashMap<String,String>();
			System.out.println("Type ::: "+voucherHeader.getType());
			if(partyName != null && !partyName.isEmpty())
			{
				if(!voucherHeader.getType().equalsIgnoreCase("Receipt"))
				{
					System.out.println("for non receipt");
					getParty(voucherPartyMap,voucherHeader.getType());
				}
				else
				{
					System.out.println("for receipts");
					getReceiptMapping(receiptPaidByMapping,list);
				}
			}
			
			System.out.println("before filter");
			if(list !=null)
			{
				System.out.println("size3 --->"+list.size());
			}
			for (CVoucherHeader cVoucherHeader : list) {
				
				 if(amount != null && amount.compareTo(BigDecimal.ZERO) != 0){
					 
					 if(partyName != null && !partyName.isEmpty()){
						 
						 if (null != cVoucherHeader.getType() &&  !cVoucherHeader.getType().equalsIgnoreCase("Receipt"))
						 {
									 
									 if(cVoucherHeader.getTotalAmount().compareTo(amount) == 0 && checkParty(cVoucherHeader.getId(),voucherPartyMap,partyName) ) {
										 filterlist.add(cVoucherHeader);
									 }
						 }
						else if (null != cVoucherHeader.getType() && cVoucherHeader.getType().equalsIgnoreCase("Receipt")){
							
								if(cVoucherHeader.getTotalAmount().compareTo(amount) == 0 && checkReceiptParty(cVoucherHeader,partyName,receiptPaidByMapping))
									{
									filterlist.add(cVoucherHeader);
									}
							}
						 
					 }
					 else {
						 
						 if(cVoucherHeader.getTotalAmount().compareTo(amount) == 0) {
							 
							 filterlist.add(cVoucherHeader);
						 }
						
					 }
					 
				 }
				 else if(partyName!=null && !partyName.isEmpty()){
					 if (null != cVoucherHeader.getType() &&  !cVoucherHeader.getType().equalsIgnoreCase("Receipt"))
					 {
						 if(checkParty(cVoucherHeader.getId(),voucherPartyMap,partyName) ) {
							 filterlist.add(cVoucherHeader);
						 }
							 
						
					
				 }
					else if (null != cVoucherHeader.getType() && cVoucherHeader.getType().equalsIgnoreCase("Receipt")){
						if(checkReceiptParty(cVoucherHeader,partyName,receiptPaidByMapping))
						{
						filterlist.add(cVoucherHeader);
						}
					}
					 
				 }
				 else {
					 filterlist=list;
				 }
				
			}
			
			if(filterlist !=null)
			{
				System.out.println("size F--->"+filterlist.size());
			}
			System.out.println("fileter execution done");
			
			
			
			populateVoucherMap(filterlist);
			System.out.println("populateVoucherMap execution done");
			if(filterlist !=null)
			{
				System.out.println("size F2--->"+filterlist.size());
			}
			for (final CVoucherHeader voucherheader : filterlist) {
				voucherMap = new HashMap<String, Object>();
				final BigDecimal amt = voucherheader.getTotalAmount();
				voucherMap.put("id", voucherheader.getId());
				voucherMap.put("vouchernumber", voucherheader.getVoucherNumber());
				voucherMap.put("type", voucherheader.getType());
				voucherMap.put("name", voucherheader.getName());
				if (voucherheader.getVouchermis() != null && voucherheader.getVouchermis().getDepartmentcode() != null
						&& !voucherheader.getVouchermis().getDepartmentcode().equals("-1")) {
					org.egov.infra.microservice.models.Department depList = microserviceUtils
							.getDepartmentByCode(voucherheader.getVouchermis().getDepartmentcode());
					voucherMap.put("deptName", depList.getName());
				}
				voucherMap.put("voucherdate", voucherheader.getVoucherDate());
				voucherMap.put("fundname", voucherheader.getFundId().getName());
				if (voucherheader.getModuleId() == null)
					voucherMap.put("source", "Internal");
				else
					voucherMap.put("source", sourceMap.get(voucherheader.getModuleId()));

				voucherMap.put("amount", amt.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString());
				voucherMap.put("status", getVoucherStatus(voucherheader.getStatus()));
				if(!(voucherheader.getName().equals("Remittance Payment") || voucherheader.getName().equals("Bill Payment") || voucherheader.getName().equals("Direct Bank Payment")) && voucherheader.getStatus()!=4 && voucherheader.getStatus()!=0 && voucherheader.getState() != null)
				{
					System.out.println("pending with ::::"+voucherheader.getState().getOwnerPosition());
					voucherMap.put("pendingWith", voucherheader.getState().getOwnerName());
				}
				else if((voucherheader.getName().equals("Remittance Payment") || voucherheader.getName().equals("Bill Payment") || voucherheader.getName().equals("Direct Bank Payment")) && voucherheader.getStatus()!=4 && voucherheader.getStatus()!=0 && voucherheader.getState() == null)
				{
					if(paymentVoucherMap.get(voucherheader.getId()) != null)
					{
						voucherMap.put("pendingWith", paymentVoucherMap.get(voucherheader.getId()));
					}
					else
					{
						voucherMap.put("pendingWith", "-");
					}
					
				}
				else
				{
					voucherMap.put("pendingWith", "-");
				}
				voucherList.add(voucherMap);
			}
			if(voucherList != null)
			{
				System.out.println("voucher size ::"+voucherList.size());
			}
			Set<String> keys=new HashSet<String>();
			if(voucherMap !=null)
			{
				keys=voucherMap.keySet();
				if(keys !=null)
				{
					System.out.println("size map Set--->"+keys.size());
				}
				
			}
			System.out.println("populateVoucherMap execution ended");
			Page page = new Page<Map<String, Object>>(1, voucherList.size(), voucherList);
			pagedResults = new EgovPaginatedList(page, voucherList.size());
			pagedResults.setList(voucherList);
			System.out.println("size ::: "+pagedResults.getList().size());
		} else
		{
			paymentVoucherMap.clear();
			populateVoucherMap(list);
			for (final CVoucherHeader voucherheader : list) {
				if (voucherheader.getState() != null) {
					final EgBillregistermis billMis = (EgBillregistermis) persistenceService
							.find("from EgBillregistermis where voucherHeader.id=?", voucherheader.getId());
					if (billMis != null) {
						/*
						 * bill state will be null if created from create JV
						 * screen and voucher is in end state
						 */
						if (billMis.getEgBillregister().getState() == null
								&& voucherheader.getState().getValue().contains("END"))
							ismodifyJv = true;
						else
							ismodifyJv = false;
					} else if (voucherheader.getName().equalsIgnoreCase(FinancialConstants.JOURNALVOUCHER_NAME_GENERAL)
							&& voucherheader.getState().getValue().contains("END"))
						ismodifyJv = true;
				} else
					ismodifyJv = true;
				if (ismodifyJv) {
					voucherMap = new HashMap<String, Object>();
					voucherMap.put("id", voucherheader.getId());
					voucherMap.put("vouchernumber", voucherheader.getVoucherNumber());
					voucherMap.put("type", voucherheader.getType());
					voucherMap.put("name", voucherheader.getName());
					if (voucherheader.getVouchermis().getDepartmentcode() != null
							&& !voucherheader.getVouchermis().getDepartmentcode().equals("-1")) {
						org.egov.infra.microservice.models.Department depList = microserviceUtils
								.getDepartmentByCode(voucherheader.getVouchermis().getDepartmentcode());
						voucherMap.put("deptName", depList.getName());
					}
					voucherMap.put("voucherdate", voucherheader.getVoucherDate());
					voucherMap.put("fundname", voucherheader.getFundId().getName());
					if (voucherheader.getModuleId() == null)
						voucherMap.put("source", "Internal");
					else
						voucherMap.put("source", sourceMap.get(voucherheader.getModuleId()));
					/*
					 * for(VoucherDetail
					 * detail:voucherheader.getVoucherDetail()) { amt =
					 * amt.add(detail.getDebitAmount()); }
					 */
					voucherMap.put("amount",
							voucherheader.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_EVEN).toString());
					voucherMap.put("status", getVoucherStatus(voucherheader.getStatus()));
					if(!(voucherheader.getName().equals("Remittance Payment") || voucherheader.getName().equals("Bill Payment") || voucherheader.getName().equals("Direct Bank Payment")) && voucherheader.getStatus()!=4 && voucherheader.getStatus()!=0 && voucherheader.getState() != null)
					{
						voucherMap.put("pendingWith", voucherheader.getState().getOwnerName());
					}
					else if((voucherheader.getName().equals("Remittance Payment") || voucherheader.getName().equals("Bill Payment") || voucherheader.getName().equals("Direct Bank Payment")) && voucherheader.getStatus()!=4 && voucherheader.getStatus()!=0 && voucherheader.getState() == null)
					{
						if(paymentVoucherMap.get(voucherheader.getId()) != null)
						{
							voucherMap.put("pendingWith", paymentVoucherMap.get(voucherheader.getId()));
						}
						else
						{
							voucherMap.put("pendingWith", "-");
						}
						
					}
					else
					{
						voucherMap.put("pendingWith", "-");
					}
					voucherList.add(voucherMap);
				}
			}
	}
		System.out.println("END");
		return SEARCH;
	}

	
	
	private boolean checkReceiptParty(CVoucherHeader cVoucherHeader, String partyName,
			Map<String, String> receiptPaidByMapping) {
		boolean result=false;
		String receiptParty="";
		if(cVoucherHeader.getVouchermis().getRecieptNumber() != null && !cVoucherHeader.getVouchermis().getRecieptNumber().isEmpty()) {
			receiptParty=receiptPaidByMapping.get(cVoucherHeader.getVouchermis().getRecieptNumber());
			if(receiptParty != null && !receiptParty.isEmpty() && (receiptParty.toLowerCase()).contains((partyName.toLowerCase()))) {
				result=true;
			}
		}
		
		
		return result;
	}

	private void getReceiptMapping(Map<String, String> receiptPaidByMapping, List<CVoucherHeader> list) {
		String receiptNumbers="";
		for(CVoucherHeader voucher:list) {
			if(voucher.getVouchermis().getRecieptNumber() != null && !voucher.getVouchermis().getRecieptNumber().isEmpty())
			{
				receiptNumbers=receiptNumbers+voucher.getVouchermis().getRecieptNumber()+",";
			}
		}
		receiptNumbers=receiptNumbers.substring(0,receiptNumbers.length()-1);
		List<Receipt> receipts = microserviceUtils.searchRecieptsFinance("MISCELLANEOUS", null, null, null,
                (receiptNumbers != null && !receiptNumbers.isEmpty() && !"".equalsIgnoreCase(receiptNumbers))
                        ? receiptNumbers : "","search");
		
		for (Receipt receipt : receipts) {

            for (org.egov.infra.microservice.models.Bill bill : receipt.getBill()) {

                for (BillDetail billDetail : bill.getBillDetails()) {
                	receiptPaidByMapping.put(billDetail.getReceiptNumber(), bill.getPaidBy());
                }
            }
		}
		
	}

	private boolean checkParty(Long id, Map<Long, List<String>> voucherPartyMap, String partyName) {
		boolean result=false;
		List<String> partyNames=voucherPartyMap.get(id);
		if(partyNames != null && !partyNames.isEmpty())
		{
			for(String party : partyNames)
			{
				if((party.toLowerCase()).contains((partyName.toLowerCase())))
				{
					result=true;
					break;
				}
			}
		}
		
		return result;
	}

	
	
	
	private void populateVoucherMap(List<CVoucherHeader> list) {
		List<Long> vhIds=new ArrayList<Long>();
		List<Paymentheader> paymentList =null;
		for(CVoucherHeader vh:list)
		{
			if((vh.getName().equals("Remittance Payment") || vh.getName().equals("Bill Payment") || vh.getName().equals("Direct Bank Payment")))
			{
				vhIds.add(vh.getId());
			}
		}
		if(vhIds!=null && !vhIds.isEmpty())
		{
			try
			{
				paymentList = persistenceService.findAllByNamedQuery("getPaymentList",
						vhIds);
			}catch (Exception e) {
				e.printStackTrace();
			}
			 
		}
		if(paymentList !=null)
		{
			System.out.println("size P--->"+paymentList.size());
		}
		
		if(paymentList!=null && !paymentList.isEmpty())
		{
			for(Paymentheader ph : paymentList)
			{
				if(ph.getState() != null)
				{
					if(ph.getState().getOwnerPosition() != null && ph.getState().getOwnerPosition() > 0L)
					{
						try
						{
							paymentVoucherMap.put(ph.getVoucherheader().getId(),ph.getState().getOwnerName());
						}catch (Exception e) {
							e.printStackTrace();
						}
						
					}
					
				}
			}
		}
		Set<Long> keys=new HashSet<Long>();
		if(paymentVoucherMap !=null)
		{
			keys=paymentVoucherMap.keySet();
			if(keys !=null)
			{
				System.out.println("size Set--->"+keys.size());
			}
			
		}
	}


	private String getVoucherStatus(final int status) {
		
		System.out.println(":::::::::::: "+status);
		if (FinancialConstants.CREATEDVOUCHERSTATUS.equals(status))
			return "Approved";
		if (FinancialConstants.REVERSEDVOUCHERSTATUS.equals(status))
			return "Reversed";
		if (FinancialConstants.REVERSALVOUCHERSTATUS.equals(status))
			return "Reversal";
		if (FinancialConstants.CANCELLEDVOUCHERSTATUS.equals(status))
			return "Cancelled";
		if (FinancialConstants.PREAPPROVEDVOUCHERSTATUS.equals(status))
			return "Created";
		if (FinancialConstants.SAVEASDRAFT.equals(status))
			return "Save As Draft";
		
			
		
		return "";
	}

	public void finYearDate() {
		final String financialYearId = financialYearDAO.getCurrYearFiscalId();
		if (financialYearId == null || financialYearId.equals(""))
			fromDate = new Date();
		else
			fromDate = (Date) persistenceService.find("select startingDate  from CFinancialYear where id=?",
					Long.parseLong(financialYearId));
	}

	public void setFinancialYearDAO(final FinancialYearDAO financialYearDAO) {
		this.financialYearDAO = financialYearDAO;
	}

	protected void getHeaderFields() {
		final List<AppConfigValues> appConfigList = appConfigValuesService.getConfigValuesByModuleAndKey(
				FinancialConstants.MODULE_NAME_APPCONFIG, "DEFAULT_SEARCH_MISATTRRIBUTES");

		for (final AppConfigValues appConfigVal : appConfigList) {
			final String value = appConfigVal.getValue();
			final String header = value.substring(0, value.indexOf('|'));
			headerFields.add(header);
			final String mandate = value.substring(value.indexOf('|') + 1);
			if (mandate.equalsIgnoreCase("M"))
				mandatoryFields.add(header);
		}

	}

	@Override
	public void validate() {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Inside Validate Method");
		if (voucherHeader.getVoucherNumber() == null || "".equals(voucherHeader.getVoucherNumber())) {
			if (fromDate == null)
				addFieldError("From Date", getText("Please Enter From Date"));
			if (toDate == null)
				addFieldError("To Date", getText("Please Enter To Date"));
			checkMandatoryField("fundId", "fund", voucherHeader.getFundId(), "voucher.fund.mandatory");
			checkMandatoryField("vouchermis.departmentcode", "department",
					voucherHeader.getVouchermis().getDepartmentcode(), "voucher.department.mandatory");
			checkMandatoryField("vouchermis.schemeid", "scheme", voucherHeader.getVouchermis().getSchemeid(),
					"voucher.scheme.mandatory");
			checkMandatoryField("vouchermis.subschemeid", "subscheme", voucherHeader.getVouchermis().getSubschemeid(),
					"voucher.subscheme.mandatory");
			checkMandatoryField("vouchermis.functionary", "functionary", voucherHeader.getVouchermis().getFunctionary(),
					"voucher.functionary.mandatory");
			checkMandatoryField("fundsourceId", "fundsource", voucherHeader.getVouchermis().getFundsource(),
					"voucher.fundsource.mandatory");
			checkMandatoryField("vouchermis.divisionId", "field", voucherHeader.getVouchermis().getDivisionid(),
					"voucher.field.mandatory");
		}
	}

	protected void checkMandatoryField(final String objectName, final String fieldName, final Object value,
			final String errorKey) {
		if (mandatoryFields.contains(fieldName) && value == null)
			addFieldError(objectName, getText(errorKey));
	}

	public boolean isFieldMandatory(final String field) {
		return mandatoryFields.contains(field);
	}

	public boolean shouldShowHeaderField(final String field) {
		return headerFields.contains(field);
	}

	public List<Map<String, Object>> getVoucherList() {
		return voucherList;
	}

	public String ajaxLoadSchemes() {
		schemeList = persistenceService.findAllBy(" from Scheme where fund=?", voucherHeader.getFundId());
		return "schemes";
	}

	public String ajaxLoadSubSchemes() {
		schemeList = persistenceService.findAllBy(" from SubScheme where scheme=?",
				voucherHeader.getVouchermis().getSchemeid());
		return "schemes";
	}

	public List<Object> getSchemeList() {
		return schemeList;
	}

	public void setSchemeId(final Integer schemeId) {
		voucherHeader.getVouchermis()
				.setSchemeid((Scheme) persistenceService.find(" from Scheme where id=?", schemeId));
	}

	public void setVoucherHeader(final CVoucherHeader voucherHeader) {
		this.voucherHeader = voucherHeader;
	}

	public void setVoucherSearchUtil(final VoucherSearchUtil voucherSearchUtil) {
		this.voucherSearchUtil = voucherSearchUtil;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(final Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(final Date toDate) {
		this.toDate = toDate;
	}

	public Map<String, String> getNameList() {
		return nameList;
	}

	public void setNameList(final Map<String, String> nameList) {
		this.nameList = nameList;
	}

	public Map<Integer, String> getSourceMap() {
		return sourceMap;
	}

	public String getShowMode() {
		return showMode;
	}

	public void setShowMode(final String showMode) {
		this.showMode = showMode;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(final Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(final Integer pageSize) {
		this.pageSize = pageSize;
	}

	public EgovPaginatedList getPagedResults() {
		return pagedResults;
	}

	public void setPagedResults(final EgovPaginatedList pagedResults) {
		this.pagedResults = pagedResults;
	}

	public List<String> getVoucherTypes() {
		return voucherTypes;
	}

	public void setVoucherTypes(final List<String> voucherTypes) {
		this.voucherTypes = voucherTypes;
	}

	public Map<String, List<String>> getVoucherNames() {
		return voucherNames;
	}

	public void setVoucherNames(final Map<String, List<String>> voucherNames) {
		this.voucherNames = voucherNames;
	}

	public Department getDeptImpl() {
		return deptImpl;
	}

	public void setDeptImpl(final Department deptImpl) {
		this.deptImpl = deptImpl;
	}
	public String getEmployeeName(Long empId){
        
	       return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
	    }
	
	public List<EmployeeInfo> getEmployeeNameList(Long empId){
        
	       return microserviceUtils.getEmployee(empId, null, null, null);
	    }

	public Map<Long, String> getPaymentVoucherMap() {
		return paymentVoucherMap;
	}

	public void setPaymentVoucherMap(Map<Long, String> paymentVoucherMap) {
		this.paymentVoucherMap = paymentVoucherMap;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPartyName() {
		return partyName;
	}

	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}
	
	private void getParty(Map<Long,List<String>> voucherPartyMap,String type) {
    	SQLQuery query =  null;
    	List<Object[]> rows = null;
    	List<String> partyName=null;
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select v.id,a.detailname from voucherheader v,generalledger g ,generalledgerdetail g2 ,accountdetailkey a where v.id =g.voucherheaderid and g.id = g2.generalledgerid and g2.detailkeyid =a.detailkey and g2.detailtypeid=a.detailtypeid and v.type =:type");
    		 query.setString("type", type);
    	    rows = query.list();
    	    
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    	for(Object[] element : rows)
    	    	{
    	    		Long vhId =Long.parseLong(element[0].toString());
    	    		String party=element[1].toString();
    	    		if(voucherPartyMap.get(vhId) == null)
    	    		{
    	    			partyName= new ArrayList<String>();
    	    			partyName.add(party);
    	    			voucherPartyMap.put(vhId, partyName);
    	    		}
    	    		else
    	    		{
    	    			voucherPartyMap.get(vhId).add(party);
    	    		}
    	    	}
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	private void getPartyReceiptJournal(Map<Long,List<String>> voucherPartyMap,String type) {
    	SQLQuery query =  null;
    	List<Object[]> rows = null;
    	List<String> partyName=null;
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select v.id,a.detailname from voucherheader v,generalledger g ,generalledgerdetail g2 ,accountdetailkey a where v.id =g.voucherheaderid and g.id = g2.generalledgerid and g2.detailkeyid =a.detailkey and g2.detailtypeid=a.detailtypeid and v.name =:type");
    		 query.setString("type", type);
    	    rows = query.list();
    	    
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    	for(Object[] element : rows)
    	    	{
    	    		Long vhId =Long.parseLong(element[0].toString());
    	    		String party=element[1].toString();
    	    		if(voucherPartyMap.get(vhId) == null)
    	    		{
    	    			partyName= new ArrayList<String>();
    	    			partyName.add(party);
    	    			voucherPartyMap.put(vhId, partyName);
    	    		}
    	    		else
    	    		{
    	    			voucherPartyMap.get(vhId).add(party);
    	    		}
    	    	}
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	

}
