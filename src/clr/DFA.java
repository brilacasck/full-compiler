/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author soheilchangizi
 */
public class DFA {

    private Set<I> is;
    private I i0;
    private ArrayList<Production> prods;
    private ArrayList<String> symbols;

    public DFA(ArrayList<Production> prods, ArrayList<String> symbols) {
        this.is = new HashSet<>();
        this.prods = prods;
        this.symbols = symbols;
    }

    public Set<String> First(ArrayList<Elements> elems) {
//        System.out.print(".............");
//        for(Elements e: elems){
//            System.out.print(e.getName()+",");
//        }
//        System.out.println("");

        if (elems.get(0) instanceof Terminal) {
            Set<String> tmp = new HashSet<>();
            tmp.add(elems.get(0).getName());
            return tmp;
        } else {
            Set<String> First = new HashSet<>();
            Set<String> tmpFirst = new HashSet<>();
            boolean isEps = false;

            for (int i = 0; i < elems.size(); i++) {
                if (elems.get(i) instanceof Terminal) {
                    ArrayList<String> tmp = new ArrayList<>();
                    First.add(elems.get(i).getName());
                    return First;
                } else {
                    for (Production p : prods) {
                        if (p.getLHS().getName().equals(elems.get(i).getName())) {
                            tmpFirst = First(p.getRHS());
                            for (String string : tmpFirst) {
                                First.add(string);
                            }
                            if (tmpFirst.contains("ε")) {
                                isEps = true;
                            }
                            tmpFirst.clear();
                        }
                    }
                    if (!isEps) {
                        break;
                    }
                }
            }
            return First;
        }

    }

    public I Clouser(I I) {
        //        repeat
        //                for (each item [ A -> ?.B?, a ] in I )
        //                for (each production B -> ? in G’)
        //                for (each terminal b in FIRST(?a))
        //                add [ B -> .? , b ] to set I;
        //                until no more items are added to I;
        //                return I;

        Set<Item> ist = new HashSet<>();

        int preSize = I.getItems().size() - 1;
        int Size = I.getItems().size();
        while (Size != preSize) {
            preSize = I.getItems().size();

            ist.addAll(I.getItems());
            for (Item i : ist) {

                if (!i.dotBeforeNT().getName().equals("]INVALID[")) {
                    for (Production p : prods) {
                        if (i.dotBeforeNT().getName().equals(p.getLHS().getName())) {
                            ArrayList<Elements> eltmp = new ArrayList<>();
                            for (int j = i.getPro().getDotPos() + 1; j < i.getPro().getRHS().size(); j++) {
                                eltmp.add(i.getPro().getRHS().get(j));
                            }
                            Terminal Lookahead = new Terminal(i.getLA());
                            eltmp.add(Lookahead);
                            Set<String> tmpF = First(eltmp);
                            for (String la : tmpF) {
                                Item tmp = new Item(p, la);
                                I.addItem(tmp);
                            }
                        }
                    }
                }
            }

            Size = I.getItems().size();
        }
        return I;
    }

    public I GoTo(I I, String X) {
        //        Initialise J to be the empty set;
        //        for ( each item A -> ?.X?, a ] in I )
        //        Add item A -> ?X.?, a ] to se J;   /* move the dot one step */
        //        return Closure(J);    /* apply closure to the set */

        I J = new I();
        for (Item i : I.getItems()) {

            if (i.getPro().getDotElem().getName().equals(X)) {
                if (i.getPro().canAdvDotElem()) {
                    Item tmp = new Item(i.getPro().advDotElem(), i.getLA());
                    J.addItem(tmp);
                }
            }
        }
        J = Clouser(J);

        return J;
    }

    public void LR1() {
        //        Initialise C to { closure ({[S’ -> .S, $]})};
        //        Repeat
        //                For (each set of items I in C)
        //                For (each grammar symbol X)
        //                if( GOTO(I, X) is not empty and not in C)
        //                Add GOTO(I, X) to C;
        //        Until no new set of items are added to C;
        Item augmented = new Item(new Production(new NonTerminal("S'"), "[S]", 0), "$");
        i0 = new I();
        i0.addItem(augmented);
        i0 = Clouser(i0);
        is.add(i0);

        Set<I> ist = new HashSet<>();

        int preSize = is.size() - 1;
        int Size = is.size();
        while (Size != preSize) {
            preSize = is.size();
            ist.addAll(is);

            for (I i : ist) {
                for (String sym : this.symbols) {
                    I tmp = GoTo(i, sym);
                    if (tmp.getItems().size() > 0 && !is.contains(tmp)) {
                        is.add(tmp);
                    }
                }
            }

            Size = is.size();
            //System.out.println(preSize + " = " + Size);
        }
    }

    public Set<I> getIs() {
        return is;
    }

    public I getI0() {
        return i0;
    }

}
