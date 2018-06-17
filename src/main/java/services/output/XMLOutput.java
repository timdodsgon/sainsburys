package services.output;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import models.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import services.OutputService;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;


public class XMLOutput implements OutputService {

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLOutput.class);

    private static final String INVALID_XML = "<Results>INVALID</Results>";
    private XmlMapper xmlMapper = new XmlMapper();

    /**
     * Generates a XML String from a Results object, that has been checked to be valid XML
     */
    @Override
    public String output(Results results) throws JsonProcessingException {
        LOGGER.info("Generating XML response");
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String xml = xmlMapper.writeValueAsString(results);
        return isXMLValid(xml) ? xml : INVALID_XML;
    }

    /**
     * Test given XML is valid
     *
     * @param xml
     * @return boolean
     */
    boolean isXMLValid(String xml) {
        try {
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
            LOGGER.info("XML is valid");
            return true;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.info("XML is invalid {}", e);
            return false;
        }
    }

    /**
     * Method to aid in running the test harness
     *
     * @param xmlMapper
     */
    void setXMLMapper(XmlMapper xmlMapper) {
        this.xmlMapper = xmlMapper;
    }
}
