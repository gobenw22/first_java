import java.util.*;
import java.io.*;  

public class MyThreadGlobal extends Thread{
	private File inputFile;
    private String outputPath;
    private int pageCharsLimit; 
	public MyThreadGlobal(File inputFile, String outputPath, int pageCharsLimit){ //arguments
		this.inputFile = inputFile;
		this.outputPath = outputPath;
		this.pageCharsLimit = pageCharsLimit;
	}
    
    //!!!!!!NEED HELP HERE ON TO PROPERLY SET UP THE METHOD
    public TreeMap TreeMapper(){
            if(inputFile != null){ //loop through the files in the input directory and perform some actions
            //Capture input file name for use in storing output file
                String f = inputFile.getName();
                int pos = f.lastIndexOf(".");
                String outputFileName = pos > 0 ? f.substring(0, pos) : f;
            //end file name capture
            int pageCharsCurrent = 0; //the character counter
            int pageCurrent = 1; //start the page counter
            TreeMap<String, TreeSet<Integer>> word_pages = new TreeMap(); //TreeMap to hold words & page numbers which are in a treeset
            TreeSet<Integer> word_pages_test = new TreeSet(); //create treeset to hold test case below.
            try{
                Scanner scnr = new Scanner(inputFile); //scan file
                while (scnr.hasNext()){ //do these things with the current line
                    String word = scnr.next();
                    word_pages_test = word_pages.get(word);//pull in the pages of the current word, if they exist
                    pageCharsCurrent += (word.length());//count the characters in the current word
                    if(pageCharsCurrent <= pageCharsLimit){ // if the current character count doesn't exceed the page character count, keep going
                        TreeSet<Integer> pagesNew = new TreeSet(); //TreeSet to hold page numbers.
                        pagesNew.add(pageCurrent); //add the first page to the pages treeset
                        if(word_pages_test == null){//test to see if the current word existed in the treemap, 
                            word_pages.put(word,pagesNew);//if current word doesn't exist in the TreeMap add the current word & page to the treemap
                        }
                        else {//if the current word DOES exist in the treemap
                            boolean exists = word_pages_test.contains(pageCurrent); //test to see if the current page exists in the page numbers for that word
                            if(exists == true){ //if the current page number DOES exists in the page numbers for that word, do nothing
                            }
                             else { //if the current page number does NOT exist in the page numbers for that word, add that page number to the TreeSet for that word
                                TreeSet<Integer> pagesOld = new TreeSet(); //create a TreeSet
                                pagesOld = word_pages.get(word);//add any existing but unique page numbers from the main TreeSet to this working TreeSet
                                pagesOld.add(pageCurrent);//add the current page number to the other existing unique page numbers
                                word_pages.put(word,pagesOld); //overwrite the key & values for the word & page numbers, ensuring a complete TreeMap Key & TreeSet values are stored for the current word/page number.
                             }
                        }
                    }
                    else {//if the current character count exceeds the page character count limit
                        pageCharsCurrent = (word.length()); //reset the character counter to the current word length
                        pageCurrent++; //increment the page number
                        TreeSet<Integer> pagesNew = new TreeSet(); //TreeSet to hold page numbers.
                        pagesNew.add(pageCurrent); //add the first page to the pages treeset
                        if(word_pages_test == null){//test to see if the current word existed in the treemap, 
                            word_pages.put(word,pagesNew);//if current word doesn't exist in the TreeMap add the current word & page to the treemap
                        }
                        else {//if the current word DOES exist in the treemap
                            boolean exists = word_pages_test.contains(pageCurrent);//test to see if the current page exists in the page numbers for that word
                            if(exists == true){//if the current page number DOES exists in the page numbers for that word, do nothing
                            //do nothing
                            }
                             else {//if the current page number does NOT exist in the page numbers for that word, add that page number to the TreeSet for that word
                                TreeSet<Integer> pagesOld = new TreeSet(); //create a TreeSet
                                pagesOld = word_pages.get(word);//add any existing but unique page numbers from the main TreeSet to this working TreeSet
                                pagesOld.add(pageCurrent);//if the page is NOT already in the pages TreeSet, add it THEN re-add the word,pages overwriting the old word,pages
                                word_pages.put(word,pagesOld); //overwrite the key & values for the word & page numbers, ensuring a complete TreeMap Key & TreeSet values are stored for the current word/page number.
                             }
                        }
                    }
                }
            }
            catch(FileNotFoundException e){ //required error handling for writing files
                e.printStackTrace();
            }
        }
        return word_pages;
	}
	public void run(){//!!!!!!NEED HELP HERE ON HOW TO CALL THE TREEMAPPER METHOD
        TreeMapper();
        
	}
    public TreeMap getIndexMap(){//!!!!!!FINALLY - NEED HELP TO CREATE A METHOD THAT I CAN CALL FROM MY RUNNER CLASS THAT WILL RETURN THE TREEMAP TO THE RUNNER
      return run.word_pages;  
    };
}
    
    
//RUNNER CLASS CODE IS BELOW BUT NOT REQUIRED TO REVIEW.

import java.util.*;
import java.io.*;  

public class GlobalRunner{
    public static  TreeMap<String, TreeSet<Integer>> global_word_pages = new TreeMap();
    
	public static void main(String args[]) throws FileNotFoundException{
        long start = System.currentTimeMillis();

        String inputFolder = args[0]; //capture input directory from the first argument 
        String outputFolder = args[1]; //capture output directory from the second argument 
        int pageCharsLimit = 0; //create int variable to hold number of characters that represents a page
        
        try { //error handling for converting page number argument into an integer
            pageCharsLimit = Integer.parseInt(args[2]); //parse the string argument into an integer variable
        }
        catch (NumberFormatException nfe) {//error handling for converting page number argument into an integer
            System.out.println("The third argument must be a positive integer."); //if the third command line argument is not a positive integer, return error & exit
            System.exit(1);
        }
        
        File dirIn = new File(inputFolder); //create new file variable for the input variable
        File[] dirInFiles = dirIn.listFiles(); //create file array for the files in the input folder
        
        int numWorkers = 0; //variable for the number of files in the directory
        for(File c : dirInFiles){ //count the files in the directory
            numWorkers += 1; //populate the file counter variable
        }
        
        MyThreadGlobal[] theWorkers = new MyThreadGlobal[numWorkers]; //generate number of workers needed
        
		for(int i=0; i<numWorkers; i++){
            theWorkers[i] = new MyThreadGlobal(dirInFiles[i], outputFolder, pageCharsLimit);
            theWorkers[i].start();
        }

        for(MyThreadGlobal mp: theWorkers){
            if(mp.isAlive()){
                try{
                    mp.join();
                    global_word_pages = mp.getIndexMap();
                }
                catch(InterruptedException xx){
                    System.out.println("Thread join exception.");
                }
            }
        }
        long duration = System.currentTimeMillis() - start;
        System.out.println(duration);
    }  
}  
