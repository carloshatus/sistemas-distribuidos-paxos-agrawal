package sistemadistribuido1.Host;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import sistemadistribuido1.Paxos.Proposta;

/**
 *
 * @author carlo
 */
public class Receive implements Runnable {

    private int porta;
    private ArrayList<Menssagem> buffer;
    private String id;
    private Semaphore semaforo;
//    private Semaphore semaforo1;
    private ArrayList<HostId> arrayHosts;
    private Host host;
    private boolean bloqueado;

//    public Receive(String id, int porta, ArrayList<Menssagem> buffer, ArrayList<HostId> arrayHosts) {
//        try {
//            semaforo = new Semaphore(1);
//            this.arrayHosts = arrayHosts;
//            this.porta = porta;
//            semaforo.acquire();
//            this.buffer = buffer;
//            semaforo.release();
//            this.id = id;
//            bloqueado = false;
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    Receive(Host host) {
//        try {
        this.host = host;
        semaforo = host.getSBufferE();
//            semaforo1 = new Semaphore(1);
        this.arrayHosts = host.getArrayHosts();
        this.porta = host.getHostid().getPorta();
//            semaforo.acquire();
        this.buffer = host.getBufferEntrada();
////            semaforo.release();
        this.id = host.getId();
        bloqueado = false;
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    @Override
    public void run() {
        try {
            ServerSocket servidor = new ServerSocket(porta, 10);
//            System.out.printf(id + " Porta %d aberta!\n", porta);
            int cont = 0;
            while (true) {
//                System.out.println(id + " Receive aguardando nova conexão:");

                Socket cliente = servidor.accept();
                if (!bloqueado) {
//                System.out.println(id + " Receive se conectou com: " + cliente.toString());
                    new Thread(id + " - Cliente " + cont) {
                        @Override
                        public void run() {
                            try {
                                Scanner s = new Scanner(cliente.getInputStream()); // lê dados do cliente
                                String a = s.nextLine();
                                Menssagem menssagem = null;
                                for (HostId h : arrayHosts) {
                                    if (h.getId().equals(a)) {
                                        menssagem = new Menssagem(h);
                                        break;
                                    }
                                }
                                a = "";
                                while (s.hasNextLine()) {
                                    a += s.nextLine();
                                }
                                menssagem.setMenssagem(a);
//                                System.out.println(menssagem);
                                s.close(); // fecha scanner
                                cliente.close(); // fecha conexão do cliente
                                if (!respostaAutomática(menssagem)) {
                                    semaforo.acquire();
                                    buffer.add(menssagem);
                                    semaforo.release();
                                }
//                                System.out.println(id + " - Nova menssagem armazenada!\n\tRecebida de " + menssagem);
                            } catch (IOException ex) {
                                System.err.println("IOException " + Thread.currentThread().getName());
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        private boolean respostaAutomática(Menssagem menssagem) throws InterruptedException {
                            if (menssagem.getMenssagem().startsWith("bloqueado")) {
//                                semaforo1.acquire();
                                bloqueado = true;
//                                semaforo1.release();
                                String texto = menssagem.getMenssagem();
                                texto = texto.replace(" ", "");
                                texto = texto.replace("bloqueado", "");
                                if (texto.startsWith("setValor:")) {
                                    texto = texto.replace("setValor:", "");
                                    Valor v = new Valor(texto);
                                    if (host.getValor().getIdade() < v.getIdade()) {
                                        host.setValor(v);
                                    }
                                }
                                new Thread(new Send(id, menssagem.getOrigem(), "concluido")).start();
//                                semaforo1.acquire();
                                bloqueado = false;
//                                semaforo1.release();
                                return true;
                            }
                            if (menssagem.getMenssagem().startsWith("proposta:")) {
//                                System.out.println(menssagem);
                                String texto = menssagem.getMenssagem();
                                texto = texto.replace(" ", "");
                                texto = texto.replace("proposta:", "");
                                Proposta p = new Proposta(menssagem.getOrigem(), texto);
//                                System.out.println(host.getAcceptor().getProposta().getNumero() + " " + p.getNumero());
                                if (host.getAcceptor().getProposta().getNumero() <= p.getNumero()) {
                                    System.out.println(id + " Recebeu uma nova Proposta: " + menssagem);
                                    if (p.getValor() == 0) {
//                                        semaforo.acquire();
                                        host.getAcceptor().fase1(p);
//                                        semaforo.release();
                                    } else {
//                                        semaforo.acquire();
                                        host.getAcceptor().fase2(p);
//                                        semaforo.release();
                                    }
                                }
                                return true;
                            }
                            if (menssagem.getMenssagem().startsWith("novoLider:")) {
                                String texto = menssagem.getMenssagem();
                                texto = texto.replace(" ", "");
                                texto = texto.replace("novoLider:", "");
                                // procurar host recebido na lista para ser o lider
                                for (HostId h : arrayHosts) {
                                    if (h.getId().equals(texto)) {
//                                        semaforo.acquire();
                                        host.setLider(h);
//                                        semaforo.release();
                                    }
                                }
                                return true;
                            }
                            switch (menssagem.getMenssagem()) {
                                case "oi":
                                    new Thread(new Send(id, menssagem.getOrigem(), "ola")).start();
                                    return true;
                                case "getValor":
                                    new Thread(new Send(id, menssagem.getOrigem(), host.getValor().toString())).start();
                                    return true;
//                                case "aceita":
//                                    return true;
                                default:
                                    return false;
                            }
                        }
                    }.start();
                    cont++;
                }
            }
        } catch (IOException ex) {
            System.err.println(id + " Receive IOException");
            ex.printStackTrace();
        }
    }
}
