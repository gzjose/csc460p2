import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;
/*=============================================================================
|   Assignment:  Program #22:  Linear Hashing Lite
|       Author:  Jose Gonzalez (gzjose@email.arizona.edu)
|       Grader:  Zheng Tang, Chenghao Xiong
|
|       Course:  CSC460
|   Instructor:  L. McCann
|     Due Date:  September 24 12:30pm
|
|  Description:  This is the second part of the Linear Hashing Lite project.
|                In this part we are taking in the binary file as well as the
				 idx file that was created in the first part. once these two
				 files are set up, the user is prompted to enter an id and then
				 the it gets sent into the hash function to look in the bucket
				 to find the id in the idx file. If it is there, then it takes
				 the pointer and goes into the bin file to get the record. If
				 it not there, then it lets the user know the key value isn't
				 in the bin file.
|                
|
|     Language:  Java 1.8
|
*===========================================================================*/
public class Prog22 {
	
	public static void main(String[] args) {
		RandomAccessFile idxFile;
		RandomAccessFile binFile;
		int H = 0;
		int bucketSize = 0;
		try {
			// gets the files from the command line
			idxFile = new RandomAccessFile(args[0], "r");
			binFile = new RandomAccessFile(args[1], "r"); 
			
			try {
				idxFile.seek(idxFile.length()-8);

				// gets the metadata from the idx file
				bucketSize = idxFile.readInt();
				H = idxFile.readInt();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// asks the user to enter an id
			
	        Scanner user = new Scanner(System.in);
	        System.out.println("Enter -1 when you are done\n");
	        System.out.print("Enter ID: ");
	        int userVal = user.nextInt();
	        
	        int[] lengths = getRecordLengths(binFile);
	        int fileIndex = 0;
	        
	        // if the user enters -1 they are done
	        while(userVal != -1) {
	        	
	        	// hashes into the idx file, then returns the file index
	        	fileIndex = hashSearch(userVal, idxFile, H, bucketSize);
	        	if(fileIndex == -1) {
	        		System.out.println("The key value ‘"+userVal+"’ was not found.");
	        	}else {

	        		//gets the values from the bin file
	                getRecordInfo(binFile,fileIndex, lengths);
	                
	        	}
	        	
	        	System.out.print("Enter ID: ");
	            userVal = user.nextInt();
	        }
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    /*---------------------------------------------------------------------
    |  Method getRecordInfo
    |
    |  Purpose:  This method goes into the bin file at the given index, and 
    		then prints out the values from the file
    |
    |  Pre-condition:  the file index points to the record that has the id
    |
    |  Post-condition: the id's values are printed out
    |
    |  Parameters:
    |       binFile: the bin file that holds the records
    		fileIndex: the pointer to the record in the bin file
    		lengths: the string lengths of the record
    |	   
    |
    |  Returns:  None
    *-------------------------------------------------------------------*/
	public static void getRecordInfo(RandomAccessFile binFile, int fileIndex, int[] lengths) {
		try {
			binFile.seek(fileIndex*lengths[5]); // goes to the record
		    
		    byte[] Name = new byte[lengths[0]];  // file -> byte[] -> String
		    byte[] GeoLoc = new byte[lengths[4]];  // file -> byte[] -> String

		    
		    // gets the name
		    binFile.readFully(Name);
		    String name = new String(Name);
		    binFile.seek(binFile.getFilePointer() + lengths[1] + lengths[2]+ lengths[3] + 12);
		    
		    //gets the year value
		    int year = binFile.readInt();
		    
		    // gets the geolocation
		    binFile.seek((fileIndex + 1)*lengths[5] - lengths[4]);
		    binFile.readFully(GeoLoc);
		    String geoloc = new String(GeoLoc);
		    
			System.out.print("["+ name +"]");
			System.out.print("["+ year +"]");
			System.out.print("["+ geoloc +"]\n");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
    /*---------------------------------------------------------------------
    |  Method getRecordLengths
    |
    |  Purpose:  the method goes to the end of the bin file and then gets
    |       the lengths of the string buffers and the record lengths
    |
    |  Pre-condition:  binFile is a valid bin file 
    |
    |  Post-condition: the lengths array is filled with values from the bin file
    |
    |  Parameters:

     		binFile: the bin file with the records

    |	   
    |
    |  Returns: an array with all of the lengths from bin file
    *-------------------------------------------------------------------*/

	public static int[] getRecordLengths(RandomAccessFile binFile) {
		int[] lengths = new int[6];
		try {
			binFile.seek(binFile.length()-24);
			for(int i = 0; i < 6; i++) {
				lengths[i] = binFile.readInt();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lengths;
		
	}

    /*---------------------------------------------------------------------
    |  Method hashSearch
    |
    |  Purpose:  the method looks at the hashVal bucket and then checks if 
    |            the value is in the idx file and if it is there then it
    |            returns the file pointer where the id is in the bin file
    |
    |  Pre-condition:  the userVal is a valid id value 
    |
    |  Post-condition: the pointer is returned or an -1 is returned
    |
    |  Parameters:
    |       userVal: the value you are searching for
     		idxFile: the idx file with the id and pointers
     		H: the value used to find the hash value
     		bucketSize: the size of the bucket beinf searched through
    |	   
    |
    |  Returns:  the pointer from the idx file
    *-------------------------------------------------------------------*/
	public static int hashSearch(int userVal, RandomAccessFile idxFile, int H, int bucketSize) {
		int hashVal = userVal % ((int)Math.pow(2,H + 1));
		int pos = 0;
		while(pos < bucketSize) {
			
			int currid;
			
			try {
				idxFile.seek(((hashVal*bucketSize) + pos) * 8);
				currid = idxFile.readInt();
				if(userVal == currid) {
					return idxFile.readInt();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pos++;
		}
		
		return -1;
	}

}
