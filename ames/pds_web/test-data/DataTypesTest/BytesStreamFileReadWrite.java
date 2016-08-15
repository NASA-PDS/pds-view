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

/*********************************************************************************************************

Author: German H. Flores

Filename: BytesStreamFileReadWrite.java

Output FileName: binary.dat

Date: 9/16/2013

This class has been writen to test all of the following data types in PDS3:

              CHARACTER
              EBCDIC-CHARACTER
              BOOLEAN
              LSB_BIT_STRING
              VAX_BIT_STRING
              MSB_BIT_STRING
              BIT_STRING
              LSB_INTEGER
              VAX_INTEGER
              PC_INTEGER
              MSB_INTEGER
              IBM_INTEGER
              MAC_INTEGER
              SUN_INTEGER
              INTEGER
              IBM_UNSIGNED_INTEGER
              MAC_UNSIGNED_INTEGER
              MSB_UNSIGNED_INTEGER
              SUN_UNSIGNED_INTEGER
              UNSIGNED_INTEGER
              LSB_UNSIGNED_INTEGER
              PC_UNSIGNED_INTEGER
              VAX_UNSIGNED_INTEGER
              IEEE_REAL
              MAC_REAL
              SUN_REAL
              FLOAT
              REAL
              PC_REAL
              VAX_REAL
              VAX_DOUBLE
              VAXG_REAL
              IEEE_COMPLEX
              MAC_COMPLEX
              SUN_COMPLEX
              COMPLEX
              PC_COMPLEX
              VAX_COMPLEX
              VAXG_COMPLEX

NOTES:

--------------------------------------------------------------------------------------------------------------
                                                  VAX TESTING
--------------------------------------------------------------------------------------------------------------
To test the conversion from VAX to IEEE, the following library results was used to compare the results 
from my code to the results from this library. The same data was used

U.S. Geological Survey
Open-File Report 2005-1424
Version 1.2
libvaxdata: VAX Data Format Conversion Routines
Link: http://pubs.usgs.gov/of/2005/1424/

Results from above library conversion tool:

VAX 4-byte F-type to IEEE 4-byte:

              1
             -1
            3.5
           -3.5
        3.14159
       -3.14159
          1e+37
         -1e+37
          1e-37
         -1e-37
        1.23457
       -1.23457

VAX 8-byte D-Type to IEEE 8-bytes

                        1
                       -1
                      3.5
                     -3.5
         3.14159265358979
        -3.14159265358979
                    1e+37
                   -1e+37
                    1e-37
                   -1e-37
         1.23456789012345
        -1.23456789012345

VAX 8-byte G-Type to IEEE 8-bytes

                        1
                       -1
                      3.5
                     -3.5
         3.14159265358979
        -3.14159265358979
                    1e+37
                   -1e+37
                    1e-37
                   -1e-37
         1.23456789012345
        -1.23456789012345


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


public class BytesStreamFileReadWrite {

  private static final String FILE_NAME = "binary.dat";
  OutputStream output = null;

  public static void main(String[] args) {

    BytesStreamFileReadWrite labelBinaryData = new BytesStreamFileReadWrite();

    //Upper Limit tests
    byte[] row_1 = {  
                      //CHARACTER
                      (byte)0x41,

                      //EBCDIC-CHARACTER
                      (byte)0xC1,

                      //BOOLEAN
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01,//BOOLEAN 4-BYTE INTEGER
                      (byte)0x00, (byte)0x01,//BOOLEAN 2-Bytes
                      (byte)0x01,//BOOLEAN 1 Byte

                      //LSB_BIT_STRING
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//LSB_BIT_STRING 4-bytes
                      (byte)0xFF, (byte)0xFF,//LSB_BIT_STRING 2-byte. Using Alias VAX_BIT_STRING
                      (byte)0xFF,//LSB_BIT_STRING 1-byte. 

                      //MSB_BIT_STRING
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//MSB_BIT_STRING 4-byte
                      (byte)0xFF, (byte)0xFF,//MSB_BIT_STRING 2-byte
                      (byte)0xFF,//MSB_BIT_STRING 1-byte. Using Alias BIT_STRING

                      //LSB_INTEGER
                      (byte)0xFF,//LSB_INTEGER 1-byte. 
                      (byte)0xFF, (byte)0xFF,//LSB_INTGER 2-byte. Using Alias VAX_INTEGER
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//LSB_INTEGER 4 Bytes. Using Alias PC INTEGER

                      //MSB_INTEGER
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//MSB_INTEGER 4-bytes
                      (byte)0xFF, (byte)0xFF,//MSB_INTEGER 2-byte. Using Alias IBM_INTEGER
                      (byte)0xFF,//MSB_INTEGER 1-byte. Using Alias MAC_INTEGER
                      (byte)0xFF, (byte)0xFF,//MSB_INTEGER 4-bytes. Using Alias SUN_INTEGER
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//MSB_INTEGER 4-bytes. Using Alias INTEGER

                      //IEEE_REAL - Testing +Infinity
                      (byte)0x7F, (byte)0x80, (byte)0x00, (byte)0x00,//IEEE FLOAT 4 Bytes
                      (byte)0x7F, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//IEEE DOUBLE 8 Bytes. Using alias MAC_REAL
                      (byte)0x7F, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//IEEE DOUBLE 8 bytes. Using alias SUN_REAL
                      (byte)0x7F, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//IEEE DOUBLE 8 bytes. Using alias FLOAT
                      (byte)0x7F, (byte)0x80, (byte)0x00, (byte)0x00,//IEEE FLOAT 4 Bytes. Using alias REAL

                      //VAX_REAL F, D and G Types
                      (byte)0x80, (byte)0x7F, (byte)0x00, (byte)0x00,//VAX_REAL F-Type - 4 bytes
                      (byte)0x80, (byte)0x7F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//VAX_REAL D-Type - 8 bytes
                      (byte)0x80, (byte)0x7F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//VAXG_REAL G-Type - 8 bytes
                      
                      //MSB_UNSINGED_INTEGER
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//MSB_UNSIGNED_INTEGER 4-bytes
                      (byte)0xFF, (byte)0xFF,//MSB_UNSIGNED_INTEGER 2-byte. Using Alias IBM_INTEGER
                      (byte)0xFF,//MSB_UNSIGNED_INTEGER 1-byte. Using Alias MAC_INTEGER
                      (byte)0xFF, (byte)0xFF,//MSB_UNSIGNED_INTEGER 2-bytes. Using Alias SUN_INTEGER
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//MSB_UNSIGNED_INTEGER 4-bytes. Using Alias INTEGER

                      //LSB_UNSIGNED_INTEGER
                      (byte)0xFF,//LSB_UNSIGNED_INTEGER 1-byte. 
                      (byte)0xFF, (byte)0xFF,//LSB_UNSIGNED_INTGER 2-byte. Using Alias VAX_UNSIGNED_INTEGER
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//LSB_UNSIGNED_INTEGER 4 Bytes. Using Alias PC_UNSIGNED_INTEGER

                      //IEEE_COMPLEX
                      (byte)0x7F, (byte)0x80, (byte)0x00, (byte)0x00, (byte)0x7F, (byte)0x80, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 8 bytes
                      (byte)0x7F, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 16 bytes. Using alias MAC_COMPLEX
                      (byte)0x7F, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                      (byte)0x7F, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 16 bytes. Using alias SUN_COMPLEX
                      (byte)0x7F, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                      (byte)0x7F, (byte)0x80, (byte)0x00, (byte)0x00, (byte)0x7F, (byte)0x80, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 8 bytes. Using alias COMPLEX

                      //VAX_COMPLEX
                      (byte)0x80, (byte)0x7F, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0x7F, (byte)0x00, (byte)0x00,//VAX_COMPLEX F-type 8-bytes
                      (byte)0x80, (byte)0x7F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//VAX_COMPLEX D-type 16 bytes
                      (byte)0x80, (byte)0x7F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                      (byte)0x80, (byte)0x7F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//VAX_COMPLEX G-type 16 bytes
                      (byte)0x80, (byte)0x7F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                      
                      //PC_REAL 4 and 8 bytes
                      (byte)0x80, (byte)0x7F, (byte)0x00, (byte)0x00,//PC_REAL - 4 bytes
                      (byte)0x80, (byte)0x7F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//PC_REAL - 8 bytes

                      //PC_COMPLEX
                      (byte)0x80, (byte)0x7F, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0x7F, (byte)0x00, (byte)0x00,//PC_COMPLEX 8-bytes
                      (byte)0x80, (byte)0x7F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//PC_COMPLEX 16 bytes
                      (byte)0x80, (byte)0x7F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,

                      //ITEMS
                      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//ITEMS
                      
                      //CONTAINER. First 4 bytes are VAX F-Type, Next 4 bytes are MSB_INTEGER, etc...
                      (byte)0x7F, (byte)0x80, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,//Repetition 1 - VAX_REALs F-Type
                      (byte)0x7F, (byte)0x80, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};//Repetition 2 - VAX_REALs F-Type
                      

    //Lower Limit tests
    byte[] row_2 = {  
                      //CHARACTER
                      (byte)0x42,

                      //EBCDIC-CHARACTER
                      (byte)0xC2,

                      //BOOLEAN
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//BOOLEAN 4-BYTE INTEGER
                      (byte)0x00, (byte)0x00,//BOOLEAN 2-Bytes
                      (byte)0x00,//BOOLEAN 1 Byte

                      //LSB_BIT_STRING
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//LSB_BIT_STRING 4-bytes
                      (byte)0x00, (byte)0x00,//LSB_BIT_STRING 2-byte. Using Alias VAX_BIT_STRING
                      (byte)0x00,//LSB_BIT_STRING 1-byte. 

                      //MSB_BIT_STRING
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//MSB_BIT_STRING 4-byte
                      (byte)0x00, (byte)0x00,//MSB_BIT_STRING 2-byte
                      (byte)0x00,//MSB_BIT_STRING 1-byte. Using Alias BIT_STRING

                      //LSB_INTEGER
                      (byte)0x00,//LSB_INTEGER 1-byte. 
                      (byte)0x00, (byte)0x00,//LSB_INTGER 2-byte. Using Alias VAX_INTEGER
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//LSB_INTEGER 4 Bytes. Using Alias PC INTEGER

                      //MSB_INTEGER
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//MSB_INTEGER 4-bytes
                      (byte)0x00, (byte)0x00,//MSB_INTEGER 2-byte. Using Alias IBM_INTEGER
                      (byte)0x00,//MSB_INTEGER 1-byte. Using Alias MAC_INTEGER
                      (byte)0x00, (byte)0x00,//MSB_INTEGER 4-bytes. Using Alias SUN_INTEGER
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//MSB_INTEGER 4-bytes. Using Alias INTEGER

                      //IEEE_REAL - Testing +Infinity
                      (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00,//IEEE FLOAT 4 Bytes
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//IEEE DOUBLE 8 Bytes. Using alias MAC_REAL
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//IEEE DOUBLE 8 bytes. Using alias SUN_REAL
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//IEEE DOUBLE 8 bytes. Using alias FLOAT
                      (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00,//IEEE FLOAT 4 Bytes. Using alias REAL

                      //VAX_REAL F, D and G Types
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00,//VAX_REAL F-Type - 4 bytes
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//VAX_REAL D-Type - 8 bytes
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//VAXG_REAL G-Type - 8 bytes
                      
                      //MSB_UNSINGED_INTEGER
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//MSB_UNSIGNED_INTEGER 4-bytes
                      (byte)0x00, (byte)0x00,//MSB_UNSIGNED_INTEGER 2-byte. Using Alias IBM_INTEGER
                      (byte)0x00,//MSB_UNSIGNED_INTEGER 1-byte. Using Alias MAC_INTEGER
                      (byte)0x00, (byte)0x00,//MSB_UNSIGNED_INTEGER 2-bytes. Using Alias SUN_INTEGER
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//MSB_UNSIGNED_INTEGER 4-bytes. Using Alias INTEGER

                      //LSB_UNSIGNED_INTEGER
                      (byte)0x00,//LSB_UNSIGNED_INTEGER 1-byte. 
                      (byte)0x00, (byte)0x00,//LSB_UNSIGNED_INTGER 2-byte. Using Alias VAX_UNSIGNED_INTEGER
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//LSB_UNSIGNED_INTEGER 4 Bytes. Using Alias PC_UNSIGNED_INTEGER

                      //IEEE_COMPLEX
                      (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 8 bytes
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 16 bytes. Using alias MAC_COMPLEX
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 16 bytes. Using alias SUN_COMPLEX
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                      (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 8 bytes. Using alias COMPLEX

                      //VAX_COMPLEX
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00,//VAX_COMPLEX F-type 8-bytes
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//VAX_COMPLEX D-type 16 bytes
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//VAX_COMPLEX G-type 16 bytes
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                      
                      //PC_REAL 4 and 8 bytes
                      (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF,//PC_REAL - 4 bytes
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF,//PC_REAL - 8 bytes

                      //PC_COMPLEX
                      (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF,//PC_COMPLEX 8-bytes
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF,//PC_COMPLEX 16 bytes
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF,

                      //ITEMS
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//ITEMS
                      
                      //CONTAINER. First 4 bytes are VAX F-Type, Next 4 bytes are MSB_INTEGER, etc...
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//Repetition 1 - VAX_REALs F-Type
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};//Repetition 2 - MSB_INTEGER
    

    //Other special values
    byte[] row_3 = {  
                      //CHARACTER
                      (byte)0x43,

                      //EBCDIC-CHARACTER
                      (byte)0xC3,

                      //BOOLEAN
                      (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x01,//BOOLEAN 4-BYTE INTEGER
                      (byte)0x00, (byte)0x00,//BOOLEAN 2-Bytes
                      (byte)0xA0,//BOOLEAN 1 Byte

                      //LSB_BIT_STRING
                      (byte)0x00, (byte)0xAA, (byte)0x00, (byte)0xFF,//LSB_BIT_STRING 4-bytes
                      (byte)0x00, (byte)0xAA,//LSB_BIT_STRING 2-byte. Using Alias VAX_BIT_STRING
                      (byte)0x0F,//LSB_BIT_STRING 1-byte. 

                      //MSB_BIT_STRING
                      (byte)0xFF, (byte)0x00, (byte)0xFF, (byte)0x00,//MSB_BIT_STRING 4-byte
                      (byte)0xFF, (byte)0x00,//MSB_BIT_STRING 2-byte
                      (byte)0xFF,//MSB_BIT_STRING 1-byte. Using Alias BIT_STRING

                      //LSB_INTEGER
                      (byte)0x0F,//LSB_INTEGER 1-byte. 
                      (byte)0x00, (byte)0xFF,//LSB_INTGER 2-byte. Using Alias VAX_INTEGER
                      (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0xFF,//LSB_INTEGER 4 Bytes. Using Alias PC INTEGER

                      //MSB_INTEGER
                      (byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00,//MSB_INTEGER 4-bytes
                      (byte)0xFF, (byte)0x00,//MSB_INTEGER 2-byte. Using Alias IBM_INTEGER
                      (byte)0xFF,//MSB_INTEGER 1-byte. Using Alias MAC_INTEGER
                      (byte)0xFF, (byte)0x00,//MSB_INTEGER 4-bytes. Using Alias SUN_INTEGER
                      (byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00,//MSB_INTEGER 4-bytes. Using Alias INTEGER

                      //IEEE_REAL - Testing +Infinity
                      (byte)0x7F, (byte)0x80, (byte)0x00, (byte)0x01,//IEEE FLOAT 4 Bytes
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01,//IEEE DOUBLE 8 Bytes. Using alias MAC_REAL
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01,//IEEE DOUBLE 8 bytes. Using alias SUN_REAL
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01,//IEEE DOUBLE 8 bytes. Using alias FLOAT
                      (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00,//IEEE FLOAT 4 Bytes. Using alias REAL

                      //VAX_REAL F, D and G Types
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00,//VAX_REAL F-Type - 4 bytes
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//VAX_REAL D-Type - 8 bytes
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//VAXG_REAL G-Type - 8 bytes
                      
                      //MSB_UNSINGED_INTEGER
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//MSB_UNSIGNED_INTEGER 4-bytes
                      (byte)0x00, (byte)0x00,//MSB_UNSIGNED_INTEGER 2-byte. Using Alias IBM_INTEGER
                      (byte)0x00,//MSB_UNSIGNED_INTEGER 1-byte. Using Alias MAC_INTEGER
                      (byte)0x00, (byte)0x00,//MSB_UNSIGNED_INTEGER 2-bytes. Using Alias SUN_INTEGER
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//MSB_UNSIGNED_INTEGER 4-bytes. Using Alias INTEGER

                      //LSB_UNSIGNED_INTEGER
                      (byte)0x00,//LSB_UNSIGNED_INTEGER 1-byte. 
                      (byte)0x00, (byte)0x00,//LSB_UNSIGNED_INTGER 2-byte. Using Alias VAX_UNSIGNED_INTEGER
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//LSB_UNSIGNED_INTEGER 4 Bytes. Using Alias PC_UNSIGNED_INTEGER

                      //IEEE_COMPLEX
                      (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 8 bytes
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 16 bytes. Using alias MAC_COMPLEX
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 16 bytes. Using alias SUN_COMPLEX
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                      (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 8 bytes. Using alias COMPLEX

                      //VAX_COMPLEX
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00,//VAX_COMPLEX F-type 8-bytes
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//VAX_COMPLEX D-type 16 bytes
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//VAX_COMPLEX G-type 16 bytes
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                      
                      //PC_REAL 4 and 8 bytes
                      (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF,//PC_REAL - 4 bytes
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF,//PC_REAL - 8 bytes

                      //PC_COMPLEX
                      (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF,//PC_COMPLEX 8-bytes
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF,//PC_COMPLEX 16 bytes
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF,

                      //ITEMS
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//ITEMS
                      
                      //CONTAINER. First 4 bytes are VAX F-Type, Next 4 bytes are MSB_INTEGER, etc...
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//Repetition 1 - VAX_REALs F-Type
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};//Repetition 2 - MSB_INTEGER


    //Random values
    byte[] row_4 = {  
                      //CHARACTER
                      (byte)0x44,

                      //EBCDIC-CHARACTER
                      (byte)0xC4,

                      //BOOLEAN
                      (byte)0xE8, (byte)0x11, (byte)0x00, (byte)0x01,//BOOLEAN 4-BYTE INTEGER
                      (byte)0x33, (byte)0xBB,//BOOLEAN 2-Bytes
                      (byte)0xFF,//BOOLEAN 1 Byte

                      //LSB_BIT_STRING
                      (byte)0x0F, (byte)0xA8, (byte)0x52, (byte)0xF8,//LSB_BIT_STRING 4-bytes
                      (byte)0xA6, (byte)0x32,//LSB_BIT_STRING 2-byte. Using Alias VAX_BIT_STRING
                      (byte)0x19,//LSB_BIT_STRING 1-byte. 

                      //MSB_BIT_STRING
                      (byte)0xFC, (byte)0xD0, (byte)0x45, (byte)0x82,//MSB_BIT_STRING 4-byte
                      (byte)0xFE, (byte)0xEE,//MSB_BIT_STRING 2-byte
                      (byte)0x01,//MSB_BIT_STRING 1-byte. Using Alias BIT_STRING

                      //LSB_INTEGER
                      (byte)0x0F,//LSB_INTEGER 1-byte. 
                      (byte)0x00, (byte)0xFF,//LSB_INTGER 2-byte. Using Alias VAX_INTEGER
                      (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0xFF,//LSB_INTEGER 4 Bytes. Using Alias PC INTEGER

                      //MSB_INTEGER
                      (byte)0x80, (byte)0x10, (byte)0xF4, (byte)0x79,//MSB_INTEGER 4-bytes
                      (byte)0xFF, (byte)0x00,//MSB_INTEGER 2-byte. Using Alias IBM_INTEGER
                      (byte)0xDD,//MSB_INTEGER 1-byte. Using Alias MAC_INTEGER
                      (byte)0xCC, (byte)0x00,//MSB_INTEGER 4-bytes. Using Alias SUN_INTEGER
                      (byte)0x80, (byte)0xAA, (byte)0xBB, (byte)0x14,//MSB_INTEGER 4-bytes. Using Alias INTEGER

                      //IEEE_REAL - Testing +Infinity
                      (byte)0x41, (byte)0x02, (byte)0x20, (byte)0x00,//IEEE FLOAT 4 Bytes
                      (byte)0x3F, (byte)0x7A, (byte)0x36, (byte)0xE2, (byte)0xEB, (byte)0x1C, (byte)0x43, (byte)0x2D,//IEEE DOUBLE 8 Bytes. Using alias MAC_REAL
                      (byte)0x40, (byte)0xDF, (byte)0x40, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//IEEE DOUBLE 8 bytes. Using alias SUN_REAL
                      (byte)0x3D, (byte)0xF5, (byte)0xFD, (byte)0x7F, (byte)0xE1, (byte)0x79, (byte)0x64, (byte)0x95,//IEEE DOUBLE 8 bytes. Using alias FLOAT
                      (byte)0x21, (byte)0x02, (byte)0x21, (byte)0x40,//IEEE FLOAT 4 Bytes. Using alias REAL

                      //VAX_REAL F, D and G Types
                      (byte)0xF0, (byte)0x7D, (byte)0xC2, (byte)0xBD,//VAX_REAL F-Type - 4 bytes
                      (byte)0x9E, (byte)0x40, (byte)0x52, (byte)0x06, (byte)0x26, (byte)0x41, (byte)0x7E, (byte)0xEC,//VAX_REAL D-Type - 8 bytes
                      (byte)0x80, (byte)0x40, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//VAXG_REAL G-Type - 8 bytes
                      
                      //MSB_UNSINGED_INTEGER
                      (byte)0x00, (byte)0x77, (byte)0x00, (byte)0xFF,//MSB_UNSIGNED_INTEGER 4-bytes
                      (byte)0x80, (byte)0x16,//MSB_UNSIGNED_INTEGER 2-byte. Using Alias IBM_INTEGER
                      (byte)0x00,//MSB_UNSIGNED_INTEGER 1-byte. Using Alias MAC_INTEGER
                      (byte)0x15, (byte)0x0A,//MSB_UNSIGNED_INTEGER 2-bytes. Using Alias SUN_INTEGER
                      (byte)0x72, (byte)0x21, (byte)0x00, (byte)0x44,//MSB_UNSIGNED_INTEGER 4-bytes. Using Alias INTEGER

                      //LSB_UNSIGNED_INTEGER
                      (byte)0x80,//LSB_UNSIGNED_INTEGER 1-byte. 
                      (byte)0x40, (byte)0x90,//LSB_UNSIGNED_INTGER 2-byte. Using Alias VAX_UNSIGNED_INTEGER
                      (byte)0xFF, (byte)0x01, (byte)0x00, (byte)0x80,//LSB_UNSIGNED_INTEGER 4 Bytes. Using Alias PC_UNSIGNED_INTEGER

                      //IEEE_COMPLEX
                      (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 8 bytes
                      (byte)0x3D, (byte)0xF5, (byte)0xFD, (byte)0x7F, (byte)0xE1, (byte)0x79, (byte)0x64, (byte)0x95,//IEEE_COMPLEX 16 bytes. Using alias MAC_COMPLEX
                      (byte)0x3D, (byte)0xF5, (byte)0xFD, (byte)0x70, (byte)0xE1, (byte)0x80, (byte)0x64, (byte)0x95,
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 16 bytes. Using alias SUN_COMPLEX
                      (byte)0x3D, (byte)0xF5, (byte)0xFD, (byte)0x00, (byte)0xE1, (byte)0x10, (byte)0x61, (byte)0x95,
                      (byte)0x40, (byte)0x40, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 8 bytes. Using alias COMPLEX

                      //VAX_COMPLEX
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x56,//VAX_COMPLEX F-type 8-bytes
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x32, (byte)0x00, (byte)0x00,//VAX_COMPLEX D-type 16 bytes
                      (byte)0x9E, (byte)0x40, (byte)0x52, (byte)0x06, (byte)0x26, (byte)0x41, (byte)0x7E, (byte)0xEC,
                      (byte)0x9E, (byte)0x40, (byte)0x52, (byte)0x86, (byte)0x26, (byte)0x81, (byte)0x7E, (byte)0xEC,//VAX_COMPLEX G-type 16 bytes
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0x00, (byte)0x00, (byte)0x9A,
                      
                      //PC_REAL 4 and 8 bytes
                      (byte)0xF0, (byte)0x80, (byte)0x80, (byte)0xFF,//PC_REAL - 4 bytes
                      (byte)0x00, (byte)0x4A, (byte)0x32, (byte)0x00, (byte)0xFD, (byte)0x00, (byte)0x10, (byte)0xFF,//PC_REAL - 8 bytes

                      //PC_COMPLEX
                      (byte)0x00, (byte)0x10, (byte)0x80, (byte)0xFF, (byte)0x32, (byte)0x00, (byte)0x80, (byte)0xFF,//PC_COMPLEX 8-bytes
                      (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x16, (byte)0x00, (byte)0x00, (byte)0x50, (byte)0xDF,//PC_COMPLEX 16 bytes
                      (byte)0x00, (byte)0x45, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x80, (byte)0x00,

                      //ITEMS
                      (byte)0x80, (byte)0x10, (byte)0x32, (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00,//ITEMS
                      
                      //CONTAINER. First 4 bytes are VAX F-Type, Next 4 bytes are MSB_INTEGER, etc...
                      (byte)0x70, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x32, (byte)0x00, (byte)0x00,//Repetition 1 - VAX_REALs F-Type
                      (byte)0x10, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0x00, (byte)0x88, (byte)0x00};//Repetition 2 - MSB_INTEGER


    //Random Values
    byte[] row_5 = {  
                      //CHARACTER
                      (byte)0x45,

                      //EBCDIC-CHARACTER
                      (byte)0xC5,

                      //BOOLEAN
                      (byte)0x0F, (byte)0x0A, (byte)0x00, (byte)0x01,//BOOLEAN 4-BYTE INTEGER
                      (byte)0xF0, (byte)0xFF,//BOOLEAN 2-Bytes
                      (byte)0xF0,//BOOLEAN 1 Byte

                      //LSB_BIT_STRING
                      (byte)0x37, (byte)0xAA, (byte)0xA0, (byte)0x7F,//LSB_BIT_STRING 4-bytes
                      (byte)0x16, (byte)0xAA,//LSB_BIT_STRING 2-byte. Using Alias VAX_BIT_STRING
                      (byte)0x0F,//LSB_BIT_STRING 1-byte. 

                      //MSB_BIT_STRING
                      (byte)0x43, (byte)0x00, (byte)0xFF, (byte)0x00,//MSB_BIT_STRING 4-byte
                      (byte)0x23, (byte)0x17,//MSB_BIT_STRING 2-byte
                      (byte)0xFF,//MSB_BIT_STRING 1-byte. Using Alias BIT_STRING

                      //LSB_INTEGER
                      (byte)0x08,//LSB_INTEGER 1-byte. 
                      (byte)0x10, (byte)0xFF,//LSB_INTGER 2-byte. Using Alias VAX_INTEGER
                      (byte)0x70, (byte)0x30, (byte)0xFA, (byte)0xFF,//LSB_INTEGER 4 Bytes. Using Alias PC INTEGER

                      //MSB_INTEGER
                      (byte)0x88, (byte)0x40, (byte)0x71, (byte)0x00,//MSB_INTEGER 4-bytes
                      (byte)0xFF, (byte)0x84,//MSB_INTEGER 2-byte. Using Alias IBM_INTEGER
                      (byte)0x39,//MSB_INTEGER 1-byte. Using Alias MAC_INTEGER
                      (byte)0xFF, (byte)0xFF,//MSB_INTEGER 4-bytes. Using Alias SUN_INTEGER
                      (byte)0x83, (byte)0x37, (byte)0xFF, (byte)0x00,//MSB_INTEGER 4-bytes. Using Alias INTEGER

                      //IEEE_REAL - Testing +Infinity
                      (byte)0x00, (byte)0x7F, (byte)0xFF, (byte)0xFF,//IEEE FLOAT 4 Bytes
                      (byte)0x44, (byte)0x31, (byte)0x58, (byte)0xE4, (byte)0x60, (byte)0x91, (byte)0x3D, (byte)0x00,//IEEE DOUBLE 8 Bytes. Using alias MAC_REAL
                      (byte)0x15, (byte)0x38, (byte)0x18, (byte)0xE4, (byte)0x63, (byte)0x91, (byte)0x00, (byte)0x00,//IEEE DOUBLE 8 bytes. Using alias SUN_REAL
                      (byte)0x84, (byte)0x51, (byte)0x58, (byte)0xE8, (byte)0x60, (byte)0x80, (byte)0x3F, (byte)0x00,//IEEE DOUBLE 8 bytes. Using alias FLOAT
                      (byte)0xFF, (byte)0x90, (byte)0x40, (byte)0x20,//IEEE FLOAT 4 Bytes. Using alias REAL

                      //VAX_REAL F, D and G Types
                      (byte)0x9E, (byte)0xC0, (byte)0x40, (byte)0x06,//VAX_REAL F-Type - 4 bytes
                      (byte)0x61, (byte)0x38, (byte)0x00, (byte)0x03, (byte)0x81, (byte)0x32, (byte)0x8F, (byte)0xAB,//VAX_REAL D-Type - 8 bytes
                      (byte)0x61, (byte)0x38, (byte)0x9D, (byte)0x03, (byte)0x8A, (byte)0x42, (byte)0x8F, (byte)0x8B,//VAXG_REAL G-Type - 8 bytes
                      
                      //MSB_UNSINGED_INTEGER
                      (byte)0x32, (byte)0x00, (byte)0x00, (byte)0x00,//MSB_UNSIGNED_INTEGER 4-bytes
                      (byte)0x00, (byte)0x90,//MSB_UNSIGNED_INTEGER 2-byte. Using Alias IBM_INTEGER
                      (byte)0x01,//MSB_UNSIGNED_INTEGER 1-byte. Using Alias MAC_INTEGER
                      (byte)0xB0, (byte)0x60,//MSB_UNSIGNED_INTEGER 2-bytes. Using Alias SUN_INTEGER
                      (byte)0x00, (byte)0x80, (byte)0xAA, (byte)0xC0,//MSB_UNSIGNED_INTEGER 4-bytes. Using Alias INTEGER

                      //LSB_UNSIGNED_INTEGER
                      (byte)0x90,//LSB_UNSIGNED_INTEGER 1-byte. 
                      (byte)0x32, (byte)0x00,//LSB_UNSIGNED_INTGER 2-byte. Using Alias VAX_UNSIGNED_INTEGER
                      (byte)0x10, (byte)0x90, (byte)0x30, (byte)0xBD,//LSB_UNSIGNED_INTEGER 4 Bytes. Using Alias PC_UNSIGNED_INTEGER

                      //IEEE_COMPLEX
                      (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 8 bytes
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 16 bytes. Using alias MAC_COMPLEX
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 16 bytes. Using alias SUN_COMPLEX
                      (byte)0xFF, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                      (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00,//IEEE_COMPLEX 8 bytes. Using alias COMPLEX

                      //VAX_COMPLEX
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00,//VAX_COMPLEX F-type 8-bytes
                      (byte)0x29, (byte)0x40, (byte)0xFB, (byte)0x21, (byte)0x44, (byte)0x54, (byte)0x18, (byte)0x2D,//VAX_COMPLEX D-type 16 bytes
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                      (byte)0x89, (byte)0xAB, (byte)0xFB, (byte)0x32, (byte)0x44, (byte)0x78, (byte)0x18, (byte)0x2D,//VAX_COMPLEX G-type 16 bytes
                      (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                      
                      //PC_REAL 4 and 8 bytes
                      (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF,//PC_REAL - 4 bytes
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF,//PC_REAL - 8 bytes

                      //PC_COMPLEX
                      (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xFF, (byte)0x00, (byte)0x54, (byte)0x80, (byte)0xFF,//PC_COMPLEX 8-bytes
                      (byte)0x00, (byte)0x00, (byte)0x16, (byte)0x00, (byte)0x34, (byte)0x00, (byte)0x80, (byte)0xFF,//PC_COMPLEX 16 bytes
                      (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x19, (byte)0x00, (byte)0x83, (byte)0xAA, (byte)0xFF,

                      //ITEMS
                      (byte)0x03, (byte)0x50, (byte)0x00, (byte)0x52, (byte)0x10, (byte)0x90, (byte)0x00, (byte)0x00,//ITEMS
                      
                      //CONTAINER. First 4 bytes are VAX F-Type, Next 4 bytes are MSB_INTEGER, etc...
                      (byte)0x80, (byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x32, (byte)0x50, (byte)0x00, (byte)0x10,//Repetition 1 - VAX_REALs F-Type
                      (byte)0xA0, (byte)0xFB, (byte)0x16, (byte)0x00, (byte)0x40, (byte)0x15, (byte)0x32, (byte)0x00};//Repetition 2 - MSB_INTEGER



    if(labelBinaryData.openForWritting(FILE_NAME)) {
      System.out.println("File opened for Writing");
    
      //Write array bytes
      labelBinaryData.writeToFile(row_1);
      labelBinaryData.writeToFile(row_2);
      labelBinaryData.writeToFile(row_3);
      labelBinaryData.writeToFile(row_4);
      labelBinaryData.writeToFile(row_5);

      //Make sure to close outputstream
      labelBinaryData.closeFile();

      //Read File contents
      byte[] fileData = labelBinaryData.read(FILE_NAME);

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
