package sistemadistribuido1.Host;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import sistemadistribuido1.Paxos.Acceptor;
import sistemadistribuido1.Paxos.Leaner;
import sistemadistribuido1.Paxos.Proposer;
import sistemadistribuido1.Paxos.Proposta;
import sistemadistribuido1.TreeQuorum.Tree;
import sistemadistribuido1.TreeQuorum.TreeQuorum;

/**
 *
 * @author carlo
 */
public class Host implements Runnable {

    private ArrayList<HostId> arrayHosts;
    private String arraySaida;
    private ArrayList<Menssagem> bufferEntrada;
    private Send send;
    private Receive receive;
    private String id;
    private Random r;
    private TreeQuorum tree; // arvore Agrawal
    private Semaphore sBufferE;
    private Valor valor;
    private int numAux;
    private Proposer proposer;
    private Acceptor acceptor;
    private Leaner leaner;
    private HostId lider;
    private HostId hostid;

    public Host(HostId hostid) {
        this.hostid = hostid;
        sBufferE = new Semaphore(1);
        id = this.hostid.getId();
        arrayHosts = new ArrayList<>();
        arraySaida = "";
        bufferEntrada = new ArrayList<>();
        r = new Random();
        valor = new Valor(0, 0);
        tree = new TreeQuorum(this.id);
        receive = new Receive(this);
        leaner = new Leaner(this);
        proposer = new Proposer(this);
        acceptor = new Acceptor(this);
        lider = null;
    }

    @Override
    public void run() {
        new Thread(receive).start(); // inicia o receptor do host
        while (true) {
            consenso();
        }
    }

    public void setArrayHosts(ArrayList<HostId> arrayHosts) {
        for (HostId h : arrayHosts) {
            this.arrayHosts.add(h);
        }
        tree.gerarArvore(arrayHosts);
//        setLider(tree.getArvore().get(1).getRaiz().getNo());
    }

    public ArrayList<Menssagem> getArrayEntrada() {
        return bufferEntrada;
    }

    public ArrayList<HostId> getArrayHosts() {
        return arrayHosts;
    }

    public TreeQuorum getTree() {
        return tree;
    }

    public Semaphore getSBufferE() {
        return sBufferE;
    }

    public HostId getHostid() {
        return hostid;
    }

    public String getArraySaida() {
        return arraySaida;
    }

    public ArrayList<Menssagem> getBufferEntrada() {
        return bufferEntrada;
    }

    public Send getSend() {
        return send;
    }

    public Receive getReceive() {
        return receive;
    }

    public String getId() {
        return id;
    }

    public Valor getValor() {
        return valor;
    }

    public Random getR() {
        return r;
    }

    public int getNumAux() {
        return numAux;
    }

    public Proposer getProposer() {
        return proposer;
    }

    public Acceptor getAcceptor() {
        return acceptor;
    }

    public Leaner getLeaner() {
        return leaner;
    }

    public HostId getLider() {
        return lider;
    }

    public void setValor(Valor valor) {
        this.valor = valor;
    }

