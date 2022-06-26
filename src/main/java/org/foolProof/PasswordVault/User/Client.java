package org.foolProof.PasswordVault.User;

import jakarta.persistence.*;

@Entity(name="Client")
@Table(name = "client")
public class Client {
    @Id
    @SequenceGenerator(
            name = "client_sequence",
            sequenceName = "client_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "client_sequence"
    )

    @Column(name = "id")
    private Long id;
    @Column(name ="email")
    private String email;
    @Column(name ="file_name")
    private String fileName;
    @Column(name ="password")
    private String password;

    public Client(String email, String fileName, String password) {
        this.email = email;
        this.fileName = fileName;
        this.password = password;
    }

    public Client() {

    }

    public String getPassword() {
        return password;
    }

    public String getFileName() {
        return fileName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", email='" + email + '\''+
                ", fileName='" + fileName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
