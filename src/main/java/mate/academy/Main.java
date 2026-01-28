package mate.academy;

import mate.academy.lib.Injector;
import mate.academy.model.CinemaHall;
import mate.academy.model.Movie;
import mate.academy.model.MovieSession;
import mate.academy.service.CinemaHallService;
import mate.academy.service.MovieService;
import mate.academy.service.MovieSessionService;
import mate.academy.service.impl.MovieServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        Injector injector = Injector.getInstance("mate.academy");

        MovieService movieService =
                (MovieService) injector.getInstance(MovieService.class);

        CinemaHallService cinemaHallService =
                (CinemaHallService) injector.getInstance(CinemaHallService.class);

        MovieSessionService movieSessionService =
                (MovieSessionService) injector.getInstance(MovieSessionService.class);

        Movie movie = new Movie();
        movie.setTitle("Stranger Things");
        movie.setDescription("Must watch");

        movie = movieService.add(movie);

        CinemaHall hall = new CinemaHall();
        hall.setCapacity(120);
        hall.setDescription("Big hall");

        hall = cinemaHallService.add(hall);

        MovieSession session1 = new MovieSession();
        session1.setMovie(movie);
        session1.setCinemaHall(hall);
        session1.setShowTime(LocalDateTime.now());

        movieSessionService.add(session1);

        MovieSession session2 = new MovieSession();
        session2.setMovie(movie);
        session2.setCinemaHall(hall);
        session2.setShowTime(LocalDateTime.now());

        movieSessionService.add(session2);

        System.out.println("All halls:");
        cinemaHallService.getAll().forEach(System.out::println);

        System.out.println("All movies:");
        movieService.getAll().forEach(System.out::println);

        System.out.println("Sessions today:");
        movieSessionService
                .findAvailableSessions(movie.getId(), LocalDate.now())
                .forEach(System.out::println);
    }
}