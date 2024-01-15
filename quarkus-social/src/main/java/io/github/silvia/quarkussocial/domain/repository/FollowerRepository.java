package io.github.silvia.quarkussocial.domain.repository;

import io.github.silvia.quarkussocial.domain.model.Follower;
import io.github.silvia.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    //Método para verificar se um usário já é seguidor de outro
    public boolean follows(User follower, User user){
        //Utilizando JEE
        /*Map<String, Object> params = new HashMap<>();
        params.put("follower", follower);
        params.put("user", user);*/

        // Utilizando Panache
        var params = Parameters.with("follower", follower).and("user", user).map();

        PanacheQuery<Follower> query = find("follower =:follower and user =:user", params);
        Optional<Follower> result = query.firstResultOptional();

        return result.isPresent();
    }

    public List<Follower> findByUser(Long userId){
        PanacheQuery<Follower> query = find("user.id", userId);
        return query.list();
    }

}
