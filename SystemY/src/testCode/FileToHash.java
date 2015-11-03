package testCode;
	public class FileToHash {
		//hashes fileName
		public static void main(String args[]){
	    	String txt3 = "3.txt" ;
	    	String txt4 = "4.txt";
	    	String txt5 = "5.txt";
	    	String txt6 = "6.txt";
	    	String txt7 = "7.txt";
	    	String txt8 = "8.txt";
	    	int h3;
	    	int h4;
	    	int h5;
	    	int h6;
	    	int h7;
	    	int h8;
	    	h3 = Math.abs(txt3.hashCode()%32768);
	    	h4 = Math.abs(txt4.hashCode()%32768);
	    	h5 = Math.abs(txt5.hashCode()%32768);
	    	h6 = Math.abs(txt6.hashCode()%32768);
	    	h7 = Math.abs(txt7.hashCode()%32768);
	    	h8 = Math.abs(txt8.hashCode()%32768);
	    	
	    	System.out.println("3: "+h3);
	    	System.out.println("4: "+h4);
	    	System.out.println("5: "+h5);
	    	System.out.println("6: "+h6);
	    	System.out.println("7: "+h7);
	    	System.out.println("8: "+h8);
		}
	}

