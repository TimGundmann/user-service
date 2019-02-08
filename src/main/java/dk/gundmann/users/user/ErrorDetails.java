package dk.gundmann.users.user;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorDetails {
	
	  private LocalDateTime timestamp;
	  private String message;
	  private String details;
	  
}
