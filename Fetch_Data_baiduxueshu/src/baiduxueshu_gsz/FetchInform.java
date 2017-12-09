package baiduxueshu_gsz;

import java.util.ArrayList;
import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.GetDocument;
import utils.ReadConfig;
import utils.SQLHelper;

public class FetchInform {
	private static ArrayList<HashMap<String, Object>> rows; // 用来封装从数据库取到的信息

	private static String tableName = ReadConfig.tablename; // 读取配置文件中的表名
	private static String key = ReadConfig.key;//读取配置文件中的字典表名
	private static String year = ReadConfig.year_;//读取配置文件中的年份

	
	
	private static synchronized HashMap<String, Object> getRow() {
		if (rows == null || rows.size() <= 0) {
			String str = "";
			str = "select top 100 iid,word from "+key+"  where mark<"
					+ ReadConfig.mark1  + " " + ReadConfig.orderBy ;// +1代表容许的出错次数
																		// 出错一般为链接超时，抓取代码选择器为空
				//	+ " limit 1000"; // 每次取出1000个没有补充详细的链接
			rows = SQLHelper.selectBySQL(str);
			if (rows.size() <= 0) {
				System.out.println("==========未取到链接=======");
				System.exit(0);
			}
		}
		HashMap<String, Object> row = rows.get(0);
		rows.remove(0);
		return row;
	}

	public static void main(String[] args) {
		for (int i = 0; i < ReadConfig.thread; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (;;) {
						Fetch();
					}
				}
			}).start();
		}
	}

	public static void Fetch() {
		HashMap<String, Object> row = getRow();
		//String url = row.get("pageurl").toString();
		int id = Integer.parseInt(row.get("iid").toString());
		String word="";
		word=row.get("word").toString();
		//word=word.substring(6, word.length()-1).replace(" ", "+");
		System.out.println(word);
		
		try {
//			String url_T="http://xueshu.baidu.com/s?wd="+word+"&tn=SE_baiduxueshu_c1gjeupa&sc_f_para=sc_tasktype%3D%7BfirstSimpleSearch%7D&sc_hit=1&bcp=2&ie=utf-8&filter=sc_year%3D%7B2016%2C%2B%7D";
			String url_T="http://xueshu.baidu.com/s?wd="+word+"&ie=utf-8&tn=SE_baiduxueshulib_9r82kicg&filter=sc_year%3D%7B"+year+"%2C"+year+"%7D";
			
			baiduxueshu_gsz.Fetchlnform_utils.fetch(url_T, tableName, null);
			String str = "Update "+key+" set mark="+ ReadConfig.mark2 +" where iid="
					+ id;
			SQLHelper.updateBySQL(str);
			
		} catch (Exception e) {
			String str = "Update "+key+" set mark=mark+1 where iid="
					+ id; // 出现异常将标记+1
			SQLHelper.updateBySQL(str);
			e.printStackTrace();
		}
	}
}
