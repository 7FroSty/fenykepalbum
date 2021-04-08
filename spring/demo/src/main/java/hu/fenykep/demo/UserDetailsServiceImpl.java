package hu.fenykep.demo;

import hu.fenykep.demo.model.Felhasznalo;
import hu.fenykep.demo.repository.FelhasznaloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private FelhasznaloRepository felhasznaloRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Felhasznalo felhasznalo = felhasznaloRepository.getFelhasznaloByEmail(email);
        if (felhasznalo == null) {
            throw new UsernameNotFoundException("Nincs ilyen felhasznalo");
        }

        return felhasznalo;
    }
}
