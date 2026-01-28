package mate.academy.dao.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import mate.academy.exception.DataProcessingException;
import mate.academy.model.MovieSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class MovieSessionDaoImpl {
    SessionFactory sessionFactory;

    public MovieSessionDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public MovieSession add(MovieSession movieSession){
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
            throw new DataProcessingException("Can not save movieSession", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Optional<MovieSession> get(Long id){
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(MovieSession.class, id));
        } catch (Exception e) {
            throw new DataProcessingException("Can not get cinema hall with id: "
                    + id, e);
        }
    }

    public List<MovieSession> findAvailableSessions(Long movieId, LocalDate date){
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        try (Session session = sessionFactory.openSession()) {
            Query<MovieSession> getAllMovieSession = session.createQuery(
                    "from MovieSession "
                            + "join left fetch movie on movie.id = :movieId "
                            + "join left fetch cinema_hall on cinema_hall.id = :cinemaHall_id"
                            + "where id = :movieId and ms.showTime >= :startOfDay "
                            + "and ms.showTime < :endOfDay", MovieSession.class);
            return getAllMovieSession.getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can not find available movie sessions", e);
        }
    }
}
