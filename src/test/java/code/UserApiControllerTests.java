package code;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

// Spring Boot instantiates only the web layer rather than the whole context.
// In an application with multiple controllers, you can even ask for only one to be instantiated by using,
// for example, @WebMvcTest(HomeController.class).
@WebMvcTest(UserApiController.class)
public class UserApiControllerTests {
	private static final String END_POINT_PATH = "/users";
	
	@Autowired private MockMvc mockMvc;
	@Autowired private ObjectMapper objectMapper;
	@MockBean private UserService service;
	
	@Test
	public void testAddShouldReturn400BadRequest() throws Exception {
		User newUser = new User().email("").firstName("kranthi").lastName("madhavan").password("We@#");
		//empty string is not a valid email expecting bad request
		String requestBody = objectMapper.writeValueAsString(newUser);
		
		mockMvc.perform(post(END_POINT_PATH).contentType("application/json")
				.content(requestBody))
				.andExpect(status().isBadRequest())
				.andDo(print())
		;
	}
	
	@Test
	public void testAddShouldReturn201Created() throws Exception {
		//verifying that the new is created and saved in required location and same object is returned.
		User newUser = new User().id(11L).email("david.parker@gmail.com")
								 .firstName("David").lastName("Parker")
								 .password("avid808");
		
		Mockito.when(service.add(newUser)).thenReturn(newUser);
		
		String requestBody = objectMapper.writeValueAsString(newUser);
		
		mockMvc.perform(post(END_POINT_PATH).contentType("application/json")
				.content(requestBody))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location",is("/users/11")))
				.andExpect(jsonPath("id",is(11)))
				.andDo(print())
		;

	}	
	
	@Test
	public void testGetShouldReturn404NotFound() throws Exception {
		//getting a non existing user
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;
		
		Mockito.when(service.get(userId)).thenThrow(UserNotFoundException.class);
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
	@Test
	public void testGetShouldReturn200OK() throws Exception {
		//tying to get existing user
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;
		String email = "david.parker@gmail.com";
		
		User user = new User().email(email)
				 .firstName("David").lastName("Parker")
				 .password("avid808")
				 .id(userId);
		
		Mockito.when(service.get(userId)).thenReturn(user);
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("id", is(123)))
			.andDo(print());
	}
	
	@Test
	public void testListShouldReturn204NoContent() throws Exception {
		Mockito.when(service.list()).thenReturn(new ArrayList<>());
		//trying to access empty database
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isNoContent())
			.andDo(print());		
	}
	
	@Test
	public void testListShouldReturn200OK() throws Exception {
		//retrieving all users from db
		User user1 = new User().email("david.parker@gmail.com")
				 .firstName("David").lastName("Parker")
				 .password("avid808")
				 .id(1L);
		
		User user2 = new User().email("john.doe@gmail.com")
				 .firstName("John").lastName("Doe")
				 .password("johnoho2")
				 .id(2L);
		
		List<User> listUser = List.of(user1, user2);
		
		Mockito.when(service.list()).thenReturn(listUser);
		
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("[0].id", is(1)))
			.andExpect(jsonPath("[1].id", is(2)))
			.andDo(print());		
	}	
	
	@Test
	public void testDeleteShouldReturn404NotFound() throws Exception {
		//deleting non existing user
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;
		
		Mockito.doThrow(UserNotFoundException.class).when(service).delete(userId);;
		
		mockMvc.perform(delete(requestURI))
			.andExpect(status().isNotFound())
			.andDo(print());		
	}
	
	@Test
	public void testDeleteShouldReturn200OK() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;
		
		Mockito.doNothing().when(service).delete(userId);;
		
		mockMvc.perform(delete(requestURI))
			.andExpect(status().isNoContent())
			.andDo(print());		
	}
	
	@Test
	public void testUpdateShouldReturn404NotFound() throws Exception {
		//trying to update non existing data
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;
		
		User user = new User().email("david.parker@gmail.com")
							 .firstName("David").lastName("Parker")
							 .password("avid808")
							 .id(userId);
		
		Mockito.when(service.update(user)).thenThrow(UserNotFoundException.class);
		
		String requestBody = objectMapper.writeValueAsString(user);
		
		mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
			.andExpect(status().isNotFound())
			.andDo(print());		
	}
	
	@Test
	public void testUpdateShouldReturn400BadRequest() throws Exception {
		//updating using invalid email format
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;
		
		User user = new User().email("david.parker")
							 .firstName("David").lastName("Parker")
							 .password("avid808")
							 .id(userId);
		
		String requestBody = objectMapper.writeValueAsString(user);
		
		mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
			.andExpect(status().isBadRequest())
			.andDo(print());		
	}
	
	@Test
	public void testUpdateShouldReturn200OK() throws Exception {

		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;
		
		String email = "david.parker@gmail.com";		
		User user = new User().email(email)
							 .firstName("David").lastName("Parker")
							 .password("avid808")
							 .id(userId);
		
		Mockito.when(service.update(user)).thenReturn(user);
		
		String requestBody = objectMapper.writeValueAsString(user);
		
		mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("id", is(123)))
			.andDo(print());		
	}
}
