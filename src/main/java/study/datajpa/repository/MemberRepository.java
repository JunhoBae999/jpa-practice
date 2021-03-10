package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

//요기에 이미 무수한 인터페이스가 있ㄲ음
public interface  MemberRepository extends JpaRepository<Member,Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();

    //    @Query(name = "Member.findByUsername") 도메인.메서드이름으로 네임드 쿼리를 먼저 찾는다.  -> 이후 쿼리 네임드로 생성함
//    사실 실무에서 사용 안함. 하지만 네임드 쿼리가 가지는 가장 큰 장점? 어플리케이션 로딩 시점에 파싱을 하고 오류를 알려줌! 이게 네임드 쿼리의 가장 큰 장점.
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username=:username and m.age=:age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto( m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMembberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);


    /*
    반환 타입을 유연하게 쓰는 것을 지원해준다!
     */
    List<Member> findListByUsername(String username); //컬렉션

    Member findMemberByUsername(String username); //단건

    Optional<Member> findOptionalByUsername(String username); //옵셔널


    @Query(value = "select m from Member m left join m.team t ", countQuery = "select count(m) from Member m")
        //실무에서 중요한 내용. 카운트 쿼리를 분류하가. 토탈 카운트는 조인을 하던 안 하던 상관이 없으니까!
    Page<Member> findByAge(int age, Pageable pageable);
    //pageable은 페이징에 대한 조건. 페이지는 반환타

    //Slice<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true) //얘가 있어야 excuteUpdate가 나감. 변경에 관련해서는 얘가 있어야함.
    @Query("update Member m set m.age = m.age+1 where m.age >=:age")
    int bulkAgePlus(@Param("age") int age);

    //패치 조인을 하면 멤버와 연관된 팀을 한방 쿼리로 다끌고옴.
    @Query("select m from Member m left join fetch m.team")
    //뭐가 문제냐..? spring jpa는 무조건 Jpql을 쓰지 않으니까. 따라서 entitygraph로 패치 조인까지 조져줌.
    List<Member> findMemberFetchJoin();


    @Override
    @EntityGraph(attributePaths = {"team"})
        //패치 조인이라고 보면 됩니다.
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberByEntityGraph();

   // @EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(@Param("username") String username);


}

