/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asck_lex;

import java.util.*;

/**
 *
 * @author ALIREZA
 */
class BinaryTree {

    /*
     ***
     (a|b)*a => creating binary syntax tree:
     .
     / \
     *   a
     /
     |
     / \
     a   b
     ***
     */
    private int leafNodeID;

    // Stacks for symbol nodes and operators
    private Stack<Node> stackNode = new Stack<>();
    private Stack<Character> operator = new Stack<Character>();

    // Set of inputs
    private Set<Character> input = new HashSet<Character>();
    private ArrayList<Character> op = new ArrayList<>();

    // Generates tree using the regular expression and returns it's root
    public Node generateTree(String regular) {
        leafNodeID = 0;
        Character[] ops = {'*', '|', '&'};
        op.addAll(Arrays.asList(ops));

        // Only inputs available
        Character ch[] = new Character[126 - 32 + 1];
        for (int i = 32; i <= 126; i++) {
            ch[i - 32] = (char) i;
        }
        Character integer[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        Character others[] = {'#', '\\', '=', '_', '.', '*', '/', '+', '-', ' ', '(', ')', (char) 4};
        input.addAll(Arrays.asList(ch));
        input.addAll(Arrays.asList(integer));
        input.addAll(Arrays.asList(others));

        // Generate regular expression with the concatenation
        regular = AddConcat(regular);

        // Cleaning stacks
        stackNode.clear();
        operator.clear();

        // Flag which is true when there is something like: \( or \* or etc
        boolean isSymbol = false;

        for (int i = 0; i < regular.length(); i++) {
            if (regular.charAt(i) == '\\' && !isSymbol) {
                isSymbol = true;
                continue;
            }
            if (isSymbol || isInputCharacter(regular.charAt(i))) {
                if (isSymbol) {
                    //create a node with "\{symbol}" symbol 
                    pushStack("\\" + Character.toString(regular.charAt(i)));
                } else {
                    pushStack(Character.toString(regular.charAt(i)));
                }
                isSymbol = false;
            } else if (operator.isEmpty()) {
                operator.push(regular.charAt(i));

            } else if (regular.charAt(i) == '(') {
                operator.push(regular.charAt(i));

            } else if (regular.charAt(i) == ')') {
                while (operator.get(operator.size() - 1) != '(') {
                    doOperation();
                }

                // Pop the '(' left parenthesis
                operator.pop();

            } else {
                while (!operator.isEmpty()
                        && Priority(regular.charAt(i), operator.get(operator.size() - 1))) {
                    doOperation();
                }
                operator.push(regular.charAt(i));
            }
        }

        // Clean the remaining elements in the stack
        while (!operator.isEmpty()) {
            doOperation();
        }

        // Get the complete nfa
        Node completeNfa = stackNode.pop();
        return completeNfa;
    }

    private boolean Priority(char first, Character second) {
        if (first == second) {
            return true;
        }
        if (first == '*') {
            return false;
        }
        if (second == '*') {
            return true;
        }
        if (first == '&') {
            return false;
        }
        if (second == '&') {
            return true;
        }
        if (first == '|') {
            return false;
        }
        return true;
    }

    // Do the desired operation based on the top of stackNode
    private void doOperation() {
        if (this.operator.size() > 0) {
            char charAt = operator.pop();

            switch (charAt) {
                case ('|'):
                    union();
                    break;

                case ('&'):
                    concatenation();
                    break;

                case ('*'):
                    star();
                    break;

                default:
                    System.out.println(">>" + charAt);
                    System.out.println("Unkown Symbol !");
                    System.exit(1);
                    break;
            }
        }
    }

    // Do the star operation
    private void star() {
        // Retrieve top NFA from Stack
        Node nfa = stackNode.pop();

        Node root = new Node("*");
        root.setLeft(nfa);
        root.setRight(null);
        nfa.setParent(root);

        // Put nfa back in the stackNode
        stackNode.push(root);
    }

    // Do the concatenation operation
    private void concatenation() {
        // retrieve nfa 1 and 2 from stackNode
        Node nfa2 = stackNode.pop();
        Node nfa1 = stackNode.pop();

        Node root = new Node("&");
        root.setLeft(nfa1);
        root.setRight(nfa2);
        nfa1.setParent(root);
        nfa2.setParent(root);

        // Put nfa back to stackNode
        stackNode.push(root);
    }

    // Makes union of sub NFA 1 with sub NFA 2
    private void union() {
        // Load two NFA in stack into variables
        Node nfa2 = stackNode.pop();
        Node nfa1 = stackNode.pop();

        Node root = new Node("|");
        root.setLeft(nfa1);
        root.setRight(nfa2);
        nfa1.setParent(root);
        nfa2.setParent(root);

        // Put NFA back to stack
        stackNode.push(root);
    }

    // Push input symbol into stackNode
    private void pushStack(String symbol) {
        Node node = new LeafNode(symbol, ++leafNodeID);
        node.setLeft(null);
        node.setRight(null);

        // Put NFA back to stackNode
        stackNode.push(node);
    }

    // add "." when is concatenation between to symbols that: "." -> "&"
    // concatenates to each other
    private String AddConcat(String regular) {
        String newRegular = new String("");

        for (int i = 0; i < regular.length() - 1; i++) {
            /*
             *#  consider a , b are characters in the Σ
             *#  and the set: {'(', ')', '*', '+', '&', '|'} are the operators
             *#  then, if '&' is the concat symbol, we have to concatenate such expressions:
             *#  a & b
             *#  a & (
             *#  ) & a
             *#  * & a
             *#  * & (
             *#  ) & (
             */
            Character[] ch = {'(', '&', '|'}; //for the input character not the operator
            Set<Character> charInput = new HashSet();
            charInput.addAll(Arrays.asList(ch));
            if (regular.charAt(i) == '\\' && isInputCharacter(regular.charAt(i + 1))) {
                newRegular += regular.charAt(i);
            } else if (regular.charAt(i) == '\\' && regular.charAt(i + 1) == '(') {
                newRegular += regular.charAt(i);
            } else if ((isInputCharacter(regular.charAt(i)) || (charInput.contains(regular.charAt(i)) && i > 0 && regular.charAt(i - 1) == '\\')) && isInputCharacter(regular.charAt(i + 1))) {
                if (i > 1 && regular.charAt(i - 1) == '\\' && regular.charAt(i - 2) == '\\') {
                    newRegular += regular.charAt(i);
                } else {
                    newRegular += regular.charAt(i) + "&";
                }
            } else if ((isInputCharacter(regular.charAt(i)) || (charInput.contains(regular.charAt(i)) && i > 0 && regular.charAt(i - 1) == '\\')) && regular.charAt(i + 1) == '(') {
                if (i > 1 && regular.charAt(i - 1) == '\\' && regular.charAt(i - 2) == '\\') {
                    newRegular += regular.charAt(i);
                } else {
                    newRegular += regular.charAt(i) + "&";
                }

            } else if (regular.charAt(i) == ')' && isInputCharacter(regular.charAt(i + 1))) {
                newRegular += regular.charAt(i) + "&";

            } else if (regular.charAt(i) == '*' && isInputCharacter(regular.charAt(i + 1))) {
                newRegular += regular.charAt(i) + "&";

            } else if (regular.charAt(i) == '*' && regular.charAt(i + 1) == '(') {
                newRegular += regular.charAt(i) + "&";

            } else if (regular.charAt(i) == ')' && regular.charAt(i + 1) == '(') {
                newRegular += regular.charAt(i) + "&";

            } else {
                newRegular += regular.charAt(i);
            }

        }
        newRegular += regular.charAt(regular.length() - 1);
        return newRegular;
    }

    //ALIREZA
    // Return true if is part of the automata Language else is false
    private boolean isInputCharacter(char charAt) {

        if (op.contains(charAt)) {
            return false;
        }
        for (Character c : input) {
            if ((char) c == charAt && charAt != '(' && charAt != ')') {
                return true;
            }
        }
        return false;
    }
    /* This funtcion is here just to test buildTree() */

    public void printInorder(Node node) {
        if (node == null) {
            return;
        }

        /* first recur on left child */
        printInorder(node.getLeft());

        /* then print the data of node */
        System.out.print(node.getSymbol() + " ");

        /* now recur on right child */
        printInorder(node.getRight());
    }

    public int getNumberOfLeafs() {
        return leafNodeID;
    }

}
