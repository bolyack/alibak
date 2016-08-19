package com.bamboo.alibak.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * 利用httpclient下载文件
 */
public class DownloadUtils {

	public static final int cache = 10 * 1024 * 1024;
	
	/**
	 * 根据url下载文件，保存到filepath中
	 * @param url
	 * @param filepath
	 * @return
	 */
	public static String download(String url, String filepath) {
		CloseableHttpClient httpclient = null;
		try {
			httpclient = HttpClients.createDefault();  
			HttpGet httpget = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.custom()  
			        .setConnectTimeout(5000).setConnectionRequestTimeout(1000)  
			        .setSocketTimeout(5000).build();  
			httpget.setConfig(requestConfig);
			HttpResponse response = httpclient.execute(httpget);

			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			File file = new File(filepath);
			file.getParentFile().mkdirs();
			FileOutputStream fileout = new FileOutputStream(file);
			/**
			 * 根据实际运行效果 设置缓冲区大小
			 */
			byte[] buffer=new byte[cache];
			int ch = 0;
			while ((ch = is.read(buffer)) != -1) {
				fileout.write(buffer,0,ch);
			}
			is.close();
			fileout.flush();
			fileout.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != httpclient) {
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
}
