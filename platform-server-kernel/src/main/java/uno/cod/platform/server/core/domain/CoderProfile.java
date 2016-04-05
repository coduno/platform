package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "coder_profile")
public class CoderProfile {

    @Id
    @Column(name = "user_id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "coderProfile")
    private User user;

    @ElementCollection
    @MapKeyColumn(name = "skill_map_KEY")
    @Column(name = "skill_map")
    @CollectionTable(name = "coder_profile_skill_map",
        joinColumns = {@JoinColumn(name = "coder_profile_user_id")})
    private Map<CodingSkill, Double> skillMap;

    @Column(name = "last_updated")
    private Date lastUpdated;
}
