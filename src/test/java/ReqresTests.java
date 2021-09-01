import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ReqresTests {

    private final String baseUrl = "https://reqres.in/";

    @Test
    public void checkMessageForRequestSingleUserNotFound() {
        makeUsersRequestSpecification()
                .basePath("/api/users/23")
                .get()
                .then()
                .body(equalTo("{}"))
                .assertThat().statusCode(404);
    }

    @Test
    public void createUser() {
        String name = "morpheus";
        String job = "cleaner";

        //отправка запроса
        Response response = given().baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(String.format("{\"name\": \"%s\",\"job\": \"%s\"}", name, job))
                .post("/api/users");

        //проверка ответа
        response.then().statusCode(201);
        response.then().body("name", equalTo(name));
        response.then().body("job", equalTo(job));

        //получение данных из ответа
        String json = response.asString();
        int idUser = JsonPath.from(json).getInt("id");

        //запросить пользователя по id
        Response responseGet = given().baseUri(baseUrl)
                .get("/api/users/{id}", idUser);
        responseGet.then().statusCode(200);
    }

    @Test
    public void createUserLikeAPro() {
        String name = "morpheus";
        String job = "cleaner";

        //отправка запроса
        User userForRequest = new User();
        userForRequest.setJob(job);
        userForRequest.setName(name);
        Response response = makeUsersRequestSpecification()
                .contentType(ContentType.JSON)
                .body(userForRequest)
                .post();

        //проверка ответа
        response.then().statusCode(201);
        User user = response.as(User.class); // маппинг с помощью Gson
        Assertions.assertThat(user.getJob()).isEqualTo(job);
        Assertions.assertThat(user.getName()).isEqualTo(name);
    }

    @Test
    public void requestNotExistingUser() {
        makeUsersRequestSpecification()
                .get("/{id}", 3475)
                .then().statusCode(404);
    }

    private RequestSpecification makeUsersRequestSpecification() {
        return given().baseUri(baseUrl).basePath("/api/uses");
    }

    class User {
        Integer id;
        String name;
        String job;
        String createdAt;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}
