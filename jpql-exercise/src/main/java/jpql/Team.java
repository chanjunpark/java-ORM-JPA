package jpql;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@IdClass(TeamPK.class)
public class Team {

    @Id
    @GeneratedValue
    private Long id;
    
    @Id
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();
    
    public List<Member> getMembers() {
        return members;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
