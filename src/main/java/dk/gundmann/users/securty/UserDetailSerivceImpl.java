package dk.gundmann.users.securty;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;

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
		User user = service.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("error");
		}
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
				return email;
			}
			
			@Override
			public String getPassword() {
				return password;
			}
			
			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return newArrayList();
			}
		};
	}

}
