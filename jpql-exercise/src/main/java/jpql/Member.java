package jpql;

import javax.persistence.*;

@Entity
@IdClass(MemberPK.class)
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String username;
    private int age;
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(value = {
            @JoinColumn(name = "TEAM_ID"),
            @JoinColumn(name = "TEAM_NAME")
    })
    private Team team;
    
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
    
    public void setTeam(Team team) {
        this.team = team;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", age=" + age +
                '}';
    }
}
