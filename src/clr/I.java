/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clr;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author soheilchangizi
 */
public class I {
    
    private Set<Item> items;
    private static int ID = 0;
    private int IID = 0; 

    public I() {
        items = new HashSet<>();
        this.IID = ID++;
    }

    public Set<Item> getItems() {
        return items;
    }
    
    public void addItem(Item item){
        items.add(item);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.items);
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
        final I other = (I) obj;
        if (!Objects.equals(this.items, other.items)) {
            return false;
        }
        return true;
    }

    

    @Override
    public String toString() {
        return this.IID + " ";
    }
    
    
    
}


