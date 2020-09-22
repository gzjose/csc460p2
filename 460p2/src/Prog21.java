import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Prog21 {
	
	public static void main(String[] args) {
		RandomAccessFile records;
      	
      	Hasher hash = new Hasher(3,2);
      	int[] lengths = new int[6];
    
      	try {
			records = new RandomAccessFile(args[0],"r");
		
        	long recordsSize = records.length()-24;
            records.seek(recordsSize);


           
            for(int i = 0; i < lengths.length; i++) {
            	lengths[i] = records.readInt();
            }
            long numOfRecords = (recordsSize)/lengths[5];
            records.seek(0);
            long i = 0;
            while (i < numOfRecords) {
            	BinDataRecord Val = new BinDataRecord(lengths);
            	Val.fetchObject(records);
            	hash.add(Val.getId(), (int)i);
                
            	i++;
            }
            hash.bucketStats();
            


            
       
        } catch (IOException e) {
            System.out.println("I/O ERROR: Something went wrong with the "
                             + "creation of the RandomAccessFile object.");
            System.exit(-1);
        }
      	
      	
      	
	}

}
