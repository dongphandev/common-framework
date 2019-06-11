package com.tmoncorp.crawler;

import java.util.List;

/**
 * Created by danhnguyen on 5/8/19.
 */
public interface CrawlerHandler {
    void process(List<Long> dealIds, String folder);
}
