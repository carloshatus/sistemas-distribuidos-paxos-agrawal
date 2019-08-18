package sistemadistribuido1;

import sistemadistribuido1.Host.HostId;
import sistemadistribuido1.Host.Host;
import java.util.ArrayList;

/**
 *
 * @author carlo
 */
public class SistemaDistribuido1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArrayList<HostId> hostsDestino = new ArrayList<>(); // conjunto de hosts para enviar
        ArrayList<Host> hosts = new ArrayList<>(); // conjunto de hosts inicializados
        int num = 21; // total de hosts
        for (int i = 1; i <= num; i++) { // inicializando
            HostId id = new HostId("localhost", 1999 + i, i); // cada um com uma porta diferente
            hostsDestino.add(id);
            hosts.add(new Host(id));
        }
        for (Host h : hosts) { // todos os hosts tem a mesma lista de endereços
            h.setArrayHosts(hostsDestino);
        }
        for (Host h : hosts) { // inicia as Treads
            new Thread(h).start();
        }
    }

}
