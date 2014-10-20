import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;

public class PlagDetect {
    
    public static void main(String[] args) {
        String syns_filename, f1_filename, f2_filename;
        int N = 3;

        if (args.length < 3 || args.length > 4) {
            System.out.println("Run with 3 required args (synonyms file, file 1, file 2) and optional third file - tuple size");
            return;
        }

        syns_filename = args[0];
        f1_filename = args[1];
        f2_filename = args[2];
        
        if (args.length == 4) {
            N = Integer.parseInt(args[3]);
        }

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
    
        String[] text1 = synonymReduction(synonyms, file1);
        String[] text2 = synonymReduction(synonyms, file2);
        
        double numTuples = Math.ceil((double) text1.length / N);
        int numTupleMatches = findNumMatches(text1, text2, N);

        double percentPlagiarised = numTupleMatches / numTuples;

        System.out.println(NumberFormat.getPercentInstance().format(percentPlagiarised));
    }

    public static String[] synonymReduction(ArrayList<String> syn, String text) {
        String reduction = "";
        String[] words = text.split(" ");
        for (int word = 0; word < words.length; word++) {
            boolean match = false;
            for (int s = 0; s < syn.size() && !match; s++) {
                if (syn.get(s).contains(words[word])) {
                    match = true;
                    reduction += syn.get(s).split(" ")[0];
                }
            }
            if (!match) {
                reduction += words[word] + " ";
            }
        }

        return reduction.split(" ");
    }

    public static int findNumMatches(String[] text1, String[] text2, int N) {
        int numMatches = 0;
        int numSequentialMatches = 0;

        for (int i = 0; i < text1.length; i++) {
            if (text1[i].equals(text2[i])) {
                numSequentialMatches++;
            } else if (numSequentialMatches >= N) {
                numMatches += numSequentialMatches - N + 1;
                numSequentialMatches = 0;
            }
        }
        
        if (numSequentialMatches >= N) {
            numMatches += numSequentialMatches - N + 1;
            numSequentialMatches = 0;
        }

        return numMatches;
    }
}
