/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asck_lex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ALIREZA
 */
public class DFA_Traversal {

    private final State q0;
    private State curr;
    private char c;
    private final Set<String> input;
    private Set<Character> op;

    public DFA_Traversal(State q0, Set<String> input) {
        this.q0 = q0;
        this.curr = this.q0;
        this.input = input;
        op = new HashSet<>();
        Character[] ch = {'(', ')', '*', '|', '&', '.', '\\', '[', ']', '+'};
        op.addAll(Arrays.asList(ch));
    }

    public boolean setCharacter(char c) {
        String s = Character.toString(c);
        if (!input.contains(s)) {
            if (!input.contains("\\" + s)) {
                return false;
            }
        }
        this.c = c;
        return true;
    }

    public boolean traverse() {
        if (op.contains(c)) {
            curr = curr.getNextStateBySymbol("\\" + c);
        } else {
            curr = curr.getNextStateBySymbol("" + c);
        }
        return curr.getIsAcceptable();
    }

    public void flush() {
        curr = q0;
    }

    public boolean CanMoveBy(String symbol) {
        if (op.contains(symbol.charAt(0))) {
            symbol = "\\" + symbol;
        }
        return (curr.getNextStateBySymbol(symbol) != null);
    }

    public State getNextStateBySymbol(String symbol) {
        if (op.contains(symbol.charAt(0))) {
            symbol = "\\" + symbol;
        }
        return curr.getNextStateBySymbol(symbol);
    }
    
    public State getCurrState(){
        return curr;
    }
}
