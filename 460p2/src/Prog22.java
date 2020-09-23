import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

public class Prog22 {
	
	public static void main(String[] args) {
		RandomAccessFile idxFile;
		RandomAccessFile binFile;
		int H = 0;
		int bucketSize = 0;
		try {
			idxFile = new RandomAccessFile(args[0], "r");
			binFile = new RandomAccessFile(args[1], "r");
			
			try {
				idxFile.seek(idxFile.length()-8);

				bucketSize = idxFile.readInt();
				H = idxFile.readInt();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        Scanner user = new Scanner(System.in);
	        System.out.print("Enter ID: ");
	        int userVal = user.nextInt();

	        int fileIndex = 0;
	        while(userVal != -1) {
	        	fileIndex = hashSearch(userVal, idxFile, H, bucketSize);
	        	if(fileIndex == -1) {
	        		System.out.println("The key value ‘"+userVal+"’ was not found.");
	        	}else {

	                int[] lengths = getRecordLengths(binFile, fileIndex);
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

	public static void getRecordInfo(RandomAccessFile binFile, int fileIndex, int[] lengths) {
		try {
			binFile.seek(fileIndex*lengths[5]);
		    
		    byte[] Name = new byte[lengths[0]];  // file -> byte[] -> String
		    byte[] GeoLoc = new byte[lengths[4]];  // file -> byte[] -> String

		    binFile.readFully(Name);
		    String name = new String(Name);
		    binFile.seek(binFile.getFilePointer() + lengths[1] + lengths[2]+ lengths[3] + 12);
		    
		    int fall = binFile.readInt();
		    binFile.seek(fileIndex*lengths[5] - lengths[4]);
		    binFile.readFully(GeoLoc);
		    String geoloc = new String(GeoLoc);
		    
			System.out.print("["+ name +"]");
			System.out.print("["+ fall +"]");
			System.out.print("["+ geoloc +"]\n");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static int[] getRecordLengths(RandomAccessFile binFile, int fileIndex) {
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
