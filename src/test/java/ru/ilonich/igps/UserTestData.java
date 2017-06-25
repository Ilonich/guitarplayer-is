package ru.ilonich.igps;

import ru.ilonich.igps.matcher.ModelMatcher;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.model.enumerations.Authority;
import ru.ilonich.igps.model.enumerations.Location;
import ru.ilonich.igps.model.enumerations.Role;

import java.util.Objects;

public class UserTestData {

    public static final ModelMatcher<User> USER_MODEL_MATCHER = ModelMatcher.of(User.class, ((expected, actual) -> expected == actual ||
            (Objects.equals(expected.getId(), actual.getId()) &&
            Objects.equals(expected.getEmail(), actual.getEmail()) &&
            Objects.equals(expected.getUsername(), actual.getUsername()) &&
            Objects.equals(expected.getPassword(), actual.getPassword()) &&
            Objects.equals(expected.isDecent(), actual.isDecent()) &&
            Objects.equals(expected.isEnabled(), actual.isEnabled()) &&
            Objects.equals(expected.getUserBio(), actual.getUserBio()) &&
            Objects.equals(expected.getRating(), actual.getRating()) &&
            Objects.equals(expected.getRegistered(), actual.getRegistered()) &&
            Objects.equals(expected.getAuthority(), actual.getAuthority()) &&
            Objects.equals(expected.getLocation(), actual.getLocation()) &&
            Objects.equals(expected.getRoles(), actual.getRoles()))));

    public static final User moderator = new User(100001, "mod@igps.ru", "$2a$10$QSbul4JCPb/pSCrZ8E7dQuZIWWMHs4WFYHW9kSAt9UUUcuca7gt7m", true, true, "Модератор", "Я хороший модератор, у меня есть кофе", 0, 1495011510000L, Authority.MEMBER, Location.UNKNOWN, Role.MODERATOR, Role.USER); //banme
    public static final User admin = new User(100000, "admin@igps.ru", "$2a$10$Zj7Perk/znZzHmk8.07ByuJOMIGpt/2K7fjZ52HbX0BCOgDJ7ixtC", true, true, "Админ", "Я хороший админ, у меня есть кот", -2, 1494840045000L, Authority.MEMBER, Location.UNKNOWN, Role.ADMIN, Role.CONFIDANT, Role.MODERATOR, Role.USER); //codeme
    public static final User typicalUser = new User(100003, "voter@yandex.ru", "$2a$10$GE5HKnDO8gyjfAez8LaJ0eOHntisGgBnUHFFr10tAtI86Cyz0F1HS", true, true, "Пользователь", "Я хороший знакомый админа, у меня есть сани", 1, 1495205506000L, Authority.REGULAR, Location.UNKNOWN, Role.USER); //likeme
    public static final User confidant = new User(100003, "friend@igps.ru", "$2a$10$D1cWOPMmYN4Qf67pY/LM7elrwEG.3am1SFP2zjlvx7Lk88egoUYBK", true, true, "Представитель", "Я хороший парень, у меня есть ружье", 0, 1495116562000L, Authority.MEMBER, Location.UNKNOWN, Role.CONFIDANT, Role.USER); //hugme
}
