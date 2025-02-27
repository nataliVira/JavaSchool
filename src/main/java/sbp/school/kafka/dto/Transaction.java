package sbp.school.kafka.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import sbp.school.kafka.enums.OperationTypeEnum;
import sbp.school.kafka.service.LocalDateTimeDeserializer;
import sbp.school.kafka.service.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    OperationTypeEnum operationType;
    BigDecimal sum;
    String account;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime date;


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

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }


}
