package de.ancash.bazaar.core.dao;

import de.ancash.bazaar.core.record.Record;
import de.ancash.bazaar.core.record.Record.RecordDataType;

/**
 * Provides access to all recorded transactions and allows to manually record a
 * transaction.
 */
public interface IStatisticsDAO {

	public Record[] getRecordsOfLastHours(int hours);

	public Record[] getRecordsOfLastDays(int days);

	public Record[] getRecordsOfLastMonths(int months);

	public Record[] getRecordsOfLastYears(int years);

	public Record getRecord(int year);

	public Record getRecord(int year, int month);

	public Record getRecord(int year, int month, int day);

	public Record getRecord(int year, int month, int day, int hour, boolean offset);

	public boolean addRecord(RecordDataType type, int amount, double unitPrice, int[] cat);

	public boolean addRecord(RecordDataType type, int amount, double unitPrice, int[] cat, long millis, boolean offset);
}
