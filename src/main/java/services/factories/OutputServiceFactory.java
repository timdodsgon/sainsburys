package services.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.OutputService;
import services.output.JSONOutput;
import services.output.XMLOutput;

/**
 * Factory to return appropriate output type
 */
public class OutputServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputServiceFactory.class);

    private OutputServiceFactory() {
    }

    public static OutputService getOutputService(String outputType) {
        if ("JSON".equals(outputType)) {
            LOGGER.info("Outputting JSON");
            return new JSONOutput();
        }
        if ("XML".equals(outputType)) {
            LOGGER.info("Outputting XML");
            return new XMLOutput();
        }
        LOGGER.error("Sorry no output types for given request {}", outputType);
        return null;
    }
}
