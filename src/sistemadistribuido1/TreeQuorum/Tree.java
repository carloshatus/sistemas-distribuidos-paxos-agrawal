package sistemadistribuido1.TreeQuorum;

import java.util.ArrayList;

/**
 *
 * @author carlo_si1d5ve
 */
public class Tree {

    private TreeNode raiz;

    public Tree(TreeNode raiz) {
        this.raiz = raiz;
    }

    public TreeNode getRaiz() {
        return raiz;
    }

    public ArrayList<Tree> getFilhos() {
        return raiz.getFilhos();
    }

    public boolean isFolha() {
        return getFilhos().isEmpty();
    }

    public boolean addFilho(Tree t) {
        raiz.addFilho(t);
        return true;
    }

    @Override
    public String toString() {
        return "" + raiz;
    }

}
