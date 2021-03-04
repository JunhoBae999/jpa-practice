package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

//요기에 이미 무수한 인터페이스가 있ㄲ음
public interface MemberRepository extends JpaRepository<Member,Long> {

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


    List<Member> findListByUsername(String username); //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); //옵셔

}
