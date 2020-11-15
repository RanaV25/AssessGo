package com.assessgo.backend;


import com.vaadin.flow.component.html.Image;
import com.assessgo.frontend.util.UIUtils;


import java.util.*;

public class DummyData {



	private static final Random random = new Random(1);

	private DummyData() {
	}


	public static Image getLogo() {
		Image image = new Image(getImageSource(), "");
		image.setAlt("Company logo");
		return image;
	}

	public static String getImageSource() {
		return UIUtils.IMG_PATH + "logos/" + DummyData.getRandomInt(1, 40)
				+ ".png";
	}

	/* === NUMBERS === */

	public static Double getAmount() {
		return random.nextBoolean() ? getNegativeAmount() : getPositiveAmount();
	}

	private static Double getPositiveAmount() {
		return random.nextDouble() * 20000;
	}

	private static Double getNegativeAmount() {
		return random.nextDouble() * -20000;
	}

	public static int getRandomInt(int min, int max) {
		return random.nextInt(max + 1 - min) + min;
	}

	public static Double getRandomDouble(int min, int max) {
		return min + (max - min) * random.nextDouble();
	}

	public static String getPhoneNumber() {
		return String.format("%09d", random.nextInt(1000000000));
	}
}
