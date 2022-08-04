package local.intranet.tombola.api.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import local.intranet.tombola.api.info.PrizeInfo;
import local.intranet.tombola.api.info.TicketAudit;
import local.intranet.tombola.api.info.TicketInfo;
import local.intranet.tombola.api.service.TombolaService;
import local.intranet.tombola.api.service.UserService;

/**
 * 
 * {@link TombolaController} for {@link local.intranet.tombola.TombolaApplication} It's for
 * charge of rest pages
 * 
 * @author Radek Kádner
 * 
 */
@RestController
@ConditionalOnExpression("${tombola.app.tombolaRest}")
@RequestMapping(
		value = "${spring.data.rest.basePath:/api}" + TombolaController.INFO_VERSION_PATH +
		TombolaController.INFO_BASE_INFO)
@Tag(name = TombolaController.TAG)
public class TombolaController {

	private static final Logger LOG = LoggerFactory.getLogger(TombolaController.class);

	/**
	 * 
	 * TAG = "tombola-controller"
	 */
	public static final String TAG = "tombola-controller";

	/**
	 * 
	 * INFO_VERSION_PATH = "/v1"
	 */
	protected static final String INFO_VERSION_PATH = "/v1";

	/**
	 * 
	 * INFO_BASE_INFO = "/rest"
	 */
	protected static final String INFO_BASE_INFO = "/tombola";

	@Value("${tombola.app.auditCnt:50}")
	private int auditCnt;
	
	@Autowired
	private TombolaService tombolaService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private StatusController statusController;
	
	/**
	 *
	 * Get prize
	 * <p>
	 * Used {@link local.intranet.tombola.api.service.TombolaService#getPrize}
	 * @see <a href="/tombola/swagger-ui/#/tombola-controller/getPrize"
	 * target="_blank">tombola/swagger-ui/#/tombola-controller/getPrize</a>
	 * 
	 * @param prizeId {@link Long}
	 * @return {@link List}&lt;{@link PrizeInfo}&gt;
	 */
	@GetMapping(value = "/prize/prizeId/{prizeId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "getPrize", summary = "Get Prize Audit - Swagger",
		description = "Get Prize Audit\n\n" +
			"This method is calling TombolaService.getPrize and used org.hibernate.envers.query.AuditQuery.\n" +
			"ticket: [ticketId, revisionNumber, revisionType]\n\n" +
	        "See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/TombolaController.html#" +
	        "getPrize(java.lang.Long)\" " +
	        "target=\"_blank\">TombolaController.getPrize</a>",
	    tags = { TombolaController.TAG })
	@PreAuthorize("permitAll()")
	public List<PrizeInfo> getPrize(
			@PathVariable @Parameter(description = "Prize's id") Long prizeId) {
		List<PrizeInfo> ret = tombolaService.getPrize(prizeId);
        // LOG.debug("GetPrize username:'{}' ip:{} sessionId:{} ret:{}", userService.getUsername(), statusController.getClientIP(), statusController.getSessionId(), ret);
		return ret;
	}

	/**
	 *
	 * Get prizes
	 * <p>
	 * Used {@link local.intranet.tombola.api.service.TombolaService#getPrizes}
	 * @see <a href="/tombola/swagger-ui/#/tombola-controller/getPrizes"
	 * target="_blank">tombola/swagger-ui/#/tombola-controller/getPrizes</a>
	 * 
	 * @return {@link List}&lt;{@link PrizeInfo}&gt;
	 */
	@GetMapping(value = "/prize", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "getPrizes", summary = "Get All Prizes - Index and Manager",
		description = "Get All Prizes\n\n" +
			"This method is calling TombolaService.getPrizes.\n\n" +
            "See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/TombolaController.html#" +
            "getPrizes()\" " +
            "target=\"_blank\">TombolaController.getPrizes</a>",
   	    tags = { TombolaController.TAG })
	@PreAuthorize("permitAll()")
	public List<PrizeInfo> getPrizes() {
		List<PrizeInfo> ret = tombolaService.getPrizes(false);
		// LOG.debug("{}", ret);
		return ret;
	}
	
