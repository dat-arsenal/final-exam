package com.vti.service;

import com.vti.entity.Account;
import com.vti.form.AccountCreateForm;
import com.vti.form.AccountFilterForm;
import com.vti.form.AccountUpdateForm;
import com.vti.form.AuthUpdateForm;
import com.vti.repository.IAccountRepository;
import com.vti.specification.AccountSpecification;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Getter
@Setter
public class AccountService implements IAccountService {
    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private IAccountRepository repository;

    @Override
    public Page<Account> findAll(Pageable pageable, AccountFilterForm form) {
        Specification spec = AccountSpecification.builSpec(form);
        return repository.findAll(spec, pageable);
    }

    @Override
    public Account findById(int id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Account findByUsername(String username) {

        return repository.findByUsername(username);
    }

    @Override
    @Transactional
    public void create(AccountCreateForm form) {
        Account account = mapper.map(form, Account.class);
        String encodedPassword = encoder.encode(form.getPassword());
        account.setPassword(encodedPassword);
        repository.save(account);
    }

    @Override
    public void update(AccountUpdateForm form) {

        Account account = mapper.map(form, Account.class);
        String encodedPassword = encoder.encode(form.getPassword());
        account.setPassword(encodedPassword);

        repository.save(account);
        // dùng để refresh authentication "làm mới xác thức người dùng"
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                auth.getPrincipal(),
                auth.getCredentials(),
                AuthorityUtils.createAuthorityList(account.getRole().toString())
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    @Override
    public void update(AuthUpdateForm form) {
        Account account = repository.findByUsername(form.getUsername());
        String encodedPassword = encoder.encode(form.getPassword());
        account.setPassword(encodedPassword);
        repository.save(account);
    }


    public void deleteAllById(List<Integer> ids) {
        repository.deleteAllById(ids);
    }


    @Override
    // lấy ra thông tin user để kểm tra xác thực người dùng
//    được gọi khi người dùng tiến hành đăng nhập
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = repository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("Can NOT found account has username = " + username);
        }
        return new User(

                account.getUsername(),
                account.getPassword(),
                AuthorityUtils.createAuthorityList(account.getRole().toString())
        );
    }
}
