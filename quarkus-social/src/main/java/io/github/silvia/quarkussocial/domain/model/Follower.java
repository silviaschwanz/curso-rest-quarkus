package io.github.silvia.quarkussocial.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "followers")
@Getter
@Setter
public class Follower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name= "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower;

}
