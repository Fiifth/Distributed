package hashcode;
	public class FileToHash {
		//hashes fileName
		public static void main(String args[]){
	    	String fileName = "voorbeeldfile.txt" ;
	    	int hashedFN;
	    	hashedFN = Math.abs(fileName.hashCode()%32768);
	    	System.out.println(hashedFN);
		}
	}

