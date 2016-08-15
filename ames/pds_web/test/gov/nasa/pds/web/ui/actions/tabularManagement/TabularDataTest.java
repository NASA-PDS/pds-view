package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.actions.BaseTestAction;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;
import java.io.File;
import java.util.List;
import java.util.Random;
import com.opensymphony.xwork2.Action;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class TabularDataTest extends BaseTestAction {


    private LoadData loadData;
    private String loadDataResult;
    private String testLabelURL;
    private SliceContainer slice;
    private TabularDataContainer table;

    /*
     * retrieves tabularData directory in test directory
     *
     * @return File file object representing tabularDataTest directory
     */
    private File getTabularDataPath() {
        return new File(getTestDataDirectory(), "tabularData");//$NON-NLS-1$
    }

    /*
     * builds loadLabel object based on test file
     *
     * @param labelFileName name of label file in test directory to be read
     */
    private void retrieveData(String labelFileName) throws Exception {
        LoadLabel loadLabel = createAction(LoadLabel.class);
    
        this.testLabelURL = labelFileName;
        System.out.println("Processing label: "+this.testLabelURL);
        
        loadLabel.setLabelURLString(this.testLabelURL);
        String loadLabelResult = loadLabel.execute();
        
        //Display errors if any
        if(loadLabel.getHasErrors()){
        
	        List<String> listOfErrors = loadLabel.getErrorMessagesSimple();
	        if(!listOfErrors.isEmpty()) {
	        	System.out.println("Errors found:");
	        	for(int i=0; i<listOfErrors.size(); i++)
	        		System.out.println("\t"+listOfErrors.get(i));
	        }
	        
        }
        //assertEquals(Action.SUCCESS, loadLabelResult);
        
        final String procId = loadLabel.getProcId();
        this.loadData = createAction(LoadData.class);
        this.loadData.blocking = true;
        this.loadData.setProcId(procId);
        this.loadDataResult = this.loadData.execute();
        
        //assertEquals(Action.SUCCESS, this.loadDataResult);
        
        TabularDataProcess process = (TabularDataProcess) this.loadData
                .getProcess();
        
        this.slice = process.getSlice();
        this.table = this.slice.getActiveTabularData();
    }

    
    /**
     * Unit test used to test multiple labels randomly. The temporary
     * tables are created and filled.
     * 
     * TODO: Add assertions to test specific aspects of the data
     * 
     * @throws Exception
     */
    public void testMultipleLabels() throws Exception {
    	
    	// parameters to set to crawl URL defined by volumeURL
    	// ================================================
    	// setup the crawler
    	int numberOfCrawlers = 1;//number of crawling threads
    	int crawlerRunTimeInSecs = 10;//wait time before shutdown
        int TOTAL_RANDOM_NUMBERS = 50;//number of random numbers to generate
    	String crawlStorageFolder = "./test-data/crawlerStorageFolder";//temporary folder used by crawler
    	//String volumeURL = "http://sbn.psi.edu/archive/ldex/ldex_validate/data_calibrated/current/";//"http://atmos.nmsu.edu/PDS/data/PDS4/LADEE/nms_bundle/calibration/2011-11/";//"http://imbrium.mit.edu/data/";
    	String volumeURL = "http://pds-rings.seti.org/cassini/cirs/";
    	
    	
    	
    	/*
    	 * PDS3:
    	 * 		http://imbrium.mit.edu/data/  - LOLA  - geoscience data
    	 * 
    	 * 
    	 * 
    	 * PDS4:
    	 * 		http://sbn.psi.edu/archive/ldex/ldex_validate/data_calibrated/current/
    	 * 
    	 * 
    	 * 
    	 * 
    	 */
    	
    	// ================================================	
    	
    	// setting up crawler configuration
    	CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setPolitenessDelay(1000);
		config.setMaxPagesToFetch(-1);//Unlimited number of pages to be crawled
		
		// Instantiate the controller for this crawl. 
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
    	
		//add the root of the volume
		LabelCrawler.VOLUME_DOMAIN = volumeURL;
		LabelCrawler.EXTENSION = ".LBL";//uppercase
		controller.addSeed(volumeURL);
		
		
		//Start the crawl. This is a blocking operation, meaning that your code
		controller.startNonBlocking(LabelCrawler.class, numberOfCrawlers);

		// Wait for N seconds
		Thread.sleep(crawlerRunTimeInSecs*1000);

		// Send the shutdown request and then wait for finishing
		controller.Shutdown();
		controller.waitUntilFinish();
		
		System.out.println("Crawler has shutdown after " + crawlerRunTimeInSecs +" seconds...");
		
		List<String> listOfLabels = LabelCrawler.getListOfLabels();
		
		// do not run test if there are no labels
		if(listOfLabels.isEmpty()){
			System.out.println("No labels found. Stopping unit test.");
			System.exit(0);
		}
		
		// test 10% of the total number of labels
		double percentageToTest = 0.10;
		TOTAL_RANDOM_NUMBERS = (int)((percentageToTest)*(double)listOfLabels.size());
		
		System.out.println("Total number of labels found in " + crawlerRunTimeInSecs + " seconds: " + listOfLabels.size());
		System.out.println("Processing a total of " + TOTAL_RANDOM_NUMBERS + " out of " + listOfLabels.size() );
		
		
		//Generate N number of labels to test
        Random randomGenerator = new Random();
        int randomInt[] = new int[TOTAL_RANDOM_NUMBERS];
		
        //Get 5 random indexes within the labels size range
        for (int i = 0; i < randomInt.length; ++i) {
            randomInt[i] = randomGenerator.nextInt(listOfLabels.size()-1);
        }
        
        System.out.println();
		System.out.println("The labels to be processed are:");
		for(int i=0; i<randomInt.length; i++) {
			System.out.println("\t"+listOfLabels.get(randomInt[i]));
		}
		System.out.println();
		
        //Select random labels
        for (int j=0; j<randomInt.length; j++) {
        	
            //Get one of the labels
            String url = listOfLabels.get(randomInt[j]);
            
            System.out.println("=======================================");
            System.out.println("Label to process: "+url);
            
            try {
            	retrieveData(url);
            } catch(Exception e) {
            	System.out.println("Error detected in this label:");
            	System.out.println("Error message: " + e.getMessage());
            	System.out.println("Error stack:");
            	e.printStackTrace();
            }
           
            System.out.println("=======================================");
            System.out.println();
        }
    }
    
    /**
     * Unit test used to test multiple labels randomly. The temporary
     * tables are created and filled.
     * 
     * TODO: Add assertions to test specific aspects of the data
     * 
     * @throws Exception
     */
    /*
    public void testMultipleLabels() throws Exception {

        Document htmlFile = null;
        
        //Parent Reference
        String url = "http://imbrium.mit.edu/DATA/LOLA_EDR/LRO_ES_01/";
        
        //Total number of random numbers to generate
        int TOTAL_RANDOM_NUMBER = 5;

        try {
            htmlFile = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //Get all hrefs
        Elements links = htmlFile.select("a[href]");
        
        //Random Generator for
        Random randomGenerator = new Random();
        int randomInt[] = new int[TOTAL_RANDOM_NUMBER];
        
        //Only references that end with .LBL or .lbl will be saved
        Elements labels = new Elements();

        //Save those URLs that are labels
        for(int k=0; k<links.size(); k++){

            if( links.get(k).attr("abs:href").endsWith(".lbl") || links.get(k).attr("abs:href").endsWith(".LBL") ){
                labels.add(links.get(k));
            }
        }

        //Get 5 random indexes within the labels size range
        for (int i = 0; i < randomInt.length; ++i){
            randomInt[i] = randomGenerator.nextInt(labels.size()-1);
        }

        //Select the random labels
        for (int j=0; j<randomInt.length; j++) {
            retrieveData(labels.get(randomInt[j]).attr("abs:href"));
        }

    }
*/
    @Override
    protected void clearAction() {
        this.loadData = null;
        this.loadDataResult = null;
        this.testLabelURL = null;
        this.slice = null;
        this.table = null;

    }


}
