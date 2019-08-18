package sistemadistribuido1.Host;

/**
 *
 * @author carlo_si1d5ve
 */
public class Valor {
    private int idade;
    private int valor;

    public Valor(String texto) {
        texto = texto.replace(" ", "");
        String aux = texto.replace("valor:", "");
        aux = aux.substring(0, aux.indexOf(";"));
        valor = Integer.valueOf(aux);
        aux = texto.replace("valor:"+aux+";idade:", "");
        idade = Integer.valueOf(aux);
    }
    public Valor(int valor, int idade) {
        this.idade = idade;
        this.valor = valor;
    }

    public int getIdade() {
        return idade;
    }

    public int getValor() {
        return valor;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "valor: " + valor +"; idade: " + idade;
    }
}
