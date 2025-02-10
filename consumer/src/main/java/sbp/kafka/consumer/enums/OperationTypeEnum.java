package sbp.kafka.consumer.enums;

import java.util.HashMap;
import java.util.Map;

public enum OperationTypeEnum {
    WITHDRAWAL("Снятие средств"),
    ENROLLMENT("Зачисление средств"),
    TRANSFER("Перевод средств");

    private final String label;



    OperationTypeEnum(String label) {
        this.label = label;
    }

    private static Map<String, OperationTypeEnum> dictionary = new HashMap<>();

    static {
        for (OperationTypeEnum typeEnum : OperationTypeEnum.values()) {
            dictionary.put(typeEnum.label, typeEnum);
        }
    }

    public static OperationTypeEnum getByLabel(String label) {
        return dictionary.get(label);
    }

    public String getLabel() {
        return label;
    }
}
