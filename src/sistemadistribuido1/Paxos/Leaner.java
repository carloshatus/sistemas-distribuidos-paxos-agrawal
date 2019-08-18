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

/**
 *
 * @author carlo
 */
public class Leaner {

    private Host host;
    private Proposta proposta;
    private String nome;
    private File file;
    private FileWriter arqW;
    private FileReader arqR;
    private PrintWriter gravarArq;
    private BufferedReader lerArq;

    public Leaner(Host host) {
        try {
            this.host = host;
            nome = "Leaner" + host.getId() + ".txt";
            file = new File(nome);
            if (file.exists()) {
                arqR = new FileReader(file);
                lerArq = new BufferedReader(arqR);
                String linha = lerArq.readLine();
                String ultimaLinha = null;
                while (linha != null) {
                    ultimaLinha = linha;
                    linha = lerArq.readLine();
                }
                if (ultimaLinha != null) {
                    proposta = new Proposta(ultimaLinha);
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

    public void salvar(Proposta proposta) {
        this.proposta = proposta;
        System.out.println(host.getId() + " Aprendeu a proposta: " + proposta);
        armazena(proposta.toString()); // salva proposta
    }

    public Proposta getProposta() {
        return proposta;
    }

    private void armazena(String s) {
//        try {
//            arqW = new FileWriter(file, true);
//            gravarArq = new PrintWriter(arqW);
//            gravarArq.println(s); // salva proposta
//            arqW.close();
//        } catch (IOException ex) {
//            Logger.getLogger(Acceptor.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
