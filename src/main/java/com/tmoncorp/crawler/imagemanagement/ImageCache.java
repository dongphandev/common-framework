package com.tmoncorp.crawler.imagemanagement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

@Service("imageCache")
public class ImageCache {

	private ExecutorService executorContext;

	private BloomFilter<Long> imageCache;

	private Set<Long> simpleImageCache;

	private Map<String, Long> dealImageCache;

	public ImageCache() {
		simpleImageCache = new HashSet<>();
		dealImageCache = new HashMap<>();
		this.executorContext = Executors.newFixedThreadPool(20);
	}

	public boolean putIfAbsent(Long image) {
		boolean isPutSuccess = false;
		synchronized (simpleImageCache) {
			isPutSuccess = simpleImageCache.add(image);
		}
		return isPutSuccess;
	}

	public void cacheAllImages(String absolutePathDirectory) {
		Stack<File> folders = new Stack<>();
		Set<File> absolutePathFolders = new HashSet<>();
		File firstFile = new File(absolutePathDirectory);
		folders.push(firstFile);
		absolutePathFolders.add(firstFile);
		do {
			File folder = folders.pop();
			File[] files = folder.listFiles();
			for (File file : files) {
				if (file.isDirectory() && (file.list() != null)) {
					folders.push(file);
					absolutePathFolders.add(file);
				}
			}
		} while (!folders.isEmpty());

		CountDownLatch latch = new CountDownLatch(absolutePathFolders.size());
		absolutePathFolders.forEach(it -> {
			executorContext.execute(new ImageReaderWorker(it, latch));
		});
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// LOG.info("**********Finished read all existing files for caching");
	}

	public void cacheAllIDealmages(String absolutePathDirectory) {
		Stack<File> folders = new Stack<>();
		Set<File> absolutePathFolders = new HashSet<>();
		File firstFile = new File(absolutePathDirectory);
		folders.push(firstFile);
		absolutePathFolders.add(firstFile);
		do {
			File folder = folders.pop();
			File[] files = folder.listFiles();
			for (File file : files) {
				if (file.isDirectory() && (file.list() != null)) {
					folders.push(file);
					absolutePathFolders.add(file);
				}
			}
		} while (!folders.isEmpty());

		CountDownLatch latch = new CountDownLatch(absolutePathFolders.size());
		absolutePathFolders.forEach(it -> {
			executorContext.execute(new DealImageReaderWorker(it, latch));
		});
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// LOG.info("**********Finished read all existing files for caching");
	}

	class ImageReaderWorker implements Runnable {
		private File imageFolder;
		private CountDownLatch latch;

		public ImageReaderWorker(File folder, CountDownLatch latch) {
			this.imageFolder = folder;
			this.latch = latch;
		}

		@Override
		public void run() {
			try {
				File[] allFiles = imageFolder.listFiles();
				if (allFiles == null || allFiles.length == 0) {
					return;
				}
				for (File file : allFiles) {
					if (file.isFile()) {
						try {
							HashFunction hash = Hashing.sha256();
							putIfAbsent(hash.hashBytes(Files.readAllBytes(file.toPath())).asLong());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();

			} finally {
				if (latch != null) {
					latch.countDown();
				}
			}
		}
	}

	class DealImageReaderWorker implements Runnable {
		private File imageFolder;
		private CountDownLatch latch;

		public DealImageReaderWorker(File folder, CountDownLatch latch) {
			this.imageFolder = folder;
			this.latch = latch;
		}

		@Override
		public void run() {
			try {
				File[] allFiles = imageFolder.listFiles();
				HashFunction hash = Hashing.sha256();
				for (File file : allFiles) {
					if (file.isFile()) {
						try {
							String fileName = file.getName();
							String dealOption = fileName.substring(0, fileName.indexOf("."));
							long newImageHashed = hash.hashBytes(Files.readAllBytes(file.toPath())).asLong();
							synchronized (dealImageCache) {
								Long exitingImageHashed = dealImageCache.get(dealOption);
								if (exitingImageHashed == null || (exitingImageHashed != null && exitingImageHashed != newImageHashed)) {
									dealImageCache.put(dealOption, newImageHashed);
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
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
