package sistemadistribuido1.Paxos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import sistemadistribuido1.Host.Host;
import sistemadistribuido1.Host.HostId;
import sistemadistribuido1.Host.Send;
import sistemadistribuido1.TreeQuorum.Tree;

/**
 *
 * @author carlo
 */
public class Proposer {

    private Host host;
    private Proposta proposta;
    private String nome;
    private File file;
    private FileWriter arqW;
    private FileReader arqR;
    private PrintWriter gravarArq;
    private BufferedReader lerArq;
    private int numAceitadores;
    private ArrayList<HostId> destino;

    public Proposer(Host host) {
        numAceitadores = 0;
        destino = new ArrayList<>();
        try {
            this.host = host;
            nome = "Proposer" + host.getId() + ".txt";
            file = new File(nome);
            if (file.exists()) {
                arqR = new FileReader(file);
                lerArq = new BufferedReader(arqR);
                String linha = lerArq.readLine();
                if (linha != null) {
                    proposta = new Proposta(linha);
                } else {
                    proposta = new Proposta(this.host.getHostid(), 0, 0);
                    armazena(proposta.toString()); // salva proposta
                }
                arqR.close();
            } else {
                proposta = new Proposta(this.host.getHostid(), 0, 0);
                armazena(proposta.toString()); // salva proposta
            }
            Proposta p = host.getLeaner().getProposta();
            if (p.getNumero() > proposta.getNumero()) {
                proposta = p;
            }
        } catch (IOException ex) {
            Logger.getLogger(Proposer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean desativa() {
        // desativa propor
        return true;
    }

    public void proporLider(int num) {
//        try {
        // ganha quem propor o maior numero aceito pelo quorum
        System.out.println(host.getId() + " Propos valor para lider, " + num);
        Proposta p = new Proposta(host.getHostid(), 0, num);
        Send send = host.getSend();
//            host.getSemaforo().acquire();
        send = new Send(host.getId(), host.getArrayHosts(), "lider:" + p.toString());
        new Thread(send).start();
//            host.getSemaforo().release();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Proposer.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public boolean propor(int numero, int valor) {
        proposta = new Proposta(host.getHostid(), numero, valor); // cria proposta
        armazena(proposta.toString()); // salva proposta
        Send send = host.getSend();
        if (valor == 0) {
            destino.clear();
            for (Tree a : host.getTree().getQuorum(3, 3).getCoterie()) {
//            for (Tree a : host.getTree().getQuorum(2, 3).getCoterie()) {
                destino.add(a.getRaiz().getNo());
            }
            numAceitadores = destino.size();
        }
//        System.out.println(destino);
        //destino.remove(host.getLider());
        send = new Send(host.getId(), destino, "proposta:" + proposta.toString()); // envia proposta para a maioria
        new Thread(send).start();
        return true;
    }

    public boolean propor() {
        System.out.println("\nLider " + host.getLider().getId() + " tenta propor com numero " + (proposta.getNumero() + 1));
        return propor(proposta.getNumero() + 1, 0); // cria proposta de valor zero
    }

    public boolean propor(int valor) {
        System.out.println("Lider " + host.getLider().getId() + " propoe o valor " + valor);
        return propor(proposta.getNumero(), valor); // cria proposta
    }

    private void armazena(String s) {
//        try {
//            arqW = new FileWriter(file);
//            gravarArq = new PrintWriter(arqW);
//            gravarArq.println(s); // salva proposta
//            arqW.close();
//        } catch (IOException ex) {
//            Logger.getLogger(Acceptor.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public Proposta getProposta() {
        return proposta;
    }

    public int getNumAceitadores() {
        return numAceitadores;
    }

    public void setNumero(int num) {
        proposta.setNumero(num);
    }
}
