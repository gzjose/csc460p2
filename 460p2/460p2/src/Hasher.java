import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/*+----------------------------------------------------------------------
||
||  Class Hasher
||
||         Author:  Jose Gonzalez
||
||        Purpose:  This object is designed to create a idx file and then
||			hash values into the file. The values that get added to the idx
||			file are an id and a pointer to the entry that contains the id value
||			in the bin file.
||
||  Inherits From:  None
||
||     Interfaces:  None
||
|+-----------------------------------------------------------------------
||
||      Constants: 	H - the value that helps determine how a value gets hashed
	
	 				bucketSize - the amount of entries per bucket
	
	
	
					idxFile - the idx valeu that stores the indecies from the 
					bin file.
	
					entrySize - size of each entry
	
	
					fileSize - the size of the file
	
||
|+-----------------------------------------------------------------------
||
||   Constructors:  Hasher
||                  - bucketSize: the amount of values in each bucket
||
||  Class Methods:  setupFile: appends empty values to the file 
					- long size: the amount of values to be added to the file
||					
||					add: hashes the given value into the file
					- int id: the id used to find hash value
					-int fileIndex: pointer to id from bin file

		
||					hashValue: checks if spot in bucket is empty 
							and the puts the value in
					- int id: the id value 
					-int fileIndex: the pointer to the id value in bin file
					-int pos: the position in the bucket
					-int hashVal: the bucket that the value is being hashed into
||					
||					bucketStats: prints out the amount of buckets, min, max, 
							and mean
||				    
||				    closeHash: adds the bucket size and H value to the file
						and then closes the idx file. 
||				    
||
||  Inst. Methods: None
||
++-----------------------------------------------------------------------*/
public class Hasher {
	
	private int H;
	
	public int bucketSize;
	
	
	
	public RandomAccessFile idxFile;
	
	private int entrySize;
	
	
	private long fileSize;
	
	
    /*---------------------------------------------------------------------
    |  Method Hasher
    |
    |  Purpose:  This method constructs the hasher object as well as the
    		.idx file that we will be using to store the hashed values.
    		By taking in the bucketsize as well as the size of the entries
    		we can fill the .idx file with empty values represented by -1
    		so that the file will be the proper initial size.
    |
    |  Pre-condition:  there is a binary file that this is gonna get its 
    		values from.
    |
    |  Post-condition: the idx file is large enough to hold the max amount
    		of values before rehashing.
    |
    |  Parameters:
    |      bucketSize -- the max amount of entries that will be in each bucket
    |      entrySize -- the lengths the values needs to be to make a record
    |	   
    |
    |  Returns:  None
    *-------------------------------------------------------------------*/
    
