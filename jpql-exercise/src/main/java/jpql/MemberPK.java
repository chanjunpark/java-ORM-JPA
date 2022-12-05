package jpql;

import java.io.Serializable;

public class MemberPK implements Serializable {

    public static final long serialVersionUID = 1L;
    
    private Long id;
    private Team team;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        MemberPK memberPK = (MemberPK) o;
        
        if (id != null ? !id.equals(memberPK.id) : memberPK.id != null) return false;
        return team != null ? team.equals(memberPK.team) : memberPK.team == null;
    }
    
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (team != null ? team.hashCode() : 0);
        return result;
    }
}
