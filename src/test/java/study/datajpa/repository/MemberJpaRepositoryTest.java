package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

//@RunWith 없어도 됨
@SpringBootTest
@Transactional
@Rollback(false) //스프링 테스트에서의 틍랜잭셔널은 끝나고나면 다 롤백해버리고 플러시도 안해서 디비에도 안들어감 . false로 해야. 실제에서는 롤백이 맞음
class MemberJpaRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member save = memberRepository.save(member);

        Member member1 = memberRepository.getOne(member.getId());

        assertThat(member1.getId()).isEqualTo(member.getId());
        assertThat(member1.getUsername()).isEqualTo(member.getUsername());

        assertThat(member1).isEqualTo(member);
        //같은 트랜잭션 안에서는 영속성 컨텍스트의 동일성을 보장하기때문에. 트루로 나옴. 같은 인스턴스인게 보장이 됨.
        //트랜잭션이 다르면 당연히 다른 객체가 조회됨. 1차캐시에 넣어놓는거 말하는 듯.
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long dcount = memberRepository.count();
        assertThat(dcount).isEqualTo(0);

        //업데이트는 알아서 됨.
    }

    @Test
    public void getGreaterthenTest() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        member1.setAge(10);
        member2.setAge(20);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
    }

    @Test
    public void paging() {

        memberJpaRepository.save(new Member("member1",10));
        memberJpaRepository.save(new Member("member2",10));
        memberJpaRepository.save(new Member("member3",10));
        memberJpaRepository.save(new Member("member4",10));
        memberJpaRepository.save(new Member("member5",10));

        int age =10;
        int offset = 1;
        int limit = 3;

        //when
        List<Member> byPage = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        //페이지 계산 공식 적용
        //total page = totalCount/size....
        //마지막 페이지..
        //최초 페이지....

        //then
        assertThat(byPage.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);

    }

    @Test
    public void bulkUpdate() {
        memberJpaRepository.save(new Member("member1",10));
        memberJpaRepository.save(new Member("member2",19));
        memberJpaRepository.save(new Member("member3",20));
        memberJpaRepository.save(new Member("member4",21));
        memberJpaRepository.save(new Member("member5",40));

        //when
        int resultCount = memberJpaRepository.bulkAgePlus(20);

        //then
        assertThat(resultCount).isEqualTo(3);

    }




}