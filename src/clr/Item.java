/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package clr;

import java.util.Objects;

/**
 *
 * @author soheilchangizi
 */
public class Item {
    private Production pro;
    private String LA;
    
    public Item(Production pro, String LA) {
        this.pro = pro;
        this.LA = LA;
    }
    
    public NonTerminal dotBeforeNT(){
        if(pro.getDotElem() instanceof NonTerminal){
            return (NonTerminal)pro.getDotElem();
        }
        return new NonTerminal("]INVALID[");
    }
    
    public Production getPro() {
        return pro;
    }
    
    public String getLA() {
        return LA;
    }

    @Override
    public String toString() {
        return this.pro.toString() + ", " + this.LA;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.pro);
        hash = 67 * hash + Objects.hashCode(this.LA);
        return hash;
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
        final Item other = (Item) obj;
        if (!Objects.equals(this.pro, other.pro)) {
            return false;
        }
        return true;
    }

    
    
    
}
