package com.bookstore;

import com.bookstore.entity.BookReview;
import static com.bookstore.entity.ReviewStatus.CHECK;
import com.bookstore.service.BookstoreService;
import java.util.logging.Logger;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MainApplication {

    private static final Logger logger = Logger.getLogger(MainApplication.class.getName());   
    
    private final BookstoreService bookstoreService;

    public MainApplication(BookstoreService bookstoreService) {
        this.bookstoreService = bookstoreService;
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    public ApplicationRunner init() {
        return args -> {
            BookReview bookReview = new BookReview();            
            bookReview.setContent("Very good book!");
            bookReview.setEmail("marinv@gmail.com");
            bookReview.setStatus(CHECK);

            String response = bookstoreService.postReview(bookReview);
            logger.info(() -> "Response: " + response);
        };
    }
}

/*
 * How To Publish Domain Events From Aggregate Root

Note: Domain events should be used with extra-caution! The best practices for using them are revealed in my book, Spring Boot Persistence Best Practices.

Description: Starting with Spring Data Ingalls release publishing domain events by aggregate roots becomes easier. Entities managed by repositories are aggregate roots. In a Domain-Driven Design application, these aggregate roots usually publish domain events. Spring Data provides an annotation @DomainEvents you can use on a method of your aggregate root to make that publication as easy as possible. A method annotated with @DomainEvents is automatically invoked by Spring Data whenever an entity is saved using the right repository. Moreover, Spring Data provides the @AfterDomainEventsPublication annotation to indicate the method that should be automatically called for clearing events after publication. Spring Data Commons comes with a convenient template base class (AbstractAggregateRoot) to help to register domain events and is using the publication mechanism implied by @DomainEvents and @AfterDomainEventsPublication. The events are registered by calling the AbstractAggregateRoot.registerEvent() method. The registered domain events are published if we call one of the save methods (e.g., save()) of the Spring Data repository and cleared after publication.

This is a sample application that relies on AbstractAggregateRoot and its registerEvent() method. We have two entities, Book and BookReview involved in a lazy-bidirectional @OneToMany association. A new book review is saved in CHECK status and a CheckReviewEvent is published. This event handler is responsible to check the review grammar, content, etc and switch the review status from CHECK to ACCEPT or REJECT and propagate the new status to the database. So, this event is registered before saving the book review in CHECK status and is published automatically after we call the BookReviewRepository.save() method. After publication, the event is cleared.

Key points:

the entity (aggregate root) that publish events should extend AbstractAggregateRoot and provide a method for registering events
here, we register a single event (CheckReviewEvent), but more can be registered
event handling take place is CheckReviewEventHandler in an asynchronous manner via @Async
 */