	/**
	 *
	 * Get prizes to finale
	 * <p>
	 * Accessible to the {@link local.intranet.tombola.api.domain.type.RoleType#MANAGER_ROLE} and
	 * {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}
	 * <p>
	 * Used {@link local.intranet.tombola.api.service.TombolaService#getPrizesReadyToWin}
	 * @see <a href="/tombola/swagger-ui/#/tombola-controller/getPrizesReadyToWin"
	 * target="_blank">tombola/swagger-ui/#/tombola-controller/getPrizesReadyToWin</a>
	 * 
	 * @return {@link List}&lt;{@link PrizeInfo}&gt;
	 */
	@GetMapping(value = "/prize/ready", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "getPrizesReadyToWin", summary = "Get Prizes Ready to Win - Manager",
			description = "Get Prizes Ready to Win\n\n" + 
					"This method is calling TombolaService.getPrizesReadyToWin.\n\n" +
					"See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/TombolaController.html#" +
					"getPrizesReadyToWin()\" " +
					"target=\"_blank\">TombolaController.getPrizesReadyToWin</a>",
		    tags = { TombolaController.TAG })
	@PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
	public List<PrizeInfo> getPrizesReadyToWin() {
		List<PrizeInfo> ret = tombolaService.getPrizesReadyToWin();
		// LOG.debug("{}", ret);
		return ret;
	}
	
