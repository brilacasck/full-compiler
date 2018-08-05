/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asck_lex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javafx.util.Pair;

/**
 *
 * @author ASCK Friends
 */
public class Tokenizer {

    private String text;
    private ArrayList<Pair<String, String>> Tokenized;
    private ArrayList<String>[] Errors;
    private char[] buffer1;
    private char[] buffer2;
    private int halfBuffersize;
    private int textIndex;

    private String[] lefts;
    private ArrayList<String> primaryRight;

    private RegexToDfa[] regToDFA;
    private State dfa[];
    private DFA_Traversal[] dfat;

    private boolean isEOF = false;
    private boolean isDelimiter = false;
    private boolean hereIsDelimiter = false;
    private boolean isIgnored = true;
    private boolean operatorPaired = false;
    private boolean isTheFirstCharOfNewLine = false;
    private boolean isComment = false;

    private boolean isFirstAccepted = false;
    private int firstAcceptedIndex = -1;

    /**
     * variables having "should go on" : for handling the literal and comments
     * which has a start and end character which are as same as each other in
     * the DFA function if the string have be seen till now, is not accepted
     * then, we have to check if it was start of a literal or comment so if it
     * is start of a literal or a comment, we assign (souldGoOn = true)
     */
    private ArrayList<Boolean> isEndOfShouldGoOn;
    private ArrayList<Character> startCharOfShouldGoOn;

    private char nextDelimiter;
    private int downCharCounter = -1;
    private int upCharCounter = -1;

    private boolean ignore[];

    private int lineCount = 0;

    private Set<Character> delimiter;
    private Set<String> opera;

    public Tokenizer(String text, int halfBuffersize, String[] lefts, String rights[], ArrayList<String> primaryRight) {
        this.text = text;
        Tokenized = new ArrayList<>();
        Errors = new ArrayList[2];
        Errors[0] = new ArrayList<>(); // This is for the line number, error occured
        Errors[1] = new ArrayList<>(); // This is for the error string

        this.halfBuffersize = halfBuffersize;
        this.buffer1 = new char[this.halfBuffersize];
        this.buffer2 = new char[this.halfBuffersize];
        buffer1[this.halfBuffersize - 1] = 3; // End of text (instead of eof)
        buffer2[this.halfBuffersize - 1] = 3; // End of text (instead of eof)
        this.textIndex = 0;
        this.lefts = lefts;
        this.primaryRight = primaryRight;

        regToDFA = new RegexToDfa[lefts.length];
        dfa = new State[lefts.length];
        dfat = new DFA_Traversal[lefts.length];
        ignore = new boolean[lefts.length];
        for (int i = 0; i < lefts.length; i++) {
            regToDFA[i] = new RegexToDfa(rights[i]);
            dfa[i] = regToDFA[i].getQ0();
            dfat[i] = new DFA_Traversal(dfa[i], regToDFA[i].getInputSymbols());
            ignore[i] = false;
        }

        Character[] ch = {' ', '(', ')', '[', ']', '{', '}', '+', '-', '=', ':', '*', '/', '?', '%', '!', '<', '>', ',', ';', '&', '|'};
        delimiter = new HashSet<>();
        delimiter.addAll(Arrays.asList(ch));

        String[] operas = {"==", "!=", "<=", ">=", ">>", "<<", "++", "--", "+=", "-=", "*=", "/=", ":=", "&&", "||"};
        opera = new HashSet<>();
        opera.addAll(Arrays.asList(operas));
        
        isEndOfShouldGoOn = new ArrayList<>();
        startCharOfShouldGoOn = new ArrayList<>();
    }

