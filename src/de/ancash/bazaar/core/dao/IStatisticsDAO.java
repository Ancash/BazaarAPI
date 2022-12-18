package de.ancash.bazaar.core.dao;

import java.util.ArrayList;

import de.ancash.bazaar.core.record.Record;
import de.ancash.bazaar.core.record.Record.RecordDataType;
import de.ancash.bazaar.core.record.Record.TimeUnit;

/**
 * Provides access to all recorded transactions and allows to manually record a
 * transaction.
 */
public interface IStatisticsDAO {

	public ArrayList<String> getStatisticsOfLastHours(int n, RecordDataType type, boolean money, int yScale);
	
	public ArrayList<String> getStatisticsOfLastHours(int n, RecordDataType type, boolean money, int yScale, int[] cat);

	public ArrayList<String> getStatisticsOfLastDays(int n, RecordDataType type, boolean money, int yScale);
	
	public ArrayList<String> getStatisticsOfLastDays(int n, RecordDataType type, boolean money, int yScale, int[] cat);

	public ArrayList<String> getStatisticsOfLastMonths(int n, RecordDataType type, boolean money, int yScale);
	
	public ArrayList<String> getStatisticsOfLastMonths(int n, RecordDataType type, boolean money, int yScale, int[] cat);

	public ArrayList<String> getStatisticsOfLastYears(int n, RecordDataType type, boolean money, int yScale);
	
	public ArrayList<String> getStatisticsOfLastYears(int n, RecordDataType type, boolean money, int yScale, int[] cat);
	
	public default ArrayList<String> getStatisticsOf(TimeUnit unit, int n, RecordDataType type, boolean money, int yScale, int[] cat) {
		switch (unit) {
		case HOUR:
			return getStatisticsOfLastHours(n, type, money, yScale);
		case DAY:
			return getStatisticsOfLastDays(n, type, money, yScale);
		case MONTH:
			return getStatisticsOfLastMonths(n, type, money, yScale);
		case YEAR:
			return getStatisticsOfLastYears(n, type, money, yScale);
		default:
			throw new IllegalArgumentException();
		}
	}

	public Record[] getRecordsOfLastHours(int hours);

	public Record[] getRecordsOfLastDays(int days);

	public Record[] getRecordsOfLastMonths(int months);

	public Record[] getRecordsOfLastYears(int years);
	
	public default Record[] getRecordsOfLast(TimeUnit unit, int n) {
		switch (unit) {
		case HOUR:
			return getRecordsOfLastHours(n);
		case DAY:
			return getRecordsOfLastDays(n);
		case MONTH:
			return getRecordsOfLastMonths(n);
		case YEAR:
			return getRecordsOfLastYears(n);
		default:
			throw new IllegalArgumentException();
		}
	}

	public Record getRecord(int year);

	public Record getRecord(int year, int month);

	public Record getRecord(int year, int month, int day);

	public Record getRecord(int year, int month, int day, int hour, boolean offset);

	public boolean addRecord(RecordDataType type, int amount, double unitPrice, int[] cat);

	public boolean addRecord(RecordDataType type, int amount, double unitPrice, int[] cat, long millis, boolean offset);
}
