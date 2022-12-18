package de.ancash.bazaar.core.record;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import de.ancash.bazaar.core.DefaultCategory;
import de.ancash.bazaar.core.dao.ICoreDAO;
import de.ancash.bazaar.core.dao.IStatisticsDAO;
import de.ancash.libs.org.simpleyaml.configuration.file.YamlFile;
import de.ancash.misc.MathsUtils;

/**
 * A Record represents all transactions in a time frame. There are yearly,
 * monthly, daily and hourly records. A record is further divided into as many
 * categories, sub categories and sub sub categories as there are defined
 * (default: 5, 18, 9). The data inside a record should not be modified through
 * a {@link Record}, instead use the {@link IStatisticsDAO} provided by the
 * {@link ICoreDAO}. It's important to note that child records are not
 * transient, while the parent record is, if there is one. So depending on the
 * configuration, for example, a yearly record can take up a significant amount
 * of bandwith, since it represents 365 day * 24 hours worth of data. Assuming
 * in hour 100 transactions took place, the size of the serialized record would
 * be ~80MB.
 */
public class Record implements Serializable {

	private static final long serialVersionUID = -2743361858148625668L;

	protected final ConcurrentHashMap<Integer, Record> childRecords = new ConcurrentHashMap<Integer, Record>();
	protected transient Record parent;
	private volatile int[][][] soldInsta = new int[DefaultCategory.maxCategories()][DefaultCategory
			.maxSubCategories()][DefaultCategory.maxSubSubCategories()];
	private volatile double[][][] soldInstaMoney = new double[DefaultCategory.maxCategories()][DefaultCategory
			.maxSubCategories()][DefaultCategory.maxSubSubCategories()];
	private volatile int[][][] boughtInsta = new int[DefaultCategory.maxCategories()][DefaultCategory
			.maxSubCategories()][DefaultCategory.maxSubSubCategories()];
	private volatile double[][][] boughtInstaMoney = new double[DefaultCategory.maxCategories()][DefaultCategory
			.maxSubCategories()][DefaultCategory.maxSubSubCategories()];

	private volatile int[][][] sellOffers = new int[DefaultCategory.maxCategories()][DefaultCategory
			.maxSubCategories()][DefaultCategory.maxSubSubCategories()];
	private volatile double[][][] sellOffersMoney = new double[DefaultCategory.maxCategories()][DefaultCategory
			.maxSubCategories()][DefaultCategory.maxSubSubCategories()];
	private volatile int[][][] buyOrders = new int[DefaultCategory.maxCategories()][DefaultCategory
			.maxSubCategories()][DefaultCategory.maxSubSubCategories()];
	private volatile double[][][] buyOrdersMoney = new double[DefaultCategory.maxCategories()][DefaultCategory
			.maxSubCategories()][DefaultCategory.maxSubSubCategories()];
	protected final transient HashMap<RecordDataType, List<Object[]>> saves = new HashMap<RecordDataType, List<Object[]>>();
	protected final transient Set<Long> ids = new HashSet<>();
	protected final int key;
	protected transient boolean changed = false;

	Record(int i) {
		this.key = i;
		saves.put(RecordDataType.BUY_ORDER, Collections.synchronizedList(new ArrayList<>()));
		saves.put(RecordDataType.BUY_INSTANTLY, Collections.synchronizedList(new ArrayList<>()));
		saves.put(RecordDataType.SELL_INSTANTLY, Collections.synchronizedList(new ArrayList<>()));
		saves.put(RecordDataType.SELL_OFFER, Collections.synchronizedList(new ArrayList<>()));
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return getClass().getSimpleName() + "{key=" + key + ",si=" + getSoldInstantlySum() + ",sim="
				+ getSoldInstantlyMoneySum() + ",bi=" + getBoughtInstantlySum() + ",bim=" + getBoughtInstantlySum()
				+ ",so=" + getSellOffersSum() + ",som=" + getSellOffersMoneySum() + ",bo=" + getBuyOrdersSum() + ",bom="
				+ getBuyOrdersMoneySum() + ",children={"
				+ String.join(", ", childRecords.values().stream().map(Record::toString).collect(Collectors.toList()))
				+ "}}";
	}

	public int getKey() {
		return key;
	}

	public Record getChild(int i) {
		return childRecords.get(i);
	}

	public Record getParent() {
		return parent;
	}

	public boolean hasParent() {
		return parent != null;
	}

