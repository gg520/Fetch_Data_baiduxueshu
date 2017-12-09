package baiduxueshu_gsz;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import utils.GetDocument;
import utils.SQLHelper;
import utils.TimeUtils;

public class jsonTest_baiduxueshu {

	public static void main(String[] args) {

		String url_Head="http://xueshu.baidu.com/usercenter/data/schinfo?url=http%3A%2F%2Fwww.freepatentsonline.com%2Fy2009%2F0117371.html&sign=1ff4ceec22a5361df0e014acaed304cf";
		String data = GetDocument.jsonIP(url_Head);
		data="["+data+"]";
		JSONObject jsonobject=null;
		JSONArray jsonArray=JSONArray.fromObject(data);
		JSONObject j1=jsonArray.getJSONObject(0);
		String status=j1.get("status").toString();
		String url=url_Head;
		String title="";
		String year="2015";
		String tableName="hah";
		String newTime=TimeUtils.getTime();
		Object[] parpm = {url,title,newTime,status,year,tableName};
		String sql="insert into baiduxueshuLog.dbo.statusErrorLog (url,title,newTime,status,year,tableName) values (?,?,?,?,?,?)";
		SQLHelper.insertBySQL(sql,parpm);
	}
}
