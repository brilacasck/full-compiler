package asck_lex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author ASCK Friends
 */
public class RegexSimplfier {

    private ArrayList<String> Regexes;
    private ArrayList<String> lefts; //left handside string before ":="
    private ArrayList<String> rights; //right handside string after ":="
    private ArrayList<String> primaryRight;

    /*
     *  keys: the left handside lexeme names
     *  values: the line of the definition of the lexemes in config file
     */
    private Map<String, Integer> lineMap;

    public RegexSimplfier(File file) throws Exception {

        Regexes = new ArrayList<>();
        lefts = new ArrayList<>();
        rights = new ArrayList<>();
        primaryRight = new ArrayList<>();
        lineMap = new HashMap<>();

        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(file);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split(":=");
                this.Regexes.add(line);
                this.lineMap.put(parts[0].trim(), this.Regexes.size() - 1);
                this.lefts.add(parts[0].trim());
                this.rights.add("("+parts[1].trim()+")");
                this.primaryRight.add(parts[1].trim());
            }

            // Always close files.
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '"
                    + file + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                    + file + "'");
            // Or we could just do this: 
            // ex.printStackTrace();
        }

    }

    public String[] Simplify() {
        //return array of regexes
        String[] regexes = new String[this.Regexes.size()];
        for (int i = 0; i < regexes.length; i++) {
            regexes[i] = rights.get(i); //initialing with right handside regexes
        }
        regexes = dotHandling(regexes);
        regexes = simplifyBrackets(regexes);
        regexes = simplifyBackSlash(regexes);
        regexes = simplifyPlus(regexes);

        for (int i = 0; i < regexes.length; i++) {
            regexes[i] = lefts.get(i) + ":=" + regexes[i];
        }
        return regexes;
    }

    private String[] simplifyBackSlash(String[] regexes) {
        String tmp = ""; //temp for the current line string

        for (int i = 0; i < regexes.length; i++) {
            if(i==regexes.length-1){
            }
            /*
             put the right handside regex of current line
             */
            tmp = regexes[i];
            /*
             traverse all characters of current line to find '\'
             */
            for (int j = 0; j < tmp.length(); j++) {
                char curr = tmp.charAt(j); //current character

                if (curr == '\\') {
                    int k = j + 1; //the next char index
                    /*
                     counter for counting the number of next chars of '\' 
                     until the substring size after '\' is 
                     bigger than the maximum of the
                     left handside strings (keys of the lineMap: lexemes)
                     */
                    /////////////////////////////////bool flags
                    boolean contain = true;
                    boolean matched = false;
                    ////////////////////////////////
                    /*
                     the string which is going to be matched
                     with the left handside strings of the lexeme.conf
                     (i.e. the key set of the lineMap hashmap)
                     */
                    int keyMaxSize = 0;
                    for (String s : lefts) {
                        if (s.length() > keyMaxSize) {
                            keyMaxSize = s.length();
                        }
                    }
                    int remainedLen = tmp.substring(k, tmp.length()).length();
                    if (keyMaxSize > remainedLen) {
                        keyMaxSize = remainedLen;
                    }
                    String toMatch = tmp.substring(k, k + keyMaxSize);
                    int counter = keyMaxSize;

                    /*
                     loop for finding the substring comes after '\'
                     and match with the left handside lexeme in conf file
                     and repalcing it with the regex defined in another line
                     */
                    while (contain) {
                        if (counter < 1) {
                            contain = false;
                            break;
                        } else {
                            toMatch = toMatch.substring(0, counter);
                            if (lefts.contains(toMatch)) {
                                for (String s : lefts) {
                                    if (s.equals(toMatch)) {
                                        tmp = tmp.replace(tmp.substring(j, k + counter), "(" + regexes[lineMap.get(toMatch)] + ")");
                                        regexes[i] = tmp;
                                        int nextJ = j + regexes[lineMap.get(toMatch)].length() + 2;
                                        j = nextJ - 1;
                                        matched = true;
                                        break;
                                    }
                                }
                                if (matched) {
                                    break;
                                }
                            }
                            counter--;
                        }
                    }

                }

            }
        }
        return regexes;
    }

    private String[] simplifyBrackets(String[] regexes) {
        String tmp = ""; //temp for the current line string

        for (int i = 0; i < regexes.length; i++) {
            /*
             put the right handside regex of current line
             */
            tmp = regexes[i];
            /*
             traverse all characters of current line to find '['
             */
            for (int j = 0; j < tmp.length(); j++) {
                char curr = tmp.charAt(j); // Current character

                if (curr == '[') {
                    if (j - 1 >= 0 && tmp.charAt(j - 1) == '\\') {
                        continue;
                    }
                    int k = j + 1;
                    int h = tmp.indexOf("]", k);
                    String matched = tmp.substring(k, h); // The string between brackets
                    if (matched.contains("-")) {
                        int k_h = matched.indexOf("-"); // Index of '-' exists between brackets in matched string
                        String leftString = matched.substring(0, k_h);
                        String rightString = matched.substring(k_h + 1, matched.length());
                        if (leftString.length() == 1 && rightString.length() == 1) {
                            char leftChar = leftString.toCharArray()[0];
                            char rightChar = rightString.toCharArray()[0];
                            String toReplace = "";
                            Character[] exceptions = {'*', '|', '&', '(', ')', '[', ']', '.', '\\', '+'};
                            Set<Character> exception = new HashSet<>();
                            exception.addAll(Arrays.asList(exceptions));
                            boolean isDot = (leftChar=='!' && rightChar=='~');  //
                            leftChar = (char) (isDot?(leftChar-1):leftChar);    //
                            for (int l = (int) leftChar; l < (int) rightChar; l++) {
                                if (exception.contains((char) l)) {
                                    toReplace += "\\" + Character.toString((char) l) + "|";
                                } else {
                                    toReplace += Character.toString((char) l) + "|";
                                }
                            }
                            toReplace += Character.toString(rightChar);
                            tmp = tmp.replace("[" + matched + "]", "(" + toReplace + ")");
                            int count = toReplace.length() + 2;
                            j += count - 1;
                        } else {
                            System.out.println("\u001B[34;43mconfig file: line " + i + ":" + "\u001B[0;31m ERROR in converting string between of []");
                            System.exit(0);
                        }
                    }
                }
            }
            regexes[i] = tmp;
        }
        return regexes;
    }

    private String[] simplifyPlus(String[] regexes) {
        String tmp = ""; //temp for the current line string

        for (int i = 0; i < regexes.length; i++) {
            /*
             put the right handside regex of current line
             */
            tmp = regexes[i];
            /*
             traverse all characters of current line to find '['
             */
            for (int j = 0; j < tmp.length(); j++) {
                char curr = tmp.charAt(j); //current character

                if (curr == '+') {
                    int k = j - 1;
                    if (tmp.charAt(k) == ')' && tmp.charAt(k - 1) != '\\') {
                        Stack<Integer> stack = new Stack<>();
                        int h = 0;
                        int paranNum = 0;
                        while (h < k) {
                            if (tmp.charAt(h) == '(') {
                                if (h - 1 >= 0 && tmp.charAt(h - 1) == '\\') {
                                    h++;
                                    continue;
                                }
                                stack.push(++paranNum);
                            } else if (tmp.charAt(h) == ')') {
                                if (h - 1 >= 0 && tmp.charAt(h - 1) == '\\') {
                                    h++;
                                    continue;
                                }
                                if (!stack.isEmpty()) {
                                    stack.pop();
                                } else {
                                    System.out.println("\u001B[34;43mconfig file: line " + i + ":" + "\u001B[0;31m ERROR; unexpected ')");
                                    System.exit(0);
                                }
                            }
                            h++;
                        }
                        paranNum = stack.pop();
                        int counter = 0;
                        int c;
                        for (c = 0; c < tmp.length(); c++) {
                            if (tmp.charAt(c) == '(') {
                                if (c > 0 && tmp.charAt(c - 1) == '\\') {
                                    continue;
                                }
                                counter++;
                                if (counter == paranNum) {
                                    break;
                                }
                            }
                        }
                        ///// Now, let's achieve the included substring among the parentheses "(<--->)"
                        String matched = tmp.substring(c, k + 1);
                        String toReplace = matched + matched + "*";
                        tmp = tmp.replace(matched + "+", toReplace);
                        int nextJ = j + matched.length() + 1;
                        j = nextJ;
                    } else if (tmp.charAt(k) == ')') {
                        String matched = tmp.substring(k - 1, k + 1);
                        String toReplace = matched + matched + "*";
                        tmp = tmp.replace(matched + "+", toReplace);
                        int nextJ = j + matched.length() + 1;
                        j = nextJ;
                    } else if (tmp.charAt(k) != '\\') {
                        String matched = tmp.substring(k, k + 1);
                        String toReplace = matched + matched + "*";
                        tmp = tmp.replace(matched + "+", toReplace);
                        int nextJ = j + matched.length() + 1;
                        j = nextJ;
                    }
                }
            }
            regexes[i] = tmp;
        }
        return regexes;
    }

    private String[] dotHandling(String[] regexes) {
        String tmp = ""; //temp for the current line string

        for (int i = 0; i < regexes.length; i++) {
            /*
             put the right handside regex of current line
             */
            tmp = regexes[i];
            /*
             traverse all characters of current line to find '['
             */
            for (int j = 0; j < tmp.length(); j++) {
                char curr = tmp.charAt(j); //current character

                if (curr == '.') {
                    if (j - 1 >= 0 && tmp.charAt(j - 1) == '\\') {
                        continue;
                    }
                    int k = j - 1; // To replace substring(j-1,j+1) with charAt(j-1)+"[!-~]"
                    int nextJ = j;
                    if (j - 1 >= 0) {
                        tmp = tmp.replace(tmp.substring(k, k + 2), tmp.charAt(k) + "[!-~]");
                    } else {
                        tmp = tmp.replace(".", "[!-~]");
                    }
                    nextJ += 4;
                    j = nextJ;
                }
            }
            regexes[i] = tmp;
        }
        return regexes;
    }
    
    public ArrayList<String> getPrimaryRegex(){
        return primaryRight;
    }

}
