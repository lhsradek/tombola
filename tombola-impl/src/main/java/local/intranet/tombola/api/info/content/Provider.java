package local.intranet.tombola.api.info.content;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.internal.reader.AuditReaderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * {@link Provider} for
 * {@link local.intranet.tombola.api.controller.IndexController} and
 * {@link Provider} for
 * {@link local.intranet.tombola.api.service.TombolaService}
 * 
 * @author Radek Kádner
 *
 */
@Component
public class Provider {

	private static final Logger LOG = LoggerFactory.getLogger(Provider.class);

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * 
	 * Get queryProvider for
	 * {@link local.intranet.tombola.api.controller.IndexController#signin}
	 * 
	 * @param params {@link List}&lt;{@link Map.Entry}&lt;{@link String},
	 *               {@link String}&gt;&gt;
	 * @return {@link String}
	 */
	public String queryProvider(List<Map.Entry<String, String>> params) {
		StringBuffer query = new StringBuffer();
		params.forEach(pair -> {
			if (query.length() > 0) {
				query.append("&");
			} else {
				query.append("?");
			}
			query.append(pair.toString());
		});
		return query.toString();
	}

	/**
	 * 
	 * Get AuditReader for
	 * {@link local.intranet.tombola.api.service.TombolaService#getPrize} and
	 * {@link local.intranet.tombola.api.service.TombolaService#getTicket}
	 * 
	 * @return {@link AuditReader}
	 */
	public AuditReader auditReader() {
		AuditReader ret = AuditReaderFactory.get(entityManager);
		if (!((AuditReaderImpl) ret).getSession().isOpen()) {
			ret = AuditReaderFactory.get(entityManagerFactory.createEntityManager());
			LOG.warn("AuditReader create EntityManager!");
		}
		return ret;
	}

}