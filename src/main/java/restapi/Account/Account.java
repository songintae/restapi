package restapi.Account;


import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(of = "id")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@Entity
public class Account {


    @Id @GeneratedValue
    private Long id;

    private String email;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles = new HashSet<>();

    public enum  UserRole {

        USER("USER"), ADMIN("ADMIN");

        private String code;

        UserRole(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
