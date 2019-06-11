package com.tmoncorp.crawler;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.tmoncorp.crawler.exception.CommonException;
import com.tmoncorp.crawler.exception.ExceptionCode;
import com.tmoncorp.crawler.filemanagement.FileHandler;
import com.tmoncorp.crawler.utils.HttpRequestProcessor;
import com.tmoncorp.crawler.utils.RequestApiUtils;
import com.tmoncorp.crawler.utils.Utilities;

/**
 * Created by danhnguyen on 5/8/19.
 */
@Service("tmonAdultDealCrawlerImpl")
public class TmonAdultDealCrawlerImpl implements CrawlerHandler {

	private static Logger LOG = LoggerFactory.getLogger(TmonAdultDealCrawlerImpl.class);

	private final String api = "http://dealinfoapi.tmon.co.kr/api/deals/list?dealNos={{dealList}}";

	private final int BATCH_SIZE = 50;

	@Autowired
	private FileHandler fileHandler;

	@Autowired
	private FailureProcessor failureProcessor;

	@Override
	public void process(List<Long> dealIds, String folder) {
		Set<String> failureDeal = new HashSet<>();
		List<List<Long>> dealList = Utilities.splitList(dealIds, BATCH_SIZE);
		for (List<Long> jobList : dealList) {
			String urlDealApi = StringUtils.replace(api, "{{dealList}}", StringUtils.join(jobList, ","));
			JsonObject jsonObject = (JsonObject) RequestApiUtils.getHttpRequest()
					.get(urlDealApi, HttpMethod.POST, false, HttpRequestProcessor.ResponseType.JSON).getContent();
			JsonArray jsonDealArray = jsonObject.getJsonArray("data");
			for (int i = 0; i < jsonDealArray.size(); i++) {
				JsonObject jsonDeal = jsonDealArray.getJsonObject(i);
				String dealId = Long.toString(jsonDeal.getJsonNumber("dealNo").longValue());
				try {
					String mainImage = jsonDeal.getJsonObject("imageInfo").getString("pc3ColImageUrl");
					String title = jsonDeal.getString("title");
					String category = jsonDeal.getJsonObject("categoryInfo").getString("categoryName");
					// String categoryNo =
					// jsonDeal.getJsonObject("categoryInfo").getString("mainCategoryNo");
					String contentLevel = jsonDeal.getString("contentsLevel");
					String imageTitle = jsonDeal.getString("titleImage");
					if (!StringUtils.isBlank(mainImage)) {
						fileHandler.saveImage(mainImage, dealId, folder);
						fileHandler.saveContent(category, title, imageTitle, contentLevel, folder, dealId);
					}
				} catch (Exception e) {
					failureDeal.add(dealId);
					LOG.error(String.format("*****Can not process deal %s:", dealId), e);
					// TODO: handle exception
				}
			}
		}
		if (failureDeal.size() > 0) {
			failureProcessor.pushFailureDeal(failureDeal, folder);
		}

	}
}
