package today.lqf.demo;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringbootDockerApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testHome() {
        ResponseEntity<String> responseObject = this.restTemplate.exchange(
                "http://127.0.0.1:" + port + "/", HttpMethod.GET, null, String.class, String.class);

        Assertions.assertThat(responseObject.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(responseObject.getBody()).isEqualTo(SpringbootDockerApplication.HELLO_WORLD);
    }

}
