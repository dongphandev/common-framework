package com.tmoncorp.crawler.filemanagement;

import java.util.List;
import java.util.Set;

/**
 * Created by danhnguyen on 4/29/19.
 */
public interface FileHandler {
	
    Set<String> readByLine(String path);
    
    void writeArrayOfLongToFile(List<Long> lst, String fileName);
    
    void writeArrayOfStringToFile(List<String> lst, String fileName);
    
    void saveImage(String url, String dealId, String folderPath) ;
    
    void saveContent(String category, String title, String imageTitle, String content,  String folder, String dealId);
}
