/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clr;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author soheilchangizi
 */
public class Production {

    private NonTerminal LHS;
    private ArrayList<Elements> RHS;
    private String demonRHS;
    private int dotPos;

    public Production(NonTerminal LHS, ArrayList<Elements> RHS, int dotPos, String demonRHS) {
        this.LHS = LHS;
        this.RHS = RHS;
        this.dotPos = dotPos;
        this.demonRHS = demonRHS;
    }

    public Production(NonTerminal LHS, String RHS, int dotPos) {
        this.LHS = LHS;
        this.RHS = new ArrayList<>();
        this.demonRHS = RHS;
        String tmp = "";
        for (int i = 0; i < RHS.length(); i++) {
            if (RHS.charAt(i) == '[') {
                tmp = "";
                while (RHS.charAt(i + 1) != ']') {
                    tmp += RHS.charAt(i + 1) + "";
                    i++;
                }
                i++;
                NonTerminal nt = new NonTerminal(tmp);
                this.RHS.add(nt);
            } else {
                tmp = "";
                while (RHS.charAt(i) != '[' && RHS.charAt(i) != ' ') {
                    tmp += RHS.charAt(i) + "";
                    if (i == RHS.length() - 1) {
                        break;
                    }
                    i++;
                }
                if (RHS.charAt(i) == '[') {
                    i--;
                }
                if (tmp != "") {
                    Terminal t = new Terminal(tmp);
                    this.RHS.add(t);
                }
            }
        }
        this.dotPos = dotPos;
    }

    public Elements getDotElem() {
        if (dotPos <= this.RHS.size() - 1) {
            return this.RHS.get(dotPos);
        } else {
            return new Elements("EEEE");
        }
    }

    public boolean canAdvDotElem() {
        if (this.dotPos + 1 <= this.RHS.size()) {
            return true;
        }
        return false;
    }

    public Production advDotElem() {
        return new Production(LHS, RHS, dotPos + 1, demonRHS);
    }

    public int getDotPos() {
        return dotPos;
    }

    public NonTerminal getLHS() {
        return LHS;
    }

    public ArrayList<Elements> getRHS() {
        return RHS;
    }

    public String getDemonRHS() {
        return demonRHS;
    }

    @Override
    public String toString() {
        String tmp = "";
        for (int i = 0; i < this.RHS.size(); i++) {
            if (i == this.dotPos) {
                tmp += "." + this.RHS.get(i).getName();
            } else {
                tmp += this.RHS.get(i).getName();
            }
        }
        if (this.dotPos == this.RHS.size()) {
            tmp += ".";
        }
        return this.LHS.getName() + " -> " + tmp;

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Production other = (Production) obj;
        if (this.dotPos != other.dotPos) {
            return false;
        }
        if (!Objects.equals(this.demonRHS, other.demonRHS)) {
            return false;
        }
        if (!Objects.equals(this.LHS, other.LHS)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.LHS);
        hash = 43 * hash + Objects.hashCode(this.demonRHS);
        hash = 43 * hash + this.dotPos;
        return hash;
    }

}
