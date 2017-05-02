package NCBI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ParsePMC_fulltext 
{
	private String nxml;
	private Pattern target;
	private Matcher matcher;

	String aLine = null;
	StringBuffer sb = new StringBuffer();
	int countRef[] = new int[2];
	
	String file_folder = "";			// File folder (xml)
	
	private String Journal;
	private String PMID;
	private String Title;
	private String Abstract;
	private String FullText;
	
	
	public ParsePMC_fulltext() throws IOException
	{
		File file = new File(file_folder);
		String articleList[] = file.list();
		
		for(int i=0;i<articleList.length;i++)
		{			
			String list = articleList[i];
			File file2 = new File(file_folder+"\\"+list); 
			String articleList2[] = file2.list();
			FileWriter fw = new FileWriter(articleList[i]+".txt", true);
			BufferedWriter bfw = new BufferedWriter(fw);
			
			//bfw.write("Journal Title	"+"PMID	"+"Title	"+"Abstract	"+"Fulltext"+"\n");
						
			for(int j=0;j<articleList2.length;j++)
			{
				String list2 = articleList2[j];
				ParsePMC_fulltext(file_folder+"\\"+list+"\\"+list2);
				
				//System.out.println(getJournalTitle()+"	"+getPMID());
				//bfw.write(getPMID()+"	");
				Journal = getJournalTitle();
				PMID = getPMID();
				Title = getArticleTitle();
				Abstract = getAbstract();
				FullText = getFulltext();
				
				// Release sb
				sb.setLength(1);
			}			
		}
		
		Runtime.getRuntime().gc();
	}
	
	private void ParsePMC_fulltext(String string) throws IOException
	{
		
		nxml = string;
		FileReader fr = new FileReader(nxml);
		BufferedReader bfr = new BufferedReader(fr);

		while((aLine=bfr.readLine())!=null)
		{
			sb.append(aLine);
		}
	}
	
	//***********  Get PMID  ***********
	private String getPMID() throws IOException
	{
		String pmid;
		target = Pattern.compile("<article-id.?pub-id-type=\"pmid\">.*?</article-id>", Pattern.CASE_INSENSITIVE);
		matcher = target.matcher(sb.toString());
		if(matcher.find()){
			pmid = matcher.group().replaceAll("<{1}[^>]{1,}>{1}", "").trim();
		}else{ pmid = null; }
			return pmid;
	}
	
	//***********  Get ISSN  ***********
	private	String getISSN() throws IOException
	{
		String issn;
		target = Pattern.compile("<issn pub-type=\"epub\">.*?</issn>", Pattern.CASE_INSENSITIVE);
		matcher = target.matcher(sb.toString());
		if(matcher.find()){
			issn = matcher.group().replaceAll("<{1}[^>]{1,}>{1}", "").trim();
		}else{ issn = ""; }
			return issn;
	}
	
	//***********  Get Journal  Title  ***********
	private	String getJournalTitle() throws IOException
	{
		String journalTitle;
		target = Pattern.compile("<journal-title>.*?</journal-title>", Pattern.CASE_INSENSITIVE);
		matcher = target.matcher(sb.toString());
		if(matcher.find()){
			journalTitle = matcher.group().replaceAll("<{1}[^>]{1,}>{1}", "").trim();
		}else{ journalTitle = null; }
			return journalTitle;
	}
	
	//***********  Get Publisher Name  ***********
	private	String getPublisherName() throws IOException
	{
		String publisherName;
		target = Pattern.compile("<publisher.*?</publisher>", Pattern.CASE_INSENSITIVE);
		matcher = target.matcher(sb.toString());
		matcher.find();
		//***  publisher���U�٦��@�hpublisher-name  ***
		target = Pattern.compile("<publisher-name>.*?</publisher-name>", Pattern.CASE_INSENSITIVE);
		matcher = target.matcher(sb.toString());
		if(matcher.find()){
			publisherName = matcher.group().replaceAll("<{1}[^>]{1,}>{1}", "").trim();
		}else{ publisherName = ""; }
			return publisherName;
	}
	
	//***********  Get Publisher Location  ***********
	private String getPublisherLocation() throws IOException
	{
		String publishLocation;
		target = Pattern.compile("<publisher.*?</publisher>", Pattern.CASE_INSENSITIVE);
		matcher = target.matcher(sb.toString());
		if(matcher.find()){ //�Y��publisher���ҫh���X�A�Y�L�h�N��Llocation
			Pattern targetLocation = Pattern.compile("<publisher-loc.*?</publisher-loc>", Pattern.CASE_INSENSITIVE);
			Matcher matcherLocation = targetLocation.matcher(matcher.group());
			if(matcherLocation.find()){ //�Y��location�����ҫh���X
				publishLocation = matcherLocation.group().replaceAll("<{1}[^>]{1,}>{1}", "").trim();
			}else{ publishLocation = ""; }
		}else{ publishLocation = ""; }
			return publishLocation;
	}
	
	//***********  Get Publish Date  ***********
	private	String getPublishDate() throws IOException
	{
		String time;
		target = Pattern.compile("<pub-date.?pub-type=\"epub\">.*?</pub-date>", Pattern.CASE_INSENSITIVE);
		matcher = target.matcher(sb.toString());
		if(matcher.find()){  //�M�䦳�_�ŦX�r��è��Xpubdate
			String day,month,year;
			Pattern target_date;
			Matcher matcher_date;

			//***  �w��~�B��B����O���P�_  ***
			target_date = Pattern.compile("<day>.*</day>", Pattern.CASE_INSENSITIVE);
			matcher_date = target_date.matcher(matcher.group());
			if(matcher_date.find()){
				day = matcher_date.group().replaceAll("<.*?day>", "");
			}else{ day=""; }

			target_date = Pattern.compile("<month>.*</month>", Pattern.CASE_INSENSITIVE);
			matcher_date = target_date.matcher(matcher.group());
			if(matcher_date.find()){
				month = matcher_date.group().replaceAll("<.*?month>", "");
			}else{ month=""; }

			target_date = Pattern.compile("<year>.*</year>", Pattern.CASE_INSENSITIVE);
			matcher_date = target_date.matcher(matcher.group());
			if(matcher_date.find()){
				year = matcher_date.group().replaceAll("<.*?year>", "");
			}else{ year=""; }

			time = year+"/"+month+"/"+day;
		}else{ time="";	}

		return time; 
	}
	
	//***********  Get Publish Year  ***********
	private	int getPublishYear() throws IOException
	{
		int intYear=0;
		target = Pattern.compile("<pub-date.?pub-type=\"epub\">.*?</pub-date>", Pattern.CASE_INSENSITIVE);
		matcher = target.matcher(sb.toString());
		if(matcher.find()){
			target = Pattern.compile("<year>.*</year>", Pattern.CASE_INSENSITIVE);
			matcher = target.matcher(matcher.group());
			if(matcher.find()){
				String year = matcher.group().replaceAll("<.*?year>", "");
				intYear = Integer.parseInt(year);
			}
		}
			
		return intYear;
	}
	
	//***********  Get Article Title  ***********
	private	String getArticleTitle() throws IOException
	{
		String articleTitle;
		target = Pattern.compile("<article-title>.*?</article-title>", Pattern.CASE_INSENSITIVE);
		matcher = target.matcher(sb.toString());
		if(matcher.find()){
			articleTitle = matcher.group().replaceAll("<{1}[^>]{1,}>{1}", "").trim();
		}else{ articleTitle=""; }

		return articleTitle;
	}
	
	//***********  Get Abstract  ***********
	private	String getAbstract() throws IOException
	{
		String abstractData = null;
		target = Pattern.compile("<abstract>.*?</abstract>", Pattern.CASE_INSENSITIVE);
		matcher = target.matcher(sb.toString());
		if(matcher.find()){
			Pattern targetAbstract = Pattern.compile("<p>.*</p>", Pattern.CASE_INSENSITIVE);
			Matcher matcherAbstract = targetAbstract.matcher(matcher.group());

			while(matcherAbstract.find()){ //�]�i��|�Φh��<p>���Ҥ��}�A�G��while�������X
				abstractData = matcherAbstract.group().replaceAll("<{1}[^>]{1,}>{1}","");
			}
		}else{ abstractData=""; }

			return abstractData;
	}
	
	//***********  Get Full text  ***********
	private	String getFulltext() throws IOException
	{
		String fulltextData = null;
		target = Pattern.compile("<body>.*?</body>", Pattern.CASE_INSENSITIVE);
		matcher = target.matcher(sb.toString());
		//fulltextData = matcher;
		if(matcher.find()){
			fulltextData = matcher.group();
		}else{ fulltextData=""; }
		
		return fulltextData;
	}
	
	//***********  Get Author Information  ***********
	private	String[] getAuthorInformation()
	{
		target = Pattern.compile("<contrib.*?</contrib>", Pattern.CASE_INSENSITIVE);
		matcher = target.matcher(sb.toString());

		int count=0; 
		String AuthorInfo[] = new String[2000];

		Pattern targetAuthorName;
		Matcher matcherAuthorName;
			
		while(matcher.find())
		{
			String authorSurName = null;
			String authorGivenName = null;
			String authorEmail = null;

			targetAuthorName = Pattern.compile("<name.*?</name>", Pattern.CASE_INSENSITIVE);
			matcherAuthorName = targetAuthorName.matcher(matcher.group());

			Pattern target_email = Pattern.compile("<email>.*?</email>", Pattern.CASE_INSENSITIVE);
			Matcher matcher_email = target_email.matcher(matcher.group());

			while(matcherAuthorName.find())
			{
				String tmp_nm = matcherAuthorName.group();

				targetAuthorName = Pattern.compile("<surname>.*?</surname>", Pattern.CASE_INSENSITIVE);
				matcherAuthorName = targetAuthorName.matcher(tmp_nm);
				if(matcherAuthorName.find()){
					authorSurName = matcherAuthorName.group().replaceAll("<.*?surname>", "").trim();
				}else{authorSurName="";}
								
				targetAuthorName = Pattern.compile("<given-names>.*?</given-names>", Pattern.CASE_INSENSITIVE);
				matcherAuthorName = targetAuthorName.matcher(tmp_nm);
				if(matcherAuthorName.find()){
					authorGivenName = matcherAuthorName.group().replaceAll("<.*?given-names>", "").trim();
				}else{authorGivenName="";}
			}
				
			while(matcher_email.find())
			{
				authorEmail = matcher_email.group().replaceAll("<{1}[^>]{1,}>{1}", "").trim();
			}
				
			if(authorEmail!=null){
				AuthorInfo[count] = authorSurName+" "+authorGivenName+"<"+authorEmail+">";
			}else{
				AuthorInfo[count] = authorSurName+" "+authorGivenName;
			}
				count++;
			}
			return AuthorInfo;
	}	
	
	private int[] countReference()
	{
		target = Pattern.compile("<ref.*?</ref>", Pattern.CASE_INSENSITIVE);
		matcher = target.matcher(sb.toString());
		int refTitleNumber = 0;
		int refAuthorNumber = 0;
		
		while(matcher.find()){
			Pattern targetCitation = Pattern.compile("<citation.*?</citation>", Pattern.CASE_INSENSITIVE);
			Matcher matcherCitation = targetCitation.matcher(matcher.group());

			if(matcherCitation.find()){
				Pattern targetPerson = Pattern.compile("person-group-type=\"author\".*?</person-group>", Pattern.CASE_INSENSITIVE);
				Matcher matcherPerson = targetPerson.matcher(matcherCitation.group());				
				Pattern targetName;
				Matcher matcherName;
				if(matcherPerson.find()==true){  //�� <person-group> �ɰw�� author �ݩʬ���
					targetName = Pattern.compile("<name.*?</name>", Pattern.CASE_INSENSITIVE);
					matcherName = targetName.matcher(matcherPerson.group());
				}else{  //�S�� <person-group> �ɪ����� <citation> �U�� <name>
					targetName = Pattern.compile("<name.*?</name>", Pattern.CASE_INSENSITIVE);
					matcherName = targetName.matcher(matcherCitation.group());
				}
				while(matcherName.find()){ refAuthorNumber++; }
				refTitleNumber++;
			}
		}
		countRef[0]=refTitleNumber;
		countRef[1]=refAuthorNumber;
//		System.out.println(countRef[0]);
//		System.out.println(countRef[1]);

		return countRef;
	}
	
	private String[][] ReferenceData2()		//Reference Data
	{  
		
		String refInfo[][] = new String[3000][1000];
		int countTitle = 0;

		target = Pattern.compile("<ref.*?</ref>", Pattern.CASE_INSENSITIVE);
		matcher = target.matcher(sb.toString());

		while(matcher.find())
		{
			int countAuthor = 1;

			Pattern targetCitation = Pattern.compile("<citation.*?</citation>", Pattern.CASE_INSENSITIVE);
			Matcher matcherCitation = targetCitation.matcher(matcher.group());

			if(matcherCitation.find()){
				Pattern targetArticle = Pattern.compile("<article-title.*?</article-title>", Pattern.CASE_INSENSITIVE);
				Matcher matcherArticle = targetArticle.matcher(matcherCitation.group());

				if(matcherArticle.find()!=true){ //�Y��article�h�u�s��article�A�Y�Larticle�h�s�����
					refInfo[countTitle][0] = matcherCitation.group().replaceAll("<{1}[^>]{1,}>{1}", "").trim(); //�����g�J��Ӭq��(no article�Bno name)}
				}else{
					refInfo[countTitle][0] = matcherArticle.group().replaceAll("<{1}[^>]{1,}>{1}", "").trim();

					Pattern targetPerson = Pattern.compile("person-group-type=\"author\".*?</person-group>", Pattern.CASE_INSENSITIVE);
					Matcher matcherPerson = targetPerson.matcher(matcherCitation.group());				

					Pattern targetName;
					Matcher matcherName;

					if(matcherPerson.find()==true){  //�� <person-group> �ɰw�� author �ݩʬ���
						targetName = Pattern.compile("<name.*?</name>", Pattern.CASE_INSENSITIVE);
						matcherName = targetName.matcher(matcherPerson.group());
					}else{  //�S�� <person-group> �ɪ����� <citation> �U�� <name>
						targetName = Pattern.compile("<name.*?</name>", Pattern.CASE_INSENSITIVE);
						matcherName = targetName.matcher(matcherCitation.group());
					}
					while(matcherName.find()){
						String surName;
						Pattern targetSurname = Pattern.compile("<surname.*?</surname>", Pattern.CASE_INSENSITIVE);
						Matcher matcherSurname = targetSurname.matcher(matcherName.group());
						if(matcherSurname.find()){
							surName = matcherSurname.group().replaceAll("<{1}[^>]{1,}>{1}", "").trim(); 
						}else{
							surName = "";
						}
						
						String givenName;
						Pattern targetGivenname = Pattern.compile("<given-names.*?</given-names>", Pattern.CASE_INSENSITIVE);
						Matcher matcherGivenname = targetGivenname.matcher(matcherName.group());
						if(matcherGivenname.find()){
							givenName = matcherGivenname.group().replaceAll("<{1}[^>]{1,}>{1}", "").trim(); 
						}else{
							givenName = "";
						}

						refInfo[countTitle][countAuthor] = surName+" "+givenName;
						countAuthor++;
					}
				}
				countTitle++;
			}else{
				Pattern targetMixCitation = Pattern.compile("<mixed-citation.*?publication-type=\"journal\">.*?</mixed-citation>", Pattern.CASE_INSENSITIVE);
				Matcher matcherMixCitation = targetMixCitation.matcher(matcher.group());
				if(matcherMixCitation.find()){
					refInfo[countTitle][0] = matcherMixCitation.group().replaceAll("<{1}[^>]{1,}>{1}", "").trim();
				}
			}
		}
		return refInfo;
	}
	
	public static void main(String args[])
	{
		try {
			ParsePMC_fulltext ppf = new ParsePMC_fulltext();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
