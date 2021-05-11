package property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * Created by Choen-hee Park
 * User : chpark
 * Date : 2021/05/11
 * Time : 10:13 AM
 */

@Validated
@Component
@ConfigurationProperties("author")
public class AuthorProperties {
    @NotEmpty
    private String name;

    @NotBlank
    private String email;

    @Min(value = 0, message = "0세부터 입력 가능합니다.")
    @Max(value = 200, message = "200세까지만 입력 가능합니다")
    private Integer age;

    private boolean isMarried;

    private String job;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public boolean isMarried() {
        return isMarried;
    }

    public void setMarried(boolean married) {
        isMarried = married;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    @Override
    public String toString() {
       StringBuilder sb = new StringBuilder();
       sb.append("name:<").append(name)
               .append(">, email:<").append(email)
               .append(">, age:<").append(age)
               .append(">, isMarried:<").append(isMarried)
               .append(">, job:<").append(job).append(">");
       return sb.toString();
    }
}
