import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/*=============================================================================
|   Assignment:  Program #1A:  Creating and Exponentially Searching a Binary File
|       Author:  Jose Gonzalez (gzjose@email.arizona.edu)
|       Grader:  Zheng Tang, Chenghao Xiong
|
|       Course:  CSC460
|   Instructor:  L. McCann
|     Due Date:  September 3 12:30pm
|
|  Description:  This Program is about Creating a binary file using information
|                taken from a csv file. In particular, this program reads 
|                a meteorite landing csv file. This information is read line
|                by line and is stored in a record and converst the values into
|                their proper type. Then the records are organized into accending
|                order by the year. Then all of the records are added to a 
|                binary file which the program creates based on the name of the 
|				 csv file.
|
|     Language:  Java 1.8
|
*===========================================================================*/

/*+----------------------------------------------------------------------
||
||  Class BinDataRecord
||
||         Author:  Jose Gonzalez
||
||        Purpose:  This object is designed to be able to store an entry from
||                  the csv file so that it can be later dumped and fetched from
||                  a binary file.
||
||  Inherits From:  None
||
||     Interfaces:  None
||
|+-----------------------------------------------------------------------
||
||      Constants: RECORD_LENGTH: the ammount of bytes within each entry in the csv file
||                 NAME_LENGTH:  the max length of a name string
||                 NAMETYPE_LENGTH: the max length of a nametype string
||                 RECCLASS_LENGTH: the max length of a recclass string
||                 FALL_LENGTH: the max length of a fall string
||                 GEOLOC_LENGTH: the max length of a geoloc string
||
||                 name: the name of the location the meteorite landed
||				   id: the id of the meteorite
||                 nametype: this shows if the name is valid or not
||                 recclass: the classification of the record
||                 mass: the mass of the meteorite in grams(g)
||                 fall: whether or not it has fallen 
||                 year: the time when the meteorite landed
||                 reclat: the lattitude of the meteorite
||                 reclong: the longitude of the meteorite
||                 geoloc: the geolocation of the meteorite
||
|+-----------------------------------------------------------------------
||
||   Constructors:  binDataRecord
||                  - items: an ArrayList with all of the parsed elements from the entry
||
||  Class Methods:  setLengths: sets the string lengths to the given max value for each column
||					- int name: name length
||				    - int namet: nametype length
||					- int recclass: recclass length
||					- int fall: fall length
||					- int geoloc: geoloc length
||					
||					dumpObject: this method adds info from the records into the binary file
||					- RandomAccessFile stream: this is the binary file with the information
||					
||					fetchObject: this method loads the records with info from the binary file
||					- RandomAccessFile stream: this is the binary file with the information
||					
||					adjust: this method adds padding to string so that it is the max length
||				    - String val: the given value to adjust
||				    - int max: the length that the value needs to reach
||				    
||				    fixVal: this method makes sure that any empty numeric column gets a value
||				            and gets the year from the year column
||				    - String value: the string that needs to be edited
||				    - String fieldType: what type the string needs to work for
||				    
||				    
||
||  Inst. Methods: None
||
++-----------------------------------------------------------------------*/

class BinDataRecord implements Comparable<BinDataRecord>
{
    public int RECORD_LENGTH; // 2 ints + str = 2(4)+22 bytes
    public int NAME_LENGTH;  // the maximum length allowed
    public int NAMETYPE_LENGTH;
    public int RECCLASS_LENGTH;
    public int FALL_LENGTH;
    public int GEOLOC_LENGTH;

                    // The data fields that comprise a record of our file
    //name,id,nametype,recclass,mass (g),fall,year,reclat,reclong,GeoLocation
    private String name;    // the name of the location the meteorite landed
    private    int id;    // the id of the meteorite
    private String nametype;   // this shows if the name is valid or not
    private String recclass;   // the classification of the record
    private double mass;   // the mass of the meteorite in grams(g)
    private String fall;   // whether or not it has fallen 
    private    int year;   // the time when the meteorite landed
    private double reclat;   // the lattitude of the meteorite
    private double reclong;   // the longitude of the meteorite
    private String geoloc;   // the geolocation of the meteorite
    
    
    public BinDataRecord(ArrayList<String> items) {
        setName(items.get(0));  // the FIPS code for states
        setId(items.get(1));   // the FIPS code for places
        setNameType(items.get(2));// of a county the place occupies
        setRecclass(items.get(3)); // of a county the place occupies
        setMass(items.get(4));  // of a county the place occupies
        setFall(items.get(5));
        setYear(items.get(6));
        setReclat(items.get(7));  // of a county the place occupies
        setReclong(items.get(8));  // of a county the place occupies
        setGeoLoc(items.get(9)); 
        this.RECORD_LENGTH = 32;
    	
    }

