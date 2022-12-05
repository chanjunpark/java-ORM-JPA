package jpql;

import java.io.Serializable;

public class TeamPK implements Serializable {
    
    public static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        TeamPK teamPK = (TeamPK) o;
        
        if (id != null ? !id.equals(teamPK.id) : teamPK.id != null) return false;
        return name != null ? name.equals(teamPK.name) : teamPK.name == null;
    }
    
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
