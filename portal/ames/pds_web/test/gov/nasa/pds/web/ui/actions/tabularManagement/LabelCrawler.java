package gov.nasa.pds.web.ui.actions.tabularManagement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class LabelCrawler extends WebCrawler {

	public static List<String> listOfLabels = new ArrayList<String>();
	public static String VOLUME_DOMAIN = "";
	public static String EXTENSION = ".LBL";

	private boolean listFull = false;

	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		
		if((href.endsWith(EXTENSION.toLowerCase()) || href.endsWith(EXTENSION))){
			listOfLabels.add(href);
		}
		return true;
	}
	
	public static boolean isEmpty() {
		return listOfLabels.isEmpty();
	}
	
	public boolean isListFull(){
		return this.listFull;
	}
	
	public static List<String> getListOfLabels(){
		

		// remove any duplicate items. Does not
		// preserved ordering
		HashSet<String> hs = new HashSet<String>();
		hs.addAll(listOfLabels);
		listOfLabels.clear();
		listOfLabels.addAll(hs);
		
		return listOfLabels;
	}

	@Override
	public void visit(Page page) {
		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();
		int parentDocid = page.getWebURL().getParentDocid();

		//System.out.println("Docid: " + docid);
		//System.out.println("URL: " + url);
		//System.out.println("Docid of parent page: " + parentDocid);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			List<WebURL> links = htmlParseData.getOutgoingUrls();

			//System.out.println("Text length: " + text.length());
			//System.out.println("Html length: " + html.length());
			//System.out.println("Number of outgoing links: " + links.size());
		}

		//System.out.println("=============");
	}
}

