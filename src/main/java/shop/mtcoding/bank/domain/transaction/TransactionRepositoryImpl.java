package shop.mtcoding.bank.domain.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

interface Dao {
    List<Transaction> findTransactionList(@Param("accountId") Long accountId, @Param("gubun") String gubun, @Param("page") Integer page);
}

@RequiredArgsConstructor
public class TransactionRepositoryImpl implements Dao {

    private final EntityManager em;

    @Override
    public List<Transaction> findTransactionList(final Long accountId, final String gubun, final Integer page) {
        // 동적 쿼리(gubun 값을 가지고 동적 쿼리 = DEPOSIT, WITHDRAW, ALL)
        // JQPL
        String sql = "";
        sql += "select t from Transaction t ";

        if (gubun.equals(TransactionEnum.WITHDRAW.name())) {
            sql += "join fetch t.withdrawAccount wa ";
            sql += "where t.withdrawAccount.id = :withdrawAccountId ";
        } else if (gubun.equals(TransactionEnum.DEPOSIT.name())) {
            sql += "join fetch t.depositAccount da ";
            sql += "where t.depositAccount.id = :depositAccountId ";
        } else {
            sql += "left join fetch t.withdrawAccount wa ";
            sql += "left join fetch t.depositAccount da ";
            sql += "where t.withdrawAccount.id = :withdrawAccountId ";
            sql += "or ";
            sql += "t.depositAccount.id = :depositAccountId";
        }

        TypedQuery<Transaction> query = em.createQuery(sql, Transaction.class);

        if (gubun.equals(TransactionEnum.WITHDRAW.name())) {
            query = query.setParameter("withdrawAccountId", accountId);
        } else if (gubun.equals(TransactionEnum.DEPOSIT.name())) {
            query = query.setParameter("depositAccountId", accountId);
        } else {
            query = query.setParameter("withdrawAccountId", accountId);
            query = query.setParameter("depositAccountId", accountId);
        }

        query.setFirstResult(page * 5); // 5, 10, 15
        query.setMaxResults(5);

        return query.getResultList();
    }
}
