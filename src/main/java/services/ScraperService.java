package services;

import java.io.IOException;

public interface ScraperService {

    /**
     * Gather information from a web resource and return a JSON String
     *
     * @param url
     * @param baseURL
     * @return JSON String
     * @throws IOException
     */
    String scrape(final String url, final String baseURL) throws IOException;
}
