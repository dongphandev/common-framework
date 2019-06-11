package com.tmoncorp.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmoncorp.crawler.imagemanagement.ImageCache;
import com.tmoncorp.crawler.utils.Utilities;

/**
 * Created by danhnguyen on 5/8/19.
 */

@Service
public class TmonAdultCrawler {

	private static Logger LOG = LoggerFactory.getLogger(TmonAdultCrawler.class);

	private ExecutorService executorContext;

	@Autowired
	private CrawlerHandler tmonAdultOptionDealCrawler;

	@Autowired
	private ImageCache imageCache;

	public TmonAdultCrawler() {
		this.executorContext = Executors.newFixedThreadPool(20);
	}

	public void crawl(String rootFolder) {

		if (rootFolder == null || rootFolder.length() == 0) {
			LOG.warn("Please specific folder contains deal id file");
			return;
		}

		LOG.info("**********Start read all existing files for caching ************");
		File folder = new File(rootFolder);
		File[] files = folder.listFiles();
		if (files == null || files.length == 0) {
			LOG.warn("Please specific folder contains deal id file");
			return;
		}
		LOG.info("******start caching image*******");
		imageCache.cacheAllIDealmages(rootFolder);
		LOG.warn("*****End caching image********");
		List<File> srlFiles = new ArrayList<>();
		int currentFolderNumber = 0;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isFile() && file.getName().contains(".csv")) {
				srlFiles.add(file);
			} else {
				try {
					int folderNumber = Integer.parseInt(file.getName());
					if (folderNumber > currentFolderNumber) {
						currentFolderNumber = folderNumber;
					}
				} catch (Exception ex) {
					LOG.warn("Ignore log directory",ex);
				}
			}
		}
		LOG.info("**********Start read srl files and collect image ************");
		for (File file : srlFiles) {
			List<Long> dealIds = loadDealJobList(file.getPath());
			List<List<Long>> items = Utilities.splitList(dealIds, 10000);
			
			String folderToStoreData = rootFolder + File.separatorChar + Integer.toString(++currentFolderNumber);
			File newFolder = new File(folderToStoreData);
			newFolder.mkdirs();
			try {
				CountDownLatch latch = new CountDownLatch(items.size());
				items.forEach(it -> {
					executorContext.execute(new ImageCollectorWorker(tmonAdultOptionDealCrawler, it, folderToStoreData, latch));
				});
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		
		LOG.info("**********End read srl files and collect image************");
	}

	private List<Long> loadDealJobList(String file) {
		List<Long> result = new ArrayList<Long>();
		BufferedReader br = null;
		String line = "";
		try {

			br = new BufferedReader(new FileReader(file));
			line = br.readLine();
			while ((line = br.readLine()) != null) {
				result.add(Long.parseLong(line.trim()));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	class ImageCollectorWorker implements Runnable {
		private CountDownLatch latch;
		private CrawlerHandler tmonAdultHandler;
		private List<Long> dealList;
		private String folder;

		public ImageCollectorWorker(CrawlerHandler tmonAdultHandler, List<Long> dealList,String folder, CountDownLatch latch) {
			this.tmonAdultHandler = tmonAdultHandler;
			this.dealList = dealList;
			this.latch = latch;
			this.folder = folder;
		}

		@Override
		public void run() {
			try {
				tmonAdultHandler.process(dealList, folder);
			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
				if (latch != null) {
					latch.countDown();
				}
			}
		}
	}

}
