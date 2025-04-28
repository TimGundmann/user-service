package dk.gundmann.users.user;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorDetails {
	
	  private LocalDateTime timestamp;
	  private String message;
	  private String details;
	  
}
