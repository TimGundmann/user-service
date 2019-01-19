package dk.gundmann.users.securty;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import dk.gundmann.users.user.Service;
import dk.gundmann.users.user.User;

@org.springframework.stereotype.Service
public class UserDetailSerivceImpl implements UserDetailsService {

	private Service service;

	public UserDetailSerivceImpl(Service service) {
		this.service = service;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return service.findByEmail(email)
				.map(convert())
				.orElseThrow(() -> new UsernameNotFoundException("error"));
	}

	private Function<? super User, ? extends UserDetails> convert() {
		return user -> {
			final String password = user.getPassword();
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
					return password;
				}

				@Override
				public Collection<? extends GrantedAuthority> getAuthorities() {
					return List.of();
				}
			};
		};
	}

}
