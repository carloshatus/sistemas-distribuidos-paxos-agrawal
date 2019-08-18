package sistemadistribuido1.Host;

import sistemadistribuido1.TreeQuorum.TreeNode;

/**
 *
 * @author carlo
 */
public class HostId {

    private String ip;
    private int porta;
    private String id;
    private TreeNode treeNode;

    public HostId(String ip, int porta, String id) {
        this.ip = ip;
        this.id = id;
        this.porta = porta;
        this.treeNode = null;
    }

    public HostId(String ip, int porta, int id) {
        this.ip = ip;
        this.id = "Host " + id;
        this.porta = porta;
    }

    public String getId() {
        return id;
    }

    public void setId(int id) {
        this.id = "Host " + id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    @Override
    public String toString() {
        return id + ": " + ip + "/" + porta;
    }

    public void setTreeNode(TreeNode t) {
        treeNode = t;
    }

    public TreeNode getTreeNode() {
        return treeNode;
    }

}
