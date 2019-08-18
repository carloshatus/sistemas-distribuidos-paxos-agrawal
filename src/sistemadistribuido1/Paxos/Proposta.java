package sistemadistribuido1.Paxos;

import sistemadistribuido1.Host.HostId;

/**
 *
 * @author carlo
 */
public class Proposta {

    private HostId origem;
    private int numero;
    private int valor;

    public Proposta(String texto) {
        texto = texto.replace(" ", "");
        texto = texto.replace("numero:", "");
        String aux = texto.substring(0, texto.indexOf(";"));
        numero = Integer.valueOf(aux);
        texto = texto.replace(aux + ";valor:", "");
        aux = texto.substring(0, texto.indexOf(";"));
        valor = Integer.valueOf(aux);
        texto = texto.replace(aux + ";", "");
        if (texto.startsWith("origem:")) {
            texto = texto.replace("origem:", "");
            String id = texto.substring(0, texto.indexOf(":"));
            texto = texto.replace(id + ":", "");
            String ip = texto.substring(0, texto.indexOf("/"));
            texto = texto.replace(ip + "/", "");
            int porta = Integer.valueOf(texto);
            origem = new HostId(ip, porta, id);
        } else {
            origem = null;
        }
    }

    public Proposta(HostId origem, String texto) {
        this.origem = origem;
        texto = texto.replace(" ", "");
        texto = texto.replace("numero:", "");
        String aux = texto.substring(0, texto.indexOf(";"));
        numero = Integer.valueOf(aux);
        texto = texto.replace(aux + ";valor:", "");
        aux = texto.substring(0, texto.indexOf(";"));
        valor = Integer.valueOf(aux);
    }

    public Proposta(HostId origem, int numero, int valor) {
        this.origem = origem;
        this.numero = numero;
        this.valor = valor;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public HostId getOrigem() {
        return origem;
    }

    @Override
    public String toString() {
        return "numero: " + numero + "; valor: " + valor + "; origem: " + origem;
    }

}
