package local.intranet.tombola.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import local.intranet.tombola.api.controller.StatusController;
import local.intranet.tombola.api.info.BeanInfo;

/**
 * 
 * {@link BeanService} for
 * {@link local.intranet.tombola.api.controller.InfoController}
 * 
 * @author Radek Kádner
 *
 */
@Service
public class BeanService {

	// private static final Logger LOG = LoggerFactory.getLogger(BeanService.class);

	@Autowired
	private StatusController statusController;

	/**
	 * 
	 * For {@link local.intranet.tombola.api.controller.InfoController#getBeanInfo}
	 * 
	 * @return {@link BeanInfo}
	 */
	public synchronized BeanInfo getBeanInfo() {
		// LOG.debug("The BeanInfo has begun...");
		BeanInfo ret = new BeanInfo(statusController.getAPIBean());
		return ret;
	}

}
