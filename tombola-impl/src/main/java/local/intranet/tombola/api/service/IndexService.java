package local.intranet.tombola.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import local.intranet.tombola.api.controller.InfoController;
import local.intranet.tombola.api.info.IndexInfo;

/**
 * 
 * {@link UserService} for
 * {@link local.intranet.tombola.api.controller.InfoController}
 * 
 * @author Radek Kádner
 *
 */
@Service
public class IndexService {

	@Value("${spring.data.rest.basePath:/api}")
	private String basePath;

	/**
	 * 
	 * Get IndexInfo for
	 * {@link local.intranet.tombola.api.controller.InfoController#getIndexInfo}
	 * 
	 * @return {@link IndexInfo}
	 */
	public IndexInfo getIndexInfo() {
		IndexInfo ret = new IndexInfo(
				basePath + InfoController.INFO_VERSION_PATH + InfoController.INFO_BASE_INFO + "/");
		return ret;
	}

}
