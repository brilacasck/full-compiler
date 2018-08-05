/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asck_lex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ALIREZA
 */
public class State {

    private int ID;
    private Set<Integer> name;
    private HashMap<String, State> move;

    private boolean IsAcceptable;
    private boolean IsMarked;

    public State(int ID) {
        this.ID = ID;
        move = new HashMap<>();
        name = new HashSet<>();
        IsAcceptable = false;
        IsMarked = false;
    }

    public void addMove(String symbol, State s) {
        move.put(symbol, s);
    }

    public void addToName(int number) {
        name.add(number);
    }

    public void addAllToName(Set<Integer> number) {
        name.addAll(number);
    }

    public void setIsMarked(boolean bool) {
        IsMarked = bool;
    }

    public boolean getIsMarked() {
        return IsMarked;
    }

    public Set<Integer> getName() {
        return name;
    }

    public void setAccept() {
        IsAcceptable = true;
    }

    public boolean getIsAcceptable() {
        return IsAcceptable;
    }

    public State getNextStateBySymbol(String str) {
        return this.move.get(str);
    }

    public HashMap<String, State> getAllMoves() {
        return move;
    }

    public static void showDFA(State q0) {
        State q = q0;
        if (q != null) {
            System.out.println(q.name + " -> ");
            HashMap<String, State> moves = q.getAllMoves();
            for (String str : moves.keySet()) {
                System.out.println(str + " to " + moves.get(str).getName());
            }
            System.out.println("\n------------------------------------------");
            Set<State> states = new HashSet<>();
            states.addAll(moves.values());
            for(State s : states){
                if(!s.getName().isEmpty()){
                    showDFA(s);
                }
            }
        }
    }

}