    public ArrayList<Pair<String, String>> tokenize() {
        //achieve the start/end character of literal or comment
        primaryRight.forEach((str) -> {
            String[] DotRegexes = str.split("\\|");
            for (String DotRegexe : DotRegexes) {
                if (DotRegexe.matches(".+\\.\\*.+")) {
                    int index = DotRegexe.indexOf(".*");
                    if (index >= 0 && index < DotRegexe.length()) {
                        char start = (DotRegexe.charAt(index - 1));
                        char end = DotRegexe.charAt(index + 2);
                        end = (end == '\\') ? DotRegexe.charAt(index + 2) : end;
                        if (end == start) {
                            if (!startCharOfShouldGoOn.contains(start)) {
                                startCharOfShouldGoOn.add(start);
                                isEndOfShouldGoOn.add(false);
                            }
                        }
                    }
                }
            }
        });
        
        boolean flag = true;
        boolean isReadingFirst = true;
        text = modifyText(text);
        reloadBuffer(true);
        while (flag) {

            for (int i = 0; i < halfBuffersize - 1; i++) {
                upCharCounter++;
                if (isReadingFirst) {
                    if (delimiter.contains(buffer1[i])) {
                        hereIsDelimiter = true;
                    }
                    if (buffer1[i] == '\n') {
                        isTheFirstCharOfNewLine = true;
                        lineCount++;
                    }
                    if (buffer1[i] == (char) 3) {
                        flag = false;
                        break;
                    } else {
                        if (i + 1 < buffer1.length - 1 && (buffer1[i + 1] == '\n' | buffer1[i + 1] == (char) 3)) {
                            isEOF = true;
                        }
                        if (i + 1 < buffer1.length - 1 && delimiter.contains(buffer1[i + 1])) {
                            isDelimiter = true;
                            nextDelimiter = buffer1[i + 1];
                        }
                        if (i == halfBuffersize - 2) {
                            reloadBuffer(false);
                            isReadingFirst = false;
                            if ((buffer2[0] == '\n' | buffer2[0] == (char) 3)) {
                                isEOF = true;
                            }
                            if (delimiter.contains(buffer2[0])) {
                                isDelimiter = true;
                                nextDelimiter = buffer2[0];
                            }
                        }
                        if (buffer1[i] == '\n') {
                            for (int k = 0; k < isEndOfShouldGoOn.size(); k++) {
                                isEndOfShouldGoOn.set(k, false);
                            }
                            continue;
                        }
                        DFA(buffer1[i]);
                    }
                } else {
                    if (delimiter.contains(buffer2[i])) {
                        hereIsDelimiter = true;
                    }
                    if (buffer2[i] == '\n') {
                        lineCount++;
                        isTheFirstCharOfNewLine = true;
                    }
                    if (buffer2[i] == (char) 3) {
                        flag = false;
                        break;
                    } else {
                        if (i + 1 < buffer2.length - 1 && (buffer2[i + 1] == '\n' | buffer2[i + 1] == (char) 3)) {
                            isEOF = true;
                        }
                        if (i + 1 < buffer2.length - 1 && delimiter.contains(buffer2[i + 1])) {
                            isDelimiter = true;
                            nextDelimiter = buffer2[i + 1];
                        }
                        if (i == halfBuffersize - 2) {
                            reloadBuffer(true);
                            isReadingFirst = true;
                            if ((buffer1[0] == '\n' | buffer1[0] == (char) 3)) {
                                isEOF = true;
                            }
                            if (delimiter.contains(buffer1[0])) {
                                isDelimiter = true;
                                nextDelimiter = buffer1[0];
                            }
                        }
                        if (buffer2[i] == '\n') {
                            for (int k = 0; k < isEndOfShouldGoOn.size(); k++) {
                                isEndOfShouldGoOn.set(k, false);
                            }
                            continue;
                        }
                        DFA(buffer2[i]);
                    }
                }
            }

        }

        return Tokenized;
    }

    private void reloadBuffer(boolean isFirst) {

        for (int i = textIndex; i < text.length() && i < textIndex + halfBuffersize - 1; i++) {
            if (isFirst) {
                buffer1[i - textIndex] = text.charAt(i);
            } else {
                buffer2[i - textIndex] = text.charAt(i);
            }
        }
        textIndex += halfBuffersize - 1;

    }

