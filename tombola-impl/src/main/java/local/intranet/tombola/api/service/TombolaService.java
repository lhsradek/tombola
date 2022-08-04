package local.intranet.tombola.api.service;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import local.intranet.tombola.api.controller.StatusController;
import local.intranet.tombola.api.exception.TombolaException;
import local.intranet.tombola.api.info.PrizeInfo;
import local.intranet.tombola.api.info.TicketAudit;
import local.intranet.tombola.api.info.TicketInfo;
import local.intranet.tombola.api.info.TicketInfoComparator;
import local.intranet.tombola.api.info.content.Provider;
import local.intranet.tombola.api.model.entity.Prize;
import local.intranet.tombola.api.model.entity.Ticket;
import local.intranet.tombola.api.model.repository.PrizeRepository;
import local.intranet.tombola.api.model.repository.TicketRepository;
import local.intranet.tombola.api.security.AESUtil;

/**
 * 
 * {@link TombolaService} for {@link local.intranet.tombola.TombolaApplication}
 * 
 * @author Radek Kádner
 *
 */
@Service
public class TombolaService {

	private static final Logger LOG = LoggerFactory.getLogger(TombolaService.class); 

	@Value("${tombola.sec.key}")
	private String key;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private StatusController statusController;
	
	@Autowired
	private PrizeRepository prizeRepository;
	
	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired
	private Provider provider;
	

