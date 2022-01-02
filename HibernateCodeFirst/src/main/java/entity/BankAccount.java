package entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "bank_account")
public class BankAccount extends BillingDetail{
    private String bankName;
    private Integer swiftCode;

    public BankAccount() {

    }

    @Column(name = "bank_name")
    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Column(name = "swiftCode")
    public Integer getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(Integer swiftCode) {
        this.swiftCode = swiftCode;
    }
}