    public BinDataRecord() {
		// TODO Auto-generated constructor stub
	}

	public BinDataRecord(int[] lengths) {
    	NAME_LENGTH = lengths[0];
    	NAMETYPE_LENGTH = lengths[1];
    	RECCLASS_LENGTH = lengths[2];
    	FALL_LENGTH = lengths[3];
    	GEOLOC_LENGTH = lengths[4];
		RECORD_LENGTH = lengths[0] + lengths[1] +lengths[2] + lengths[3] + lengths[4] + 32;
	}

	// 'Getters' for the data field values

    public String getName() {return name;}   
    public    int getId() {return id; }   
    public String getNameType() {return nametype;}   
    public String getRecclass() {return recclass;}  
    public double getMass() {return mass;}  
    public String getFall() {return fall;}
    public Integer getYear() {return year;}
    public double getReclat() {return reclat;}   
    public double getReclong() {return reclong;}   
    public String getGeoLoc() {return geoloc;}   

    // 'Setters' for the data field values
    
    public void setName(String value) {name = fixVal(value,"s"); }  
    public void setId(String value) {id = Integer.valueOf(fixVal(value,"n"));}    
    public void setNameType(String value) {nametype = fixVal(value,"s");}   
    public void setRecclass(String value) {recclass = fixVal(value,"s");}   
    public void setMass(String value) {mass = Double.valueOf(fixVal(value,"n"));}  
    public void setFall(String value) {fall = fixVal(value,"s");}
    public void setYear(String value) {year = Integer.valueOf(fixVal(value,"y"));}
    public void setReclat(String value) {reclat = Double.valueOf(fixVal(value,"n"));}  
    public void setReclong(String value) {reclong = Double.valueOf(fixVal(value,"n"));}   
    public void setGeoLoc(String value) {geoloc = fixVal(value,"s");}   

    public void setLengths(int name, int namet, int recclass, int fall, int geoloc) {
    	NAME_LENGTH = name;
    	NAMETYPE_LENGTH = namet;
    	RECCLASS_LENGTH = recclass;
    	FALL_LENGTH = fall;
    	GEOLOC_LENGTH = geoloc;
    	this.name = adjust(this.name, NAME_LENGTH);
    	this.nametype = adjust(this.nametype, NAMETYPE_LENGTH);
    	this.recclass = adjust(this.recclass, RECCLASS_LENGTH);
    	this.fall = adjust(this.fall, FALL_LENGTH);
    	this.geoloc = adjust(this.geoloc, GEOLOC_LENGTH);
    	RECORD_LENGTH = (name + namet + recclass + fall + geoloc) + 32;
    } 

    /*---------------------------------------------------------------------
    |  Method dumpObject
    |
    |  Purpose:  This method writes all of the values saved in this BinDataRecord object
    |            into the RandomAccessFile. The strings are turned into Stringbuffers so that
    |            the bytes can be stored into the file, then the integers and doubles are just
    |            written directly into the file.  
    |
    |  Pre-condition:  the max lengths of each string need to be established first in order 
    |                  for this to work.
    |
    |  Post-condition: All of the data from this object should be stored into the binary file
    |
    |  Parameters:
    |      stream -- This is the binary file in which all of the information from this 
    |                object should be stored in.
    |
    |  Returns:  None
    *-------------------------------------------------------------------*/