    public void setLider(HostId lider) {
//        System.out.println(arrayHosts);
//        try {
//            semaforo.acquire();
        arrayHosts.remove(lider);
        arrayHosts.add(0, lider);
        tree.gerarArvore(arrayHosts);
//            System.out.println(arrayHosts);
//            semaforo.release();
        this.lider = lider;
        System.out.println(id + " aceitou novo lider: " + lider.getId());
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    @Override
    public String toString() {
        String saida = id + " " + arraySaida + "\n";
        return saida;
    }

    public Valor read() { // ler Agrawal
        try {
            ArrayList<HostId> destino = new ArrayList<>();
            acessar();
            for (Tree a : tree.getQuorum(1, 3).getCoterie()) {
                destino.add(a.getRaiz().getNo());
            }
            send = new Send(id, destino, "getValor");
            new Thread(send).start();
            Thread.sleep(2000);
            Valor v = valor;
            sBufferE.acquire();
            for (int i = 0; i < bufferEntrada.size(); i++) {
                Menssagem m = bufferEntrada.get(i);
                if (m.getMenssagem().startsWith("valor:")) {
                    if (v == null) {
                        v = new Valor(m.getMenssagem());
                        System.out.println("valor " + v);
                    } else {
                        Valor x = new Valor(m.getMenssagem());
                        if (x.getIdade() > v.getIdade()) {
                            v = x;
                        }
                    }
                    bufferEntrada.remove(m);
                    i--;
                }
            }
            if (valor.getIdade() < v.getIdade()) {
                valor = v;
            }
//            System.out.println("Caixa de entrada " + bufferEntrada);
            sBufferE.release();
        } catch (InterruptedException ex) {
            Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valor;
    }

    public boolean write(Valor valor) { // escrever Agrawal
        try {
            ArrayList<HostId> destino = new ArrayList<>();
            acessar();
            for (Tree a : tree.getQuorum(3, 3).getCoterie()) {
//            for (Tree a : tree.getQuorum(2, 3).getCoterie()) {
                destino.add(a.getRaiz().getNo());
            }
            send = new Send(id, destino, "bloqueado setValor: " + valor.toString());
            new Thread(send).start();
            Thread.sleep(2000);
            sBufferE.acquire();
            for (int i = 0; i < bufferEntrada.size(); i++) {
                Menssagem m = bufferEntrada.get(i);
                if (m.getMenssagem().startsWith("concluido")) {
                    if (this.valor != valor) {
                        this.valor = valor;
                    }
                    bufferEntrada.remove(m);
                    i--;
                }
            }
//            System.out.println("Caixa de entrada " + bufferEntrada);
            sBufferE.release();
        } catch (InterruptedException ex) {
            Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
        }
//        return valor;
        return true;
    }

    public void consenso() {
        if (lider != null && lider.getTreeNode().isAcessivel(id)) { // se o lider existe e é acessível
//            System.out.println(id + " " + (lider == hostid));
            //acessar();
            if (lider == hostid) { // se o host atual é o lider
                try {
                    Thread.sleep(10000);
                    // propor do lider
                    proposer.propor(); // gera proposta
                    Thread.sleep(5000); // espera a resposta
                    sBufferE.acquire();
                    int val = 0;
                    int cont = 0;
//                    System.out.println(bufferEntrada);
                    for (int i = 0; i < bufferEntrada.size(); i++) {
                        Menssagem m = bufferEntrada.get(i);
                        if (m.getMenssagem().startsWith("promessa:")) {
                            System.out.println("Promessa de " + m.getOrigem().getId());
                            cont++;
                            String texto = m.getMenssagem();
                            texto = texto.replace(" ", "");
                            texto = texto.replace("promessa:", "");
                            Proposta p = new Proposta(m.getOrigem(), texto);
                            if (val < p.getValor()) {
                                val = p.getValor();
                            }
                            bufferEntrada.remove(m);
                            i--;
                        }
                    }
//                    System.out.println(cont);
                    sBufferE.release();
                    if (cont >= Math.round(proposer.getNumAceitadores() / 2.0f)) { // Maioria dos aceitadores aceitou
                        System.out.println("A maioria dos Aceptors prometeu aceitar.");
                        if (val == 0 || leaner.getProposta().getNumero() < proposer.getProposta().getNumero()) {
                            proposer.propor(r.nextInt(100) + 1); // não propoe 0
                        } else {
                            proposer.propor(val);
                        }
                    }
                    Thread.sleep(5000);
                    sBufferE.acquire();
                    cont = 0;
//                    System.out.println(bufferEntrada);
                    for (int i = 0; i < bufferEntrada.size(); i++) {
                        Menssagem m = bufferEntrada.get(i);
                        if (m.getMenssagem().equals("aceita")) {
                            System.out.println("Proposta aceita por " + m.getOrigem().getId());
                            cont++;
                            bufferEntrada.remove(m);
                            i--;
                        }
                    }
                    sBufferE.release();
                    if (cont >= Math.round(proposer.getNumAceitadores() / 2.0f)) { // Maioria dos aceitadores
                        System.out.println("A Maioria dos Aceptors aceitou.");
                        for (Tree t : tree.getQuorum(3, 3).getCoterie()) { // calcula ávore e envia
//                        for (Tree t : tree.getQuorum(2, 3).getCoterie()) {
                            send = new Send(id, t.getRaiz().getNo(), "aceita:" + proposer.getProposta().toString()); // comunica a proposta recebida a maioria
                            new Thread(send).start();
                        }
                        valor = new Valor(proposer.getProposta().getValor(), valor.getIdade() + 1);
                        write(valor); // escreve o valor Agrawal
                        System.out.println("\n" + getId() + " escreveu o valor: " + valor + "\n");
                        leaner.salvar(proposer.getProposta());
                    }
                    Thread.sleep(5000);
                    sBufferE.acquire();
                    bufferEntrada.clear();
                    sBufferE.release();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else { // host atual não é o lider, é Leaner
                try {
                    Thread.sleep(27000); // espera
                    valor = read();
                    System.out.println("\n" + getId() + " leu o valor: " + valor);
                    sBufferE.acquire();
                    for (Menssagem m : bufferEntrada) {
                        if (m.getMenssagem().startsWith("aceita:")) {
                            String texto = m.getMenssagem();
                            texto = texto.replace(" ", "");
                            texto = texto.replace("aceita:", "");
                            Proposta p = new Proposta(m.getOrigem(), texto);
//                            System.out.println("Leaner " + proposta.getNumero() + " " + p.getNumero());
                            if (leaner.getProposta().getNumero() < p.getNumero()) {
                                leaner.salvar(p);
                                send = new Send(id, arrayHosts, "aceita:" + p.toString()); // comunica a proposta recebida a maioria
                                new Thread(send).start();
                            }
                        }
                    }
                    bufferEntrada.clear();
                    sBufferE.release();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else { // não tem líder
            try {
                int v = r.nextInt(10000);
                Thread.sleep(v); // espera um tempo aleatório, redes :D
                proposer.proporLider(r.nextInt(100) + 1); // se não conhece o lider propoe para ser
//                v = r.nextInt(arrayHosts.size());
//                if (lider == null) {
//                    write(new Valor(v, valor.getIdade() + 1));
//                }
                Thread.sleep(2000);
//                Valor a = read();
//                lider = arrayHosts.get(a.getValor());
                sBufferE.acquire();
                HostId novoLider = null;
                for (int i = 0; i < bufferEntrada.size(); i++) {
                    Menssagem m = bufferEntrada.get(i);
                    if (m.getMenssagem().startsWith("lider:")) {
                        String texto = m.getMenssagem();
                        texto = texto.replace(" ", "");
                        texto = texto.replace("lider:", "");
                        Proposta p = new Proposta(m.getOrigem(), texto);
                        if (p.getValor() % 9 == 0) { // se o valor for multiplo de 9 é o lider
                            novoLider = p.getOrigem();
                            bufferEntrada.clear();
                            break;
                        }
                        bufferEntrada.remove(m);
                        i--;
                    }
                }
                sBufferE.release();
                if (lider == null || !lider.getTreeNode().isAcessivel(id)) { // se nehum lider foi aceito nesse tempo
                    if (novoLider != null) { // aceita o novo lider
                        setLider(novoLider);
                        send = new Send(id, arrayHosts, "novoLider: " + lider.getId());
                        new Thread(send).start();
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void acessar() { // saber se está vivo, manda um oi
        try {
            send = new Send(id, arrayHosts, "oi");
            new Thread(send).start();
            Thread.sleep(2000);
            for (Tree a : tree.getArvore()) {
                a.getRaiz().setAcessivel(false);
            }
            sBufferE.acquire();
//            for (Menssagem m : bufferEntrada)
            for (int i = 0; i < bufferEntrada.size(); i++) {
                Menssagem m = bufferEntrada.get(i);
                if (m.getMenssagem().equals("ola")) {
                    for (Tree a : tree.getArvore()) {
                        if (a.getRaiz().getNo().equals(m.getOrigem())) {
                            a.getRaiz().setAcessivel(true);
                        }
                    }
                    bufferEntrada.remove(m);
                    i--;
                }
            }
//            System.out.println("Caixa de entrada " + bufferEntrada);
            sBufferE.release();
        } catch (InterruptedException ex) {
            Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
