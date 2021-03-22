package labs.pm.data;

public enum Raiting {
	//Black star - \u2605
	//White stat - \u2606
	
	NOT_RATED("\u2606\u2606\u2606\u2606\u2606"),
	ONE_STAR("\u2605\u2606\u2606\u2606\u2606"),
	TWO_STAR("\u2605\u2605\u2606\u2606\u2606"),
	THREE_STAR("\u2605\u2605\u2605\u2606\u2606"),
	FOUR_STAR("\u2605\u2605\u2605\u2605\u2606"),
	FIVE_STAR("\u2605\u2605\u2605\u2605\u2605");

	private String stars;

	private Raiting(String stars) {
		this.stars = stars;
	}
	
	public String getStars() {
		return stars;
	}
	
	
}
