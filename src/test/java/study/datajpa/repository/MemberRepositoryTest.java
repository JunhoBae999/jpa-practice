package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.awt.print.Pageable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

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

    @Test
    public void findByMembers() {

        Member member1 = new Member("aaa");
        Member member2 = new Member("bbb");

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> byNames = memberRepository.findByNames(Arrays.asList("aaa", "bbb"));
        System.out.println("=======");
        for (Member byName : byNames) {
            System.out.println(byName);
        }


    }


    @Test
    public void returnType() {
        Member m1 = new Member("AAA");
        Member m2 = new Member("BBB");


        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa= memberRepository.findListByUsername("AAA");
        System.out.println("list member : " + aaa.get(0));
        //리스트로 하면 무조건 널이 아니게 나옴. 빈 리스트로 나옴. 무조건!!!

        Member finduser = memberRepository.findMemberByUsername("asdf");
        System.out.println("findmember="+finduser);
        //단건 조회일 때가 문제가 됨. 없으면 널이 나옴.

        //아 몰라. 데이터 있을 지 없을 지 모르면 그냥 옵셔널로 해. 옵셔널? -> 없을 수도 있다! 클라이언트가 알아서 처리해!
        //디비에서 조회하는데 업승ㄹ 수도 있고 있을 수도 있어? 그럼 옵셔널로 써.
        Optional<Member> findMember = memberRepository.findOptionalByUsername("asdasda");
        System.out.println("find optional member = " + findMember);

        /*
        옵셔널을 하던, 단건조회를 하던간에 만약 중복 데이터가 있으면 익셉션이 터짐.
        NonuniqueException -> IncorrectResultSizeDataAccessException(스프링 예외) 로 변경되어 나옴. 스프링이 추상화한 예외 사용함.
         */
    }


    @Test
    public void paging() {

        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));

        int age=10;

        //PageRequest는 Pagable의 구현체.
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //total count 쿼리 까지 같이 나감. 대박적. 심지어 소팅도 안해서 성능 이점도 챙김. 딱 생각한 대로의 쿼리가 나감.
        //이거는 이제 넘기면 절대 안됩니다... 엔티티 넘기면 안대요. dto로 변환해서 넘겨야 합니다.
        Page<Member> byPage = memberRepository.findByAge(age, pageRequest);

        //내부를 dto로 바꿈. 이거는 이제 api로 넘겨도 댐.
        //페이지는 dto로 넘겨도 괜찮음!
        Page<MemberDto> toMap = byPage.map(member -> new MemberDto(member.getId(),member.getUsername(),null));


        //Slice<Member> byPage = memberRepository.findByAge(age, pageRequest);
        //Slice에서는 살짝 한개 더 가져옴! 쿼리에서 limit에서 1개 살짝 주면 됨. 그리고 토탈 카운트가 안날라감.

        //then
        List<Member> content = byPage.getContent();
        //long totalElements = byPage.getTotalElements();

        //페이징 관련 각종
        assertThat(content.size()).isEqualTo(3);
        //assertThat(byPage.getTotalElements()).isEqualTo(5);
        assertThat(byPage.getNumber()).isEqualTo(0);
        //assertThat(byPage.getTotalPages()).isEqualTo(2);


    }


    @Test
    public void bulkUpdate() {
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",19));
        memberRepository.save(new Member("member3",20));
        memberRepository.save(new Member("member4",21));
        memberRepository.save(new Member("member5",40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);

        //벌크 연산 하고 나서는 다 날려버려야 함!

        /*
        jpa에서 벌크 연산을 할 때는 주의해야함.
        벌크 연산은 영속성 컨텍스트를 무시하고 디비에 빵 때리는거기 때문에, 위의 멤버 객체들은 지금 반영이 안된 상태!
         */

        //then
        assertThat(resultCount).isEqualTo(3);

        List<Member> member5 = memberRepository.findByUsername("member5");
        Member member = member5.get(0);

        System.out.println("member5 = "+member5); // 영속성 컨텍스트의 1차캐시에 그래로 있는 40이 나온다. 벌크연산은 디비에다 때리는 거가 때문에! 따라서 플러시 하고 날려버린다음에 다시 읽어와야함.

        /*
        따라서, 벌크 연산 이후에는 영속성 컨텍스트를 다 날려버려야 함!
        그냥 벌크연산만 하고 트랜잭션이 끝나버리면 상관 없을 수도 있지만, 만약 바로 다른 로직이 나온다면 주의해야함. 플러시를 하고 해야함.
         */



    }

    @Test
    public void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1",10,teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        System.out.println("====");

        //N+1 문제. (member들이 가지고 있는 team을 호출하는 쿼리 -  n개의 쿼리) + (select member - 1개의 쿼리)
        //jpa에서는 fetch join으로 해결! 조인도 하고 select 절에 다 넣어줌.
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member = " + member.getTeam().getClass()); //패치조인을 하니까 진짜 팀이 나옴.
            System.out.println("member = " + member.getTeam().getName());
        }

    }






}