package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import models.Results;

public interface OutputService {

    /**
     * Output the desired structured data type to the console
     *
     * @param results
     * @return List of products
     */
    String output(final Results results) throws JsonProcessingException;
}