    public void dumpObject(RandomAccessFile stream)
    {
         StringBuffer nameB = new StringBuffer(name);  // paddable county name
         StringBuffer nametypeB = new StringBuffer(nametype);   // of a county the place occupies
         StringBuffer recclassB = new StringBuffer(recclass);   // of a county the place occupies
         StringBuffer fallB = new StringBuffer(fall);   // of a county the place occupies
         StringBuffer geolocB = new StringBuffer(geoloc);   // of a county the place occupies
        
         try {
             nameB.setLength(NAME_LENGTH); // the maximum length allowed
             nametypeB.setLength(NAMETYPE_LENGTH);
             recclassB.setLength(RECCLASS_LENGTH);
             fallB.setLength(FALL_LENGTH);
             geolocB.setLength(GEOLOC_LENGTH);


             stream.writeBytes(nameB.toString());
             stream.writeInt(id);
             stream.writeBytes(nametypeB.toString());
             stream.writeBytes(recclassB.toString());
             stream.writeDouble(mass);
             stream.writeBytes(fallB.toString());
             stream.writeInt(year);
             stream.writeDouble(reclat);   // of a county the place occupies
             stream.writeDouble(reclong);
             stream.writeBytes(geolocB.toString());
             

             
        } catch (IOException e) {
            System.out.println("I/O ERROR: Couldn't write to the file;\n\t"
                             + "perhaps the file system is full?");
            System.exit(-1);
         }
    }
    public void display() {
		System.out.print("["+ this.getName()+"] ");
		System.out.print("["+ this.getYear()+"] ");
		System.out.print("["+ this.getGeoLoc()+"]\n");
    }

    /*---------------------------------------------------------------------
    |  Method fetchObject
    |
    |  Purpose:  This method gets all of the values saved in the RandomAccessFile and stores
    |            the values into BinDataRecord object. I establish byte arrays that will
    |            store the string values when they are read from the binary file and then the
    |            numeric values from the file are just read directly into the fields.
    |
    |  Pre-condition:  the max lengths of each string need to be established first in order 
    |                  for this to work.
    |
    |  Post-condition: All of the data from the binary file should fit perfectly into 
    |                  the objects.
    |
    |  Parameters:
    |      stream -- This is the binary file in which all of the information is stored and 
    |                object should be able to grab from.
    |
    |  Returns:  None
    *-------------------------------------------------------------------*/

    public void fetchObject(RandomAccessFile stream)
    {
         byte[] Name = new byte[NAME_LENGTH];  // file -> byte[] -> String
         byte[] NameType = new byte[NAMETYPE_LENGTH];  // file -> byte[] -> String
         byte[] Recclass = new byte[RECCLASS_LENGTH];  // file -> byte[] -> String
         byte[] Fall = new byte[FALL_LENGTH];  // file -> byte[] -> String
         byte[] GeoLoc = new byte[GEOLOC_LENGTH];  // file -> byte[] -> String


         try {
             stream.readFully(Name);
             name = new String(Name);
             id = stream.readInt();
             stream.readFully(NameType);
             nametype = new String(NameType);
             stream.readFully(Recclass);
             recclass = new String(Recclass);
             mass = stream.readDouble();
             stream.readFully(Fall);
             fall = new String(Fall);
             year = stream.readInt();
             reclat = stream.readDouble();
             reclong = stream.readDouble();
             stream.readFully(GeoLoc);
             geoloc = new String(GeoLoc);
             
        } catch (IOException e) {
            System.out.println("I/O ERROR: Couldn't read from the file;\n\t"
                             + "is the file accessible?");
            System.exit(-1);
         }
    }
    
    /*---------------------------------------------------------------------
    |  Method adjust
    |
    |  Purpose:  This method is in charge of making sure that all of the strings
    |            have the proper amount of so that they fit the size of their field
    |
    |  Pre-condition:  the max lengths of each string need to be established first in order 
    |                  for this to work.
    |
    |  Post-condition: the length of the value should match the given size
    |
    |  Parameters:
    |      val -- the string value that needs padding.
    |      max -- the length the value needs to be.
    |
    |  Returns:  the string with the padding
    *-------------------------------------------------------------------*/
    
