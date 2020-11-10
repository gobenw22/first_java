import java.util.*;
import java.io.*;  
  
public class Index {  
    public static void main(String[] args) throws FileNotFoundException {
        long now = System.currentTimeMillis(); //capture system time at start of process
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
        if(dirInFiles != null){ //loop through the files in the input directory and perform some actions
            for (File child : dirInFiles){//iterate over any files in the input folder
                //Capture input file name for use in storing output file
                    String f = child.getName();
                    int pos = f.lastIndexOf(".");
                    String inputFileName = pos > 0 ? f.substring(0, pos) : f;
                //end file name capture
                int pageCharsCurrent = 0; //the character counter
                int pageCurrent = 1; //start the page counter
                TreeMap<String, TreeSet<Integer>> word_pages = new TreeMap(); //TreeMap to hold words & page numbers which are in a treeset
                TreeSet<Integer> word_pages_test = new TreeSet(); //create treeset to hold test case below.
                Scanner scnr = new Scanner(child); //scan file
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
                 try {//required error handling for bad file paths
                    String aggFileName = outputFolder+inputFileName+String.valueOf("_output.txt");//generate a file path & file name
                    FileWriter fstream = new FileWriter(aggFileName);//generate the file writer to store the file
                    BufferedWriter out = new BufferedWriter (fstream);//generate the buffered writer to store the file
                    for (Map.Entry<String, TreeSet<Integer>> entry : word_pages.entrySet()){//iterate over the TreeMap
                        String key = entry.getKey();//grab the key from the TreeMap
                        TreeSet<Integer> value_list = entry.getValue(); //pull out the TreeSet from the TreeMap;
                        String values = ""; //crate a string variable to hold the TreeSet values
                        for(int i : value_list){//iterate over the TreeSet values to collapse them into a string for properly formatted print out.
                            values += i+", "; //properly format page number values per assignment
                        }
                        int pos2 = values.lastIndexOf(","); //get last position of a comma for clean up
                        String cleanValues = pos2 > 0 ? values.substring(0, pos2) : values; //clean up the values so there's no trailing comma
                        out.write(key+" "+cleanValues); //write the word & page numbers to the output file
                        out.newLine(); //create a new line for the next set of words & page numbers.
                    }
                    out.close(); //close the file writer.
                }
                catch(IOException e){ //required error handling for writing files
                    e.printStackTrace();
                } 
            }
        }
    long later = System.currentTimeMillis(); //capture execution stop time
    long runTime = later-now; //calculate execution duration
    System.out.println("Execution time in milliseconds: "+runTime); //print out execution duration 
    }  
}  