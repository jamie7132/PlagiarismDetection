import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;

public class PlagDetect {
    
    public static void main(String[] args) {
        //vars
        String syns_filename, f1_filename, f2_filename;
        int N = 3;

        //check for acceptable number of arguments
        if (args.length < 3 || args.length > 4) {
            System.out.println("Run with 3 required args (synonyms file, file 1, file 2) and optional third file - tuple size");
            return;
        }

        //set vars with command line arg values
        syns_filename = args[0];
        f1_filename = args[1];
        f2_filename = args[2];
        
        if (args.length == 4) {
            N = Integer.parseInt(args[3]);
        }

        //read in data from files
        ArrayList<String> synonyms = new ArrayList<String>();
        String file1, file2;
        try {
            file1 = new String(Files.readAllBytes(Paths.get(f1_filename))).replaceAll("\n", " ");
            file2 = new String(Files.readAllBytes(Paths.get(f2_filename))).replaceAll("\n", " ");
        } catch(Exception e) {
            System.out.println(e);
            return;
        }

        try {    
            BufferedReader syns_fr = new BufferedReader(new FileReader(syns_filename));
            
            String line;
            
            while ((line = syns_fr.readLine()) != null) {
                synonyms.add(line);
            }

            syns_fr.close();
        } catch(Exception e) {
            System.out.println(e);
            return;
        }
    
        if (file1.split(" ").length < N) {
            System.out.println(NumberFormat.getPercentInstance().format(0));
            return;
        }


        //get a copy of the text where all words have been replaced by their first synonym in the list
        String text1 = synonymReduction(synonyms, file1);
        String text2 = synonymReduction(synonyms, file2);
        
        //calculate percent plagiarised
        double numTuples = text1.split(" ").length - N + 1;
        int numTupleMatches = findNumMatches(text1, text2, N);
        
        double percentPlagiarised = numTupleMatches / numTuples;
        //print and return
        System.out.println(NumberFormat.getPercentInstance().format(percentPlagiarised));
        return;
    }

    //REQUIRES: takes in an ArrayList<String> of synonyms where each String is a line of synonomous words separated by spaces and takes in a String text which is the text to be reduced
    //EFFECT: returns a string in which all the words from the orginal text have been replaced by the first synonym entry if it exists in a synonymous list
    public static String synonymReduction(ArrayList<String> syn, String text) {
        String reduction = "";
        String[] words = text.split(" ");

        //for each word in text, check if it exists in the synonym ArrayList
        for (int word = 0; word < words.length; word++) {
            boolean match = false;
        
            for (int s = 0; s < syn.size() && !match; s++) {
                //if so, then add the first synomous entry to reduction
                if (syn.get(s).contains(words[word])) {
                    match = true;
                    reduction += syn.get(s).split(" ")[0];
                }
            }
            //otherwise, add the word from text
            if (!match) {
                reduction += words[word] + " ";
            }
        }

        return reduction;
    }
    
    //REQUIRES: Two Strings representing the texts to be compared against each other, and N the tuple size to check
    //          Assumption is NOT made that texts are of equal word length
    //EFFECTS: returns the number of N-tuple matches
    public static int findNumMatches(String t1, String text2, int N) {
        int numMatches = 0;
        String[] text1 = t1.split(" ");

        for (int i = 0; i <= text1.length - N; i++) {
            int fromIndex = 0;
            int pos = text2.indexOf(text1[i], fromIndex);
            
            //for all occurences of the text1 word in text2
            while (pos != -1) {
                String[] str = text2.substring(pos).split(" ");
                int tempCount = 1; //since it exists, already matched first word
                 
                //check if an N-tuple match
                for (int j = 1; j < str.length && tempCount < N && tempCount != -1; j++) {
                    
                    if (text1[i + j].equals(str[j])) {
                        tempCount++;
                    } else {
                        tempCount = -1; //break
                    }
                }

                //if so, increase the count
                if (tempCount == N) {
                    numMatches++;
                }

                //continue with next match
                pos = text2.indexOf(text1[i], pos + 1);
            }
        }
        
        return numMatches;
    }
}
