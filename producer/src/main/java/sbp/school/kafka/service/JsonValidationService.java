package sbp.school.kafka.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Класс валидации отправляемого сообщения
 * @version 1.0
 */
public class JsonValidationService {

    public JsonValidationService() {
    }

    public boolean validate(String data) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);

        try (
                InputStream schemaStream = new FileInputStream(Thread.currentThread().getContextClassLoader()
                        .getResource("schema.json").getPath());
        ) {
            JsonNode json = objectMapper.readTree(data);
            JsonSchema schema = schemaFactory.getSchema(schemaStream);
            Set<ValidationMessage> validationResult = schema.validate(json);
            if (validationResult.isEmpty()) {
                System.out.println("no validation errors");
                return true;
            } else {
                validationResult.forEach(vm -> System.out.println(vm.getMessage()));
            }
        }
        return false;
    }

}