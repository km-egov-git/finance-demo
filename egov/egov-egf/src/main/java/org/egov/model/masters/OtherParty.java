package org.egov.model.masters;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.egov.commons.Bank;
import org.egov.commons.EgwStatus;
import org.egov.commons.utils.EntityType;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.persistence.validator.annotation.OptionalPattern;
import org.egov.infra.persistence.validator.annotation.Required;
import org.egov.infra.persistence.validator.annotation.Unique;
import org.egov.infra.validation.regex.Constants;
import org.egov.utils.FinancialConstants;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "EGF_OTHER_PARTY")
@Unique(fields = { "code", "registrationNumber" }, id = "id", tableName = "EGF_OTHER_PARTY", enableDfltMsg = true)
@SequenceGenerator(name = OtherParty.SEQ_EGF_OTHERPARTY, sequenceName = OtherParty.SEQ_EGF_OTHERPARTY, allocationSize = 1)
public class OtherParty extends AbstractAuditable implements EntityType {

    private static final long serialVersionUID = 2507334170114202599L;

    public static final String SEQ_EGF_OTHERPARTY = "SEQ_EGF_OTHERPARTY";

    @Id
    @GeneratedValue(generator = SEQ_EGF_OTHERPARTY, strategy = GenerationType.SEQUENCE)
    private Long id;

    @Length(max = 50, message = "Maximum of 50 Characters allowed for Code")
    @OptionalPattern(regex = FinancialConstants.alphaNumericwithspecialchar, message = "Special Characters are not allowed in Code")
    private String code;

    @Required(message = "Please Enter the Name")
    @OptionalPattern(regex = FinancialConstants.alphaNumericwithspecialcharForContraWOAndSupplierName, message = "Special Characters are not allowed in Name")
    @Length(max = 100, message = "Maximum of 100 Characters allowed for Name")
    private String name;

    @Length(max = 250, message = "Maximum of 250 Characters allowed for Correspondence Address")
    @OptionalPattern(regex = FinancialConstants.ALPHANUMERICWITHALLSPECIALCHAR, message = "Special characters are not allowed in correspondence address")
    private String correspondenceAddress;

    @Length(max = 250, message = "Maximum of 250 Characters allowed for Payment Address")
    @OptionalPattern(regex = FinancialConstants.ALPHANUMERICWITHALLSPECIALCHAR, message = "Special characters are not allowed in payment address")
    private String paymentAddress;

    @Length(max = 100, message = "Maximum of 100 Characters allowed for Contact Person")
    @OptionalPattern(regex = Constants.ALPHANUMERIC_WITHSPACE, message = "Special Characters are not allowed in Contact Person")
    private String contactPerson;

    @OptionalPattern(regex = Constants.EMAIL, message = "Invalid Email")
    @Length(max = 100, message = "Maximum of 100 Characters allowed for Email")
    private String email;

    @Length(max = 1024, message = "Maximum of 1024 Characters allowed for Narration")
    @OptionalPattern(regex = FinancialConstants.ALPHANUMERICWITHALLSPECIALCHAR, message = "Special Characters are not allowed in narration")
    private String narration;

    @Length(max = 10, message = "PAN No Field length should be 10 and it should be in the format XXXXX1234X")
    @OptionalPattern(regex = Constants.PANNUMBER, message = "Enter the PAN No in correct format - XXXXX1234X")
    private String panNumber;

    @Length(min = 15, max = 15, message = "Maximum of 15 Characters allowed for TIN/GST No")
    @OptionalPattern(regex = Constants.ALPHANUMERIC, message = "Special Characters are not allowed in TIN/GST No")
    private String tinNumber;

    @ManyToOne
    @JoinColumn(name = "bank")
    private Bank bank;

    @Length(min = 11, max = 11, message = "Maximum of 11 Characters allowed for IFSC Code")
    @OptionalPattern(regex = Constants.ALPHANUMERIC, message = "Special Characters are not allowed in IFSC Code")
    private String ifscCode;

    @Length(max = 22, message = "Maximum of 22 Characters allowed for Bank Account")
    @OptionalPattern(regex = Constants.ALPHANUMERIC, message = "Special Characters are not allowed in Bank Account")
    private String bankAccount;

    @Length(max = 10)
    @OptionalPattern(regex = Constants.MOBILE_NUM, message = "Please enter valid mobile number")
    private String mobileNumber;

    @Length(max = 21, message = "Maximum of 21 Characters allowed for Registration No")
    @OptionalPattern(regex = FinancialConstants.alphaNumericwithspecialchar, message = "Special Characters are not allowed in Registration No")
    private String registrationNumber;

    @Length(max = 24, message = "Maximum of 20 Characters allowed for EPF No")
    @OptionalPattern(regex = FinancialConstants.alphaNumericwithoutspecialchar, message = "Special Characters are not allowed in EPF No")
    private String epfNumber;

    @Length(max = 21, message = "Maximum of 17 Characters allowed for ESI No")
    @OptionalPattern(regex = FinancialConstants.numericwithoutspecialchar, message = "Special Characters are not allowed in ESI No")
    private String esiNumber;

    
    @Length(max = 250, message = "Maximum of 250 Characters allowed for GST Registered State")
    private String gstRegisteredState;

    @ManyToOne
    @JoinColumn(name = "status")
    private EgwStatus status;

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getCorrespondenceAddress() {
        return correspondenceAddress;
    }

    public void setCorrespondenceAddress(final String correspondenceAddress) {
        this.correspondenceAddress = correspondenceAddress;
    }

    public String getPaymentAddress() {
        return paymentAddress;
    }

    public void setPaymentAddress(final String paymentAddress) {
        this.paymentAddress = paymentAddress;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(final String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(final String narration) {
        this.narration = narration;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(final String panNumber) {
        this.panNumber = panNumber;
    }

    public String getTinNumber() {
        return tinNumber;
    }

    public void setTinNumber(final String tinNumber) {
        this.tinNumber = tinNumber;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(final Bank bank) {
        this.bank = bank;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(final String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(final String bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Override
    public String getBankaccount() {

        return bankAccount;
    }

    @Override
    public String getBankname() {

        if (bank == null)
            return "";
        else
            return bank.getName();
    }

    @Override
    public String getIfsccode() {

        return ifscCode;
    }

    @Override
    public String getPanno() {

        return panNumber;
    }

    @Override
    public String getTinno() {

        return tinNumber;
    }

    @Override
    public String getModeofpay() {

        return null;
    }

    @Override
    public Integer getEntityId() {
        return Integer.valueOf(id.intValue());
    }

    @Override
    public String getEntityDescription() {

        return getName();
    }

    @Override
    public EgwStatus getEgwStatus() {

        return status;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public EgwStatus getStatus() {
        return status;
    }

    public void setStatus(EgwStatus status) {
        this.status = status;
    }

    public String getEpfNumber() {
        return epfNumber;
    }

    public void setEpfNumber(String epfNumber) {
        this.epfNumber = epfNumber;
    }

    public String getEsiNumber() {
        return esiNumber;
    }

    public void setEsiNumber(String esiNumber) {
        this.esiNumber = esiNumber;
    }

    public String getGstRegisteredState() {
        return gstRegisteredState;
    }

    public void setGstRegisteredState(String gstRegisteredState) {
        this.gstRegisteredState = gstRegisteredState;
    }

}

