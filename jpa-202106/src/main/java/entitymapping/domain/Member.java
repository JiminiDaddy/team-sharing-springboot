package entitymapping.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Access(AccessType.FIELD)
@Table(name = "Members", uniqueConstraints = {
    @UniqueConstraint(
        name = "NAME_AGE_UNIQUE",
        columnNames = {"name", "age"}
    )
})
@Entity
/* SEQUENCE 전략 사용시 필요함
@SequenceGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        sequenceName = "MEMBER_SEQ",
        initialValue = 1,       // 초기값
        allocationSize = 1      // 증가량
)
*/
/* TABLE 전략 사용시 필요함
@TableGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        table = "MEMBERS_SEQUENCE",
        pkColumnValue = "MEMBER_SEQ", allocationSize = 1
)
*/
public class Member {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
    //@GeneratedValue(strategy = GenerationType.TABLE, generator = "MEMBER_SEQ_GENERATOR")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false, length = 8)
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Lob
    private String description;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Transient
    private LocalDateTime loginAt;

    protected Member() {}

    public Member(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getLoginAt() {
        return loginAt;
    }

    public void login() {
        this.loginAt = LocalDateTime.now();
    }
}
