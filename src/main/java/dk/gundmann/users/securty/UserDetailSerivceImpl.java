package dk.gundmann.users.securty;

import dk.gundmann.users.user.User;
import dk.gundmann.users.user.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserDetailSerivceImpl implements UserDetailsService {

	private UserService service;

	public UserDetailSerivceImpl(UserService service) {
		this.service = service;
	}

	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		return service.findActiveUser(id)
				.map(convert())
				.orElseThrow(() -> new UsernameNotFoundException("error"));
	}

	private Function<? super User, ? extends UserDetails> convert() {
		return user -> {
			return new UserDetails() {

				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					return true;
				}

				@Override
				public boolean isCredentialsNonExpired() {
					return true;
				}

				@Override
				public boolean isAccountNonLocked() {
					return true;
				}

				@Override
				public boolean isAccountNonExpired() {
					return true;
				}

				@Override
				public String getUsername() {
					return user.getEmail();
				}

				@Override
				public String getPassword() {
					return user.getPassword();
				}

				@Override
				public Collection<? extends GrantedAuthority> getAuthorities() {
					return user.getRoles().stream()
							.map(role -> new SimpleGrantedAuthority(role))
							.collect(Collectors.toList());
				}
			};
		};
	}

}
