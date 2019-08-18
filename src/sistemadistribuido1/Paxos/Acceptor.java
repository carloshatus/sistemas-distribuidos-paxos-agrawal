package sistemadistribuido1.Paxos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import sistemadistribuido1.Host.Host;
import sistemadistribuido1.Host.HostId;
import sistemadistribuido1.Host.Send;

/**
 *
 * @author carlo
 */
public class Acceptor {

    private Host host;
    private Proposta proposta;
    private String nome;
    private File file;
    private FileWriter arqW;
    private FileReader arqR;
    private PrintWriter gravarArq;
    private BufferedReader lerArq;
    private HostId prometiA;

    public Acceptor(Host host) {
        try {
            prometiA = null;
            this.host = host;
            nome = "Acceptor" + host.getId() + ".txt";
            file = new File(nome);
//            arqW = new FileWriter(file);
//            gravarArq = new PrintWriter(arqW);
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
                proposta = new Proposta(host.getHostid(), 0, 0);
                armazena(proposta.toString()); // salva proposta
            }
        } catch (IOException ex) {
            Logger.getLogger(Proposer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean fase1(Proposta p) {
        if (p.getNumero() > proposta.getNumero()) {
            armazena(p.toString()); // salva proposta
            System.out.println(host.getId() + " fase1: " + p);
            prometiA = p.getOrigem();
            Send send = new Send(host.getId(), prometiA, "promessa:" + proposta.toString()); // envia a maior proposta recebida
            new Thread(send).start();
//            System.out.println(host.getId() + " Respondeu a " + p.getOrigem());
            proposta = p;
        }
        return true;
    }

    public boolean fase2(Proposta p) {
        System.out.println(host.getId() + " fase2: " + p);
        if (prometiA != null) {
//            System.out.println("Prometi a " + prometiA);
            if (prometiA.getId().equals(p.getOrigem().getId())) {
//                System.out.println("Ok");
                proposta = p;
                armazena(proposta.toString()); // salva proposta
                System.out.println(host.getId() + " aceitou a proposta " + p);
                Send send = host.getSend();
                send = new Send(host.getId(), prometiA, "aceita"); // aceita a proposta recebida
                new Thread(send).start();
                return true;
            }
        }
        return false;
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

}
