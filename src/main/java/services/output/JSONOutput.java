package services.output;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import models.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.OutputService;

import java.io.IOException;

public class JSONOutput implements OutputService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONOutput.class);

    private static final String INVALID_JSON = "{ \"results\" : \"INVALID\" }";
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Generates a JSON String from a Results object, that has been checked to be valid JSON
     */
    @Override
    public String output(Results results) throws JsonProcessingException {
        LOGGER.info("Generating JSON response");
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json = objectMapper.writeValueAsString(results);
        return isJSONValid(json) ? json : INVALID_JSON;
    }

    /**
     * Test given JSON is valid
     *
     * @param json
     * @return boolean
     */
    boolean isJSONValid(String json) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(json);
            LOGGER.info("JSON is valid");
            return true;
        } catch (IOException e) {
            LOGGER.error("JSON is invalid {}", e);
            return false;
        }
    }

    /**
     * Method to aid in running the test harness
     *
     * @param objectMapper
     */
    void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
