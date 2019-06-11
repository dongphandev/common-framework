package com.tmoncorp.crawler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.tmoncorp.crawler.exception.CommonException;
import com.tmoncorp.crawler.exception.ExceptionCode;
import com.tmoncorp.crawler.imagemanagement.ImageCache;
import com.tmoncorp.crawler.utils.HttpRequestProcessor;
import com.tmoncorp.crawler.utils.RequestApiUtils;

@Service("tmonAdultOptionDealCrawler")
public class TmonAdultDealOptionCrawler implements CrawlerHandler {
	private static Logger LOG = LoggerFactory.getLogger(TmonAdultDealCrawlerImpl.class);

	private final String dealInfo = "http://dealinfoapi.tmon.co.kr/api/deals/list?dealNos={{dealList}}";

	private final String optionNoListApi = "http://dealinfoapi.tmon.co.kr/api/deals/{dealNo}/options/list";

	private final String optionContentList = "http://dealinfoapi.tmon.co.kr/api/deals/{dealNo}/options/list?optionNoList={optionNoList}";

	private ExecutorService executorContext;

	@Autowired
	private ImageCache imageCache;

	public TmonAdultDealOptionCrawler() {
		this.executorContext = Executors.newFixedThreadPool(100);
	}

	@Override
	public void process(List<Long> dealIds, String folder) {
		List<String> batchOfOption = new ArrayList<String>();
		Set<Long> ids = new HashSet<>();
		LOG.info(String.format("**********Start process total deallist size %d ************", dealIds.size()));
		for (Long dealId : dealIds) {
			try {
				String urlOptionNoListApi = StringUtils.replace(optionNoListApi, "{dealNo}", dealId.toString());
				JsonObject jsonObject = (JsonObject) RequestApiUtils.getHttpRequest()
						.get(urlOptionNoListApi, HttpMethod.GET, false, HttpRequestProcessor.ResponseType.JSON)
						.getContent();
				JsonArray jsonOptionsArray = jsonObject.getJsonArray("data");
				for (int i = 0; i < jsonOptionsArray.size(); i++) {
					ids.add(jsonOptionsArray.getJsonNumber(i).longValue());
					if (ids.size() == 30 || ((i + 1) == jsonOptionsArray.size())) {
						String batch = StringUtils.join(ids, ",");
						batchOfOption.add(batch);
						ids.clear();
					}
				}
				try {
					CountDownLatch latch = new CountDownLatch(batchOfOption.size());
					batchOfOption.forEach(it -> {
						executorContext.execute(new AdultOptionWorker(it, dealId.toString(), folder, latch));
					});
					latch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (Exception ex) {
				LOG.error(String.format("**********Error in process deallist size %d************", dealId), ex);
			} finally {
				batchOfOption.clear();
				ids.clear();
			}
		}
		LOG.info(String.format("**********End process total deal list &d************", dealIds.size()));
	}

	private void saveImage(String url, String dealId, String folderPath) {
		InputStream is = null;
		OutputStream os = null;
		ByteArrayOutputStream baos = null;
		try {
			URL imageUrl = new URL(url);
			is = imageUrl.openStream();
			byte[] b = new byte[4096];
			int length;
			baos = new ByteArrayOutputStream();
			while ((length = is.read(b)) != -1) {
				baos.write(b, 0, length);
			}
			HashFunction hash = Hashing.sha256();
			long imageCode = hash.hashBytes(baos.toByteArray()).asLong();
			if (imageCache.putIfAbsent(imageCode)) {
				String filePath = folderPath + File.separator + dealId
						+ url.substring(url.lastIndexOf("."), url.length());
				os = new FileOutputStream(filePath);
				// os = new FileOutputStream("D:\\NSFW_March_2019\\TmonAdultDealOption\\19\\" +
				// dealId + url.substring(url.lastIndexOf("."), url.length()));
				// os = new FileOutputStream("D:\\Dong\\02\\" + dealId +
				// url.substring(url.lastIndexOf("."), url.length()));
				baos.writeTo(os);
			}
		} catch (IOException e) {
			throw new CommonException(ExceptionCode.CAN_NOT_SAVE_IMAGE, e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

	class AdultOptionWorker implements Runnable {
		private CountDownLatch latch;
		private String batchOption;
		private String dealId;
		private String folder;

		public AdultOptionWorker(String batchOption, String dealId, String folder, CountDownLatch latch) {
			this.batchOption = batchOption;
			this.latch = latch;
			this.dealId = dealId;
			this.folder = folder;

		}

		@Override
		public void run() {
			try {
				String urlOptionApi = StringUtils.replace(optionContentList, "{dealNo}", dealId.toString());
				urlOptionApi = StringUtils.replace(urlOptionApi, "{optionNoList}", batchOption);
				JsonObject jsonOptions = (JsonObject) RequestApiUtils.getHttpRequest()
						.get(urlOptionApi, HttpMethod.POST, false, HttpRequestProcessor.ResponseType.JSON).getContent();
				JsonArray options = jsonOptions.getJsonArray("data");
				for (int i = 0; i < options.size(); i++) {
					JsonObject optionObject = options.getJsonObject(i);
					String optionId = optionObject.getJsonNumber("optionNo").toString();
					try {
						JsonArray optionImages = optionObject.getJsonArray("optionImages");
						for (int j = 0; j < optionImages.size(); j++) {
							JsonObject optionImage = optionImages.getJsonObject(j);
							if ("FRONT".equalsIgnoreCase(optionImage.getString("optionImageType"))) {
								String image = optionImage.getString("optionImageUrl");
								saveImage(image, dealId + "_" + optionId, this.folder);
							}
						}
					} catch (Exception ex) {
						LOG.error(
								String.format("**********Error in process storing optionId %s of dealId %s************",
										optionId, dealId),
								ex);
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
