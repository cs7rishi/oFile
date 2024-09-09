package com.cs7rishi.oFile.config;

import com.cs7rishi.oFile.entity.Customer;
import com.cs7rishi.oFile.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OFileAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        String email = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials().toString();

        List<Customer> customerList = customerRepository.findByEmail(email);
        if (!customerList.isEmpty()) {
            if (passwordEncoder.matches(password, customerList.get(0).getPwd())) {
                return new UsernamePasswordAuthenticationToken(email,password,
                        getGrantedAuthorities(customerList.get(0)));

            }else{
                throw new BadCredentialsException("Invalid Credentials");
            }
        }else{
            throw new BadCredentialsException("No Registered User found with provided credential");
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private List<GrantedAuthority> getGrantedAuthorities(Customer customer){
        return List.of(new SimpleGrantedAuthority(customer.getRole()));
    }
}
