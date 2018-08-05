/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author soheilchangizi
 */
public class ParseTable {

    private DFA dfa;
    private Stack<I> States;
    private boolean isAcc;
    private boolean isRej;
    private ArrayList<Production> prods;
    private ArrayList<String> symbs;

    public ParseTable(String File) {
        prods = new ArrayList<>();
        symbs = new ArrayList<>();

        File file = new File(File);
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            int prodCount = 0;

            while ((line = reader.readLine()) != null) {
                String[] sp = line.split("\\s+");
                String tmp = "";
                for (int i = 2; i < sp.length; i++) {
                    if ("|".equals(sp[i])) {
                        if (tmp.charAt(tmp.length() - 1) == ' ') {
                            tmp = tmp.substring(0, tmp.length() - 1);
                        }
                        prods.add(new Production(new NonTerminal(sp[0]), tmp, 0));
                        tmp = "";
                    } else {
                        tmp += (sp[i] + ' ');
                    }
                }
                if (tmp != "") {
                    if (tmp.charAt(tmp.length() - 1) == ' ') {
                        tmp = tmp.substring(0, tmp.length() - 1);
                    }
                    prods.add(new Production(new NonTerminal(sp[0]), tmp, 0));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }

        for (int i = 0; i < prods.size(); i++) {
            if (!symbs.contains(prods.get(i).getLHS().getName())) {
                symbs.add(prods.get(i).getLHS().getName());
            }
            for (int j = 0; j < prods.get(i).getRHS().size(); j++) {
                if (!symbs.contains(prods.get(i).getRHS().get(j).getName())) {
                    symbs.add(prods.get(i).getRHS().get(j).getName());
                }
            }
        }
        symbs.add("$");
        System.out.println(prods);
        this.dfa = new DFA(prods, symbs);
        this.dfa.LR1();
        this.isAcc = false;
        for (I i : dfa.getIs()) {
            for (Item item : i.getItems()) {
                System.out.println(item.toString());
            }
            System.out.println("---------------");
        }
        States = new Stack<>();
        States.push(this.dfa.getI0());

    }

    public boolean checkShift(String terminal) {
        boolean isShift = false;
//        System.out.println("Before: " );
//        System.out.println(States.peek().getItems());

        if (!this.symbs.contains(terminal)) {
            this.isRej = true;
        } else {
            I J = this.dfa.GoTo(States.peek(), terminal);
            if (this.dfa.getIs().contains(J)) {
                isShift = true;
                States.push(J);
            }
        }
//        System.out.println("After: " );
//        System.out.println(States.peek().getItems());
        System.out.println("-----");
        return isShift;
    }

    public boolean checkEpsilon() {
        boolean isEpsilon = false;
        if (this.symbs.contains("ε")) {
            I Je = this.dfa.GoTo(States.peek(), "ε");
            if (this.dfa.getIs().contains(Je)) {
                States.push(Je);
                isEpsilon = true;
            }
        }
        return isEpsilon;
    }

    public void Action(String terminal, String lookahead) {
//        System.out.println("Before: " );
//        System.out.println(States.peek().getItems());

        I J = this.dfa.GoTo(States.peek(), terminal);
        Production p = States.peek().getItems().iterator().next().getPro();

        // Check Reduce
        for (Item item : States.peek().getItems()) {
            if (!item.getPro().canAdvDotElem()) { // dot on the end of prod
                if (item.getLA().equals(lookahead)) { // lookahead match Reduce Prod
                    p = item.getPro();
                }
            }
        }

        // Check Accept
        if (terminal.equals("$") && lookahead.equals("")) {
            if (States.peek().getItems().iterator().next().getPro().getLHS().getName().equals("S'")) {
                this.isAcc = true;
            }
        }

        if (!this.isAcc) {
            if (!States.isEmpty() && p.getRHS().size() < States.size()) {
                for (int i = 0; i < p.getRHS().size(); i++) {
                    States.pop();
                }
                GoTo(p.getLHS().getName());
            } else {
                this.isRej = true;
            }
        }

//        System.out.println("After: " );
//        System.out.println(States.peek().getItems());
        //System.out.println("-----");
    }

    public void GoTo(String nonTerminal) {
        I J = this.dfa.GoTo(States.peek(), nonTerminal);
        if (this.dfa.getIs().contains(J)) {
            States.push(J);
        }
    }

    public boolean Analyse(ArrayList<String> input) {
        int ptr = 0;
        while (!this.isAcc && !this.isRej) {
            if (ptr < input.size() && checkShift(input.get(ptr))) {
                ptr++;
                System.out.println("shift");
            } else if (checkEpsilon()) {
                System.out.println("ε");
            } else if (ptr + 1 < input.size()) {
                Action(input.get(ptr), input.get(ptr + 1));
                System.out.println("reduce");
            } else {
                Action(input.get(ptr), "");
                System.out.println("reduce");
            }
            System.out.println(ptr);
        }
        return this.isAcc;
    }

    public boolean isIsAcc() {
        return isAcc;
    }

    public boolean isIsRej() {
        return isRej;
    }

}
