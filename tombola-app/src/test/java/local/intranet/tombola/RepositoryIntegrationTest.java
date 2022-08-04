package local.intranet.tombola;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import local.intranet.tombola.api.model.entity.Prize;
import local.intranet.tombola.api.model.entity.Role;
import local.intranet.tombola.api.model.entity.User;
import local.intranet.tombola.api.model.repository.PrizeRepository;
import local.intranet.tombola.api.model.repository.RoleRepository;
import local.intranet.tombola.api.model.repository.UserRepository;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-default.properties")
@DataJpaTest
public class RepositoryIntegrationTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(RepositoryIntegrationTest.class);
	
	@Autowired
	private PrizeRepository prizeRepository;
	
	@Autowired
    private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Test
	public void printAllPrizes() {
		for (Prize u : prizeRepository.findAll()) {
			LOG.debug("prize:{}", u.getPrizeName());
		}
	}
	
	@Test
	public void printAllRoles() {
	    for (Role u : roleRepository.findAll()) {
	    	LOG.debug("role:{}", u.getRoleName());
	    }
	}
	
	@Test
	public void printAllUsers() {
		for (User u : userRepository.findAll()) {
	    	LOG.debug("username:{}", u.getUserName());
		}
	}

}
