package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void getGreaterthenTest() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member1");

        member1.setAge(10);
        member2.setAge(20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("member1",15);

        for (Member member : members) {
            System.out.println("member: " + member.getUsername());

        }

        assertThat(members.get(0).getUsername()).isEqualTo("member1");
        assertThat(members.get(0).getAge()).isEqualTo(20);

    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA");
        Member m2 = new Member("BBB");

        m1.setAge(10);
        m2.setAge(20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA",10);

        assertThat(result.get(0)).isEqualTo(m1);

    }

    @Test
    public void findUsernameList() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        member1.setAge(10);
        member2.setAge(20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernameList = memberRepository.findUsernameList();

        System.out.println("=============");

        for (String s : usernameList) {
            System.out.println(s);
        }

    }

    @Test
    public void findUserDto() {

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        Team team1 = new Team("team1");
        Team team2 = new Team("team2");

        teamRepository.save(team1);
        teamRepository.save(team2);

        member1.setAge(10);
        member2.setAge(20);

        member1.changeTeam(team1);
        member2.changeTeam(team2);

        memberRepository.save(member1);
        memberRepository.save(member2);
        
        List<MemberDto> memberDtos = memberRepository.findMembberDto();

        System.out.println("======");
        for (MemberDto memberDto : memberDtos) {
            System.out.println(memberDtos);
        }

    }





}