	@SuppressWarnings("nls")
	protected void save() {
		if (!childRecords.isEmpty()) {
			childRecords.values().forEach(Record::save);
			return;
		}
		if (!changed)
			return;
		int hour = key;
		int day = parent.key;
		int month = parent.parent.key;
		int year = parent.parent.parent.key;
		String dir = RecordArchive.BASE_DIR + "/" + year + "/" + month + "/" + day;
		try {
			Files.createDirectories(Paths.get(dir));
		} catch (IOException e) {
			System.err.println("could not save data for: " + dir + ": " + e);
			return;
		}
		YamlFile file = new YamlFile(dir + "/" + hour + ".yml");
		try {
			file.createNewFile(true);
		} catch (IOException e) {
			System.err.println("could not create file: " + file.getFilePath() + ": " + e);
			return;
		}

		for (RecordDataType type : saves.keySet()) {
			List<Object[]> saves = this.saves.get(type);
			for (Object[] save : saves) {
				file.set(type.toString() + "." + save[3] + ".a", save[0]);
				file.set(type.toString() + "." + save[3] + ".u-p", save[1]);
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < 3; i++) {
					if (((int[]) save[2])[i] < 10)
						builder.append(0);
					builder.append(((int[]) save[2])[i]);
				}
				file.set(type.toString() + "." + save[3] + ".c", builder.toString());
			}
		}
		try {
			file.save();
		} catch (IOException e) {
			System.err.println("could not save file: " + file.getFilePath() + ": " + e);
			try {
				file.deleteFile();
			} catch (IOException e1) {
			}
			return;
		}
		changed = false;
	}

	public Collection<Record> getChildren() {
		return Collections.unmodifiableCollection(childRecords.values());
	}

	protected void add(RecordDataType type, int amount, double unitPrice, int[] cat) {
		add(type, amount, unitPrice, cat, System.currentTimeMillis());
	}

	protected void add(RecordDataType type, int amount, double unitPrice, int[] cat, long millis) {
		if (childRecords.isEmpty()) {
			synchronized (this) {
				long temp = millis - java.util.concurrent.TimeUnit.HOURS.toMillis(java.util.concurrent.TimeUnit.MILLISECONDS.toHours(millis));
				if (ids.contains(temp)) {
					int cnt = 0;
					while (ids.contains(temp) || (temp <= 0 || temp >= java.util.concurrent.TimeUnit.HOURS.toMillis(1))) {
						cnt++;
						if (cnt % 2 == 0)
							temp -= cnt;
						else
							temp += cnt;
					}
				}
				ids.add(temp);
				saves.get(type).add(new Object[] { amount, unitPrice, cat, temp });
				changed = true;

				switch (type) {
				case BUY_INSTANTLY:
					boughtInsta[cat[0] - 1][cat[1] - 1][cat[2] - 1] += amount;
					boughtInstaMoney[cat[0] - 1][cat[1] - 1][cat[2] - 1] += unitPrice * amount;
					break;
				case SELL_INSTANTLY:
					soldInsta[cat[0] - 1][cat[1] - 1][cat[2] - 1] += amount;
					soldInstaMoney[cat[0] - 1][cat[1] - 1][cat[2] - 1] += unitPrice * amount;
					break;
				case BUY_ORDER:
					buyOrders[cat[0] - 1][cat[1] - 1][cat[2] - 1] += amount;
					buyOrdersMoney[cat[0] - 1][cat[1] - 1][cat[2] - 1] += unitPrice * amount;
					break;
				case SELL_OFFER:
					sellOffers[cat[0] - 1][cat[1] - 1][cat[2] - 1] += amount;
					sellOffersMoney[cat[0] - 1][cat[1] - 1][cat[2] - 1] += unitPrice * amount;
					break;
				default:
					break;
				}
			}
		} else {
			if (sellOffers != null) {
				sellOffers = null;
				sellOffersMoney = null;
				buyOrders = null;
				buyOrdersMoney = null;
				soldInsta = null;
				soldInstaMoney = null;
				boughtInsta = null;
				boughtInstaMoney = null;
			}
		}

		if (parent != null)
			parent.add(type, amount, unitPrice, cat, millis);
	}

	private int sumIntArr(int[][][] arr) {
		int cnt = 0;
		for (int a = 0; a < arr.length; a++)
			for (int b = 0; b < arr[a].length; b++)
				for (int c = 0; c < arr[a][b].length; c++)
					cnt += arr[a][b][c];
		return cnt;
	}

	private double sumDoubleArr(double[][][] arr) {
		double cnt = 0;
		for (int a = 0; a < arr.length; a++)
			for (int b = 0; b < arr[a].length; b++)
				for (int c = 0; c < arr[a][b].length; c++)
					cnt += arr[a][b][c];
		return MathsUtils.round(cnt, 2);
	}

	public int getSoldInstantlySum() {
		if (childRecords.isEmpty())
			return sumIntArr(soldInsta);
		return childRecords.values().stream().map(Record::getSoldInstantlySum).mapToInt(Integer::valueOf).sum();
	}

	public int getSoldInstantly(int cat, int sub, int subsub) {
		if (childRecords.isEmpty())
			return soldInsta[cat - 1][sub - 1][subsub - 1];
		return childRecords.values().stream().map(c -> c.getSoldInstantly(cat, sub, subsub)).mapToInt(Integer::valueOf)
				.sum();
	}

	public double getSoldInstantlyMoneySum() {
		if (childRecords.isEmpty())
			return sumDoubleArr(soldInstaMoney);
		return MathsUtils.round(
				childRecords.values().stream().map(Record::getSoldInstantlyMoneySum).mapToDouble(Double::valueOf).sum(),
				2);
	}

	public double getSoldInstantlyMoney(int cat, int sub, int subsub) {
		if (childRecords.isEmpty())
			return MathsUtils.round(soldInstaMoney[cat - 1][sub - 1][subsub - 1], 2);
		return MathsUtils.round(childRecords.values().stream().map(c -> c.getSoldInstantlyMoney(cat, sub, subsub))
				.mapToDouble(Double::valueOf).sum(), 2);
	}

	public int getBoughtInstantlySum() {
		if (childRecords.isEmpty())
			return sumIntArr(boughtInsta);
		return childRecords.values().stream().map(Record::getBoughtInstantlySum).mapToInt(Integer::valueOf).sum();
	}

	public int getBoughtInstantly(int cat, int sub, int subsub) {
		if (childRecords.isEmpty())
			return boughtInsta[cat - 1][sub - 1][subsub - 1];
		return childRecords.values().stream().map(c -> c.getBoughtInstantly(cat, sub, subsub))
				.mapToInt(Integer::valueOf).sum();
	}

	public double getBoughtInstantlyMoneySum() {
		if (childRecords.isEmpty())
			return sumDoubleArr(boughtInstaMoney);
		return MathsUtils.round(childRecords.values().stream().map(Record::getBoughtInstantlyMoneySum)
				.mapToDouble(Double::valueOf).sum(), 2);
	}

	public double getBoughtInstantlyMoney(int cat, int sub, int subsub) {
		if (childRecords.isEmpty())
			return MathsUtils.round(boughtInstaMoney[cat - 1][sub - 1][subsub - 1], 2);
		return MathsUtils.round(childRecords.values().stream().map(c -> c.getBoughtInstantlyMoney(cat, sub, subsub))
				.mapToDouble(Double::valueOf).sum(), 2);
	}

	public int getSellOffersSum() {
		if (childRecords.isEmpty())
			return sumIntArr(sellOffers);
		return childRecords.values().stream().map(Record::getSellOffersSum).mapToInt(Integer::valueOf).sum();
	}

	public int getSellOffers(int cat, int sub, int subsub) {
		if (childRecords.isEmpty())
			return sellOffers[cat - 1][sub - 1][subsub - 1];
		return childRecords.values().stream().map(c -> c.getSellOffers(cat, sub, subsub)).mapToInt(Integer::valueOf)
				.sum();
	}

	public double getSellOffersMoneySum() {
		if (childRecords.isEmpty())
			return sumDoubleArr(sellOffersMoney);
		return MathsUtils.round(
				childRecords.values().stream().map(Record::getSellOffersMoneySum).mapToDouble(Double::valueOf).sum(),
				2);
	}

	public double getSellOffersMoney(int cat, int sub, int subsub) {
		if (childRecords.isEmpty())
			return MathsUtils.round(sellOffersMoney[cat - 1][sub - 1][subsub - 1], 2);
		return MathsUtils.round(childRecords.values().stream().map(c -> c.getSellOffersMoney(cat, sub, subsub))
				.mapToDouble(Double::valueOf).sum(), 2);
	}

	public int getBuyOrdersSum() {
		if (childRecords.isEmpty())
			return sumIntArr(buyOrders);
		return childRecords.values().stream().map(Record::getBuyOrdersSum).mapToInt(Integer::valueOf).sum();
	}

	public int getBuyOrders(int cat, int sub, int subsub) {
		if (childRecords.isEmpty())
			return buyOrders[cat - 1][sub - 1][subsub - 1];
		return childRecords.values().stream().map(c -> c.getBuyOrders(cat, sub, subsub)).mapToInt(Integer::valueOf)
				.sum();
	}

	public double getBuyOrdersMoneySum() {
		if (childRecords.isEmpty())
			return sumDoubleArr(buyOrdersMoney);
		return MathsUtils.round(
				childRecords.values().stream().map(Record::getBuyOrdersMoneySum).mapToDouble(Double::valueOf).sum(), 2);
	}

	public double getBuyOrdersMoney(int cat, int sub, int subsub) {
		if (childRecords.isEmpty())
			return MathsUtils.round(buyOrdersMoney[cat - 1][sub - 1][subsub - 1], 2);
		return MathsUtils.round(childRecords.values().stream().map(c -> c.getBuyOrdersMoney(cat, sub, subsub))
				.mapToDouble(Double::valueOf).sum(), 2);
	}

	public enum RecordDataType {
		BUY_INSTANTLY, SELL_INSTANTLY, BUY_ORDER, SELL_OFFER;
	}

	public enum TimeUnit {
		HOUR, DAY, MONTH, YEAR;
	}
}