import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/*=============================================================================
|   Assignment:  Program #21:  Linear Hashing Lite
|       Author:  Jose Gonzalez (gzjose@email.arizona.edu)
|       Grader:  Zheng Tang, Chenghao Xiong
|
|       Course:  CSC460
|   Instructor:  L. McCann
|     Due Date:  September 24 12:30pm
|
|  Description:  This is the first part of the Linear Hashing Lite project.
|                In this part we are taking in the binary file that was created
                 in the previous project and then we are taking every record 
|				 and then we are getting the id and pointer and putting them into
|                an idx file using the Hasher object that properly hashes the 
|                values
|
|     Language:  Java 1.8
|
*===========================================================================*/

public class Prog21 {
	
	public static void main(String[] args) {
		RandomAccessFile records;
      	
		
		
      	Hasher hash = new Hasher(50); // creates Hasher object with 50 buckets
      	
      	// the lengths from the bin file
      	int[] lengths = new int[6];
    
      	try {
			records = new RandomAccessFile(args[0],"r");
			
			// goes to the end of the file to get the lengths
        	long recordsSize = records.length()-24; 
            records.seek(recordsSize);
            

            //gets the lengths
            for(int i = 0; i < lengths.length; i++) {
            	lengths[i] = records.readInt();
            }
            long numOfRecords = (recordsSize)/lengths[5];
            records.seek(0);
            long i = 0;
            
            // goes through all of the records to be added to the Hasher object
            while (i < numOfRecords) {
            	BinDataRecord Val = new BinDataRecord(lengths);
            	Val.fetchObject(records);
            	hash.add(Val.getId(), (int)i);
                
            	i++;
            }
            
            hash.bucketStats(); // prints out the bucket stats
            hash.closeHash(); // closes the idx file


            
       
        } catch (IOException e) {
            System.out.println("I/O ERROR: Something went wrong with the "
                             + "creation of the RandomAccessFile object.");
            System.exit(-1);
        }
      	
      	
      	
	}

}
