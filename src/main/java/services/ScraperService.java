package services;

import java.io.IOException;

public interface ScraperService {

    /**
     * Gather information from a web resource and return a JSON String
     *
     * @param domain
     * @param path
     * @return JSON String
     * @throws IOException
     */
    String scrape(final String domain, final String path) throws IOException;
}
