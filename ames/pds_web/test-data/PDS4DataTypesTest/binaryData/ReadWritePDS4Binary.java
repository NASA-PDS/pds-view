import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.DatatypeConverter;

/*********************************************************************************************************

Author: German H. Flores

Filename: BytesStreamFileReadWrite.java

Output FileName: binary.dat

Date: 9/16/2014

This class has been writen to test all of the following data types in PDS4:

            


--------------------------------------------------------------------------------------------------------------
                                                  IEEE Special Values
--------------------------------------------------------------------------------------------------------------

IEEE Special values

  Infinity: +infinity and -infinity are denoted with an exponent of all 1s
            and a fraction of all 0s. 
            The sign bit distinguishes between negative infinity and positive
            infinity.

            Since mysql does not handle infinity, if the value is infinity, the
            largest allowable or smallest allowable values will be used to denote
            +infinity or -infinity.

  Not A Number: Used to represent a value that does not represent a real number.
                NaNs are represented by a bit pattern with an exponent of all
                1s and a non-zero fraction

                Since mysql does not handle NaN, the value to denote a NaN is null,
                a blank cell

*********************************************************************************************************************/


public class ReadWritePDS4Binary {

  private static String FILE_PATH = "";
  OutputStream output = null;
  private static ReadWritePDS4Binary labelBinaryData  = null;

