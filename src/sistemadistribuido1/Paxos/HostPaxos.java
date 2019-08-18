package sistemadistribuido1.Paxos;

import sistemadistribuido1.Host.Host;
import sistemadistribuido1.Host.HostId;

/**
 *
 * @author carlo
 */
public class HostPaxos extends Host{
    
    private Proposta proposta;
    
    public HostPaxos(HostId hostid) {
        super(hostid);
    }

    public Proposta getProposta() {
        return proposta;
    }
    
}
