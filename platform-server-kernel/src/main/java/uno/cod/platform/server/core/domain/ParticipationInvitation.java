package uno.cod.platform.server.core.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "participation_invitation")
@AssociationOverrides({
        @AssociationOverride(name = "key.user", joinColumns = {@JoinColumn(name = "user_id")}),
        @AssociationOverride(name = "key.challenge", joinColumns = {@JoinColumn(name = "challenge_id")})
})
public class ParticipationInvitation {
    @EmbeddedId
    private ParticipationKey key;

    @ElementCollection
    @Column(name = "email")
    @CollectionTable(name = "participation_invitation_email",
            joinColumns = {@JoinColumn(name = "participation_invitation_challenge_id", referencedColumnName = "challenge_id"),
                    @JoinColumn(name = "participation_invitation_user_id", referencedColumnName = "user_id")})
    private Set<String> emails;

    public ParticipationKey getKey() {
        return key;
    }

    public void setKey(ParticipationKey key) {
        this.key = key;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }

    public void addEmails(Set<String> emails) {
        if (this.emails == null) {
            this.emails = new HashSet<>();
        }
        this.emails.addAll(emails);
    }
}
