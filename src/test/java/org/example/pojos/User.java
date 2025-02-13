package org.example.pojos;

import lombok.Getter;
import lombok.Setter;

public class User {


    @Getter @Setter
    private String email;
    @Getter @Setter
    private String first_name;
    @Getter @Setter
    private String last_name;

    @Getter @Setter
    private String password;
    @Getter @Setter
    private String avatar;
}
