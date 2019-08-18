package sistemadistribuido1.Host;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlo
 */
public class Send extends Thread {

    private ArrayList<HostId> arrayHosts;
    private String valorSaida;
    private String id;
    private Random r;

    public Send(String id, HostId host, String saida) {
        arrayHosts = new ArrayList<>();
        arrayHosts.add(host);
        this.valorSaida = saida;
        this.id = id;
        r = new Random();
    }

    public Send(String id, ArrayList<HostId> arrayHosts, String saida) {
        this.arrayHosts = arrayHosts;
        this.valorSaida = saida;
        this.id = id;
        r = new Random();
    }

    @Override
    public void run() {
        try {
            int cont = 0;
            Thread.sleep(r.nextInt(1000));
            for (HostId h : arrayHosts) {
//                System.out.println(id + " Tentativa "  + h.toString());
                Socket cliente = new Socket(h.getIp(), h.getPorta()); // ip e porta do servidor
                new Thread(id + " - Cliente " + cont) {
                    @Override
                    public void run() {
                        try {
                            PrintStream saida = new PrintStream(cliente.getOutputStream());
                            saida.println(id);
                            saida.println(valorSaida);
//                            System.out.println(id + " Mensagem enviada para " + h);
                            saida.close(); // fecha prinStream
                            cliente.close(); // fecha conexão com cliente
                        } catch (IOException ex) {
                            System.err.println("IOException " + Thread.currentThread().getName());
                        }
                    }
                }.start();
                cont++;
            }
        } catch (IOException ex) {
            System.err.println(id + " Send IOException, Destino está bloqueado temporariamente.");
//            Thread.interrupted();
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            Logger.getLogger(Send.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
