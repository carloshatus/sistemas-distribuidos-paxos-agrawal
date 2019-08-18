package sistemadistribuido1.Host;


/**
 *
 * @author carlo
 */
public class Menssagem {
    private HostId origem;
    private String menssagem;
    private int numeroSerie;

    public Menssagem(HostId origem, int numeroSerie) {
        this.origem = origem;
        this.numeroSerie = numeroSerie;
    }

    public Menssagem(HostId origem, String menssagem, int numeroSerie) {
        this.origem = origem;
        this.menssagem = menssagem;
        this.numeroSerie = numeroSerie;
    }

    public Menssagem(HostId origem) {
        this.origem = origem;
        menssagem = "";
    }

    public HostId getOrigem() {
        return origem;
    }

    public String getMenssagem() {
        return menssagem;
    }

    public void setMenssagem(String menssagem) {
        this.menssagem = menssagem;
    }

    public int getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(int numeroSerie) {
        this.numeroSerie = numeroSerie;
    }
    
    @Override
    public String toString() {
        return origem + ": " + menssagem;
    }
    
}
