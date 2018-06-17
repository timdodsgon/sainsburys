package services;

import config.Config;
import models.Product;

import java.io.IOException;
import java.util.List;

public interface ScraperService {

    /**
     * Gather information from a http response and return a list of products
     *
     * @param config
     * @return List of products
     * @throws IOException
     */
    List<Product> scrape(final Config config) throws IOException;
}
