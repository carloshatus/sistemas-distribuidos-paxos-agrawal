package sistemadistribuido1.TreeQuorum;

import java.util.ArrayList;
import sistemadistribuido1.Host.HostId;

/**
 *
 * @author carlo_si1d5ve
 */
public class TreeNode {

    private HostId no;
    private ArrayList<Tree> filhos;
    private boolean acessivel;

    public TreeNode(HostId no) {
        this.no = no;
        this.no.setTreeNode(this);
        filhos = new ArrayList<>();
        acessivel = true;
    }

    public HostId getNo() {
        return no;
    }

    public boolean isAcessivel(String id) {
        return acessivel;
    }

    public ArrayList<Tree> getFilhos() {
        return filhos;
    }

    public void setAcessivel(boolean acessivel) {
        this.acessivel = acessivel;
    }

    public boolean addFilho(Tree t) {
        filhos.add(t);
        return true;
    }

    public boolean remFilho(Tree t) {
        filhos.remove(t);
        return true;
    }

    @Override
    public String toString() {
        return "" + no;
    }
}
