package net.filippov.newsportal.service;

import net.filippov.newsportal.domain.User;

public interface UserService {

    User get(Long id);

    User getByLogin(String login);

    void add(User user);

    void update(User user);
}
