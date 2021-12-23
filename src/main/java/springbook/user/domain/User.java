package springbook.user.domain;

public class User {
	String id;
	String name;
	String password;
	private int recommand;
	private Level level;
	private int login;
	
	public User() {}

	
	public User(String id, String name, String password, Level level, int login, int recommand) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.level = level;
		this.login = login;
		this.recommand = recommand;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getLogin() {
		return login;
	}

	public void setLogin(int login) {
		this.login = login;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public int getRecommand() {
		return recommand;
	}

	public void setRecommand(int recommand) {
		this.recommand = recommand;
	}

	public enum Level {
		BASIC(1), SILVER(2), GOLD(3);

		private final int value;

		Level(int value) {
			this.value = value;
		}

		public int intValue() {
			return this.value;
		}

		public static Level valueOf(int value) {
			switch (value) {
				case 1: return BASIC;
				case 2: return SILVER;
				case 3: return GOLD;
				default: throw new AssertionError("Unknown Value:: " + value);
			}
		}
	}
}