	public Hasher(int bucketSize){
		this.bucketSize = bucketSize;
		this.entrySize = 8; // the size of the idex entry (the id value and a pointer to where it is in the file)
		
		
		this.H = 0;   //sets the H value to be used in future hashing
		
		try {
	        File fileRef = new File("lhl.idx"); //creates the lhl.idx file that will be holding the indecies
	      
	        if(fileRef.exists()) { // resents the file
	    		fileRef.delete();
	    	}
			this.idxFile = new RandomAccessFile("lhl.idx","rw");
			
			setupFile(2); //sets the intial amount of buckets and value spaces into the file

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			this.fileSize = this.idxFile.length(); // sets the size of the file
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
    /*---------------------------------------------------------------------
    |  Method setupFile
    |
    |  Purpose:  This method appends empty index values to the end of the 
   			file. In the first use of this method, it makes the initial em)
    |
    |  Pre-condition:  there is a binary file that this is gonna get its 
    		values from.
    |
    |  Post-condition: the idx file is large enough to hold the max amount
    		of values before rehashing.
    |
    |  Parameters:
    |      bucketSize -- the max amount of entries that will be in each bucket
    |      entrySize -- the lengths the values needs to be to make a record
    |	   
    |
    |  Returns:  None
    *-------------------------------------------------------------------*/
	private void setupFile(long size) {
		try {
			this.idxFile.seek(this.idxFile.length());
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < bucketSize*size; i++) {
			for(int j = 0; j < 2; j++) {
				try {
					this.idxFile.writeInt(-1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

    /*---------------------------------------------------------------------
    |  Method add
    |
    |  Purpose:  This method takes in the value from the bin file and a
    		pointer to the position of the value in the bin file. it puts 
    		the value through the hash function to determine which bucket 
    		it should go in. It moves down the bucket until there is an
    		open spot for the entry. If the bucket is filled, buckets are 
    		added to the file and then all of the values get rehashed.
    		
    |
    |  Pre-condition:  there should be at least one bucket with an open 
    		spot.
    |
    |  Post-condition: the id value is placed into the index file
    |
    |  Parameters:
    |      id -- the value that gets hashed and placed into the index file
    |      fileIndex -- the pointer to the values position in the bin file
    |	   
    |
    |  Returns:  None
    *-------------------------------------------------------------------*/	
	public void add(int id, int fileIndex) {
		
		/*
		 * This part gets the hash value and then places the id into the 
		 * bucket. If there is a value in the position you are trying to get
		 * into, it moves down the bucket until it fits.
		 */
		int pos = 0;
		int hashVal = id % ((int)Math.pow(2,this.H + 1));
		boolean hashed = false;
		//moves through the bucket and stops when it gets placed in th idx file
		while(pos < this.bucketSize & !hashed) { 
			hashed = hashValue(id,fileIndex, pos, hashVal);
			if(!hashed) {
				pos++;
			}else {
				pos = 0;
			}
			
		}
		
		/*
		 * if the position is at the very end of the bucket and doesnt place
		 * anything into the index file, then it begins the rehashing.
		 * 
		 */
			
		if(pos == this.bucketSize) {

			
			long prevSize = (long) Math.pow(2,this.H + 1) * bucketSize;
			this.H += 1;
			long newSize = (long) Math.pow(2,this.H + 1) * bucketSize;
			this.fileSize = newSize * entrySize;
			
			// appends the extra buckets needed to the file
			setupFile(newSize-prevSize);
			
			int i = 0;
			pos = 0;
			hashed = false;
			//this part goes through the previous values and rehashes them
			while(i < prevSize) {
				try {
					this.idxFile.seek(i * ( entrySize));
					
					int idVal = idxFile.readInt();
					int fileIdx = idxFile.readInt(); // gets the value
					this.idxFile.seek(i * (entrySize));
					
					// empties out the emtry at that point
					idxFile.writeInt(-1);
					idxFile.writeInt(-1); 
					
					//hash
					hashVal = idVal % ((int)Math.pow(2,this.H + 1));
					if(hashVal != -1) {
						
						hashed = hashValue(idVal,fileIdx, pos, hashVal);
						while(!hashed) {
							pos++;
							hashed = hashValue(idVal,fileIdx, pos, hashVal);
						}
							
						pos = 0;
						hashed = false;
						
				
					}
					i++;
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			// hashes the value that current id value
			pos = 0;
			hashVal = id % ((int)Math.pow(2,this.H + 1));
			hashed = false;
			while(pos < this.bucketSize & !hashed) {
				hashed = hashValue(id,fileIndex, pos, hashVal);
				if(!hashed) {
					pos++;
				}else {
					pos = 0;
				}
				
				
			}

		}
		
	}
	
	
    /*---------------------------------------------------------------------
    |  Method hashValue
    |
    |  Purpose:  This method goes into a certain position in and checks
    		if there are values in that position. If there is a value,
    		then it returns false since it could not be placed
    		
    |
    |  Pre-condition:  the pos value is less than the size of the bucketSize
    |
    |  Post-condition: the value gets placed into the file or it doesn't
    |
    |  Parameters:
    |      id -- the value that gets hashed and placed into the index file
    |      fileIndex -- the pointer to the values position in the bin file
    |      pos -- the position within the bucket we are trying to place the
    |             value
    |      hashVal -- the bucket where the value is being checked and placed
    |	   
    |
    |  Returns:  whether or not the value was placed
    *-------------------------------------------------------------------*/	
	
	private boolean hashValue(int id, int fileIndex, int pos,int hashVal) {
		
		try {	
		this.idxFile.seek((hashVal) * this.bucketSize * entrySize + (pos * entrySize));

		//goes to the spot and checks if it is empty
		int currId = this.idxFile.readInt();
		if( currId == -1 ) {
			this.idxFile.seek((hashVal) * this.bucketSize * entrySize + (pos * entrySize));
			// adds value
			this.idxFile.writeInt(id);
			this.idxFile.writeInt(fileIndex);
			return true;
		}else {
			return false;
		}	
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

		
	}
    /*---------------------------------------------------------------------
    |  Method bucketStats
    |
    |  Purpose:  This method prints out information about the index file.
    		counts the amount of entries in each bucket, finds the min and max
    		amount of values in each bucket. Then it prints the amount of buckets
    		the min, the max, and then the mean of the buckets.
    |
    |  Pre-condition:  all of the values in the bin file have been added to the
    |       index file.
    |
    |  Post-condition: the min, max, and the number of buckets were found
    |
    |  Parameters: None

    |
    |  Returns:  None
    *-------------------------------------------------------------------*/	
	public void bucketStats() {
		try {
			
			
		this.idxFile.seek(0);

		long entries = this.fileSize / (this.entrySize); // amount of entries
		int numofbuckets = 0;
		
		//sets min and max
		int min = this.bucketSize;
		int max = 0;
		
		int sum = 0;
		int bucketval = 0;
		for(int i = 0 ; i < entries; i++) {
			if(i == 0) {
				
				numofbuckets++; // counts the amount of buckets
		
			}else if(i % this.bucketSize == 0 && i != 0) {
				numofbuckets++; // counts the amount of buckets
			
				
				// checks if the value is the max or min
				if(bucketval < min) {
					min = bucketval;
				}
				if(bucketval > max) {
					max = bucketval;
				}
				sum += bucketval;
				
				bucketval = 0; //goes back to zero for next bucket
			}
			int currId = idxFile.readInt();
			int currFP = idxFile.readInt();
			if( currId == -1) {

			}else {

				bucketval++;
			}
		}
		
		// checks if the last bucket is the min or max
		if(bucketval < min) {
			min = bucketval;
		}
		if(bucketval > max) {
			max = bucketval;
		}
		sum += bucketval;		
		System.out.println("amount of buckets: " + entries/this.bucketSize);
		System.out.println("MAX: " + max);
		System.out.println("MIN: " + min);
		System.out.println("MEAN: " + (sum / numofbuckets));
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
    /*---------------------------------------------------------------------
    |  Method closeHash
    |
    |  Purpose:  This method is in charge of going to the end of the file and
    |		then places the bucketSize and the H values so that the file
    |       can be hashed elsewhere 
    |
    |  Pre-condition: all of the values were added and the stats were already
    		printed out
    |
    |  Post-condition: the file is closed and metadata was added
    |
    |  Parameters: None

    |
    |  Returns:  None
    *-------------------------------------------------------------------*/	
	public void closeHash() {

		try {
			this.idxFile.seek(this.idxFile.length());
			this.idxFile.writeInt(this.bucketSize);
			this.idxFile.writeInt(this.H);
			this.idxFile.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}



