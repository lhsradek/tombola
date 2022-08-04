package local.intranet.tombola.api.info;

/**
 * 
 * {@link LevelCount} for {@link local.intranet.tombola.api.model.repository.LoggingEventRepository} and
 * {@link local.intranet.tombola.api.service.LoggingEventService}
 * <p>
 * https://www.baeldung.com/jpa-queries-custom-result-with-aggregation-functions
 *
 */
public class LevelCount {
	
	private final String level;
    private final Long total;
    
    /**
     * 
     * Constructor with parameters
     * 
     * @param level {@link String}
     * @param total {@link Long}
     */
	public LevelCount(String level, Long total) {
		super();
		this.level = level;
		this.total = total;
	}

	/**
	 * 
	 * Get level
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * 
	 * Get total
	 * 
	 * @return the total
	 */
	public Long getTotal() {
		return total;
	}

	/**
	 * 
	 * Returns a string representation of the object.
	 */
	@Override
	public String toString() {
		return "LevelCount [level=" + level + ", total=" + total + "]";
	}
	
}
