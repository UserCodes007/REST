package code.Controller;

import java.net.URI;
import java.util.List;

import code.Service.UserService;
import code.Model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="Users API")
public class UserApiController {
	private UserService service;
	
	protected UserApiController(UserService service) {
		this.service = service;
	}

	//this control adds user to database and returns the added user.
	@Operation(summary = "Add User Details", description = "Adds the User to repo and returns the added User")
	@ApiResponses(value = {
			@ApiResponse(content = {@Content(
					mediaType = "application/json",
					schema = @Schema(implementation = User.class))
			},responseCode = "200", description = "Successfully Added"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")
	})
	@PostMapping
	public ResponseEntity<?> add(@RequestBody @Valid @Parameter(name = "User", description = "User Details"  ) User user) {
		User persistedUser = service.add(user);

		URI uri = URI.create("/users/" + persistedUser.getId());
		
		return ResponseEntity.created(uri).body(persistedUser);
	}

	//this control method fetches the user using given id
	@Operation(summary = "Get user details by id", description = "Returns User as per the id")
	@ApiResponses(value = {
			@ApiResponse(content = {@Content(
					mediaType = "application/json",
					schema = @Schema(implementation = User.class))
			},responseCode = "200", description = "Successfully retrieved"),
			@ApiResponse(content = {@Content(
					mediaType = "text/plain",
					schema = @Schema(implementation = String.class),
					examples = @ExampleObject(value = "No User found in db with the given id to perform the operation"))
			},responseCode = "404", description = "Not found - User not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")
	})
	@GetMapping("/{id}")
	public ResponseEntity<?> get(@PathVariable("id") @Parameter(name = "id", description = "User Details", example = "99" ) Long id) {
			User user = service.get(id);
			return ResponseEntity.ok(user);
	}

	//this control method returns all users available as a list
	@Operation(summary = "Get All User Details", description = "Returns a List of Users")
	@ApiResponses(value = {
			@ApiResponse(content = {@Content(
					mediaType = "application/json",
					array = @ArraySchema(schema = @Schema(implementation = User.class)))
			},responseCode = "200", description = "Successfully retrieved"),
			@ApiResponse(responseCode = "204", description = "No User Content found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")
	})
	@GetMapping
	public ResponseEntity<?> list() {
		List<User> listUsers = service.list();
		if (listUsers.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(listUsers);
	}

	@Operation(summary = "Update User By Id", description = "Updates and returns Updated User Details")
	@ApiResponses(value = {
			@ApiResponse(content = {@Content(
					mediaType = "application/json",
					schema = @Schema(implementation = User.class))
			},responseCode = "200", description = "Successfully Updated"),
			@ApiResponse(content = {@Content(
					mediaType = "text/plain",
					schema = @Schema(implementation = String.class),
					examples = @ExampleObject(value = "No User found in db with the given id to perform the operation"))
			},responseCode = "404", description = "Not found - User not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable("id") @Parameter(name = "id", description = "User id", example = "99" ) Long id, @RequestBody @Valid @Parameter(name = "User", description = "User Details") User user) {
			user.setId(id);
			User updatedUser = service.update(user);			
			return ResponseEntity.ok(updatedUser);
	}

	@Operation(summary = "Delete User By Id", description = "Deletes and returns status about User deletion")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "No Content about the Specified User"),
			@ApiResponse(content = {@Content(
					mediaType = "text/plain",
					schema = @Schema(implementation = String.class),
					examples = @ExampleObject(value = "No User found in db with the given id to perform the operation"))
			},responseCode = "404", description = "Not found - User not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error")
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") @Parameter(name = "id", description = "User id", example = "99" )  Long id) {
			service.delete(id);
			return ResponseEntity.noContent().build();
	}
}
