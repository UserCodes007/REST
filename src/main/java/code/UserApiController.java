package code;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserApiController {
	private UserService service;
	
	protected UserApiController(UserService service) {
		this.service = service;
	}

	//this control adds user to database and returns the added user.
	@PostMapping
	public ResponseEntity<?> add(@RequestBody @Valid User user) {
		User persistedUser = service.add(user);
		
		URI uri = URI.create("/users/" + persistedUser.getId());
		
		return ResponseEntity.created(uri).body(persistedUser);
	}

	//this control method fetches the user using given id
	@GetMapping("/{id}")
	public ResponseEntity<?> get(@PathVariable("id") Long id) {
		try {
			User user = service.get(id);
			return ResponseEntity.ok(user);
			
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.notFound().build();
		}
	}

	//this control method returns all users available as a list
	@GetMapping
	public ResponseEntity<?> list() {
		List<User> listUsers = service.list();
		if (listUsers.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(listUsers);
	}

	//
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody @Valid User user) {
		try {
			user.setId(id);
			User updatedUser = service.update(user);			
			return ResponseEntity.ok(updatedUser);
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.notFound().build();
		}		
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		try {
			service.delete(id);
			return ResponseEntity.noContent().build();
			
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.notFound().build();
		}
	}
}