  public static void main(String[] args) {


    String[] listOfFileNames = {
          "binary_fields_only.bin",
          "binary_fieldsAndGroups_FAO_1.bin",
          "binary_fieldsAndGroups_FAO_2.bin",
          "binary_fieldsAndGroups_FAOS1.bin",
          "binary_oneFileTwoTables.bin",
          "binary_fields_multiple_rows.bin",
          "all_binary_datatypes.bin",
    }; 


    FILE_PATH = args[0]+"/";

    labelBinaryData = new ReadWritePDS4Binary();

    //Upper Limit tests for FieldBinary types and some ASCII types
    //XML:  Table_Binary.xml and Table_Binary_Multiple.xml
    //DATA: binary_fields_only.bin
    byte[] binary_fields_only = {  
                      
                      (byte)0x66, (byte)0x61, (byte)0x6c, (byte)0x73, (byte)0x65,//ASCII_BOOLEAN - false
                      (byte)0x74, (byte)0x72, (byte)0x75, (byte)0x65,//ASCII_BOOLEAN - true
                      (byte)0x30,//ASCII_BOOLEAN - 0
                      (byte)0x31,//ASCII_BOOLEAN - 1
                      (byte)0x34, (byte)0x35,//ASCII_INTEGER - +45
                      (byte)0x2D, (byte)0x34, (byte)0x35,//ASCII_INTEGER - -45
                      (byte)0x40, (byte)0x49, (byte)0x0F, (byte)0xF9,//IEEE754MSBSingle
                      (byte)0xF9, (byte)0x0F, (byte)0x49, (byte)0x40,//IEEE754LSBSingle
                      (byte)0x40, (byte)0x09, (byte)0x21, (byte)0xFF, (byte)0x2E, (byte)0x48, (byte)0xE8, (byte)0xA7,//IEEE754MSBDouble
                      (byte)0xA7, (byte)0xE8, (byte)0x48, (byte)0x2E, (byte)0xFF, (byte)0x21, (byte)0x09, (byte)0x40,//IEEE754LSBDouble
                      (byte)0xA7, (byte)0xE8, (byte)0x48, (byte)0x2E, (byte)0xFF, (byte)0x21, (byte)0x09, (byte)0x40,//ComplexLSB16
                      (byte)0xA7, (byte)0xE8, (byte)0x48, (byte)0x2E, (byte)0xFF, (byte)0x21, (byte)0x09, (byte)0x40,
                      (byte)0xF9, (byte)0x0F, (byte)0x49, (byte)0x40, (byte)0xF9, (byte)0x0F, (byte)0x49, (byte)0x40,//ComplexLSB8
                      (byte)0x40, (byte)0x09, (byte)0x21, (byte)0xFF, (byte)0x2E, (byte)0x48, (byte)0xE8, (byte)0xA7,//ComplexMSB16
                      (byte)0x40, (byte)0x09, (byte)0x21, (byte)0xFF, (byte)0x2E, (byte)0x48, (byte)0xE8, (byte)0xA7,
                      (byte)0x40, (byte)0x49, (byte)0x0F, (byte)0xF9, (byte)0x40, (byte)0x49, (byte)0x0F, (byte)0xF9,//ComplexMSB8
                      (byte)0xFF, (byte)0xFF, (byte)0xFF,//SignedBitString -3B
                      (byte)0xFF, (byte)0xFF, (byte)0xFF,//UnsignedBitString -3B
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//SignedBitString - 4B
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//UnsignedBitString - 4B
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//SignedMSB8
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, //SignedMSB4
                      (byte)0xFF, (byte)0xFF,//SignedMSB2
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//UnignedMSB8
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//UnsignedMSB4
                      (byte)0xFF, (byte)0xFF,//UnsignedMSB2
                      (byte)0xFF,//UnsignedByte
                      (byte)0xFF, (byte)0xFF, //UnsignedLSB2
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//UnsignedLSB4
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//UnignedLSB8
                      (byte)0xFF,//SignedByte
                      (byte)0xFF, (byte)0xFF,//SignedLSB2
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//SignedLSB4
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, //SignedLSB8
    };           

    //Fields and GieldBroups test with 1 and 2 repetitions for 1st FieldAreaObservational
    //XML:  Table_Binary_Multiple.xml
    //DATA: binary_fieldAndGroups_FAO_1.bin
    byte[] binary_fieldsAndGroups_FAO_1 = {  
                      (byte)0x66, (byte)0x61, (byte)0x6c, (byte)0x73, (byte)0x65,//ASCII_BOOLEAN - false
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//SignedLSB4
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//SignedLSB8
                      (byte)0xFF, (byte)0xFF,//Group level 1 - UnsignedByte - repetitions 2
                      (byte)0xFF, (byte)0xFF,//SignedMSB2
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//UnsignedMSB2 - repetitions 2
    };    

    //Fields and GieldBroups test with 1 and 2 repetitions for 2nd FieldAreaObservational
    //XML:  Table_Binary_Multiple.xml
    //DATA: binary_fieldAndGroups_FAO_1
    byte[] binary_fieldsAndGroups_FAO_2 = {  
                      (byte)0x66, (byte)0x61, (byte)0x6c, (byte)0x73, (byte)0x65,//ASCII_BOOLEAN - false
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//SignedLSB4
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//SignedLSB8
                      (byte)0x00, (byte)0x00,//Group level 1 - UnsignedByte - repeat twice
                      (byte)0x00, (byte)0x00,//SignedMSB2
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//UnsignedMSB2 - repeated twice
    };   

    //Fields and GieldBroups test with 1 and 2 repetitions for 1 FieldAreaObservationalSupplemental
    //XML:  Table_Binary_Multiple.xml
    //DATA: binary_fieldsAndGroups_FAOS1.bin
    byte[] binary_fieldsAndGroups_FAOS1 = {  
                      
                      (byte)0x66, (byte)0x61, (byte)0x6c, (byte)0x73, (byte)0x65,//ASCII_BOOLEAN - false
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//SignedLSB4
                      (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0xFF,//SignedLSB8
                      (byte)0x00, (byte)0xFF,//Group level 1 - UnsignedByte - repeat twice
                      (byte)0x00, (byte)0xFF,//SignedMSB2
                      (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0xFF,//UnsignedMSB2 - repeated twice
    };   

    //This tests two tables in a single File_Area_Observationa, starting at a different offset
    //XML:  Table_Binary_Multiple.xml
    //DATA: binary_oneFileTwoTables.bin
     byte[] binary_oneFileTwoTables = {  
                      //Table 1
                      (byte)0x66, (byte)0x61, (byte)0x6c, (byte)0x73, (byte)0x65,//ASCII_BOOLEAN - false
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//SignedLSB4
                      (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0xFF,//SignedLSB8
                      (byte)0x00, (byte)0xFF,//Group level 1 - UnsignedByte - repeat twice
                      (byte)0x00, (byte)0xFF,//SignedMSB2
                      (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0xFF,//UnsignedMSB2 - repeated twice

                      //Table 2
                      (byte)0x66, (byte)0x61, (byte)0x6c, (byte)0x73, (byte)0x65,//ASCII_BOOLEAN - false
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//SignedLSB4
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//SignedLSB8
                      (byte)0x00, (byte)0x00,//Group level 1 - UnsignedByte - repeat twice
                      (byte)0x00, (byte)0x00,//SignedMSB2
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//UnsignedMSB2 - repeated twice
    };   



  String binaryAllData = "66616c7365" //ASCII_Boolean = false - 5B
                         + "74727565" //ASCII_Boolean = true - 4B
                         + "323031342d30392d3234" //ASCII_Date = 2014-09-24 - 10B
                         + "32303134" //ASCII_Day_DOY = 2014 - 4B
                         + "323031342d30392d3234" //ASCII_Date_YMD = 2014-09-24 - 10B
                         + "323031342d30392d32345430393a3038"//ASCII_Date_Time = 2014-09-24 -16B
                         + ""//ASCII_Date_Time_DOY
                         + ""//ASCII_Date_Time_UTC
                         + ""//ASCII_Date_Time_YMD 
                         + ""//ASCII_Time
                         + ""//ASCII_Integer 
                         + ""//ASCII_NonNegative_Integer 
                         + ""//ASCII_Real 
                         + ""//ASCII_Numeric_Base2
                         + ""//ASCII_Numeric_Base8
                         + ""//ASCII_Numeric_Base16 
                         + ""//MD5_Checksum
                         + ""//ASCII_AnyURI
                         + ""//ASCII_DOI
                         + ""//ASCII_LID
                         + ""//ASCII_LIDVID
                         + ""//ASCII_LIDVID_LID
                         + ""//ASCII_VID
                         + ""//ASCII_Directory_Path_Name
                         + ""//ASCII_File_Name
                         + ""//ASCII_File_Specification_Name
                         + ""//ASCII_Short_String_Collapsed
                         + ""//ASCII_Short_String_Preserved
                         + ""//ASCII_Text_Collapsed
                         + ""//ASCII_Text_Preserved
                         + ""//UTF8_Short_String_Collapsed
                         + ""//UTF8_Short_String_Preserved
                         + ""//UTF8_Text_Collapsed
                         + ""//UTF8_Text_Preserved
                         + ""//ASCII_AnyURI
                         + ""//ASCII_DOI
                         + ""//ASCII_File_Name
                         + ""//ASCII_File_Specification_Name
                         + ""//ASCII_LID
                         + ""//ASCII_LIDVID
                         + ""//ASCII_VID
                         + ""//ASCII_String
                         + ""//UTF8_String

                         + "FFFFFFFFFFFFFFFF"//SignedLSB8 = -1 - 8B
                         + "FFFFFFFF"//SignedLSB4 = -1 - 4B
                         + "FFFF"//SignedLSB2 = -1 - 2B
                         + "FF"//SignedByte = -1 = 1B
                         + "FFFFFFFFFFFFFFFF"//UnsignedLSB8 = 18446744073709551615 - 8B
                         + "FFFFFFFF"//UnsignedLSB4 = 4294967295 - 4B
                         + "FFFF"//UnsignedLSB2 = 65535 - 2B
                         + "FF"//UnsignedByte = 255 - 1B
                         + "FFFFFFFFFFFFFFFF"//SignedMSB8 = -1 - 8B
                         + "FFFFFFFF"//SignedMSB4 = -1 - 4B
                         + "FFFF"//SignedMSB2 = -1 - 2B
                         + "FFFFFFFFFFFFFFFF"//UnsignedMSB8 = 18446744073709551615 - 8B
                         + "FFFFFFFF"//UnsignedMSB4 = 4294967295 - 4B
                         + "FFFF"//UnsignedMSB2 = 65535 - 2B
                         + "FFFFFFFFFFFFFFFF"//IEEE754LSBDouble = NaN - 8B
                         + "FFFFFFFF"//IEEE754LSBSingle = NaN - 4B
                         + "FFFFFFFFFFFFFFFF"//IEEE754MSBDouble = NaN - 8B
                         + "FFFFFFFF"//IEEE754MSBSingle = NaN - 4B
                         + "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"//ComplexLSB16 = NaN, NaN - 16B
                         + "FFFFFFFFFFFFFFFF"//ComplexLSB8 = NaN - 8B
                         + "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"//ComplexMSB16 = NaN, NaN - 16B
                         + "FFFFFFFFFFFFFFFF"//ComplexMSB8 = NaN - 8B
                         + "FFFF"//SignedBitString - 4B
                         + "FFFF";//UnsignedBitString - 4B



//------------------------------------------------------------------------------------------
String binDataTypes_row1 = "FFFFFFFFFFFFFFFF"//SignedLSB8 = -1 - 8B
                         + "FFFFFFFF"//SignedLSB4 = -1 - 4B
                         + "FFFF"//SignedLSB2 = -1 - 2B
                         + "FF"//SignedByte = -1 = 1B
                         + "FFFFFFFFFFFFFFFF"//UnsignedLSB8 = 18446744073709551615 - 8B
                         + "FFFFFFFF"//UnsignedLSB4 = 4294967295 - 4B
                         + "FFFF"//UnsignedLSB2 = 65535 - 2B
                         + "FF"//UnsignedByte = 255 - 1B
                         + "FFFFFFFFFFFFFFFF"//SignedMSB8 = -1 - 8B
                         + "FFFFFFFF"//SignedMSB4 = -1 - 4B
                         + "FFFF"//SignedMSB2 = -1 - 2B
                         + "FFFFFFFFFFFFFFFF"//UnsignedMSB8 = 18446744073709551615 - 8B
                         + "FFFFFFFF"//UnsignedMSB4 = 4294967295 - 4B
                         + "FFFF"//UnsignedMSB2 = 65535 - 2B
                         + "FFFFFFFFFFFFFFFF"//IEEE754LSBDouble = NaN - 8B
                         + "FFFFFFFF"//IEEE754LSBSingle = NaN - 4B
                         + "FFFFFFFFFFFFFFFF"//IEEE754MSBDouble = NaN - 8B
                         + "FFFFFFFF"//IEEE754MSBSingle = NaN - 4B
                         + "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"//ComplexLSB16 = NaN, NaN - 16B
                         + "FFFFFFFFFFFFFFFF"//ComplexLSB8 = NaN - 8B
                         + "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"//ComplexMSB16 = NaN, NaN - 16B
                         + "FFFFFFFFFFFFFFFF"//ComplexMSB8 = NaN - 8B
                         + "FFFFFFFF"//SignedBitString - 4B
                         + "FFFFFFFF";//UnsignedBitString - 4B

byte[] binDataTypesBytes_row1 = DatatypeConverter.parseHexBinary(binDataTypes_row1);
//------------------------------------------------------------------------------------------

String binDataTypes_row2 = "0000000000000000"//SignedLSB8 = -1 - 8B
                         + "00000000"//SignedLSB4 = -1 - 4B
                         + "0000"//SignedLSB2 = -1 - 2B
                         + "00"//SignedByte = -1 = 1B
                         + "0000000000000000"//UnsignedLSB8 = 18446744073709551615 - 8B
                         + "00000000"//UnsignedLSB4 = 4294967295 - 4B
                         + "0000"//UnsignedLSB2 = 65535 - 2B
                         + "00"//UnsignedByte = 255 - 1B
                         + "0000000000000000"//SignedMSB8 = -1 - 8B
                         + "00000000"//SignedMSB4 = -1 - 4B
                         + "0000"//SignedMSB2 = -1 - 2B
                         + "0000000000000000"//UnsignedMSB8 = 18446744073709551615 - 8B
                         + "00000000"//UnsignedMSB4 = 4294967295 - 4B
                         + "0000"//UnsignedMSB2 = 65535 - 2B
                         + "0000000000000000"//IEEE754LSBDouble = NaN - 8B
                         + "00000000"//IEEE754LSBSingle = NaN - 4B
                         + "0000000000000000"//IEEE754MSBDouble = NaN - 8B
                         + "00000000"//IEEE754MSBSingle = NaN - 4B
                         + "00000000000000000000000000000000"//ComplexLSB16 = NaN, NaN - 16B
                         + "0000000000000000"//ComplexLSB8 = NaN - 8B
                         + "00000000000000000000000000000000"//ComplexMSB16 = NaN, NaN - 16B
                         + "0000000000000000"//ComplexMSB8 = NaN - 8B
                         + "00000000"//SignedBitString - 4B
                         + "00000000";//UnsignedBitString - 4B

byte[] binDataTypesBytes_row2 = DatatypeConverter.parseHexBinary(binDataTypes_row2);

//------------------------------------------------------------------------------------------

String binDataTypes_row3 = "FFFFFFFF00000000"//SignedLSB8 = -1 - 8B
                         + "FFFF0000"//SignedLSB4 = -1 - 4B
                         + "FF00"//SignedLSB2 = -1 - 2B
                         + "F0"//SignedByte = -1 = 1B
                         + "FFFFFFFF00000000"//UnsignedLSB8 = 18446744073709551615 - 8B
                         + "FFFF0000"//UnsignedLSB4 = 4294967295 - 4B
                         + "FF00"//UnsignedLSB2 = 65535 - 2B
                         + "F0"//UnsignedByte = 255 - 1B
                         + "FFFFFFFF00000000"//SignedMSB8 = -1 - 8B
                         + "FFFF0000"//SignedMSB4 = -1 - 4B
                         + "FF00"//SignedMSB2 = -1 - 2B
                         + "FFFFFFFF00000000"//UnsignedMSB8 = 18446744073709551615 - 8B
                         + "FFFF0000"//UnsignedMSB4 = 4294967295 - 4B
                         + "FF00"//UnsignedMSB2 = 65535 - 2B
                         + "FFFFFFFF00000000"//IEEE754LSBDouble = NaN - 8B
                         + "FFFF0000"//IEEE754LSBSingle = NaN - 4B
                         + "FFFFFFFF00000000"//IEEE754MSBDouble = NaN - 8B
                         + "FFFF0000"//IEEE754MSBSingle = NaN - 4B
                         + "FFFFFFFF00000000FFFFFFFF00000000"//ComplexLSB16 = NaN, NaN - 16B
                         + "FFFF0000FFFF0000"//ComplexLSB8 = NaN - 8B
                         + "FFFFFFFF00000000FFFFFFFF00000000"//ComplexMSB16 = NaN, NaN - 16B
                         + "FFFF0000FFFF0000"//ComplexMSB8 = NaN - 8B
                         + "FFFF0000"//SignedBitString - 4B
                         + "FFFF0000";//UnsignedBitString - 4B

byte[] binDataTypesBytes_row3 = DatatypeConverter.parseHexBinary(binDataTypes_row3);

//------------------------------------------------------------------------------------------

String binDataTypes_row4 = "FFFFFFFF00000000"//SignedLSB8 = -1 - 8B
                         + "FFFF0000"//SignedLSB4 = -1 - 4B
                         + "FF00"//SignedLSB2 = -1 - 2B
                         + "F0"//SignedByte = -1 = 1B
                         + "FFFFFFFF00000000"//UnsignedLSB8 = 18446744073709551615 - 8B
                         + "FFFF0000"//UnsignedLSB4 = 4294967295 - 4B
                         + "FF00"//UnsignedLSB2 = 65535 - 2B
                         + "F0"//UnsignedByte = 255 - 1B
                         + "FFFFFFFF00000000"//SignedMSB8 = -1 - 8B
                         + "FFFF0000"//SignedMSB4 = -1 - 4B
                         + "FF00"//SignedMSB2 = -1 - 2B
                         + "FFFFFFFF00000000"//UnsignedMSB8 = 18446744073709551615 - 8B
                         + "FFFF0000"//UnsignedMSB4 = 4294967295 - 4B
                         + "FF00"//UnsignedMSB2 = 65535 - 2B
                         + "FFFFFFFF00000000"//IEEE754LSBDouble = NaN - 8B
                         + "FFFF0000"//IEEE754LSBSingle = NaN - 4B
                         + "FFFFFFFF00000000"//IEEE754MSBDouble = NaN - 8B
                         + "FFFF0000"//IEEE754MSBSingle = NaN - 4B
                         + "FFFFFFFF00000000FFFFFFFF00000000"//ComplexLSB16 = NaN, NaN - 16B
                         + "FFFF0000FFFF0000"//ComplexLSB8 = NaN - 8B
                         + "FFFFFFFF00000000FFFFFFFF00000000"//ComplexMSB16 = NaN, NaN - 16B
                         + "FFFF0000FFFF0000"//ComplexMSB8 = NaN - 8B
                         + "FFFF0000"//SignedBitString - 4B
                         + "FFFF0000";//UnsignedBitString - 4B

byte[] binDataTypesBytes_row4 = DatatypeConverter.parseHexBinary(binDataTypes_row4);

//------------------------------------------------------------------------------------------


    if (args[1].equalsIgnoreCase("all")) {

        labelBinaryData.writeDataToFile(FILE_PATH+listOfFileNames[0],binary_fields_only);
        labelBinaryData.writeDataToFile(FILE_PATH+listOfFileNames[1],binary_fieldsAndGroups_FAO_1);
        labelBinaryData.writeDataToFile(FILE_PATH+listOfFileNames[2],binary_fieldsAndGroups_FAO_2);
        labelBinaryData.writeDataToFile(FILE_PATH+listOfFileNames[3],binary_fieldsAndGroups_FAOS1);
        labelBinaryData.writeDataToFile(FILE_PATH+listOfFileNames[4],binary_oneFileTwoTables);
       
        // save the data    
        if(labelBinaryData.openForWritting(FILE_PATH+listOfFileNames[5])) {
                System.out.println("File opened for Writing");
    
                //Write array bytes
                int totalNumberOfRows = 10000;
                for(int i=0; i<totalNumberOfRows; i++) {
                    labelBinaryData.writeToFile(binary_fields_only);
                }
                //close outputstream
                labelBinaryData.closeFile();

                //Read File contents
                byte[] fileData = labelBinaryData.read(FILE_PATH+listOfFileNames[5]);

                if(fileData != null)
                  System.out.println("Row bytes: " + fileData.length);
                else
                  System.out.println("There was an error reading the file.");

        } else {
                System.out.println("Not able to open the file for Writing");
        }


        //save rows of data
        if(labelBinaryData.openForWritting(FILE_PATH+listOfFileNames[6])) {
                System.out.println("File opened for Writing");
                System.out.println("\n\n[6] Saving multiple rows of binary data:");
                System.out.println("Saving 3 rows");
              
                labelBinaryData.writeToFile(binDataTypesBytes_row1);//row1
                labelBinaryData.writeToFile(binDataTypesBytes_row2);//row2
                labelBinaryData.writeToFile(binDataTypesBytes_row3);//row3

                //close outputstream
                labelBinaryData.closeFile();

                //Read File contents
                byte[] fileData = labelBinaryData.read(FILE_PATH+listOfFileNames[6]);

                if(fileData != null)
                  System.out.println("Row bytes: " + fileData.length);
                else
                  System.out.println("There was an error reading the file.");

        } else {
                System.out.println("Not able to open the file for Writing");
        }




    } else {

      System.out.println("Need to provide argument");

    }

  }
  
  public void writeDataToFile(String filePath, byte[] bytesToSave) {

    if(labelBinaryData.openForWritting(filePath)) {
      System.out.println("File opened for Writing");
    
      //Write array bytes
      labelBinaryData.writeToFile(bytesToSave);

      //close outputstream
      labelBinaryData.closeFile();

      //Read File contents
      byte[] fileData = labelBinaryData.read(filePath);

      if(fileData != null)
        System.out.println("Row bytes: " + fileData.length);
      else
        System.out.println("There was an error reading the file.");

    } else {
      System.out.println("Not able to open the file for Writing");
    }
  }

  /**
  * Read binary file contents
  *
  * @param inputFileName - Name of binary file to open
  * @return byte array with the contents of the file 
  */
  public byte[] read(String inputFileName){

    System.out.println("Reading binary file: " + inputFileName);
    
    File file = new File(inputFileName);
    byte[] dataRead = new byte[(int)file.length()];
    InputStream input = null;

    try {

        int totalBytesRead = 0;
        input = new BufferedInputStream(new FileInputStream(file));

         while(totalBytesRead < dataRead.length){
              
            int bytesRead = input.read(dataRead, totalBytesRead, dataRead.length - totalBytesRead); 

            if (bytesRead > 0){
                totalBytesRead = totalBytesRead + bytesRead;
            }
        }
            
        input.close();

    }catch (IOException ex) {
        System.out.println("There was an error reading the file. Error: " + ex);
        return null;
    }
  
    return dataRead;
  }
  
  /**
  * Open file for writting
  *
  * @param outputFileName - Name of file to write to
  * @return boolean true if the file was opened successfully for writing. False otherwise
  */
  public boolean openForWritting(String outputFileName) {
    boolean status = false;

     try {
        output = new BufferedOutputStream(new FileOutputStream(outputFileName));
        status = true;
      }
      catch(FileNotFoundException ex){
        System.out.println("File not found.");
      }

    return status;
  }

  /**
  * Close file stream
  *
  * @param None
  * @return boolean true if the outputstream was closed successfully
  */
  public boolean closeFile() {

    boolean status = false;

    try {
        output.close();
        status = true;
    } catch(FileNotFoundException ex){
        System.out.println("File not found.");
    } catch(IOException ex){
        System.out.println(ex);
    }

    return status;
  }

  /**
   * Write to File
   *
   * @param bytesToWrite - Byte array that contains all the bytes to write to a file
   * @return boolean true if there were no error writing to the file
   */
	public boolean writeToFile(byte[] bytesToWrite) {
	
	  boolean status = false;
	
	  try {
	        output.write(bytesToWrite);
	        status = true;
	  }catch(IOException ex){
	      System.out.println(ex);
	  }
	    return status;
	}

} 
