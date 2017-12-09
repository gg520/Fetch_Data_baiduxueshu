package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import utils.ProxyHost.IP;

public class GetDocument {
	public static int sleep=ReadConfig.sleep;
	public static boolean isSleep=ReadConfig.isSleep;
	private static Boolean isProxy=ReadConfig.isProxy;
	private static String proxyIp=ReadConfig.proxyIp; //代理ip
	private static int proxyPort=ReadConfig.proxyPort; //代理端口
	
	
	public static Document changeIp(String url,String Encoading){
		Document document1 = null;
		int ii = 1;
		while (ii <= 10) { // 请求超时多次请求
			try {
				document1 = changeIp1(url, Encoading);
				return document1;
			} catch (Exception e1) {
				try {
					if(sleep>0&&isSleep){
						Thread.sleep(sleep*1000);
//						System.out.println("第"+ii+"次访问该网站失败，等待 "+sleep+" 秒");
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				ii++;
			}
	}
		return null;
	}
	
	public static Document changeIp1(String url,String Encoading) {
		StringBuilder sBuilder = null;
		InputStream inputStream = null;
		IP ip = null;
		Document document = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			if (ReadConfig.isProxy) {
				HttpHost proxy = null;
				if (ReadConfig.isuseGoAgent) {
					proxy = new HttpHost(ReadConfig.proxyIp,
							ReadConfig.proxyPort);
				} else {
					ip = ProxyHost.getIp(true);
					proxy = new HttpHost(ip.ip, ip.port);
				}
				httpClient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
			httpClient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 60 * 1000);// 
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 60 * 1000);// 
			httpClient.getParams().setParameter(
					ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
			//链接网址
			HttpGet httpGet = new HttpGet(url.trim());
			httpGet.setHeader("User-Agent", UserAgent.getUserAgent());
			httpGet.setHeader("Cookie",ReadConfig.cookie);
			
            
			
         HttpResponse execute = httpClient.execute(httpGet);
			System.err.println(execute.getStatusLine());
			if (execute.getStatusLine().getStatusCode() != 200) {
				if (ip != null) {
					int code = execute.getStatusLine().getStatusCode();
					System.out.println(ip.ip + ":" + ip.port + "---" + code
							+ "--");
					ProxyHost.removeIp(ip);
				}
				return null;
			}
			BufferedReader bReader = null;
			sBuilder = new StringBuilder();
			HttpEntity entity = execute.getEntity();
			inputStream = entity.getContent();
			/*if(Encoading.toUpperCase().contains("UTF-8")){
				bReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
				for (String line = bReader.readLine(); line != null; line = bReader
						.readLine()) {
					sBuilder.append(line).append("\r\n");
				}
			}else{
				bReader = new BufferedReader(new InputStreamReader(inputStream,
						"gbk"));
				for (String line = new String(bReader.readLine().getBytes("gbk"),"utf-8"); line != null; line = bReader.readLine()) {
					sBuilder.append(line).append("\r\n");
				}
			}*/
			if (sBuilder != null) {
				document = Jsoup.parse(sBuilder.toString());
			}
		} catch (Exception ex) {
			if (ip != null) {
				ProxyHost.removeIp(ip);
			}
			ex.printStackTrace();

		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return document;
	}
	public static Document connect(String url){
		Document document1 = null;
		int ii = 1;
		while (ii <= 10) { // 请求超时多次请求
			try {
				 
				document1 = Jsoup
						.connect(url)
						.userAgent(
								"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0")
						.ignoreContentType(true).timeout(30000)
						.get();
				return document1;
			} catch (IOException e1) {
//				try {
//					if(sleep_every>0)
//						Thread.sleep(sleep_every);
//				} catch (Exception e) {
//					
//					e.printStackTrace();
//				}
				ii++;
			}
	}
		return null;
	}
	public static Document connect_sleep(String url){
		Document document1 = null;
		int ii = 1;
		while (ii <= 10) { // 请求超时多次请求
			try {
				document1 = Jsoup
						.connect(url)
						.userAgent(
								"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0")
						.ignoreContentType(true).timeout(30000)
						.get();
				//System.out.println("访问成功");
				return document1;
			} catch (IOException e1) {
				try {
					if(sleep>0&&isSleep){
						Thread.sleep(sleep*1000);
						System.out.println("第"+ii+"次访问该网站失败，等待 "+sleep+" 秒");
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				ii++;
			}
	}
		return null;
	}
	/**
	 * 获取json值
	 * @param url
	 * @return
	 */
	public static String jsonIP(String url){
		HttpClient httpClient = new DefaultHttpClient();
		 HttpGet httpGet = new HttpGet(url);
		 if(isProxy){
			 HttpHost proxy=new HttpHost(proxyIp,proxyPort);
			 System.err.println("代理IP："+proxyIp+"     端口号："+proxyPort);
			 httpClient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
		 }
		 httpClient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 60 * 1000);
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 60 * 1000);
			httpClient.getParams().setParameter(
					ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
			httpGet.setHeader("User-Agent", UserAgent.getUserAgent());
		 HttpResponse execute = null;  
	    try{
	    	execute = httpClient.execute(httpGet);
	        if (execute.getStatusLine().getStatusCode() != 200) {
	        	System.err.println("IP失效！！");
	        	return null;
	        }
	    }catch (Exception e) {
	    	
	    	e.printStackTrace();
	    	return null;
	    }
	    String data=null;
	    try{
	    	System.out.println(execute.getStatusLine());
	        HttpEntity entity = execute.getEntity();
	        data=EntityUtils.toString(entity,"UTF-8");
	        
	        return data;
	    }catch (Exception e) {
	    	
	    	e.printStackTrace();
	    }
		return null;
	}
//	agency
	/**
	 * 代理IP处理 jsonp
	 * @param url
	 * @return
	 */
	public static Document connect_sleep_IP(String url){
		Document document1 = null;
		int ii = 1;
		
		while (ii <= 10) { // 请求超时多次请求
			try {
				if(isProxy){
					//System.out.println("数据");
					System.err.println("代理IP："+proxyIp+"     端口号："+proxyPort);
					document1 = Jsoup
							.connect(url)
							.proxy(proxyIp, proxyPort)//代理ip，手动添加
							.userAgent(
									"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")
							.ignoreContentType(true).timeout(30000)
							.get();
					
				}else{
					System.out.println("未使用代理IP");
					document1 = Jsoup
							.connect(url)
							.userAgent(
									"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0")
							.ignoreContentType(true).timeout(30000)
							.get();
				}
				
				//System.out.println("访问成功");
				return document1;
			} catch (IOException e1) {
				try {
					if(sleep>0&&isSleep){
						Thread.sleep(sleep*1000);
						System.out.println("第"+ii+"次访问该网站失败，等待 "+sleep+" 秒");
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				ii++;
			}
	}
		return null;
	}
}
