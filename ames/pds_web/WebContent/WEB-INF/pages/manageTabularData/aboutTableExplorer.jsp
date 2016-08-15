<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="defaultWrapper">
    <div class="title">About Table Explorer</div>
    <div class="basicBox">
        <div class="label">About the Tool</div>
        <div class="contents">
        The Table Explorer is a tool designed to facilitate the viewing, filtering, and downloading of planetary data sets. 
        These data sets generally store label metadata in a file that is separate from the data files. The Table Explorer 
        takes the stored label metadata and presents it alongside a tabular data product with the option to extract a portion 
        or all of the data into a readable file. Users input the URL of a PDS3 or PDS4 label, and the Table Explorer reads 
        both the label metadata and the ASCII or binary data. The data is extracted and presented in tabular format, which 
        includes table headers and their corresponding data values. Users can filter the data in order to display, hide, or 
        download those rows and columns that meet a specific criteria. The table can then be downloaded as a CSV file for 
        input into Excel or other analytical tools.</div>
        
        <div class="label">Table Explorer Process</div>
        <div class="contents">In general, the Table Explorer works as follows:
            <ol>
                <li>The user copies and pastes the URL of a PDS3 or PDS4 label</li>
                <li>A Label object is created</li>
                <li>A temporary table is created and the data from the data file is read and saved into the database</li>
                <li>Once all the data has been inserted into the database, the data is queried and displayed in tabular format.
                This allows for sorting, ordering, and downloading of the data into Fixed Width or CSV formats</li>
            </ol>
            
            <div class="contents">
                Table Explorer currently works with ASCII and Binary tables, detached PDS3 Labels, columns with items and bit columns
                with items, and PDS4 labels with table types: Delimited, Character and Binary.
            </div>
        </div>
    </div>
</div>