import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Hasher {
	
	private int H;
	
	public int bucketSize;
	
	
	
	public RandomAccessFile idxFile;
	
	private int entrySize;
	
	
	private long fileSize;
	
	
	
	public Hasher(int bucketSize, int entrySize){
		this.bucketSize = bucketSize;
		this.entrySize = entrySize * 4;
		this.H = 0;
		try {
	        File fileRef = new File("lhl.idx");
	      	if(fileRef.exists()) {
	    		fileRef.delete();
	    	}
			this.idxFile = new RandomAccessFile("lhl.idx","rw");
			setupFile(2);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			this.fileSize = this.idxFile.length();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void setupFile(long size) {
		try {
			this.idxFile.seek(this.idxFile.length());
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < bucketSize*size; i++) {
			for(int j = 0; j < (this.entrySize/4); j++) {
				try {
					this.idxFile.writeInt(-1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void show() {
	
		try {
			this.idxFile.seek(0);

		long f = this.fileSize / (this.entrySize);
		int bucket = 0;
		for(int i = 0 ; i < f; i++) {
			if(i % this.bucketSize == 0) {
				System.out.println("Bucket #" + bucket);
				bucket++;
			}
			int currId = idxFile.readInt();
			int currFP = idxFile.readInt();
			if( currId == -1) {
				System.out.println("[]");
			}else {
				System.out.println("["+currId+ ","+currFP+"]");
			}
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void add(int id, int fileIndex) {
		
		if(fileIndex == 3) {
			System.out.println();
		}

		int pos = 0;
		int hashVal = id % ((int)Math.pow(2,this.H + 1));
		boolean hashed = false;
		while(pos < this.bucketSize & !hashed) {
			hashed = hashValue(id,fileIndex, pos, hashVal);
			if(!hashed) {
				pos++;
			}else {
				pos = 0;
			}
			
		}
		
		
		
		if(pos == this.bucketSize) {
			long prevSize = (long) Math.pow(2,this.H + 1) * bucketSize;
			this.H += 1;
			long newSize = (long) Math.pow(2,this.H + 1) * bucketSize;
			
			this.fileSize = newSize * entrySize;
			
			setupFile(newSize-prevSize);
			
			int i = 0;
			pos = 0;
			hashed = false;
			while(i < prevSize) {
				try {
					this.idxFile.seek(i * (4 * entrySize));
					
					int idVal = idxFile.readInt();
					int fileIdx = idxFile.readInt(); 
					this.idxFile.seek(i * (4 * entrySize));
					idxFile.writeInt(-1);
					idxFile.writeInt(-1);
					hashVal = idVal % ((int)Math.pow(2,this.H + 1));
					if(hashVal != -1) {
						hashed = hashValue(idVal,fileIdx, pos, hashVal);
						if(!hashed) {
							pos++;
						}else {
							i++;
							pos = 0;
							hashed = false;
						}
					
					}
					i++;
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
	
	private boolean hashValue(int id, int fileIndex, int pos,int hashVal) {
		
		try {	
		this.idxFile.seek((hashVal) * this.bucketSize * entrySize + (pos * entrySize));

		int currId = this.idxFile.readInt();
		if( currId == -1 ) {
			this.idxFile.seek((hashVal) * this.bucketSize * entrySize+ (pos * entrySize));
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
	
	public void bucketStats() {
		try {
			this.idxFile.seek(0);

		long f = this.fileSize / (this.entrySize);
		int numofbuckets = 0;
		int min = this.bucketSize;
		int max = 0;
		
		int sum = 0;
		int bucketval = 0;
		for(int i = 0 ; i < f; i++) {
			if(i % this.bucketSize == 0) {
				numofbuckets++;
				bucketval = 0;
				System.out.println("++++++++");
			}
			int currId = idxFile.readInt();
			int currFP = idxFile.readInt();
			if( currId == -1) {
				if(bucketval < min) {
					min = bucketval;
				}
				if(bucketval > max) {
					max = bucketval;
				}
				sum += bucketval;
				System.out.println("[]");
				bucketval = 0;
			}else {
				bucketval++;
				System.out.println("["+currId+ ","+currFP+"]");
			}
		}
		
		System.out.println("amount of buckets: " + numofbuckets);
		System.out.println("MAX: " + max);
		System.out.println("MIN: " + min);
		System.out.println("MEAN: " + (sum / numofbuckets));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	
	

}