	/**
	 *
	 * Patch prize name
	 * <p>
	 * Accessible to the {@link local.intranet.tombola.api.domain.type.RoleType#MANAGER_ROLE} and
	 * {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}
	 * <p>
	 * Used {@link local.intranet.tombola.api.service.TombolaService#patchPrizeName}
	 * @see <a href="/tombola/swagger-ui/#/tombola-controller/patchPrizeName"
	 * target="_blank">tombola/swagger-ui/#/tombola-controller/patchPrizeName</a>
	 * 
	 * @param prizeId   {@link Long} Prize's id
	 * @param prizeName {@link String} New prize name
	 * @return {@link PrizeInfo}
	 */
	@PatchMapping(value = "/prize/prizeId/{prizeId}/prizeName/{prizeName}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "patchPrizeName", summary = "Patch (changes) Prize Name - Swagger",
			description = "Patch (changes) Prize Name\n\n" + 
					"This method is calling TombolaService.patchPrizeName.\n\n" +
					"See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/TombolaController.html#" +
					"patchPrizeName(java.lang.Long,java.lang.String)\" " +
					"target=\"_blank\">TombolaController.patchPrizeName</a>",
		    tags = { TombolaController.TAG })
	@PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
	public PrizeInfo patchPrizeName(
			@PathVariable @Parameter(description = "Prize's id") Long prizeId,
			@PathVariable @Parameter(description = "New prize name") String prizeName) {
		PrizeInfo ret = tombolaService.patchPrizeName(prizeId, prizeName);
		LOG.warn("PatchPrizeName username:'{}' {} prizeId:{} prizeName:'{}'",
				userService.getUsername(), statusController.getSessionId(), prizeId, prizeName);
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 *
	 * Patch prize ticket
	 * <p>
	 * Accessible to the {@link local.intranet.tombola.api.domain.type.RoleType#MANAGER_ROLE} and
	 * {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}
	 * <p>
	 * Used {@link local.intranet.tombola.api.service.TombolaService#patchPrizeTicket}
	 * @see <a href="/tombola/swagger-ui/#/tombola-controller/patchPrizeTicket"
	 * target="_blank">tombola/swagger-ui/#/tombola-controller/patchPrizeTicket</a>
	 * 
	 * @param prizeId   {@link Long} Prize's id
	 * @param ticketId   {@link Long} Ticket's id
	 * @return {@link PrizeInfo}
	 */
	@PatchMapping(value = "/prize/prizeId/{prizeId}/ticketId/{ticketId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			operationId = "patchPrizeTicket", summary = "Patch (changes) prize for win ticket - Index and Swagger",
			description = "Patch (changes) prize for win ticket\n\n" +
					"This method is calling TombolaService.patchPrizeTicket.\n\n" +
					"See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/TombolaController.html#" +
					"patchPrizeTicket(java.lang.Long,java.lang.Long)\" " +
					"target=\"_blank\">TombolaController.patchPrizeTicket</a>",
			tags = { TombolaController.TAG })
	@PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
	public PrizeInfo patchPrizeTicket(
			@PathVariable @Parameter(description = "Prize's id") Long prizeId,
			@PathVariable @Parameter(description = "Win ticket's id") Long ticketId) {
		PrizeInfo ret = tombolaService.patchPrizeTicket(prizeId, ticketId);
		LOG.warn("PatchPrizeTicket username:'{}' {} ticketId:{} prizeId:{}",
				userService.getUsername(), statusController.getSessionId(), ticketId, prizeId);
		// LOG.debug("{}", ret);
		return ret;
	}
	
	/**
	 *
	 * Get ticket
	 * <p>
	 * Used {@link local.intranet.tombola.api.service.TombolaService#getTicket}
	 * @see <a href="/tombola/swagger-ui/#/tombola-controller/getTicket"
	 * target="_blank">tombola/swagger-ui/#/tombola-controller/getTicket</a>
	 * 
	 * @param ticketId {@link Long}
	 * @return {@link List}&lt;{@link TicketAudit}&gt;
	 */
	@GetMapping(value = "/ticket/ticketId/{ticketId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			operationId = "getTicket", summary = "Get Ticket Audit - Swagger",
			description = "Get Ticket Audit\n\n" + 
					"This method is calling TombolaService.getTicket and used org.hibernate.envers.query.AuditQuery.\n\n" + 
		            "See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/TombolaController.html#" +
		            "getTicket(java.lang.Long)\" " +
		            "target=\"_blank\">TombolaController.getTicket</a>",
			tags = { TombolaController.TAG })
	@PreAuthorize("permitAll()")
	public List<TicketAudit> getTicket(
			@PathVariable @Parameter(description = "Ticket's id") Long ticketId) {
		List<TicketAudit> ret = tombolaService.getTicket(ticketId);
        // LOG.debug("GetTicket username:'{}' ip:{} sessionId:{} ret:{}", userService.getUsername(), statusController.getClientIP(), statusController.getSessionId(), ret);
		return ret;
	}

	/**
	 *
	 * Get tickets by page
	 * <p>
	 * Accessible to the {@link local.intranet.tombola.api.domain.type.RoleType#MANAGER_ROLE} and
	 * {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}
	 * <p>
	 * Used {@link local.intranet.tombola.api.service.TombolaService#getTicketsPage}
	 * @see <a href="/tombola/swagger-ui/#/tombola-controller/getTicketsPage"
	 * target="_blank">tombola/swagger-ui/#/tombola-controller/getTicketsPage</a>
	 * 
	 * @param page {@link Integer}
	 * @param size {@link Integer}
	 * @return {@link Page}&lt;{@link TicketInfo}&gt;
	 */
	@GetMapping(value = "/ticket/page/{page}/size/{size}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			operationId = "getTicketsPage", summary = "Get Tickets Pages - Manager",
			description = "Get Ticket Audit\n\n" + 
					"Get Tickets Pages - Manager\n\n" + 
					"This method is calling TombolaService.getTicketsPage\n\n" +
		            "See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/TombolaController.html#" +
		            "getTicketsPage(java.lang.Integer,java.lang.Integer)\" " +
		            "target=\"_blank\">TombolaController.getTicketsPage</a>",
			tags = { TombolaController.TAG })
	@PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
	public Page<TicketInfo> getTicketsPage(
			@PathVariable @Parameter(allowEmptyValue = true, example = "0", description = "Zero-based page index (0..N)") Integer page,
			@PathVariable @Parameter(example = "20", description = "The size of the page to be returned") Integer size
			) {
		Page<TicketInfo> ret = tombolaService.getTicketsPage(PageRequest.of(page, size));
		// LOG.debug("{}", ret);
		return ret;
	}
	
	/**
	 *
	 * Get win tickets by page
	 * <p>
	 * Used {@link local.intranet.tombola.api.service.TombolaService#getTicketsWinPage}
	 * @see <a href="/tombola/swagger-ui/#/tombola-controller/getTicketsWinPage"
	 * target="_blank">tombola/swagger-ui/#/tombola-controller/getTicketsWinPage</a>
	 * 
	 * @param page {@link Integer}
	 * @param size {@link Integer}
	 * @return {@link Page}&lt;{@link TicketInfo}&gt;
	 */
	@GetMapping(value = "/ticket/win/page/{page}/size/{size}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			operationId = "getTicketsWinPage", summary = "Get win pages - Index",
			description = "Get win pages - Index\n\n" +
					"This method is calling TombolaService.getTicketsWinPage\n\n" +
		            "See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/TombolaController.html#" +
		            "getTicketsWinPage(java.lang.Integer,java.lang.Integer)\" " +
		            "target=\"_blank\">TombolaController.getTicketsWinPage</a>",
			tags = { TombolaController.TAG })
	@PreAuthorize("permitAll()")
	public Page<TicketInfo> getTicketsWinPage(
			@PathVariable @Parameter(allowEmptyValue = true, example = "0", description = "Zero-based page index (0..N)") Integer page,
			@PathVariable @Parameter(example = "20", description = "The size of the page to be returned") Integer size
			) {
		Page<TicketInfo> ret = tombolaService.getTicketsWinPage(PageRequest.of(page, size));
		// LOG.debug("{}", ret);
		return ret;
	}
	
	/**
	 *
	 * Get prizes to finale
	 * <p>
	 * Accessible to the {@link local.intranet.tombola.api.domain.type.RoleType#MANAGER_ROLE} and
	 * {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}
	 * <p>
	 * Used {@link local.intranet.tombola.api.service.TombolaService#getTicketsPrizes}
	 * @see <a href="/tombola/swagger-ui/#/tombola-controller/getTicketsPrizes"
	 * target="_blank">tombola/swagger-ui/#/tombola-controller/getTicketsPrizes</a>
	 * 
	 * @param size {@link Integer} Number of returned ticket
	 * @return {@link List}&lt;{@link PrizeInfo}&gt;
	 */
	@GetMapping(value = "/ticket/size/{size}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			operationId = "getTicketsPrizes", summary = "Get Tickets Prizes - Manager",
			description = "Get Tickets Prizes - Manager\n\n" +
					"This method is calling TombolaService.getTicketsPrizes\n\n" + 
		            "See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/TombolaController.html#" +
		            "getTicketsPrizes(java.lang.Integer)\" " +
		            "target=\"_blank\">TombolaController.getTicketsPrizes</a>",
			tags = { TombolaController.TAG })
	@PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
	public List<TicketInfo> getTicketsPrizes(
			@PathVariable @Parameter(example = "5", description = "Number of returned tickets") Integer size) {
		List<TicketInfo> ret = tombolaService.getTicketsPrizes(size);
		// LOG.debug("{}", ret);
		return ret;
	}
	
    /**
     * 
     * Put tickets
	 * <p>
	 * Accessible to the {@link local.intranet.tombola.api.domain.type.RoleType#MANAGER_ROLE} and
	 * {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}
	 * <p>
	 * Used {@link local.intranet.tombola.api.service.TombolaService#putTickets}
	 * @see <a href="/tombola/swagger-ui/#/tombola-controller/putTickets"
	 * target="_blank">tombola/swagger-ui/#/tombola-controller/putTickets</a>
	 * 
     * @param size int
     */
	@PutMapping(value = "/ticket/putTickets/{size}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			operationId = "putTickets", summary = "Put Tickets - Swagger",
			description = "Put Tickets\n\n" +
					"This method is calling TombolaService.putTickets\n\n" + 
		            "See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/TombolaController.html#" +
		            "putTickets(java.lang.Integer)\" " +
		            "target=\"_blank\">TombolaController.putTickets</a>",
			tags = { TombolaController.TAG })
	@PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
	public void putTickets(
			@PathVariable @Parameter(example = "50", description = "Number of puted tickets") Integer size) {
		 LOG.warn("PutTickets username:'{}' {} cnt:{}",
				 userService.getUsername(), statusController.getSessionId(), size);
		 tombolaService.putTickets(size);
    }

	/**
	 *
	 * Delete ticket by ticketId
	 * <p>
	 * Accessible to the {@link local.intranet.tombola.api.domain.type.RoleType#MANAGER_ROLE} and
	 * {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}
	 * <p>
	 * Used {@link local.intranet.tombola.api.service.TombolaService#deleteTicket}
	 * @see <a href="/tombola/swagger-ui/#/tombola-controller/deleteTicket"
	 * target="_blank">tombola/swagger-ui/#/tombola-controller/deleteTicket</a>
	 * 
	 * @param ticketId {@link Long} Ticket's id
	 */
	@DeleteMapping(value = "/ticket/ticketId/{ticketId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			operationId = "deleteTicket", summary = "Delete Ticket - Swagger",
			description = "Delete Ticket\n\n" +
					"Delete ticket and returns the prize to the TombolaApplication.\n\n" +
				    "See <a href=\"/tombola-javadoc/local/intranet/tombola/api/controller/TombolaController.html#" +
					"deleteTicket(java.lang.Long)\" " +
					"target=\"_blank\">TombolaController.deleteTicket</a>",
			tags = { TombolaController.TAG })
	@PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
	public void deleteTicket(
			@PathVariable @Parameter(description = "Ticket's id") Long ticketId) {
		LOG.warn("DeleteTicket username:'{}' {} ticketId:{}",
				userService.getUsername(), statusController.getSessionId(), ticketId);
		tombolaService.deleteTicket(ticketId);
	}

}