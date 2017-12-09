package baiduxueshu_gsz;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.GetDocument;
import utils.ReadConfig;
import utils.SQLHelper;
import utils.TimeUtils;

public class Fetchlnform_utils {
	
	public static boolean isEnglishi(String str){
		
		Pattern p=Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m=p.matcher(str);
		if(m.find()){
			return false;
		}
		return true;
	}

	public static String toMD5(String plainText) {
		try {
			//生成实现指定摘要算法的 MessageDigest 对象。
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			//使用指定的字节数组更新摘要。
			md.update(plainText.getBytes());
			//通过执行诸如填充之类的最终操作完成哈希计算。
			byte b[] = md.digest();
			//生成具体的md5密码到buf数组
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
				i += 256;
				if (i < 16)
				buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
			// System.out.println("32位: " + buf.toString());
			// System.out.println("16位: " + buf.toString().substring(8, 24));// 16位的加密，其实就是32位加密后的截取
		} 
		catch (Exception e) {
		System.err.println("md5加密出现异常！！");
		e.printStackTrace();
		}
		return null;
   }
	public static  void fetch(String url_T ,String tablename,Object name) throws Exception{
				
		try{
			System.out.println("表名称："+tablename);
//			Document dd=Jsoup.connect(url_T).userAgent("Mozilla/5.0(Windows NT 6.0;WOW64;rv:35.0)Gecko/20100101 Firefox/35.0").ignoreContentType(true).timeout(30000).get();//
//			Document dd=GetDocument.connect_sleep(url_T);
			Document dd=GetDocument.connect_sleep_IP(url_T);
			
			if(dd!=null){
				
			
			
			int i=0;
			int x1=0;
			
			while(dd.select("#page a").text().trim().contains("下一页>")){
				
					String uu="http://xueshu.baidu.com"+dd.select("#page a").get(0).attr("href").trim();
					String u1=uu.substring(0, uu.indexOf("&pn=")+4);
					String u2=uu.substring(uu.indexOf("&tn=SE_baiduxueshu"));
					System.out.println(u1+i+'0'+u2);
//					dd=GetDocument.connect_sleep(u1+i+'0'+u2);
					dd=GetDocument.connect_sleep_IP(u1+i+'0'+u2);
					i++;
					Elements ss=dd.select(".result");
					for(Element ea:ss){
						try{
							String biaozhi="";
							String other_biaozhi="";
							if(ea.select("h3").html().contains("<i")){
								String biaozhi1=ea.select("h3 i").attr("class").trim();
								
								switch(biaozhi1){
								case "c-icon c-icon-tushu-mark":
									biaozhi="图书";
									break;
								case "c-icon c-icon-zhuanli-mark":
									biaozhi="专利";
									break;
								case "c-icon c-icon-pdf-mark":
									//biaozhi="";
									biaozhi="PDF";
									break;
								default:
									other_biaozhi=other_biaozhi+biaozhi1+";";
									break;
										
								}
							}
							System.out.println("other_biaozhi========="+other_biaozhi);
							
							
							String  url="http://xueshu.baidu.com"+ea.select("h3 a").attr("href").trim();
							System.out.println("url="+url);
							try{
//								Document doc= GetDocument.connect_sleep(url);
								Document doc=GetDocument.connect_sleep_IP(url);
								String laiyuan="";
								if(doc==null){
									System.out.println("网页加载失败");
								}
								String author_show="";
								author_show=doc.select("#dtl_l > div:nth-child(1) > div.c_content.content_hidden > div.author_wr > p.author_text").text().trim();
								System.out.println("author_show===="+author_show);
								String chubanyuan="";
								chubanyuan=doc.select("#dtl_l > div:nth-child(1) > div.c_content.content_hidden > div.publish_wr > p.publish_text").text().trim();
								System.err.println("出版源："+chubanyuan);
								String laiyuan_show="";
								Elements es1=doc.select("#allversion_wr > div > .dl_item_span");
								HashMap<String,String> mm=new HashMap<>();
								for(Element eea:es1){
									if(!eea.select(".more_text").text().trim().contains("查看更多")){
										String uurl=eea.select("a").attr("href").trim();
										if(uurl.contains("http://xueshu.baidu.com/s")){
											uurl=uurl.substring(uurl.indexOf("&sc_vurl=")+9);
										}
										uurl=uurl.replaceAll("%3A", ":");
										uurl=uurl.replaceAll("%2F","/");
										String uu_t="";
										String uurl_titie=eea.text().trim();
//										if(uurl_titie.contains("("))
//											uu_t=uurl_titie.substring(0, uurl_titie.indexOf("("));
										mm.put(uurl_titie, uurl);
										laiyuan_show=laiyuan_show+uurl_titie+":"+uurl+"@@";
									}
								}
								String title=doc.select("#dtl_l > div:nth-child(1) > h3 > a").text().trim();
								System.out.println("title="+title);
								String yanjiu="";
								//System.out.println("size=="+doc.select("#dtl_r > div.dtl_search_word > div > a").size());
								Elements els123=doc.select("#dtl_r > div.dtl_search_word > div > a");
								for(Element e:els123){
									yanjiu=yanjiu+e.select("a").text().trim()+";";
								}
								//yanjiu=doc.select("a").text().trim();
								System.out.println("yanjiu="+yanjiu);
								laiyuan=laiyuan_show;
								String abstract_url1=doc.select("#dtl_l > div:nth-child(1) > h3 > a").attr("data-link").trim();
								String abstract_url2=doc.select("#dtl_l .abstract_wr p[class=abstract]").attr("data-sign").trim();
								String url_Head="http://xueshu.baidu.com/usercenter/data/schinfo?url="+abstract_url1+"&sign="+abstract_url2;
								 
								
							    String data=GetDocument.jsonIP(url_Head);
							    
								
								if(data==null||data.equals("")||data.length()<=0)
								{
									System.err.println("未找到数据！！");
									break;
								}
								
								data="["+data+"]";
								//System.out.println(data);
								JSONObject jsonobject=null;
								JSONArray jsonArray=JSONArray.fromObject(data);
								int iSize=jsonArray.size();
								//System.out.println(iSize);
								String sc_StdStg="";
								String sc_abstract="";
								String sc_all_version="";
								String author="";
								String sc_cited="";
								String sc_citedyear="";
								String sc_codetype="";
								String sc_doctype="";
								String sc_doi="";
								String sc_iscitestyle="";
								String sc_issue="";
								String sc_keyword="";
								String sc_level="";
								String sc_libstate="";
								String sc_longsign="";
								String sc_page_score="";
								String sc_pageno="";
								String sc_parsertype="";
								String sc_pdf_read="";
								String sc_journal="";
								String sc_journal_uri="";
								String sc_publisher="";
								String sc_saveanchor="";
								String sc_savelibsign="";
								String sc_savesize="";
								String sc_saveurl="";
								String sc_vtype="";
								String sc_ref_sum="";
								String sc_all_version_url="";
								String sc_time="";
								String sc_year="";
								String sc_savelink="";
								String sc_savelink_sum="";
								String sc_savetype="";
								String sc_scholarurl="";
								String sc_source="";
								String sc_title="";
								String sc_type="";
								String sc_volumeno="";
								
								
								
								String xiazai="";
								JSONObject j1=jsonArray.getJSONObject(0);
								String status=j1.get("status").toString();
								if(status.equals("0")){
									for(int k=0;k<iSize;k++){
										try {
											JSONObject jj=jsonArray.getJSONObject(k);
											data="["+jj.get("meta_di_info")+"]";
											//System.out.println(""+jj.get("meta_di_info"));
											jsonArray=JSONArray.fromObject(data);
											//System.out.println(jsonArray.size());
											for(int k0=0;k0<jsonArray.size();k0++){
												try {
													JSONObject js=jsonArray.getJSONObject(k0);
													if(js.containsKey("sc_StdStg")){
														sc_StdStg=js.get("sc_StdStg").toString();
														sc_StdStg=sc_StdStg.substring(sc_StdStg.indexOf("\"")+1,sc_StdStg.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_abstract")){
														sc_abstract=js.get("sc_abstract").toString();
														sc_abstract=sc_abstract.substring(sc_abstract.indexOf("\"")+1,sc_abstract.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_all_version")){
														sc_all_version=js.get("sc_all_version").toString();
														sc_all_version=sc_all_version.substring(sc_all_version.indexOf("\"")+1,sc_all_version.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_all_version_url"))
														sc_all_version_url=js.get("sc_all_version_url").toString();
													System.out.println(sc_all_version_url);
													if(!sc_all_version_url.isEmpty()){
														JSONArray jsArray_sc_all_version_url=JSONArray.fromObject(sc_all_version_url);
														for(int k1=0;k1<jsArray_sc_all_version_url.size();k1++){
															try {
																JSONObject js_sc_vanchor=jsArray_sc_all_version_url.getJSONObject(k1);
																/**
																 * 来源解析
																 */
																if(js_sc_vanchor.containsKey("sc_vanchor")&&!js_sc_vanchor.containsKey("sc_vadd")){
																	String sc_vanchor=js_sc_vanchor.get("sc_vanchor").toString();
																	String sc_vurl=js_sc_vanchor.get("sc_vurl").toString();
																	sc_vanchor=sc_vanchor.substring(sc_vanchor.indexOf("\"")+1);
																	sc_vanchor=sc_vanchor.substring(0, sc_vanchor.lastIndexOf("\""));
																	sc_vurl=sc_vurl.substring(sc_vurl.indexOf("\"")+1);
																	sc_vurl=sc_vurl.substring(0, sc_vurl.lastIndexOf("\""));
																	if(mm.get(sc_vanchor)==""||mm.get(sc_vanchor)==null){
																		mm.put(sc_vanchor, sc_vurl);
																		laiyuan=laiyuan+sc_vanchor+"："+sc_vurl+"@@";
																	}
																}
															} catch (Exception e1) {
																e1.printStackTrace();
															}
														}
													}
													if(js.containsKey("sc_savelink"))
														sc_savelink=js.get("sc_savelink").toString();
													System.out.println("sc_savelink="+sc_savelink);
													if(!sc_savelink.isEmpty()){
														JSONArray jsArray_sc_savelink=JSONArray.fromObject(sc_savelink);
														System.out
																.println("长度："+jsArray_sc_savelink.size());
														for(int k1=0;k1<jsArray_sc_savelink.size();k1++){
															try {
																JSONObject js_sc_saveanchor=jsArray_sc_savelink.getJSONObject(k1);
																/**
																 * 下载解析
																 */
																if(js_sc_saveanchor.containsKey("sc_saveanchor")&&!js_sc_saveanchor.containsKey("sc_vadd")){
																	String sc_saveanchor1=js_sc_saveanchor.get("sc_saveanchor").toString();
																	String sc_saveurl1=js_sc_saveanchor.get("sc_saveurl").toString();
																	sc_saveanchor1=sc_saveanchor1.substring(sc_saveanchor1.indexOf("\"")+1);
																	sc_saveanchor1=sc_saveanchor1.substring(0, sc_saveanchor1.lastIndexOf("\""));
																	sc_saveurl1=sc_saveurl1.substring(sc_saveurl1.indexOf("\"")+1);
																	sc_saveurl1=sc_saveurl1.substring(0, sc_saveurl1.lastIndexOf("\""));
																	if(mm.get(sc_saveanchor1+" (全网免费下载)")==""||mm.get(sc_saveanchor1+" (全网免费下载)")==null){
																		mm.put(sc_saveanchor1+" (全网免费下载)", sc_saveurl1);
																		laiyuan=laiyuan+sc_saveanchor1+" (全网免费下载):"+sc_saveurl1+"@@";
																	}
																}
															} catch (Exception e1) {
																e1.printStackTrace();
															}
														}
													}
	//												System.out.println("sc_all_version_url.size=="+jsArray_sc_all_version_url.size());
													if(js.containsKey("sc_author")){
														try {
															
															String sc_author=js.get("sc_author").toString();
															//System.out.println(sc_author);
															if(!sc_author.isEmpty()){
																JSONArray jsArray_sc_author=JSONArray.fromObject(sc_author);
																//System.out.println("sc_all_version_url.size=="+jsArray_sc_author.size());
																
																for(int k1=0;k1<jsArray_sc_author.size();k1++){
																	try {
																		JSONObject js_author=jsArray_sc_author.getJSONObject(k1);
																		/**
																		 * 作者解析
																		 */
																		
																		if(js_author.containsKey("sc_name")){
																			String author1=js_author.get("sc_name").toString();
																			String author_show1=author1.substring(author1.indexOf("\",\"")+3);
																			if(author_show1.contains("\",\""))
																				author_show1=author_show1.substring(0, author_show1.indexOf("\",\""));
																			if(author_show1.contains("\""))
																				author_show1=author_show1.substring(0, author_show1.indexOf("\""));
																			//author_show=author_show1+";"+author_show;
																			author1=author1.substring(author1.indexOf("\"")+1);
																			author1=author1.substring(0, author1.lastIndexOf("\""));
																			author1=author1.replaceAll("\",\"","@@");
																			author=author1+";"+author;
																		}
																		//System.out.println("name--"+k1+"==="+author1);
																	} catch (Exception e1) {
																		// TODO Auto-generated catch block
																		e1.printStackTrace();
																	}
																}
															}
														} catch (Exception e1) {
															// TODO Auto-generated catch block
															e1.printStackTrace();
														}
														
													}
													if(js.containsKey("sc_cited")){
														sc_cited=js.get("sc_cited").toString();
														sc_cited=sc_cited.substring(sc_cited.indexOf("\"")+1,sc_cited.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_citedyear")){
														sc_citedyear=js.get("sc_citedyear").toString();
														sc_citedyear=sc_citedyear.substring(sc_citedyear.indexOf("\"")+1,sc_citedyear.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_codetype")){
														sc_codetype=js.get("sc_codetype").toString();
														sc_codetype=sc_codetype.substring(sc_codetype.indexOf("\"")+1,sc_codetype.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_doctype")){
														sc_doctype=js.get("sc_doctype").toString();
														sc_doctype=sc_doctype.substring(sc_doctype.indexOf("\"")+1,sc_doctype.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_doi")){
														sc_doi=js.get("sc_doi").toString();
														sc_doi=sc_doi.substring(sc_doi.indexOf("\"")+5,sc_doi.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_iscitestyle")){
														sc_iscitestyle=js.get("sc_iscitestyle").toString();
														sc_iscitestyle=sc_iscitestyle.substring(sc_iscitestyle.indexOf("\"")+1,sc_iscitestyle.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_issue")){
														sc_issue=js.get("sc_issue").toString();
														sc_issue=sc_issue.substring(sc_issue.indexOf("\"")+1,sc_issue.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_keyword")){
														sc_keyword=js.get("sc_keyword").toString();
														sc_keyword=sc_keyword.substring(sc_keyword.indexOf("[")+1,sc_keyword.lastIndexOf("]"));
														sc_keyword=sc_keyword.replaceAll("\"", "");
														
													}
													if(js.containsKey("sc_level")){
														sc_level=js.get("sc_level").toString();
														sc_level=sc_level.substring(sc_level.indexOf("\"")+1,sc_level.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_liblink")){//次数待处理
														try {
															String sc_liblink=js.get("sc_liblink").toString();
															//System.out.println("sc_liblink=="+sc_liblink);
															if(!sc_liblink.isEmpty()){
																JSONArray jsArray_sc_liblink=JSONArray.fromObject(sc_liblink);
																//System.out.println("sc_liblink.size=="+jsArray_sc_liblink.size());
																for(int k2=0;k2<jsArray_sc_liblink.size();k2++){
																	try {
																		JSONObject js_sc_liblink=jsArray_sc_liblink.getJSONObject(k2);
																		if(js_sc_liblink.containsKey("sc_saveanchor")){
																			sc_saveanchor=js_sc_liblink.get("sc_saveanchor").toString();
																			sc_saveanchor=sc_saveanchor.substring(sc_saveanchor.indexOf("\"")+1,sc_saveanchor.lastIndexOf("\""));
																		}
																		if(js_sc_liblink.containsKey("sc_savelibsign")){
																			sc_savelibsign=js_sc_liblink.get("sc_savelibsign").toString();
																			sc_savelibsign=sc_savelibsign.substring(sc_savelibsign.indexOf("\"")+1,sc_savelibsign.lastIndexOf("\""));
																		}
																		if(js_sc_liblink.containsKey("sc_savesize")){
																			sc_savesize=js_sc_liblink.get("sc_savesize").toString();
																			sc_savesize=sc_savesize.substring(sc_savesize.indexOf("\"")+1,sc_savesize.lastIndexOf("\""));
																		}
																		if(js_sc_liblink.containsKey("sc_saveurl")){
																			sc_saveurl=js_sc_liblink.get("sc_saveurl").toString();
																			//sc_saveurl=sc_saveanchor.substring(sc_saveurl.indexOf("\"")+1,sc_saveurl.lastIndexOf("\""));
																		}
																		if(js_sc_liblink.containsKey("sc_vtype")){
																			sc_vtype=js_sc_liblink.get("sc_vtype").toString();
																			sc_vtype=sc_vtype.substring(sc_vtype.indexOf("\"")+1,sc_vtype.lastIndexOf("\""));
																		}
																	} catch (Exception e1) {
																		// TODO Auto-generated catch block
																		e1.printStackTrace();
																	}
																	
																}
															}
														} catch (Exception e1) {
															// TODO Auto-generated catch block
															e1.printStackTrace();
														}
													}
													
													if(js.containsKey("sc_libstate")){
														sc_libstate=js.get("sc_libstate").toString();
														sc_libstate=sc_libstate.substring(sc_libstate.indexOf("\"")+1,sc_libstate.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_longsign")){
														sc_longsign=js.get("sc_longsign").toString();
														sc_longsign=sc_longsign.substring(sc_longsign.indexOf("\"")+1,sc_longsign.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_page_score")){
														sc_page_score=js.get("sc_page_score").toString();
														sc_page_score=sc_page_score.substring(sc_page_score.indexOf("\"")+1,sc_page_score.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_pageno")){
														sc_pageno=js.get("sc_pageno").toString();
														sc_pageno=sc_pageno.substring(sc_pageno.indexOf("\"")+1,sc_pageno.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_parsertype")){
														sc_parsertype=js.get("sc_parsertype").toString();
														sc_parsertype=sc_parsertype.substring(sc_parsertype.indexOf("\"")+1,sc_parsertype.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_pdf_read")){
														sc_pdf_read=js.get("sc_pdf_read").toString();
														sc_pdf_read=sc_pdf_read.substring(sc_pdf_read.indexOf("\"")+1,sc_pdf_read.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_publish")){
														try {
															String sc_publish=js.get("sc_publish").toString();
															if(!sc_publish.isEmpty()){
																JSONArray jsArray_sc_publish=JSONArray.fromObject(sc_publish);
																//System.out.println("sc_publish.size=="+jsArray_sc_publish.size());
																for(int k2=0;k2<jsArray_sc_publish.size();k2++){
																	try {
																		JSONObject js_sc_liblink=jsArray_sc_publish.getJSONObject(k2);
																		if(js_sc_liblink.containsKey("sc_journal")){
																			sc_journal=js_sc_liblink.get("sc_journal").toString();
																			sc_journal=sc_journal.substring(sc_journal.indexOf("[")+1,sc_journal.lastIndexOf("]"));
																			sc_journal=sc_journal.replaceAll("\"", "");
																		}
																		if(js_sc_liblink.containsKey("sc_journal_uri")){	
																			sc_journal_uri=js_sc_liblink.get("sc_journal_uri").toString();
																			sc_journal_uri=sc_journal_uri.substring(sc_journal_uri.indexOf("\"")+1,sc_journal_uri.lastIndexOf("\""));
																		}
																		if(js_sc_liblink.containsKey("sc_publisher")){	
																			sc_publisher=js_sc_liblink.get("sc_publisher").toString();
																			sc_publisher=sc_publisher.substring(sc_publisher.indexOf("\"")+1,sc_publisher.lastIndexOf("\""));
																		}
																	} catch (Exception e1) {
																		// TODO Auto-generated catch block
																		e1.printStackTrace();
																	}
																	
																	
																}
															}
														} catch (Exception e1) {
															// TODO Auto-generated catch block
															e1.printStackTrace();
														}
														//System.out.println("sc_publish=="+sc_publish);
													}
													if(js.containsKey("sc_ref_sum")){
														sc_ref_sum=js.get("sc_ref_sum").toString();
														sc_ref_sum=sc_ref_sum.substring(sc_ref_sum.indexOf("\"")+1,sc_ref_sum.lastIndexOf("\""));
														
													}
													
													if(js.containsKey("sc_time")){
														sc_time=js.get("sc_time").toString();
														sc_time=sc_time.substring(sc_time.indexOf("\"")+1,sc_time.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_year")){
														sc_year=js.get("sc_year").toString();
														sc_year=sc_year.substring(sc_year.indexOf("\"")+1,sc_year.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_savelink_sum")){
														sc_savelink_sum=js.get("sc_savelink_sum").toString();
														sc_savelink_sum=sc_savelink_sum.substring(sc_savelink_sum.indexOf("\"")+1,sc_savelink_sum.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_savetype")){
														sc_savetype=js.get("sc_savetype").toString();
														sc_savetype=sc_savetype.substring(sc_savetype.indexOf("\"")+1,sc_savetype.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_scholarurl")){
														sc_scholarurl=js.get("sc_scholarurl").toString();
														sc_scholarurl=sc_scholarurl.substring(sc_scholarurl.indexOf("\"")+1,sc_scholarurl.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_source")){
														sc_source=js.get("sc_source").toString();
														sc_source=sc_source.substring(sc_source.indexOf("\"")+1,sc_source.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_title")){
														sc_title=js.get("sc_title").toString();
														sc_title=sc_title.substring(sc_title.indexOf("\"")+1,sc_title.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_type")){
														sc_type=js.get("sc_type").toString();
														sc_type=sc_type.substring(sc_type.indexOf("\"")+1,sc_type.lastIndexOf("\""));
														
													}
													if(js.containsKey("sc_volumeno")){
														sc_volumeno=js.get("sc_volumeno").toString();
														sc_volumeno=sc_volumeno.substring(sc_volumeno.indexOf("\"")+1,sc_volumeno.lastIndexOf("\""));
													
													}						
												} catch (Exception e1) {
													
													e1.printStackTrace();
												}
											}
										} catch (Exception e1) {
											
											e1.printStackTrace();
										}
									}
								}else{
									System.err.println("json请求界面失效,status"+status);
									String url_md5=toMD5(url);
									String newTime=TimeUtils.getTime();
									
									Object[] parpm = {url,title,newTime,status,ReadConfig.year_,tablename};
									String sql="insert into baiduxueshuLog.dbo.statusErrorLog (url,title,newTime,status,year,tableName) values (?,?,?,?,?,?)";
									SQLHelper.insertBySQL(sql,parpm);
									break;
								}
								if(isEnglishi(sc_title)){
										
									
									if(sc_abstract==null||sc_abstract==""){
										sc_abstract=doc.select("#dtl_l > div:nth-child(1) > div.c_content.content_hidden > div.abstract_wr > p.abstract").text().trim();
										System.out.println("json 摘要为空 ；页面显示为："+sc_abstract);
									}
									String url_md5="";
									url_md5=toMD5(url);
										Object[] parpm = {url,url_md5,title,sc_StdStg,sc_abstract,sc_all_version,author,sc_cited,sc_citedyear,sc_codetype,sc_doctype,
									  sc_doi,sc_iscitestyle,sc_issue,sc_keyword,sc_level,sc_saveanchor,sc_savelibsign,sc_savesize,sc_saveurl,sc_vtype,
									  sc_libstate,sc_longsign,sc_page_score,sc_pageno,sc_parsertype,sc_pdf_read,sc_journal,sc_journal_uri,sc_publisher,sc_ref_sum,
									  sc_time,sc_year,sc_savelink_sum,sc_savetype,sc_scholarurl,sc_source,sc_type,sc_volumeno,biaozhi,author_show,laiyuan,chubanyuan,yanjiu
									  };
										
										
										String sql  = "insert into "+tablename + "("
									+ "url,url_md5,title,sc_StdStg,sc_abstract,sc_all_version,author,sc_cited,sc_citedyear,sc_codetype,sc_doctype,"
									 +"sc_doi,sc_iscitestyle,sc_issue,sc_keyword,sc_level,sc_saveanchor,sc_savelibsign,sc_savesize,sc_saveurl,sc_vtype,"
									 + "sc_libstate,sc_longsign,sc_page_score,sc_pageno,sc_parsertype,sc_pdf_read,sc_journal,sc_journal_uri,sc_publisher,sc_ref_sum,"
									 + "sc_time,sc_year,sc_savelink_sum,sc_savetype,sc_scholarurl,sc_source,sc_type,sc_volumeno,biaozhi,author_show,"
									 + "laiyuan,chubanyuan,yjdfx)"
												+ " values(?,?,?,?,?,?,?,?,?,?,"
												+ "?,?,?,?,?,?,?,?,?,?,"
												+ "?,?,?,?,?,?,?,?,?,?,"
												+ "?,?,?,?,?,?,?,?,?,?,"
												+ "?,?,?,?)";
										
											SQLHelper.insertBySQL(sql,parpm);
									
									/*System.out.println("md5=="+url_md5);
									System.out.println("author_show="+author_show);
									System.out.println("author="+author);
									System.out.println("laiyuan="+laiyuan);*/
									
										/*	System.out.println(url);
											System.out.println(sc_title);
											 
									System.out.println("sc_StdStg=="+sc_StdStg);
									System.out.println("sc_abstract=="+sc_abstract);
									System.out.println("sc_all_version=="+sc_all_version);
									System.out.println("laiyuan=="+laiyuan);
									System.out.println("xiazai="+xiazai);
									System.out.println("author="+author);
									System.out.println("sc_cited=="+sc_cited);
									System.out.println("sc_citedyear=="+sc_citedyear);
									System.out.println("sc_codetype=="+sc_codetype);
									System.out.println("sc_doctype=="+sc_doctype);
									System.out.println("sc_doi=="+sc_doi);
									System.out.println("sc_iscitestyle=="+sc_iscitestyle);
									System.out.println("sc_issue=="+sc_issue);
									System.out.println("sc_keyword=="+sc_keyword);
									System.out.println("sc_level=="+sc_level);
									System.out.println("sc_saveanchor=="+sc_saveanchor);
									System.out.println("sc_savelibsign=="+sc_savelibsign);
									System.out.println("sc_savesize=="+sc_savesize);
									System.out.println("sc_saveurl=="+sc_saveurl);
									System.out.println("sc_vtype=="+sc_vtype);
									System.out.println("sc_libstate=="+sc_libstate);
									System.out.println("sc_longsign=="+sc_longsign);
									System.out.println("sc_page_score=="+sc_page_score);
									System.out.println("sc_pageno=="+sc_pageno);
									System.out.println("sc_parsertype=="+sc_parsertype);
									System.out.println("sc_pdf_read=="+sc_pdf_read);
									System.out.println("sc_journal=="+sc_journal);
									System.out.println("sc_journal_uri=="+sc_journal_uri);
									System.out.println("sc_publisher=="+sc_publisher);
									System.out.println("sc_ref_sum=="+sc_ref_sum);
									System.out.println(sc_time);
									System.out.println(sc_year);
									System.out.println(sc_savelink_sum);System.out.println(sc_savetype);System.out.println(sc_scholarurl);
									System.out.println(sc_source);System.out.println(sc_type);System.out.println(sc_volumeno);System.out.println(biaozhi);*/
									
								}
								}catch (Exception e) {
									e.printStackTrace();
								}
							
				}catch(Exception e){
					System.out.println("查找数据失败");
					e.printStackTrace();
				}
				}
				
				
			
				
	}
			}
				}catch(Exception e){
					throw e;
				}
		}

		
		
		
}