    private void DFA(char in) {
        boolean accepted = false;
        int i;
        ////// check if the first character of new line, is in the delimiter set
        if ((upCharCounter == 0 || isTheFirstCharOfNewLine) && delimiter.contains(in)) {
            isTheFirstCharOfNewLine = false;
            hereIsDelimiter = true;
        }
        ///////check if the characteer is the literal symbol
        boolean IsDelimiterNextOfLiteral = false;
        for (int k = 0; k < startCharOfShouldGoOn.size(); k++) {
            if (startCharOfShouldGoOn.contains(in)) {
                int index = startCharOfShouldGoOn.indexOf(in);
                isEndOfShouldGoOn.set(index, !isEndOfShouldGoOn.get(index));
                if (isEndOfShouldGoOn.get(index) && delimiter.contains(nextDelimiter)) {
                    IsDelimiterNextOfLiteral = true;
                }
                break;
            }
        }
        //////
        if (!operatorPaired && isDelimiter && opera.contains(in + "" + nextDelimiter)) {
            operatorPaired = true;
        } else if (operatorPaired) {
            operatorPaired = false;
        }
        isIgnored = true;

        for (i = 0; i < lefts.length; i++) {
            if (ignore[i]) {
                continue;
            }
            accepted = false;
            if (!dfat[i].setCharacter(in) || (dfat[i].CanMoveBy(in + "") && dfat[i].getNextStateBySymbol(in + "").getName().isEmpty())) {
                if (dfat[i].setCharacter(in)) {
                    if (!delimiter.contains(in)) {
                        ignore[i] = true;
                        continue;
                    } else {
                        hereIsDelimiter = true;
                    }
                } else {
                    ignore[i] = true;
                    continue;
                }
            }
            isIgnored = false;

            if (primaryRight.get(i).matches(".+\\.\\*$")) {
                if (dfat[i].CanMoveBy(in + "") && !dfat[i].getNextStateBySymbol(in + "").getName().isEmpty()) {
                    if ((hereIsDelimiter && isDelimiter && nextDelimiter != ' ')) {
                        dfat[i].setCharacter(in);
                        dfat[i].traverse();
                        if ((dfat[i].CanMoveBy(nextDelimiter + "") && !dfat[i].getNextStateBySymbol(nextDelimiter + "").getName().isEmpty())) {
                            isComment = true;
                            if (isEOF) {
                                accepted = true;
                            }
                            hereIsDelimiter = false;
                            isDelimiter = false;
                            break;
                        }
                    } else if (isComment) {
                        dfat[i].setCharacter(in);
                        dfat[i].traverse();
                        if (isEOF) {
                            accepted = true;
                        }
                        hereIsDelimiter = false;
                        isDelimiter = false;
                        break;
                    }
                }
            }

            /**
             * check if this character can be accepted or not
             */
            if (dfat[i].traverse()) {
                ///////////////////////////////////////////////////////////////
                if (hereIsDelimiter && isDelimiter) {
                    if (operatorPaired && (!dfat[i].CanMoveBy(nextDelimiter + "") || dfat[i].getNextStateBySymbol(nextDelimiter + "").getName().isEmpty())) {
                        continue;
                    }
                }
                accepted = true;
                boolean hereWasDelimiter = false;
                if (hereIsDelimiter) {
                    hereWasDelimiter = true;
                    if (!isDelimiter) {
                        hereIsDelimiter = false;
                    }
                    isEOF = true;
                    if (isDelimiter) {
                        isDelimiter = false;
                        opera.forEach((str) -> {
                            if (str.charAt(0) == in && str.charAt(1) == nextDelimiter) {
                                if (operatorPaired) {
                                    isEOF = false;//PERHAPS TRUE
                                }
                            }
                        });
                        if (!isEOF) {
                            accepted = false;
                            break;
                        }
                    }
                }
                if (!isFirstAccepted) {
                    isFirstAccepted = accepted;
                    if (isFirstAccepted) {
                        firstAcceptedIndex = i;
                    }
                }
                if (isDelimiter && !hereWasDelimiter) {
                    isDelimiter = false;
                    hereIsDelimiter = true;
                    isEOF = true;
                    
                    if (!isEndOfShouldGoOn.contains(true)) {
                        if (startCharOfShouldGoOn.contains(in)) {
                            isEOF = true;
                            break;
                        }
                    } else {
                        if (IsDelimiterNextOfLiteral) {
                            isEOF = false;
                            hereIsDelimiter = false;
                            IsDelimiterNextOfLiteral = false;
                            continue;
                        }
                    }
                    
                    if (!dfat[i].CanMoveBy(nextDelimiter + "")) {
                        isEOF = true;
                    } else {
                        if (!dfat[i].getNextStateBySymbol(in + "").getName().isEmpty()) {
                            accepted = false;
                            isEOF = false;
                            break;
                        }
                    }
                }
                if (isEOF) {
                    break;
                }
                ///////////////////////////////////////////////////////////////

            } else {

                ///////////////////////////////////////////////////////////////
                int k = -1;
                for (int m = 0; m < lefts.length; m++) {
                    if (ignore[m] == false) {
                        k = m;
                    }
                }
                boolean hereWasDelimiter = false;
                if (i == k && hereIsDelimiter) {
                    hereWasDelimiter = true;
                    if (!isDelimiter) {
                        hereIsDelimiter = false;
                    } else {
                        isDelimiter = false;
                    }
                    isEOF = true;
                    if (isEndOfShouldGoOn.contains(true)) {
                        isEOF = false;
                        break;
                    }
                    if (hereIsDelimiter && opera.contains(in + "" + nextDelimiter)) {
                        isEOF = false;
                    }

                    //new added
                    if (!dfat[i].getCurrState().getName().isEmpty()) {
                        isEOF = false;
                    }
                    if (isEOF) {
                        break;
                    }
                }

                /**
                 * if the next character is a delimiter and all the DFAs didn't
                 * accepted the string till now, we have to check maybe this
                 * substring is a part of a literal string or something like
                 * that
                 */
                if (i == k && isDelimiter && !hereWasDelimiter) {
                    //isEOF = true;
                    if (startCharOfShouldGoOn.contains(in)) {
                        int index = startCharOfShouldGoOn.indexOf(in);
                        if (isEndOfShouldGoOn.get(index)) {
                            isDelimiter = false;
                            hereIsDelimiter = false;
                        }
                    } else {
                        if (!dfat[i].CanMoveBy(nextDelimiter + "") || dfat[i].getNextStateBySymbol(nextDelimiter + "").getName().isEmpty()) {
                            isDelimiter = false;
                            hereIsDelimiter = true;
                            isEOF = true;
                            break;
                        } else {
                            if (!isEndOfShouldGoOn.contains(true)) {
                                isDelimiter = false;
                                hereIsDelimiter = true;
                                isEOF = true;
                                if (primaryRight.get(i).contains(nextDelimiter + "")) {
                                    hereIsDelimiter = false;
                                    isEOF = false;
                                }
                                break;
                            } else {
                                isDelimiter = false;
                                hereIsDelimiter = false;
                                isEOF = false;
                                break;
                            }
                        }
                    }
                }
                ///////////////////////////////////////////////////////////////

            }
        }

        /**
         * checking if no DFAs can traverse the symbols in the input
         */
        if (isIgnored && (isDelimiter || hereIsDelimiter)) {
            isEOF = true;
            isIgnored = false;
            hereIsDelimiter = isDelimiter;
            isDelimiter = false;
        }

        /**
         * check if the string till now, can be accepted and the string is
         * finished (not any more characters should be added to string)
         */
        if (accepted && isEOF) {
            Tokenized.add(new Pair(lefts[i], text.substring(downCharCounter + 1, upCharCounter + 1).replace("\n", "")));
            for (i = 0; i < lefts.length; i++) {
                dfat[i].flush();
            }
            downCharCounter = upCharCounter;
            isEOF = false;
            isComment = false;
            for (int j = 0; j < ignore.length; j++) {
                ignore[j] = false;
            }
        } /////ERROR HANDLING SEGMENT
        else if (isEOF) {
            isEOF = false;
            for (i = 0; i < lefts.length; i++) {
                dfat[i].flush();
            }
            if (text.substring(downCharCounter + 1, upCharCounter + 1).equalsIgnoreCase(" ")) {
                downCharCounter = upCharCounter;
                for (int j = 0; j < ignore.length; j++) {
                    ignore[j] = false;
                }
                return;
            }
            System.out.println("\u001B[31m!!ERROR!!\t\u001B[35m" + text.substring(downCharCounter + 1, upCharCounter + 1) + "\u001B[0m");
            Errors[0].add(Integer.toString(lineCount));
            Errors[1].add(text.substring(downCharCounter + 1, upCharCounter + 1).replace("\n", ""));
            downCharCounter = upCharCounter;
            for (int j = 0; j < ignore.length; j++) {
                ignore[j] = false;
            }
        }

    }

    private String modifyText(String text) {
        text = text.replaceAll(" +", " ");
        //text = text.replaceAll(" *= *", "=");
        //text = text.replaceAll(" *: *", ":");
        //text = text.replaceAll(" *\\+ *", "+");
        //text = text.replaceAll(" *- *", "-");
        //text = text.replaceAll(" *\\* *", "*");
        //text = text.replaceAll(" */ *", "/");
        text = text.replace("\t", " ");
        text = text.replace("\r", "");
        text = text.replaceAll(" *\n *", "\n");
        text = text.replaceAll("\n+", "\n");
        text = text.trim();
        text += Character.toString((char) 3);
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLefts(String[] lefts) {
        this.lefts = lefts;
    }

    public void flushAll() {
        for (int i = 0; i < lefts.length; i++) {
            dfat[i].flush();
        }
    }

    public ArrayList<String>[] getErrors() {
        return Errors;
    }

    public void clear() {
        isEOF = false;
        downCharCounter = -1;
        upCharCounter = -1;
        textIndex = 0;
        Tokenized.clear();
    }
}
