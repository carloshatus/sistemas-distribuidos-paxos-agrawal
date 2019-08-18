package sistemadistribuido1.TreeQuorum;

import java.util.ArrayList;
import java.util.Random;
import sistemadistribuido1.Host.HostId;

/**
 *
 * @author carlo_si1d5ve
 */
public class TreeQuorum {

    private ArrayList<Tree> arvore;
    private String id;
    private int d;

    public TreeQuorum(String id) {
        arvore = new ArrayList<>();
        this.id = id;
        d = 4;
    }

    public void gerarArvore(ArrayList<HostId> arrayHosts) {

        //        System.out.println(arrayHosts);
//        Arvore d=3; h=4 40 nós
        arvore.clear();
        for (HostId h : arrayHosts) {
            arvore.add(new Tree(new TreeNode(h)));
        }
        int cont = 1;
        int antes = cont * d - cont - 1;
        int depois = 1;
//        System.out.println("antes e depois," + antes + ", " + depois);
        while (cont * d + depois <= arvore.size()) {
            for (int i = (cont * d - antes - 1), j = 0; j < d; j++) {
                if (i < arvore.size()) {
                    arvore.get(cont - 1).addFilho(arvore.get(i++));
                }
            }
//            System.out.println(cont + " filhos " + arvore.get(cont - 1).getFilhos());
            cont++;
        }
        int resto = arvore.size() - ((cont - 1) * d + depois);
        for (int i = resto; i > 0; i--) {
            arvore.get(cont - 1).addFilho(arvore.get(arvore.size() - i));
        }
//        System.out.println(arvore.get(cont - 1).getRaiz() + " "+ arvore.get(cont - 1).getFilhos().toString());
        System.out.println(id + " tem uma nova árvore de: " + arvore.size() + " Nós; h = " + heigth(arvore.get(0)) + "; d = " + d + "; completa: " + isCompleta(arvore.get(0)));

    }

    public Quorum getQuorum(int length, int width) {
        if (!arvore.isEmpty()) {
            return getQuorum(arvore.get(0), length, width);
        } else {
            return new Quorum(); // lista vazia
        }
    }

    private Quorum getQuorum(Tree tree, int length, int width) {
        Quorum childQuorum = new Quorum();
        Quorum tempQuorum = new Quorum();
//        System.out.println("entrou " + tree + " e seus filhos " + tree.getFilhos());
        if (length > heigth(tree)) {
//            System.out.println("l>h");
            return null; // erro
        } else if (length == 0) {
//            System.out.println("l=0");
            return new Quorum(); // lista vazia
        } else if (tree.getRaiz().isAcessivel(id)) {
//            System.out.println("raiz");

            int cont = 0;
            if (!tree.isFolha()) {
//                System.out.println("nao e folha");
                for (Tree t : tree.getFilhos()) {
                    if (t.getRaiz().isAcessivel(id)) {
//                        System.out.println("acessivel");
                        tempQuorum.addToQuorum(t);
                        cont++;
                    }
                    if (cont >= width) {
                        break;
                    }
                }
                while (cont < width) {
                    Random r = new Random();
                    int n = r.nextInt(tree.getFilhos().size());
                    if (!tempQuorum.contem(tree.getFilhos().get(n))) {
                        tempQuorum.addToQuorum(tree.getFilhos().get(n));
                        cont++;
                    }
                }
                for (int i = 0; i < width; i++) {
//                    System.out.println(i + " - recursão " + length + " " + tempQuorum.getCoterie());
                    childQuorum.addToQuorum(getQuorum(tempQuorum.getCoterie().get(i), length - 1, width));
                }
            }
            childQuorum.addToQuorum(tree);
//            System.out.println(childQuorum.getCoterie().toString());
            return childQuorum;
        } else {
//            System.out.println("sem raiz");
            int cont = 0;
            if (!tree.isFolha()) {
//                System.out.println("nao e folha");
                for (Tree t : tree.getFilhos()) {
                    if (t.getRaiz().isAcessivel(id)) {
//                        System.out.println("acessivel");
                        tempQuorum.addToQuorum(t);
                        cont++;
                    }
                    if (cont >= width) {
                        break;
                    }
                }
                while (cont < width) {
                    Random r = new Random();
                    int n = r.nextInt(tree.getFilhos().size());
                    if (!tempQuorum.contem(tree.getFilhos().get(n))) {
                        tempQuorum.addToQuorum(tree.getFilhos().get(n));
                        cont++;
                    }
                }
                for (int i = 0; i < width; i++) {
//                    System.out.println(i + " - recursão " + length + " " + tempQuorum.getCoterie());
                    childQuorum.addToQuorum(getQuorum(tempQuorum.getCoterie().get(i), length, width));
                }
            }
            System.out.println(childQuorum.getCoterie().toString());
            return childQuorum;
        }
    }

    public int heigth(Tree t) { // busca em profundidade
        int cont = 0;
        if (t.getRaiz() != null) {
            cont++;
            if (!t.getFilhos().isEmpty()) {
                int comp = 0;
                for (Tree a : t.getFilhos()) {
                    int aux = heigth(a);
                    if (aux > comp) {
                        comp = aux;
                    }
                }
                cont += comp;
            }
        }
        return cont;
    }

    public boolean isCompleta(Tree t) { // busca em profundidade
        boolean completa = false;
        if (t.getRaiz() != null) {
//            System.out.print(t.getFilhos().size());
            if (t.getFilhos().size() == d) {
//                System.out.print("\n");
                completa = true;
                boolean aux1 = true;
                int h = heigth(t);
                for (Tree a : t.getFilhos()) {
                    boolean aux2 = isCompleta(a);
                    boolean aux3 = (heigth(a) == h - 1);
                    aux1 &= aux2 & aux3;
                }
                completa &= aux1;
            } else if (t.isFolha()) {
//                System.out.print(" folha\n");
                completa = true;
            }
        }
        return completa;
    }

    public ArrayList<Tree> getArvore() {
        return arvore;
    }

    public String getId() {
        return id;
    }
}
