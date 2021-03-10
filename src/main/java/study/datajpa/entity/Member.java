package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter // 실무에서는 setter는 지양하고 비즈니스 로직적인걸로 설정하세요
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of={"id","username","age","team"}) //연관관계 필드는 투스트링 하지 말 것.
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username =:username"
)
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;

    private  int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="team_id")
    private Team team;

    //jpa에서 엔티티는 기본적으로 protected까지 열어놓는 기본 생성자가 있어야함
    // 프록시를 쓸 때 구현체들이 객체를 강제로 만들 때 프라이빗으로 막아놓으면 다 막히기 때문.

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age=age;
        if(team != null){
            changeTeam(team);
        }
    }

    public void changeTeam(Team team) {
        this.team = team;
        //반대쪽의 멤버도 바꿔줘야함.
        team.getMembers().add(this);
    }


}
