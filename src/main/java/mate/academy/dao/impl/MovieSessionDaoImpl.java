package mate.academy.dao.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import mate.academy.dao.MovieSessionDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.lib.Inject;
import mate.academy.model.MovieSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

@Dao
public class MovieSessionDaoImpl implements MovieSessionDao {
    @Inject
    private SessionFactory sessionFactory;

    public MovieSession add(MovieSession movieSession) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.persist(movieSession);
            transaction.commit();
            return movieSession;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can not save movie session", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Optional<MovieSession> get(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(MovieSession.class, id));
        } catch (Exception e) {
            throw new DataProcessingException("Can not get movie session with id: "
                    + id, e);
        }
    }

    public List<MovieSession> findAvailableSessions(Long movieId,
                                                    LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        try (Session session = sessionFactory.openSession()) {
            Query<MovieSession> getAllMovieSession = session.createQuery(
                    "FROM MovieSession ms "
                            + "WHERE ms.movie.id = :movieId "
                            + "AND ms.showTime >= :startOfDay "
                            + "AND ms.showTime < :endOfDay ", MovieSession.class);
            getAllMovieSession .setParameter("movieId", movieId)
                    .setParameter("startOfDay", startOfDay)
                    .setParameter("endOfDay", endOfDay);
            return getAllMovieSession.getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can not find available movie sessions", e);
        }
    }
}
