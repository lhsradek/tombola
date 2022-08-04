package local.intranet.tombola;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import local.intranet.tombola.api.domain.type.RoleType;
import local.intranet.tombola.api.info.LevelCount;
import local.intranet.tombola.api.model.entity.Role;
import local.intranet.tombola.api.model.entity.User;
import local.intranet.tombola.api.model.repository.PrizeRepository;
import local.intranet.tombola.api.model.repository.RoleRepository;
import local.intranet.tombola.api.model.repository.TicketRepository;
import local.intranet.tombola.api.model.repository.UserRepository;
import local.intranet.tombola.api.service.LoggingEventService;
import local.intranet.tombola.api.service.TombolaService;

/**
 * 
 * {@link InitData} make data for {@link local.intranet.tombola.InitData#makeData(TombolaService)}
 * 
 * @author Radek Kádner
 *
 */
@Configuration
public class InitData {

	private static final Logger LOG = LoggerFactory.getLogger(InitData.class);

	private static final String ADMIN_PASSWORD = 
			"243261243034246a77614b5352774e75766e4433596734426849714b2e5856724b5a594c37494543316a5765374e74757867454369554a35726c4e65";
	private static final String LHS_PASSWORD = 
			"24326124303424655569755352476f2e30737873636f4a3832515878756875656d573634436a7054674f784d5656654b3379586c4f584f6c32684447";
	private static final String USER_PASSWORD = 
			"243261243034244358565237544c4f3853754a6f695552384d386f572e5259693672486c6b6b4b36724163542e624c4b44716878365a735a306a6d75";
	
	@Value("${tombola.app.putTicket}")
	private int putTicket;

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PrizeRepository prizeRepository;
	
	@Autowired
	private TicketRepository ticketRepository;

	@Autowired
	private LoggingEventService loggingEventService;
	
	/**
	 * 
	 * MakeData puts init data to db when is db empty
	 * and logs counts 
	 * @param tombolaService {@link TombolaService}
	 */
	public void makeData(TombolaService tombolaService) {
   		// LOG.debug("MakeData start");
		forJdbc();
		if (ticketRepository.count() == 0) {
			tombolaService.putTickets(putTicket);
   		}
		makePrizeIfNot();
		makeDataEnd();
		// LOG.debug("MakeData end");
	}

	/**
	 * 
	 * forJdbc
	 */
	@Transactional
	protected void forJdbc() {
		if (roleRepository.count() == 0 && userRepository.count() == 0) { // forTestingWithoutFlyware
			roleRepository.save(new Role(RoleType.ADMIN_ROLE.getRole()));
   			roleRepository.save(new Role(RoleType.MANAGER_ROLE.getRole()));
   			roleRepository.save(new Role(RoleType.USER_ROLE.getRole()));
   			userRepository.save(new User("admin", ADMIN_PASSWORD, new HashSet<Role>(Arrays.asList(
   					roleRepository.findByName(RoleType.ADMIN_ROLE.getRole()),
   					roleRepository.findByName(RoleType.MANAGER_ROLE.getRole()),
   					roleRepository.findByName(RoleType.USER_ROLE.getRole())))));
   			userRepository.save(new User("lhs", LHS_PASSWORD, new HashSet<Role>(Arrays.asList(
   					roleRepository.findByName(RoleType.MANAGER_ROLE.getRole()),
   					roleRepository.findByName(RoleType.USER_ROLE.getRole())))));
   			userRepository.save(new User("user", USER_PASSWORD, new HashSet<Role>(Arrays.asList(
   					roleRepository.findByName(RoleType.USER_ROLE.getRole())))));
   	   		LOG.debug("MakeData data is saved");
		}
	}

	/**
	 * 
	 * makeDataEnd
	 */
	@Transactional(readOnly = true)
	protected void makeDataEnd() {
		for (CrudRepository<?, Long> r : Arrays.asList(roleRepository, userRepository, prizeRepository,
				ticketRepository)) {
			Optional<?> o = r.findById((long) 1);
			if (!o.isEmpty()) {
				LOG.debug("MakeData entity:'{}' cnt:{} repository:'{}'",
						StringUtils.uncapitalize(o.get().getClass().getSimpleName()), r.count(), "jpa");
			}
		}
		for (LevelCount l : loggingEventService.countTotalLoggingEvents()) {
			LOG.info("MakeData levelString:'{}' total:{}", l.getLevel(), l.getTotal());
		}
	}
	
	/**
	 * 
	 * makePrizeInfo
	 */
	@Transactional
	private synchronized void makePrizeIfNot() {
		if (prizeRepository.count() == 0) {
			/*
			ArrayList<Prize> arr = new ArrayList<>(Arrays.asList(
					new Prize("Zájezd", 1),
					new Prize("Hasící přístroj", 2),
					new Prize("Telefon", 3),
					new Prize("Pytel brambor", 4),
					new Prize("Pytel cibule", 5),
					new Prize("Váza", 6),
					new Prize("Kuchyňské hodiny", 7),
					new Prize("Outdoorové hodinky", 8),
					new Prize("Sauna", 9),
					new Prize("Sportovní prádlo", 10),
					new Prize("Kniha", 12),
					new Prize("Kalendář", 13)));
			prizeRepository.saveAll(arr);
			*/
		}
	}

}
