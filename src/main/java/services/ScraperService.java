package services;

import config.Config;

import java.io.IOException;

public interface ScraperService {

    /**
     * Gather information from a http response and return a JSON String
     *
     * @param config
     * @return JSON String
     * @throws IOException
     */
    String scrape(final Config config) throws IOException;
}
