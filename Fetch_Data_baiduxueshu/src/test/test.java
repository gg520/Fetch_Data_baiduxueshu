package test;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import utils.GetDocument;

public class test {
	public static void main(String[] args) throws Exception {
		
		String url_Head="http://xueshu.baidu.com/usercenter/data/schinfo?url=http%3A%2F%2Fwww.freepatentsonline.com%2FEP0102938.html&sign=3a80a46f5fdb0a74c52d08ae3f013392";
		
/*//		String data=GetDocument.connect_sleep(url_Head).select("body").text();
		System.out.println(GetDocument.connect_sleep_IP(url_Head));
		String data=GetDocument.connect_sleep_IP(url_Head).select("body").text();
		System.err.println(data);
		if(data.isEmpty()||data.equals(""))
		{
			System.err.println("未找到数据！！");
			
		}
		*/
		HttpClient httpClient=new DefaultHttpClient();
		
		/*Map<String, String> cookiesMap=new HashMap<>();
		cookiesMap.put("Cookie", "CNZZDATA726388=cnzz_eid%3D635198617-1492321225-%26ntime%3D1492321225; __cfduid=dc6191111c4d6afecb3ff541ed56bc5b51492825026; SC_PKU=1; BDUSS=lGOXp6ZHAtMGsyMWVXSn5MOEUycGhyMmJJVUQ3MHB4SDljMlhQbzVXWGgtanhaSVFBQUFBJCQAAAAAAAAAAAEAAACdWeJ1xaTGqLnJzOHEqrbTs6QAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOFtFVnhbRVZOF; Hm_lvt_4e02f5bd10b78e0d840a125896a7d0f2=1496982729; neverShow=no; Hm_lvt_f28578486a5410f35e6fbd0da5361e5f=1501132210,1502161886,1502433843,1502446560; PSTM=1503897718; BIDUPSID=A7BD8C518329020850A529C8B5CA114C; BAIDUID=8CC362CE4B2E21334F42775C8476CD29:FG=1; BCLID=9100436765127682676; BDSFRCVID=cEusJeC62wbVAKJZ6jPZt3OvRHtv4knTH6aIBAcmr4C22yWsa-DUEG0Pqf8g0Ku-dAbHogKKBeOTHn3P; H_BDCLCKID_SF=fRKJoDD-JIvDj6rmbtOhq4tHeUb4-RJZ5mAqot3tQqjvOxFCMl3A55tg5R57-nKJ2KonaIQqWloNHn64jM-KDRFiKULL0pQ43bRTQRLy5KJvfj6gLt51hP-UyP5-Wh37aJ7TVJO-KKCMMILRjU5; pgv_pvi=8050164736; pgv_si=s1345654784; BDRCVFR[sRqEqgcBEzc]=mk3SLVN4HKm; BD_CK_SAM=1; BDSVRTM=196; PSINO=7; H_PS_PSSID=1434_21116_17001_20882_20927; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598");
		Map<String, String> headersMap=new HashMap<>();
		headersMap.put("Host", "xueshu.baidu.com");
		headersMap.put("Accept-Encoding", "gzip, deflate");
		//headersMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,/;q=0.8");
		headersMap.put("Upgrade-Insecure-Requests", "1");
		headersMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headersMap.put("Cache-Control", "max-age=0");
		headersMap.put("Connection", "keep-alive");
		Response response= Jsoup
				.connect(url_Head)
				//.cookie("BAIDUID", "D34B4B0B0486604368FEB5D80B8EC169:FG=1")
				.headers(headersMap)
				.userAgent(
						"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36").execute();
		System.out.println(response.statusMessage());
		Document document1 = Jsoup
				.connect(url_Head)
				//.cookie("BAIDUID", "D34B4B0B0486604368FEB5D80B8EC169:FG=1")
				.headers(headersMap)
				.userAgent(
						"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")
				.ignoreContentType(true).timeout(30000)
				.get();
		System.out.println(document1);*/
	}

}
