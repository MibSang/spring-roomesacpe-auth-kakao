package nextstep.theme;

import io.restassured.RestAssured;
import nextstep.framework.exception.AuthErrorCode;
import nextstep.publics.login.TokenRequest;
import nextstep.publics.theme.ThemeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ThemeE2ETest {

    private String token;

    @BeforeEach
    void setUp() {
        TokenRequest tokenRequest = new TokenRequest("admin", "pass");
        var tokenResponse = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tokenRequest)
                .when().post("/login/token")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
        token = tokenResponse.jsonPath().getString("accessToken");
    }

    @DisplayName("테마를 생성한다")
    @Test
    public void create() {
        ThemeRequest body = new ThemeRequest("테마이름", "테마설명", 22000);
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(body)
                .when().post("/themes")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("테마 목록을 조회한다")
    @Test
    public void showThemes() {
        createTheme();

        var response = RestAssured
                .given().log().all()
                .param("date", "2022-08-11")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when().get("/themes")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
        assertThat(response.jsonPath().getList(".").size()).isGreaterThan(0);
    }

    @DisplayName("권한 없이 테마 목록을 조회하면 예외가 발생한다.")
    @Test
    public void noAuthGetThemes() {
        createTheme();

        var response = RestAssured
                .given().log().all()
                .param("date", "2022-08-11")
                .when().get("/themes")
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract();

        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo(AuthErrorCode.UNAUTHORIZED.getMessage());
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("테마를 삭제한다")
    @Test
    void delete() {
        Long id = createTheme();

        var response = RestAssured
                .given().log().all()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when().delete("/themes/" + id)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("토큰 없이 테마를 삭제하면 예외가 발생한다.")
    @Test
    void deleteNoToken() {
        Long location = createTheme();

        var response = RestAssured
                .given().log().all()
                .when().delete("/themes/" + location)
                .then().log().all()
                .extract();

        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo(AuthErrorCode.UNAUTHORIZED.getMessage());
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("유저 권한으로 테마를 삭제하면 예외가 발생한다.")
    @Test
    void deleteNoAdmin() {
        TokenRequest tokenRequest = new TokenRequest("test", "pass");
        var tokenResponse = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tokenRequest)
                .when().post("/login/token")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
        String userToken = tokenResponse.jsonPath().getString("accessToken");

        Long location = createTheme();

        var response = RestAssured
                .given().log().all()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .when().delete("/themes/" + location)
                .then().log().all()
                .extract();

        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo(AuthErrorCode.UNAUTHORIZED.getMessage());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    public Long createTheme() {
        ThemeRequest body = new ThemeRequest("테마이름", "테마설명", 22000);
        String location = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(body)
                .when().post("/themes")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().header("Location");
        return Long.parseLong(location.split("/")[2]);
    }
}
