package local.intranet.tombola.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import local.intranet.tombola.api.info.LevelCount;
import local.intranet.tombola.api.info.LoggingEventInfo;
import local.intranet.tombola.api.model.entity.LoggingEvent;
import local.intranet.tombola.api.model.repository.LoggingEventRepository;

/**
 * 
 * {@link LoggingEventService} for
 * {@link local.intranet.tombola.TombolaApplication}
 * <p>
 * An entity {@link LoggingEvent} without setters is only used to read data
 * written by logback-spring DbAppender
 * 
 * @author Radek Kádner
 *
 */
@Service
public class LoggingEventService {

	private static final Logger LOG = LoggerFactory.getLogger(LoggingEventService.class);

	private static final String LOGGING_EVENT_INFO = "INFO";

	@Autowired
	private LoggingEventRepository loggingEventRepository;

	/**
	 * 
	 * countTotalLoggingEvents
	 * <p>
	 * Used
	 * {@link local.intranet.tombola.api.model.repository.LoggingEventRepository#countTotalLoggingEvents}
	 * 
	 * @return {@link List}&lt;{@link LevelCount}&gt;
	 */
	@Transactional(readOnly = true)
	public List<LevelCount> countTotalLoggingEvents() {
		List<LevelCount> ret = loggingEventRepository.countTotalLoggingEvents();
		// LOG.debug("{}", ret);
		return ret;
	}

	/**
	 * 
	 * findPageByLevelString {@link LoggingEvent}
	 * <p>
	 * Used
	 * {@link local.intranet.tombola.api.model.repository.LoggingEventRepository#findPageByLevelString}
	 * 
	 * @param pageable    {@link Pageable}
	 * @param levelString {@link List}&lt;{@link String}&gt;
	 * 
	 * @return {@link Page}&lt;{@link LoggingEventInfo}&gt;
	 */
	@Transactional(readOnly = true)
	public Page<LoggingEventInfo> findPageByLevelString(Pageable pageable, List<String> levelString) {
		try {
			Page<LoggingEvent> pa = loggingEventRepository.findPageByLevelString(pageable, levelString);
			List<LoggingEventInfo> list = new ArrayList<>();
			for (LoggingEvent l : pa) {
				list.add(makeLoggingEventInfo(l));
			}
			Page<LoggingEventInfo> ret = new PageImpl<>(list, pageable, pa.getTotalElements());
			// LOG.debug("{}", ret);
			return ret;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * 
	 * findPageByCaller {@link LoggingEvent}
	 * <p>
	 * Used
	 * {@link local.intranet.tombola.api.model.repository.LoggingEventRepository#findPageByCaller}
	 * 
	 * @param page         int
	 * @param cnt          int
	 * @param sort         {@link Sort}
	 * @param callerClass  {@link List}&lt;{@link String}&gt;
	 * @param callerMethod {@link List}&lt;{@link String}&gt;
	 * @return {@link Page}&lt;{@link LoggingEventInfo}&gt;
	 */
	@Transactional(readOnly = true)
	public Page<LoggingEventInfo> findPageByCaller(int page, @NotNull int cnt, Sort sort, List<String> callerClass,
			List<String> callerMethod) {
		try {
			String s = sort.toString();
			Direction direction = Direction.ASC;
			if (s.contains(Direction.DESC.toString())) {
				direction = Direction.DESC;
			}
			s = s.replaceAll(Direction.ASC.toString(), "").replaceAll(Direction.DESC.toString(), "").replaceAll(":",
					"");
			s = s.trim();
			Pageable pageable = PageRequest.of(page, cnt, JpaSort.unsafe(direction, s.split(" ,")));
			Page<LoggingEvent> pa = loggingEventRepository.findPageByCaller(pageable, callerClass, callerMethod,
					Arrays.asList(LOGGING_EVENT_INFO));
			List<LoggingEventInfo> list = new ArrayList<>();
			for (LoggingEvent l : pa) {
				list.add(makeLoggingEventInfo(l));
			}
			Page<LoggingEventInfo> ret = new PageImpl<>(list, pageable, pa.getTotalElements());
			return ret;
			// LOG.debug("{}", ret);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * 
	 * Make loggingEventInfo
	 * 
	 * @param loggingEvent {@link LoggingEvent}
	 * @return {@link LoggingEventInfo}
	 */
	protected LoggingEventInfo makeLoggingEventInfo(LoggingEvent loggingEvent) {
		String[] s = loggingEvent.getCallerClass().split("\\.");
		String arg0 = (loggingEvent.getArg0() == null) ? "[NULL]" : loggingEvent.getArg0();
		String arg1 = (loggingEvent.getArg1() == null) ? "[NULL]" : loggingEvent.getArg1();
		String arg2 = (loggingEvent.getArg2() == null) ? "[NULL]" : loggingEvent.getArg2();
		String arg3 = (loggingEvent.getArg3() == null) ? "[NULL]" : loggingEvent.getArg3();
		// LOG.debug("arg0:{} arg1:{} arg2:{} arg3:{}", arg0, arg1, arg2, arg3);
		LoggingEventInfo ret = new LoggingEventInfo(loggingEvent.getId(), loggingEvent.getFormattedMessage(),
				loggingEvent.getLevelString(), (s.length > 0) ? s[s.length - 1] : "", loggingEvent.getCallerMethod(),
				arg0, arg1, arg2, arg3, new Date(loggingEvent.getTimestmp()));
		// LOG.debug("{}", ret);
		return ret;
	}

}
