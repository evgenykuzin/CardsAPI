package ru.sberbank.kuzin19190813.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "TOKEN")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Token {
    @Id
    String token;
}