    private String adjust(String val, int max) {
    		
    	String fixed = val;
    	
    	int sizeVal = val.length();	
    	while(sizeVal <= max) { 
    		fixed = fixed + " "; //adds the padding aka space (" ")
    		sizeVal++;
    	}
    	return fixed;
    	
    }
    /*---------------------------------------------------------------------
    |  Method fixVal
    |
    |  Purpose:  This method makes sure that all of the values are in the correct
    |            format so that they can be read properly. if the value is empty in a
    |            numeric field, it will return -1. If there is a comma in a numeric field it is
    |            removed. and then in the date line we only need to get the year from the
    |            string.
    |
    |  Pre-condition:  determine
    |
    |  Post-condition: the length of the value should match the given size
    |
    |  Parameters:
    |      value -- the string value that needs to be fixed.
    |      fieldType -- a flag that shows how the value should be treated. As a numeric value,
    |                   a string value, and a year value.
    |
    |  Returns: The fixed string value
    *-------------------------------------------------------------------*/   
    public   String fixVal(String value, String fieldType) {
    	if(fieldType == "n" && value == "") {
    		return "-1";
    		
    	}else if(fieldType == "n" && value.contains(",")){
    		return value.trim().replace(",", "");
    		
    	}else if(fieldType == "y"){
    		if(value == "") {
    			return "-1";
    		}else {
        		return value.trim().split("/")[2].substring(0,4);    			
    		}
    		
    	}else {
    		return value;
    	}
  	    	
    }

	@Override
	public int compareTo(BinDataRecord other) {
		
		return this.getYear().compareTo(other.getYear());
	}
    

}


public class Prog1A {

