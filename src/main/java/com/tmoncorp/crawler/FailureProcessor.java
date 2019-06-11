package com.tmoncorp.crawler;

import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmoncorp.crawler.filemanagement.FileHandler;

@Service
public class FailureProcessor implements InitializingBean{

	private static final String fileName = "deal-failure.csv";

	@Autowired
	private FileHandler fileHandler;

	private LinkedBlockingQueue<FailureInfo> queue = new LinkedBlockingQueue<FailureInfo>();

	public void pushFailureDeal(Set<String> dealIds, String folder) {
		FailureInfo info = new FailureInfo(folder, dealIds);
		queue.add(info);
	}

	public void store() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					FailureInfo info = null;
					while ((info = queue.poll()) != null) {
						//fileHandler.writeArrayOfStringToFile(lst, fileName);(info.dealIds, info.folder + File.separator + fileName);
					}

				}
			}
		}).start();
		
	}

	public class FailureInfo {

		public FailureInfo(String folder, Set<String> dealIds) {
			this.folder = folder;
			this.dealIds = dealIds;
		}

		public String folder;

		public Set<String> dealIds;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		store();
	}
}
