package sbp.kafka.consumer.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import sbp.kafka.consumer.enums.OperationTypeEnum;
import sbp.kafka.consumer.service.LocalDateTimeDeserializer;
import sbp.kafka.consumer.service.LocalDateTimeSerializer;


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
