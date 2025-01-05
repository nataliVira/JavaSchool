package sbp.school.kafka.dto;

import sbp.school.kafka.enums.OperationTypeEnum;

public class Transaction {

    OperationTypeEnum operationType;
    Double sum;
    String account;
    String date;


    @Override
    public String toString() {
        return "Transaction{" +
                "operationType=" + operationType +
                ", sum=" + sum +
                ", account='" + account + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public OperationTypeEnum getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationTypeEnum operationType) {
        this.operationType = operationType;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
