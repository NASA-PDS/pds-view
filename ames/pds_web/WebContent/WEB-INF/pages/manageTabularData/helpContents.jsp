<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="defaultWrapper">
    <div class="title">Help</div>
    <div class="basicBox">
    	<div class="label">Table of Contents</div>
    	<div class="contents">
    		<ul>
    			<li><a href="#overview">Overview of the Table Explorer</a></li>
    			<li><a href="#usage">Usage Information</a></li>
    			<li><a href="#faq">Frequently Asked Questions</a></li>
    			<li><a href="#comparison">Comparison with Other Tools</a></li>
    		</ul>
    	</div>
        
        <div class="label"><a name="overview"></a>Overview of the Table Explorer</div>
        <div class="contents">The Table Explorer requires the URL of a PDS3 or PDS4 label. The labels can be acquired from one
        of the <a href="https://pds.jpl.nasa.gov/about/organization.shtml" target="_blank">PDS Nodes. </a> 
     
                 There are three main sections in the Table Explorer:
             
             <ol>
             	<li>Control section: Allows you to sort, order, and download the data as follows:<br>
             		<ul>
             			<li>Select Columns: Select the columns to be shown and/or downloaded.</li>
             			<li>Order Columns: Change the order in which columns are displayed and/or downloaded.</li>
             			<li>Filter Rows: Filter the rows to be shown or downloaded given a numeric or string condition.</li>
             			<li>Sort Rows: Sort the selected column by row values.</li>
             			<li>Download: Download the selected, sorted and/or filtered tabular data as CSV or Fixed-width.</li>
             		</ul>
	 	</li>
	 	<li>Status section: Progress is shown as well as additional information about
	 		the source files.
	 	</li>
	 	<li>View section: Displays all the data described in the label in table format.
	 	</li>
             </ol>
         
         <br><img alt="Table Explorer Import Label" src="web/images/tableExplorerLayout.png" class="image" width="71%"/><br>
         
        </div>
        
        <div class="label"><a name="usage"></a>Usage Information</div>
        <div class="contents">The proper way to use the Table Explorer is as follows:
            <ol>
                <li>Access one of the PDS nodes data volume archives. For example, <a href="http://imbrium.mit.edu/LOLA.html" target="_blank">
                LOLA (Lunar Orbiter Laser Altimeter)</a> for PDS3 labels or <a href="http://pds-atmospheres.nmsu.edu/data_and_services/atmospheres_data/LADEE/mainr.html" target="_blank">
                LADEE (Lunar Atmosphere and Dust Environment Explorer)</a> for PDS4 labels.
                </li>
                <li>Browse through the archive until you find the desired label. The label has extension .LBL for PDS3 or .XML for PDS4 labels.</li>
                <li>Copy the URL of the label. 
                    For example: 
                    <ul>
                		<li><a href="http://pds-rings.seti.org/volumes/ASTROM_xxxx/ASTROM_0001/DATA/EASYDATA/DIONE.LBL" target="_blank">http://pds-rings.seti.org/volumes/ASTROM_xxxx/ASTROM_0001/DATA/EASYDATA/DIONE.LBL</a></li>
                		<li><a href="http://sbn.psi.edu/archive/ldex/ldex_validate/data_housekeeping/primary/housekeeping_13325_13334.xml" target="_blank">
                	http://sbn.psi.edu/archive/ldex/ldex_validate/data_housekeeping/primary/housekeeping_13325_13334.xml</a></li>
                	</ul>
                </li>
                <li>Paste the label's URL into the Import Label field of the Table Explorer as shown in the image below.
                <br><br><img alt="Table Explorer Import Label" src="web/images/tableExplorerImportLabel.png" class="image" width="73%"/>
                <br>
                </li>
                <li>Click "Get Started" to begin processing the label. This might take sometime depending on the size of the data.
                While the rest of the data is processed, you may browse through the first 100 rows of data.<br>Information about each of the columns
                as well as each of the source files can be accessed by moving the mouse pointer over the column headers or source file name as shown below.
                <img alt="Table Explorer Import Label" src="web/images/tableExplorerTable.png" class="image" />
                <br>
                </li>
             
                <li>Once the processing has completed, the rest of the data will be available and the ability to Select/Order columns, 
                Filter/Sort rows, and download the data will be enabled as shown below.
                <br><br><img alt="Table Explorer Import Label" src="web/images/tableExplorerTableFinished.png" class="image"/><br>
                </li>

            </ol>

        </div>
        
        <div class="label"><a name="faq"></a>Frequently Asked Questions</div>
        <div class="contents">
            <ol>
                <li>What is PDS?<br><br>
                 The Planetary Data System is a distributed data system, which consists of 8 nodes and 2 supporting nodes, and the various tools for using, manipulating and visualizing data.
                 The system archives and distributes all the data collected by robotic missions, astronomical observations, and other telemetry measurements
				 to ensure its effective and long-lasting storage, exchange and post-processing analysis.<br><br>
				 </li>
                
                <li>Where can I find information about the PDS nodes?<br><br>
                All the information about the PDS Nodes, including the data, tools, documents and other information can be accessed at 
                <a href="https://pds.jpl.nasa.gov/" target="_blank">https://pds.jpl.nasa.gov/</a><br><br>
                </li>
                
                <li>Why do I get the message that the URL entered is not reachable?<br><br>
                At times, the host changes the URL of the website. For example, the following label link was changed from
                <p> <a href="http://pds-rings.seti.org/vol/ASTROM_xxxx/ASTROM_0001/DATA/EASYDATA/DIONE.LBL" target="_blank">http://pds-rings.seti.org/vol/ASTROM_xxxx/ASTROM_0001/DATA/EASYDATA/DIONE.LBL</a> to 
                <a href="http://pds-rings.seti.org/volumes/ASTROM_xxxx/ASTROM_0001/DATA/EASYDATA/DIONE.LBL" target="_blank">http://pds-rings.seti.org/volumes/ASTROM_xxxx/ASTROM_0001/DATA/EASYDATA/DIONE.LBL</a></p>
                <p>As you may see, the only difference is that "vol" was changed to "volumes". Therefore, if you get this error message, check that the URL is accessible or use its new URL.</p>
                </li>
                
                <li>Can I process multiple labels at the same time?<br><br>
                No. At this time, only one label can be processed at a time.<br><br>
                </li>
                <li>What are the output formats currently supported?<br><br>
                Currently, the system supports Fixed Width and CSV formats for ASCII data and CSV format for binary data.
                </li>
            </ol>
        </div>
        
        <div class="label"><a name="comparison"></a>Comparison with Other Tools</div>
        <div class="contents">
        	<p>Other tools for viewing PDS tables include these:</p>
        	<ul>
        		<li>PPI Data Table Viewer &#x2013; Integrated into the <a href="http://ppi.pds.nasa.gov/">PPI web site</a> and available
        		  when browsing data sets containing tabular data.</li>
        		<li>Orbital Data Explorer (ODE) &#x2013; The <a href="http://ode.rsl.wustl.edu">Orbital Data Explorer</a> sites
        		  at the Geosciences Node allows downloading tabular data in native format.</li>
        		<li>LRO LEND Data Viewer &#x2013; The <a href="http://pds-geosciences.wustl.edu/missions/lro/lend.htm">LEND</a>
        		  instrument web page at the Geosciences Node has a downloadable tool for viewing LEND data from the Lunar
        		  Reconnaissance Orbiter. This tool runs on Windows only.</li>
        	</ul>
        	<p>See <a href="web/downloads/TableExplorerOverview.pdf">this presentation</a> for a comparison of features of the Table Explorer and these other tools.</p>
        </div>
    </div>
</div>