	public static void main(String[] args) {
		
        File             fileRef;
        // used to create the file
        RandomAccessFile dataStream = null;   // specializes the file I/O
        BinDataRecord       rec;          // the objects to write/read
        long             numberOfRecords = 0;
        int[] lengths = new int[5];
        // loop counter for reading file


                    // Create and populate the records to be written
        ArrayList<BinDataRecord> records = new ArrayList<BinDataRecord>();
        try {
        Scanner csvFile = new Scanner(new File(args[0]));

        csvFile.nextLine();

         // stores the max length in each string field
        while(csvFile.hasNextLine()) {
        	ArrayList<String> items = splitEntry(csvFile.nextLine()); //splits line into items
        	
        	if(lengths[0] < items.get(0).length()) {
        		lengths[0] = items.get(0).length();
        	}else if(lengths[1] < items.get(2).length()) {
        		lengths[1] = items.get(2).length();
        	}else if(lengths[2] < items.get(3).length()) {
        		lengths[2] = items.get(3).length();
        	}else if(lengths[3] < items.get(5).length()) {
        		lengths[3] = items.get(5).length();
        	}else if(lengths[4] < items.get(9).length()) {
        		lengths[4] = items.get(9).length();
        	}       	
        	
        	rec = new BinDataRecord(items); // creates the BinDataRecord object
        	
        	records.add(rec);
        
        }
        
        // fixes the max lengths for the strings in each record
        for(int i =0;i<records.size();i++) {
        	records.get(i).setLengths(lengths[0], lengths[1], lengths[2], lengths[3], lengths[4]);
        }

        }catch(FileNotFoundException e) {
            System.out.println("VERY STRANGE I/O ERROR: Couldn't close "
                    + "the file!");
        	
        }
        

                    /* Create a File object to represent the file and a
                     * RandomAccessFile (RAF) object to supply appropriate
                     * file access methods.  Note that there is a constructor
                     * available for creating RAFs directly (w/o needing a
                     * File object first), but having access to File object
                     * methods is often handy.
                     */
        String[] binName = args[0].substring(0, args[0].length()-3).concat("bin").split("/");
        

        fileRef = new File(binName[binName.length-1]);
        
        // removes any previous file with that name
      	if(fileRef.exists()) {
    		fileRef.delete();
    	}
        try {
  
            dataStream = new RandomAccessFile(fileRef,"rw");
        } catch (IOException e) {
            System.out.println("I/O ERROR: Something went wrong with the "
                             + "creation of the RandomAccessFile object.");
            System.exit(-1);
        }
        


        // sorts the records in accending order based on their year value
        Collections.sort(records);
        


        // goes through the new sorted records and adds the values to the binary file
        int i = 0;
        int j = 1;
        while (i < records.size()) {
        	if(records.get(i).getYear() == 1880) {
        		System.out.println(j);
        		j++;
        	}
        	records.get(i).dumpObject(dataStream);
            i++;
        }
        

        try {
			dataStream.writeInt(records.get(0).NAME_LENGTH);
			dataStream.writeInt(records.get(0).NAMETYPE_LENGTH);
			dataStream.writeInt(records.get(0).RECCLASS_LENGTH);
			dataStream.writeInt(records.get(0).FALL_LENGTH);
			dataStream.writeInt(records.get(0).GEOLOC_LENGTH);
			dataStream.writeInt(records.get(0).RECORD_LENGTH);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  // the maximum length allowed



        // Clean-up by closing the file 

        try {
            dataStream.close();
        } catch (IOException e) {
            System.out.println("VERY STRANGE I/O ERROR: Couldn't close "
                             + "the file!");
        }
		
	
		
	}
	
	
public static ArrayList<BinDataRecord> ExBinSearch(RandomAccessFile dataStream, long length, int[] lengths, int target) {
	
	int i = 0;
	

	int index = (int)(2* (Math.pow(2, i)-1));
	BinDataRecord indexRec = new BinDataRecord(lengths);
	try {
		dataStream.seek((long) index * length);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	indexRec.fetchObject(dataStream);
	while(index < length && indexRec.getYear() < target) {
		i++;
		index = (int) (2* (Math.pow(2, i)-1));
		try {
			dataStream.seek((long) index * length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		indexRec.fetchObject(dataStream);
		//System.out.println(index);
	}
	
	int start = (int) (2* (Math.pow(2, i-1)-1)+1);
	int end = (int) Math.min(2* (Math.pow(2, i)-1)-1, length);
	
	
	return binarySearch(dataStream,start, end, length, target);
}




    /*---------------------------------------------------------------------
    |  Method bubbleSort
    |
    |  Purpose:  This method sorts the records in accending order based on 
                 the year 
    |
    |  Pre-condition:  None
    |
    |  Post-condition: the records are sorted in accending order
    |
    |  Parameters:
    |      records -- the arraylist with all of the records
    |
    |  Returns: an array with sorted data records
    *-------------------------------------------------------------------*/  
     
	private static ArrayList<BinDataRecord> binarySearch(RandomAccessFile dataStream, int start, long end, long length, int target) {
		int mid = (int) Math.floor((start + end)/2);
		BinDataRecord midRec = new BinDataRecord();
		ArrayList<BinDataRecord> l = new ArrayList<BinDataRecord>();
		try {
			dataStream.seek((long) mid * length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		midRec.fetchObject(dataStream);
		int midVal = midRec.getYear();
		if(midVal == target) {

			l.add(midRec);
			return l;
			
		}else if( midVal> target) {
			return binarySearch(dataStream, start, mid -1 , length, target);
		}else {
			return binarySearch(dataStream, mid + 1 , start, length, target);
		}
		
	}


	

    /*---------------------------------------------------------------------
    |  Method splitEntry
    |
    |  Purpose:  This method splits the entry into items based on the commas
    |           in the line but it shouldn't split if the comma is in a quote.
    |           So it adds characters to a string until it hits a comma that
    |           is not inbetween the quotes.
    |
    |  Pre-condition:  the row is in a csv format
    |
    |  Post-condition: the arraylist should be as large as the amount of fields
    |                  in the csv file
    |
    |  Parameters:
    |      row -- the row taht was read from the csv file
    |
    |  Returns: an arraylist with the items from the row.
    *-------------------------------------------------------------------*/ 


	public static ArrayList<String> splitEntry(String row) {
		
		ArrayList<String> items = new ArrayList<String>();
		
		String current = "";
		
		int i = 0;
		boolean inQuote = false;
		while(i < row.length()) {
			if(row.charAt(i) != ',') {
				 //checks when it is in quotes
				if(row.charAt(i) == '"' && !inQuote) {
					inQuote = true;	
					
			    //checks when it is not in quotes
				}else if(row.charAt(i) == '"' && inQuote) {
					inQuote = false;
			    }else {
			    	// adds character to the current string
					current = current + row.charAt(i);
				}
			}else if(row.charAt(i) == ','){
				// when the pointer reaches the next comma, it adds the 
				// string to the list of items
				if(!inQuote){
					String normItem =Normalizer
							.normalize(current, Normalizer.Form.NFKD)
							.replaceAll("[^\\p{ASCII}]", "");
					items.add(normItem);

					current = "";
				}else {
					current = current + row.charAt(i);
				}
				
			}
			i++;
		
		}
		//adds the string to the list of items
		String normItem =Normalizer
				.normalize(current, Normalizer.Form.NFKD)
				.replaceAll("[^\\p{ASCII}]", "");
		items.add(normItem);		
		current = "";
		
		return items;
	}
	
}