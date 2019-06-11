package com.tmoncorp.crawler.filemanagement;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmoncorp.crawler.TmonAdultCrawler;

@Service
public class FileUtile {
	
	
	public FileUtile() {
		this.executorContext = Executors.newFixedThreadPool(20);
	}
	
	private static Logger LOG = LoggerFactory.getLogger(TmonAdultCrawler.class);

	private ExecutorService executorContext;

	@Autowired
	private FileHandler fileHandler;

	class Worker implements Runnable {
		private CountDownLatch latch;
		private FileHandler fileHandler;
		private List<Long> dealList;
		private String folder;
		private String fileName;

		public Worker(FileHandler fileHandler, List<Long> dealList, CountDownLatch latch,
				String folder, String fileName) {
			this.fileHandler = fileHandler;
			this.dealList = dealList;
			this.latch = latch;
			this.folder = folder;
			this.fileName = fileName;
		}

		@Override
		public void run() {
			try {
				LOG.info(String.format("Start write to file %s with total size %s ",fileName, dealList.size()));
				fileHandler.writeArrayOfLongToFile(dealList,folder + File.separator + this.fileName+ ".csv");
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
