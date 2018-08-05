/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asck_lex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ALIREZA
 */
public class RegexToDfa {

    /**
     * @param args the command line arguments
     */
    private Set<Integer>[] followPos;
    private Node root;
    private Set<State> DStates;

    private String regex;

    private Set<String> input = new HashSet<String>();
    private HashMap<Integer, String> symbNum;

    private State q0;

    public RegexToDfa(String regex) {
        this.regex = regex + (char)4;
        getSymbols(this.regex);
        SyntaxTree st = new SyntaxTree(this.regex);
        root = st.getRoot();
        followPos = st.getFollowPos();
        DStates = new HashSet<>();

        // Get the start state of the created dfa
        q0 = createDFA();
    }

    private State createDFA() {
        int id = 0;
        Set<Integer> firstpos_n0 = root.getFirstPos();

        State q0 = new State(id++);
        q0.addAllToName(firstpos_n0);
        if (q0.getName().contains(followPos.length)) {
            q0.setAccept();
        }
        DStates.clear();
        DStates.add(q0);
        
        int count = 0;
        while (true) {
            count++;
            boolean exit = true;
            State s = null;
            for (State state : DStates) {
                if (!state.getIsMarked()) {
                    exit = false;
                    s = state;
                }
            }
            if (exit) {
                break;
            }

            if (s.getIsMarked()) {
                continue;
            }
            s.setIsMarked(true); //mark the state
            Set<Integer> name = s.getName();
            for (String a : input) {
                Set<Integer> U = new HashSet<>();
                for (int p : name) {
                    if (symbNum.get(p).equals(a)) {
                        U.addAll(followPos[p - 1]);
                    }
                }
                boolean flag = false;
                State tmp = null;
                for (State state : DStates) {
                    if (state.getName().equals(U)) {
                        tmp = state;
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    State q = new State(id++);
                    q.addAllToName(U);
                    if (U.contains(followPos.length)) {
                        q.setAccept();
                    }
                    DStates.add(q);
                    tmp = q;
                }
                s.addMove(a, tmp);
            }
        }

        return q0;
    }

    private void getSymbols(String regex) {
        input = new HashSet<>();
        symbNum = new HashMap<>();
        int num = 1;
        Set<Character> op = new HashSet<>();
        Character[] ch = {'(', ')', '*', '|', '&', '.', '\\', '[', ']', '+'};
        op.addAll(Arrays.asList(ch));
        for (int i = 0; i < regex.length(); i++) {
            char charAt = regex.charAt(i);
            if (op.contains(charAt)) {
                if (i - 1 >= 0 && regex.charAt(i - 1) == '\\') {
                    if (i - 2 >= 0 && regex.charAt(i - 2) == '\\') {
                        continue;
                    }
                    input.add("\\" + charAt);
                    symbNum.put(num++, "\\" + charAt);
                }
            } else {
                input.add("" + charAt);
                symbNum.put(num++, "" + charAt);
            }
    
        }        
    }

    public State getQ0() {
        return q0;
    }

    public Set<String> getInputSymbols() {
        return input;
    }
}
