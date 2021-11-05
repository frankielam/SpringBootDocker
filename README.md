# SpringBootDocker

SpringBoot 集成 Docker Demo.



### 0. 前置条件

* 安装 docker 客户端（如 mac 下安装 Docker Desktop）
* JDK 1.8 或以上
* Maven 3  或以上
* IDE （Eclipse / SpringToolSuite4 / IntelliJ IDEA），非必须

### 1. 创建 Project
* SpringToolSuite4 下创建 New  一个 Spring  Starter Project（或在 [https://start.spring.io/](https://start.spring.io/) 创建）

```
Name: SpringbootDocker
Teyp: maven
Packaging:  Jar
Java Version: 8
Group: today.lqf
Artifact: springboot-docker
Version: latest
Package: today.lqf.demo
```

* pom.xml 

```
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
```

### 2. 代码
* 创建类 SpringbootDockerApplication.java

```  java 
package today.lqf.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringbootDockerApplication {

    static final String HELLO_WORLD = "Hello frankie.";

    @RequestMapping("/")
    public String home() {
        return HELLO_WORLD;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringbootDockerApplication.class, args);
    }

}
```


* 创建测试类 SpringbootDockerApplicationTests.java

``` java
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
```
IDE 上执行下  Junit Test，确保代码正常运行。

### 3. 加入 Dockerfile Maven Plugin

* pom.xml 增加配置

``` configure
			<properties>
				<dockerfile-maven-version>1.4.13</dockerfile-maven-version>
				<docker.image.prefix>demo</docker.image.prefix>
			</properties>


			<plugin>
				<groupId>com.spotify</groupId>
				<artifactId>dockerfile-maven-plugin</artifactId>
				<version>${dockerfile-maven-version}</version>
				<executions>
					<execution>
						<id>default</id>
						<goals>
							<goal>build</goal>
							<goal>push</goal>
						</goals>
					</execution>
				</executions>
				<configuration>
					<repository>${docker.image.prefix}/${project.artifactId}</repository>
					<tag>${project.version}</tag>
					<buildArgs>
						<JAR_FILE>${project.build.finalName}.jar</JAR_FILE>
					</buildArgs>
				</configuration>
			</plugin>

```

* 项目根目录创建 Dockerfile 文件

``` vim
FROM openjdk
MAINTAINER frankie <frankie.lqf@gmail.com>

ARG JAR_FILE
WORKDIR /app
COPY target/${JAR_FILE} ./app.jar
EXPOSE 8080
CMD ["java", "-jar",  "app.jar"]
```

### 4. 打包 / 启动应用

* 打包

``` bash
mvn package dockerfile:build
# 若需要跳过测试打包则加上参数 -DskipTests=true
mvn -DskipTests=true package dockerfile:build 
```

* 若 build 报找不到 activation 包错误，pom.xml 增加 activation dependency 配置后重新打包

```
		<dependency>
			<groupId>javax.activation</groupId>
			<artifactId>activation</artifactId>
			<version>1.1.1</version>
		</dependency>
```

* 启动应用

``` bash
docker run  -p 8080:8080 demo/springboot-docker
```

* 访问 App

``` bash
curl  http://127.0.0.1:8080/
Hello frankie.
```



##### 参考资料

[https://spring.io/guides/gs/spring-boot-docker/](https://spring.io/guides/gs/spring-boot-docker/)

[https://github.com/spotify/dockerfile-maven/blob/master/README.md](https://github.com/spotify/dockerfile-maven/blob/master/README.md)