	/**
	 * 
	 * Sec for Manager Set In thymeleaf manager.html 
	 * <p>
	 * used {@link local.intranet.tombola.api.controller.IndexController#setManager}
	 * 
	 * @param Id        {@link Long}
	 * @param secretKey {@link SecretKey}
	 * @param iv        {@link IvParameterSpec}
	 * @return {@link String}
	 * @throws TombolaException 
	 */
	public synchronized static String secForManagerSet(Long Id, SecretKey secretKey, IvParameterSpec iv) throws TombolaException {
		try {
			String ret = AESUtil.setHex(AESUtil.encrypt(String.valueOf(Id), secretKey, iv));
			return ret;
		} catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException
				| InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
			LOG.error(e.getMessage(), e);
			throw new TombolaException(e.getMessage());
		}
	}

    /**
     * 
     * Get prize
     * <p>
     * For {@link local.intranet.tombola.api.controller.TombolaController#getPrize}
     *  
     * @param prizeId {@link Long}
     * @return {@link List}&lt;{@link PrizeInfo}&gt;
     */
    @Transactional(readOnly = true)
    public synchronized List<PrizeInfo> getPrize(@NotNull Long prizeId) {
    	List<PrizeInfo> ret = new ArrayList<>();
    	try {
    		AuditReader reader = provider.auditReader();
    		AuditQuery query = reader.createQuery().forRevisionsOfEntity(Prize.class, true, true)
    				.addProjection(AuditEntity.revisionNumber())
    				.addProjection(AuditEntity.selectEntity(false))
    				.add(AuditEntity.id().eq(prizeId))
    				.addOrder(AuditEntity.revisionNumber().desc())
    				.addOrder(AuditEntity.id().desc());
    		for (Object row : query.getResultList()) {
    			Object[] arr = (Object[]) row;
    			Integer revisionNumber = (Integer) arr[0];
    			@SuppressWarnings("unchecked")
    			Map<String, Object> entity = (Map<String, Object>) arr[1];
    			
    			AuditQuery query2 = reader.createQuery().forRevisionsOfEntity(Ticket.class, true, true)
    					.addProjection(AuditEntity.revisionType())
    					.addProjection(AuditEntity.id())
    					.addProjection(AuditEntity.selectEntity(false))
    					.add(AuditEntity.revisionNumber().eq(revisionNumber));
        		for (Object row2 : query2.getResultList()) {
        			Object[] arr2 = (Object[]) row2;
        			RevisionType revisionType = (RevisionType) arr2[0];
        			Long ticketId = (Long) arr2[1];
    			
        			PrizeInfo p = makePrizeInfo(prizeId, ticketId, revisionNumber, revisionType, entity);
        			if (p != null) {
        				ret.add(p);
        			}
        			break;
        		}
        		if (ret.size() == 0) {
        			ret.add(new PrizeInfo(
            				prizeId,
            				(String) entity.get("prizeName"),
            				(Integer) entity.get("cnt"),
            				(Integer) entity.get("issued"),
            				new Date((long) entity.get("modifiedDate")),
            				Arrays.asList()));
        		}
    		}
    		LOG.debug("GetPrize username:'{}' ip:{} sessionId:{} ret.size:{}",
    				userService.getUsername(), statusController.getClientIP(), statusController.getSessionId(), ret.size());
    	} catch (Exception e) {
    		LOG.error(e.getMessage(), e);
    	}
    	return ret;
    }
    
    /**
     * 
     * Make prize info
     * <p>
     * For {@link #getPrize}
     * 
     * @param prizeId {@link Long}
     * @param ticketId {@link Long}
     * @param revisionNumber {@link Integer}
     * @param revisionType {@link RevisionType}
     * @param entity {@link Map}&lt;{@link String}, {@link Object}&gt;
     * @return {@link PrizeInfo} 
     */
    protected PrizeInfo makePrizeInfo(
    		Long prizeId, Long ticketId, Integer revisionNumber, RevisionType revisionType, Map<String, Object> entity) {
    	if (entity != null) {
    		PrizeInfo ret = new PrizeInfo(
    				prizeId,
    				(String) entity.get("prizeName"),
    				(Integer) entity.get("cnt"),
    				(Integer) entity.get("issued"),
    				new Date((long) entity.get("modifiedDate")),
    				Arrays.asList(ticketId, (long) revisionNumber, (long) revisionType.ordinal()));
            // LOG.debug("MakePrizeInfo username:'{}' ip:{} sessionId:{} ret:{}", userService.getUsername(), statusController.getClientIP(), statusController.getSessionId(), ret);
        	return ret;
    	} else {
    		return null; 
    	}
    }
    
	/**
	 * 
	 * Get prizes
	 * <p>
	 * For {@link local.intranet.tombola.api.controller.TombolaController#getPrizes},
	 * {@link local.intranet.tombola.api.controller.IndexController#getIndex} and
	 * {@link local.intranet.tombola.api.controller.IndexController#getManager}
	 * 
	 * @param desc {@link Boolean}
	 * @return {@link List}&lt;{@link PrizeInfo}&gt;
	 */
    @Transactional(readOnly = true)
    public synchronized List<PrizeInfo> getPrizes(Boolean desc) {
    	List<PrizeInfo> ret = new ArrayList<>();
		Sort sort;
    	if (desc) {
    		sort = Sort.by("id").descending();
    	} else {
    		sort = Sort.by("id");
    	}
    	for (Prize prize : prizeRepository.findAll(sort)) {
    		List<TicketInfo> list = prize.getTicket().stream()
    				.map(ticket -> new TicketInfo(ticket.getId(), ticket.getWin(),
    						new Date(ticket.getModifiedDate()))).collect(Collectors.toList());
    		Collections.sort(list, new TicketInfoComparator());
    		ret.add(new PrizeInfo(prize.getId(), prize.getPrizeName(), prize.getCnt(), prize.getIssued(),
    				new Date(prize.getModifiedDate()), ticketInfoAsLong(list)));
    	}
    	return ret;
	}

    /**
     * 
     * Get prizes ready to win
     * <p>
     * For {@link local.intranet.tombola.api.controller.TombolaController#getPrizesReadyToWin}
     * 
     * @return {@link List}&lt;{@link PrizeInfo}&gt;
     */
    @Transactional(readOnly = true)
    public synchronized List<PrizeInfo> getPrizesReadyToWin() {
    	List<PrizeInfo> ret = new ArrayList<>();
    	for (Prize prize : prizeRepository.findByReadyToWin()) {
    		final List<TicketInfo> list = prize.getTicket().stream()
    				.map(ticket -> new TicketInfo(ticket.getId(), ticket.getWin(),
    						new Date(ticket.getModifiedDate()))).collect(Collectors.toList());
    		Collections.sort(list, new TicketInfoComparator());
    		ret.add(new PrizeInfo(prize.getId(), prize.getPrizeName(), prize.getCnt(), prize.getIssued(),
    				new Date(prize.getModifiedDate()), ticketInfoAsLong(list)));
    	}
    	return ret;
    }
    
	/**
	 * 
	 * Get ticket
	 * <p>
	 * For {@link local.intranet.tombola.api.controller.TombolaController#getTicket}
	 * 
	 * @param ticketId {@link Long}
	 * @return {@link List}&lt;{@link TicketAudit}&gt;
	 */
    @Transactional(readOnly = true)
	public synchronized List<TicketAudit> getTicket(@NotNull Long ticketId) {
    	List<TicketAudit> ret = new ArrayList<>();
    	try {
    		AuditReader reader = provider.auditReader();
       		AuditQuery query = reader.createQuery().forRevisionsOfEntity(Ticket.class, true, true)
       				.addProjection(AuditEntity.revisionNumber())
       				.addProjection(AuditEntity.revisionType())
       				.addProjection(AuditEntity.selectEntity(false))
       				.add(AuditEntity.id().eq(ticketId))
       				.addOrder(AuditEntity.revisionNumber().desc())
       				.addOrder(AuditEntity.id().desc());
       		for (Object row : query.getResultList()) {
     			Object[] arr = (Object[]) row;
       			Integer revisionNumber = (Integer) arr[0];
       			RevisionType revisionType = (RevisionType) arr[1];
       			@SuppressWarnings("unchecked")
				Map<String, Object> entity = (Map<String, Object>) arr[2];
       			TicketAudit p = makeTicketAudit(ticketId, revisionNumber, revisionType, entity);
       			if (p != null) {
       				ret.add(p);
       			}
    		}
            LOG.debug("GetTicket username:'{}' ip:{} sessionId:{} ret:{}",
            		userService.getUsername(), statusController.getClientIP(), statusController.getSessionId(), ret);
    	} catch (Exception e) {
    		LOG.error(e.getMessage(), e);
		}
    	return ret;
    }
    
    /**
     * 
     * Make ticket info
     * <p>
     * For {@link #getTicket}
     * 
     * @param ticketId {@link Long}
     * @param revisionNumber {@link Integer}
     * @param revisionType {@link RevisionType}
     * @param entity {@link Map}&lt;{@link String}, {@link Object}&gt;
     * @return {@link TicketAudit} 
     */
    protected TicketAudit makeTicketAudit(
    		Long ticketId, Integer revisionNumber, RevisionType revisionType, Map<String, Object> entity) {
    	if (entity != null) {
    		TicketAudit ret = new TicketAudit(
    				ticketId,
    				(entity.get("win") == null) ? 0 : (long) entity.get("win"),
    				(entity.get("modifiedDate") == null) ? new Date(0) : new Date((long) entity.get("modifiedDate")),
    				revisionNumber, revisionType);
            // LOG.debug("MakeTicketInfo username:'{}' ip:{} sessionId:{} ret:{}", userService.getUsername(), statusController.getClientIP(), statusController.getSessionId(), ret);
        	return ret;
    	} else {
    		return null; 
    	}
    }
    
    /**
     * 
     * Get tickets page
     * <p>
     * For {@link local.intranet.tombola.api.controller.TombolaController#getTicketsPage} and
     * {@link local.intranet.tombola.api.controller.IndexController#getManager}
     * 
     * @param pageable {@link Pageable}
     * @return {@link Page}&lt;{@link TicketInfo}&gt;
     */
    @Transactional(readOnly = true)
	public synchronized Page<TicketInfo> getTicketsPage(Pageable pageable) {
    	List<TicketInfo> list = new ArrayList<>();
    	Page<Ticket> pa = ticketRepository.findPage(pageable);
   		for (Ticket p : pa) {
   			list.add(new TicketInfo(p.getId(), p.getWin(), new Date(p.getModifiedDate())));
    	}
    	Page<TicketInfo> ret = new PageImpl<>(list, pageable, pa.getTotalElements());
    	return ret;
	}
    
    /**
     * 
     * Get ticketsWinPage
     * <p>
     * For {@link local.intranet.tombola.api.controller.TombolaController#getTicketsWinPage},
     * {@link local.intranet.tombola.api.controller.IndexController#getIndex},
     * {@link local.intranet.tombola.api.controller.IndexController#getManager} and
     * {@link local.intranet.tombola.api.controller.IndexController#setManager}
     * 
     * @param pageable {@link Pageable}
     * @return {@link Page}&lt;{@link TicketInfo}&gt;
     */
    @Transactional(readOnly = true)
    public synchronized Page<TicketInfo> getTicketsWinPage(Pageable pageable) {
    	List<TicketInfo> list = new ArrayList<>();
   		Page<Ticket> pa = ticketRepository.findPageAndWin(pageable);
   		for (Ticket p : pa) {
   			list.add(new TicketInfo(p.getId(), p.getWin(), new Date(p.getModifiedDate())));
   		}
    	Page<TicketInfo> ret = new PageImpl<>(list, pageable, pa.getTotalElements());
    	return ret;
    }

    /**
     * 
     * Get tickets prizes
     * <p>
     * For {@link local.intranet.tombola.api.controller.TombolaController#getTicketsPrizes} and
     * {@link local.intranet.tombola.api.controller.IndexController#getManager}
     * 
     * @param cnt  {@link Integer}
     * @return {@link List}&lt;{@link TicketInfo}&gt;
     */
    @Transactional(readOnly = true)
    public synchronized List<TicketInfo> getTicketsPrizes(@NotNull Integer cnt) {
    	List<TicketInfo> ret = new ArrayList<>();
    	if (cnt > 0) {
    		List<TicketInfo> ticketInfo = new ArrayList<>();
    		for (Ticket l : ticketRepository.findPageAndReadyWin(
    				PageRequest.of(0, (int) ticketRepository.count()))) {
    			if (!(ticketInfo.add(
    					new TicketInfo(l.getId(), l.getWin(), new Date(l.getModifiedDate()))))) {
    				break;
    			}
    		}
    		if (ticketInfo.size() > 0) {
    			try {
    				for (Prize prize : prizeRepository.findByReadyToWin()) {
    					if (cnt > 0) {
    						--cnt;
    						int idx = (int) (Math.random() * ticketInfo.size());
    						ret.add(new TicketInfo(
    								ticketInfo.get(idx).getId(), prize.getId(), new Date(prize.getModifiedDate())));
    							ticketInfo.remove(idx);
    					} else {
    						break;
    					}
        			}
   				} catch (UnsupportedOperationException | IndexOutOfBoundsException e) {
					LOG.error(e.getMessage(), e);
				}
    		}
    	}
    	return ret;
    }

	/**
	 * 
	 * For thymeleaf index.html and manager.html
	 * <p>
	 * and {@link local.intranet.tombola.api.controller.IndexController#getIndex},
	 * {@link local.intranet.tombola.api.controller.IndexController#getAudit},
	 * {@link local.intranet.tombola.api.controller.IndexController#getManager},
	 * {@link local.intranet.tombola.api.controller.IndexController#setManager}
	 * 
	 * @param {@link List}&lt;{@link Map.Entry}&lt;{@link Long}, {@link Integer}&gt;&gt;
	 * @return {@link List}&lt;{@link Long}&gt;
	 */
    @Transactional(readOnly = true)
	public synchronized List<Map.Entry<Long, Integer>> prizeInfoAsMap() {
		List<Map.Entry<Long, Integer>> ret = Collections.synchronizedList(new ArrayList<>());
		for (Prize l : prizeRepository.findAll(Sort.by("id"))) {
			ret.add(Map.entry(l.getId(), l.getIssued()));
		}
		// LOG.debug("PrizeInfoAsMap {} {}", userService.getUsername(), ret);
		return ret;
	}
	
    /**
     * 
     * Patch prize name
     * <p>
     * For {@link local.intranet.tombola.api.controller.TombolaController#patchPrizeName}
     * 
     * @param prizeId   {@link Long}
     * @param prizeName {@link String}
     * @return {@link PrizeInfo}
     */
    @Transactional
	public synchronized PrizeInfo patchPrizeName(@NotNull Long prizeId, @NotNull String prizeName) {
    	Optional<Prize> prize = prizeRepository.findById(prizeId);
    	PrizeInfo ret;
    	if (prize.isEmpty()) {
        	ret = new PrizeInfo();
    	} else {
    		try {
    			prize.get().setPrizeName(prizeName);
    			long st = new Date().getTime();
    			String username = userService.getUsername();
    			Prize p = prizeRepository.save(prize.get());
    			List<TicketInfo> list = p.getTicket().stream()
    					.map(ticket -> new TicketInfo(ticket.getId(), ticket.getWin(), new Date(ticket.getModifiedDate())))
    					.collect(Collectors.toList());
    			Collections.sort(list, new TicketInfoComparator());
    			ret = new PrizeInfo(
    					p.getId(), p.getPrizeName(), p.getCnt(), p.getIssued(), new Date(st), ticketInfoAsLong(list));
    			LOG.info("PatchPrizeName username:'{}' {} prizeId:{} prizeName:'{}'",
    					username, 0, prizeId, prizeName);
    		} catch (Exception e) {
    			LOG.error(e.getMessage(), e);
    			throw e;
			}
    	}
		return ret;
	}

    /**
     * 
     * Patch prize ticket
     * <p>
     * For {@link local.intranet.tombola.api.controller.TombolaController#patchPrizeTicket} and
     * {@link local.intranet.tombola.api.controller.IndexController#setManager}
     * 
     * @param prizeId  {@link Long}
     * @param ticketId {@link Long}
     * @return {@link PrizeInfo}
     */
	@Transactional
	public synchronized PrizeInfo patchPrizeTicket(@NotNull Long prizeId, @NotNull Long ticketId) {
		PrizeInfo ret;
		Optional<Prize> prize = prizeRepository.findById(prizeId);
		Optional<Ticket> ticket = ticketRepository.findById(ticketId);
		if (!prize.isEmpty() && !ticket.isEmpty() && ticket.get().getWin() == 0) {
			ticket.get().setWin(prizeId);
			ticketRepository.save(ticket.get());
			prize.get().setIssued(prize.get().getIssued() + 1);
			ArrayList<Ticket> arr = new ArrayList<>(prize.get().getTicket());
			arr.add(ticketRepository.findById(ticketId).get());
			prize.get().setTicket(new HashSet<Ticket>(arr));
			Prize v = prizeRepository.save(prize.get());
			List<TicketInfo> list = v.getTicket().stream()
					.map(l -> new TicketInfo(l.getId(), l.getWin(), new Date(l.getModifiedDate())))
					.collect(Collectors.toList());
			Collections.sort(list, new TicketInfoComparator());
			ret = new PrizeInfo(v.getId(), v.getPrizeName(), v.getCnt(), v.getIssued(),
					new Date(new Date().getTime()), ticketInfoAsLong(list));
			LOG.info("PatchPrizeTicket username:'{}' ticketId:{} prizeId:{} prizeName:'{}'",
					userService.getUsername(), ticketId, prizeId, prize.get().getPrizeName());
		} else {
			ret = new PrizeInfo();
		}
		return ret;
	}
    
	/**
	 * 
	 * Put prizes if (prizeRepository.count() == 0)
     * <p>
     * For {@link local.intranet.tombola.api.config.ApplicationConfig#setPrizes}
     * <p>
     * JSON configuration is in classpath:/prize-${spring.profiles.active}.json
     * (for example in /usr/share/tomcat/webapps/tombola/WEB-INF/classes/prize-production.json) This
     * is reading with {@link local.intranet.tombola.api.config.JsonFactory}.
     * 
	 * @param prizes {@link List}&lt;{@link Map}&lt;{@link String},{@link Object}&gt;&gt;
	 */
    @Transactional
	public synchronized void putPrizes(List<Map<String, Object>> prizes) {
		if (prizeRepository.count() == 0) {
			List<Map<String, Object>> value = (List<Map<String, Object>>) prizes;
			if (value != null) {
				ArrayList<Prize> list = new ArrayList<>();
				for (Map<String, Object> k : value) {
					// LOG.debug("PutPrizes prize:'{}'", k.get("prize_name") + ":" + k.get("cnt"));
					list.add(new Prize(k.get("prize_name").toString(), Integer.parseInt(k.get("cnt").toString())));
				}
				prizeRepository.saveAll(list);
			}
			LOG.info("PutPrizes username:{} cnt:{} {} '{}'",
					"admin",
					prizeRepository.count(),
					0,
					"Create " +
					StringUtils.uncapitalize(prizeRepository.findById((long) 1).get().getClass().getSimpleName()) +
					"s");
		}
    }
    
    /**
     * 
     * Put tickets
     * <p>
     * For {@link local.intranet.tombola.api.controller.TombolaController#putTickets}
     * 
     * @param cnt {@link Integer}
     */
    @Transactional
	public synchronized void putTickets(@NotNull Integer cnt) {
    	if (cnt > 0) {
    		String username = "admin";
    		long date = new Date().getTime();
    		ArrayList<Ticket> arr = new ArrayList<>();
        	for (int i = 0; i < cnt; i++) {
        		Ticket t = new Ticket();
        		t.setCreatedBy(username);
        		t.setModifiedBy("");
        		t.setCreatedDate(date);
        		t.setModifiedDate(date);
        		arr.add(t);
    		}
    		ticketRepository.saveAll(arr);
    		LOG.info("PutTickets username:'{}' cnt:{} {} '{}'", username, cnt, 0, "Create tickets");
    	}
	}

    /**
     * 
     * Delete ticket and return the price to the TombolaAplication
     * <p>
     * For {@link local.intranet.tombola.api.controller.TombolaController#deleteTicket}
     * 
     * @param ticketId {@link Long}
     */
    @Transactional
    public synchronized void deleteTicket(@NotNull Long ticketId) {
		// LOG.warn("DeleteTicket username:{} ticketId:{} prizeId:{} '{}'", userService.getUsername(), ticketId, 0, "");
    	Optional<Ticket> t = ticketRepository.findById(ticketId);
    	if (t.isEmpty()) {
    	} else {
    		long prizeId = 0;
    		String prizeName = "";
    		for (Prize p : prizeRepository.findAll(Sort.by("id"))) {
        		if (p.getTicket().contains(t.get())) {
        			Set<Ticket> set = p.getTicket();
        			set.remove(t.get());
        			p.setTicket(set);
        			p.setIssued(p.getIssued() - 1);
        			prizeRepository.save(p);
        			prizeId = p.getId();
        			prizeName = p.getPrizeName();
        		}
    		}
    		ticketRepository.delete(t.get());
			LOG.info("DeleteTicket username:'{}' ticketId:{} prizeId:{} '{}'",
					userService.getUsername(), ticketId, prizeId, prizeName);
    	}
    }

	/**
	 * 
	 * Get ticket info as Long
	 * <p>
	 * For {@link local.intranet.tombola.api.controller.IndexController#getIndex},
	 * {@link local.intranet.tombola.api.controller.IndexController#getManager},
	 * {@link local.intranet.tombola.api.controller.IndexController#setManager},
	 * {@link #getPrizes},
	 * {@link #getPrizesReadyToWin},
	 * {@link #patchPrizeName} and
	 * {@link #patchPrizeTicket}
	 * 
	 * @param {@link List}&lt;{@link TicketInfo}&gt;
	 * @return {@link List}&lt;{@link Long}&gt;
	 */
	public synchronized List<Long> ticketInfoAsLong(List<TicketInfo> list) {
		List<Long> ret = new ArrayList<>();
		for (TicketInfo l : list) {
			ret.add(l.getId());
		}
		return ret;
	}

}
