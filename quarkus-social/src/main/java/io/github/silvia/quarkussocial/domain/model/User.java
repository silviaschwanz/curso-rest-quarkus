package io.github.silvia.quarkussocial.domain.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "name")
    private String name;

    @Column(name="age")
    private Integer age;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(age, user.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age);
    }

}
