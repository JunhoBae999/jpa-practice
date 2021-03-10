package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;


    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select t from Member t",Member.class).getResultList();
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class,id);
        return Optional.ofNullable(member);
        //java8의 기본 기능. 옵셔널 공부하기
    }

    public long count() {
        return em.createQuery("select count(t) from Member t",long.class).getSingleResult();

    }


    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age=:age order by m.username desc",Member.class)
                .setParameter("age",age)
                .setFirstResult(offset) //몇번째부터?
                .setMaxResults(limit) //몇명까지?
                .getResultList();
    }

    //카운트를 가져오는데 소팅을 할 핋요가?
    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age= :age",Long.class)
                .setParameter("age",age)
                .getSingleResult();
    }

    public int bulkAgePlus(int age) {
        //응답값의 갯수가 나옴,
        int resultCount =  em.createQuery("update Member m set m.age = m.age+1 where m.age>=:age")
                .setParameter("age",age)
                .executeUpdate();

        return resultCount;

    }





}