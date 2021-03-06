package com.bamboo.alibak.task;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bamboo.alibak.utils.DateUtils;
import com.bamboo.alibak.utils.DownloadUtils;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.rds.model.v20140815.DescribeBinlogFilesRequest;
import com.aliyuncs.rds.model.v20140815.DescribeBinlogFilesResponse;
import com.aliyuncs.rds.model.v20140815.DescribeBinlogFilesResponse.BinLogFile;

/**
 * 备份阿里云数据库上MySQL的binLog文件到本地(其他目录)
 * @author admin
 *
 */
@Component
public class ScheduledBinLogTasks {

	private final static String FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private final static String TIME_ZONE = "GMT";
	
	/**
	 * 用于日志输出的日期格式
	 */
	private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static final Logger loggger = Logger.getLogger(ScheduledBinLogTasks.class);

	/**
	 * 数据库实例id
	 */
	@Value("${db_instanceId}")
	private String dbInstanceId;
	
	/**
	 * 区域id
	 */
	@Value("${region_id}")
	private String regionId;
	
	/**
	 * 数据库实例key_id
	 */
	@Value("${key_id}")
	private String keyId;
	
	/**
	 * 数据库实例key秘钥
	 */
	@Value("${key_secret}")
	private String keySecret;
	
	/**
	 * 文件下载路径
	 */
	@Value("${binLog_downPath}")
	private String downPath;
	
	/**
	 * 
	 */
	@Value("${binLog_Instance}")
	private String binLogInstance;
	
	@Scheduled(cron = "${binLogCronTime}") //每天上午10:15
	public void backBinLogTask() {
		loggger.info("阿里rds数据库binLog文件备份开始...");
		Date startTime = null;
		Date curretTime = new Date();
		try {

			IClientProfile profile = DefaultProfile.getProfile(regionId, keyId, keySecret);
			IAcsClient client = new DefaultAcsClient(profile);
			
			DescribeBinlogFilesRequest req = new DescribeBinlogFilesRequest();
			
			Calendar c = Calendar.getInstance();
			c.setTime(curretTime);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			startTime = c.getTime();
			
			//@see https://help.aliyun.com/document_detail/26273.html?spm=5176.doc26208.6.279.mQl0Wc
			/**
			 * 当前定时任务的凌晨时间
			 */
			req.setActionName("DescribeBinlogFiles");
			req.setStartTime(DateUtils.getISO8601Time(c.getTime(), FORMAT_ISO8601, TIME_ZONE));
			req.setDBInstanceId(dbInstanceId);
			req.setEndTime(DateUtils.getISO8601Time(curretTime, FORMAT_ISO8601, TIME_ZONE));

			/**
			 * 请求响应
			 */
			DescribeBinlogFilesResponse res = client.getAcsResponse(req);
			
			int recordCount = res.getPageRecordCount() == null ? 0 : res.getPageRecordCount().intValue();
			if (null != res &&  recordCount > 0) {
				List<BinLogFile> backs = res.getItems();
				if (null != backs && backs.size() > 0) {
					for (BinLogFile backup : backs) {
						String downloadUrl = backup.getDownloadLink();
						if (null != downloadUrl && !"".equals(downloadUrl) && downloadUrl.contains(binLogInstance)) {
							int startPos = downloadUrl.lastIndexOf("/") + 1;
							int endPos = downloadUrl.indexOf("?");
							String path = downPath + File.separator;
							String fileName = path + downloadUrl.substring(startPos, endPos);
							File file = new File(fileName);
							if (!file.exists()) {
								file.createNewFile();
								DownloadUtils.download(downloadUrl, fileName);
								loggger.info(String.format("文件%s下载成功！", fileName));
							}
						}
					}
				} else {
					loggger.info(String.format("在日期%s到%s之间阿里rds数据库文件备份binLog文件不存在...", DF.format(startTime), DF.format(curretTime)));
				}
			} else {
				loggger.info(String.format("在日期%s到%s之间阿里rds数据库文件备份binLog文件不存在...", DF.format(startTime), DF.format(curretTime)));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			loggger.error("访问ali的数据库备份binLog文件sdk接口异常！", e);
		}
		loggger.info("阿里rds数据库binLog文件备份结束...\n");
	}
	
}
