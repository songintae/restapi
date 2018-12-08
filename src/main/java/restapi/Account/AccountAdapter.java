package restapi.Account;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountAdapter extends User {
    private Account account;

    public AccountAdapter(Account account) {
        super(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
        this.account = account;

    }

    public Account getAccount() {
        return this.account;
    }

    private static List<GrantedAuthority> authorities(Set<Account.UserRole> roleSet) {
        return roleSet.stream().map(userRole -> new SimpleGrantedAuthority(userRole.getCode())).collect(Collectors.toList());
    }
}
