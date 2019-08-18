/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemadistribuido1.TreeQuorum;

import java.util.ArrayList;

/**
 *
 * @author carlo_si1d5ve
 */
public class Quorum { // conjunto maioritário

    private ArrayList<Tree> quorum;

    public Quorum() {
        quorum = new ArrayList<>();
    }

    public boolean addToQuorum(Tree t) {
        if (!quorum.contains(t)) {
            quorum.add(t);
        }
        return true;
    }

//    public boolean addToQuorum(TreeNode t) {
//        return addToQuorum(new Tree(t));
//    }

    public boolean contem(Tree t) { ////
        return quorum.contains(t);
    }

    public boolean addToQuorum(Quorum q) {
//        System.out.println("q "+q.getCoterie());
        if (q != null) {
            for (Tree t : q.getCoterie()) {
                if (!quorum.contains(t)) {
                    quorum.add(t);
                }
            }
        }
        return q == null;
    }

    public ArrayList<Tree> getCoterie() {
        return quorum;
    }

    public boolean isEmpty() {
        return quorum.isEmpty();
    }

}
